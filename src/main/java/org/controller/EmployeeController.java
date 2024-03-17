package org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dto.EmployeeIncomingDTO;
import org.dto.EmployeeOutGoingDTO;
import org.dto.EmployeeUpdateDTO;
import org.exeption.NotFoundException;
import org.service.EmployeeService;
import org.service.impl.EmployeeServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/employee/*"})
public class EmployeeController extends HttpServlet {
    private final transient EmployeeService employeeService = EmployeeServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public EmployeeController() {
        this.objectMapper = new ObjectMapper();
    }

    private static void setJsonHeader(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private static String getJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader postData = req.getReader();
        String line;
        while ((line = postData.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);

        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            if ("all".equals(pathPart[1])) {
                List<EmployeeOutGoingDTO> employeeDTOList = employeeService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(employeeDTOList);
            } else {
                Long userId = Long.parseLong(pathPart[1]);
                EmployeeOutGoingDTO employeeDTO = employeeService.findById(userId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(employeeDTO);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            Long userId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            employeeService.delete(userId);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = null;
        Optional<EmployeeIncomingDTO> employeeResponse;
        try {
            employeeResponse = Optional.ofNullable(objectMapper.readValue(json, EmployeeIncomingDTO.class));
            EmployeeIncomingDTO employee = employeeResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(employeeService.save(employee));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect employee Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = "";
        Optional<EmployeeUpdateDTO> employeeResponse;
        try {
            employeeResponse = Optional.ofNullable(objectMapper.readValue(json, EmployeeUpdateDTO.class));
            EmployeeUpdateDTO userUpdateDto = employeeResponse.orElseThrow(IllegalArgumentException::new);
            employeeService.update(userUpdateDto);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect employee Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }
}

package org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dto.RoleIncomingDTO;
import org.dto.RoleOutGoingDTO;
import org.dto.RoleUpdateDTO;
import org.exeption.NotFoundException;
import org.service.RoleService;
import org.service.impl.RoleServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/role/*"})
public class RoleController extends HttpServlet {
    private final transient RoleService roleService;
    private final ObjectMapper objectMapper;

    public RoleController() {
        this.roleService = RoleServiceImpl.getInstance();
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
                List<RoleOutGoingDTO> roleDtoList = roleService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(roleDtoList);
            } else {
                Long roleId = Long.parseLong(pathPart[1]);
                RoleOutGoingDTO roleDto = roleService.findById(roleId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(roleDto);
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
            Long roleId = Long.parseLong(pathPart[1]);
            if (roleService.delete(roleId)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = null;
        Optional<RoleIncomingDTO> roleResponse;
        try {
            roleResponse = Optional.ofNullable(objectMapper.readValue(json, RoleIncomingDTO.class));
            RoleIncomingDTO role = roleResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(roleService.save(role));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect role Object.";
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
        Optional<RoleUpdateDTO> roleResponse;
        try {
            roleResponse = Optional.ofNullable(objectMapper.readValue(json, RoleUpdateDTO.class));
            RoleUpdateDTO roleUpdateDto = roleResponse.orElseThrow(IllegalArgumentException::new);
            roleService.update(roleUpdateDto);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect role Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }
}

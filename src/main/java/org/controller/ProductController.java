package org.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dto.ProductIncomingDTO;
import org.dto.ProductOutGoingDTO;
import org.dto.ProductUpdateDTO;
import org.exeption.NotFoundException;
import org.service.ProductService;
import org.service.impl.ProductServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/product/*"})
public class ProductController extends HttpServlet {
    private final transient ProductService productService = ProductServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public ProductController() {
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
                List<ProductOutGoingDTO> productDTOList = productService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(productDTOList);
            } else {
                Long productId = Long.parseLong(pathPart[1]);
                ProductOutGoingDTO productDTO = productService.findById(productId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(productDTO);
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
            Long productId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            if (req.getPathInfo().contains("/deleteEmployee/")) {
                if ("deleteEmployee".equals(pathPart[2])) {
                    Long employeeId = Long.parseLong(pathPart[3]);
                    productService.deleteEmployeeFromProduct(productId, employeeId);
                }
            } else {
                productService.delete(productId);
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
        Optional<ProductIncomingDTO> productResponse;
        try {
            productResponse = Optional.ofNullable(objectMapper.readValue(json, ProductIncomingDTO.class));
            ProductIncomingDTO product = productResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(productService.save(product));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect product Object.";
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
        Optional<ProductUpdateDTO> productResponse;
        try {
            if (req.getPathInfo() != null && req.getPathInfo().contains("/addEmployee/")) {
                String[] pathPart = req.getPathInfo().split("/");
                if (pathPart.length > 3 && "addEmployee".equals(pathPart[2])) {
                    Long productId = Long.parseLong(pathPart[1]);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    Long employeeId = Long.parseLong(pathPart[3]);
                    productService.addEmployeeToProduct(productId, employeeId);
                }
            } else {
                productResponse = Optional.ofNullable(objectMapper.readValue(json, ProductUpdateDTO.class));
                ProductUpdateDTO productUpdateDTO = productResponse.orElseThrow(IllegalArgumentException::new);
                productService.update(productUpdateDTO);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect product Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }
}
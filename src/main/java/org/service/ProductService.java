package org.service;

import org.dto.ProductIncomingDTO;
import org.dto.ProductOutGoingDTO;
import org.dto.ProductUpdateDTO;
import org.exeption.NotFoundException;

import java.util.List;

public interface ProductService {
    ProductOutGoingDTO save(ProductIncomingDTO product);

    void update(ProductUpdateDTO product) throws NotFoundException;

    ProductOutGoingDTO findById(Long productId) throws NotFoundException;

    List<ProductOutGoingDTO> findAll();

    void delete(Long productId) throws NotFoundException;

    void deleteEmployeeFromProduct(Long productId, Long employeeId) throws NotFoundException;

    void addEmployeeToProduct(Long productId, Long employeeId) throws NotFoundException;
}

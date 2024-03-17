package org.mapper.impl;

import org.dto.EmployeeSmallOutGoingDTO;
import org.dto.ProductIncomingDTO;
import org.dto.ProductOutGoingDTO;
import org.dto.ProductUpdateDTO;
import org.mapper.ProductDTOMapper;
import org.model.Product;

import java.util.List;

public class ProductDTOMapperImpl implements ProductDTOMapper {
    private static ProductDTOMapper instance;

    private ProductDTOMapperImpl() {
    }

    public static synchronized ProductDTOMapper getInstance() {
        if (instance == null) {
            instance = new ProductDTOMapperImpl();
        }
        return instance;
    }

    @Override
    public Product map(ProductIncomingDTO dto) {
        return new Product(
                null,
                dto.getName(),
                null
        );
    }

    @Override
    public ProductOutGoingDTO map(Product product) {
        List<EmployeeSmallOutGoingDTO> employeeList = product.getEmployeeList()
                .stream().map(user -> new EmployeeSmallOutGoingDTO(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName()
                )).toList();

        return new ProductOutGoingDTO(
                product.getId(),
                product.getName(),
                employeeList
        );
    }

    @Override
    public Product map(ProductUpdateDTO updateDto) {
        return new Product(
                updateDto.getId(),
                updateDto.getName(),
                null
        );
    }

    @Override
    public List<ProductOutGoingDTO> map(List<Product> productList) {
        return productList.stream().map(this::map).toList();
    }

    @Override
    public List<Product> mapUpdateList(List<ProductUpdateDTO> productList) {
        return productList.stream().map(this::map).toList();
    }
}

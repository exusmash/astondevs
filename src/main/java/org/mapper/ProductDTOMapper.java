package org.mapper;

import org.dto.ProductIncomingDTO;
import org.dto.ProductOutGoingDTO;
import org.dto.ProductUpdateDTO;
import org.model.Product;

import java.util.List;

public interface ProductDTOMapper {
    Product map(ProductIncomingDTO productIncomingDTO);

    ProductOutGoingDTO map(Product product);

    Product map(ProductUpdateDTO productUpdateDTO);

    List<ProductOutGoingDTO> map(List<Product> productList);

    List<Product> mapUpdateList(List<ProductUpdateDTO> productList);
}

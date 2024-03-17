package mapperTest;

import org.dto.ProductIncomingDTO;
import org.dto.ProductOutGoingDTO;
import org.dto.ProductUpdateDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapper.ProductDTOMapper;
import org.mapper.impl.ProductDTOMapperImpl;
import org.model.Employee;
import org.model.Product;

import java.util.List;

public class ProductDTOMapperTest {
    private ProductDTOMapper productDTOMapper;

    @BeforeEach
    void setUp() {
        productDTOMapper = ProductDTOMapperImpl.getInstance();
    }

    @DisplayName("Product map(ProductIncomingDTO")
    @Test
    void mapIncoming() {
        ProductIncomingDTO dto = new ProductIncomingDTO("New Product");
        Product result = productDTOMapper.map(dto);

        Assertions.assertNull(result.getId());
        Assertions.assertEquals(dto.getName(), result.getName());
    }

    @DisplayName("ProductOutGoingDTO map(Product")
    @Test
    void testMapOutgoing() {
        Product product = new Product(100L, "Product #100", List.of(new Employee(), new Employee()));

        ProductOutGoingDTO result = productDTOMapper.map(product);

        Assertions.assertEquals(product.getId(), result.getId());
        Assertions.assertEquals(product.getName(), result.getName());
        Assertions.assertEquals(product.getEmployeeList().size(), result.getEmployeeList().size());
    }

    @DisplayName("Product map(ProductUpdateDTO")
    @Test
    void testMapUpdate() {
        ProductUpdateDTO dto = new ProductUpdateDTO(10L, "Update name.");

        Product result = productDTOMapper.map(dto);
        Assertions.assertEquals(dto.getId(), result.getId());
        Assertions.assertEquals(dto.getName(), result.getName());
    }

    @DisplayName("List<ProductOutGoingDTO> map(List<Product>")
    @Test
    void testMap2() {
        List<Product> productList = List.of(
                new Product(1L, "product 1", List.of()),
                new Product(2L, "product 2", List.of()),
                new Product(3L, "product 3", List.of())
        );

        List<ProductOutGoingDTO> result = productDTOMapper.map(productList);

        Assertions.assertEquals(3, result.size());
    }

    @DisplayName("List<Product> mapUpdateList(List<ProductUpdateDTO>")
    @Test
    void mapUpdateList() {
        List<ProductUpdateDTO> productList = List.of(
                new ProductUpdateDTO(),
                new ProductUpdateDTO(),
                new ProductUpdateDTO()
        );

        List<Product> result = productDTOMapper.mapUpdateList(productList);

        Assertions.assertEquals(3, result.size());
    }
}

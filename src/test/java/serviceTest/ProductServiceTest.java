package serviceTest;

import org.dto.ProductIncomingDTO;
import org.dto.ProductOutGoingDTO;
import org.dto.ProductUpdateDTO;
import org.exeption.NotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.model.EmployeeToProduct;
import org.model.Product;
import org.repository.EmployeeRepository;
import org.repository.EmployeeToProductRepository;
import org.repository.ProductRepository;
import org.repository.impl.EmployeeRepositoryImpl;
import org.repository.impl.EmployeeToProductRepositoryImpl;
import org.repository.impl.ProductRepositoryImpl;
import org.service.ProductService;
import org.service.impl.ProductServiceImpl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class ProductServiceTest {
    private static ProductService productService;
    private static ProductRepository mockProductRepository;
    private static EmployeeRepository mockEmployeeRepository;
    private static EmployeeToProductRepository mockEmployeeToProductRepository;
    private static ProductRepositoryImpl oldProductInstance;
    private static EmployeeRepositoryImpl oldEmployeeInstance;
    private static EmployeeToProductRepositoryImpl oldLinkInstance;

    private static void setMock(ProductRepository mock) {
        try {
            Field instance = ProductRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldProductInstance = (ProductRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMock(EmployeeRepository mock) {
        try {
            Field instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldEmployeeInstance = (EmployeeRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMock(EmployeeToProductRepository mock) {
        try {
            Field instance = EmployeeToProductRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldLinkInstance = (EmployeeToProductRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockProductRepository = Mockito.mock(ProductRepository.class);
        setMock(mockProductRepository);
        mockEmployeeRepository = Mockito.mock(EmployeeRepository.class);
        setMock(mockEmployeeRepository);
        mockEmployeeToProductRepository = Mockito.mock(EmployeeToProductRepository.class);
        setMock(mockEmployeeToProductRepository);

        productService = ProductServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = ProductRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldProductInstance);

        instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldEmployeeInstance);

        instance = EmployeeToProductRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldLinkInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockProductRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;

        ProductIncomingDTO dto = new ProductIncomingDTO("product #2");
        Product product = new Product(expectedId, "product #10", List.of());

        Mockito.doReturn(product).when(mockProductRepository).save(Mockito.any(Product.class));

        ProductOutGoingDTO result = productService.save(dto);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        ProductUpdateDTO dto = new ProductUpdateDTO(expectedId, "product update #1");

        Mockito.doReturn(true).when(mockProductRepository).exitsById(Mockito.any());

        productService.update(dto);

        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(mockProductRepository).update(argumentCaptor.capture());

        Product result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateNotFound() {
        ProductUpdateDTO dto = new ProductUpdateDTO(1L, "product update #1");

        Mockito.doReturn(false).when(mockProductRepository).exitsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    productService.update(dto);
                }, "Not found."
        );
        Assertions.assertEquals("Product not found.", exception.getMessage());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<Product> product = Optional.of(new Product(expectedId, "product found #1", List.of()));

        Mockito.doReturn(true).when(mockProductRepository).exitsById(Mockito.any());
        Mockito.doReturn(product).when(mockProductRepository).findById(Mockito.anyLong());

        ProductOutGoingDTO dto = productService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findByIdNotFound() {
        Optional<Product> product = Optional.empty();

        Mockito.doReturn(false).when(mockProductRepository).exitsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    productService.findById(1L);
                }, "Not found."
        );
        Assertions.assertEquals("Product not found.", exception.getMessage());
    }

    @Test
    void findAll() {
        productService.findAll();
        Mockito.verify(mockProductRepository).findAll();
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 100L;

        Mockito.doReturn(true).when(mockProductRepository).exitsById(Mockito.any());
        productService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockProductRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }

    @Test
    void deleteEmployeeFromProduct() throws NotFoundException {
        Long expectedId = 100L;
        Optional<EmployeeToProduct> link = Optional.of(new EmployeeToProduct(expectedId, 1L, 2L));

        Mockito.doReturn(true).when(mockEmployeeRepository).exitsById(Mockito.any());
        Mockito.doReturn(true).when(mockProductRepository).exitsById(Mockito.any());
        Mockito.doReturn(link).when(mockEmployeeToProductRepository).findByEmployeeIdAndProductId(Mockito.anyLong(), Mockito.anyLong());

        productService.deleteEmployeeFromProduct(1L, 1l);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockEmployeeToProductRepository).deleteById(argumentCaptor.capture());
        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }

    @Test
    void addEmployeeToProduct() throws NotFoundException {
        Long expectedEmployeeId = 100L;
        Long expectedProductId = 500L;

        Mockito.doReturn(true).when(mockEmployeeRepository).exitsById(Mockito.any());
        Mockito.doReturn(true).when(mockProductRepository).exitsById(Mockito.any());

        productService.addEmployeeToProduct(expectedProductId, expectedEmployeeId);

        ArgumentCaptor<EmployeeToProduct> argumentCaptor = ArgumentCaptor.forClass(EmployeeToProduct.class);
        Mockito.verify(mockEmployeeToProductRepository).save(argumentCaptor.capture());
        EmployeeToProduct result = argumentCaptor.getValue();

        Assertions.assertEquals(expectedEmployeeId, result.getEmployeeId());
        Assertions.assertEquals(expectedProductId, result.getProductId());
    }
}

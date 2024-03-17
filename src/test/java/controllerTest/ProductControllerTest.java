package controllerTest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.controller.ProductController;
import org.dto.ProductIncomingDTO;
import org.dto.ProductUpdateDTO;
import org.exeption.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.service.ProductService;
import org.service.impl.ProductServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(
        MockitoExtension.class
)
public class ProductControllerTest {
    private static ProductService mockProductService;
    @InjectMocks
    private static ProductController productController;
    private static ProductServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(ProductService mock) {
        try {
            Field instance = ProductServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (ProductServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockProductService = Mockito.mock(ProductService.class);
        setMock(mockProductService);
        productController = new ProductController();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = ProductServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockProductService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("product/all").when(mockRequest).getPathInfo();

        productController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockProductService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("product/2").when(mockRequest).getPathInfo();

        productController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockProductService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("product/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockProductService).findById(100L);

        productController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("product/2q").when(mockRequest).getPathInfo();

        productController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("product/2").when(mockRequest).getPathInfo();

        productController.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockProductService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("product/a100").when(mockRequest).getPathInfo();

        productController.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String expectedName = "New product";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"name\":\"" + expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        productController.doPost(mockRequest, mockResponse);

        ArgumentCaptor<ProductIncomingDTO> argumentCaptor = ArgumentCaptor.forClass(ProductIncomingDTO.class);
        Mockito.verify(mockProductService).save(argumentCaptor.capture());

        ProductIncomingDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        String expectedName = "Update product";

        Mockito.doReturn("product/").when(mockRequest).getPathInfo();
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 4,\"name\": \"" +
                        expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        productController.doPut(mockRequest, mockResponse);

        ArgumentCaptor<ProductUpdateDTO> argumentCaptor = ArgumentCaptor.forClass(ProductUpdateDTO.class);
        Mockito.verify(mockProductService).update(argumentCaptor.capture());

        ProductUpdateDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn("product/").when(mockRequest).getPathInfo();
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        productController.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}

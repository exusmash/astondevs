package controllerTest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.controller.AccessLogController;
import org.dto.AccessLogIncomingDTO;
import org.dto.AccessLogUpdateDTO;
import org.exeption.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.service.AccessLogService;
import org.service.impl.AccessLogServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(
        MockitoExtension.class
)
public class AccessLogControllerTest {
    private static AccessLogService mockAccessLogService;
    @InjectMocks
    private static AccessLogController accessLogController;
    private static AccessLogServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(AccessLogService mock) {
        try {
            Field instance = AccessLogServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (AccessLogServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockAccessLogService = Mockito.mock(AccessLogService.class);
        setMock(mockAccessLogService);
        accessLogController = new AccessLogController();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = AccessLogServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockAccessLogService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("accessLog/all").when(mockRequest).getPathInfo();

        accessLogController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockAccessLogService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("phone/2").when(mockRequest).getPathInfo();

        accessLogController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockAccessLogService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("accessLog/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockAccessLogService).findById(100L);

        accessLogController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("accessLog/2q").when(mockRequest).getPathInfo();

        accessLogController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException {
        Mockito.doReturn("accessLog/2").when(mockRequest).getPathInfo();
        Mockito.doReturn(true).when(mockAccessLogService).delete(Mockito.anyLong());

        accessLogController.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockAccessLogService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("accessLog/a100").when(mockRequest).getPathInfo();

        accessLogController.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String expectedDescription = "desc";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"description\":\"" + expectedDescription + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        accessLogController.doPost(mockRequest, mockResponse);

        ArgumentCaptor<AccessLogIncomingDTO> argumentCaptor = ArgumentCaptor.forClass(AccessLogIncomingDTO.class);
        Mockito.verify(mockAccessLogService).save(argumentCaptor.capture());

        AccessLogIncomingDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedDescription, result.getDescription());
    }

    @Test
    void doPut() throws NotFoundException, IOException {
        String expectedDescription = "desc";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 4,\"description\": \"" +
                        expectedDescription + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        accessLogController.doPut(mockRequest, mockResponse);

        ArgumentCaptor<AccessLogUpdateDTO> argumentCaptor = ArgumentCaptor.forClass(AccessLogUpdateDTO.class);
        Mockito.verify(mockAccessLogService).update(argumentCaptor.capture());

        AccessLogUpdateDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedDescription, result.getDescription());
    }
}

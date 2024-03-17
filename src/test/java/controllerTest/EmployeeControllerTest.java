package controllerTest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.controller.EmployeeController;
import org.dto.EmployeeIncomingDTO;
import org.dto.EmployeeUpdateDTO;
import org.exeption.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.service.EmployeeService;
import org.service.impl.EmployeeServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(
        MockitoExtension.class
)
public class EmployeeControllerTest {
    private static EmployeeService mockEmployeeService;
    @InjectMocks
    private static EmployeeController employeeController;
    private static EmployeeServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(EmployeeService mock) {
        try {
            Field instance = EmployeeServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (EmployeeServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockEmployeeService = Mockito.mock(EmployeeService.class);
        setMock(mockEmployeeService);
        employeeController = new EmployeeController();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = EmployeeServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockEmployeeService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("employee/all").when(mockRequest).getPathInfo();

        employeeController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockEmployeeService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("employee/2").when(mockRequest).getPathInfo();

        employeeController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockEmployeeService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("employee/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockEmployeeService).findById(100L);

        employeeController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("employee/2q").when(mockRequest).getPathInfo();

        employeeController.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("employee/2").when(mockRequest).getPathInfo();

        employeeController.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockEmployeeService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteNotFound() throws IOException, NotFoundException {
        Mockito.doReturn("employee/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockEmployeeService).delete(100L);

        employeeController.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockEmployeeService).delete(100L);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("employee/a100").when(mockRequest).getPathInfo();

        employeeController.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String expectedFirstname = "New first";
        String expectedLastname = "New last";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"firstName\":\"" + expectedFirstname + "\"" +
                        ",\"lastName\":\"" + expectedLastname + "\"" +
                        ", \"role\":{\"id\":4,\"name\":\"Admin\"} " +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        employeeController.doPost(mockRequest, mockResponse);

        ArgumentCaptor<EmployeeIncomingDTO> argumentCaptor = ArgumentCaptor.forClass(EmployeeIncomingDTO.class);
        Mockito.verify(mockEmployeeService).save(argumentCaptor.capture());

        EmployeeIncomingDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedFirstname, result.getFirstName());
        Assertions.assertEquals(expectedLastname, result.getLastName());
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        String expectedFirstname = "New first";
        String expectedLastname = "New last";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("{\"id\": 1," +
                        "\"firstName\":\"" + expectedFirstname + "\"" +
                        ",\"lastName\":\"" + expectedLastname + "\"" +
                        ", \"role\":{\"id\":4}, " +
                        "\"accessLogList\": [{ \"id\": 1,\"description\": \"desc\"}]," +
                        "\"productList\": [{\"id\": 2}]" +
                        "}",
                null
        ).when(mockBufferedReader).readLine();

        employeeController.doPut(mockRequest, mockResponse);

        ArgumentCaptor<EmployeeUpdateDTO> argumentCaptor = ArgumentCaptor.forClass(EmployeeUpdateDTO.class);
        Mockito.verify(mockEmployeeService).update(argumentCaptor.capture());

        EmployeeUpdateDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedFirstname, result.getFirstName());
        Assertions.assertEquals(expectedLastname, result.getLastName());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        employeeController.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}

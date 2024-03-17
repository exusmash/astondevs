package serviceTest;

import org.dto.EmployeeIncomingDTO;
import org.dto.EmployeeOutGoingDTO;
import org.dto.EmployeeUpdateDTO;
import org.dto.RoleUpdateDTO;
import org.exeption.NotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.model.Employee;
import org.model.Role;
import org.repository.EmployeeRepository;
import org.repository.impl.EmployeeRepositoryImpl;
import org.service.EmployeeService;
import org.service.impl.EmployeeServiceImpl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class EmployeeServiceTest {
    private static EmployeeService employeeService;
    private static EmployeeRepository mockEmployeeRepository;
    private static Role role;
    private static EmployeeRepositoryImpl oldInstance;

    private static void setMock(EmployeeRepository mock) {
        try {
            Field instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (EmployeeRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        role = new Role(1L, "role#1");
        mockEmployeeRepository = Mockito.mock(EmployeeRepository.class);
        setMock(mockEmployeeRepository);
        employeeService = EmployeeServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = EmployeeRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockEmployeeRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;

        EmployeeIncomingDTO dto = new EmployeeIncomingDTO("f1 name", "l1 name", role);
        Employee employee = new Employee(expectedId, "f1 name", "l1 name", role, List.of(), List.of());

        Mockito.doReturn(employee).when(mockEmployeeRepository).save(Mockito.any(Employee.class));

        EmployeeOutGoingDTO result = employeeService.save(dto);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        EmployeeUpdateDTO dto = new EmployeeUpdateDTO(expectedId, "f1 name", "l1 name",
                new RoleUpdateDTO(1L, "role#1"), List.of(), List.of());

        Mockito.doReturn(true).when(mockEmployeeRepository).exitsById(Mockito.any());

        employeeService.update(dto);

        ArgumentCaptor<Employee> argumentCaptor = ArgumentCaptor.forClass(Employee.class);
        Mockito.verify(mockEmployeeRepository).update(argumentCaptor.capture());

        Employee result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateNotFound() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO(1L, "f1 name", "l1 name", null, null, null);

        Mockito.doReturn(false).when(mockEmployeeRepository).exitsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    employeeService.update(dto);
                }, "Not found."
        );
        Assertions.assertEquals("Employee not found.", exception.getMessage());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<Employee> employee = Optional.of(new Employee(expectedId, "f1 name", "l1 name", role, List.of(), List.of()));

        Mockito.doReturn(true).when(mockEmployeeRepository).exitsById(Mockito.any());
        Mockito.doReturn(employee).when(mockEmployeeRepository).findById(Mockito.anyLong());

        EmployeeOutGoingDTO dto = employeeService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findByIdNotFound() {
        Optional<Employee> employee = Optional.empty();

        Mockito.doReturn(false).when(mockEmployeeRepository).exitsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    employeeService.findById(1L);
                }, "Not found."
        );
        Assertions.assertEquals("Employee not found.", exception.getMessage());
    }

    @Test
    void findAll() {
        employeeService.findAll();
        Mockito.verify(mockEmployeeRepository).findAll();
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 100L;

        Mockito.doReturn(true).when(mockEmployeeRepository).exitsById(Mockito.any());
        employeeService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockEmployeeRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }
}

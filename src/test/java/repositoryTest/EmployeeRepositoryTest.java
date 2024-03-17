package repositoryTest;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.model.AccessLog;
import org.model.Employee;
import org.model.Product;
import org.model.Role;
import org.repository.EmployeeRepository;
import org.repository.impl.EmployeeRepositoryImpl;
import org.service.impl.EmployeeServiceImpl;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.util.PropertiesUtil;

import java.util.List;
import java.util.Optional;

public class EmployeeRepositoryTest {
    private static final String INIT_SQL = "sql/schema.sql";
    private static final int containerPort = 5432;
    private static final int localPort = 5433;
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("users_db")
            .withUsername(PropertiesUtil.getProperties("db.username"))
            .withPassword(PropertiesUtil.getProperties("db.password"))
            .withExposedPorts(containerPort)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(localPort), new ExposedPort(containerPort)))
            ))
            .withInitScript(INIT_SQL);
    public static EmployeeRepository employeeRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        employeeRepository = EmployeeRepositoryImpl.getInstance();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(container, "");
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, INIT_SQL);
    }

    @Test
    void save() {
        String expectedFirstname = "new Firstname";
        String expectedLastname = "new Lastname";

        Employee employee = new Employee(
                null,
                expectedFirstname,
                expectedLastname,
                null,
                null,
                null);
        employee = employeeRepository.save(employee);
        Optional<Employee> resultEmployee = employeeRepository.findById(employee.getId());

        Assertions.assertTrue(resultEmployee.isPresent());
        Assertions.assertEquals(expectedFirstname, resultEmployee.get().getFirstName());
        Assertions.assertEquals(expectedLastname, resultEmployee.get().getLastName());
    }

    @Test
    void update() {
        String expectedFirstname = "UPDATE Firstname";
        String expectedLastname = "UPDATE Lastname";
        Long expectedRoleId = 1L;

        Employee employeeForUpdate = employeeRepository.findById(3L).get();

        List<Product> productList = employeeForUpdate.getProductList();
        int accessLogListSize = employeeForUpdate.getAccessLogList().size();
        int productListSize = employeeForUpdate.getProductList().size();
        Role oldRole = employeeForUpdate.getRole();

        Assertions.assertNotEquals(expectedRoleId, employeeForUpdate.getRole().getId());
        Assertions.assertNotEquals(expectedFirstname, employeeForUpdate.getFirstName());
        Assertions.assertNotEquals(expectedLastname, employeeForUpdate.getLastName());

        employeeForUpdate.setFirstName(expectedFirstname);
        employeeForUpdate.setLastName(expectedLastname);
        employeeRepository.update(employeeForUpdate);

        Employee resultEmployee = employeeRepository.findById(3L).get();

        Assertions.assertEquals(expectedFirstname, resultEmployee.getFirstName());
        Assertions.assertEquals(expectedLastname, resultEmployee.getLastName());

        Assertions.assertEquals(accessLogListSize, resultEmployee.getAccessLogList().size());
        Assertions.assertEquals(productListSize, resultEmployee.getProductList().size());
        Assertions.assertEquals(oldRole.getId(), resultEmployee.getRole().getId());

        employeeForUpdate.setAccessLogList(List.of());
        employeeForUpdate.setProductList(List.of());
        employeeForUpdate.setRole(new Role(expectedRoleId, null));
        employeeRepository.update(employeeForUpdate);
        resultEmployee = employeeRepository.findById(3L).get();

        Assertions.assertEquals(0, resultEmployee.getAccessLogList().size());
        Assertions.assertEquals(0, resultEmployee.getProductList().size());
        Assertions.assertEquals(expectedRoleId, resultEmployee.getRole().getId());

        productList.add(new Product(3L, null, null));
        productList.add(new Product(4L, null, null));
        employeeForUpdate.setProductList(productList);
        employeeRepository.update(employeeForUpdate);
        resultEmployee = employeeRepository.findById(3L).get();

        Assertions.assertEquals(3, resultEmployee.getProductList().size());

        productList.remove(2);
        employeeForUpdate.setProductList(productList);
        employeeRepository.update(employeeForUpdate);
        resultEmployee = employeeRepository.findById(3L).get();

        Assertions.assertEquals(2, resultEmployee.getProductList().size());

        employeeForUpdate.setAccessLogList(List.of(
                new AccessLog(null, "new al", null),
                new AccessLog(null, "AccessLog", null)));
        employeeForUpdate.setProductList(List.of(new Product(1L, null, null)));

        employeeRepository.update(employeeForUpdate);
        resultEmployee = employeeRepository.findById(3L).get();

        Assertions.assertEquals(1, resultEmployee.getAccessLogList().size());
        Assertions.assertEquals(1, resultEmployee.getProductList().size());
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = employeeRepository.findAll().size();

        Employee employee = new Employee(
                null,
                "employee for delete Firstname.",
                "employee for delete Lastname.",
                null,
                null,
                null
        );
        employee = employeeRepository.save(employee);

        boolean resultDelete = employeeRepository.deleteById(employee.getId());
        int roleListAfterSize = employeeRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, roleListAfterSize);
    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Employee> employee = employeeRepository.findById(expectedId);
        Assertions.assertEquals(expectedValue, employee.isPresent());
        employee.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @Test
    void findAll() {
        int expectedSize = 7;
        int resultSize = employeeRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void exitsById(Long roleId, Boolean expectedValue) {
        boolean isEmployeeExist = employeeRepository.exitsById(roleId);

        Assertions.assertEquals(expectedValue, isEmployeeExist);
    }
}

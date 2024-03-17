package repositoryTest;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.model.EmployeeToProduct;
import org.repository.EmployeeToProductRepository;
import org.repository.impl.EmployeeToProductRepositoryImpl;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.util.PropertiesUtil;

import java.util.Optional;

public class EmployeeToProductRepositoryTest {
    private static final String INIT_SQL = "sql/schema.sql";
    public static EmployeeToProductRepository employeeToProductRepository;
    private static int containerPort = 5432;
    private static int localPort = 5433;
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
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        employeeToProductRepository = EmployeeToProductRepositoryImpl.getInstance();
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
        Long expectedEmployeeId = 1L;
        Long expectedProductId = 4L;
        EmployeeToProduct link = new EmployeeToProduct(
                null,
                expectedEmployeeId,
                expectedProductId
        );
        link = employeeToProductRepository.save(link);
        Optional<EmployeeToProduct> resultLink = employeeToProductRepository.findById(link.getId());

        Assertions.assertTrue(resultLink.isPresent());
        Assertions.assertEquals(expectedEmployeeId, resultLink.get().getEmployeeId());
        Assertions.assertEquals(expectedProductId, resultLink.get().getProductId());
    }

    @Test
    void update() {
        Long expectedEmployeeId = 1L;
        Long expectedProductId = 4L;

        EmployeeToProduct link = employeeToProductRepository.findById(2L).get();

        Long oldProductId = link.getProductId();
        Long oldEmployeeId = link.getEmployeeId();

        Assertions.assertNotEquals(expectedEmployeeId, oldEmployeeId);
        Assertions.assertNotEquals(expectedProductId, oldProductId);

        link.setEmployeeId(expectedEmployeeId);
        link.setProductId(expectedProductId);

        employeeToProductRepository.update(link);

        EmployeeToProduct resultLink = employeeToProductRepository.findById(2L).get();

        Assertions.assertEquals(link.getId(), resultLink.getId());
        Assertions.assertEquals(expectedEmployeeId, resultLink.getEmployeeId());
        Assertions.assertEquals(expectedProductId, resultLink.getProductId());
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = employeeToProductRepository.findAll().size();

        EmployeeToProduct link = new EmployeeToProduct(null, 1L, 3L);
        link = employeeToProductRepository.save(link);

        int resultSizeBefore = employeeToProductRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        boolean resultDelete = employeeToProductRepository.deleteById(link.getId());

        int resultSizeAfter = employeeToProductRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSizeAfter);
    }

    @DisplayName("Delete by EmployeeId.")
    @ParameterizedTest
    @CsvSource(value = {
            "2, true",
            "1000, false"
    })
    void deleteByEmployeeId(Long expectedEmployeeId, Boolean expectedValue) {
        int beforeSize = employeeToProductRepository.findAllByEmployeeId(expectedEmployeeId).size();
        Boolean resultDelete = employeeToProductRepository.deleteByEmployeeId(expectedEmployeeId);

        int afterDelete = employeeToProductRepository.findAllByEmployeeId(expectedEmployeeId).size();

        Assertions.assertEquals(expectedValue, resultDelete);
        if (beforeSize != 0) {
            Assertions.assertNotEquals(beforeSize, afterDelete);
        }
    }

    @DisplayName("Delete by Product Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "2, true",
            "1000, false"
    })
    void deleteByProductId(Long expectedProductId, Boolean expectedValue) {
        int beforeSize = employeeToProductRepository.findAllByProductId(expectedProductId).size();
        Boolean resultDelete = employeeToProductRepository.deleteByProductId(expectedProductId);

        int afterDelete = employeeToProductRepository.findAllByProductId(expectedProductId).size();

        Assertions.assertEquals(expectedValue, resultDelete);
        if (beforeSize != 0) {
            Assertions.assertNotEquals(beforeSize, afterDelete);
        }
    }

    @DisplayName("Delete by Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true, 1, 1",
            "3, true, 3, 2",
            "1000, false, 0, 0"
    })
    void findById(Long expectedId, Boolean expectedValue, Long expectedEmployeeId, Long expectedProductId) {
        Optional<EmployeeToProduct> link = employeeToProductRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, link.isPresent());
        if (link.isPresent()) {
            Assertions.assertEquals(expectedId, link.get().getId());
            Assertions.assertEquals(expectedEmployeeId, link.get().getEmployeeId());
            Assertions.assertEquals(expectedProductId, link.get().getProductId());
        }
    }

    @Test
    void findAll() {
        int expectedSize = 8;
        int resultSize = employeeToProductRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "3, true",
            "1000, false"
    })
    void exitsById(Long expectedId, Boolean expectedValue) {
        Boolean resultValue = employeeToProductRepository.exitsById(expectedId);

        Assertions.assertEquals(expectedValue, resultValue);
    }

    @DisplayName("Find by employee Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 1",
            "6, 2",
            "1000, 0"
    })
    void findAllByEmployeeId(Long employeeId, int expectedSize) {
        int resultSize = employeeToProductRepository.findAllByEmployeeId(employeeId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Find product by employee Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "3, 1",
            "6, 2",
            "1000, 0"
    })
    void findProductsByEmployeeId(Long employeeId, int expectedSize) {
        int resultSize = employeeToProductRepository.findProductsByEmployeeId(employeeId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Find by product Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 3",
            "2, 3",
            "3, 1",
            "1000, 0"
    })
    void findAllByProductId(Long productId, int expectedSize) {
        int resultSize = employeeToProductRepository.findAllByProductId(productId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Find employees by product Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 3",
            "2, 3",
            "3, 1",
            "1000, 0"
    })
    void findEmployeesByProductId(Long productId, int expectedSize) {
        int resultSize = employeeToProductRepository.findEmployeesByProductId(productId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Find by employees and by product Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 1, true",
            "1, 4, false"
    })
    void findByEmployeeIdAndProductId(Long employeeId, Long productId, Boolean expectedValue) {
        Optional<EmployeeToProduct> link = employeeToProductRepository.findByEmployeeIdAndProductId(employeeId, productId);

        Assertions.assertEquals(expectedValue, link.isPresent());
    }
}

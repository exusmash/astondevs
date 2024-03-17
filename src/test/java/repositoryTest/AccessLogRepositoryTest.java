package repositoryTest;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.model.AccessLog;
import org.repository.AccessLogRepository;
import org.repository.impl.AccessLogRepositoryImpl;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.util.PropertiesUtil;

import java.util.Optional;

public class AccessLogRepositoryTest {
    private static final String INIT_SQL = "sql/schema.sql";
    public static AccessLogRepository accessLogRepository;
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
        accessLogRepository = AccessLogRepositoryImpl.getInstance();
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
        String expectedDesc = "Loggg";
        AccessLog accessLog = new AccessLog(
                null,
                expectedDesc,
                null
        );
        accessLog = accessLogRepository.save(accessLog);
        Optional<AccessLog> resultAccessLog = accessLogRepository.findById(accessLog.getId());

        Assertions.assertTrue(resultAccessLog.isPresent());
        Assertions.assertEquals(expectedDesc, resultAccessLog.get().getDescription());
    }

    @Test
    void update() {
        String expectedLog = "Logg)";

        AccessLog accessLogUpdate = accessLogRepository.findById(3L).get();
        String oldAccessLog = accessLogUpdate.getDescription();

        accessLogUpdate.setDescription(expectedLog);
        accessLogRepository.update(accessLogUpdate);

        AccessLog accessLog = accessLogRepository.findById(3L).get();

        Assertions.assertNotEquals(expectedLog, oldAccessLog);
        Assertions.assertEquals(expectedLog, accessLog.getDescription());
    }

    @DisplayName("Delete by ID")
    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = accessLogRepository.findAll().size();

        AccessLog tempAccessLog = new AccessLog(null, "Lo0g", null);
        tempAccessLog = accessLogRepository.save(tempAccessLog);

        int resultSizeBefore = accessLogRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        boolean resultDelete = accessLogRepository.deleteById(tempAccessLog.getId());
        int resultSizeListAfter = accessLogRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSizeListAfter);
    }

    @DisplayName("Delete by ID")
    @Test
    void deleteByEmployeeId() {
        Boolean expectedValue = true;
        int expectedSize = accessLogRepository.findAll().size() - accessLogRepository.findAllByEmployeeId(1L).size();

        boolean resultDelete = accessLogRepository.deleteByEmployeeId(1L);

        int resultSize = accessLogRepository.findAll().size();
        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Check exist by Access Log.")
    @ParameterizedTest
    @CsvSource(value = {
            "'Loggg',true",
            "'not log', false"
    })
    void existsByDescription(String description, Boolean expectedValue) {
        boolean isExist = accessLogRepository.existsByDescription(description);

        Assertions.assertEquals(expectedValue, isExist);
    }

    @DisplayName("Find by Description.")
    @ParameterizedTest
    @CsvSource(value = {
            "'Loggg',true",
            "'not log', false"
    })
    void findByDescription(String findDesc, Boolean expectedValue) {
        Optional<AccessLog> accessLog = accessLogRepository.findByDescription(findDesc);

        Assertions.assertEquals(expectedValue, accessLog.isPresent());
        if (accessLog.isPresent()) {
            Assertions.assertEquals(findDesc, accessLog.get().getDescription());
        }
    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "4, true",
            "1000, false"
    })
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<AccessLog> accessLog = accessLogRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, accessLog.isPresent());
        if (accessLog.isPresent()) {
            Assertions.assertEquals(expectedId, accessLog.get().getId());
        }
    }

    @Test
    void findAll() {
        int expectedSize = 7;
        int resultSize = accessLogRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "4, true",
            "1000, false"
    })
    void exitsById(Long expectedId, Boolean expectedValue) {
        Boolean resultValue = accessLogRepository.exitsById(expectedId);

        Assertions.assertEquals(expectedValue, resultValue);
    }

    @DisplayName("Find by EmployeeId")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 2",
            "2, 2",
            "3, 1",
            "1000, 0"
    })
    void findAllByEmployeeId(Long employeeId, int expectedSize) {
        int resultSize = accessLogRepository.findAllByEmployeeId(employeeId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }
}

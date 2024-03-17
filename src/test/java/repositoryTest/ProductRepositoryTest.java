package repositoryTest;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.model.Product;
import org.repository.ProductRepository;
import org.repository.impl.ProductRepositoryImpl;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.util.PropertiesUtil;

import java.util.List;
import java.util.Optional;

@Testcontainers
@Tag("DockerRequired")
public class ProductRepositoryTest {
    private static final String INIT_SQL = "sql/schema.sql";
    public static ProductRepository productRepository;
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
        productRepository = ProductRepositoryImpl.getInstance();
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
        String expectedName = "new Product Yo!";
        Product product = new Product(
                null,
                expectedName,
                null
        );
        product = productRepository.save(product);
        Optional<Product> resultProduct = productRepository.findById(product.getId());

        Assertions.assertTrue(resultProduct.isPresent());
        Assertions.assertEquals(expectedName, resultProduct.get().getName());

    }

    @Test
    void update() {
        String expectedName = "Update product name";

        Product product = productRepository.findById(2L).get();
        String oldName = product.getName();
        int expectedSizeUserList = product.getEmployeeList().size();
        product.setName(expectedName);
        productRepository.update(product);

        Product resultProduct = productRepository.findById(2L).get();
        int resultSizeUserList = resultProduct.getEmployeeList().size();

        Assertions.assertNotEquals(expectedName, oldName);
        Assertions.assertEquals(expectedName, resultProduct.getName());
        Assertions.assertEquals(expectedSizeUserList, resultSizeUserList);
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = productRepository.findAll().size();

        Product tempProduct = new Product(null, "New product", List.of());
        tempProduct = productRepository.save(tempProduct);

        int resultSizeBefore = productRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        boolean resultDelete = productRepository.deleteById(tempProduct.getId());
        int resultSizeAfter = productRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSizeAfter);

    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "4, true",
            "1000, false"
    })
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Product> product = productRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, product.isPresent());
        product.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @Test
    void findAll() {
        int expectedSize = 4;
        int resultSize = productRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @DisplayName("Exist by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void exitsById(Long productId, Boolean expectedValue) {
        boolean isRoleExist = productRepository.exitsById(productId);

        Assertions.assertEquals(expectedValue, isRoleExist);
    }
}

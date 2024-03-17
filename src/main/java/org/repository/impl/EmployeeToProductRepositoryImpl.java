package org.repository.impl;

import org.db.ConnectionManager;
import org.db.ConnectionManagerImpl;
import org.exeption.RepositoryException;
import org.model.Employee;
import org.model.EmployeeToProduct;
import org.model.Product;
import org.repository.EmployeeRepository;
import org.repository.EmployeeToProductRepository;
import org.repository.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeToProductRepositoryImpl implements EmployeeToProductRepository {
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final ProductRepository productRepository = ProductRepositoryImpl.getInstance();
    private static final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private static final String SAVE_SQL = """
            INSERT INTO employee_product_link (employee_id, product_id)
            VALUES (?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE employee_product_link
            SET employee_id = ?,
                product_id = ?
            WHERE link_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM employee_product_link
            WHERE link_id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT link_id, employee_id, product_id FROM employee_product_link
            WHERE link_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT link_id, employee_id, product_id FROM employee_product_link;
            """;
    private static final String FIND_ALL_BY_EMPLOYEE_ID_SQL = """
            SELECT link_id, employee_id, product_id FROM employee_product_link
            WHERE employee_id = ?;
            """;
    private static final String FIND_ALL_BY_PRODUCT_ID_SQL = """
            SELECT link_id, employee_id, product_id FROM employee_product_link
            WHERE product_id = ?;
            """;
    private static final String FIND_BY_EMPLOYEE_ID_AND_PRODUCT_ID_SQL = """
            SELECT link_id, employee_id, product_id FROM employee_product_link
            WHERE employee_id = ? AND product_id = ?
            LIMIT 1;
            """;
    private static final String DELETE_BY_EMPLOYEE_ID_SQL = """
            DELETE FROM employee_product_link
            WHERE employee_id = ?;
            """;
    private static final String DELETE_BY_PRODUCT_ID_SQL = """
            DELETE FROM employee_product_link
            WHERE product_id = ?;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM employee_product_link
                        WHERE link_id = ?
                        LIMIT 1);
            """;
    private static EmployeeToProductRepository instance;

    private EmployeeToProductRepositoryImpl() {
    }

    public static synchronized EmployeeToProductRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeToProductRepositoryImpl();
        }
        return instance;
    }

    private static EmployeeToProduct createEmployeeToProduct(ResultSet resultSet) throws SQLException {
        EmployeeToProduct employeeToProduct;
        employeeToProduct = new EmployeeToProduct(
                resultSet.getLong("link_id"),
                resultSet.getLong("employee_id"),
                resultSet.getLong("product_id")
        );
        return employeeToProduct;
    }

    @Override
    public EmployeeToProduct save(EmployeeToProduct employeeToProduct) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, employeeToProduct.getEmployeeId());
            preparedStatement.setLong(2, employeeToProduct.getProductId());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                employeeToProduct = new EmployeeToProduct(
                        resultSet.getLong("link_id"),
                        employeeToProduct.getEmployeeId(),
                        employeeToProduct.getProductId()
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return employeeToProduct;
    }

    @Override
    public void update(EmployeeToProduct employeeToProduct) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setLong(1, employeeToProduct.getEmployeeId());
            preparedStatement.setLong(2, employeeToProduct.getProductId());
            preparedStatement.setLong(3, employeeToProduct.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {

            preparedStatement.setLong(1, id);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public boolean deleteByEmployeeId(Long employeeId) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_EMPLOYEE_ID_SQL);) {

            preparedStatement.setLong(1, employeeId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public boolean deleteByProductId(Long productId) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_PRODUCT_ID_SQL);) {

            preparedStatement.setLong(1, productId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public Optional<EmployeeToProduct> findById(Long id) {
        EmployeeToProduct employeeToProduct = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employeeToProduct = createEmployeeToProduct(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(employeeToProduct);
    }

    @Override
    public List<EmployeeToProduct> findAll() {
        List<EmployeeToProduct> employeeToProductList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeToProductList.add(createEmployeeToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return employeeToProductList;
    }

    @Override
    public boolean exitsById(Long id) {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return isExists;
    }

    public List<EmployeeToProduct> findAllByEmployeeId(Long employeeId) {
        List<EmployeeToProduct> employeeToProductList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_EMPLOYEE_ID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeToProductList.add(createEmployeeToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return employeeToProductList;
    }

    @Override
    public List<Product> findProductsByEmployeeId(Long employeeId) {
        List<Product> productList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_EMPLOYEE_ID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long productId = resultSet.getLong("product_id");
                Optional<Product> optionalProduct = productRepository.findById(productId);
                optionalProduct.ifPresent(productList::add);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return productList;
    }

    public List<EmployeeToProduct> findAllByProductId(Long productId) {
        List<EmployeeToProduct> employeeToProductList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_PRODUCT_ID_SQL)) {

            preparedStatement.setLong(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeToProductList.add(createEmployeeToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return employeeToProductList;
    }

    public List<Employee> findEmployeesByProductId(Long productId) {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_PRODUCT_ID_SQL)) {

            preparedStatement.setLong(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long employeeId = resultSet.getLong("employee_id");
                Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
                optionalEmployee.ifPresent(employeeList::add);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return employeeList;
    }

    @Override
    public Optional<EmployeeToProduct> findByEmployeeIdAndProductId(Long employeeId, Long productId) {
        Optional<EmployeeToProduct> employeeToProduct = Optional.empty();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_EMPLOYEE_ID_AND_PRODUCT_ID_SQL)) {

            preparedStatement.setLong(1, employeeId);
            preparedStatement.setLong(2, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employeeToProduct = Optional.of(createEmployeeToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return employeeToProduct;
    }
}

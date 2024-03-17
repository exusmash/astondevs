package org.repository.impl;

import org.db.ConnectionManager;
import org.db.ConnectionManagerImpl;
import org.exeption.RepositoryException;
import org.model.*;
import org.repository.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private static final String SAVE_SQL = """
            INSERT INTO employees (firstName, lastName, role_id)
            VALUES (?, ? ,?) ;
            """;
    private static final String UPDATE_SQL = """
            UPDATE employees
            SET firstName = ?,
                lastName = ?,
                role_id =?
            WHERE id = ?  ;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM employees
            WHERE id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, firstName, lastName, role_id FROM employees
            WHERE id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, firstName, lastName, role_id FROM employees;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM employees
                        WHERE id = ?
                        LIMIT 1);
            """;
    private static EmployeeRepository instance;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private final EmployeeToProductRepository employeeToProductRepository = EmployeeToProductRepositoryImpl.getInstance();
    private final AccessLogRepository accessLogRepository = AccessLogRepositoryImpl.getInstance();
    private final RoleRepository roleRepository = RoleRepositoryImpl.getInstance();
    private final ProductRepository productRepository = ProductRepositoryImpl.getInstance();

    private EmployeeRepositoryImpl() {
    }

    public static synchronized EmployeeRepository getInstance() {
        if (instance == null) {
            instance = new EmployeeRepositoryImpl();
        }
        return instance;
    }

    private Employee createEmployee(ResultSet resultSet) throws SQLException {
        Long employeeId = resultSet.getLong("id");
        Role role = roleRepository.findById(resultSet.getLong("role_id")).orElse(null);

        return new Employee(
                employeeId,
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                role,
                null,
                null
        );
    }

    @Override
    public Employee save(Employee employee) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            if (employee.getRole() == null) {
                preparedStatement.setNull(3, Types.NULL);
            } else {
                preparedStatement.setLong(3, employee.getRole().getId());
            }
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                employee = new Employee(
                        resultSet.getLong("id"),
                        employee.getFirstName(),
                        employee.getLastName(),
                        employee.getRole(),
                        null,
                        null
                );
            }
            saveAccessLogList(employee);
            saveProductList(employee);
            employee.getAccessLogList();
            employee.getProductList();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return employee;
    }


    private void saveProductList(Employee employee) {
        if (employee.getProductList() != null && !employee.getProductList().isEmpty()) {
            List<Long> productIdList = new ArrayList<>(
                    employee.getProductList()
                            .stream()
                            .map(Product::getId)
                            .toList()
            );
            List<EmployeeToProduct> existsProductList = employeeToProductRepository.findAllByEmployeeId(employee.getId());
            for (EmployeeToProduct employeeToProduct : existsProductList) {
                if (!productIdList.contains(employeeToProduct.getProductId())) {
                    employeeToProductRepository.deleteById(employeeToProduct.getId());
                }
                productIdList.remove(employeeToProduct.getProductId());
            }
            for (Long productId : productIdList) {
                if (productRepository.exitsById(productId)) {
                    EmployeeToProduct employeeToProduct = new EmployeeToProduct(
                            null,
                            employee.getId(),
                            productId
                    );
                    employeeToProductRepository.save(employeeToProduct);
                }
            }

        } else {
            employeeToProductRepository.deleteByEmployeeId(employee.getId());
        }
    }


    private void saveAccessLogList(Employee employee) {
        if (employee.getAccessLogList() != null && !employee.getAccessLogList().isEmpty()) {
            List<AccessLog> accessLogList = new ArrayList<>(employee.getAccessLogList());
            List<Long> existsAccessLogIdList = new ArrayList<>(
                    accessLogRepository.findAllByEmployeeId(employee.getId())
                            .stream()
                            .map(AccessLog::getId)
                            .toList()
            );

            for (int i = 0; i < accessLogList.size(); i++) {
                AccessLog accessLog = accessLogList.get(i);
                accessLog.setEmployee(employee);
                if (existsAccessLogIdList.contains(accessLog.getId())) {
                    accessLogRepository.update(accessLog);
                } else {
                    saveOrUpdateExitsDescription(accessLog);
                }
                accessLogList.set(i, null);
                existsAccessLogIdList.remove(accessLog.getId());
            }
            accessLogList
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(accessLog -> {
                        accessLog.setEmployee(employee);
                        accessLogRepository.save(accessLog);
                    });
            existsAccessLogIdList
                    .stream()
                    .forEach(accessLogRepository::deleteById);
        } else {
            accessLogRepository.deleteByEmployeeId(employee.getId());
        }

    }


    private void saveOrUpdateExitsDescription(AccessLog accessLog) {
        if (accessLogRepository.existsByDescription(accessLog.getDescription())) {
            Optional<AccessLog> exitDescription = accessLogRepository.findByDescription(accessLog.getDescription());
            if (exitDescription.isPresent()
                    && exitDescription.get().getEmployee() != null
                    && exitDescription.get().getEmployee().getId() > 0) {
                accessLog = new AccessLog(exitDescription.get().getId(),
                        exitDescription.get().getDescription(),
                        exitDescription.get().getEmployee()
                );
                accessLogRepository.update(accessLog);

            }
        } else {
            accessLogRepository.save(accessLog);
        }

    }

    @Override
    public void update(Employee employee) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, employee.getFirstName());
            preparedStatement.setString(2, employee.getLastName());
            if (employee.getRole() == null) {
                preparedStatement.setNull(3, Types.NULL);
            } else {
                preparedStatement.setLong(3, employee.getRole().getId());
            }
            preparedStatement.setLong(4, employee.getId());

            preparedStatement.executeUpdate();
            saveAccessLogList(employee);
            saveProductList(employee);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {

            employeeToProductRepository.deleteByEmployeeId(id);
            accessLogRepository.deleteByEmployeeId(id);

            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        Employee employee = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                employee = createEmployee(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(employee);
    }

    @Override
    public List<Employee> findAll() {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                employeeList.add(createEmployee(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return employeeList;
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
}

package org.repository.impl;

import org.db.ConnectionManager;
import org.db.ConnectionManagerImpl;
import org.exeption.RepositoryException;
import org.model.AccessLog;
import org.model.Employee;
import org.repository.AccessLogRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccessLogRepositoryImpl implements AccessLogRepository {
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final String SAVE_SQL = """
            INSERT INTO access_log (description, employee_id)
            VALUES (?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE access_log
            SET description = ?,
                employee_id = ?
            WHERE id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM access_log
            WHERE id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, description, employee_id FROM access_log
            WHERE id = ?
            LIMIT 1;
            """;
    private static final String FIND_BY_DESCRIPTION_SQL = """
            SELECT id, description, employee_id FROM access_log
            WHERE description = ?
            LIMIT 1;
            """;
    private static final String EXIST_BY_DESCRIPTION_SQL = """
            SELECT exists (
                SELECT 1
                    FROM access_log
                        WHERE description = LOWER(?)
                        LIMIT 1
            );
            """;
    private static final String FIND_ALL_BY_EMPLOYEE_SQL = """
            SELECT id, description, employee_id FROM access_log
            WHERE employee_id = ?;
            """;
    private static final String DELETE_ALL_BY_EMPLOYEE_SQL = """
            DELETE FROM access_log
            WHERE employee_id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, description, employee_id FROM access_log;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM access_log
                        WHERE id = ?
                        LIMIT 1);
            """;
    private static AccessLogRepository instance;

    private AccessLogRepositoryImpl() {
    }

    public static synchronized AccessLogRepository getInstance() {
        if (instance == null) {
            instance = new AccessLogRepositoryImpl();
        }
        return instance;
    }

    private static AccessLog createAccessLog(ResultSet resultSet) throws SQLException {
        AccessLog accessLog;
        Employee employee = new Employee(
                resultSet.getLong("employee_id"),
                null,
                null,
                null,
                List.of(),
                List.of()
        );
        accessLog = new AccessLog(
                resultSet.getLong("id"),
                resultSet.getString("description"),
                employee);
        return accessLog;
    }

    @Override
    public AccessLog save(AccessLog accessLog) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, accessLog.getDescription());
            if (accessLog.getEmployee() == null) {
                preparedStatement.setNull(2, Types.NULL);
            } else {
                preparedStatement.setLong(2, accessLog.getEmployee().getId());
            }
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {

                accessLog = new AccessLog(
                        resultSet.getLong("id"),
                        accessLog.getDescription(),
                        accessLog.getEmployee()
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return accessLog;
    }

    @Override
    public void update(AccessLog accessLog) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, accessLog.getDescription());
            if (accessLog.getEmployee() == null) {
                preparedStatement.setNull(2, Types.NULL);
            } else {
                preparedStatement.setLong(2, accessLog.getEmployee().getId());
            }
            preparedStatement.setLong(3, accessLog.getId());

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
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_BY_EMPLOYEE_SQL);) {

            preparedStatement.setLong(1, employeeId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public boolean existsByDescription(String description) {
        boolean isExists = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_DESCRIPTION_SQL)) {

            preparedStatement.setString(1, description);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return isExists;
    }

    @Override
    public Optional<AccessLog> findByDescription(String description) {
        AccessLog accessLog = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_DESCRIPTION_SQL)) {

            preparedStatement.setString(1, description);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                accessLog = createAccessLog(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(accessLog);
    }

    @Override
    public Optional<AccessLog> findById(Long id) {
        AccessLog accessLog = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                accessLog = createAccessLog(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(accessLog);
    }

    @Override
    public List<AccessLog> findAll() {
        List<AccessLog> accessLogList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                accessLogList.add(createAccessLog(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return accessLogList;
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

    @Override
    public List<AccessLog> findAllByEmployeeId(Long employeeId) {
        List<AccessLog> accessLogList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_EMPLOYEE_SQL)) {

            preparedStatement.setLong(1, employeeId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                accessLogList.add(createAccessLog(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return accessLogList;
    }
}

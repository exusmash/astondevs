package org.repository.impl;

import org.db.ConnectionManager;
import org.db.ConnectionManagerImpl;
import org.exeption.RepositoryException;
import org.model.Role;
import org.repository.RoleRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleRepositoryImpl implements RoleRepository {
    private static final String SAVE_SQL = """
            INSERT INTO roles (name)
            VALUES (?) ;
            """;
    private static final String UPDATE_SQL = """
            UPDATE roles
            SET name = ?
            WHERE id = ?  ;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM roles
            WHERE id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, name FROM roles
            WHERE id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, name FROM roles ;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM roles
                        WHERE id = ?
                        LIMIT 1);
            """;
    private static RoleRepository instance;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private RoleRepositoryImpl() {
    }

    public static synchronized RoleRepository getInstance() {
        if (instance == null) {
            instance = new RoleRepositoryImpl();
        }
        return instance;
    }

    private static Role createRole(ResultSet resultSet) throws SQLException {
        Role role;
        role = new Role(resultSet.getLong("id"),
                resultSet.getString("name"));
        return role;
    }

    @Override
    public Role save(Role role) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, role.getName());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                role = new Role(
                        resultSet.getLong("id"),
                        role.getName());
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return role;
    }

    @Override
    public void update(Role role) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, role.getName());
            preparedStatement.setLong(2, role.getId());

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
    public Optional<Role> findById(Long id) {
        Role role = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                role = createRole(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(role);
    }

    @Override
    public List<Role> findAll() {
        List<Role> roleList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                roleList.add(createRole(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return roleList;
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

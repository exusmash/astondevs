package org.repository.impl;

import org.db.ConnectionManager;
import org.db.ConnectionManagerImpl;
import org.exeption.RepositoryException;
import org.model.Product;
import org.repository.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {
    private static final String SAVE_SQL = """
            INSERT INTO products (name)
            VALUES (?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE products
            SET name = ?
            WHERE id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM products
            WHERE id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT id, name FROM products
            WHERE id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT id, name FROM products;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM products
                        WHERE id = ?
                        LIMIT 1);
            """;
    private static ProductRepository instance;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private ProductRepositoryImpl() {
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepositoryImpl();
        }
        return instance;
    }

    private static Product createProduct(ResultSet resultSet) throws SQLException {
        Product product;
        product = new Product(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                null);
        return product;
    }

    @Override
    public Product save(Product product) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, product.getName());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                product = new Product(
                        resultSet.getLong("id"),
                        product.getName(),
                        null
                );
                product.getEmployeeList();
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return product;
    }

    @Override
    public void update(Product product) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, product.getName());
            preparedStatement.setLong(2, product.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult = true;
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
    public Optional<Product> findById(Long id) {
        Product product = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                product = createProduct(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(product);
    }

    @Override
    public List<Product> findAll() {
        List<Product> productList = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                productList.add(createProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return productList;
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

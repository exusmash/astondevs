package org.repository;

import org.model.Employee;
import org.model.EmployeeToProduct;
import org.model.Product;

import java.util.List;
import java.util.Optional;

public interface EmployeeToProductRepository extends Repository<EmployeeToProduct, Long> {
    boolean deleteByEmployeeId(Long employeeId);

    boolean deleteByProductId(Long productId);

    List<EmployeeToProduct> findAllByEmployeeId(Long employeeId);

    List<Product> findProductsByEmployeeId(Long employeeId);

    List<EmployeeToProduct> findAllByProductId(Long productId);

    List<Employee> findEmployeesByProductId(Long productId);

    Optional<EmployeeToProduct> findByEmployeeIdAndProductId(Long employeeId, Long productId);
}

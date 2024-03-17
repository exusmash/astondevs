package org.model;

import org.repository.AccessLogRepository;
import org.repository.EmployeeToProductRepository;
import org.repository.impl.AccessLogRepositoryImpl;
import org.repository.impl.EmployeeToProductRepositoryImpl;

import java.util.List;

public class Employee {
    private static final AccessLogRepository accessLogRepository = AccessLogRepositoryImpl.getInstance();
    private static final EmployeeToProductRepository employeeToProductRepository = EmployeeToProductRepositoryImpl.getInstance();
    private Long id;
    private String firstName;
    private String lastName;
    private Role role;
    private List<AccessLog> accessLogList;
    private List<Product> productList;

    public Employee() {
    }

    public Employee(Long id, String firstName, String lastName, Role role, List<AccessLog> accessLogList, List<Product> productList) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.accessLogList = accessLogList;
        this.productList = productList;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<AccessLog> getAccessLogList() {
        if (accessLogList == null) {
            this.accessLogList = accessLogRepository.findAllByEmployeeId(this.id);
        }
        return accessLogList;
    }

    public void setAccessLogList(List<AccessLog> accessLogList) {
        this.accessLogList = accessLogList;
    }

    public List<Product> getProductList() {
        if (productList == null) {
            productList = employeeToProductRepository.findProductsByEmployeeId(this.id);
        }
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}

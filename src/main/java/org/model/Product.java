package org.model;

import org.repository.EmployeeToProductRepository;
import org.repository.impl.EmployeeToProductRepositoryImpl;

import java.util.List;

public class Product {
    private static final EmployeeToProductRepository employeeToProductRepository = EmployeeToProductRepositoryImpl.getInstance();
    private Long id;
    private String name;
    private List<Employee> employeeList;

    public Product() {
    }

    public Product(Long id, String name, List<Employee> employeeList) {
        this.id = id;
        this.name = name;
        this.employeeList = employeeList;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Employee> getEmployeeList() {
        if (employeeList == null) {
            employeeList = employeeToProductRepository.findEmployeesByProductId(this.id);
        }
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }
}

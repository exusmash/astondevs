package org.model;

import org.repository.EmployeeRepository;
import org.repository.impl.EmployeeRepositoryImpl;

public class AccessLog {
    private static final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private Long id;
    private String description;
    private Employee employee;

    public AccessLog() {
    }

    public AccessLog(Long id, String description, Employee employee) {
        this.id = id;
        this.description = description;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getEmployee() {
        if (employee != null && employee.getId() > 0 && employee.getFirstName() == null) {
            this.employee = employeeRepository.findById(employee.getId()).orElse(employee);
        } else if (employee != null && employee.getId() == 0) {
            this.employee = null;
        }
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}

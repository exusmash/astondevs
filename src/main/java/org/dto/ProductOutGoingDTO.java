package org.dto;

import java.util.List;

public class ProductOutGoingDTO {
    private Long id;
    private String name;
    private List<EmployeeSmallOutGoingDTO> employeeList;

    public ProductOutGoingDTO() {
    }

    public ProductOutGoingDTO(Long id, String name, List<EmployeeSmallOutGoingDTO> employeeList) {
        this.id = id;
        this.name = name;
        this.employeeList = employeeList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EmployeeSmallOutGoingDTO> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<EmployeeSmallOutGoingDTO> employeeList) {
        this.employeeList = employeeList;
    }
}

package org.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessLogOutGoingDTO {
    private Long id;
    private String description;
    @JsonProperty("employee")
    private EmployeeSmallOutGoingDTO employeeDto;

    public AccessLogOutGoingDTO() {
    }

    public AccessLogOutGoingDTO(Long id, String description, EmployeeSmallOutGoingDTO employeeDto) {
        this.id = id;
        this.description = description;
        this.employeeDto = employeeDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EmployeeSmallOutGoingDTO getEmployeeDto() {
        return employeeDto;
    }

    public void setEmployeeDto(EmployeeSmallOutGoingDTO employeeDto) {
        this.employeeDto = employeeDto;
    }
}

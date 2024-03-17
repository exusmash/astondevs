package org.dto;

public class AccessLogUpdateDTO {
    private Long id;
    private String description;
    private Long employeeId;

    public AccessLogUpdateDTO() {
    }

    public AccessLogUpdateDTO(Long id, String description, Long employeeId) {
        this.id = id;
        this.description = description;
        this.employeeId = employeeId;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getEmployeeId() {
        return employeeId;
    }
}

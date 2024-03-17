package org.dto;

public class EmployeeSmallOutGoingDTO {
    private Long id;
    private String firstName;
    private String lastName;

    public EmployeeSmallOutGoingDTO() {
    }

    public EmployeeSmallOutGoingDTO(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

package org.dto;

import org.model.Role;

public class EmployeeIncomingDTO {
    private String firstName;
    private String lastName;

    private Role role;

    public EmployeeIncomingDTO() {
    }

    public EmployeeIncomingDTO(String firstName, String lastName, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Role getRole() {
        return role;
    }

}

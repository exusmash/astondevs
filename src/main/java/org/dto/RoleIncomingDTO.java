package org.dto;

public class RoleIncomingDTO {
    private String name;

    public RoleIncomingDTO() {
    }

    public RoleIncomingDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

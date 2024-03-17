package org.dto;

public class RoleOutGoingDTO {
    private Long id;
    private String name;

    public RoleOutGoingDTO() {
    }

    public RoleOutGoingDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

package org.dto;

public class ProductUpdateDTO {
    private Long id;
    private String name;

    public ProductUpdateDTO() {
    }

    public ProductUpdateDTO(Long id, String name) {
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

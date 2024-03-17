package org.dto;

import java.util.List;

public class EmployeeUpdateDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private RoleUpdateDTO role;
    private List<AccessLogUpdateDTO> accessLogList;
    private List<ProductUpdateDTO> productList;

    public EmployeeUpdateDTO() {
    }

    public EmployeeUpdateDTO(Long id, String firstName, String lastName, RoleUpdateDTO role, List<AccessLogUpdateDTO> accessLogList, List<ProductUpdateDTO> productList) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.accessLogList = accessLogList;
        this.productList = productList;
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

    public RoleUpdateDTO getRole() {
        return role;
    }

    public List<AccessLogUpdateDTO> getAccessLogList() {
        return accessLogList;
    }

    public List<ProductUpdateDTO> getProductList() {
        return productList;
    }
}

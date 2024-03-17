package org.dto;

import java.util.List;

public class EmployeeOutGoingDTO {
    private Long id;
    private String firstName;
    private String lastName;

    private RoleOutGoingDTO role;
    private List<AccessLogOutGoingDTO> accessLogList;
    private List<ProductOutGoingDTO> productList;

    public EmployeeOutGoingDTO() {
    }

    public EmployeeOutGoingDTO(Long id, String firstName, String lastName, RoleOutGoingDTO role, List<AccessLogOutGoingDTO> accessLogList, List<ProductOutGoingDTO> productList) {
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

    public RoleOutGoingDTO getRole() {
        return role;
    }


    public List<AccessLogOutGoingDTO> getAccessLogList() {
        return accessLogList;
    }

    public List<ProductOutGoingDTO> getProductList() {
        return productList;
    }

}

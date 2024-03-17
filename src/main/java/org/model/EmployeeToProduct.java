package org.model;

public class EmployeeToProduct {
    private Long id;
    private Long employeeId;
    private Long productId;

    public EmployeeToProduct() {
    }

    public EmployeeToProduct(Long id, Long employeeId, Long productId) {
        this.id = id;
        this.employeeId = employeeId;
        this.productId = productId;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}

package org.dto;

public class AccessLogIncomingDTO {
    private String description;

    public AccessLogIncomingDTO() {
    }

    public AccessLogIncomingDTO(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

package com.example.secretmanager.dto;

import javax.persistence.Id;

public class ApplicationDTO {
    @Id
    private String id;

    private String secretToken;

    protected ApplicationDTO() {}

    public ApplicationDTO(String id, String secretToken) {
        this.id = id;
        this.secretToken = secretToken;
    }

    public String getId() {
        return id;
    }

    public String getSecretToken() {
        return secretToken;
    }
}

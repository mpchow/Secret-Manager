package com.example.secretmanager.dto;

import org.springframework.lang.NonNull;

import javax.persistence.Id;

public class ApplicationDTO {
    @Id
    @NonNull
    private String id;

    @NonNull
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

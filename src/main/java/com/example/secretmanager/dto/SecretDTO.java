package com.example.secretmanager.dto;

import org.springframework.lang.NonNull;

import javax.persistence.Id;

public class SecretDTO {
    @Id
    @NonNull
    private String id;

    @NonNull
    private String secretVal;

    protected SecretDTO() {}

    public SecretDTO(String id, String secretVal) {
        this.id = id;
        this.secretVal = secretVal;
    }


    public String getId() {
        return id;
    }

    public String getSecretVal() {
        return secretVal;
    }

}

package com.example.secretmanager.dto;

import javax.persistence.Id;

public class SecretDTO {
    @Id
    private String id;

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

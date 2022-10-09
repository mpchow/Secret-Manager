package com.example.secretmanager.dto;


public class AccessDTO {
    private String id;
    private String secretId;

    protected AccessDTO () {}

    public AccessDTO(String id, String secretId) {
        this.id = id;
        this.secretId = secretId;
    }

    public String getId() {
        return id;
    }

    public String getSecretId() {
        return secretId;
    }
}

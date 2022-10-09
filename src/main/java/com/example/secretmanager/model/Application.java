package com.example.secretmanager.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Application {
    @Id
    private String id;

    private String name;

    private String secretToken;

    // Store the secrets an application is permitted to retrieve as a string of id's separated by ","
    private String allowedSecrets;

    public Application() {
        this.allowedSecrets = "";
        this.id = UUID.randomUUID().toString();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public void setAllowedSecrets(String allowedSecrets) {
        this.allowedSecrets = allowedSecrets;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public String getAllowedSecrets() {
        return allowedSecrets;
    }
}

package com.example.secretmanager.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Application {
    @Id
    private String id;

    private String secretToken;

    // Store the secrets an application is permitted to retrieve as a string of id's separated by ","
    private String allowedSecrets;

    public Application() {
        this.allowedSecrets = "";
    }

    public void setId(String id) {
        this.id = id;
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

    public String getSecretToken() {
        return secretToken;
    }

    public String getAllowedSecrets() {
        return allowedSecrets;
    }
}

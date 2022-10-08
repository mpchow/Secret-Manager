package com.example.secretmanager.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Application {
    @Id
    private String id;

    private String secretToken;

    public Application() {}

    public void setId(String id) {
        this.id = id;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    public String getId() {
        return id;
    }

    public String getSecretToken() {
        return secretToken;
    }
}

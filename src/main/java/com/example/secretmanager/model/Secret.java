package com.example.secretmanager.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Secret {
    @Id
    private String id;

    private String secretVal;

    public Secret() {}

    public void setId(String id) {
        this.id = id;
    }

    public void setSecretVal(String secretVal) {
        this.secretVal = secretVal;
    }

    public String getSecretVal() {
        return secretVal;
    }
}

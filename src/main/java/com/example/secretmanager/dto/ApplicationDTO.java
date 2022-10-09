package com.example.secretmanager.dto;

import javax.persistence.Id;

public class ApplicationDTO {
    @Id
    private String name;

    protected ApplicationDTO() {}

    public ApplicationDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

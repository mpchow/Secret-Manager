package com.example.secretmanager.repository;

import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.model.Application;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends CrudRepository<Application, String> {
    public Optional<Application> findByName(String name);
    public Optional<Application> findById(UUID id);
}

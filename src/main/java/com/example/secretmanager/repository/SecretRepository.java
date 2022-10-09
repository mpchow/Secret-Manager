package com.example.secretmanager.repository;

import com.example.secretmanager.model.Secret;
import org.springframework.data.repository.CrudRepository;

public interface SecretRepository extends CrudRepository<Secret, String> {
}

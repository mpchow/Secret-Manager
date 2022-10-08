package com.example.secretmanager.repository;

import com.example.secretmanager.model.Application;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationRepository extends CrudRepository<Application, String> {
}

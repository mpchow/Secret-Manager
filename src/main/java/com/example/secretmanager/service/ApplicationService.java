package com.example.secretmanager.service;

import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.model.Application;
import com.example.secretmanager.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApplicationService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ApplicationRepository applicationRepository;

    public void saveApplication(ApplicationDTO applicationDTO) {
        Optional<Application> applicationData = applicationRepository.findById(applicationDTO.getId());
        Application application;

        // If the application already exists, replace the exiting token
        if (applicationData.isPresent()) {
            application = applicationData.get();
        } else {
            application = new Application();
            application.setId(applicationDTO.getId());
        }
        application.setSecretToken(passwordEncoder.encode(applicationDTO.getSecretToken()));

        applicationRepository.save(application);
    }

    // Check if a credential is valid
    public Boolean validCredential(String id, String token) {
        Optional<Application> applicationData = applicationRepository.findById(id);

        if (applicationData.isPresent()) {
            Application application = applicationData.get();
            return passwordEncoder.matches(token, application.getSecretToken());
        }
        return false;
    }
}

package com.example.secretmanager.service;

import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.model.Application;
import com.example.secretmanager.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Optional;

import static org.springframework.security.crypto.keygen.KeyGenerators.secureRandom;

@Service
public class ApplicationService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ApplicationRepository applicationRepository;

    public HashMap<String, String> saveApplication(ApplicationDTO applicationDTO) throws IllegalArgumentException {
        Optional<Application> applicationData = applicationRepository.findByName(applicationDTO.getName());

        if (applicationData.isPresent()) {
            throw new IllegalArgumentException();
        }

        HashMap<String, String> response = new HashMap<>();

        Application application = new Application();
        application.setName(applicationDTO.getName());

        SecureRandom secureRandom = new SecureRandom();
        String token = Long.toString(secureRandom.nextLong(0, Long.MAX_VALUE));

        application.setSecretToken(passwordEncoder.encode(token));

        applicationRepository.save(application);

        response.put("id", application.getId());
        response.put("token", token);
        return response;
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

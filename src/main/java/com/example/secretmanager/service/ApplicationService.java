package com.example.secretmanager.service;

import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.model.Application;
import com.example.secretmanager.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    ApplicationRepository applicationRepository;

    public Application newApplication(ApplicationDTO applicationDTO) {
        Application application = new Application();
        application.setId(applicationDTO.getId());

        application.setSecretToken(passwordEncoder.encode(applicationDTO.getSecretToken()));

        return applicationRepository.save(application);
    }
}

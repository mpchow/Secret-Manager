package com.example.secretmanager.service;

import com.example.secretmanager.dto.AccessDTO;
import com.example.secretmanager.model.Application;
import com.example.secretmanager.model.Secret;
import com.example.secretmanager.repository.ApplicationRepository;
import com.example.secretmanager.repository.SecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Service
public class AccessService {
    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    SecretRepository secretRepository;

    public void addAllowedSecret(AccessDTO accessDTO) {
        Optional<Application> applicationData = applicationRepository.findById(accessDTO.getId());
        Optional<Secret> secretData = secretRepository.findById(accessDTO.getSecretId());

        if (applicationData.isPresent() && secretData.isPresent()) {
            Application application = applicationData.get();

            ArrayList<String> allowedSecrets;
            String newAllowedSecrets = "";

            allowedSecrets = new ArrayList<>(Arrays.asList(application.getAllowedSecrets().split(",")));
            allowedSecrets.add(accessDTO.getSecretId());

            for (String secret : allowedSecrets) {
                newAllowedSecrets += secret + ",";
            }

            application.setAllowedSecrets(newAllowedSecrets);

            applicationRepository.save(application);
        }
    }
}

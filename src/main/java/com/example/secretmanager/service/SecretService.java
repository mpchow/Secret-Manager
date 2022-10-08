package com.example.secretmanager.service;

import com.example.secretmanager.dto.SecretDTO;
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
public class SecretService {

    @Autowired
    SecretRepository secretRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    public void newSecret(SecretDTO secretDTO) {
        Secret secret = new Secret();

        secret.setId(secretDTO.getId());
        secret.setSecretVal(secretDTO.getSecretVal());

        secretRepository.save(secret);
    }

    public String retrieveSecret(String id, String secretId) throws ChangeSetPersister.NotFoundException {
        Optional<Application> applicationData = applicationRepository.findById(id);
        if (applicationData.isPresent()) {
            Application application = applicationData.get();
            ArrayList<String> allowedSecrets = new ArrayList<>(Arrays.asList(application.getAllowedSecrets().split(",")));

            if (allowedSecrets.contains(secretId)) {
                Optional<Secret> secretData = secretRepository.findById(secretId);

                if (secretData.isPresent()) {
                    return secretData.get().getSecretVal();
                }
            }
        }
        throw new ChangeSetPersister.NotFoundException();
    }
}

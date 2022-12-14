package com.example.secretmanager.service;

import com.example.secretmanager.dto.AccessDTO;
import com.example.secretmanager.model.Application;
import com.example.secretmanager.model.Secret;
import com.example.secretmanager.repository.ApplicationRepository;
import com.example.secretmanager.repository.SecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccessService {
    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    SecretRepository secretRepository;

    public void addAllowedSecret(AccessDTO accessDTO) throws ChangeSetPersister.NotFoundException {
        Optional<Application> applicationData = applicationRepository.findById(accessDTO.getId());
        Optional<Secret> secretData = secretRepository.findById(accessDTO.getSecretId());

        if(!applicationData.isPresent() || !secretData.isPresent()) {
            throw new ChangeSetPersister.NotFoundException();
        }

        Application application = applicationData.get();

        // Append the new secret
        String newAllowedSecrets = application.getAllowedSecrets();
        newAllowedSecrets += accessDTO.getSecretId() + ",";

        application.setAllowedSecrets(newAllowedSecrets);

        applicationRepository.save(application);
    }
}

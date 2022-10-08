package com.example.secretmanager.service;

import com.example.secretmanager.dto.SecretDTO;
import com.example.secretmanager.model.Secret;
import com.example.secretmanager.repository.SecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecretService {

    @Autowired
    SecretRepository secretRepository;

    public void newSecret(SecretDTO secretDTO) {
        Secret secret = new Secret();

        secret.setId(secretDTO.getId());
        secret.setSecretVal(secretDTO.getSecretVal());

        secretRepository.save(secret);
    }
}

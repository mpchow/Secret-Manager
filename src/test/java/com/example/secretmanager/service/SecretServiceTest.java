package com.example.secretmanager.service;

import com.example.secretmanager.dto.SecretDTO;
import com.example.secretmanager.model.Application;
import com.example.secretmanager.model.Secret;
import com.example.secretmanager.repository.ApplicationRepository;
import com.example.secretmanager.repository.SecretRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.crossstore.ChangeSetPersister;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
public class SecretServiceTest {

    @Autowired
    SecretService secretService;

    @MockBean
    private SecretRepository secretRepository;

    @MockBean
    private ApplicationRepository applicationRepository;

    @Test
    public void testNewSecret() {
        SecretDTO secretDTO = new SecretDTO("secretId", "secretVal");

        ArgumentCaptor<Secret> argument = ArgumentCaptor.forClass(Secret.class);

        secretService.newSecret(secretDTO);

        verify(secretRepository, times(1)).save(argument.capture());
        assertEquals("Checking secret id", "secretId", argument.getValue().getId());
        assertEquals("Checking secret val", "secretVal", argument.getValue().getSecretVal());
    }

    @Test
    public void testRetrieveSecret() {
        Application application = new Application();
        application.setId("id");
        application.setAllowedSecrets(",secretId,");
        application.setSecretToken("token");

        Secret secret = new Secret();
        secret.setId("secretId");
        secret.setSecretVal("secretVal");

        when(applicationRepository.findById("id")).thenReturn(Optional.of(application));
        when(secretRepository.findById("secretId")).thenReturn(Optional.of(secret));

        assertDoesNotThrow(() -> {
            String secretVal = secretService.retrieveSecret("id", "secretId");
            assertEquals("Checking secret value", "secretVal", secretVal);
        });
    }

    @Test
    public void testRetrieveSecretNotPermitted() {
        Application application = new Application();
        application.setId("id");
        application.setAllowedSecrets("");
        application.setSecretToken("token");

        Secret secret = new Secret();
        secret.setId("secretId");
        secret.setSecretVal("secretVal");

        when(applicationRepository.findById("id")).thenReturn(Optional.of(application));
        when(secretRepository.findById("secretId")).thenReturn(Optional.of(secret));

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            secretService.retrieveSecret("id", "secretId");
        });
    }

    @Test
    public void testRetrieveSecretDoesNotExist() {
        Application application = new Application();
        application.setId("id");
        application.setAllowedSecrets("");
        application.setSecretToken("token");

        when(applicationRepository.findById("id")).thenReturn(Optional.of(application));
        when(secretRepository.findById("secretId")).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            secretService.retrieveSecret("id", "secretId");
        });
    }

    @Test
    public void testRetrieveSecretApplicationDoesNotExist() {
        Secret secret = new Secret();
        secret.setId("secretId");
        secret.setSecretVal("secretVal");

        when(applicationRepository.findById("id")).thenReturn(Optional.empty());
        when(secretRepository.findById("secretId")).thenReturn(Optional.of(secret));

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            secretService.retrieveSecret("id", "secretId");
        });
    }
}

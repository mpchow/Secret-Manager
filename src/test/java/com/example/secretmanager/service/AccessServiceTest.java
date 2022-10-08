package com.example.secretmanager.service;

import com.example.secretmanager.dto.AccessDTO;
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
public class AccessServiceTest {

    @Autowired
    AccessService accessService;

    @MockBean
    private SecretRepository secretRepository;

    @MockBean
    private ApplicationRepository applicationRepository;

    @Test
    public void testAddAllowedSecret() {
        Secret secret = new Secret();
        secret.setSecretVal("secretVal");
        secret.setId("secretId");

        Application application = new Application();
        application.setSecretToken("secretToken");
        application.setId("id");

        when(secretRepository.findById("secretId")).thenReturn(Optional.of(secret));
        when(applicationRepository.findById("id")).thenReturn(Optional.of(application));
        ArgumentCaptor<Application> argument = ArgumentCaptor.forClass(Application.class);

        assertDoesNotThrow(() -> {
            accessService.addAllowedSecret(new AccessDTO("id", "secretId"));
        });

        verify(secretRepository, times(1)).findById("secretId");
        verify(applicationRepository, times(1)).findById("id");
        verify(applicationRepository).save(argument.capture());
        assertEquals("Checking permitted secrets", ",secretId,", argument.getValue().getAllowedSecrets());
    }

    @Test
    public void testUnknownSecret() {
        Secret secret = new Secret();
        secret.setSecretVal("secretVal");
        secret.setId("secretId");

        Application application = new Application();
        application.setSecretToken("secretToken");
        application.setId("id");

        when(secretRepository.findById("secretId")).thenReturn(Optional.empty());
        when(applicationRepository.findById("id")).thenReturn(Optional.of(application));

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            accessService.addAllowedSecret(new AccessDTO("id", "secretId"));
        });
    }

    @Test
    public void testUnknownId() {
        Secret secret = new Secret();
        secret.setSecretVal("secretVal");
        secret.setId("secretId");

        Application application = new Application();
        application.setSecretToken("secretToken");
        application.setId("id");

        when(secretRepository.findById("secretId")).thenReturn(Optional.of(secret));
        when(applicationRepository.findById("id")).thenReturn(Optional.empty());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            accessService.addAllowedSecret(new AccessDTO("id", "secretId"));
        });
    }
}

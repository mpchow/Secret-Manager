package com.example.secretmanager.service;

import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.model.Application;
import com.example.secretmanager.repository.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
public class ApplicationServiceTest {
    @Autowired
    ApplicationService applicationService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private ApplicationRepository applicationRepository;

    @Test
    public void testNewApplication() {
        ApplicationDTO applicationDTO = new ApplicationDTO("id");

        ArgumentCaptor<Application> argument = ArgumentCaptor.forClass(Application.class);

        when(applicationRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> {
            HashMap<String, String> credentials = applicationService.saveApplication(applicationDTO);
            verify(applicationRepository, times(1)).save(argument.capture());
            assertEquals("Checking save argument", Application.class, argument.getValue().getClass());
            assertNotNull("Checking returned id", credentials.get("id"));
            assertNotNull("Checking returned token", credentials.get("token"));
        });
    }

    @Test
    public void testNewApplicationWithExisting() {
        Application application = new Application();
        application.setId("id");
        application.setSecretToken("token");

        ApplicationDTO applicationDTO = new ApplicationDTO("id");

        ArgumentCaptor<Application> argument = ArgumentCaptor.forClass(Application.class);

        when(applicationRepository.findByName(any(String.class))).thenReturn(Optional.of(application));

        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.saveApplication(applicationDTO);
        });
    }

    @Test
    public void testValidCredentials() {
        Application application = new Application();
        application.setId("id");
        application.setSecretToken("encodedToken");

        when(applicationRepository.findById("id")).thenReturn(Optional.of(application));
        when(passwordEncoder.matches("token", "encodedToken")).thenReturn(true);

        assertTrue("Testing a valid credential", applicationService.validCredential("id", "token"));
    }

    @Test
    public void testValidCredentialsInvalidCred() {
        Application application = new Application();
        application.setId("id");
        application.setSecretToken("encodedToken");

        when(applicationRepository.findById("id")).thenReturn(Optional.of(application));
        when(passwordEncoder.matches("token", "encodedToken")).thenReturn(false);

        assertFalse("Testing a invalid credential", applicationService.validCredential("id", "token"));
    }

    @Test
    public void testValidCredentialsApplicationNotFound() {
        when(applicationRepository.findById("id")).thenReturn(Optional.empty());
        when(passwordEncoder.matches("token", "encodedToken")).thenReturn(true);

        assertFalse("Testing a invalid credential", applicationService.validCredential("id", "token"));
    }
}

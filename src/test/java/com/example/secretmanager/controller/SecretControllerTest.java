package com.example.secretmanager.controller;

import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.dto.SecretDTO;
import com.example.secretmanager.service.ApplicationService;
import com.example.secretmanager.service.SecretService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecretControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SecretService secretService;

    @MockBean
    private ApplicationService applicationService;

    @Test
    public void testPostSuccessfulRequest() throws Exception {
        doNothing().when(secretService).newSecret(any(SecretDTO.class));

        mvc.perform(MockMvcRequestBuilders.post("/secret")
            .content(new ObjectMapper().writeValueAsString(new SecretDTO("secretId", "secretVal")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Secret created")));
    }

    @Test
    public void testPostMalformedInput() throws Exception {
        doNothing().when(secretService).newSecret(any(SecretDTO.class));

        mvc.perform(MockMvcRequestBuilders.post("/secret")
            .content(new ObjectMapper().writeValueAsString(new SecretDTO(null, "secretVal")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(emptyString()));
    }

    @Test
    public void testGetSuccessfulRequest() throws Exception {
        when(applicationService.validCredential("user", "pass")).thenReturn(true);
        when(secretService.retrieveSecret("user","secretId")).thenReturn("secretVal");

        mvc.perform(MockMvcRequestBuilders.get("/secret/secretId")
            .with(httpBasic("user", "pass"))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("secretVal")));
    }

    @Test
    public void testGetBadCredentials() throws Exception {
        when(applicationService.validCredential("id", "token")).thenReturn(false);
        when(secretService.retrieveSecret("id","secretId")).thenReturn("secretVal");

        mvc.perform(MockMvcRequestBuilders.get("/secret/secretId")
            .with(httpBasic("id", "token"))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized())
            .andExpect(content().string(equalTo("Invalid credentials")));
    }

    @Test
    public void testGetNoCredentials() throws Exception {
        when(applicationService.validCredential("id", "token")).thenReturn(false);
        when(secretService.retrieveSecret("id","secretId")).thenReturn("secretVal");

        mvc.perform(MockMvcRequestBuilders.get("/secret/secretId")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(equalTo("Invalid credentials")));
    }

    @Test
    public void testGetBadSecretId() throws Exception {
        when(applicationService.validCredential("id", "token")).thenReturn(true);
        when(secretService.retrieveSecret("id","secretId")).thenThrow(ChangeSetPersister.NotFoundException.class);

        mvc.perform(MockMvcRequestBuilders.get("/secret/secretId")
            .with(httpBasic("id", "token"))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(equalTo("Secret not found for Application")));
    }


}

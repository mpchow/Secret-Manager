package com.example.secretmanager.integration;

import com.example.secretmanager.dto.AccessDTO;
import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.dto.SecretDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class IntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testBasicFlow() throws Exception {
        ApplicationDTO applicationDTO = new ApplicationDTO("app1", "token1");
        SecretDTO secretDTO = new SecretDTO("secret1", "secretpass1");
        AccessDTO accessDTO = new AccessDTO("app1", "secret1");

        mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(applicationDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Application created")));

        mvc.perform(MockMvcRequestBuilders.post("/secret")
            .content(new ObjectMapper().writeValueAsString(secretDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Secret created")));

        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(accessDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Access successfully updated")));

        mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO.getId())
            .with(httpBasic(applicationDTO.getId(), applicationDTO.getSecretToken()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(secretDTO.getSecretVal())));
    }

    @Test
    public void testMultipleSecrets() throws Exception {
        ApplicationDTO applicationDTO = new ApplicationDTO("app1", "token1");
        List<SecretDTO> secrets =  Arrays.asList(new SecretDTO("secret1", "secretpass1"), new SecretDTO("secret2", "secretpass2"), new SecretDTO("secret3", "secretpass3"), new SecretDTO("secret4", "secretpass4"));
        List<AccessDTO> accessRequests = Arrays.asList(new AccessDTO("app1", "secret1"), new AccessDTO("app1", "secret2"), new AccessDTO("app1", "secret3"), new AccessDTO("app1", "secret4"));

        mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(applicationDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Application created")));

        for (int i = 0; i < secrets.size(); i++) {
            SecretDTO secretDTO = secrets.get(i);
            AccessDTO accessDTO = accessRequests.get(i);

            mvc.perform(MockMvcRequestBuilders.post("/secret")
                .content(new ObjectMapper().writeValueAsString(secretDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Secret created")));

            mvc.perform(MockMvcRequestBuilders.post("/access")
                .content(new ObjectMapper().writeValueAsString(accessDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Access successfully updated")));

            mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO.getId())
                .with(httpBasic(applicationDTO.getId(), applicationDTO.getSecretToken()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(secretDTO.getSecretVal())));
        }
    }

    @Test
    public void testMultipleApplications() throws Exception {
        ApplicationDTO applicationDTO1 = new ApplicationDTO("app1", "token1");
        ApplicationDTO applicationDTO2 = new ApplicationDTO("app2", "token2");

        SecretDTO secretDTO1 = new SecretDTO("secret1", "secretpass1");
        SecretDTO secretDTO2 = new SecretDTO("secret2", "secretpass2");

        mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(applicationDTO1))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Application created")));

        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO("app1", "secret1")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(emptyString()));

        mvc.perform(MockMvcRequestBuilders.post("/secret")
            .content(new ObjectMapper().writeValueAsString(secretDTO1))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Secret created")));

        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO("app1", "secret1")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Access successfully updated")));

        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO("app2", "secret1")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(emptyString()));

        mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO1.getId())
            .with(httpBasic(applicationDTO1.getId(), applicationDTO1.getSecretToken()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(secretDTO1.getSecretVal())));

        mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(applicationDTO2))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Application created")));

        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO("app2", "secret1")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Access successfully updated")));

        mvc.perform(MockMvcRequestBuilders.post("/secret")
            .content(new ObjectMapper().writeValueAsString(secretDTO2))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Secret created")));

        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO("app1", "secret2")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Access successfully updated")));

        mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO2.getId())
            .with(httpBasic(applicationDTO1.getId(), applicationDTO1.getSecretToken()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(secretDTO2.getSecretVal())));

        mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO1.getId())
            .with(httpBasic(applicationDTO2.getId(), applicationDTO2.getSecretToken()))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo(secretDTO1.getSecretVal())));
    }
}

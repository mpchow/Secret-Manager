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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.util.AssertionErrors.assertEquals;
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
        ApplicationDTO applicationDTO = new ApplicationDTO("app");
        SecretDTO secretDTO = new SecretDTO("secret", "secretpass");
        AccessDTO accessDTO;

        MvcResult applicationResult = mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(applicationDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        HashMap<String, String> credentials = new ObjectMapper().readValue(applicationResult.getResponse().getContentAsString(), HashMap.class);
        accessDTO = new AccessDTO(credentials.get("id"), "secret");

        mvc.perform(MockMvcRequestBuilders.post("/secret")
            .content(new ObjectMapper().writeValueAsString(secretDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(accessDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        MvcResult secretResult = mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO.getId())
            .with(httpBasic(credentials.get("id"), credentials.get("token")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        HashMap<String, String> secretResponse = new ObjectMapper().readValue(secretResult.getResponse().getContentAsString(), HashMap.class);
        assertEquals("Checking secret value", "secretpass", secretResponse.get("secret"));

    }

    @Test
    public void testMultipleSecrets() throws Exception {
        ApplicationDTO applicationDTO = new ApplicationDTO("app1");
        List<SecretDTO> secrets =  Arrays.asList(new SecretDTO("secret1", "secretpass1"), new SecretDTO("secret2", "secretpass2"), new SecretDTO("secret3", "secretpass3"), new SecretDTO("secret4", "secretpass4"));

        MvcResult applicationResult = mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(applicationDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        HashMap<String, String> credentials = new ObjectMapper().readValue(applicationResult.getResponse().getContentAsString(), HashMap.class);

        for (int i = 0; i < secrets.size(); i++) {
            SecretDTO secretDTO = secrets.get(i);
            AccessDTO accessDTO = new AccessDTO(credentials.get("id"), secretDTO.getId());

            mvc.perform(MockMvcRequestBuilders.post("/secret")
                .content(new ObjectMapper().writeValueAsString(secretDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

            mvc.perform(MockMvcRequestBuilders.post("/access")
                .content(new ObjectMapper().writeValueAsString(accessDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

            MvcResult secretResult = mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO.getId())
                .with(httpBasic(credentials.get("id"), credentials.get("token")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

            HashMap<String, String> secretResponse = new ObjectMapper().readValue(secretResult.getResponse().getContentAsString(), HashMap.class);
            assertEquals("Checking secret value", secretDTO.getSecretVal(), secretResponse.get("secret"));
        }
    }

    @Test
    public void testMultipleApplications() throws Exception {
        ApplicationDTO applicationDTO1 = new ApplicationDTO("app1");
        ApplicationDTO applicationDTO2 = new ApplicationDTO("app2");

        SecretDTO secretDTO1 = new SecretDTO("secret1", "secretpass1");
        SecretDTO secretDTO2 = new SecretDTO("secret2", "secretpass2");

        MvcResult secretResultForApp1;
        MvcResult secretResultForApp2;

        HashMap<String, String> credentials1;
        HashMap<String, String> credentials2;

        HashMap<String, String> secretResponseForApp1;
        HashMap<String, String> secretResponseForApp2;

        // app1 is registered
        MvcResult applicationResult1 = mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(applicationDTO1))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        credentials1 = new ObjectMapper().readValue(applicationResult1.getResponse().getContentAsString(), HashMap.class);

        // app1 tries to retrieve a secret but it has no access to any secrets currently
        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO(credentials1.get("id"), "secret1")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        // secret1 is registered
        mvc.perform(MockMvcRequestBuilders.post("/secret")
            .content(new ObjectMapper().writeValueAsString(secretDTO1))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // app1 is given permission for secret1
        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO(credentials1.get("id"), "secret1")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // try to give app2 permission for secret2 but app2 is unregistered
        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO("app2", "secret1")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());

        // app1 successfully retrieves secret1
        secretResultForApp1 = mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO1.getId())
            .with(httpBasic(credentials1.get("id"), credentials1.get("token")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        secretResponseForApp1 = new ObjectMapper().readValue(secretResultForApp1.getResponse().getContentAsString(), HashMap.class);
        assertEquals("Checking secret value", secretDTO1.getSecretVal(), secretResponseForApp1.get("secret"));

        // app2 is registered
        MvcResult applicationResult2 = mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(applicationDTO2))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        credentials2 = new ObjectMapper().readValue(applicationResult2.getResponse().getContentAsString(), HashMap.class);

        // app2 is given permission for secret1
        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO(credentials2.get("id"), "secret1")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // secret2 is registered
        mvc.perform(MockMvcRequestBuilders.post("/secret")
            .content(new ObjectMapper().writeValueAsString(secretDTO2))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // app1 is given permission fo secret2
        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO(credentials1.get("id"), "secret2")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // app1 successfully retrieves secret2
        secretResultForApp1 = mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO2.getId())
            .with(httpBasic(credentials1.get("id"), credentials1.get("token")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        secretResponseForApp1 = new ObjectMapper().readValue(secretResultForApp1.getResponse().getContentAsString(), HashMap.class);
        assertEquals("Checking secret value", secretDTO2.getSecretVal(), secretResponseForApp1.get("secret"));

        // app2 successfully retrieves secret1
        secretResultForApp2 = mvc.perform(MockMvcRequestBuilders.get("/secret/" + secretDTO1.getId())
            .with(httpBasic(credentials2.get("id"), credentials2.get("token")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();

        secretResponseForApp2 = new ObjectMapper().readValue(secretResultForApp2.getResponse().getContentAsString(), HashMap.class);
        assertEquals("Checking secret value", secretDTO1.getSecretVal(), secretResponseForApp2.get("secret"));
    }
}

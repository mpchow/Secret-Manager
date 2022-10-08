package com.example.secretmanager.controller;

import com.example.secretmanager.dto.AccessDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AccessControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void testPostSuccessfulRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO("test-id", "test-secret")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Access successfully updated")));
    }

    @Test
    public void testPostMalformedInput() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/access")
            .content(new ObjectMapper().writeValueAsString(new AccessDTO(null, "test-secret")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(emptyString()));
    }

}

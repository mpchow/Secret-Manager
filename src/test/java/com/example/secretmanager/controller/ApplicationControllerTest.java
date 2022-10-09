package com.example.secretmanager.controller;

import com.example.secretmanager.dto.ApplicationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testPostSuccessfulRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(new ApplicationDTO("id", "test-token")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(equalTo("Application created")));
    }

    @Test
    public void testPostMalformedInput() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(new ApplicationDTO(null, "test-token")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(emptyString()));
    }
}

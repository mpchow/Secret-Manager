package com.example.secretmanager.controller;

import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.service.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ApplicationService applicationService;

    @Test
    public void testPostSuccessfulRequest() throws Exception {
        HashMap<String, String> response = new HashMap<>();
        response.put("id", "testId");
        response.put("token", "testToken");

        when(applicationService.saveApplication(any(ApplicationDTO.class))).thenReturn(response);

        mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(new ApplicationDTO("app")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(new ObjectMapper().writeValueAsString(response)));
    }

    @Test
    public void testPostMalformedInput() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/application")
            .content(new ObjectMapper().writeValueAsString(new ApplicationDTO(null)))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(emptyString()));
    }

    @Test
    public void testPostApplicationAlreadyRegistered() throws Exception {
        when(applicationService.saveApplication(any(ApplicationDTO.class))).thenThrow(IllegalArgumentException.class);

        mvc.perform(MockMvcRequestBuilders.post("/application")
                .content(new ObjectMapper().writeValueAsString(new ApplicationDTO("app")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(emptyString()));
    }
}

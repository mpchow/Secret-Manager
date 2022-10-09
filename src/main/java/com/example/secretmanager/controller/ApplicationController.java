package com.example.secretmanager.controller;

import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApplicationController {

    @Autowired
    ApplicationService applicationService;

    // Handles Post requests to register a new application to the secret manager
    @PostMapping("/application")
    public ResponseEntity<Map<String, String>> postApplication(@RequestBody ApplicationDTO applicationDTO) {
        if (applicationDTO.getName() != null) {
            try {
                Map<String, String> response = applicationService.saveApplication(applicationDTO);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (IllegalArgumentException e) {}
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
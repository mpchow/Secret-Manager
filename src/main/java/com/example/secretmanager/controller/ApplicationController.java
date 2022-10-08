package com.example.secretmanager.controller;

import com.example.secretmanager.dto.ApplicationDTO;
import com.example.secretmanager.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationController {

    @Autowired
    ApplicationService applicationService;

    @PostMapping("/application")
    public ResponseEntity<String> postApplication(@RequestBody ApplicationDTO applicationDTO) {
        applicationService.newApplication(applicationDTO);
        return new ResponseEntity<>("Application created", HttpStatus.OK);
    }
}
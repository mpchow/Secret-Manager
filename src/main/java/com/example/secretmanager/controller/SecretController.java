package com.example.secretmanager.controller;

import com.example.secretmanager.dto.SecretDTO;
import com.example.secretmanager.service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecretController {
    @Autowired
    SecretService secretService;

    @PostMapping("/secret")
    public ResponseEntity<String> postSecret(@RequestBody SecretDTO secretDTO) {
        secretService.newSecret(secretDTO);
        return new ResponseEntity<>("Secret created", HttpStatus.OK);
    }
}

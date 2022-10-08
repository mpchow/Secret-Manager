package com.example.secretmanager.controller;

import com.example.secretmanager.dto.AccessDTO;
import com.example.secretmanager.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccessController {
    @Autowired
    AccessService accessService;

    @PostMapping("/access")
    public ResponseEntity<String> postAccess(@RequestBody AccessDTO accessDTO) {
        if (accessDTO.getId() == null || accessDTO.getSecretId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        accessService.addAllowedSecret(accessDTO);
        return new ResponseEntity<>("Access successfully updated", HttpStatus.OK);
    }
}

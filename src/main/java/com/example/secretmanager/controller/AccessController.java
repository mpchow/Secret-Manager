package com.example.secretmanager.controller;

import com.example.secretmanager.dto.AccessDTO;
import com.example.secretmanager.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccessController {
    @Autowired
    AccessService accessService;

    // Handles Post requests to give an application access to a given secret
    @PostMapping("/access")
    public ResponseEntity<String> postAccess(@RequestBody AccessDTO accessDTO) {
        if (accessDTO.getId() == null || accessDTO.getSecretId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            accessService.addAllowedSecret(accessDTO);
        }
        catch (ChangeSetPersister.NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>("Access successfully updated", HttpStatus.OK);
    }
}

package com.example.secretmanager.controller;

import com.example.secretmanager.dto.AccessDTO;
import com.example.secretmanager.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccessController {
    @Autowired
    AccessService accessService;

    @PostMapping("/access")
    public ResponseEntity<String> postAccess(@RequestBody AccessDTO accessDTO) {
        accessService.addAllowedSecret(accessDTO);
        return new ResponseEntity<>("Access successfully updated", HttpStatus.OK);
    }
}

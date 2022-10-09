package com.example.secretmanager.controller;

import com.example.secretmanager.dto.SecretDTO;
import com.example.secretmanager.service.ApplicationService;
import com.example.secretmanager.service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;


@RestController
public class SecretController {
    @Autowired
    SecretService secretService;

    @Autowired
    ApplicationService applicationService;

    // Handles Get requests to retrieve a given secret. Requires Basic Auth
    @GetMapping("/secret/{id}")
    public ResponseEntity<String> getSecret(@PathVariable("id") String secretId, @RequestHeader(HttpHeaders.AUTHORIZATION) HttpHeaders headers) {
        if (headers.get("authorization") != null) {
            // Decode the Basic Auth credentials from base64
            String encodedCredentials = headers.get("authorization").get(0).split(" ")[1];
            byte[] decodedBytes = Base64.getDecoder().decode(encodedCredentials);
            String[] decodedCredentials = new String(decodedBytes).split(":");

            if (applicationService.validCredential(decodedCredentials[0], decodedCredentials[1])) {
                try {
                    String secret = secretService.retrieveSecret(decodedCredentials[0], secretId);
                    return new ResponseEntity<>(secret, HttpStatus.OK);
                } catch (ChangeSetPersister.NotFoundException e) {
                    // Responding with Not Found so there is no information given whether the requested secret exists or not
                    return new ResponseEntity<>("Secret not found for Application", HttpStatus.NOT_FOUND);
                }
            }
        }

        return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    // Handles Post requests to create a new secret in the secret manager
    @PostMapping("/secret")
    public ResponseEntity<String> postSecret(@RequestBody SecretDTO secretDTO) {
        if (secretDTO.getId() == null || secretDTO.getSecretVal() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        secretService.newSecret(secretDTO);
        return new ResponseEntity<>("Secret created", HttpStatus.OK);
    }

}

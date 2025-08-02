package com.inptrental.inptrental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/whoami")
    public ResponseEntity<Map<String, String>> whoami(Authentication authentication) {
        return ResponseEntity.ok(Map.of(
                "inemail", authentication.getName()
        ));
    }
}

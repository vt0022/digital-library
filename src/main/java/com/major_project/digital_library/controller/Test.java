package com.major_project.digital_library.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {
    @GetMapping("/home")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello is exception");
    }

    @GetMapping("/profile")
    public ResponseEntity<String> getCustomerList() {
        return ResponseEntity.ok("Deny");
    }
}


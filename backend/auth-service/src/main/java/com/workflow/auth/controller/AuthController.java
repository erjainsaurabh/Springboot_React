package com.workflow.auth.controller;

import com.workflow.auth.dto.LoginRequest;
import com.workflow.auth.dto.LoginResponse;
import com.workflow.auth.model.User;
import com.workflow.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    AuthService authService;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/init")
    public ResponseEntity<?> initializeDefaultUser() {
        User user = authService.createDefaultUser();
        Map<String, String> response = new HashMap<>();
        if (user != null) {
            response.put("message", "Default user created successfully");
            response.put("username", user.getUsername());
        } else {
            response.put("message", "Default user already exists");
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        if (token != null && token.startsWith("Bearer ")) {
            response.put("valid", "true");
        } else {
            response.put("valid", "false");
        }
        return ResponseEntity.ok(response);
    }
}

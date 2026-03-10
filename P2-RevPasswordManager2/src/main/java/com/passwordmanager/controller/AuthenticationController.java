package com.passwordmanager.controller;

import com.passwordmanager.dto.LoginDto;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Check if username or email already exists
        if(userService.getUserByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if(userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // Register user
        User createdUser = userService.registerUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginRequest) {
        try {
            // Call service-layer login
            User user = userService.loginUser(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword()
            );

            return ResponseEntity.ok(user);
        } catch (RuntimeException ex) {
            // ex.getMessage() = "User not found" or "Invalid credentials"
            return ResponseEntity.status(401).body(ex.getMessage());
        }
    }
}
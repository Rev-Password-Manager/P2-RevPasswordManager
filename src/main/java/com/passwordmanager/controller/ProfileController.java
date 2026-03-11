package com.passwordmanager.controller;

import com.passwordmanager.dto.UserDto;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
//profile controller//
//handles all related operations//

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================
    // GET USER PROFILE
    // =========================
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getProfile(@PathVariable Long userId) {
        logger.info("Fetching profile for userId: {}", userId);
        User user = userService.getUserById(userId);
        UserDto response = userService.mapToDto(user);
        logger.info("Profile fetched successfully for userId: {}", userId);
        return ResponseEntity.ok(response);
    }

    // =========================
    // UPDATE PROFILE (name, email, phone, master password)
    // =========================
    @PutMapping("/{userId}/update")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId,
                                           @RequestBody Map<String, String> payload) {

        logger.info("Updating profile for userId: {}", userId);

        String masterPassword = payload.get("masterPassword");
        String newPassword = payload.get("newPassword");

        User user = userService.getUserById(userId);

        // Verify old master password
        if (!passwordEncoder.matches(masterPassword, user.getMasterPasswordHash())) {
            logger.warn("Invalid master password provided for userId: {}", userId);
            return ResponseEntity.status(401).body("Invalid Master Password");
        }

        // Update user details
        user.setFullName(payload.get("fullName"));
        user.setEmail(payload.get("email"));
        user.setPhoneNumber(payload.get("phoneNumber"));

        // Update password if provided
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setMasterPasswordHash(passwordEncoder.encode(newPassword));
            logger.info("Master password updated for userId: {}", userId);
        }

        userService.registerUser(user);
        logger.info("Profile updated successfully for userId: {}", userId);

        return ResponseEntity.ok("Profile Updated");
    }

    // =========================
    // CHANGE MASTER PASSWORD
    // =========================
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long userId,
                                                 @RequestParam String currentPassword,
                                                 @RequestParam String newPassword) {

        logger.info("Changing master password for userId: {}", userId);

        User user = userService.getUserById(userId);

        // Check if current password matches
        if (!user.getMasterPasswordHash().equals(currentPassword)) {
            logger.warn("Incorrect current password for userId: {}", userId);
            return ResponseEntity.status(401).body("Current password is incorrect");
        }

        userService.updateMasterPassword(userId, newPassword);
        logger.info("Master password changed successfully for userId: {}", userId);

        return ResponseEntity.ok("Password changed successfully");
    }
}
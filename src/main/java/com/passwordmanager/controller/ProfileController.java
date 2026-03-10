package com.passwordmanager.controller;

import com.passwordmanager.dto.UserDto;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get user profile
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getProfile(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        UserDto response = userService.mapToDto(user);
        return ResponseEntity.ok(response);
    }

    // Update profile (name, email, phone)
    @PutMapping("/{userId}/update")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestBody Map<String,String> payload){

        String masterPassword = payload.get("masterPassword");
        String newPassword = payload.get("newPassword");

        User user = userService.getUserById(userId);

        // verify old master password
        if(!passwordEncoder.matches(masterPassword,
                user.getMasterPasswordHash())){

            return ResponseEntity.status(401)
                    .body("Invalid Master Password");
        }

        user.setFullName(payload.get("fullName"));
        user.setEmail(payload.get("email"));
        user.setPhoneNumber(payload.get("phoneNumber"));

        // update password if provided
        if(newPassword != null && !newPassword.isEmpty()){
            user.setMasterPasswordHash(
                    passwordEncoder.encode(newPassword)
            );
        }

        userService.registerUser(user);

        return ResponseEntity.ok("Profile Updated");
    }
    // Change master password
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long userId,
                                                 @RequestParam String currentPassword,
                                                 @RequestParam String newPassword) {
        User user = userService.getUserById(userId);

        if(!user.getMasterPasswordHash().equals(currentPassword)) {
            return ResponseEntity.status(401).body("Current password is incorrect");
        }

        userService.updateMasterPassword(userId, newPassword);
        return ResponseEntity.ok("Password changed successfully");
    }
}
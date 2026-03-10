package com.passwordmanager.controller;

import com.passwordmanager.entity.User;
import com.passwordmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    // Get user profile
    @GetMapping("/{userId}")
    public ResponseEntity<User> getProfile(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // Update profile (name, email, phone)
    @PutMapping("/{userId}/update")
    public ResponseEntity<User> updateProfile(@PathVariable Long userId, @RequestBody User updatedUser) {
        User user = userService.getUserById(userId);

        if(updatedUser.getFullName() != null) user.setFullName(updatedUser.getFullName());
        if(updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if(updatedUser.getPhoneNumber() != null) user.setPhoneNumber(updatedUser.getPhoneNumber());

        User saved = userService.registerUser(user); // reuse save method
        return ResponseEntity.ok(saved);
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
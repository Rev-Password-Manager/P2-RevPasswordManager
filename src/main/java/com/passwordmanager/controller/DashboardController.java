package com.passwordmanager.controller;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.PasswordVaultService;
import com.passwordmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/*
 * DashboardController
 * -------------------
 * This controller handles dashboard related APIs.
 * It returns summary information about a user's stored passwords
 * such as total passwords, weak passwords, strong passwords, and favorites.
 */

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordVaultService passwordVaultService;

    // Get dashboard summary
    @GetMapping("/{userId}")
    public ResponseEntity<?> getDashboard(@PathVariable Long userId) {

        User user = userService.getUserById(userId);
        List<PasswordEntry> allPasswords =
                passwordVaultService.getAllEntriesByUser(user);

        

        long weak = allPasswords.stream()
                .filter(e -> "WEAK".equalsIgnoreCase(e.getStrength()))
                .count();

        long strong = allPasswords.stream()
                .filter(e -> "STRONG".equalsIgnoreCase(e.getStrength()))
                .count();

        long veryStrong = allPasswords.stream()
                .filter(e -> "VERY_STRONG".equalsIgnoreCase(e.getStrength()))
                .count();
        
        long total = allPasswords.size();
        
        List<PasswordEntry> favorites = allPasswords.stream()
                .filter(e -> "Y".equalsIgnoreCase(e.getIsFavorite()))
                .toList();
        
        Map<String,Object> summary = new HashMap<>();
        summary.put("total", total);
        summary.put("weak", weak);
        summary.put("strong", strong);
        summary.put("veryStrong", veryStrong); 
        summary.put("favorites", favorites);

        return ResponseEntity.ok(summary);
    }
}
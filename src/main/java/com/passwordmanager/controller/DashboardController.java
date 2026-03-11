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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    // Logger to track method calls and events
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordVaultService passwordVaultService;

    // Get dashboard summary
    @GetMapping("/{userId}")
    public ResponseEntity<?> getDashboard(@PathVariable Long userId) {
        // Log method entry
        logger.info("Entered getDashboard method for userId: {}", userId);

        // Log fetching user
        logger.info("Fetching user details for userId: {}", userId);
        User user = userService.getUserById(userId);

        // Log fetching all password entries for user
        logger.info("Fetching all password entries for userId: {}", userId);
        List<PasswordEntry> allPasswords = passwordVaultService.getAllEntriesByUser(user);

        // Log calculating password strength counts
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

        // Log calculating favorite passwords
        List<PasswordEntry> favorites = allPasswords.stream()
                .filter(e -> "Y".equalsIgnoreCase(e.getIsFavorite()))
                .toList();

        // Log preparing summary map
        Map<String, Object> summary = new HashMap<>();
        summary.put("total", total);
        summary.put("weak", weak);
        summary.put("strong", strong);
        summary.put("veryStrong", veryStrong);
        summary.put("favorites", favorites);

        // Log method exit
        logger.info("Exiting getDashboard method for userId: {}. Total passwords: {}, weak: {}, strong: {}, veryStrong: {}, favorites: {}",
                userId, total, weak, strong, veryStrong, favorites.size());

        return ResponseEntity.ok(summary);
    }
}
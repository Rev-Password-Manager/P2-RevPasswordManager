package com.passwordmanager.controller;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.PasswordVaultService;
import com.passwordmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

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
        List<PasswordEntry> allPasswords = passwordVaultService.getAllEntriesByUser(user);

        long totalPasswords = allPasswords.size();
        long weakPasswords = allPasswords.stream()
                .filter(e -> passwordVaultService.isWeakPassword(e))
                .count();
        List<PasswordEntry> recentlyAdded = allPasswords.stream()
                .sorted((a,b) -> b.getDateAdded().compareTo(a.getDateAdded()))
                .limit(5)
                .collect(Collectors.toList());
        List<PasswordEntry> favoritePasswords = allPasswords.stream()
                .filter(e -> "Y".equalsIgnoreCase(e.getIsFavorite()))
                .collect(Collectors.toList());

        var summary = new Object() {
            public long total = totalPasswords;
            public long weak = weakPasswords;
            public List<PasswordEntry> recent = recentlyAdded;
            public List<PasswordEntry> favorites = favoritePasswords;
        };

        return ResponseEntity.ok(summary);
    }
}
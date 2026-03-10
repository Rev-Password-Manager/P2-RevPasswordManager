package com.passwordmanager.controller;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.PasswordVaultService;
import com.passwordmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passwords")
public class PasswordVaultController {

    @Autowired
    private PasswordVaultService passwordVaultService;

    @Autowired
    private UserService userService;

    // Add new password entry
    @PostMapping("/{userId}/add")
    public ResponseEntity<PasswordEntry> addPasswordEntry(@PathVariable Long userId,
                                                          @RequestBody PasswordEntry entry) {
        User user = userService.getUserById(userId);
        entry.setUser(user);
        PasswordEntry saved = passwordVaultService.addPasswordEntry(entry);
        return ResponseEntity.ok(saved);
    }

    // Update existing password entry
    @PutMapping("/{userId}/update/{entryId}")
    public ResponseEntity<PasswordEntry> updatePasswordEntry(@PathVariable Long userId,
                                                             @PathVariable Long entryId,
                                                             @RequestBody PasswordEntry updatedEntry) {
        User user = userService.getUserById(userId);
        PasswordEntry entry = passwordVaultService.getEntryByIdAndUser(entryId, user)
                .orElseThrow(() -> new RuntimeException("Password entry not found"));

        // Update fields
        entry.setAccountName(updatedEntry.getAccountName());
        entry.setWebsiteUrl(updatedEntry.getWebsiteUrl());
        entry.setUsernameEmail(updatedEntry.getUsernameEmail());
        entry.setEncryptedPassword(updatedEntry.getEncryptedPassword());
        entry.setCategory(updatedEntry.getCategory());
        entry.setNotes(updatedEntry.getNotes());
        entry.setIsFavorite(updatedEntry.getIsFavorite());

        PasswordEntry saved = passwordVaultService.updatePasswordEntry(entry);
        return ResponseEntity.ok(saved);
    }

    // Delete password entry
    @DeleteMapping("/{userId}/delete/{entryId}")
    public ResponseEntity<String> deletePasswordEntry(@PathVariable Long userId,
                                                      @PathVariable Long entryId) {
        User user = userService.getUserById(userId);
        passwordVaultService.getEntryByIdAndUser(entryId, user)
                .orElseThrow(() -> new RuntimeException("Password entry not found"));

        passwordVaultService.deletePasswordEntry(entryId);
        return ResponseEntity.ok("Password entry deleted successfully");
    }

    // Get all password entries for user
    @GetMapping("/{userId}/all")
    public ResponseEntity<List<PasswordEntry>> getAllEntries(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        List<PasswordEntry> entries = passwordVaultService.getAllEntriesByUser(user);
        return ResponseEntity.ok(entries);
    }

    // Get password entry by ID
    @GetMapping("/{userId}/{entryId}")
    public ResponseEntity<PasswordEntry> getEntryById(@PathVariable Long userId,
                                                      @PathVariable Long entryId) {
        User user = userService.getUserById(userId);
        PasswordEntry entry = passwordVaultService.getEntryByIdAndUser(entryId, user)
                .orElseThrow(() -> new RuntimeException("Password entry not found"));
        return ResponseEntity.ok(entry);
    }

    // Get password entries by category
    @GetMapping("/{userId}/category/{category}")
    public ResponseEntity<List<PasswordEntry>> getEntriesByCategory(@PathVariable Long userId,
                                                                    @PathVariable String category) {
        User user = userService.getUserById(userId);
        List<PasswordEntry> entries = passwordVaultService.getEntriesByCategory(user, category);
        return ResponseEntity.ok(entries);
    }

    // Get favorite password entries
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<PasswordEntry>> getFavoriteEntries(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        List<PasswordEntry> favorites = passwordVaultService.getFavoriteEntries(user);
        return ResponseEntity.ok(favorites);
    }
}
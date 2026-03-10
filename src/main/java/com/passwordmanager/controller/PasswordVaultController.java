package com.passwordmanager.controller;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.PasswordVaultService;
import com.passwordmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/passwords")
@CrossOrigin
public class PasswordVaultController {

    @Autowired
    private PasswordVaultService passwordVaultService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // ADD PASSWORD
    @PostMapping("/{userId}/add")
    public PasswordEntry addPassword(@PathVariable Long userId,
                                     @RequestBody PasswordEntry entry) {

        User user = userService.getUserById(userId);
        entry.setUser(user);

        if (entry.getIsFavorite() == null)
            entry.setIsFavorite("N");

        return passwordVaultService.addPasswordEntry(entry);
    }

    // GET ALL PASSWORDS
    @GetMapping("/{userId}/all")
    public List<PasswordEntry> getAll(@PathVariable Long userId) {
        return passwordVaultService.getAllEntriesByUser(
                userService.getUserById(userId));
    }

    // FAVORITES
    @GetMapping("/{userId}/favorites")
    public List<PasswordEntry> getFavorites(@PathVariable Long userId) {
        return passwordVaultService.getFavoriteEntries(
                userService.getUserById(userId));
    }

    // SORTING
    @GetMapping("/{userId}/sorted/{type}")
    public List<PasswordEntry> getSorted(@PathVariable Long userId,
                                         @PathVariable String type) {
        return passwordVaultService.getSortedEntries(
                userService.getUserById(userId), type);
    }

    // WEAK COUNT
    @GetMapping("/{userId}/weak-count")
    public long weakCount(@PathVariable Long userId) {
        return passwordVaultService.countWeakPasswords(
                userService.getUserById(userId));
    }

    // STRONG COUNT
    @GetMapping("/{userId}/strong-count")
    public long strongCount(@PathVariable Long userId) {
        return passwordVaultService.countStrongPasswords(
                userService.getUserById(userId));
    }

    // =========================
    // DELETE PASSWORD (WITH MASTER PASSWORD VERIFY)
    // =========================
    @PostMapping("/{userId}/delete/{entryId}")
    public ResponseEntity<?> deletePassword(
            @PathVariable Long userId,
            @PathVariable Long entryId,
            @RequestBody Map<String,String> body) {

        String masterPassword = body.get("masterPassword");

        User user = userService.getUserById(userId);

        // verify master password
        if(!passwordEncoder.matches(masterPassword,
                user.getMasterPasswordHash())) {

            return ResponseEntity.status(401)
                    .body("Invalid Master Password");
        }

        passwordVaultService.deletePasswordEntry(entryId);

        return ResponseEntity.ok("Deleted Successfully");
    }

    
    @PostMapping("/{userId}/verify/{entryId}")
    public ResponseEntity<?> verifyAndReturnPassword(
            @PathVariable Long userId,
            @PathVariable Long entryId,
            @RequestBody Map<String,String> body) {

        String masterPassword = body.get("masterPassword");

        User user = userService.getUserById(userId);

        // Verify master password
        if(!user.getMasterPasswordHash().equals(masterPassword)) {
            return ResponseEntity.status(401).body("Invalid master password");
        }

        PasswordEntry entry =
            passwordVaultService.getEntryByIdAndUser(entryId, user)
                    .orElseThrow(() -> new RuntimeException("Entry not found"));

        return ResponseEntity.ok(entry.getEncryptedPassword());
    }
    
    @GetMapping("/{userId}/get/{entryId}")
    public PasswordEntry getEntry(@PathVariable Long userId,
                                  @PathVariable Long entryId) {

        User user = userService.getUserById(userId);

        return passwordVaultService
                .getEntryByIdAndUser(entryId, user)
                .orElseThrow(() ->
                    new RuntimeException("Entry not found"));
    }
    @PutMapping("/{userId}/update/{entryId}")
    public ResponseEntity<?> updatePassword(
            @PathVariable Long userId,
            @PathVariable Long entryId,
            @RequestBody PasswordEntry updatedEntry) {

        User user = userService.getUserById(userId);

        PasswordEntry existing =
                passwordVaultService
                    .getEntryByIdAndUser(entryId, user)
                    .orElseThrow(() -> new RuntimeException("Entry not found"));

        existing.setAccountName(updatedEntry.getAccountName());
        existing.setWebsiteUrl(updatedEntry.getWebsiteUrl());
        existing.setUsernameEmail(updatedEntry.getUsernameEmail());
        existing.setCategory(updatedEntry.getCategory());
        existing.setIsFavorite(updatedEntry.getIsFavorite());
        existing.setEncryptedPassword(updatedEntry.getEncryptedPassword());
        existing.setDateModified(java.time.LocalDateTime.now());

        passwordVaultService.updatePasswordEntry(existing);

        return ResponseEntity.ok("Updated Successfully");
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<?> getFilteredPasswords(
            @PathVariable Long userId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String strength,
            @RequestParam(required = false) String favorite,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String search) {

        User user = userService.getUserById(userId);

        List<PasswordEntry> entries =
                passwordVaultService.getAllEntriesByUser(user);

        // 🔎 SEARCH FILTER
        if (search != null && !search.isEmpty()) {
            entries = entries.stream()
                    .filter(e ->
                            (e.getAccountName() != null &&
                             e.getAccountName().toLowerCase().contains(search.toLowerCase()))
                            ||
                            (e.getWebsiteUrl() != null &&
                             e.getWebsiteUrl().toLowerCase().contains(search.toLowerCase()))
                    )
                    .collect(Collectors.toList());
        }

        // 📂 CATEGORY FILTER
        if (category != null && !category.isEmpty()) {
            entries = entries.stream()
                    .filter(e -> category.equalsIgnoreCase(e.getCategory()))
                    .collect(Collectors.toList());
        }

        // ⭐ FAVORITE FILTER
        if ("FAV".equalsIgnoreCase(favorite)) {
            entries = entries.stream()
                    .filter(e -> "Y".equalsIgnoreCase(e.getIsFavorite()))
                    .collect(Collectors.toList());
        }

        // 🔐 STRENGTH FILTER
        if ("WEAK".equalsIgnoreCase(strength)) {
            entries = entries.stream()
                    .filter(e -> passwordVaultService
                            .isWeakPassword(e.getEncryptedPassword()))
                    .collect(Collectors.toList());
        }

        if ("STRONG".equalsIgnoreCase(strength)) {
            entries = entries.stream()
                    .filter(e -> passwordVaultService
                            .isStrongPassword(e.getEncryptedPassword()))
                    .collect(Collectors.toList());
        }

        // 🔃 SORTING
        if ("AZ".equalsIgnoreCase(sort)) {
            entries.sort((a,b) ->
                    a.getAccountName().compareToIgnoreCase(b.getAccountName()));
        }

        if ("ZA".equalsIgnoreCase(sort)) {
            entries.sort((a,b) ->
                    b.getAccountName().compareToIgnoreCase(a.getAccountName()));
        }

        if ("LATEST".equalsIgnoreCase(sort)) {
            entries.sort((a,b) ->
                    b.getDateAdded().compareTo(a.getDateAdded()));
        }

        if ("OLDEST".equalsIgnoreCase(sort)) {
            entries.sort((a,b) ->
                    a.getDateAdded().compareTo(b.getDateAdded()));
        }

        return ResponseEntity.ok(entries);
    }
    
}
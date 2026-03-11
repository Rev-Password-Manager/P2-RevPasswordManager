package com.passwordmanager.controller;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.security.EncryptionService;
import com.passwordmanager.service.PasswordVaultService;
import com.passwordmanager.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/passwords")
@CrossOrigin
public class PasswordVaultController {

    // Logger to track method calls and events
    private static final Logger logger = LoggerFactory.getLogger(PasswordVaultController.class);

    @Autowired
    private PasswordVaultService passwordVaultService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EncryptionService encryptionService;

    // =========================
    // ADD PASSWORD
    // =========================
    @PostMapping("/{userId}/add")
    public PasswordEntry addPassword(@PathVariable Long userId,
                                     @RequestBody PasswordEntry entry) {

        logger.info("Adding new password entry for userId: {}", userId);
        User user = userService.getUserById(userId);
        entry.setUser(user);

        if (entry.getIsFavorite() == null) entry.setIsFavorite("N");

        PasswordEntry savedEntry = passwordVaultService.addPasswordEntry(entry);
        logger.info("Password entry added successfully for userId: {}, entryId: {}", userId, savedEntry.getEntryId());

        return savedEntry;
    }

    // =========================
    // GET ALL PASSWORDS
    // =========================
    @GetMapping("/{userId}/all")
    public List<PasswordEntry> getAll(@PathVariable Long userId) {
        logger.info("Fetching all password entries for userId: {}", userId);
        User user = userService.getUserById(userId);
        return passwordVaultService.getAllEntriesByUser(user);
    }

    // =========================
    // GET FAVORITE PASSWORDS
    // =========================
    @GetMapping("/{userId}/favorites")
    public List<PasswordEntry> getFavorites(@PathVariable Long userId) {
        logger.info("Fetching favorite password entries for userId: {}", userId);
        User user = userService.getUserById(userId);
        return passwordVaultService.getFavoriteEntries(user);
    }

    // =========================
    // GET SORTED PASSWORDS
    // =========================
    @GetMapping("/{userId}/sorted/{type}")
    public List<PasswordEntry> getSorted(@PathVariable Long userId,
                                         @PathVariable String type) {
        logger.info("Fetching sorted password entries for userId: {}, type: {}", userId, type);
        User user = userService.getUserById(userId);
        return passwordVaultService.getSortedEntries(user, type);
    }

    // =========================
    // WEAK/STRONG COUNT
    // =========================
    @GetMapping("/{userId}/weak-count")
    public long weakCount(@PathVariable Long userId) {
        logger.info("Counting weak passwords for userId: {}", userId);
        User user = userService.getUserById(userId);
        return passwordVaultService.countWeakPasswords(user);
    }

    @GetMapping("/{userId}/strong-count")
    public long strongCount(@PathVariable Long userId) {
        logger.info("Counting strong passwords for userId: {}", userId);
        User user = userService.getUserById(userId);
        return passwordVaultService.countStrongPasswords(user);
    }

    // =========================
    // DELETE PASSWORD (MASTER PASSWORD VERIFY)
    // =========================
    @PostMapping("/{userId}/delete/{entryId}")
    public ResponseEntity<?> deletePassword(@PathVariable Long userId,
                                            @PathVariable Long entryId,
                                            @RequestBody Map<String, String> body) {

        logger.info("Deleting password entryId: {} for userId: {}", entryId, userId);
        String masterPassword = body.get("masterPassword");
        User user = userService.getUserById(userId);

        if (!passwordEncoder.matches(masterPassword, user.getMasterPasswordHash())) {
            logger.warn("Invalid master password for userId: {}", userId);
            return ResponseEntity.status(401).body("Invalid Master Password");
        }

        passwordVaultService.deletePasswordEntry(entryId);
        logger.info("Password entryId: {} deleted successfully for userId: {}", entryId, userId);

        return ResponseEntity.ok("Deleted Successfully");
    }

    // =========================
    // VERIFY AND RETURN PASSWORD
    // =========================
    @PostMapping("/{userId}/verify/{entryId}")
    public ResponseEntity<?> verifyAndReturnPassword(@PathVariable Long userId,
                                                     @PathVariable Long entryId,
                                                     @RequestBody Map<String, String> body) {
        logger.info("Verifying and returning password for entryId: {} userId: {}", entryId, userId);
        String masterPassword = body.get("masterPassword");
        User user = userService.getUserById(userId);

        if (!passwordEncoder.matches(masterPassword, user.getMasterPasswordHash())) {
            logger.warn("Invalid master password for verification, userId: {}", userId);
            return ResponseEntity.status(401).body("Invalid master password");
        }

        PasswordEntry entry = passwordVaultService.getEntryByIdAndUser(entryId, user)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        String decryptedPassword;
        try {
            decryptedPassword = encryptionService.decrypt(entry.getEncryptedPassword());
            logger.info("Password decrypted successfully for entryId: {} userId: {}", entryId, userId);
        } catch (Exception e) {
            logger.error("Decryption failed for entryId: {} userId: {}", entryId, userId, e);
            return ResponseEntity.status(500).body("Decryption failed");
        }

        return ResponseEntity.ok(decryptedPassword);
    }

    // =========================
    // GET SINGLE ENTRY
    // =========================
    @GetMapping("/{userId}/get/{entryId}")
    public PasswordEntry getEntry(@PathVariable Long userId,
                                  @PathVariable Long entryId) {
        logger.info("Fetching single password entry entryId: {} for userId: {}", entryId, userId);
        User user = userService.getUserById(userId);

        return passwordVaultService.getEntryByIdAndUser(entryId, user)
                .orElseThrow(() -> new RuntimeException("Entry not found"));
    }

    // =========================
    // UPDATE PASSWORD
    // =========================
    @PutMapping("/{userId}/update/{entryId}")
    public ResponseEntity<?> updatePassword(@PathVariable Long userId,
                                            @PathVariable Long entryId,
                                            @RequestBody PasswordEntry updatedEntry) {
        logger.info("Updating password entryId: {} for userId: {}", entryId, userId);
        User user = userService.getUserById(userId);

        PasswordEntry existing = passwordVaultService.getEntryByIdAndUser(entryId, user)
                .orElseThrow(() -> new RuntimeException("Entry not found"));

        existing.setAccountName(updatedEntry.getAccountName());
        existing.setWebsiteUrl(updatedEntry.getWebsiteUrl());
        existing.setUsernameEmail(updatedEntry.getUsernameEmail());
        existing.setCategory(updatedEntry.getCategory());
        existing.setIsFavorite(updatedEntry.getIsFavorite());
        existing.setEncryptedPassword(updatedEntry.getEncryptedPassword());
        existing.setDateModified(java.time.LocalDateTime.now());

        passwordVaultService.updatePasswordEntry(existing);
        logger.info("Password entryId: {} updated successfully for userId: {}", entryId, userId);

        return ResponseEntity.ok("Updated Successfully");
    }

    // =========================
    // FILTERED PASSWORDS
    // =========================
    @GetMapping("/{userId}")
    public ResponseEntity<?> getFilteredPasswords(@PathVariable Long userId,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(required = false) String strength,
                                                  @RequestParam(required = false) String favorite,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(required = false) String search) {

        logger.info("Fetching filtered passwords for userId: {}", userId);
        User user = userService.getUserById(userId);

        List<PasswordEntry> entries = passwordVaultService.getAllEntriesByUser(user);

        // Apply search filter
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
            logger.info("Applied search filter for userId: {}, matches found: {}", userId, entries.size());
        }

        // Apply category filter
        if (category != null && !category.isEmpty()) {
            entries = entries.stream()
                    .filter(e -> category.equalsIgnoreCase(e.getCategory()))
                    .collect(Collectors.toList());
            logger.info("Applied category filter: {} for userId: {}, matches found: {}", category, userId, entries.size());
        }

        // Apply favorite filter
        if ("FAV".equalsIgnoreCase(favorite)) {
            entries = entries.stream()
                    .filter(e -> "Y".equalsIgnoreCase(e.getIsFavorite()))
                    .collect(Collectors.toList());
            logger.info("Applied favorite filter for userId: {}, matches found: {}", userId, entries.size());
        }

        // Apply strength filter
        if ("WEAK".equalsIgnoreCase(strength)) {
            entries = entries.stream()
                    .filter(e -> passwordVaultService.isWeakPassword(e.getEncryptedPassword()))
                    .collect(Collectors.toList());
        }
        if ("STRONG".equalsIgnoreCase(strength)) {
            entries = entries.stream()
                    .filter(e -> passwordVaultService.isStrongPassword(e.getEncryptedPassword()))
                    .collect(Collectors.toList());
        }

        // Apply sorting
        if ("AZ".equalsIgnoreCase(sort)) {
            entries.sort((a, b) -> a.getAccountName().compareToIgnoreCase(b.getAccountName()));
        }
        if ("ZA".equalsIgnoreCase(sort)) {
            entries.sort((a, b) -> b.getAccountName().compareToIgnoreCase(a.getAccountName()));
        }
        if ("LATEST".equalsIgnoreCase(sort)) {
            entries.sort((a, b) -> b.getDateAdded().compareTo(a.getDateAdded()));
        }
        if ("OLDEST".equalsIgnoreCase(sort)) {
            entries.sort((a, b) -> a.getDateAdded().compareTo(b.getDateAdded()));
        }

        logger.info("Returning {} filtered password entries for userId: {}", entries.size(), userId);
        return ResponseEntity.ok(entries);
    }

    // =========================
    // EXPORT VAULT
    // =========================
    @GetMapping("/{userId}/export")
    public ResponseEntity<org.springframework.core.io.Resource> exportVault(@PathVariable Long userId) {
        logger.info("Exporting vault for userId: {}", userId);
        User user = userService.getUserById(userId);
        byte[] encryptedData = passwordVaultService.exportVault(user);
        ByteArrayResource resource = new ByteArrayResource(encryptedData);
        logger.info("Vault exported successfully for userId: {}", userId);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=vault-backup.json")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // =========================
    // IMPORT VAULT
    // =========================
    @PostMapping("/{userId}/import")
    public ResponseEntity<?> importVault(@PathVariable Long userId,
                                         @RequestParam("file") MultipartFile file) {
        logger.info("Importing vault for userId: {}", userId);
        try {
            User user = userService.getUserById(userId);
            byte[] fileBytes = file.getBytes();
            passwordVaultService.importVault(user, fileBytes);
            logger.info("Vault imported successfully for userId: {}", userId);
            return ResponseEntity.ok("Vault imported successfully");
        } catch (Exception e) {
            logger.error("Failed to import vault for userId: {}", userId, e);
            return ResponseEntity.status(500).body("Failed to import vault");
        }
    }
}
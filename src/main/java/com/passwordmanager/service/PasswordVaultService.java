package com.passwordmanager.service;

import com.passwordmanager.dto.SecurityAuditDto;
import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;

import java.util.List;
import java.util.Optional;

public interface PasswordVaultService {

    // CRUD operations
    PasswordEntry addPasswordEntry(PasswordEntry entry);
    PasswordEntry updatePasswordEntry(PasswordEntry entry);
    void deletePasswordEntry(Long entryId);

    // Fetch all entries for a user
    List<PasswordEntry> getAllEntriesByUser(User user);

    // Fetch single entry by ID + User
    Optional<PasswordEntry> getEntryByIdAndUser(Long entryId, User user);

    // Filtering methods
    List<PasswordEntry> getEntriesByCategory(User user, String category);
    List<PasswordEntry> getFavoriteEntries(User user);
    List<PasswordEntry> getSortedEntries(User user, String sortType);

    // Password strength analysis
    long countWeakPasswords(User user);
    long countStrongPasswords(User user);
    long countVeryStrongPasswords(User user);

    boolean isWeakPassword(String password);
    boolean isStrongPassword(String password);

    // Security audit report
    SecurityAuditDto getAuditReport(User user);

    // Specialized lists
    List<PasswordEntry> getWeakPasswords(User user);
    List<PasswordEntry> getReusedPasswords(User user);
    List<PasswordEntry> getOldPasswords(User user, int daysThreshold);

    // Vault export/import
    byte[] exportVault(User user);
    void importVault(User user, byte[] encryptedBytes);
}
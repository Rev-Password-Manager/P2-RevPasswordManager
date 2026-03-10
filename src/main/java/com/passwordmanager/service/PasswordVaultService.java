package com.passwordmanager.service;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import java.util.List;
import java.util.Optional;

public interface PasswordVaultService {

    PasswordEntry addPasswordEntry(PasswordEntry entry);

    PasswordEntry updatePasswordEntry(PasswordEntry entry);

    void deletePasswordEntry(Long entryId);

    List<PasswordEntry> getAllEntriesByUser(User user);

    Optional<PasswordEntry> getEntryByIdAndUser(Long entryId, User user);

    List<PasswordEntry> getEntriesByCategory(User user, String category);

    List<PasswordEntry> getFavoriteEntries(User user);
    
    boolean isWeakPassword(PasswordEntry entry);
    
}
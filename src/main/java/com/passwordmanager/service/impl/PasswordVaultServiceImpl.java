package com.passwordmanager.service.impl;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.PasswordEntryRepository;
import com.passwordmanager.service.PasswordVaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PasswordVaultServiceImpl implements PasswordVaultService {

    @Autowired
    private PasswordEntryRepository passwordEntryRepository;

    @Override
    public PasswordEntry addPasswordEntry(PasswordEntry entry) {
        return passwordEntryRepository.save(entry);
    }

    @Override
    public PasswordEntry updatePasswordEntry(PasswordEntry entry) {
        return passwordEntryRepository.save(entry);
    }

    @Override
    public void deletePasswordEntry(Long entryId) {
        passwordEntryRepository.deleteById(entryId);
    }

    @Override
    public List<PasswordEntry> getAllEntriesByUser(User user) {
        return passwordEntryRepository.findByUser(user);
    }

    @Override
    public Optional<PasswordEntry> getEntryByIdAndUser(Long entryId, User user) {
        return passwordEntryRepository.findByEntryIdAndUser(entryId, user);
    }

    @Override
    public List<PasswordEntry> getEntriesByCategory(User user, String category) {
        return passwordEntryRepository.findByUser(user).stream()
                .filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<PasswordEntry> getFavoriteEntries(User user) {
        return passwordEntryRepository.findByUser(user).stream()
                .filter(e -> "Y".equalsIgnoreCase(e.getIsFavorite()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isWeakPassword(PasswordEntry entry) {

        String password = entry.getEncryptedPassword();
  
        if (password.length() < 8) return true;
        if (!password.matches(".*[A-Z].*")) return true;
        if (!password.matches(".*[a-z].*")) return true;
        if (!password.matches(".*\\d.*")) return true;
        if (!password.matches(".*[!@#$%^&*()_+=<>?].*")) return true;
        return false;
    }
}
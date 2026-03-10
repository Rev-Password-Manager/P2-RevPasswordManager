package com.passwordmanager.service.impl;

import com.passwordmanager.dto.SecurityAuditDto;
import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.PasswordEntryRepository;
import com.passwordmanager.security.EncryptionService;
import com.passwordmanager.service.PasswordVaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PasswordVaultServiceImpl implements PasswordVaultService {

    @Autowired
    private PasswordEntryRepository repository;

    @Autowired
    private EncryptionService encryptionService;
    
    @Override
    public PasswordEntry addPasswordEntry(PasswordEntry entry) {

        entry.setDateAdded(LocalDateTime.now());
        entry.setDateModified(LocalDateTime.now());

        // Encrypt password first
        String plainPassword = entry.getEncryptedPassword();

        // Calculate strength using PLAIN password
        entry.setStrength(calculateStrength(plainPassword));

        // Now encrypt and store
        entry.setEncryptedPassword(
                encryptionService.encrypt(plainPassword)
        );

        return repository.save(entry);
    }
    
    @Override
    public PasswordEntry updatePasswordEntry(PasswordEntry entry) {

        entry.setDateModified(LocalDateTime.now());

        String plainPassword = entry.getEncryptedPassword();

        entry.setStrength(calculateStrength(plainPassword));

        entry.setEncryptedPassword(
                encryptionService.encrypt(plainPassword)
        );

        return repository.save(entry);
    }
    
    @Override
    public void deletePasswordEntry(Long entryId) {
        repository.deleteById(entryId);
    }

    @Override
    public List<PasswordEntry> getAllEntriesByUser(User user) {
    	List<PasswordEntry> entries = repository.findByUser(user);

    	for (PasswordEntry entry : entries) {
    	    entry.setEncryptedPassword(
    	            encryptionService.decrypt(entry.getEncryptedPassword())
    	    );
    	}

    	return entries;
    }

    @Override
    public Optional<PasswordEntry> getEntryByIdAndUser(Long entryId, User user) {
        return repository.findByEntryIdAndUser(entryId, user);
    }

    @Override
    public List<PasswordEntry> getEntriesByCategory(User user, String category) {
        return repository.findByUserAndCategory(user, category);
    }

    @Override
    public List<PasswordEntry> getFavoriteEntries(User user) {
        return repository.findByUserAndIsFavorite(user, "Y");
    }

    @Override
    public List<PasswordEntry> getSortedEntries(User user, String sortType) {
        switch (sortType) {
            case "AZ":
                return repository.findByUserOrderByAccountNameAsc(user);
            case "ZA":
                return repository.findByUserOrderByAccountNameDesc(user);
            case "LATEST":
                return repository.findByUserOrderByDateAddedDesc(user);
            case "MODIFIED":
                return repository.findByUserOrderByDateModifiedDesc(user);
            default:
                return repository.findByUser(user);
        }
    }

    @Override
    public long countWeakPasswords(User user) {
        return repository.findByUserAndStrength(user, "WEAK").size();
    }

    @Override
    public long countStrongPasswords(User user) {
        return repository.findByUserAndStrength(user, "STRONG").size()
             + repository.findByUserAndStrength(user, "VERY_STRONG").size();
    }

    @Override
    public long countVeryStrongPasswords(User user) {
        return repository.findByUserAndStrength(user, "VERY_STRONG").size();
    }
    
    public List<PasswordEntry> getEntriesByStrength(User user, String strength) {
        return repository.findByUserAndStrength(user, strength);
    }
    
    
    public boolean isWeakPassword(String password) {

        if (password == null) return true;

        if (password.length() < 8) return true;
        if (!password.matches(".*[A-Z].*")) return true;
        if (!password.matches(".*[a-z].*")) return true;
        if (!password.matches(".*\\d.*")) return true;
        if (!password.matches(".*[!@#$%^&*()_+=<>?].*")) return true;

        return false;
    }
    
    @Override
    public boolean isStrongPassword(String password) {

        if (password == null) return false;

        return password.length() >= 8 && password.length() < 12 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*\\d.*") &&
               password.matches(".*[!@#$%^&*()_+=<>?].*");
    }
    
    public boolean isVeryStrongPassword(String password) {
        if (password == null) return false;

        return password.length() >= 12 &&
               password.matches(".*[A-Z].*") &&
               password.matches(".*[a-z].*") &&
               password.matches(".*\\d.*") &&
               password.matches(".*[!@#$%^&*()_+=<>?].*");
    }
    
    public String calculateStrength(String password) {

        if (password == null) return "WEAK";

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+=<>?].*");

        if (password.length() >= 12 && hasUpper && hasLower && hasDigit && hasSpecial) {
            return "VERY_STRONG";
        }

        if (password.length() >= 8 && hasUpper && hasLower && hasDigit && hasSpecial) {
            return "STRONG";
        }

        return "WEAK";
    }
    
    @Override
    public SecurityAuditDto getAuditReport(User user) {

        List<PasswordEntry> entries = repository.findByUser(user);

        long weak = entries.stream()
                .filter(e -> "WEAK".equalsIgnoreCase(e.getStrength()))
                .count();

        long strong = entries.stream()
                .filter(e -> "STRONG".equalsIgnoreCase(e.getStrength()))
                .count();

        return new SecurityAuditDto(
                entries.size(),
                weak,
                strong
        );
    }
    
    @Override 
    public List<PasswordEntry> getWeakPasswords(User user) { 
    	return repository.findByUserAndStrength(user, "WEAK"); 
   	} 
   
    @Override 
    public List<PasswordEntry> getReusedPasswords(User user) { 
    	List<PasswordEntry> entries = repository.findByUser(user); 
    	Map<String, List<PasswordEntry>> grouped = entries.stream().collect(Collectors.groupingBy(PasswordEntry::getEncryptedPassword)); 
    	return grouped.values().stream() .filter(list -> list.size() > 1) .flatMap(List::stream) .collect(Collectors.toList()); 
    } 
    
    @Override 
    public List<PasswordEntry> getOldPasswords(User user, int daysThreshold) { 
    	LocalDateTime cutoff = LocalDateTime.now().minusDays(daysThreshold); 
    	return repository.findByUser(user).stream() .filter(e -> e.getDateModified() != null && e.getDateModified().isBefore(cutoff)) .collect(Collectors.toList()); 
    }
}
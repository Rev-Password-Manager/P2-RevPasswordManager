package com.passwordmanager.service;

import com.passwordmanager.dto.SecurityAuditDto;
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

    List<PasswordEntry> getSortedEntries(User user, String sortType);

    long countWeakPasswords(User user);

    long countStrongPasswords(User user);

	long countVeryStrongPasswords(User user);
	
    boolean isWeakPassword(String password);
    
    boolean isStrongPassword(String password);

	SecurityAuditDto getAuditReport(User user);
	
	List<PasswordEntry> getWeakPasswords(User user); 
	
	List<PasswordEntry> getReusedPasswords(User user); 
	
	List<PasswordEntry> getOldPasswords(User user, int daysThreshold);

}
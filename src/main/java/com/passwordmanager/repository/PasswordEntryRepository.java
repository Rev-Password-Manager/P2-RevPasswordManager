package com.passwordmanager.repository;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repository for managing PasswordEntry entities
public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {

    // Get all password entries for a user
    List<PasswordEntry> findByUser(User user);

    // Find a specific entry by entryId and user
    Optional<PasswordEntry> findByEntryIdAndUser(Long entryId, User user);

    // Get entries for a user in a specific category
    List<PasswordEntry> findByUserAndCategory(User user, String category);

    // Get favorite entries for a user
    List<PasswordEntry> findByUserAndIsFavorite(User user, String isFavorite);

    // Get all entries for a user ordered by account name ascending
    List<PasswordEntry> findByUserOrderByAccountNameAsc(User user);

    // Get all entries for a user ordered by account name descending
    List<PasswordEntry> findByUserOrderByAccountNameDesc(User user);

    // Get entries ordered by date added descending
    List<PasswordEntry> findByUserOrderByDateAddedDesc(User user);

    // Get entries ordered by date modified descending
    List<PasswordEntry> findByUserOrderByDateModifiedDesc(User user);
    
    // Get entries filtered by strength (WEAK, STRONG, etc.)
    List<PasswordEntry> findByUserAndStrength(User user, String strength);

    // Find entry by user, account name, and username/email combination
    Optional<PasswordEntry> findByUserAndAccountNameAndUsernameEmail(
            User user, String accountName, String usernameEmail);
}
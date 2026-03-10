package com.passwordmanager.repository;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PasswordEntryRepository extends JpaRepository<PasswordEntry, Long> {

    List<PasswordEntry> findByUser(User user);

    Optional<PasswordEntry> findByEntryIdAndUser(Long entryId, User user);

    List<PasswordEntry> findByUserAndCategory(User user, String category);

    List<PasswordEntry> findByUserAndIsFavorite(User user, String isFavorite);

    List<PasswordEntry> findByUserOrderByAccountNameAsc(User user);

    List<PasswordEntry> findByUserOrderByAccountNameDesc(User user);

    List<PasswordEntry> findByUserOrderByDateAddedDesc(User user);

    List<PasswordEntry> findByUserOrderByDateModifiedDesc(User user);
    
    List<PasswordEntry> findByUserAndStrength(User user, String strength);
}
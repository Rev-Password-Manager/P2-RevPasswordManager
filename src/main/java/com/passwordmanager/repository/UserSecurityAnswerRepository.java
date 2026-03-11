package com.passwordmanager.repository;

import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository for managing UserSecurityAnswer entities
public interface UserSecurityAnswerRepository extends JpaRepository<UserSecurityAnswer, Long> {

    // Option 1: Get all answers by User object
    List<UserSecurityAnswer> findByUser(User user);

    // Option 2: Get all answers by userId directly
    List<UserSecurityAnswer> findByUser_UserId(Long userId);

    // Delete all security answers for a specific userId
    void deleteByUser_UserId(Long userId);
}
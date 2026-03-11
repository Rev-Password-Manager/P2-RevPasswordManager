package com.passwordmanager.repository;

import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Repository for managing VerificationCode entities in the database
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    // Get all verification codes for a specific user
    List<VerificationCode> findByUser(User user);

    // Find a verification code by code string, user, and whether it is used
    Optional<VerificationCode> findByCodeAndUserAndUsed(String code, User user, String used);
}
package com.passwordmanager.repository;

import com.passwordmanager.entity.SecurityQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository for managing SecurityQuestion entities
public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {
    // Basic CRUD methods provided by JpaRepository
}
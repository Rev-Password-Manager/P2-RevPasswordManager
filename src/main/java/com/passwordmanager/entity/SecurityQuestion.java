package com.passwordmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

// ======================
// SecurityQuestion Entity
// Stores predefined security questions
// ======================
@Entity
@Data
@Table(name = "SECURITY_QUESTIONS")
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId; // primary key

    private String questionText; // actual question text
}
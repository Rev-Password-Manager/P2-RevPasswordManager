package com.passwordmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "SECURITY_QUESTIONS")
public class SecurityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    private String questionText;

}
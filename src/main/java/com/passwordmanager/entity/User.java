package com.passwordmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "USERS")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "master_password_hash", nullable = false)
    private String masterPasswordHash;

    @Column(name = "two_factor_enabled", length = 1, columnDefinition = "CHAR(1)")
    private String twoFactorEnabled = "N";

    @Column(name = "two_factor_secret")
    private String twoFactorSecret;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PasswordEntry> passwordEntries;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserSecurityAnswer> securityAnswers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VerificationCode> verificationCodes;

    public User() {}
}
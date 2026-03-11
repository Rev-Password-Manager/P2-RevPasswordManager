package com.passwordmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

//======================
//User Entity
//Stores user information, master password, 2FA info, and relationships
//======================
@Entity
@Table(name = "USERS")
@Data
public class User {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "user_id")
 private Long userId; // primary key

 @Column(unique = true, nullable = false)
 private String username; // login username

 @Column(unique = true, nullable = false)
 private String email; // login email

 @Column(name = "full_name")
 private String fullName; // user's full name

 @Column(name = "phone_number")
 private String phoneNumber; // user's phone number

 @Column(name = "master_password_hash", nullable = false)
 private String masterPasswordHash; // encrypted master password

 @Column(name = "two_factor_enabled", length = 1, columnDefinition = "CHAR(1)")
 private String twoFactorEnabled = "N"; // "Y" if 2FA enabled

 @Column(name = "two_factor_secret")
 private String twoFactorSecret; // secret for 2FA

 @Column(name = "created_at")
 private LocalDateTime createdAt; // creation timestamp

 @Column(name = "updated_at")
 private LocalDateTime updatedAt; // last update timestamp

 // Relationships
 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
 private List<PasswordEntry> passwordEntries; // user's saved passwords

 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
 private List<UserSecurityAnswer> securityAnswers; // user's security answers

 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
 private List<VerificationCode> verificationCodes; // user's verification codes

 public User() {}
}
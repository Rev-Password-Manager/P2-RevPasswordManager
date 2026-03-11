package com.passwordmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "PASSWORD_ENTRIES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PasswordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId; // primary key

    private String accountName;   // Name of the account, e.g., Gmail
    private String websiteUrl;    // Website URL
    private String usernameEmail; // Username or email for this account

    @Column(name = "encrypted_password", length = 500)
    private String encryptedPassword; // AES-encrypted password

    private String category; // Category/tag for this password (e.g., Social, Work)
    
    @Lob
    @Column(name = "notes")
    private String notes; // Optional notes for the entry

    @Column(name = "is_favorite", length = 1)
    private String isFavorite = "N"; // "Y" if marked favorite, "N" otherwise

    @Column(length = 20)
    private String strength; // Password strength: WEAK, STRONG, VERY_STRONG

    private LocalDateTime dateAdded;    // When the password was added
    private LocalDateTime dateModified; // Last modified timestamp

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore // Avoid circular serialization with User
    private User user; // Owning user

    // =========================
    // Optional getters/setters for custom logic
    // =========================
    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }
}
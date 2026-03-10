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
    private Long entryId;

    private String accountName;
    private String websiteUrl;
    private String usernameEmail;

    @Column(name = "encrypted_password", length = 500) 
    private String encryptedPassword;

    private String category;
    
    @Lob
    @Column(name = "notes")
    private String notes;
    @Column(name = "is_favorite", length = 1)
    private String isFavorite = "N";

    @Column(length = 20)
    private String strength; // WEAK, STRONG, VERY_STRONG
    
    public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	private LocalDateTime dateAdded;
    private LocalDateTime dateModified;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore 
    private User user;
}
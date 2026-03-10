package com.passwordmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordEntryDto {

    private Long userId;
    private String accountName;
    private String websiteUrl;
    private String usernameEmail;
    private String password;
    private String category;
    private String notes;
    private String isFavorite;

    
}
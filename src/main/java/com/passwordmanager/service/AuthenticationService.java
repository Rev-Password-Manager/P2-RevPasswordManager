package com.passwordmanager.service;

import com.passwordmanager.dto.LoginDto;
import com.passwordmanager.dto.RegistrationDto;

public interface AuthenticationService {

    // Register user from DTO
    void registerUser(RegistrationDto dto);

    // Login user from DTO
    void loginUser(LoginDto dto);
}
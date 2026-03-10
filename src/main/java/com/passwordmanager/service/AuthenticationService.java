package com.passwordmanager.service;

import com.passwordmanager.dto.LoginDto;
import com.passwordmanager.dto.RegistrationDto;

public interface AuthenticationService {

    void registerUser(RegistrationDto dto);

    void loginUser(LoginDto dto);
}
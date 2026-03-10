package com.passwordmanager.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.passwordmanager.dto.LoginDto;
import com.passwordmanager.dto.RegistrationDto;
import com.passwordmanager.entity.User;
import com.passwordmanager.exception.InvalidCredentialsException;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.service.AuthenticationService;

@Service
public class AuthenticationServiceImpl 
        implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public void registerUser(RegistrationDto dto) {

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setMasterPasswordHash(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
    }

    @Override
    public void loginUser(LoginDto dto) {

    	User user = userRepository.findByUsername(dto.getUsernameOrEmail())
    	        .orElse(userRepository.findByEmail(dto.getUsernameOrEmail())
    	                .orElseThrow(() -> new RuntimeException("User not found")));

    	if (!passwordEncoder.matches(dto.getPassword(), user.getMasterPasswordHash())) {
    	    throw new InvalidCredentialsException("Invalid master password");
    	}
    }
}
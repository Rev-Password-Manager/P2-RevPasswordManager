package com.passwordmanager.service.impl;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // added for logging
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

    // Logger to track method calls and execution
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void registerUser(RegistrationDto dto) {
        logger.info("Entering registerUser with username: {}, email: {}", dto.getUsername(), dto.getEmail());

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setMasterPasswordHash(passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);
        logger.info("User registered successfully with username: {}, email: {}", dto.getUsername(), dto.getEmail());
    }

    @Override
    public void loginUser(LoginDto dto) {
        logger.info("Entering loginUser with usernameOrEmail: {}", dto.getUsernameOrEmail());

        User user;
        try {
            user = userRepository.findByUsername(dto.getUsernameOrEmail())
                    .orElse(userRepository.findByEmail(dto.getUsernameOrEmail())
                            .orElseThrow(() -> new RuntimeException("User not found")));
            logger.info("User found with id: {}", user.getUserId());
        } catch (RuntimeException e) {
            logger.error("User not found for usernameOrEmail: {}", dto.getUsernameOrEmail(), e);
            throw e;
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getMasterPasswordHash())) {
            logger.warn("Invalid master password attempt for userId: {}", user.getUserId());
            throw new InvalidCredentialsException("Invalid master password");
        }

        logger.info("User logged in successfully with userId: {}", user.getUserId());
    }
}
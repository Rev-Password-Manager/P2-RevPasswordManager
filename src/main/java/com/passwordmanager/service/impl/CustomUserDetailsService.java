package com.passwordmanager.service.impl;

import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Logger to track method calls and flow
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    // =========================
    // LOAD USER BY USERNAME OR EMAIL
    // =========================
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Entered loadUserByUsername with input: {}", username);

        // Try username first, then email
        User user = userRepository.findByUsername(username)
            .orElseGet(() -> userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username/email: {}", username);
                    return new UsernameNotFoundException("User not found");
                }));

        logger.info("User found: {} (id: {})", user.getUsername(), user.getUserId());

        // Build UserDetails object
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getMasterPasswordHash()) // hashed password from DB
            .authorities("USER") // default authority
            .build();

        logger.info("UserDetails object created for userId: {}", user.getUserId());
        return userDetails;
    }
}
package com.passwordmanager.service;

import com.passwordmanager.dto.UserDto;
import com.passwordmanager.entity.User;

import java.util.Optional;

public interface UserService {

    // Check if a password meets policy requirements
    boolean isValidPassword(String password);

    // Convert User entity to DTO
    UserDto mapToDto(User user);

    // Register or update a user
    User registerUser(User user);

    // Fetch user by id
    User getUserById(Long userId);

    // Fetch user by username
    Optional<User> getUserByUsername(String username);

   // Fetch user by email
    Optional<User> getUserByEmail(String email);

    // Update master password for user
    User updateMasterPassword(Long userId, String newPasswordHash);

    // Login user by username/email + password
    User loginUser(String usernameOrEmail, String password);

    // Validate a new password against policy
    boolean isPasswordValid(String newPassword);

    // NEW: Update user entity (used for profile updates)
    User updateUser(User user);
}
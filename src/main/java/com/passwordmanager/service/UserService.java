package com.passwordmanager.service;

import com.passwordmanager.dto.UserDto;
import com.passwordmanager.entity.User;
import java.util.Optional;

public interface UserService {

    boolean isValidPassword(String password);

    UserDto mapToDto(User user);

    User registerUser(User user);

    User getUserById(Long userId);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    User updateMasterPassword(Long userId, String newPasswordHash);

    User loginUser(String usernameOrEmail, String password);

    boolean isPasswordValid(String newPassword);

    // ⭐ ADD THIS METHOD
    User updateUser(User user);
}
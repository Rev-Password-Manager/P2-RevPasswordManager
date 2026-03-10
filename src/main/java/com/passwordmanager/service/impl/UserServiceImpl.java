package com.passwordmanager.service.impl;

import com.passwordmanager.dto.UserDto;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    @Override
    public UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateMasterPassword(Long userId, String newPasswordHash) {
        User user = getUserById(userId);
        user.setMasterPasswordHash(newPasswordHash);
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String usernameOrEmail, String password) {

        Optional<User> userOptional = userRepository.findByUsername(usernameOrEmail);

        if(userOptional.isEmpty()){
            userOptional = userRepository.findByEmail(usernameOrEmail);
        }

        if(userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        if(!passwordEncoder.matches(password, user.getMasterPasswordHash())){
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    @Override
    public boolean isPasswordValid(String newPassword) {
        return newPassword != null && newPassword.length() >= 6;
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }
}
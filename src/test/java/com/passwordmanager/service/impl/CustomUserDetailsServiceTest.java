package com.passwordmanager.service.impl;

import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User createUser() {
        User user = new User();
        user.setUsername("shiva");
        user.setEmail("shiva@test.com");
        user.setMasterPasswordHash("encodedPassword");
        return user;
    }

    @Test
    void testLoadUserByUsername_success_withUsername() {

        User user = createUser();

        when(userRepository.findByUsername("shiva"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("shiva");

        assertEquals("shiva", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
    }

    @Test
    void testLoadUserByUsername_success_withEmail() {

        User user = createUser();

        when(userRepository.findByUsername("shiva@test.com"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("shiva@test.com"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("shiva@test.com");

        assertEquals("shiva", userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_userNotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown"));
    }
}
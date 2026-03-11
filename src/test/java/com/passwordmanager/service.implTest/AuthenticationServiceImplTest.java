package com.passwordmanager.service.impl;

import com.passwordmanager.dto.LoginDto;
import com.passwordmanager.dto.RegistrationDto;
import com.passwordmanager.entity.User;
import com.passwordmanager.exception.InvalidCredentialsException;
import com.passwordmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User createUser(){
        User user = new User();
        user.setUsername("shiva");
        user.setEmail("shiva@gmail.com");
        user.setMasterPasswordHash("encodedPassword");
        return user;
    }

    @Test
    void testRegisterUser(){

        RegistrationDto dto = new RegistrationDto();
        dto.setUsername("shiva");
        dto.setEmail("shiva@gmail.com");
        dto.setPassword("123456");

        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");

        authenticationService.registerUser(dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLoginUserSuccessWithUsername(){

        LoginDto dto = new LoginDto();
        dto.setUsernameOrEmail("shiva");
        dto.setPassword("123456");

        User user = createUser();

        when(userRepository.findByUsername("shiva"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("shiva"))
                .thenReturn(Optional.of(user));   // IMPORTANT

        when(passwordEncoder.matches("123456","encodedPassword"))
                .thenReturn(true);

        assertDoesNotThrow(() -> authenticationService.loginUser(dto));
    }

    @Test
    void testLoginUserSuccessWithEmail(){

        LoginDto dto = new LoginDto();
        dto.setUsernameOrEmail("shiva@gmail.com");
        dto.setPassword("123456");

        User user = createUser();

        when(userRepository.findByUsername("shiva@gmail.com"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("shiva@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456","encodedPassword"))
                .thenReturn(true);

        assertDoesNotThrow(() -> authenticationService.loginUser(dto));
    }

    @Test
    void testLoginUserInvalidPassword(){

        LoginDto dto = new LoginDto();
        dto.setUsernameOrEmail("shiva");
        dto.setPassword("wrong");

        User user = createUser();

        when(userRepository.findByUsername("shiva"))
                .thenReturn(Optional.of(user));

        when(userRepository.findByEmail("shiva"))
                .thenReturn(Optional.of(user));  // IMPORTANT

        when(passwordEncoder.matches("wrong","encodedPassword"))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authenticationService.loginUser(dto));
    }

    @Test
    void testLoginUserUserNotFound(){

        LoginDto dto = new LoginDto();
        dto.setUsernameOrEmail("unknown");
        dto.setPassword("123");

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> authenticationService.loginUser(dto));
    }
}
package com.passwordmanager.service.impl;

import com.passwordmanager.dto.UserDto;
import com.passwordmanager.entity.User;
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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User createUser() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("shiva");
        user.setEmail("shiva@test.com");
        user.setMasterPasswordHash("hashedPassword");
        return user;
    }

    @Test
    void testIsValidPassword() {
        assertTrue(userService.isValidPassword("123456"));
        assertFalse(userService.isValidPassword("123"));
    }

    @Test
    void testMapToDto() {

        User user = createUser();

        UserDto dto = userService.mapToDto(user);

        assertEquals(user.getUserId(), dto.getUserId());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getEmail(), dto.getEmail());
    }

    @Test
    void testRegisterUser() {

        User user = createUser();

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.registerUser(user);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void testGetUserById() {

        User user = createUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals("shiva", result.getUsername());
    }

    @Test
    void testGetUserByUsername() {

        User user = createUser();

        when(userRepository.findByUsername("shiva"))
                .thenReturn(Optional.of(user));

        Optional<User> result =
                userService.getUserByUsername("shiva");

        assertTrue(result.isPresent());
    }

    @Test
    void testGetUserByEmail() {

        User user = createUser();

        when(userRepository.findByEmail("shiva@test.com"))
                .thenReturn(Optional.of(user));

        Optional<User> result =
                userService.getUserByEmail("shiva@test.com");

        assertTrue(result.isPresent());
    }

    @Test
    void testUpdateMasterPassword() {

        User user = createUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        User result =
                userService.updateMasterPassword(1L, "newHash");

        assertEquals("newHash", result.getMasterPasswordHash());
    }

    @Test
    void testLoginUserSuccess() {

        User user = createUser();

        when(userRepository.findByUsername("shiva"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "hashedPassword"))
                .thenReturn(true);

        User result =
                userService.loginUser("shiva", "123456");

        assertNotNull(result);
    }

    @Test
    void testLoginUserInvalidPassword() {

        User user = createUser();

        when(userRepository.findByUsername("shiva"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "hashedPassword"))
                .thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> userService.loginUser("shiva", "wrong"));
    }

    @Test
    void testLoginUserUserNotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> userService.loginUser("unknown", "123"));
    }

    @Test
    void testIsPasswordValid() {

        assertTrue(userService.isPasswordValid("abcdef"));
        assertFalse(userService.isPasswordValid("abc"));
    }

    @Test
    void testUpdateUser() {

        User user = createUser();

        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertNotNull(result);
        verify(userRepository).save(user);
    }
}
package com.passwordmanager.repository;

import com.passwordmanager.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User user = new User();
        user.setFullName("Shiva Kumar");
        user.setUsername("shiva");
        user.setEmail("shiva@test.com");
        user.setPhoneNumber("9999999999");
        user.setMasterPasswordHash("encryptedPass");
        user.setTwoFactorEnabled("N");
        user.setTwoFactorSecret(null);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Test
    void testSaveUser() {

        User user = new User();
        user.setFullName("Test User");
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        user.setPhoneNumber("8888888888");
        user.setMasterPasswordHash("123456");
        user.setTwoFactorEnabled("N");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        assertNotNull(saved.getUserId());
    }

    @Test
    void testFindByUsername() {

        User saved = createUser();

        Optional<User> result =
                userRepository.findByUsername("shiva");

        assertTrue(result.isPresent());
        assertEquals(saved.getEmail(), result.get().getEmail());
    }

    @Test
    void testFindByEmail() {

        User saved = createUser();

        Optional<User> result =
                userRepository.findByEmail("shiva@test.com");

        assertTrue(result.isPresent());
        assertEquals(saved.getUsername(), result.get().getUsername());
    }

    @Test
    void testExistsByUsername() {

        createUser();

        boolean exists = userRepository.existsByUsername("shiva");

        assertTrue(exists);
    }

    @Test
    void testExistsByEmail() {

        createUser();

        boolean exists = userRepository.existsByEmail("shiva@test.com");

        assertTrue(exists);
    }

    @Test
    void testDeleteUser() {

        User saved = createUser();

        userRepository.deleteById(saved.getUserId());

        Optional<User> result =
                userRepository.findById(saved.getUserId());

        assertTrue(result.isEmpty());
    }
}
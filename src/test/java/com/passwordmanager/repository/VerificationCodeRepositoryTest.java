package com.passwordmanager.repository;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VerificationCodeRepositoryTest {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User user = new User();
        user.setFullName("Test User");
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPhoneNumber("9999999999");
        user.setMasterPasswordHash("123456");
        user.setTwoFactorEnabled("N");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private VerificationCode createCode(User user) {
        VerificationCode code = new VerificationCode();
        code.setUser(user);
        code.setCode("123456");
        code.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        code.setUsed("N");
        return verificationCodeRepository.save(code);
    }

    @Test
    void testFindByUser() {

        User user = createUser();
        createCode(user);

        List<VerificationCode> result =
                verificationCodeRepository.findByUser(user);

        assertFalse(result.isEmpty());
    }

    @Test
    void testFindByCodeAndUserAndUsed() {

        User user = createUser();
        createCode(user);

        Optional<VerificationCode> result =
                verificationCodeRepository
                        .findByCodeAndUserAndUsed("123456", user, "N");

        assertTrue(result.isPresent());
    }

    @Test
    void testSaveVerificationCode() {

        User user = createUser();

        VerificationCode code = new VerificationCode();
        code.setUser(user);
        code.setCode("654321");
        code.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        code.setUsed("N");

        VerificationCode saved =
                verificationCodeRepository.save(code);

        assertNotNull(saved.getCodeId());
    }

    @Test
    void testDeleteVerificationCode() {

        User user = createUser();
        VerificationCode code = createCode(user);

        verificationCodeRepository.deleteById(code.getCodeId());

        Optional<VerificationCode> result =
                verificationCodeRepository.findById(code.getCodeId());

        assertTrue(result.isEmpty());
    }
}
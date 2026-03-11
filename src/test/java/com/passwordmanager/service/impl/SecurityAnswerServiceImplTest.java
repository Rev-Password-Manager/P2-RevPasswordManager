package com.passwordmanager.service.impl;

import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityAnswerServiceImplTest {

    @Mock
    private UserSecurityAnswerRepository userSecurityAnswerRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private SecurityAnswerServiceImpl securityAnswerService;

    private UserSecurityAnswer createAnswer(String hash) {
        UserSecurityAnswer ans = new UserSecurityAnswer();
        ans.setAnswerHash(hash);
        return ans;
    }

    @Test
    void testValidateAnswers_success() {

        List<UserSecurityAnswer> dbAnswers = List.of(
                createAnswer("hash1"),
                createAnswer("hash2")
        );

        when(userSecurityAnswerRepository.findByUser_UserId(1L))
                .thenReturn(dbAnswers);

        when(passwordEncoder.matches("dog", "hash1")).thenReturn(true);
        when(passwordEncoder.matches("cat", "hash2")).thenReturn(true);

        boolean result = securityAnswerService.validateAnswers(
                1L,
                List.of("dog", "cat")
        );

        assertTrue(result);
    }

    @Test
    void testValidateAnswers_wrongAnswer() {

        List<UserSecurityAnswer> dbAnswers = List.of(
                createAnswer("hash1")
        );

        when(userSecurityAnswerRepository.findByUser_UserId(1L))
                .thenReturn(dbAnswers);

        when(passwordEncoder.matches("wrong", "hash1"))
                .thenReturn(false);

        boolean result = securityAnswerService.validateAnswers(
                1L,
                List.of("wrong")
        );

        assertFalse(result);
    }

    @Test
    void testValidateAnswers_sizeMismatch() {

        List<UserSecurityAnswer> dbAnswers = List.of(
                createAnswer("hash1"),
                createAnswer("hash2")
        );

        when(userSecurityAnswerRepository.findByUser_UserId(1L))
                .thenReturn(dbAnswers);

        boolean result = securityAnswerService.validateAnswers(
                1L,
                List.of("dog")
        );

        assertFalse(result);
    }

    @Test
    void testValidateAnswers_noAnswersInDB() {

        when(userSecurityAnswerRepository.findByUser_UserId(1L))
                .thenReturn(List.of());

        boolean result = securityAnswerService.validateAnswers(
                1L,
                List.of("dog", "cat")
        );

        assertFalse(result);
    }
}
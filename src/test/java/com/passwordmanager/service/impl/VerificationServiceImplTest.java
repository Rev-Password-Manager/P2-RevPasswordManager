package com.passwordmanager.service.impl;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.repository.VerificationCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceImplTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @InjectMocks
    private VerificationServiceImpl verificationService;

    private User createUser() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("shiva");
        return user;
    }

    private VerificationCode createCode() {
        VerificationCode code = new VerificationCode();
        code.setCode("123456");
        code.setUsed("N");
        code.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        return code;
    }

    @Test
    void testGenerateCode() {

        User user = createUser();

        VerificationCode code = createCode();

        when(verificationCodeRepository.save(any()))
                .thenReturn(code);

        VerificationCode result =
                verificationService.generateCode(
                        user,
                        "123456",
                        LocalDateTime.now().plusMinutes(5)
                );

        assertNotNull(result);
        verify(verificationCodeRepository).save(any());
    }

    @Test
    void testVerifyCodeSuccess() {

        User user = createUser();
        VerificationCode code = createCode();

        when(verificationCodeRepository.findByCodeAndUserAndUsed(
                "123456", user, "N"))
                .thenReturn(Optional.of(code));

        Optional<VerificationCode> result =
                verificationService.verifyCode(user, "123456");

        assertTrue(result.isPresent());
    }

    @Test
    void testVerifyCodeExpired() {

        User user = createUser();

        VerificationCode code = createCode();
        code.setExpiryTime(LocalDateTime.now().minusMinutes(1));

        when(verificationCodeRepository.findByCodeAndUserAndUsed(
                "123456", user, "N"))
                .thenReturn(Optional.of(code));

        Optional<VerificationCode> result =
                verificationService.verifyCode(user, "123456");

        assertTrue(result.isEmpty());
    }

    @Test
    void testMarkCodeUsed() {

        VerificationCode code = createCode();

        verificationService.markCodeUsed(code);

        assertEquals("Y", code.getUsed());
        verify(verificationCodeRepository).save(code);
    }

    @Test
    void testGetCodesByUser() {

        User user = createUser();

        when(verificationCodeRepository.findByUser(user))
                .thenReturn(List.of(createCode()));

        List<VerificationCode> result =
                verificationService.getCodesByUser(user);

        assertEquals(1, result.size());
    }

    @Test
    void testGetCodeById() {

        VerificationCode code = createCode();

        when(verificationCodeRepository.findById(1L))
                .thenReturn(Optional.of(code));

        VerificationCode result =
                verificationService.getCodeById(1L);

        assertNotNull(result);
    }

    @Test
    void testGetCodeByIdNotFound() {

        when(verificationCodeRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> verificationService.getCodeById(1L));
    }
}
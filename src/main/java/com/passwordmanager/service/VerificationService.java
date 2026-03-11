package com.passwordmanager.service;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationService {

    // Generate a new verification code for a user
    VerificationCode generateCode(User user, String code, LocalDateTime expiryTime);

    // Verify a code for a user, returns Optional<VerificationCode>
    Optional<VerificationCode> verifyCode(User user, String code);

    // Mark a code as used after verification
    void markCodeUsed(VerificationCode verificationCode);
    
    // Get all verification codes for a user
    List<VerificationCode> getCodesByUser(User user);
    
    // Fetch verification code by codeId
    VerificationCode getCodeById(Long codeId);
}
package com.passwordmanager.service;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationService {

    VerificationCode generateCode(User user, String code, LocalDateTime expiryTime);

    Optional<VerificationCode> verifyCode(User user, String code);

    void markCodeUsed(VerificationCode verificationCode);
    
    List<VerificationCode> getCodesByUser(User user);
    
    VerificationCode getCodeById(Long codeId);
    
}
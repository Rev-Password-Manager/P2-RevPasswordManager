package com.passwordmanager.service.impl;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.repository.VerificationCodeRepository;
import com.passwordmanager.service.VerificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Override
    public VerificationCode generateCode(User user, String code, LocalDateTime expiryTime) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setCode(code);
        verificationCode.setExpiryTime(expiryTime);
        verificationCode.setUsed("N");
        return verificationCodeRepository.save(verificationCode);
    }

    @Override
    public Optional<VerificationCode> verifyCode(User user, String code) {
        return verificationCodeRepository
                .findByCodeAndUserAndUsed(code, user, "N")
                .filter(v -> v.getExpiryTime().isAfter(LocalDateTime.now()));
    }

    @Override
    public void markCodeUsed(VerificationCode verificationCode) {
        verificationCode.setUsed("Y");
        verificationCodeRepository.save(verificationCode);
    }

    // ✅ NEW: Get all codes by user
    @Override
    public List<VerificationCode> getCodesByUser(User user) {
        return verificationCodeRepository.findByUser(user);
    }

    // ✅ NEW: Get code by ID
    @Override
    public VerificationCode getCodeById(Long codeId) {
        return verificationCodeRepository.findById(codeId)
                .orElseThrow(() -> new RuntimeException("Verification code not found"));
    }
}
package com.passwordmanager.service.impl;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.repository.VerificationCodeRepository;
import com.passwordmanager.service.VerificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VerificationServiceImpl implements VerificationService {

    // Logger to track method calls and program flow
    private static final Logger logger = LoggerFactory.getLogger(VerificationServiceImpl.class);

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    // =========================
    // GENERATE VERIFICATION CODE
    // =========================
    @Override
    public VerificationCode generateCode(User user, String code, LocalDateTime expiryTime) {
        // Log method entry
        logger.info("Entered generateCode method for userId: {}", user.getUserId());

        // Create a new VerificationCode object
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setCode(code);
        verificationCode.setExpiryTime(expiryTime);
        verificationCode.setUsed("N");

        // Save the verification code to the database
        VerificationCode savedCode = verificationCodeRepository.save(verificationCode);

        // Log successful creation
        logger.info("Verification code generated successfully for userId: {}, codeId: {}", user.getUserId(), savedCode.getCodeId());

        // Return the saved code
        return savedCode;
    }

    // =========================
    // VERIFY VERIFICATION CODE
    // =========================
    @Override
    public Optional<VerificationCode> verifyCode(User user, String code) {
        // Log method entry
        logger.info("Entered verifyCode method for userId: {}", user.getUserId());

        // Find unused code for the user and check if it is still valid (not expired)
        Optional<VerificationCode> vCode = verificationCodeRepository
                .findByCodeAndUserAndUsed(code, user, "N")
                .filter(v -> v.getExpiryTime().isAfter(LocalDateTime.now()));

        // Log whether verification succeeded or failed
        if (vCode.isPresent()) {
            logger.info("Verification code valid for userId: {}, codeId: {}", user.getUserId(), vCode.get().getCodeId());
        } else {
            logger.warn("Verification failed or code expired for userId: {}", user.getUserId());
        }

        // Return the Optional result
        return vCode;
    }

    // =========================
    // MARK VERIFICATION CODE AS USED
    // =========================
    @Override
    public void markCodeUsed(VerificationCode verificationCode) {
        // Log method entry
        logger.info("Entered markCodeUsed method for codeId: {}", verificationCode.getCodeId());

        // Update the code to indicate it has been used
        verificationCode.setUsed("Y");
        verificationCodeRepository.save(verificationCode);

        // Log successful update
        logger.info("Verification code marked as used, codeId: {}", verificationCode.getCodeId());
    }

    // =========================
    // GET ALL CODES FOR A USER
    // =========================
    @Override
    public List<VerificationCode> getCodesByUser(User user) {
        // Log method entry
        logger.info("Entered getCodesByUser method for userId: {}", user.getUserId());

        // Fetch all verification codes associated with the user
        List<VerificationCode> codes = verificationCodeRepository.findByUser(user);

        // Log how many codes were found
        logger.info("Found {} verification codes for userId: {}", codes.size(), user.getUserId());

        // Return the list
        return codes;
    }

    // =========================
    // GET CODE BY ID
    // =========================
    @Override
    public VerificationCode getCodeById(Long codeId) {
        // Log method entry
        logger.info("Entered getCodeById method for codeId: {}", codeId);

        // Fetch code by its ID or throw exception if not found
        VerificationCode code = verificationCodeRepository.findById(codeId)
                .orElseThrow(() -> new RuntimeException("Verification code not found"));

        // Log successful retrieval
        logger.info("Found verification code with codeId: {}", codeId);

        // Return the code
        return code;
    }
}
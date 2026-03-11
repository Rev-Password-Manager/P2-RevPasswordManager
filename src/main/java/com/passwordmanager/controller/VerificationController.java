package com.passwordmanager.controller;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.service.UserService;
import com.passwordmanager.service.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    private static final Logger logger = LoggerFactory.getLogger(VerificationController.class);

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private UserService userService;

    // =========================
    // GENERATE VERIFICATION CODE
    // =========================
    @PostMapping("/{userId}/generate")
    public ResponseEntity<?> generateCode(@PathVariable Long userId) {
        logger.info("Generating verification code for userId: {}", userId);

        User user = userService.getUserById(userId);

        String code = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        VerificationCode vCode = verificationService.generateCode(user, code, expiry);

        logger.info("Generated verification code: {} for userId: {} (expires at {})", code, userId, expiry);
        return ResponseEntity.ok(vCode);
    }

    // =========================
    // VERIFY VERIFICATION CODE
    // =========================
    @PostMapping("/{userId}/verify")
    public ResponseEntity<?> verifyCode(@PathVariable Long userId,
                                        @RequestParam String code) {
        logger.info("Verifying code {} for userId: {}", code, userId);

        User user = userService.getUserById(userId);

        Optional<VerificationCode> vCode = verificationService.verifyCode(user, code);

        if (vCode.isPresent()) {
            verificationService.markCodeUsed(vCode.get());
            logger.info("Verification successful for userId: {}", userId);
            return ResponseEntity.ok("Verification successful");
        }

        logger.warn("Verification failed for userId: {}, code: {}", userId, code);
        return ResponseEntity.status(400).body("Invalid or expired code");
    }

    // =========================
    // GET ALL VERIFICATION CODES FOR USER
    // =========================
    @GetMapping("/{userId}/all")
    public ResponseEntity<List<VerificationCode>> getAllCodes(@PathVariable Long userId) {
        logger.info("Fetching all verification codes for userId: {}", userId);

        User user = userService.getUserById(userId);
        List<VerificationCode> codes = verificationService.getCodesByUser(user);

        logger.info("Fetched {} verification codes for userId: {}", codes.size(), userId);
        return ResponseEntity.ok(codes);
    }

    // =========================
    // MARK CODE AS USED
    // =========================
    @PutMapping("/{codeId}/mark-used")
    public ResponseEntity<String> markCodeUsed(@PathVariable Long codeId) {
        logger.info("Marking verification codeId {} as used", codeId);

        VerificationCode code = verificationService.getCodeById(codeId);
        verificationService.markCodeUsed(code);

        logger.info("Verification codeId {} marked as used", codeId);
        return ResponseEntity.ok("Code marked as used");
    }
}
package com.passwordmanager.controller;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.service.UserService;
import com.passwordmanager.service.VerificationService;
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

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private UserService userService;

    // Generate verification code
    @PostMapping("/{userId}/generate")
    public ResponseEntity<?> generateCode(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        String code = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        VerificationCode vCode = verificationService.generateCode(user, code, expiry);
        return ResponseEntity.ok(vCode);
    }

    // Verify code
    @PostMapping("/{userId}/verify")
    public ResponseEntity<?> verifyCode(@PathVariable Long userId,
                                        @RequestParam String code) {
        User user = userService.getUserById(userId);

        Optional<VerificationCode> vCode =
                verificationService.verifyCode(user, code);

        if (vCode.isPresent()) {
            verificationService.markCodeUsed(vCode.get());
            return ResponseEntity.ok("Verification successful");
        }

        return ResponseEntity.status(400)
                .body("Invalid or expired code");
    }

    // Get all verification codes for a user
    @GetMapping("/{userId}/all")
    public ResponseEntity<List<VerificationCode>> getAllCodes(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(
                verificationService.getCodesByUser(user)
        );
    }

    // Mark verification code as used
    @PutMapping("/{codeId}/mark-used")
    public ResponseEntity<String> markCodeUsed(@PathVariable Long codeId) {
        VerificationCode code = verificationService.getCodeById(codeId);
        verificationService.markCodeUsed(code);
        return ResponseEntity.ok("Code marked as used");
    }
}
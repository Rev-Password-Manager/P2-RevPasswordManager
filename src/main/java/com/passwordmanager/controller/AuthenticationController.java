package com.passwordmanager.controller;

import com.passwordmanager.dto.LoginDto;
import com.passwordmanager.entity.*;
import com.passwordmanager.service.*;
import com.passwordmanager.repository.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthenticationController {

    // Logger to track method calls and events
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityAnswerRepository userSecurityAnswerRepository;

    @Autowired
    private SecurityAnswerService securityAnswerService;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SecurityQuestionRepository securityQuestionRepository;

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletRequest request) throws ServletException {
        logger.info("Entered login method for username/email: {}", loginDto.getUsernameOrEmail());

        User user = userService.loginUser(
                loginDto.getUsernameOrEmail(),
                loginDto.getPassword()
        );

        if (user == null) {
            logger.warn("Login failed for username/email: {}", loginDto.getUsernameOrEmail());
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }

        logger.info("User authenticated successfully: {}", user.getUsername());

        // Programmatic login
        request.login(user.getUsername(), loginDto.getPassword());

        logger.info("Exiting login method for user: {}", user.getUsername());
        return ResponseEntity.ok(Map.of("userId", user.getUserId()));
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        logger.info("Entered register method with username: {}", body.get("username"));
        try {
            User user = new User();
            user.setFullName(body.get("fullName"));
            user.setUsername(body.get("username"));
            user.setEmail(body.get("email"));
            user.setPhoneNumber(body.get("phoneNumber"));
            user.setMasterPasswordHash(passwordEncoder.encode(body.get("masterPassword")));
            user.setTwoFactorEnabled("N");
            user.setTwoFactorSecret(null);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            if (userService.getUserByUsername(user.getUsername()).isPresent()) {
                logger.warn("Registration failed: Username already exists: {}", user.getUsername());
                return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
            }

            if (userService.getUserByEmail(user.getEmail()).isPresent()) {
                logger.warn("Registration failed: Email already exists: {}", user.getEmail());
                return ResponseEntity.badRequest().body(Map.of("message", "Email already exists"));
            }

            User savedUser = userService.registerUser(user);
            logger.info("User registered successfully: {}", savedUser.getUsername());

            return ResponseEntity.ok(Map.of(
                    "message", "User Registered Successfully",
                    "userId", savedUser.getUserId()
            ));

        } catch (Exception e) {
            logger.error("Exception during registration", e);
            return ResponseEntity.status(500).body(Map.of("message", "Something went wrong: " + e.getMessage()));
        }
    }

    // ================= CHECK USER =================
    @PostMapping("/check-user")
    public Map<String, Object> checkUser(@RequestBody Map<String, String> req) {
        String usernameOrEmail = req.get("usernameOrEmail");
        logger.info("Entered checkUser method for username/email: {}", usernameOrEmail);

        Optional<User> userOpt = userService.getUserByUsername(usernameOrEmail);
        if (userOpt.isEmpty()) {
            userOpt = userService.getUserByEmail(usernameOrEmail);
        }

        if (userOpt.isEmpty()) {
            logger.warn("User not found for username/email: {}", usernameOrEmail);
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        logger.info("User found: {}", user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("twoFactorEnabled", user.getTwoFactorEnabled());

        logger.info("Exiting checkUser method for user: {}", user.getUsername());
        return response;
    }

    // ================= SECURITY QUESTIONS =================
    @GetMapping("/security-questions/{userId}")
    public ResponseEntity<?> getSecurityQuestions(@PathVariable Long userId) {
        logger.info("Fetching security questions for userId: {}", userId);
        List<UserSecurityAnswer> questions = userSecurityAnswerRepository.findByUser_UserId(userId);
        logger.info("Returning {} security questions for userId: {}", questions.size(), userId);
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/validate-security-answers")
    public ResponseEntity<?> validateSecurityAnswers(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        logger.info("Validating security answers for userId: {}", userId);

        List<String> answers = (List<String>) body.get("answers");
        boolean valid = securityAnswerService.validateAnswers(userId, answers);

        if (!valid) {
            logger.warn("Security answers validation failed for userId: {}", userId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Security answers incorrect"));
        }

        logger.info("Security answers validated successfully for userId: {}", userId);
        return ResponseEntity.ok(Map.of("message", "Security answers correct"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        logger.info("Entered resetPassword method for userId: {}", userId);
        try {
            String newPassword = body.get("newPassword");

            User user = userService.getUserById(userId);
            user.setMasterPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());

            userService.updateUser(user);

            logger.info("Password reset successfully for userId: {}", userId);
            return ResponseEntity.ok(Map.of("message", "Password reset successful"));
        } catch (Exception e) {
            logger.error("Password reset failed for userId: {}", userId, e);
            return ResponseEntity.status(500).body(Map.of("message", "Password reset failed"));
        }
    }

    @PostMapping("/save-security-answers")
    public ResponseEntity<?> saveSecurityAnswers(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        logger.info("Entered saveSecurityAnswers method for userId: {}", userId);
        try {
            List<Map<String, String>> answers = (List<Map<String, String>>) body.get("answers");
            User user = userService.getUserById(userId);

            for (Map<String, String> ans : answers) {
                UserSecurityAnswer usa = new UserSecurityAnswer();
                usa.setUser(user);

                SecurityQuestion q = new SecurityQuestion();
                q.setQuestionId(Long.valueOf(ans.get("questionId")));
                usa.setQuestion(q);

                usa.setAnswerHash(passwordEncoder.encode(ans.get("answer")));
                userSecurityAnswerRepository.save(usa);
            }

            logger.info("Security answers saved successfully for userId: {}", userId);
            return ResponseEntity.ok(Map.of("message", "Security questions saved"));
        } catch (Exception e) {
            logger.error("Failed to save security answers for userId: {}", userId, e);
            return ResponseEntity.status(500).body(Map.of("message", "Failed to save security answers"));
        }
    }

    // ================= 2FA METHODS =================
    @PostMapping("/enable-2fa/{userId}")
    public ResponseEntity<?> enable2FA(@PathVariable Long userId) {
        logger.info("Enabling 2FA for userId: {}", userId);
        User user = userService.getUserById(userId);

        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();

        String secret = key.getKey().substring(0, Math.min(key.getKey().length(), 20));

        user.setTwoFactorEnabled("Y");
        user.setTwoFactorSecret(secret);

        userService.updateUser(user);

        logger.info("2FA enabled successfully for userId: {}", userId);
        return ResponseEntity.ok(Map.of("secret", secret));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        int otp = Integer.parseInt(body.get("otp"));
        logger.info("Verifying OTP for userId: {}", userId);

        User user = userService.getUserById(userId);
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean valid = gAuth.authorize(user.getTwoFactorSecret(), otp);

        logger.info("OTP verification result for userId {}: {}", userId, valid);
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/disable-2fa/{userId}")
    public ResponseEntity<?> disable2FA(@PathVariable Long userId) {
        logger.info("Disabling 2FA for userId: {}", userId);

        User user = userService.getUserById(userId);
        user.setTwoFactorEnabled("N");
        user.setTwoFactorSecret(null);

        userService.updateUser(user);

        logger.info("2FA disabled successfully for userId: {}", userId);
        return ResponseEntity.ok(Map.of("message", "2FA disabled"));
    }

    @GetMapping("/all-security-questions")
    public ResponseEntity<?> getAllSecurityQuestions() {
        logger.info("Fetching all security questions");
        List<SecurityQuestion> questions = securityQuestionRepository.findAll();
        logger.info("Returning {} security questions", questions.size());
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/send-auth-code")
    public ResponseEntity<?> sendAuthCode(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        logger.info("Generating authentication code for userId: {}", userId);

        User user = userService.getUserById(userId);
        int code = (int) (Math.random() * 900000) + 100000;

        VerificationCode v = new VerificationCode();
        v.setUser(user);
        v.setCode(String.valueOf(code));
        v.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        v.setUsed("N");

        verificationCodeRepository.save(v);

        logger.info("Authentication code generated and saved for userId: {}", userId);
        return ResponseEntity.ok(Map.of("message", "Authentication code generated"));
    }

    @PostMapping("/verify-auth-code")
    public ResponseEntity<?> verifyAuthCode(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        String code = body.get("code");
        logger.info("Verifying authentication code for userId: {}", userId);

        User user = userService.getUserById(userId);
        Optional<VerificationCode> codeOptional =
                verificationCodeRepository.findByCodeAndUserAndUsed(code, user, "N");

        boolean valid = codeOptional.isPresent() && !codeOptional.get().getExpiryTime().isBefore(LocalDateTime.now());

        if (valid) {
            VerificationCode v = codeOptional.get();
            v.setUsed("Y");
            verificationCodeRepository.save(v);
        }

        logger.info("Authentication code verification result for userId {}: {}", userId, valid);
        return ResponseEntity.ok(Map.of("valid", valid));
    }
}
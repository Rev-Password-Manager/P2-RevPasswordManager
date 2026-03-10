package com.passwordmanager.controller;

import com.passwordmanager.dto.LoginDto;
import com.passwordmanager.dto.SecurityAnswerDto;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.service.SecurityAnswerService;
import com.passwordmanager.service.UserService;
import com.passwordmanager.service.impl.SecurityAnswerServiceImpl;
import com.passwordmanager.repository.UserSecurityAnswerRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityAnswerRepository userSecurityAnswerRepository;

    
    @Autowired
    private SecurityAnswerService securityAnswerService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpServletRequest request) throws ServletException {
        User user = userService.loginUser(
                loginDto.getUsernameOrEmail(),
                loginDto.getPassword()
        );

        if (user == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("message","Invalid credentials"));
        }

        // ✅ From first version: programmatic login
        request.login(user.getUsername(), loginDto.getPassword());

        // ✅ From first version: return only userId
        return ResponseEntity.ok(Map.of("userId", user.getUserId()));
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String,String> body){
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

            if(userService.getUserByUsername(user.getUsername()).isPresent()){
                return ResponseEntity.badRequest().body(Map.of("message","Username already exists"));
            }

            if(userService.getUserByEmail(user.getEmail()).isPresent()){
                return ResponseEntity.badRequest().body(Map.of("message","Email already exists"));
            }

            User savedUser = userService.registerUser(user);

            return ResponseEntity.ok(Map.of(
                    "message","User Registered Successfully",
                    "userId", savedUser.getUserId()
            ));

        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message","Something went wrong: "+e.getMessage()));
        }
    }

    // ======================================================
    // FORGOT PASSWORD FLOW USING SECURITY QUESTIONS
    // ======================================================

    // 1️⃣ CHECK USERNAME / EMAIL
    @PostMapping("/check-user")
    public ResponseEntity<?> checkUser(@RequestBody Map<String,String> body){
        String usernameOrEmail = body.get("usernameOrEmail");

        Optional<User> userOptional = userService.getUserByUsername(usernameOrEmail);
        if(userOptional.isEmpty()){
            userOptional = userService.getUserByEmail(usernameOrEmail);
        }

        if(userOptional.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("message","User not found"));
        }

        User user = userOptional.get();
        return ResponseEntity.ok(Map.of("message","User exists","userId",user.getUserId()));
    }

 // 2️⃣ GET SECURITY QUESTIONS
    @GetMapping("/security-questions/{userId}")
    public ResponseEntity<?> getSecurityQuestions(@PathVariable Long userId){
        List<UserSecurityAnswer> questions = userSecurityAnswerRepository.findByUser_UserId(userId);
        return ResponseEntity.ok(questions);
    }

    // 3️⃣ VALIDATE SECURITY ANSWERS
    @PostMapping("/validate-security-answers")
    public ResponseEntity<Map<String, String>> validateSecurityAnswers(@RequestBody SecurityAnswerDto request) {
        boolean valid = securityAnswerService.validateAnswers(request.getUserId(), request.getAnswers());

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Security answers incorrect"));
        }

        return ResponseEntity.ok(Map.of("message", "Security answers correct"));
    }


    // 4️⃣ RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String,String> body){
        try {
            Long userId = Long.valueOf(body.get("userId"));
            String newPassword = body.get("newPassword");

            User user = userService.getUserById(userId);
            user.setMasterPasswordHash(passwordEncoder.encode(newPassword));
            user.setUpdatedAt(LocalDateTime.now());

            userService.updateUser(user); // FIXED: use updateUser

            return ResponseEntity.ok(Map.of("message","Password reset successful"));
        } catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("message","Password reset failed"));
        }
    }

    // ================= SAVE SECURITY QUESTIONS =================
    @PostMapping("/save-security-answers")
    public ResponseEntity<?> saveSecurityAnswers(@RequestBody Map<String,Object> body){
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            List<Map<String,String>> answers = (List<Map<String,String>>) body.get("answers");

            User user = userService.getUserById(userId);

            for(Map<String,String> ans : answers){
                UserSecurityAnswer usa = new UserSecurityAnswer();
                usa.setUser(user);

                SecurityQuestion q = new SecurityQuestion();
                q.setQuestionId(Long.valueOf(ans.get("questionId")));
                usa.setQuestion(q);

                // ✅ Hash the raw answer
                usa.setAnswerHash(passwordEncoder.encode(ans.get("answer")));


                userSecurityAnswerRepository.save(usa);
            }

            return ResponseEntity.ok(Map.of("message","Security questions saved"));
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message","Failed to save security answers"));
        }
    }
}
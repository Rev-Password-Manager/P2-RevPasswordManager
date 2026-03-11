package com.passwordmanager.controller;

import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.SecurityQuestionService;
import com.passwordmanager.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
public class SecurityQuestionController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityQuestionController.class);

    @Autowired
    private SecurityQuestionService securityQuestionService;

    @Autowired
    private UserService userService;

    // =========================
    // GET ALL SECURITY QUESTIONS
    // =========================
    @GetMapping("/questions")
    public ResponseEntity<List<SecurityQuestion>> getAllQuestions() {
        logger.info("Fetching all security questions");
        List<SecurityQuestion> questions = securityQuestionService.getAllQuestions();
        logger.info("Total security questions fetched: {}", questions.size());
        return ResponseEntity.ok(questions);
    }

    // =========================
    // SAVE USER ANSWERS
    // =========================
    @PostMapping("/{userId}/answers")
    public ResponseEntity<?> saveAnswers(@PathVariable Long userId, @RequestBody List<UserSecurityAnswer> answers) {
        logger.info("Saving security answers for userId: {}", userId);
        User user = userService.getUserById(userId);

        for (UserSecurityAnswer answer : answers) {
            answer.setUser(user);
            securityQuestionService.saveUserAnswer(answer);
            logger.info("Saved answer for questionId: {} for userId: {}", 
                        answer.getQuestion().getQuestionId(), userId);
        }

        logger.info("All security answers saved successfully for userId: {}", userId);
        return ResponseEntity.ok("Answers saved");
    }

    // =========================
    // GET USER ANSWERS
    // =========================
    @GetMapping("/{userId}/answers")
    public ResponseEntity<?> getUserAnswers(@PathVariable Long userId) {
        logger.info("Fetching security answers for userId: {}", userId);
        User user = userService.getUserById(userId);

        List<UserSecurityAnswer> answers = securityQuestionService.getAnswersByUser(user);
        logger.info("Fetched {} security answers for userId: {}", answers.size(), userId);

        return ResponseEntity.ok(answers);
    }

    // =========================
    // VERIFY ANSWERS FOR PASSWORD RESET
    // =========================
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAnswers(@RequestBody Map<String,String> body){
        Long userId = Long.parseLong(body.get("userId"));
        logger.info("Verifying security answers for userId: {}", userId);

        String answer1 = body.get("answer1");
        String answer2 = body.get("answer2");

        User user = userService.getUserById(userId);
        List<UserSecurityAnswer> answers = securityQuestionService.getAnswersByUser(user);

        if(answers.size() < 2){
            logger.warn("Security questions not configured for userId: {}", userId);
            return ResponseEntity.badRequest().body("Security questions not configured");
        }

        boolean valid1 = answers.get(0).getAnswerHash().equals(answer1);
        boolean valid2 = answers.get(1).getAnswerHash().equals(answer2);

        if(valid1 && valid2){
            logger.info("Security answers verified successfully for userId: {}", userId);
            return ResponseEntity.ok("Answers verified");
        }

        logger.warn("Security answers verification failed for userId: {}", userId);
        return ResponseEntity.badRequest().body("Wrong answers");
    }
}
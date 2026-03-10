package com.passwordmanager.controller;

import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.SecurityQuestionService;
import com.passwordmanager.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
public class SecurityQuestionController {

    @Autowired
    private SecurityQuestionService securityQuestionService;

    @Autowired
    private UserService userService;

    // Get all security questions
    @GetMapping("/questions")
    public ResponseEntity<List<SecurityQuestion>> getAllQuestions() {
        return ResponseEntity.ok(securityQuestionService.getAllQuestions());
    }

    // Save user answers
    @PostMapping("/{userId}/answers")
    public ResponseEntity<?> saveAnswers(@PathVariable Long userId, @RequestBody List<UserSecurityAnswer> answers) {

        User user = userService.getUserById(userId);

        for(UserSecurityAnswer answer : answers) {
            answer.setUser(user);
            securityQuestionService.saveUserAnswer(answer);
        }

        return ResponseEntity.ok("Answers saved");
    }

    // Get user answers
    @GetMapping("/{userId}/answers")
    public ResponseEntity<?> getUserAnswers(@PathVariable Long userId) {

        User user = userService.getUserById(userId);

        List<UserSecurityAnswer> answers = securityQuestionService.getAnswersByUser(user);

        return ResponseEntity.ok(answers);
    }


    // ⭐ NEW API - Verify answers for password reset
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAnswers(@RequestBody Map<String,String> body){

        Long userId = Long.parseLong(body.get("userId"));
        String answer1 = body.get("answer1");
        String answer2 = body.get("answer2");

        User user = userService.getUserById(userId);

        List<UserSecurityAnswer> answers =
                securityQuestionService.getAnswersByUser(user);

        if(answers.size() < 2){
            return ResponseEntity.badRequest()
                    .body("Security questions not configured");
        }

        boolean valid1 = answers.get(0).getAnswerHash().equals(answer1);
        boolean valid2 = answers.get(1).getAnswerHash().equals(answer2);

        if(valid1 && valid2){
            return ResponseEntity.ok("Answers verified");
        }

        return ResponseEntity.badRequest().body("Wrong answers");
    }
}
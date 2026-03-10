package com.passwordmanager.controller;

import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.repository.SecurityQuestionRepository;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/security-answers")
public class UserSecurityAnswerController {

    @Autowired
    private UserSecurityAnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityQuestionRepository questionRepository;

    // ✅ ADD SECURITY ANSWERS
    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addAnswers(
            @PathVariable Long userId,
            @RequestBody List<UserSecurityAnswer> answers) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = optionalUser.get();

        for (UserSecurityAnswer answer : answers) {
            answer.setUser(user);
        }

        answerRepository.saveAll(answers);

        return ResponseEntity.ok("Security answers saved successfully");
    }

    // ✅ UPDATE SECURITY ANSWERS
    @PutMapping("/{userId}/update")
    public ResponseEntity<?> updateAnswers(
            @PathVariable Long userId,
            @RequestBody List<UserSecurityAnswer> updatedAnswers) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = optionalUser.get();

        List<UserSecurityAnswer> existingAnswers = answerRepository.findByUser(user);

        for (UserSecurityAnswer existing : existingAnswers) {
            for (UserSecurityAnswer updated : updatedAnswers) {
                if (existing.getQuestion().getQuestionId()
                        .equals(updated.getQuestion().getQuestionId())) {

                    existing.setAnswerHash(updated.getAnswerHash());
                }
            }
        }

        answerRepository.saveAll(existingAnswers);

        return ResponseEntity.ok("Security answers updated successfully");
    }

    // ✅ GET ALL SECURITY ANSWERS (ONLY QUESTIONS)
    @GetMapping("/{userId}/all")
    public ResponseEntity<?> getAllAnswers(@PathVariable Long userId) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = optionalUser.get();

        List<UserSecurityAnswer> answers = answerRepository.findByUser(user);

        // Return only question text (not hash)
        List<String> questions = answers.stream()
                .map(a -> a.getQuestion().getQuestionText())
                .collect(Collectors.toList());

        return ResponseEntity.ok(questions);
    }
}
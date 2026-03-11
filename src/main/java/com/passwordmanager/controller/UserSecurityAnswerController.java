package com.passwordmanager.controller;

import com.passwordmanager.dto.UserQuestionDTO;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/security-answers")
public class UserSecurityAnswerController {

    private static final Logger logger = LoggerFactory.getLogger(UserSecurityAnswerController.class);

    @Autowired
    private UserSecurityAnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // =========================
    // GET ALL QUESTIONS FOR PROFILE VIEW
    // =========================
    @GetMapping("/{userId}/all")
    public ResponseEntity<?> getAllAnswers(@PathVariable Long userId) {
        logger.info("Fetching all security questions for userId: {}", userId);

        List<UserSecurityAnswer> answers = answerRepository.findByUser_UserId(userId);
        logger.info("Total questions fetched: {}", answers.size());

        answers.forEach(a -> logger.info("Question: {}", a.getQuestion().getQuestionText()));

        List<String> questions = answers.stream()
                .map(a -> a.getQuestion().getQuestionText())
                .collect(Collectors.toList());

        return ResponseEntity.ok(questions);
    }

    // =========================
    // GET QUESTIONS FOR EDIT MODAL
    // =========================
    @GetMapping("/{userId}/edit")
    public ResponseEntity<?> getQuestionsForEdit(@PathVariable Long userId) {
        logger.info("Fetching security questions for edit modal for userId: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for userId: {}", userId);
            return ResponseEntity.badRequest().body("User not found");
        }

        List<UserSecurityAnswer> answers = answerRepository.findByUser(userOpt.get());

        List<UserQuestionDTO> dtoList = answers.stream().map(a -> {
            UserQuestionDTO dto = new UserQuestionDTO();
            dto.setAnswerId(a.getAnswerId());
            dto.setQuestionId(a.getQuestion().getQuestionId());
            dto.setQuestionText(a.getQuestion().getQuestionText());
            dto.setAnswer(""); // frontend input
            return dto;
        }).collect(Collectors.toList());

        logger.info("Questions prepared for edit modal for userId: {}", userId);
        return ResponseEntity.ok(dtoList);
    }

    // =========================
    // UPDATE SECURITY ANSWERS
    // =========================
    @PutMapping("/{userId}/update")
    public ResponseEntity<?> updateAnswers(@PathVariable Long userId,
                                           @RequestBody List<UserQuestionDTO> updatedAnswers) {

        logger.info("Updating security answers for userId: {}", userId);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User not found for userId: {}", userId);
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();
        List<UserSecurityAnswer> existingAnswers = answerRepository.findByUser(user);

        for (UserSecurityAnswer existing : existingAnswers) {
            for (UserQuestionDTO updated : updatedAnswers) {
                if (existing.getAnswerId().equals(updated.getAnswerId())) {
                    existing.setAnswerHash(passwordEncoder.encode(updated.getAnswer()));
                    logger.info("Updated answer for questionId: {} for userId: {}",
                            updated.getQuestionId(), userId);
                }
            }
        }

        answerRepository.saveAll(existingAnswers);
        logger.info("All security answers updated successfully for userId: {}", userId);

        return ResponseEntity.ok("Security answers updated successfully");
    }
}
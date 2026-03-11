package com.passwordmanager.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import com.passwordmanager.service.SecurityAnswerService;

@Service
public class SecurityAnswerServiceImpl implements SecurityAnswerService {

    // Logger to track method calls and flow
    private static final Logger logger = LoggerFactory.getLogger(SecurityAnswerServiceImpl.class);

    @Autowired
    private UserSecurityAnswerRepository userSecurityAnswerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // =========================
    // VALIDATE USER SECURITY ANSWERS
    // =========================
    @Override
    public boolean validateAnswers(Long userId, List<String> submittedAnswers) {
        logger.info("Entered validateAnswers for userId: {}", userId);
        logger.info("Number of submitted answers: {}", submittedAnswers.size());

        // Fetch answers from DB for the user
        List<UserSecurityAnswer> dbAnswers = userSecurityAnswerRepository.findByUser_UserId(userId);
        logger.info("Number of stored answers found: {}", (dbAnswers != null ? dbAnswers.size() : 0));

        // Check if DB answers exist
        if (dbAnswers == null || dbAnswers.isEmpty()) {
            logger.warn("No security answers configured for userId: {}", userId);
            return false;
        }

        // Check if number of submitted answers matches DB
        if (dbAnswers.size() != submittedAnswers.size()) {
            logger.warn("Mismatch in number of submitted answers and stored answers for userId: {}", userId);
            return false;
        }

        // Compare submitted answers with hashed DB answers
        for (int i = 0; i < dbAnswers.size(); i++) {
            String hashed = dbAnswers.get(i).getAnswerHash();
            String raw = submittedAnswers.get(i).trim();

            if (!passwordEncoder.matches(raw, hashed)) {
                logger.warn("Security answer mismatch at index {} for userId: {}", i, userId);
                return false;
            }
        }

        logger.info("All security answers validated successfully for userId: {}", userId);
        return true;
    }
}
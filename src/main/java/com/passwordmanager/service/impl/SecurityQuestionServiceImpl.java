package com.passwordmanager.service.impl;

import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.repository.SecurityQuestionRepository;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import com.passwordmanager.service.SecurityQuestionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    // Logger to track method calls and flow
    private static final Logger logger = LoggerFactory.getLogger(SecurityQuestionServiceImpl.class);

    @Autowired
    private SecurityQuestionRepository questionRepository;

    @Autowired
    private UserSecurityAnswerRepository answerRepository;

    // =========================
    // GET ALL SECURITY QUESTIONS
    // =========================
    @Override
    public List<SecurityQuestion> getAllQuestions() {
        logger.info("Entered getAllQuestions method");

        List<SecurityQuestion> questions = questionRepository.findAll();

        logger.info("Fetched {} security questions", questions.size());
        return questions;
    }

    // =========================
    // SAVE USER SECURITY ANSWER
    // =========================
    @Override
    public UserSecurityAnswer saveUserAnswer(UserSecurityAnswer answer) {
        logger.info("Entered saveUserAnswer method for userId: {}", answer.getUser().getUserId());

        UserSecurityAnswer savedAnswer = answerRepository.save(answer);

        logger.info("Saved security answer with answerId: {} for userId: {}",
                savedAnswer.getAnswerId(), savedAnswer.getUser().getUserId());
        return savedAnswer;
    }

    // =========================
    // GET USER SECURITY ANSWERS
    // =========================
    @Override
    public List<UserSecurityAnswer> getAnswersByUser(User user) {
        logger.info("Entered getAnswersByUser method for userId: {}", user.getUserId());

        List<UserSecurityAnswer> answers = answerRepository.findByUser(user);

        logger.info("Fetched {} security answers for userId: {}", answers.size(), user.getUserId());
        return answers;
    }
}
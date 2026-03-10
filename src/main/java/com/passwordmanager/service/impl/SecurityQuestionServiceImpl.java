package com.passwordmanager.service.impl;

import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.repository.SecurityQuestionRepository;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import com.passwordmanager.service.SecurityQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SecurityQuestionServiceImpl implements SecurityQuestionService {

    @Autowired
    private SecurityQuestionRepository questionRepository;

    @Autowired
    private UserSecurityAnswerRepository answerRepository;

    @Override
    public List<SecurityQuestion> getAllQuestions() {
        return questionRepository.findAll();
    }

    @Override
    public UserSecurityAnswer saveUserAnswer(UserSecurityAnswer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public List<UserSecurityAnswer> getAnswersByUser(User user) {
        return answerRepository.findByUser(user);
    }
}
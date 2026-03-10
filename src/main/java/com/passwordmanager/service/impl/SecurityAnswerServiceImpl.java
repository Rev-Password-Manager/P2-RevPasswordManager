package com.passwordmanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import com.passwordmanager.service.SecurityAnswerService;

@Service
public class SecurityAnswerServiceImpl implements SecurityAnswerService {

    @Autowired
    private UserSecurityAnswerRepository userSecurityAnswerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean validateAnswers(Long userId, List<String> submittedAnswers) {
        // ✅ Use the by-userId method
        List<UserSecurityAnswer> dbAnswers = userSecurityAnswerRepository.findByUser_UserId(userId);

        if (dbAnswers == null || dbAnswers.isEmpty()) {
            return false;
        }

        if (dbAnswers.size() != submittedAnswers.size()) {
            return false;
        }

        for (int i = 0; i < dbAnswers.size(); i++) {
            String hashed = dbAnswers.get(i).getAnswerHash();
            String raw = submittedAnswers.get(i).trim();

            if (!passwordEncoder.matches(raw, hashed)) {
                return false;
            }
        }
        return true;
    }
}

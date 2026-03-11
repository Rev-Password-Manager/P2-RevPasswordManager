package com.passwordmanager.service;

import java.util.List;

public interface SecurityAnswerService {

    // Validate submitted answers for a user (used in password reset)
    boolean validateAnswers(Long userId, List<String> submittedAnswers);
}
package com.passwordmanager.service;

import java.util.List;

public interface SecurityAnswerService {
    boolean validateAnswers(Long userId, List<String> submittedAnswers);
}

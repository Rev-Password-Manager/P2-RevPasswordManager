package com.passwordmanager.dto;

import java.util.List;

public class SecurityAnswerDto {
    private Long userId;              // the user whose answers are being validated
    private List<String> answers;     // the answers submitted by the user

    // getters and setters
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getAnswers() {
        return answers;
    }
    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}

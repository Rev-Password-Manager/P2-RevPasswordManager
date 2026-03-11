package com.passwordmanager.dto;

import java.util.List;

public class SecurityAnswerDto {

    private Long userId;
    private List<AnswerDto> answers;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<AnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDto> answers) {
        this.answers = answers;
    }
}
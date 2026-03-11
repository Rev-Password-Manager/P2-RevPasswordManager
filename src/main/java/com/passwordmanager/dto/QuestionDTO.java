package com.passwordmanager.dto;

// For profile view: questions only
public class QuestionDTO {
    private Long questionId;
    private String questionText;

    public QuestionDTO(Long questionId, String questionText) {
        this.questionId = questionId;
        this.questionText = questionText;
    }

    public Long getQuestionId() { return questionId; }
    public String getQuestionText() { return questionText; }
}
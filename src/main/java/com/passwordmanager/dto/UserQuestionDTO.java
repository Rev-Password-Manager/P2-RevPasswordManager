package com.passwordmanager.dto;

// For edit modal: includes answerId
public class UserQuestionDTO {
    private Long answerId;      // needed for updating
    private Long questionId;
    private String questionText;
    private String answer;      // user can edit

    // getters & setters
    public Long getAnswerId() { return answerId; }
    public void setAnswerId(Long answerId) { this.answerId = answerId; }

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
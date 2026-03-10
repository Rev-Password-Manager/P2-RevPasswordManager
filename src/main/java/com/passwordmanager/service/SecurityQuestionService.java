package com.passwordmanager.service;

import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import java.util.List;

public interface SecurityQuestionService {

    List<SecurityQuestion> getAllQuestions();

    UserSecurityAnswer saveUserAnswer(UserSecurityAnswer answer);

    List<UserSecurityAnswer> getAnswersByUser(User user);
}
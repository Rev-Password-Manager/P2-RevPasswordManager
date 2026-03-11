package com.passwordmanager.service.impl;

import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.repository.SecurityQuestionRepository;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import com.passwordmanager.service.impl.SecurityQuestionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityQuestionServiceImplTest {

    @Mock
    private SecurityQuestionRepository securityQuestionRepository;

    @Mock
    private UserSecurityAnswerRepository userSecurityAnswerRepository;

    @InjectMocks
    private SecurityQuestionServiceImpl securityQuestionService;

    private SecurityQuestion createQuestion() {
        SecurityQuestion q = new SecurityQuestion();
        q.setQuestionId(1L);
        q.setQuestionText("Your pet name?");
        return q;
    }

    private User createUser() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("shiva");
        return user;
    }

    private UserSecurityAnswer createAnswer() {
        UserSecurityAnswer answer = new UserSecurityAnswer();
        answer.setAnswerId(1L);
        answer.setAnswerHash("hashedAnswer");
        answer.setUser(createUser());
        answer.setQuestion(createQuestion());
        return answer;
    }

    @Test
    void testGetAllQuestions() {

        List<SecurityQuestion> questions = List.of(createQuestion());

        when(securityQuestionRepository.findAll()).thenReturn(questions);

        List<SecurityQuestion> result =
                securityQuestionService.getAllQuestions();

        assertEquals(1, result.size());
        verify(securityQuestionRepository).findAll();
    }

    @Test
    void testSaveUserAnswer() {

        UserSecurityAnswer answer = createAnswer();

        when(userSecurityAnswerRepository.save(answer))
                .thenReturn(answer);

        UserSecurityAnswer result =
                securityQuestionService.saveUserAnswer(answer);

        assertNotNull(result);
        verify(userSecurityAnswerRepository).save(answer);
    }

    @Test
    void testGetAnswersByUser() {

        User user = createUser();
        List<UserSecurityAnswer> answers = List.of(createAnswer());

        when(userSecurityAnswerRepository.findByUser(user))
                .thenReturn(answers);

        List<UserSecurityAnswer> result =
                securityQuestionService.getAnswersByUser(user);

        assertEquals(1, result.size());
        verify(userSecurityAnswerRepository).findByUser(user);
    }
}
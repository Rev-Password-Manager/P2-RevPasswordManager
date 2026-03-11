package com.passwordmanager.repository;

import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserSecurityAnswerRepositoryTest {

    @Autowired
    private UserSecurityAnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityQuestionRepository questionRepository;

    private User createUser() {
        User user = new User();
        user.setFullName("Test User");
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        user.setPhoneNumber("9999999999");
        user.setMasterPasswordHash("123456");
        user.setTwoFactorEnabled("N");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private SecurityQuestion createQuestion() {
        SecurityQuestion q = new SecurityQuestion();
        q.setQuestionText("What is your pet name?");
        return questionRepository.save(q);
    }

    private UserSecurityAnswer createAnswer(User user, SecurityQuestion question) {
        UserSecurityAnswer answer = new UserSecurityAnswer();
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setAnswerHash("hashedAnswer");
        return answerRepository.save(answer);
    }

    @Test
    void testFindByUser() {

        User user = createUser();
        SecurityQuestion q = createQuestion();
        createAnswer(user, q);

        List<UserSecurityAnswer> result =
                answerRepository.findByUser(user);

        assertFalse(result.isEmpty());
    }

    @Test
    void testFindByUserId() {

        User user = createUser();
        SecurityQuestion q = createQuestion();
        createAnswer(user, q);

        List<UserSecurityAnswer> result =
                answerRepository.findByUser_UserId(user.getUserId());

        assertFalse(result.isEmpty());
    }

    @Test
    void testDeleteByUserId() {

        User user = createUser();
        SecurityQuestion q = createQuestion();
        createAnswer(user, q);

        answerRepository.deleteByUser_UserId(user.getUserId());

        List<UserSecurityAnswer> result =
                answerRepository.findByUser_UserId(user.getUserId());

        assertTrue(result.isEmpty());
    }
}
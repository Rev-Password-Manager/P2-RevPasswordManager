package com.passwordmanager.repository;

import com.passwordmanager.entity.SecurityQuestion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SecurityQuestionRepositoryTest {

    @Autowired
    private SecurityQuestionRepository repository;

    private SecurityQuestion createQuestion(String text) {
        SecurityQuestion q = new SecurityQuestion();
        q.setQuestionText(text);
        return repository.save(q);
    }

    @Test
    void testSaveSecurityQuestion() {

        SecurityQuestion q = new SecurityQuestion();
        q.setQuestionText("What is your pet name?");

        SecurityQuestion saved = repository.save(q);

        assertNotNull(saved.getQuestionId());
        assertEquals("What is your pet name?", saved.getQuestionText());
    }

    @Test
    void testFindById() {

        SecurityQuestion saved = createQuestion("What is your school name?");

        Optional<SecurityQuestion> result =
                repository.findById(saved.getQuestionId());

        assertTrue(result.isPresent());
        assertEquals("What is your school name?", result.get().getQuestionText());
    }

    @Test
    void testFindAll() {

        createQuestion("Question 1");
        createQuestion("Question 2");

        List<SecurityQuestion> questions = repository.findAll();

        assertTrue(questions.size() >= 2);
    }

    @Test
    void testDeleteQuestion() {

        SecurityQuestion saved = createQuestion("Delete test question");

        repository.deleteById(saved.getQuestionId());

        Optional<SecurityQuestion> result =
                repository.findById(saved.getQuestionId());

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateQuestion() {

        SecurityQuestion saved = createQuestion("Old Question");

        saved.setQuestionText("Updated Question");
        repository.save(saved);

        SecurityQuestion updated =
                repository.findById(saved.getQuestionId()).get();

        assertEquals("Updated Question", updated.getQuestionText());
    }
}
package com.passwordmanager.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passwordmanager.controller.UserSecurityAnswerController;
import com.passwordmanager.dto.UserQuestionDTO;
import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserSecurityAnswerController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserSecurityAnswerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSecurityAnswerRepository answerRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser(){
        User user = new User();
        user.setUserId(1L);
        return user;
    }

    private UserSecurityAnswer mockAnswer(Long id,String questionText){
        SecurityQuestion q = new SecurityQuestion();
        q.setQuestionId(1L);
        q.setQuestionText(questionText);

        UserSecurityAnswer a = new UserSecurityAnswer();
        a.setAnswerId(id);
        a.setQuestion(q);
        a.setAnswerHash("hashed");
        return a;
    }

    @Test
    void testGetAllAnswers() throws Exception {

        Mockito.when(answerRepository.findByUser_UserId(1L))
                .thenReturn(List.of(
                        mockAnswer(1L,"Pet name"),
                        mockAnswer(2L,"Birth city")
                ));

        mockMvc.perform(get("/api/security-answers/1/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetQuestionsForEdit() throws Exception {

        User user = mockUser();

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Mockito.when(answerRepository.findByUser(user))
                .thenReturn(List.of(
                        mockAnswer(1L,"Pet name"),
                        mockAnswer(2L,"Birth city")
                ));

        mockMvc.perform(get("/api/security-answers/1/edit"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetQuestionsForEditUserNotFound() throws Exception {

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/security-answers/1/edit"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateAnswers() throws Exception {

        User user = mockUser();

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        UserSecurityAnswer existing = mockAnswer(1L,"Pet name");

        Mockito.when(answerRepository.findByUser(user))
                .thenReturn(List.of(existing));

        Mockito.when(passwordEncoder.encode(Mockito.anyString()))
                .thenReturn("hashedNew");

        UserQuestionDTO dto = new UserQuestionDTO();
        dto.setAnswerId(1L);
        dto.setAnswer("dog");

        mockMvc.perform(put("/api/security-answers/1/update")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(List.of(dto))))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateAnswersUserNotFound() throws Exception {

        Mockito.when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/security-answers/1/update")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(List.of())))
                .andExpect(status().isBadRequest());
    }
}
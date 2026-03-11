package com.passwordmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.service.SecurityQuestionService;
import com.passwordmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SecurityQuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SecurityQuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityQuestionService securityQuestionService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser(){
        User user = new User();
        user.setUserId(1L);
        return user;
    }

    private UserSecurityAnswer mockAnswer(String ans){
        UserSecurityAnswer answer = new UserSecurityAnswer();
        answer.setAnswerHash(ans);

        SecurityQuestion q = new SecurityQuestion();
        q.setQuestionId(1L);
        q.setQuestionText("Sample Question");
        answer.setQuestion(q); // ✅ ensure question is set

        return answer;
    }

    @Test
    void testGetAllQuestions() throws Exception {
        SecurityQuestion q = new SecurityQuestion();
        q.setQuestionId(1L);
        q.setQuestionText("Your pet name?");

        Mockito.when(securityQuestionService.getAllQuestions())
                .thenReturn(List.of(q));

        mockMvc.perform(get("/api/security/questions"))
                .andExpect(status().isOk());
    }

    @Test
    void testSaveAnswers() throws Exception {
        User user = mockUser();

        UserSecurityAnswer a1 = mockAnswer("dog"); // ✅ includes question

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(post("/api/security/1/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(a1))))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserAnswers() throws Exception {
        User user = mockUser();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(securityQuestionService.getAnswersByUser(user))
                .thenReturn(List.of(mockAnswer("dog")));

        mockMvc.perform(get("/api/security/1/answers"))
                .andExpect(status().isOk());
    }

    @Test
    void testVerifyAnswersSuccess() throws Exception {
        User user = mockUser();

        UserSecurityAnswer a1 = mockAnswer("dog");
        UserSecurityAnswer a2 = mockAnswer("cat");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(securityQuestionService.getAnswersByUser(user))
                .thenReturn(List.of(a1,a2));

        Map<String,String> body = Map.of(
                "userId","1",
                "answer1","dog",
                "answer2","cat"
        );

        mockMvc.perform(post("/api/security/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void testVerifyAnswersWrong() throws Exception {
        User user = mockUser();

        UserSecurityAnswer a1 = mockAnswer("dog");
        UserSecurityAnswer a2 = mockAnswer("cat");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(securityQuestionService.getAnswersByUser(user))
                .thenReturn(List.of(a1,a2));

        Map<String,String> body = Map.of(
                "userId","1",
                "answer1","wrong",
                "answer2","cat"
        );

        mockMvc.perform(post("/api/security/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerifyAnswersNotConfigured() throws Exception {
        User user = mockUser();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(securityQuestionService.getAnswersByUser(user))
                .thenReturn(List.of());

        Map<String,String> body = Map.of(
                "userId","1",
                "answer1","a",
                "answer2","b"
        );

        mockMvc.perform(post("/api/security/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }
}

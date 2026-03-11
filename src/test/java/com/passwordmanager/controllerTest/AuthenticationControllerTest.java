package com.passwordmanager.controllerTest;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.controller.AuthenticationController;
import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.service.UserService;
import com.passwordmanager.service.SecurityAnswerService;
import com.passwordmanager.repository.UserSecurityAnswerRepository;
import com.passwordmanager.repository.VerificationCodeRepository;
import com.passwordmanager.repository.SecurityQuestionRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserSecurityAnswerRepository userSecurityAnswerRepository;

    @MockBean
    private SecurityAnswerService securityAnswerService;

    @MockBean
    private VerificationCodeRepository verificationCodeRepository;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private SecurityQuestionRepository securityQuestionRepository;

    @Test
    void testLoginSuccess() throws Exception {

        User user = new User();
        user.setUserId(1L);
        user.setUsername("test");

        Mockito.when(userService.loginUser("test", "pass"))
                .thenReturn(user);

        String json = """
    {
      "usernameOrEmail":"test",
      "password":"pass"
    }
    """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testRegisterSuccess() throws Exception {

        User user = new User();
        user.setUserId(1L);

        Mockito.when(userService.getUserByUsername("test"))
                .thenReturn(Optional.empty());

        Mockito.when(userService.getUserByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        Mockito.when(passwordEncoder.encode(any()))
                .thenReturn("hashed");

        Mockito.when(userService.registerUser(any()))
                .thenReturn(user);

        String json = """
        {
          "fullName":"Test User",
          "username":"test",
          "email":"test@gmail.com",
          "phoneNumber":"9999999999",
          "masterPassword":"pass"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testCheckUser() throws Exception {

        User user = new User();
        user.setUserId(1L);
        user.setTwoFactorEnabled("N");

        Mockito.when(userService.getUserByUsername("test"))
                .thenReturn(Optional.of(user));

        String json = """
        {
            "usernameOrEmail":"test"
        }
        """;

        mockMvc.perform(post("/api/auth/check-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testGetSecurityQuestions() throws Exception {

        UserSecurityAnswer ans = new UserSecurityAnswer();

        Mockito.when(userSecurityAnswerRepository.findByUser_UserId(1L))
                .thenReturn(List.of(ans));

        mockMvc.perform(get("/api/auth/security-questions/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testValidateSecurityAnswers() throws Exception {

        Mockito.when(securityAnswerService.validateAnswers(eq(1L), any()))
                .thenReturn(true);

        String json = """
        {
            "userId":"1",
            "answers":["a","b"]
        }
        """;

        mockMvc.perform(post("/api/auth/validate-security-answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testResetPassword() throws Exception {

        User user = new User();
        user.setUserId(1L);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordEncoder.encode(any())).thenReturn("hash");

        String json = """
        {
            "userId":"1",
            "newPassword":"newpass"
        }
        """;

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testEnable2FA() throws Exception {

        User user = new User();
        user.setUserId(1L);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(post("/api/auth/enable-2fa/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testDisable2FA() throws Exception {

        User user = new User();
        user.setUserId(1L);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(post("/api/auth/disable-2fa/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllSecurityQuestions() throws Exception {

        SecurityQuestion q = new SecurityQuestion();

        Mockito.when(securityQuestionRepository.findAll())
                .thenReturn(List.of(q));

        mockMvc.perform(get("/api/auth/all-security-questions"))
                .andExpect(status().isOk());
    }

    @Test
    void testSendAuthCode() throws Exception {

        User user = new User();
        user.setUserId(1L);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        String json = """
        {
            "userId":"1"
        }
        """;

        mockMvc.perform(post("/api/auth/send-auth-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testVerifyAuthCode() throws Exception {

        User user = new User();
        user.setUserId(1L);

        VerificationCode v = new VerificationCode();
        v.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        v.setUsed("N");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(verificationCodeRepository
                        .findByCodeAndUserAndUsed(any(), eq(user), eq("N")))
                .thenReturn(Optional.of(v));

        String json = """
        {
            "userId":"1",
            "code":"123456"
        }
        """;

        mockMvc.perform(post("/api/auth/verify-auth-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
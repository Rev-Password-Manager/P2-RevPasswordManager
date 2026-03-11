
package com.passwordmanager.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passwordmanager.controller.VerificationController;
import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.service.UserService;
import com.passwordmanager.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VerificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VerificationService verificationService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser(){
        User user = new User();
        user.setUserId(1L);
        return user;
    }

    private VerificationCode mockCode(){
        VerificationCode code = new VerificationCode();
        code.setCodeId(1L);
        code.setCode("123456");
        code.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        return code;
    }

    @Test
    void testGenerateCode() throws Exception {

        User user = mockUser();
        VerificationCode code = mockCode();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(verificationService.generateCode(Mockito.eq(user),
                Mockito.anyString(), Mockito.any())).thenReturn(code);

        mockMvc.perform(post("/api/verification/1/generate"))
                .andExpect(status().isOk());
    }

    @Test
    void testVerifyCodeSuccess() throws Exception {

        User user = mockUser();
        VerificationCode code = mockCode();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(verificationService.verifyCode(user,"123456"))
                .thenReturn(Optional.of(code));

        mockMvc.perform(post("/api/verification/1/verify")
                        .param("code","123456"))
                .andExpect(status().isOk());
    }

    @Test
    void testVerifyCodeFail() throws Exception {

        User user = mockUser();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(verificationService.verifyCode(user,"111111"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/verification/1/verify")
                        .param("code","111111"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllCodes() throws Exception {

        User user = mockUser();
        VerificationCode code = mockCode();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(verificationService.getCodesByUser(user))
                .thenReturn(List.of(code));

        mockMvc.perform(get("/api/verification/1/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testMarkCodeUsed() throws Exception {

        VerificationCode code = mockCode();

        Mockito.when(verificationService.getCodeById(1L))
                .thenReturn(code);

        mockMvc.perform(put("/api/verification/1/mark-used"))
                .andExpect(status().isOk());
    }
}
package com.passwordmanager.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import com.passwordmanager.controller.PageController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PageController.class)
@AutoConfigureMockMvc(addFilters = false)   // 🔥 disables Spring Security
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testHomeRedirect() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void testDashboardPage() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    void testVerificationPage() throws Exception {
        mockMvc.perform(get("/verification"))
                .andExpect(status().isOk());
    }

    @Test
    void testSecurityQuestionsPage() throws Exception {
        mockMvc.perform(get("/security-questions"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateSecurityQuestionsPage() throws Exception {
        mockMvc.perform(get("/updatesecurityquestions"))
                .andExpect(status().isOk());
    }

    @Test
    void testForgotPasswordPage() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk());
    }

    @Test
    void testResetPasswordPage() throws Exception {
        mockMvc.perform(get("/reset-password"))
                .andExpect(status().isOk());
    }

    @Test
    void testBackupPage() throws Exception {
        mockMvc.perform(get("/backup"))
                .andExpect(status().isOk());
    }

    @Test
    void testNoAccessPage() throws Exception {
        mockMvc.perform(get("/no-access"))
                .andExpect(status().isOk());
    }

    @Test
    void testVaultPage() throws Exception {
        mockMvc.perform(get("/vault"))
                .andExpect(status().isOk());
    }

    @Test
    void testGeneratorPage() throws Exception {
        mockMvc.perform(get("/generator"))
                .andExpect(status().isOk());
    }

    @Test
    void testAuditPage() throws Exception {
        mockMvc.perform(get("/audit"))
                .andExpect(status().isOk());
    }

    @Test
    void testProfilePage() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk());
    }
}
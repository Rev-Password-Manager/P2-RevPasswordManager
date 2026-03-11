package com.passwordmanager.controllerTest;

import com.passwordmanager.controller.AuditController;
import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.PasswordVaultService;
import com.passwordmanager.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordVaultService vaultService;

    @Test
    void testGetAuditReport_Success() throws Exception {

        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);

        PasswordEntry entry = new PasswordEntry();

        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        Mockito.when(vaultService.getWeakPasswords(user)).thenReturn(List.of(entry));
        Mockito.when(vaultService.getReusedPasswords(user)).thenReturn(List.of(entry));
        Mockito.when(vaultService.getOldPasswords(user, 90)).thenReturn(List.of(entry));

        mockMvc.perform(get("/api/audit/{userId}", userId)
                        .param("daysThreshold", "90"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAuditReport_DefaultDaysThreshold() throws Exception {

        Long userId = 1L;

        User user = new User();
        user.setUserId(userId);

        PasswordEntry entry = new PasswordEntry();

        Mockito.when(userService.getUserById(userId)).thenReturn(user);
        Mockito.when(vaultService.getWeakPasswords(any())).thenReturn(List.of(entry));
        Mockito.when(vaultService.getReusedPasswords(any())).thenReturn(List.of());
        Mockito.when(vaultService.getOldPasswords(any(), eq(90))).thenReturn(List.of());

        mockMvc.perform(get("/api/audit/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAuditReport_UserNotFound() throws Exception {

        Long userId = 1L;

        Mockito.when(userService.getUserById(userId))
                .thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/api/audit/{userId}", userId))
                .andExpect(status().isInternalServerError());
    }
}
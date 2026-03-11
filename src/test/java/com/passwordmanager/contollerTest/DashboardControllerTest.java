package com.passwordmanager.controllerTest;

import com.passwordmanager.controller.DashboardController;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordVaultService passwordVaultService;

    @Test
    void testGetDashboard() throws Exception {

        User user = new User();
        user.setUserId(1L);

        PasswordEntry weakEntry = new PasswordEntry();
        weakEntry.setStrength("WEAK");
        weakEntry.setIsFavorite("N");

        PasswordEntry strongEntry = new PasswordEntry();
        strongEntry.setStrength("STRONG");
        strongEntry.setIsFavorite("Y");

        PasswordEntry veryStrongEntry = new PasswordEntry();
        veryStrongEntry.setStrength("VERY_STRONG");
        veryStrongEntry.setIsFavorite("Y");

        List<PasswordEntry> entries = List.of(weakEntry, strongEntry, veryStrongEntry);

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordVaultService.getAllEntriesByUser(user)).thenReturn(entries);

        mockMvc.perform(get("/api/dashboard/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.weak").value(1))
                .andExpect(jsonPath("$.strong").value(1))
                .andExpect(jsonPath("$.veryStrong").value(1))
                .andExpect(jsonPath("$.favorites").isArray());
    }
}
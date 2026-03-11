package com.passwordmanager.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passwordmanager.controller.ProfileController;
import com.passwordmanager.dto.UserDto;
import com.passwordmanager.entity.User;
import com.passwordmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser(){
        User user = new User();
        user.setUserId(1L);
        user.setFullName("Shiva");
        user.setEmail("shiva@gmail.com");
        user.setPhoneNumber("9999999999");
        user.setMasterPasswordHash("hashed");
        return user;
    }

    private UserDto mockUserDto(){
        UserDto dto = new UserDto();
        dto.setUserId(1L);
        dto.setFullName("Shiva");
        dto.setEmail("shiva@gmail.com");
        dto.setPhoneNumber("9999999999");
        return dto;
    }

    @Test
    void testGetProfile() throws Exception {

        User user = mockUser();
        UserDto dto = mockUserDto();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(userService.mapToDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/profile/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProfileSuccess() throws Exception {

        User user = mockUser();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        Map<String,String> payload = Map.of(
                "masterPassword","123",
                "fullName","New Shiva",
                "email","new@gmail.com",
                "phoneNumber","8888888888",
                "newPassword","456"
        );

        mockMvc.perform(put("/api/profile/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProfileWrongPassword() throws Exception {

        User user = mockUser();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Map<String,String> payload = Map.of(
                "masterPassword","wrong"
        );

        mockMvc.perform(put("/api/profile/1/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testChangePasswordSuccess() throws Exception {

        User user = mockUser();
        user.setMasterPasswordHash("123");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(put("/api/profile/1/change-password")
                        .param("currentPassword","123")
                        .param("newPassword","456"))
                .andExpect(status().isOk());
    }

    @Test
    void testChangePasswordWrongCurrentPassword() throws Exception {

        User user = mockUser();
        user.setMasterPasswordHash("123");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(put("/api/profile/1/change-password")
                        .param("currentPassword","wrong")
                        .param("newPassword","456"))
                .andExpect(status().isUnauthorized());
    }
}
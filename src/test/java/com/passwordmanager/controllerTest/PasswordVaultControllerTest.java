package com.passwordmanager.controllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passwordmanager.controller.PasswordVaultController;
import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.security.EncryptionService;
import com.passwordmanager.service.PasswordVaultService;
import com.passwordmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordVaultController.class)
@AutoConfigureMockMvc(addFilters = false)
class PasswordVaultControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordVaultService passwordVaultService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EncryptionService encryptionService;

    @Autowired
    private ObjectMapper objectMapper;

    private User mockUser() {
        User user = new User();
        user.setUserId(1L);
        user.setMasterPasswordHash("hashed");
        return user;
    }

    private PasswordEntry mockEntry() {
        PasswordEntry entry = new PasswordEntry();
        entry.setEntryId(10L);
        entry.setAccountName("gmail");
        entry.setWebsiteUrl("gmail.com");
        entry.setEncryptedPassword("enc123");
        entry.setIsFavorite("N");
        return entry;
    }

    @Test
    void testAddPassword() throws Exception {

        User user = mockUser();
        PasswordEntry entry = mockEntry();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordVaultService.addPasswordEntry(any())).thenReturn(entry);

        mockMvc.perform(post("/api/passwords/1/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAllPasswords() throws Exception {

        User user = mockUser();
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordVaultService.getAllEntriesByUser(user))
                .thenReturn(List.of(mockEntry()));

        mockMvc.perform(get("/api/passwords/1/all"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetFavorites() throws Exception {

        User user = mockUser();
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordVaultService.getFavoriteEntries(user))
                .thenReturn(List.of(mockEntry()));

        mockMvc.perform(get("/api/passwords/1/favorites"))
                .andExpect(status().isOk());
    }

    @Test
    void testWeakCount() throws Exception {

        User user = mockUser();
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordVaultService.countWeakPasswords(user)).thenReturn(2L);

        mockMvc.perform(get("/api/passwords/1/weak-count"))
                .andExpect(status().isOk());
    }

    @Test
    void testStrongCount() throws Exception {

        User user = mockUser();
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordVaultService.countStrongPasswords(user)).thenReturn(3L);

        mockMvc.perform(get("/api/passwords/1/strong-count"))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePasswordSuccess() throws Exception {

        User user = mockUser();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        mockMvc.perform(post("/api/passwords/1/delete/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("masterPassword","123"))))
                .andExpect(status().isOk());
    }

    @Test
    void testDeletePasswordWrongMasterPassword() throws Exception {

        User user = mockUser();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/passwords/1/delete/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("masterPassword","wrong"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testVerifyAndReturnPassword() throws Exception {

        User user = mockUser();
        PasswordEntry entry = mockEntry();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        Mockito.when(passwordVaultService.getEntryByIdAndUser(10L, user))
                .thenReturn(Optional.of(entry));
        Mockito.when(encryptionService.decrypt("enc123")).thenReturn("realPassword");

        mockMvc.perform(post("/api/passwords/1/verify/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("masterPassword","123"))))
                .andExpect(status().isOk());
    }

    @Test
    void testGetEntry() throws Exception {

        User user = mockUser();
        PasswordEntry entry = mockEntry();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordVaultService.getEntryByIdAndUser(10L,user))
                .thenReturn(Optional.of(entry));

        mockMvc.perform(get("/api/passwords/1/get/10"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdatePassword() throws Exception {

        User user = mockUser();
        PasswordEntry entry = mockEntry();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordVaultService.getEntryByIdAndUser(10L,user))
                .thenReturn(Optional.of(entry));

        mockMvc.perform(put("/api/passwords/1/update/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isOk());
    }

    @Test
    void testExportVault() throws Exception {

        User user = mockUser();
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(passwordVaultService.exportVault(user))
                .thenReturn("data".getBytes());

        mockMvc.perform(get("/api/passwords/1/export"))
                .andExpect(status().isOk());
    }

    @Test
    void testImportVault() throws Exception {

        User user = mockUser();
        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "vault.json",
                "application/json",
                "testdata".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/passwords/1/import")
                        .file(file))
                .andExpect(status().isOk());
    }
}
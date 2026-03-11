package com.passwordmanager.service.impl;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.PasswordEntryRepository;
import com.passwordmanager.security.EncryptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordVaultServiceImplTest {

    @Mock
    private PasswordEntryRepository repository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private PasswordVaultServiceImpl service;

    private User createUser() {
        User user = new User();
        user.setUserId(1L);
        return user;
    }

    private PasswordEntry createEntry() {
        PasswordEntry entry = new PasswordEntry();
        entry.setAccountName("gmail");
        entry.setEncryptedPassword("Password@123");
        entry.setStrength("STRONG");
        return entry;
    }

    @Test
    void testAddPasswordEntry() {

        PasswordEntry entry = createEntry();

        when(encryptionService.encrypt("Password@123"))
                .thenReturn("encryptedValue");

        when(repository.save(any()))
                .thenReturn(entry);

        PasswordEntry result = service.addPasswordEntry(entry);

        assertNotNull(result);
        verify(repository, times(1)).save(any());
    }

    @Test
    void testUpdatePasswordEntry() {

        PasswordEntry entry = createEntry();

        when(encryptionService.encrypt("Password@123"))
                .thenReturn("encryptedValue");

        when(repository.save(any()))
                .thenReturn(entry);

        PasswordEntry result = service.updatePasswordEntry(entry);

        assertNotNull(result);
        verify(repository).save(any());
    }

    @Test
    void testDeletePasswordEntry() {

        service.deletePasswordEntry(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void testGetAllEntriesByUser() {

        User user = createUser();

        PasswordEntry entry = createEntry();
        entry.setEncryptedPassword("encrypted");

        when(repository.findByUser(user))
                .thenReturn(List.of(entry));

        when(encryptionService.decrypt("encrypted"))
                .thenReturn("Password@123");

        List<PasswordEntry> result =
                service.getAllEntriesByUser(user);

        assertEquals(1, result.size());
    }

    @Test
    void testGetEntryByIdAndUser() {

        User user = createUser();
        PasswordEntry entry = createEntry();

        when(repository.findByEntryIdAndUser(1L, user))
                .thenReturn(Optional.of(entry));

        Optional<PasswordEntry> result =
                service.getEntryByIdAndUser(1L, user);

        assertTrue(result.isPresent());
    }

    @Test
    void testGetFavoriteEntries() {

        User user = createUser();

        when(repository.findByUserAndIsFavorite(user, "Y"))
                .thenReturn(List.of(createEntry()));

        List<PasswordEntry> result =
                service.getFavoriteEntries(user);

        assertFalse(result.isEmpty());
    }

    @Test
    void testCountWeakPasswords() {

        User user = createUser();

        when(repository.findByUserAndStrength(user, "WEAK"))
                .thenReturn(List.of(createEntry()));

        long result = service.countWeakPasswords(user);

        assertEquals(1, result);
    }

    @Test
    void testCountStrongPasswords() {

        User user = createUser();

        when(repository.findByUserAndStrength(user, "STRONG"))
                .thenReturn(List.of(createEntry()));

        when(repository.findByUserAndStrength(user, "VERY_STRONG"))
                .thenReturn(List.of(createEntry()));

        long result = service.countStrongPasswords(user);

        assertEquals(2, result);
    }

    @Test
    void testIsWeakPassword() {

        boolean result = service.isWeakPassword("abc");

        assertTrue(result);
    }

    @Test
    void testIsStrongPassword() {

        boolean result = service.isStrongPassword("Password@1");

        assertTrue(result);
    }

    @Test
    void testCalculateStrengthVeryStrong() {

        String result =
                service.calculateStrength("Password@1234");

        assertEquals("VERY_STRONG", result);
    }

    @Test
    void testCalculateStrengthWeak() {

        String result =
                service.calculateStrength("123");

        assertEquals("WEAK", result);
    }

    @Test
    void testGetWeakPasswords() {

        User user = createUser();

        when(repository.findByUserAndStrength(user, "WEAK"))
                .thenReturn(List.of(createEntry()));

        List<PasswordEntry> result =
                service.getWeakPasswords(user);

        assertEquals(1, result.size());
    }

    @Test
    void testGetReusedPasswords() {

        User user = createUser();

        PasswordEntry e1 = createEntry();
        e1.setEncryptedPassword("same");

        PasswordEntry e2 = createEntry();
        e2.setEncryptedPassword("same");

        when(repository.findByUser(user))
                .thenReturn(List.of(e1, e2));

        List<PasswordEntry> result =
                service.getReusedPasswords(user);

        assertEquals(2, result.size());
    }

    @Test
    void testExportVault() {

        User user = createUser();

        when(repository.findByUser(user))
                .thenReturn(List.of(createEntry()));

        when(encryptionService.encrypt(any()))
                .thenReturn("encryptedData");

        byte[] result = service.exportVault(user);

        assertNotNull(result);
    }

    @Test
    void testImportVault() throws Exception {

        User user = createUser();

        String json = """
                [{
                  "accountName":"gmail",
                  "usernameEmail":"test@gmail.com",
                  "encryptedPassword":"abc"
                }]
                """;

        when(encryptionService.decryptFromBytes(any()))
                .thenReturn(json);

        when(repository.findByUserAndAccountNameAndUsernameEmail(any(), any(), any()))
                .thenReturn(Optional.empty());

        service.importVault(user, "data".getBytes());

        verify(repository, atLeastOnce()).save(any());
    }
}
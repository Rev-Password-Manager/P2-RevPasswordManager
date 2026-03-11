package com.passwordmanager.repository;

import com.passwordmanager.entity.PasswordEntry;
import com.passwordmanager.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PasswordEntryRepositoryTest {

    @Autowired
    private PasswordEntryRepository repository;

    @Autowired
    private UserRepository userRepository;


    private User createUser() {

        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@mail.com");
        user.setFullName("Test User");
        user.setMasterPasswordHash("123456");
        user.setPhoneNumber("9999999999");

        return userRepository.save(user);
    }


    private PasswordEntry createEntry(User user) {

        PasswordEntry entry = new PasswordEntry();
        entry.setAccountName("Google");
        entry.setWebsiteUrl("google.com");
        entry.setUsernameEmail("test@gmail.com");
        entry.setCategory("EMAIL");
        entry.setStrength("STRONG");
        entry.setEncryptedPassword("encrypted123");
        entry.setIsFavorite("Y");
        entry.setDateAdded(LocalDateTime.now());
        entry.setDateModified(LocalDateTime.now());
        entry.setUser(user);

        return repository.save(entry);
    }


    @Test
    void testFindByUser() {

        User user = createUser();
        createEntry(user);

        List<PasswordEntry> result = repository.findByUser(user);

        assertFalse(result.isEmpty());
    }


    @Test
    void testFindByEntryIdAndUser() {

        User user = createUser();
        PasswordEntry entry = createEntry(user);

        Optional<PasswordEntry> result =
                repository.findByEntryIdAndUser(entry.getEntryId(), user);

        assertTrue(result.isPresent());
    }


    @Test
    void testFindByUserAndCategory() {

        User user = createUser();
        createEntry(user);

        List<PasswordEntry> result =
                repository.findByUserAndCategory(user,"EMAIL");

        assertFalse(result.isEmpty());
    }


    @Test
    void testFindByUserAndIsFavorite() {

        User user = createUser();
        createEntry(user);

        List<PasswordEntry> result =
                repository.findByUserAndIsFavorite(user,"Y");

        assertFalse(result.isEmpty());
    }


    @Test
    void testFindByUserOrderByAccountNameAsc() {

        User user = createUser();
        createEntry(user);

        List<PasswordEntry> result =
                repository.findByUserOrderByAccountNameAsc(user);

        assertFalse(result.isEmpty());
    }


    @Test
    void testFindByUserOrderByAccountNameDesc() {

        User user = createUser();
        createEntry(user);

        List<PasswordEntry> result =
                repository.findByUserOrderByAccountNameDesc(user);

        assertFalse(result.isEmpty());
    }


    @Test
    void testFindByUserOrderByDateAddedDesc() {

        User user = createUser();
        createEntry(user);

        List<PasswordEntry> result =
                repository.findByUserOrderByDateAddedDesc(user);

        assertFalse(result.isEmpty());
    }


    @Test
    void testFindByUserOrderByDateModifiedDesc() {

        User user = createUser();
        createEntry(user);

        List<PasswordEntry> result =
                repository.findByUserOrderByDateModifiedDesc(user);

        assertFalse(result.isEmpty());
    }


    @Test
    void testFindByUserAndStrength() {

        User user = createUser();
        createEntry(user);

        List<PasswordEntry> result =
                repository.findByUserAndStrength(user,"STRONG");

        assertFalse(result.isEmpty());
    }


    @Test
    void testFindByUserAndAccountNameAndUsernameEmail() {

        User user = createUser();
        createEntry(user);

        Optional<PasswordEntry> result =
                repository.findByUserAndAccountNameAndUsernameEmail(
                        user,"Google","test@gmail.com");

        assertTrue(result.isPresent());
    }

}
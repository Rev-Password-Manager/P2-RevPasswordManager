package com.passwordmanager.repository;

import com.passwordmanager.entity.UserSecurityAnswer;
import com.passwordmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserSecurityAnswerRepository extends JpaRepository<UserSecurityAnswer, Long> {
    // ✅ Option 1: by User object
    List<UserSecurityAnswer> findByUser(User user);

    // ✅ Option 2: by userId directly
    List<UserSecurityAnswer> findByUser_UserId(Long userId);
}

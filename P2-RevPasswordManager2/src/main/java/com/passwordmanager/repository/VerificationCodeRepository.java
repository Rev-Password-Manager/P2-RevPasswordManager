package com.passwordmanager.repository;

import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    List<VerificationCode> findByUser(User user);

    Optional<VerificationCode> findByCodeAndUserAndUsed(String code, User user, String used);
}
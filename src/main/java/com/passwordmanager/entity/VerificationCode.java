package com.passwordmanager.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;

// ======================
// VerificationCode Entity
// Stores codes for authentication/verification purposes
// ======================
@Entity
@Data
@Table(name = "VERIFICATION_CODES")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Long codeId; // primary key

    private String code; // 6-digit code

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime; // expiration time of the code

    @Column(name = "used", length = 1, columnDefinition = "CHAR(1)")
    private String used = "N"; // "Y" if used, "N" otherwise

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore // prevents infinite recursion when serializing
    private User user; // associated user

    public VerificationCode() {}
}
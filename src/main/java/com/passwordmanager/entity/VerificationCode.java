package com.passwordmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Data
@Table(name = "VERIFICATION_CODES")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Long codeId;

    private String code;

    @Column(name = "expiry_time")
    private LocalDateTime expiryTime;

    @Column(name = "used", length = 1, columnDefinition = "CHAR(1)")
    private String used = "N";

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public VerificationCode() {}

    
}
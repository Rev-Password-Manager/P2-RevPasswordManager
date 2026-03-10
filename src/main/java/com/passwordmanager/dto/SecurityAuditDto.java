package com.passwordmanager.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class SecurityAuditDto {
    private long totalPasswords;
    private long weakPasswords;
    private long strongPasswords;
}
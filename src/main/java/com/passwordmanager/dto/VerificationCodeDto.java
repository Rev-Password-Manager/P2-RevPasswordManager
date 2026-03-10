package com.passwordmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationCodeDto {

    private Long userId;
    private String code;

}
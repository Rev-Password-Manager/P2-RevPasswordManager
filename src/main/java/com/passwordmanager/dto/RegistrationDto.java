package com.passwordmanager.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDto {

    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String password;

    private List<Long> questionIds;
    private List<String> answers;
}
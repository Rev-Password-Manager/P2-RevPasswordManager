package com.passwordmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String twoFactorEnabled;
}
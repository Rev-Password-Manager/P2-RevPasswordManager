package com.passwordmanager.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordGeneratorDto {

    private int length = 12;        // default 12 chars
    private boolean includeUppercase = true;
    private boolean includeLowercase = true;
    private boolean includeNumbers = true;
    private boolean includeSpecial = true;
    private boolean excludeSimilar = true; // like 0, O, l, 1
    private String generatedPassword;      // result after generation
}
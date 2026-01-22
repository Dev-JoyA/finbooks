package com.whytelabeltech.finbooks.app.password.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordDto {
    private String email;
    private String otp;
    private String newPassword;
    private String confirmPassword;
}

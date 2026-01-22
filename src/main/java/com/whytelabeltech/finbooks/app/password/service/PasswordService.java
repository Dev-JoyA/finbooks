package com.whytelabeltech.finbooks.app.password.service;

import com.whytelabeltech.finbooks.app.password.dto.AttemptForgotPasswordDto;
import com.whytelabeltech.finbooks.app.password.dto.ForgotPasswordDto;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;

public interface PasswordService {
    MessageResponse forceResetPassword (AttemptForgotPasswordDto attemptForgotPasswordDto);

    MessageResponse validateOtp(String otp);

    MessageResponse forgotPassword (ForgotPasswordDto forgotPasswordDto);
}

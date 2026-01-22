package com.whytelabeltech.finbooks.app.password.controller;

import com.whytelabeltech.finbooks.app.password.dto.AttemptForgotPasswordDto;
import com.whytelabeltech.finbooks.app.password.dto.ForgotPasswordDto;
import com.whytelabeltech.finbooks.app.password.service.PasswordService;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("password")
public class PasswordController {

    @Autowired
    PasswordService passwordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> attemptForgotPassword(@RequestBody AttemptForgotPasswordDto attemptForgotPasswordDto){
        MessageResponse passwordResponseDto = passwordService.forceResetPassword(attemptForgotPasswordDto);
        return ResponseEntity.ok(passwordResponseDto);

    }

    @GetMapping("/validate-otp")
    public ResponseEntity<?> validateOtp(@RequestParam String otp){
        MessageResponse passwordResponseDto = passwordService.validateOtp(otp);
        return ResponseEntity.ok(passwordResponseDto);
    }

    @PutMapping("/new-password")
    public ResponseEntity<?> effectForgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto){
        MessageResponse passwordResponseDto = passwordService.forgotPassword(forgotPasswordDto);
        return ResponseEntity.accepted().body(passwordResponseDto);
    }

}

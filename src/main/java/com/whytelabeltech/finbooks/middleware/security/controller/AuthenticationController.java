package com.whytelabeltech.finbooks.middleware.security.controller;

import com.whytelabeltech.finbooks.middleware.security.dto.LoginRequest;
import com.whytelabeltech.finbooks.middleware.security.dto.LoginResponse;
import com.whytelabeltech.finbooks.middleware.security.dto.RefreshTokenDto;
import com.whytelabeltech.finbooks.middleware.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("login")
    public ResponseEntity<LoginResponse> authenticate(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse authResponse = service.login(request);
        response.setHeader("Refresh-Token", authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestBody RefreshTokenDto token,
            HttpServletResponse response
    ) {
        LoginResponse authResponse = service.refreshToken(token);
        response.setHeader("Refresh-Token", authResponse.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }
}

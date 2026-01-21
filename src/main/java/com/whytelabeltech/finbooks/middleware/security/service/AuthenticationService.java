package com.whytelabeltech.finbooks.middleware.security.service;

import com.whytelabeltech.finbooks.app.email.service.MailManager;
import com.whytelabeltech.finbooks.app.email.util.EmailBuilderRequest;
import com.whytelabeltech.finbooks.app.user.dto.request.UserRequestDto;
import com.whytelabeltech.finbooks.app.user.dto.response.UserResponseDto;
import com.whytelabeltech.finbooks.app.user.model.Role;
import com.whytelabeltech.finbooks.app.user.repository.UserRepository;
import com.whytelabeltech.finbooks.app.user.model.User;
import com.whytelabeltech.finbooks.middleware.security.dto.LoginRequest;
import com.whytelabeltech.finbooks.middleware.security.dto.LoginResponse;
import com.whytelabeltech.finbooks.middleware.security.dto.RefreshTokenDto;
import com.whytelabeltech.finbooks.middleware.security.model.CustomUserDetail;
import com.whytelabeltech.finbooks.middleware.exception.error.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthenticationService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        if (request == null) {
            throw new AuthenticationException("Invalid authentication request");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException("No User with that email" + request.getUsername()));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new AuthenticationException("Incorrect password");
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }


        String accessToken = jwtService.generateToken(new CustomUserDetail(user));
        String refreshToken = jwtService.generateRefreshToken(new CustomUserDetail(user));

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

    }

    public LoginResponse refreshToken(RefreshTokenDto token) {
        String refreshToken = token.getRefreshToken();
        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("No User with that username"));

        if (jwtService.isTokenValid(refreshToken, new CustomUserDetail(user))) {
            String newAccessToken = jwtService.generateToken(new CustomUserDetail(user));

            return LoginResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole())
                    .build();
        }
        throw new AuthenticationException("Invalid refresh token");
    }


}

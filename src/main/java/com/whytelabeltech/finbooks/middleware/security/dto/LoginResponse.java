package com.whytelabeltech.finbooks.middleware.security.dto;

import com.whytelabeltech.finbooks.app.user.model.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
    private String email;
    private Role role;
}
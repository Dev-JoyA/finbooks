package com.whytelabeltech.finbooks.app.user.dto.response;

import com.whytelabeltech.finbooks.app.user.model.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private Role role;
}

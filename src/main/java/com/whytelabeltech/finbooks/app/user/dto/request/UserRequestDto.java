package com.whytelabeltech.finbooks.app.user.dto.request;

import com.whytelabeltech.finbooks.app.user.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private String username;
    private String email;
    private String password;
    private Role role;
}

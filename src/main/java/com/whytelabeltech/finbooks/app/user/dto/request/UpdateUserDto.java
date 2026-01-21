package com.whytelabeltech.finbooks.app.user.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {
    private String username;
    private String email;
    private String password;
}

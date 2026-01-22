package com.whytelabeltech.finbooks.app.author.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorRequestDto {

    private String name;

    @Email
    private String email;

    @Size(max=500)
    private String bio;
}

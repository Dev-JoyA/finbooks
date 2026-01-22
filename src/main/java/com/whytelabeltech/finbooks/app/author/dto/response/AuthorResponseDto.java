package com.whytelabeltech.finbooks.app.author.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AuthorResponseDto {
    private Long id;
    private String name;
    private String email;
    private String bio;
    private List<BookTitleResponse> books = new ArrayList<>();
}

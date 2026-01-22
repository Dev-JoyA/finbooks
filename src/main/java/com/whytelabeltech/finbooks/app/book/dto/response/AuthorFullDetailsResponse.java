package com.whytelabeltech.finbooks.app.book.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorFullDetailsResponse {
    private Long id;
    private String name;
    private String email;
    private String bio;
}

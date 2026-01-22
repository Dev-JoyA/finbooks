package com.whytelabeltech.finbooks.app.author.dto.response;

import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookAuthorDto {
    private Long id;
    private String name;
    private String email;

    private String bio;
    private List<BookDetailsResponse> books = new ArrayList<>();
}

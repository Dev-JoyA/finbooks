package com.whytelabeltech.finbooks.app.author.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookDetailsResponse {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishedDate;
}

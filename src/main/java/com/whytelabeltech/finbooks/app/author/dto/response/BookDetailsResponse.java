package com.whytelabeltech.finbooks.app.author.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookDetailsResponse {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishedDate;
}

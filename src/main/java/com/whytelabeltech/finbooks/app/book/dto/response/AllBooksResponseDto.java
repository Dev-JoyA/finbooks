package com.whytelabeltech.finbooks.app.book.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AllBooksResponseDto {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishedDate;
    private AuthorDetailsResponse author;
    private List<CategoryDetailResponse> categories = new ArrayList<>() ;
    private Integer rating;
}


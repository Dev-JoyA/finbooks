package com.whytelabeltech.finbooks.app.book.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class AllBooksRequestDto {
    private Long authorId;
    private Long categoryId;
    private Double ratingMin;
    private Double ratingMax;
    private LocalDate publishedStart;
    private LocalDate publishedEnd;
}

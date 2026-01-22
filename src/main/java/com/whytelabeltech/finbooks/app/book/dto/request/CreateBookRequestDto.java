package com.whytelabeltech.finbooks.app.book.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateBookRequestDto {
    private String title;
    private String isbn;
    private LocalDate publishedDate;
    private Long authorId;
    private List<Long> categoryIds = new ArrayList<>();
}

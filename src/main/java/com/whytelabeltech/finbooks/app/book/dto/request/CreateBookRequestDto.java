package com.whytelabeltech.finbooks.app.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateBookRequestDto {

    @NotNull
    private String title;

    @NotNull
    private String isbn;

    @NotNull
    private LocalDate publishedDate;

    @NotNull
    private Long authorId;

    @NotNull
    private List<Long> categoryIds = new ArrayList<>();
}

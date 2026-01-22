package com.whytelabeltech.finbooks.app.book.dto.response;

import com.whytelabeltech.finbooks.app.book.dto.response.review.ReviewResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BookResponseDto {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishedDate;
    private List<AuthorFullDetailsResponse> authors = new ArrayList<>();
    private List<CategoryDetailResponse> categories = new ArrayList<>();
    private Integer rating;
    private List<ReviewResponseDto> reviews = new ArrayList<>();

}

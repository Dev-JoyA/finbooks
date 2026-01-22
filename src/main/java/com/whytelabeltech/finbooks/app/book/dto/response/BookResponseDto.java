package com.whytelabeltech.finbooks.app.book.dto.response;

import com.whytelabeltech.finbooks.app.book.dto.response.review.ReviewsDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookResponseDto {
    private Long id;
    private String title;
    private String isbn;
    private LocalDate publishedDate;
    private AuthorFullDetailsResponse author;
    private List<CategoryDetailResponse> categories = new ArrayList<>();
    private Integer rating;
    private List<ReviewsDto> reviews = new ArrayList<>();

}

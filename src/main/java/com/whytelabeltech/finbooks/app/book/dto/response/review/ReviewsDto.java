package com.whytelabeltech.finbooks.app.book.dto.response.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewsDto {
    private Long id;
    private UserReviewResponse user;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

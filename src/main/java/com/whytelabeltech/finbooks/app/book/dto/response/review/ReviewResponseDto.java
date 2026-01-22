package com.whytelabeltech.finbooks.app.book.dto.response.review;

import java.time.LocalDateTime;

public class ReviewResponseDto {
    private Long id;
    private UserReviewResponse user;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

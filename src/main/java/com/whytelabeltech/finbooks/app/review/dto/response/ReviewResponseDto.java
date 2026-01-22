package com.whytelabeltech.finbooks.app.review.dto.response;

import java.time.LocalDateTime;

public class ReviewResponseDto {
    private Long id;

    private Integer rating;

    private ReviewUserDto user;

    private ReviewBookDto book;

    private LocalDateTime createdAt;

}

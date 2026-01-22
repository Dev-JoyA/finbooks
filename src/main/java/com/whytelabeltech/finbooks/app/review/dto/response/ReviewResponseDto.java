package com.whytelabeltech.finbooks.app.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {
    private Long id;

    private Integer rating;

    private ReviewUserDto user;

    private ReviewBookDto book;

    private LocalDateTime createdAt;

}

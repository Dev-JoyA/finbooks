package com.whytelabeltech.finbooks.app.review.service;

import com.whytelabeltech.finbooks.app.review.dto.requests.UpdateReviewDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewResponseDto;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;

public interface ReviewService {
    ReviewResponseDto updateReview (Long id, UpdateReviewDto request);
    MessageResponse deleteReview (Long id);
}

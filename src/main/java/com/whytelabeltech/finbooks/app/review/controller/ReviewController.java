package com.whytelabeltech.finbooks.app.review.controller;

import com.whytelabeltech.finbooks.app.review.dto.requests.UpdateReviewDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewResponseDto;
import com.whytelabeltech.finbooks.app.review.service.ReviewService;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/reviews")
@RestController
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @PutMapping("{id}")
    ResponseEntity<ReviewResponseDto> update (@PathVariable Long id,
                                              @RequestBody UpdateReviewDto request){
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("{id}")
    ResponseEntity<MessageResponse> delete (@PathVariable Long id){
        return ResponseEntity.ok(reviewService.deleteReview(id));
    }
}

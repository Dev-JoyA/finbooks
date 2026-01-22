package com.whytelabeltech.finbooks.app.review.service;

import com.whytelabeltech.finbooks.app.review.dto.requests.UpdateReviewDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewBookDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewResponseDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewUserDto;
import com.whytelabeltech.finbooks.app.review.model.Review;
import com.whytelabeltech.finbooks.app.review.repository.ReviewRepository;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.middleware.exception.error.ReviewException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService{

    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public ReviewResponseDto updateReview (Long id, UpdateReviewDto request){
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewException("Review cannot be found"));

        if(request.getComment() != null){
            review.setComment(review.getComment());
        }
        if(request.getRating() != null){
            review.setRating(review.getRating());
        }

        reviewRepository.save(review);

        ReviewResponseDto dto = new ReviewResponseDto();

        ReviewUserDto user = new ReviewUserDto();
        user.setId(review.getUser().getId());
        user.setUsername(review.getUser().getUsername());

        ReviewBookDto book = new ReviewBookDto();
        book.setTitle(review.getBook().getTitle());
        book.setId(review.getBook().getId());

        dto.setRating(review.getRating());
        dto.setId(review.getId());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUser(user);
        dto.setBook(book);

        return dto;
    }

    @Override
    public MessageResponse deleteReview (Long id){
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewException("Review cannot be found"));
        reviewRepository.delete(review);

        return new MessageResponse("Review Deleted Successfully");
    }
}

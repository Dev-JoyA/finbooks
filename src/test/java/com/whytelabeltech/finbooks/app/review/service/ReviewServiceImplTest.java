package com.whytelabeltech.finbooks.app.review.service;


import com.whytelabeltech.finbooks.app.book.model.Book;
import com.whytelabeltech.finbooks.app.book.repository.BookRepository;
import com.whytelabeltech.finbooks.app.review.dto.requests.UpdateReviewDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewResponseDto;
import com.whytelabeltech.finbooks.app.review.model.Review;
import com.whytelabeltech.finbooks.app.review.repository.ReviewRepository;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.app.user.model.User;
import com.whytelabeltech.finbooks.app.user.repository.UserRepository;
import com.whytelabeltech.finbooks.middleware.exception.error.ReviewException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Book book;
    private User user;
    private Review review;

    @BeforeEach
    void setUp() {

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setPublishedDate(LocalDate.of(2023, 1, 1));

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        review = new Review();
        review.setId(1L);
        review.setBook(book);
        review.setUser(user);
        review.setRating(5);
        review.setComment("Great Book!");
        review.setCreatedAt(LocalDateTime.now());

        book.setReviews(new ArrayList<>());
        book.getReviews().add(review);

    }


    @Test
    void testDeleteReview_Success() {
        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        doNothing().when(reviewRepository).delete(review);

        MessageResponse response = reviewService.deleteReview(review.getId());

        assertThat(response.getMessage()).isEqualTo("Review Deleted Successfully");
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void testDeleteReview_NotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(99L))
                .isInstanceOf(ReviewException.class)
                .hasMessage("Review cannot be found");
    }

    @Test
    void testUpdateReview_Success() {
        UpdateReviewDto request = new UpdateReviewDto();
        request.setComment("Updated Comment");
        request.setRating(4);

        when(reviewRepository.findById(review.getId())).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponseDto response = reviewService.updateReview(review.getId(), request);

        assertThat(response.getId()).isEqualTo(review.getId());
        assertThat(response.getComment()).isEqualTo("Updated Comment");
        assertThat(response.getRating()).isEqualTo(4);
        assertThat(response.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getBook().getTitle()).isEqualTo(book.getTitle());
        assertThat(response.getCreatedAt()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));



        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    void testUpdateReview_ReviewNotFound() {
        UpdateReviewDto request = new UpdateReviewDto();
        request.setComment("Updated Comment");

        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(99L, request))
                .isInstanceOf(ReviewException.class)
                .hasMessage("Review cannot be found");
    }


}


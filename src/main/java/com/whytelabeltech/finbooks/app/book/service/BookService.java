package com.whytelabeltech.finbooks.app.book.service;

import com.whytelabeltech.finbooks.app.book.dto.request.CreateBookRequestDto;
import com.whytelabeltech.finbooks.app.book.dto.request.UpdateBookDto;
import com.whytelabeltech.finbooks.app.book.dto.response.AllBooksResponseDto;
import com.whytelabeltech.finbooks.app.book.dto.response.BookResponseDto;
import com.whytelabeltech.finbooks.app.review.dto.requests.ReviewRequestDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewResponseDto;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.app.shared.dto.PaginatedResponse;

import java.time.LocalDate;
import java.util.List;

public interface BookService {
    PaginatedResponse<AllBooksResponseDto> getAll(
            int page,
            int size,
            Long authorId,
            Long categoryId,
            Double ratingMin,
            Double ratingMax,
            LocalDate publishedStart,
            LocalDate publishedEnd,
            String sortBy
    );

    BookResponseDto getById (Long id);
    AllBooksResponseDto createBook (CreateBookRequestDto request);
    AllBooksResponseDto updateBook (Long id, UpdateBookDto request);
    MessageResponse deleteBook (Long id);
    ReviewResponseDto createReview (Long id, ReviewRequestDto request);
    List<ReviewResponseDto> getBookReview (Long id);
}

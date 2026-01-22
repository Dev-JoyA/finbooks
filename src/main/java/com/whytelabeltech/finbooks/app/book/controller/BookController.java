package com.whytelabeltech.finbooks.app.book.controller;

import com.whytelabeltech.finbooks.app.book.dto.request.CreateBookRequestDto;
import com.whytelabeltech.finbooks.app.book.dto.request.UpdateBookDto;
import com.whytelabeltech.finbooks.app.book.dto.response.AllBooksResponseDto;
import com.whytelabeltech.finbooks.app.book.dto.response.BookResponseDto;
import com.whytelabeltech.finbooks.app.book.service.BookService;
import com.whytelabeltech.finbooks.app.review.dto.requests.ReviewRequestDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewResponseDto;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.app.shared.dto.PaginatedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    @Autowired
    BookService bookService;

    @GetMapping()
    ResponseEntity<PaginatedResponse<AllBooksResponseDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double ratingMin,
            @RequestParam(required = false) Double ratingMax,
            @RequestParam(required = false) LocalDate publishedStart,
            @RequestParam(required = false) LocalDate publishedEnd,
            @RequestParam(required = false) String sortBy
    ){
        return ResponseEntity.ok(bookService.getAll(page, size,
                authorId, categoryId,
                ratingMin, ratingMax,
                publishedStart, publishedEnd,
                sortBy));
    }

    @GetMapping("{id}")
    ResponseEntity<BookResponseDto> getOne (@PathVariable Long id){
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<AllBooksResponseDto> create (@RequestBody CreateBookRequestDto request){
        return ResponseEntity.ok(bookService.createBook(request));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<AllBooksResponseDto> update (@PathVariable Long id,
                                                @RequestBody UpdateBookDto request){
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<MessageResponse> delete (@PathVariable Long id){
        return ResponseEntity.ok(bookService.deleteBook(id));
    }

    @PostMapping("{bookId}/reviews")
    ResponseEntity<ReviewResponseDto> createReview (@PathVariable Long bookId,
                                                    @RequestBody ReviewRequestDto request){
        return ResponseEntity.ok(bookService.createReview(bookId, request));
    }

    @GetMapping("{bookId}/reviews")
    ResponseEntity<List<ReviewResponseDto>> getBookReview (@PathVariable Long bookId){
        return ResponseEntity.ok(bookService.getBookReview(bookId));
    }

}

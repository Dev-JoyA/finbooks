package com.whytelabeltech.finbooks.app.book.service;

import com.whytelabeltech.finbooks.app.author.model.Author;
import com.whytelabeltech.finbooks.app.author.repository.AuthorRepository;
import com.whytelabeltech.finbooks.app.book.dto.request.CreateBookRequestDto;
import com.whytelabeltech.finbooks.app.book.dto.request.UpdateBookDto;
import com.whytelabeltech.finbooks.app.book.dto.response.AllBooksResponseDto;
import com.whytelabeltech.finbooks.app.book.dto.response.BookResponseDto;
import com.whytelabeltech.finbooks.app.book.model.Book;
import com.whytelabeltech.finbooks.app.book.repository.BookRepository;
import com.whytelabeltech.finbooks.app.category.model.Category;
import com.whytelabeltech.finbooks.app.category.repository.CategoryRepository;
import com.whytelabeltech.finbooks.app.review.dto.requests.ReviewRequestDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewResponseDto;
import com.whytelabeltech.finbooks.app.review.model.Review;
import com.whytelabeltech.finbooks.app.review.repository.ReviewRepository;
import com.whytelabeltech.finbooks.app.user.model.User;
import com.whytelabeltech.finbooks.app.user.repository.UserRepository;
import com.whytelabeltech.finbooks.middleware.exception.error.BookException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author author;
    private Category category;
    private Book book;
    private User user;
    private Review review;

    @BeforeEach
    void setup() {
        author = new Author();
        author.setId(1L);
        author.setName("Test Author");

        category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setPublishedDate(LocalDate.now());
        book.setAuthor(author);
        book.setCategories(Set.of(category));
        book.setReviews(List.of());

        review = new Review();
        review.setId(1L);
        review.setBook(book);
        review.setUser(user);
        review.setRating(5);
        review.setComment("Great Book!");
        review.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetById_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponseDto dto = bookService.getById(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Test Book");
        assertThat(dto.getAuthor().getName()).isEqualTo("Test Author");
        assertThat(dto.getCategories()).hasSize(1);
        assertThat(dto.getRating()).isEqualTo(0);
    }

    @Test
    void testGetById_NotFound() {
        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById(2L))
                .isInstanceOf(BookException.class)
                .hasMessage("Book not found");
    }

    @Test
    void testCreateBook_Success() {
        setupSecurityContext();

        CreateBookRequestDto request = new CreateBookRequestDto();
        request.setTitle("New Book");
        request.setIsbn("1234567890");
        request.setAuthorId(1L);
        request.setCategoryIds(List.of(1L));
        request.setPublishedDate(LocalDate.now());

        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));

        Book newBook = new Book();
        newBook.setId(1L);
        newBook.setTitle("New Book");
        newBook.setIsbn("1234567890");
        newBook.setPublishedDate(LocalDate.now());
        newBook.setAuthor(author);
        newBook.setCategories(Set.of(category));
        newBook.setReviews(List.of());

        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        AllBooksResponseDto dto = bookService.createBook(request);

        assertThat(dto.getTitle()).isEqualTo("New Book");
        assertThat(dto.getAuthor().getName()).isEqualTo("Test Author");
        assertThat(dto.getCategories()).hasSize(1);
    }

    @Test
    void testUpdateBook_Success() {
        setupSecurityContext();

        UpdateBookDto request = new UpdateBookDto();
        request.setTitle("Updated Title");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        AllBooksResponseDto updatedDto = bookService.updateBook(1L, request);

        assertThat(updatedDto.getTitle()).isEqualTo("Updated Title");
        assertThat(updatedDto.getAuthor().getName()).isEqualTo("Test Author");
    }

    @Test
    void testUpdateBook_NotFound() {
        setupSecurityContext();

        UpdateBookDto request = new UpdateBookDto();
        request.setTitle("Updated Title");

        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(2L, request))
                .isInstanceOf(BookException.class)
                .hasMessage("Book not found");
    }

    @Test
    void testDeleteBook_Success() {
        setupSecurityContext();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        doNothing().when(bookRepository).delete(book);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void testDeleteBook_NotFound() {
        setupSecurityContext();

        when(bookRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(2L))
                .isInstanceOf(BookException.class)
                .hasMessage("Book not found");
    }

    @Test
    void testGetAllBooks() {
        Page<Book> page = new PageImpl<>(List.of(book), PageRequest.of(0, 10), 1);
        when(bookRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        var response = bookService.getAll(0, 10, null, null, null, null, null, null, null);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getTitle()).isEqualTo("Test Book");
    }

    @Test
    void testCreateReview_Success() {
        ReviewRequestDto request = new ReviewRequestDto();
        request.setUserId(1L);
        request.setRating(5);
        request.setComment("Amazing!");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        ReviewResponseDto response = bookService.createReview(1L, request);

        assertThat(response.getRating()).isEqualTo(review.getRating());
        assertThat(response.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getBook().getTitle()).isEqualTo(book.getTitle());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void testCreateReview_BookNotFound() {
        ReviewRequestDto request = new ReviewRequestDto();
        request.setUserId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.createReview(1L, request))
                .isInstanceOf(BookException.class)
                .hasMessage("Book not found");
    }

    @Test
    void testCreateReview_UserNotFound() {
        ReviewRequestDto request = new ReviewRequestDto();
        request.setUserId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.createReview(1L, request))
                .isInstanceOf(BookException.class)
                .hasMessage("User not found");
    }

    @Test
    void testGetBookReviews_Success() {
        Review review2 = new Review();
        review2.setId(2L);
        review2.setRating(4);
        review2.setComment("Good book");
        review2.setUser(user);
        review2.setBook(book);
        review2.setCreatedAt(LocalDateTime.now());

        book.setReviews(List.of(review, review2));

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        List<ReviewResponseDto> reviews = bookService.getBookReview(1L);

        assertThat(reviews).hasSize(2);
        assertThat(reviews.get(0).getRating()).isEqualTo(review.getRating());
        assertThat(reviews.get(1).getRating()).isEqualTo(review2.getRating());
    }

    @Test
    void testGetBookReviews_BookNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookReview(99L))
                .isInstanceOf(BookException.class)
                .hasMessage("Book not found");
    }

    private void setupSecurityContext() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(new SimpleGrantedAuthority("ADMIN"))
                );

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
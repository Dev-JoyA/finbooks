package com.whytelabeltech.finbooks.app.book.service;

import com.whytelabeltech.finbooks.app.author.model.Author;
import com.whytelabeltech.finbooks.app.author.repository.AuthorRepository;
import com.whytelabeltech.finbooks.app.book.dto.request.CreateBookRequestDto;
import com.whytelabeltech.finbooks.app.book.dto.request.UpdateBookDto;
import com.whytelabeltech.finbooks.app.book.dto.response.*;
import com.whytelabeltech.finbooks.app.book.dto.response.review.ReviewsDto;
import com.whytelabeltech.finbooks.app.book.dto.response.review.UserReviewResponse;
import com.whytelabeltech.finbooks.app.book.model.Book;
import com.whytelabeltech.finbooks.app.book.repository.BookRepository;
import com.whytelabeltech.finbooks.app.category.model.Category;
import com.whytelabeltech.finbooks.app.category.repository.CategoryRepository;
import com.whytelabeltech.finbooks.app.review.dto.requests.ReviewRequestDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewBookDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewResponseDto;
import com.whytelabeltech.finbooks.app.review.dto.response.ReviewUserDto;
import com.whytelabeltech.finbooks.app.review.model.Review;
import com.whytelabeltech.finbooks.app.review.repository.ReviewRepository;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.app.shared.dto.PaginatedResponse;
import com.whytelabeltech.finbooks.app.user.model.User;
import com.whytelabeltech.finbooks.app.user.repository.UserRepository;
import com.whytelabeltech.finbooks.middleware.exception.error.AuthorException;
import com.whytelabeltech.finbooks.middleware.exception.error.BookException;
import jakarta.persistence.criteria.Join;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public PaginatedResponse<AllBooksResponseDto> getAll(
            int page,
            int size,
            Long authorId,
            Long categoryId,
            Double ratingMin,
            Double ratingMax,
            LocalDate publishedStart,
            LocalDate publishedEnd,
            String sortBy
    ) {

        Sort sort = Sort.by(sortBy == null ? "id" : sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Book> spec = (root, query, cb) -> cb.conjunction();

        if (authorId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("author").get("id"), authorId));
        }

        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Book, Category> join = root.join("categories");
                return cb.equal(join.get("id"), categoryId);
            });
        }

        if (publishedStart != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("publishedDate"), publishedStart));
        }

        if (publishedEnd != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("publishedDate"), publishedEnd));
        }

        Page<Book> pageResult = bookRepository.findAll(spec, pageable);

        List<AllBooksResponseDto> content = pageResult.getContent()
                .stream()
                .map(this::buildBooksResponse)
                .filter(dto -> {
                    if (ratingMin != null && dto.getRating() < ratingMin) return false;
                    if (ratingMax != null && dto.getRating() > ratingMax) return false;
                    return true;
                })
                .toList();

        return new PaginatedResponse<>(
                content,
                pageResult.getNumber(),
                pageResult.getSize()
        );
    }


    @Override
    public BookResponseDto getById (Long id){
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookException("Book not found"));
        BookResponseDto response = new BookResponseDto();
        response.setId(book.getId());
        response.setIsbn(book.getIsbn());
        response.setTitle(book.getTitle());
        response.setPublishedDate(book.getPublishedDate());

        AuthorFullDetailsResponse authorDto = new AuthorFullDetailsResponse();
        authorDto.setId(book.getAuthor().getId());
        authorDto.setName(book.getAuthor().getName());
        authorDto.setEmail(book.getAuthor().getEmail());
        authorDto.setBio(book.getAuthor().getBio());
        response.setAuthor(authorDto);

        response.setCategories(
                book.getCategories().stream()
                        .map(cat -> new CategoryDetailResponse(cat.getId(), cat.getName()))
                        .toList()
        );


        response.setReviews(
                book.getReviews().stream()
                        .map(this::mapReview)
                        .toList()
        );;

        response.setRating(calculateRating(book));
        return response;
    }

    @Override
    public AllBooksResponseDto createBook (CreateBookRequestDto request){
        checkRole();

        if (request.getCategoryIds() == null || request.getCategoryIds().isEmpty()) {
            throw new BookException("Book must have at least one category");
        }

        if (!isValidIsbn(request.getIsbn())) {
            throw new BookException("Invalid ISBN-10 or ISBN-13");
        }

        if (bookRepository.findByIsbn(request.getIsbn()).isPresent()) {
            throw new BookException("ISBN already exists");
        }

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new AuthorException("Author not found"));

        Set<Category> categories = categoryRepository.findAllById(request.getCategoryIds())
                .stream().collect(Collectors.toSet());

        if (categories.isEmpty()) {
            throw new BookException("Invalid categories supplied");
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setCreatedAt(LocalDateTime.now());
        book.setCategories(categories);
        book.setAuthor(author);
        book.setPublishedDate(request.getPublishedDate());

        Book savedBook = bookRepository.save(book);

        return buildBooksResponse(savedBook);
    }

    @Override
    public AllBooksResponseDto updateBook(Long id, UpdateBookDto request) {
        checkRole();

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookException("Book not found"));

        if (request.getIsbn() != null) {
            if (!isValidIsbn(request.getIsbn())) {
                throw new BookException("Invalid ISBN");
            }

            bookRepository.findByIsbn(request.getIsbn())
                    .filter(b -> !b.getId().equals(id))
                    .ifPresent(b -> {
                        throw new BookException("ISBN already exists");
                    });

            book.setIsbn(request.getIsbn());
        }

        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }

        if (request.getAuthorId() != null) {
            Author author = authorRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new AuthorException("Author not found"));
            book.setAuthor(author);
        }

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<Category> categories = categoryRepository.findAllById(request.getCategoryIds())
                    .stream().collect(Collectors.toSet());
            book.setCategories(categories);
        }

        Book saved = bookRepository.save(book);
        return buildBooksResponse(saved);
    }


    @Override
    public MessageResponse deleteBook (Long id){
        checkRole();

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookException("Book not found"));
        bookRepository.delete(book);
        return new MessageResponse("Book Deleted Successfully");
    }

    @Override
    public ReviewResponseDto createReview(Long id, ReviewRequestDto request) {

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookException("Book not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BookException("User not found"));

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);

        return new ReviewResponseDto(
                saved.getId(),
                saved.getRating(),
                new ReviewUserDto(user.getId(), user.getUsername()),
                new ReviewBookDto(book.getId(), book.getTitle()),
                saved.getCreatedAt()
        );
    }

    @Override
    public List<ReviewResponseDto> getBookReview(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookException("Book not found"));

        return book.getReviews()
                .stream()
                .map(review -> new ReviewResponseDto(
                        review.getId(),
                        review.getRating(),
                        new ReviewUserDto(
                                review.getUser().getId(),
                                review.getUser().getUsername()
                        ),
                        new ReviewBookDto(
                                book.getId(),
                                book.getTitle()
                        ),
                        review.getCreatedAt()
                ))
                .toList();
    }



    private void checkRole (){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean roles = authentication.getAuthorities().stream().anyMatch(role ->
                role.getAuthority().equals("ADMIN"));

        if(!roles){
            throw new BookException("Only Admin can perform this function");
        }
    }

    private Integer calculateRating(Book book) {
        if (book.getReviews().isEmpty()) return 0;

        return (int) Math.round(
                book.getReviews().stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0)
        );
    }

    private ReviewsDto mapReview(Review review) {
        ReviewsDto dto = new ReviewsDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());

        dto.setUser(new UserReviewResponse(
                review.getUser().getId(),
                review.getUser().getUsername()
        ));
        return dto;
    }


    private boolean isValidIsbn(String isbn) {
        return isbn != null && (isbn.matches("\\d{10}") || isbn.matches("\\d{13}"));
    }


    private AllBooksResponseDto buildBooksResponse(Book book) {
        AllBooksResponseDto dto = new AllBooksResponseDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setPublishedDate(book.getPublishedDate());
        dto.setRating(calculateRating(book));

        AuthorDetailsResponse authorDto = new AuthorDetailsResponse();
        authorDto.setId(book.getAuthor().getId());
        authorDto.setName(book.getAuthor().getName());
        dto.setAuthor(authorDto);

        dto.setCategories(
                book.getCategories().stream()
                        .map(cat -> new CategoryDetailResponse(cat.getId(), cat.getName()))
                        .toList()
        );

        return dto;
    }

}

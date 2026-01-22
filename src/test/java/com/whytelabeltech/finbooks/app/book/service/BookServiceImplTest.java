package com.whytelabeltech.finbooks.app.book.service;


import com.whytelabeltech.finbooks.app.author.model.Author;
import com.whytelabeltech.finbooks.app.book.model.Book;
import com.whytelabeltech.finbooks.app.book.repository.BookRepository;
import com.whytelabeltech.finbooks.app.category.model.Category;
import com.whytelabeltech.finbooks.middleware.exception.error.BookException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author author;
    private Category category;
    private Book book;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        author = new Author();
        author.setId(1L);
        author.setName("Test Author");

        category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
        book.setPublishedDate(LocalDate.now());
        book.setAuthor(author);
        book.setCategories(List.of(category));
        book.setRating(4.5);
    }

    @Test
    void testGetById_BookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getById(1L);

        assertThat(result.getTitle()).isEqualTo("Test Book");
        assertThat(result.getAuthor().getName()).isEqualTo("Test Author");
    }

    @Test
    void testGetById_BookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookException.class, () -> bookService.getById(1L));
    }

    @Test
    void testGetAll_DefaultPageAndSize() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Book> result = bookService.getAll(null, null, null, null, null, null, null, "title");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Book");
    }

    @Test
    void testGetAll_WithFilters() {
        Page<Book> page = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Book> result = bookService.getAll(
                1L, 1L, 4.0, 5.0,
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(1),
                "title"
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRating()).isBetween(4.0, 5.0);
    }


    @Test
    void testCreateBook_Success() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book created = bookService.createBook(book);

        assertThat(created.getTitle()).isEqualTo("Test Book");
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testUpdateBook_Success() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        book.setTitle("Updated Title");
        Book updated = bookService.updateBook(1L, book);

        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testUpdateBook_NotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookException.class, () -> bookService.updateBook(1L, book));
    }

    @Test
    void testDeleteBook_Success() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteBook_NotFound() {
        when(bookRepository.existsById(1L)).thenReturn(false);

        assertThrows(BookException.class, () -> bookService.deleteBook(1L));
    }
}


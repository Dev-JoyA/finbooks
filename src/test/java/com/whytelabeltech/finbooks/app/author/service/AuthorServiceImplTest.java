package com.whytelabeltech.finbooks.app.author.service;

import com.whytelabeltech.finbooks.app.author.dto.request.AuthorRequestDto;
import com.whytelabeltech.finbooks.app.author.dto.response.AuthorResponseDto;
import com.whytelabeltech.finbooks.app.author.dto.response.BookAuthorDto;
import com.whytelabeltech.finbooks.app.author.model.Author;
import com.whytelabeltech.finbooks.app.author.repository.AuthorRepository;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.middleware.exception.error.AuthorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private Author author;
    private AuthorResponseDto authorResponseDto;

    @BeforeEach
    void setup() {
        author = new Author();
        author.setId(1L);
        author.setName("Test Author");
        author.setEmail("author@test.com");
        author.setBio("Author bio");

        authorResponseDto = new AuthorResponseDto();
        authorResponseDto.setId(1L);
        authorResponseDto.setName("Test Author");
        authorResponseDto.setEmail("author@test.com");
        authorResponseDto.setBio("Author bio");
    }

    @Test
    void testGetAllAuthors() {
        when(authorRepository.findAll()).thenReturn(List.of(author));
        when(modelMapper.map(any(Author.class), eq(AuthorResponseDto.class)))
                .thenReturn(authorResponseDto);

        List<AuthorResponseDto> authors = authorService.getAll();

        assertThat(authors).hasSize(1);
        verify(authorRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(Author.class), eq(AuthorResponseDto.class));
    }

    @Test
    void testGetOneAuthor_Success() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        BookAuthorDto result = authorService.getOne(1L);

        assertThat(result.getName()).isEqualTo(author.getName());
        assertThat(result.getEmail()).isEqualTo(author.getEmail());
        assertThat(result.getBio()).isEqualTo(author.getBio());
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOneAuthor_NotFound() {
        when(authorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.getOne(2L))
                .isInstanceOf(AuthorException.class)
                .hasMessage("Author not found");

        verify(authorRepository, times(1)).findById(2L);
    }

    @Test
    void testCreateAuthor_Success() {
        setupSecurityContext();

        AuthorRequestDto request = new AuthorRequestDto();
        request.setName("New Author");
        request.setEmail("new@test.com");
        request.setBio("New bio");

        when(authorRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenReturn(author);
        when(modelMapper.map(any(Author.class), eq(AuthorResponseDto.class)))
                .thenReturn(authorResponseDto);

        AuthorResponseDto result = authorService.create(request);

        assertThat(result.getEmail()).isEqualTo(authorResponseDto.getEmail());
        assertThat(result.getName()).isEqualTo(authorResponseDto.getName());

        verify(authorRepository, times(1)).findByEmail("new@test.com");
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testCreateAuthor_EmailExists() {
        setupSecurityContext();

        AuthorRequestDto request = new AuthorRequestDto();
        request.setName("Duplicate Author");
        request.setEmail("author@test.com");

        when(authorRepository.findByEmail("author@test.com")).thenReturn(Optional.of(author));

        assertThatThrownBy(() -> authorService.create(request))
                .isInstanceOf(AuthorException.class)
                .hasMessage("Author already exist with that email");

        verify(authorRepository, times(1)).findByEmail("author@test.com");
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void testCreateAuthor_WithoutName() {
        setupSecurityContext();

        AuthorRequestDto request = new AuthorRequestDto();
        request.setEmail("test@test.com");

        when(authorRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.create(request))
                .isInstanceOf(AuthorException.class)
                .hasMessage("Authors name cannot be blank");

        verify(authorRepository, times(1)).findByEmail("test@test.com");
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void testUpdateAuthor_Success() {
        setupSecurityContext();

        AuthorRequestDto request = new AuthorRequestDto();
        request.setName("Updated Name");
        request.setEmail("updated@test.com");
        request.setBio("Updated bio");

        Author updatedAuthor = new Author();
        updatedAuthor.setId(1L);
        updatedAuthor.setName("Updated Name");
        updatedAuthor.setEmail("updated@test.com");
        updatedAuthor.setBio("Updated bio");

        AuthorResponseDto updatedResponse = new AuthorResponseDto();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Name");
        updatedResponse.setEmail("updated@test.com");
        updatedResponse.setBio("Updated bio");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(updatedAuthor);
        when(modelMapper.map(any(Author.class), eq(AuthorResponseDto.class)))
                .thenReturn(updatedResponse);

        AuthorResponseDto result = authorService.update(1L, request);

        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getEmail()).isEqualTo("updated@test.com");

        verify(authorRepository, times(1)).findById(1L);
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void testUpdateAuthor_NotFound() {
        setupSecurityContext();

        AuthorRequestDto request = new AuthorRequestDto();
        request.setName("Updated Name");

        when(authorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.update(2L, request))
                .isInstanceOf(AuthorException.class)
                .hasMessage("Author not found");

        verify(authorRepository, times(1)).findById(2L);
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void testDeleteAuthor_Success() {
        setupSecurityContext();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        doNothing().when(authorRepository).delete(author);

        MessageResponse response = authorService.deleteAuthor(1L);

        assertThat(response.getMessage()).isEqualTo("Author Deleted Successfully");
        verify(authorRepository, times(1)).delete(author);
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteAuthor_NotFound() {
        setupSecurityContext();

        when(authorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.deleteAuthor(2L))
                .isInstanceOf(AuthorException.class)
                .hasMessage("Author not found");

        verify(authorRepository, times(1)).findById(2L);
        verify(authorRepository, never()).delete(any(Author.class));
    }

    @Test
    void testDeleteAuthor_WithoutAdminRole() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);

        assertThatThrownBy(() -> authorService.deleteAuthor(1L))
                .isInstanceOf(AuthorException.class)
                .hasMessage("Only Admin can perform this function");

        verify(authorRepository, never()).findById(any());
        verify(authorRepository, never()).delete(any(Author.class));
    }

    private void setupSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
    }
}
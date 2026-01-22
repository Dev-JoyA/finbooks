package com.whytelabeltech.finbooks.app.author.service;

import com.whytelabeltech.finbooks.app.author.dto.request.AuthorRequestDto;
import com.whytelabeltech.finbooks.app.author.dto.response.AuthorResponseDto;
import com.whytelabeltech.finbooks.app.author.dto.response.BookAuthorDto;
import com.whytelabeltech.finbooks.app.author.dto.response.BookDetailsResponse;
import com.whytelabeltech.finbooks.app.author.dto.response.BookTitleResponse;
import com.whytelabeltech.finbooks.app.author.model.Author;
import com.whytelabeltech.finbooks.app.author.repository.AuthorRepository;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.middleware.exception.error.AuthorException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthorRepository authorRepository;

    @Override
    public List<AuthorResponseDto> getAll (){
        return authorRepository.findAll()
                .stream()
                .map(this::buildAuthorResponseDto)
                .toList();
    }

    @Override
    public BookAuthorDto getOne (Long id){
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorException("Author not found"));

        BookAuthorDto dto = new BookAuthorDto();
        dto.setBio(author.getBio());
        dto.setName(author.getName());
        dto.setEmail(author.getEmail());
        dto.setBooks(author.getBooks().stream()
                .map(book -> new BookDetailsResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getPublishedDate()
                )).collect(Collectors.toList()));

        return dto;
    }

    public AuthorResponseDto create (AuthorRequestDto request){
        checkRole();

        if(authorRepository.findByEmail(request.getEmail()).isPresent()){
            throw new AuthorException("Author already exist with that email");
        }

        if(request.getName() == null){
            throw new AuthorException("Authors name cannot be blank");
        }
        if(request.getEmail() == null){
            throw new AuthorException("Authors Email cannot be blank");
        }

        Author author = new Author();

        if(request.getBio() != null){
            author.setBio(request.getBio());
        }
        author.setName(request.getName());
        author.setEmail(request.getEmail());
        Author savedAuthor = authorRepository.save(author);
        return buildAuthorResponseDto(savedAuthor);

    }

    @Override
    public AuthorResponseDto update (Long id, AuthorRequestDto request){
        checkRole();

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorException("Author not found"));
        if(request.getBio() != null){
            author.setBio(request.getBio());
        }
        if(request.getEmail() != null){
            author.setEmail(request.getEmail());
        }
        if(request.getName() != null){
            author.setName(request.getName());
        }
        Author savedAuthor = authorRepository.save(author);
        return buildAuthorResponseDto(savedAuthor);

    }

    @Override
    public MessageResponse deleteAuthor (Long id){
        checkRole();

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorException("Author not found"));

        authorRepository.delete(author);
        return new MessageResponse("Author Deleted Successfully");
    }

    private void checkRole (){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean roles = authentication.getAuthorities().stream().anyMatch(role ->
                role.getAuthority().equals("ADMIN"));

        if(!roles){
            throw new AuthorException("Only Admin can perform this function");
        }
    }

    private AuthorResponseDto buildAuthorResponseDto(Author author){
        AuthorResponseDto dto = modelMapper.map(author, AuthorResponseDto.class);

        dto.setBio(author.getBio());
        dto.setId(author.getId());
        dto.setEmail(author.getEmail());
        dto.setBooks(author.getBooks()
                .stream()
                .map(book -> new BookTitleResponse(book.getId(), book.getTitle()))
                .collect(Collectors.toList()));

        return dto;
    }
}

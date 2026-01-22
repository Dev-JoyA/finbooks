package com.whytelabeltech.finbooks.app.author.service;

import com.whytelabeltech.finbooks.app.author.dto.request.AuthorRequestDto;
import com.whytelabeltech.finbooks.app.author.dto.response.AuthorResponseDto;
import com.whytelabeltech.finbooks.app.author.dto.response.BookAuthorDto;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;

import java.util.List;

public interface AuthorService {
    List<AuthorResponseDto> getAll ();
    BookAuthorDto getOne (Long id);
    AuthorResponseDto create (AuthorRequestDto request);
    AuthorResponseDto update (Long id, AuthorRequestDto request);
    MessageResponse deleteAuthor (Long id);
}

package com.whytelabeltech.finbooks.app.author.controller;

import com.whytelabeltech.finbooks.app.author.dto.request.AuthorRequestDto;
import com.whytelabeltech.finbooks.app.author.dto.response.AuthorResponseDto;
import com.whytelabeltech.finbooks.app.author.dto.response.BookAuthorDto;
import com.whytelabeltech.finbooks.app.author.service.AuthorService;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class authorController {

    @Autowired
    AuthorService authorService;

    @GetMapping()
    ResponseEntity<List<AuthorResponseDto>> allAuthors (){
        return ResponseEntity.ok(authorService.getAll());
    }

    @GetMapping("{id}")
    ResponseEntity<BookAuthorDto> oneAuthor(@PathVariable Long id){
        return ResponseEntity.ok(authorService.getOne(id));
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<AuthorResponseDto> createAuthor(@RequestBody @Valid AuthorRequestDto request){
        return ResponseEntity.ok(authorService.create(request));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<AuthorResponseDto> update (@PathVariable Long id,
                                              @RequestBody AuthorRequestDto request){
        return ResponseEntity.ok(authorService.update(id, request));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    ResponseEntity<MessageResponse> delete (@PathVariable Long id){
        return ResponseEntity.ok(authorService.deleteAuthor(id));
    }


}

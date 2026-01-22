package com.whytelabeltech.finbooks.app.category.controller;

import com.whytelabeltech.finbooks.app.category.dto.request.CategoryRequestDto;
import com.whytelabeltech.finbooks.app.category.dto.response.CategoryResponseDto;
import com.whytelabeltech.finbooks.app.category.service.CategoryService;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll (){

        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CategoryResponseDto> create (
            @RequestBody CategoryRequestDto request
        ) {
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<CategoryResponseDto> update (
            @PathVariable Long id,
            @RequestBody CategoryRequestDto request
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<MessageResponse> delete (
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }

}

package com.whytelabeltech.finbooks.app.category.service;

import com.whytelabeltech.finbooks.app.category.dto.request.CategoryRequestDto;
import com.whytelabeltech.finbooks.app.category.dto.response.CategoryResponseDto;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDto> getAll ();
    CategoryResponseDto createCategory (CategoryRequestDto request);
    CategoryResponseDto updateCategory (Long id, CategoryRequestDto request);
    MessageResponse deleteCategory (Long id);
}

package com.whytelabeltech.finbooks.app.category.service;

import com.whytelabeltech.finbooks.app.category.dto.request.CategoryRequestDto;
import com.whytelabeltech.finbooks.app.category.dto.response.CategoryResponseDto;
import com.whytelabeltech.finbooks.app.category.model.Category;
import com.whytelabeltech.finbooks.app.category.repository.CategoryRepository;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.middleware.exception.error.AuthenticationException;
import com.whytelabeltech.finbooks.middleware.exception.error.CategoryException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponseDto> getAll (){

        return categoryRepository.findAll()
                .stream()
                .map(this::buildCategoryResponseDto)
                .toList();
    }

    @Override
    public CategoryResponseDto createCategory (CategoryRequestDto request){
        checkRole();
        if(categoryRepository.findByName(request.getName()).isPresent()){
            throw new CategoryException("Category name already exist");
        }

        Category category = new Category();
        category.setName(request.getName());

        categoryRepository.save(category);

        return buildCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory (Long id, CategoryRequestDto request){
        checkRole();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found"));

        if(request.getName() != null){
            if (categoryRepository.findByName(request.getName())
                    .filter(u -> !u.getId().equals(id)).isPresent()) {
                throw new CategoryException("Category name already exist");
            }
            category.setName(request.getName());
        }

        categoryRepository.save(category);
        return buildCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public MessageResponse deleteCategory (Long id){
        checkRole();
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Category not found"));

        categoryRepository.deleteById(id);
        return new MessageResponse("Category Deleted Successfully");
    }

    private void checkRole (){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean roles = authentication.getAuthorities().stream().anyMatch(role ->
                role.getAuthority().equals("ADMIN"));

        if(!roles){
            throw new CategoryException("Only Admin can perform this function");
        }
    }

    private CategoryResponseDto buildCategoryResponseDto(Category category) {
       CategoryResponseDto dto = modelMapper.map(category, CategoryResponseDto.class);

       dto.setId(category.getId());
       dto.setName(category.getName());

        return dto;
    }
}

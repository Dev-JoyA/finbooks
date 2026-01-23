package com.whytelabeltech.finbooks.app.category.service;

import com.whytelabeltech.finbooks.app.category.dto.request.CategoryRequestDto;
import com.whytelabeltech.finbooks.app.category.dto.response.CategoryResponseDto;
import com.whytelabeltech.finbooks.app.category.model.Category;
import com.whytelabeltech.finbooks.app.category.repository.CategoryRepository;
import com.whytelabeltech.finbooks.app.shared.dto.MessageResponse;
import com.whytelabeltech.finbooks.middleware.exception.error.CategoryException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryResponseDto categoryResponseDto;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Fiction");

        categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(1L);
        categoryResponseDto.setName("Fiction");
    }

    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(modelMapper.map(any(Category.class), eq(CategoryResponseDto.class)))
                .thenReturn(categoryResponseDto);

        List<CategoryResponseDto> response = categoryService.getAll();

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("Fiction");
        verify(categoryRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(any(Category.class), eq(CategoryResponseDto.class));
    }

    @Test
    void testCreateCategory_Success() {
        setupSecurityContext();

        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Non-Fiction");

        Category newCategory = new Category();
        newCategory.setName("Non-Fiction");

        CategoryResponseDto newResponseDto = new CategoryResponseDto();
        newResponseDto.setName("Non-Fiction");

        when(categoryRepository.findByName("Non-Fiction")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);
        when(modelMapper.map(any(Category.class), eq(CategoryResponseDto.class)))
                .thenReturn(newResponseDto);

        CategoryResponseDto response = categoryService.createCategory(request);

        assertThat(response.getName()).isEqualTo("Non-Fiction");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void testCreateCategory_DuplicateName() {
        setupSecurityContext();

        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Fiction");

        when(categoryRepository.findByName("Fiction")).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(CategoryException.class)
                .hasMessage("Category name already exist");
    }

    @Test
    void testCreateCategory_WithoutAdminRole() {
        setupNonAdminSecurityContext();

        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("History");

        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(CategoryException.class)
                .hasMessage("Only Admin can perform this function");
    }

    @Test
    void testUpdateCategory_Success() {
        setupSecurityContext();

        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Sci-Fi");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Sci-Fi");

        CategoryResponseDto updatedResponseDto = new CategoryResponseDto();
        updatedResponseDto.setId(1L);
        updatedResponseDto.setName("Sci-Fi");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Sci-Fi")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);
        when(modelMapper.map(any(Category.class), eq(CategoryResponseDto.class)))
                .thenReturn(updatedResponseDto);

        CategoryResponseDto response = categoryService.updateCategory(1L, request);

        assertThat(response.getName()).isEqualTo("Sci-Fi");
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testUpdateCategory_NotFound() {
        setupSecurityContext();

        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Sci-Fi");

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(99L, request))
                .isInstanceOf(CategoryException.class)
                .hasMessage("Category not found");
    }

    @Test
    void testUpdateCategory_DuplicateName() {
        setupSecurityContext();

        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Fiction");

        Category anotherCategory = new Category();
        anotherCategory.setId(2L);
        anotherCategory.setName("Fiction");

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(anotherCategory));
        when(categoryRepository.findByName("Fiction")).thenReturn(Optional.of(category));

        assertThatThrownBy(() -> categoryService.updateCategory(2L, request))
                .isInstanceOf(CategoryException.class)
                .hasMessage("Category name already exist");
    }

    @Test
    void testUpdateCategory_WithoutAdminRole() {
        setupNonAdminSecurityContext();

        CategoryRequestDto request = new CategoryRequestDto();
        request.setName("Sci-Fi");

        assertThatThrownBy(() -> categoryService.updateCategory(1L, request))
                .isInstanceOf(CategoryException.class)
                .hasMessage("Only Admin can perform this function");
    }

    @Test
    void testDeleteCategory_Success() {
        setupSecurityContext();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).deleteById(1L);

        MessageResponse response = categoryService.deleteCategory(1L);

        assertThat(response.getMessage()).isEqualTo("Category Deleted Successfully");
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteCategory_NotFound() {
        setupSecurityContext();

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(99L))
                .isInstanceOf(CategoryException.class)
                .hasMessage("Category not found");
    }

    @Test
    void testDeleteCategory_WithoutAdminRole() {
        setupNonAdminSecurityContext();

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(CategoryException.class)
                .hasMessage("Only Admin can perform this function");
    }

    private void setupSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ADMIN"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
    }

    private void setupNonAdminSecurityContext() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));
        when(authentication.getAuthorities()).thenReturn((Collection) authorities);
    }
}
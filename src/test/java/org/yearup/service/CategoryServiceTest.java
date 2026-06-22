package org.yearup.service;

import org.yearup.models.Category;
import org.yearup.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CategoryServiceTest
{
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp()
    {
        category1 = new Category();
        category1.setCategoryId(1);
        category1.setName("Vinyl Records");
        category1.setDescription("Classic and modern albums");

        category2 = new Category();
        category2.setCategoryId(2);
        category2.setName("CDs");
        category2.setDescription("Compact discs");
    }

    @Test
    void getAllCategories_returnsAllCategories_whenDataExists() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Vinyl Records", result.get(0).getName());
        assertEquals("CDs", result.get(1).getName());
    }

    @Test
    void getAllCategories_returnsEmptyList_whenNoDataExists() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getById_returnsCorrectCategory_whenIdExists() {
        // Arrange
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category1));

        // Act
        Optional<Category> result = categoryService.getById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Vinyl Records", result.get().getName());
    }

    @Test
    void getByCategoryId_returnsNull_whenNoIdExists() {
        // Arrange
        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.getById(99);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void create_returnsNewCategory_withGeneratedId() {
        // Arrange
        Category newCategory = new Category();
        newCategory.setName("Turntables");
        newCategory.setDescription("Record players and accessories");

        Category savedCategory = new Category();
        savedCategory.setCategoryId(4);
        savedCategory.setName("Turntables");
        savedCategory.setDescription("Record players and accessories");

        when(categoryRepository.save(newCategory)).thenReturn(savedCategory);

        // Act
        Category result = categoryService.create(newCategory);

        // Assert
        assertEquals(4, result.getCategoryId());
        assertEquals("Turntables", result.getName());
    }

    @Test
    void update_returnsSuccessfullyUpdatedFields_whenChangingExistingCategoryFields() {
        // Arrange
        Category updateRequest = new Category();
        updateRequest.setName("Turntables");
        updateRequest.setDescription("Record players and accessories");

        Category savedCategory = new Category();
        savedCategory.setCategoryId(1);
        savedCategory.setName("Turntables");
        savedCategory.setDescription("Record players and accessories");

        when(categoryRepository.findById(1)).thenReturn(Optional.of(category1));
        when(categoryRepository.save(category1)).thenReturn((savedCategory));

        // Act
        Optional<Category> result = categoryService.update(1, updateRequest);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Turntables", result.get().getName());
        assertEquals("Record players and accessories", result.get().getDescription());
    }

    @Test
    void update_returnsEmpty_whenCategoryDoesNotExist() {
        // Arrange
        Category updateRequest = new Category();
        updateRequest.setName("Turntables");
        updateRequest.setDescription("Record players and accessories");

        when(categoryRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        Optional<Category> result = categoryService.update(99, updateRequest);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_callsDeleteById_withCorrectId() {
        // Arrange
        Category savedCategory = new Category();
        savedCategory.setCategoryId(3);
        savedCategory.setName("Turntables");
        savedCategory.setDescription("Record players and accessories");

        // Act
        categoryService.delete(3);

        // Assert
        verify(categoryRepository).deleteById(3);

    }
}
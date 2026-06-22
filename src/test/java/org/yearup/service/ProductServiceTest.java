package org.yearup.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.models.Product;
import org.yearup.repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product vinylRecord;
    private Product cd;
    private Product cassetteTape;

    @BeforeEach
    void setUp()
    {
        vinylRecord = new Product(1, "Abbey Road", 24.99, 1, "Classic Beatles album", "Rock", 10, true, "abbey-road.jpg");
        cd = new Product(2, "Greatest Hits", 12.99, 1, "Compilation CD", "Pop", 5, false, "greatest-hits.jpg");
        cassetteTape = new Product(3, "Kind of Blue", 14.99, 2, "Classic jazz album by Miles Davis", "Jazz", 8, false, "kind-of-blue.jpg");
    }

    @Test
    void getProductsByCategoryId_returnsMatchingProducts() {
        // Arrange
        when(productRepository.findByCategoryId(1)).thenReturn(List.of(vinylRecord, cd));

        // Act
        List<Product> result = productService.listByCategoryId(1);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Abbey Road", result.get(0).getName());
    }

    @Test
    void getProductsByCategoryId_returnsEmpty_whenCategoryExistWithNoProducts() {
        // Arrange
        when(productRepository.findByCategoryId(99)).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productService.listByCategoryId(99);

        // Asset
        assertTrue(result.isEmpty());
    }

    @Test
    void search_returnsAllProducts_whenNoFiltersProvided() {
        // Arrange
        List<Product> products = List.of(vinylRecord, cd);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.search(null, null,null, null);

        assertEquals(2, result.size());

        verify(productRepository).findAll();
    }

    @Test
    void search_returnsOnlySelectedCategoryProducts_whenCategoryFilterProvided() {
        // Arrange
        when(productRepository.findByCategoryId(2)).thenReturn(List.of(cassetteTape));

        // Act
        List<Product> result = productService.search(2, null, null, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getCategoryId());
        assertEquals("Kind of Blue", result.get(0).getName());

        verify(productRepository).findByCategoryId(2);
    }

    @Test
    void search_returnsProductsAboveMinPrice_whenMinPriceFilterProvided() {
        // Arrange
        List<Product> products = List.of(vinylRecord, cd, cassetteTape);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.search(null, 13.00, null, null);

        // Assert
        assertEquals(2, result.size());
        assertEquals(vinylRecord, result.get(0));
        assertEquals(cassetteTape, result.get(1));

        verify(productRepository).findAll();
    }

    @Test
    void search_returnsProductsBelowMaxPrice_whenMaxPriceFilterProvided() {
        // Arrange
        List<Product> products = List.of(vinylRecord, cd, cassetteTape);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.search(null, null, 13.00, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(cd, result.get(0));

        verify(productRepository).findAll();
    }

    @Test
    void search_returnsProductsBetweenPriceRange_whenMinPriceAndMaxPriceFiltersProvided() {
        // Arrange
        List<Product> products = List.of(vinylRecord, cd, cassetteTape);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.search(null, 14.99, 20.00, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(cassetteTape, result.get(0));

        verify(productRepository).findAll();
    }

    @Test
    void search_returnsProductsMatchingSubCategory_whenSubCategoryFilterProvided() {
        // Arrange
        List<Product> products = List.of(vinylRecord, cd, cassetteTape);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<Product> result = productService.search(null, null, null, "rock");

        // Assert
        assertEquals(1, result.size());
        assertEquals(vinylRecord, result.get(0));
        assertEquals("Rock", result.get(0).getSubCategory());
        assertEquals("Abbey Road", result.get(0).getName());

        verify(productRepository).findAll();
    }

    @Test
    void search_returnsProductsMatchingCategoryIdAndSubCategory_whenCategoryIdAndSubCategoryFiltersProvided() {
        // Arrange
        when(productRepository.findByCategoryId(1)).thenReturn(List.of(vinylRecord));

        // Act
        List<Product> result = productService.search(1, null, null, "Rock");

        // Assert
        assertEquals(1, result.size());
        assertEquals(vinylRecord, result.get(0));
        assertEquals("Rock", result.get(0).getSubCategory());
        assertEquals("Abbey Road", result.get(0).getName());

        verify(productRepository).findByCategoryId(1);
    }

    @Test
    void update_returnsProductWithUpdatedStock_whenChangedValuesProvided() {
        // Arrange
        Product updateRequest = new Product();
        updateRequest.setName("Abbey Road");
        updateRequest.setPrice(24.99);
        updateRequest.setCategoryId(1);
        updateRequest.setDescription("Classic Beatles album");
        updateRequest.setSubCategory("Rock");
        updateRequest.setStock(9);
        updateRequest.setFeatured(true);
        updateRequest.setImageUrl("abbey-road.jpg");

        when(productRepository.findById(1)).thenReturn(Optional.of(vinylRecord));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product result = productService.update(1, updateRequest);

        // Assert
        assertEquals(9, result.getStock());
    }

    @Test
    void update_returnsProductWithUpdatedField_whenAllValuesChange() {
        // Arrange
        Product updateRequest = new Product();
        updateRequest.setName("Is This It");
        updateRequest.setPrice(29.99);
        updateRequest.setCategoryId(2);
        updateRequest.setDescription("Classic Strokes album");
        updateRequest.setSubCategory("Alternative/Indie Rock");
        updateRequest.setStock(6);
        updateRequest.setFeatured(true);
        updateRequest.setImageUrl("is-this-it.jpg");

        when(productRepository.findById(1)).thenReturn(Optional.of(vinylRecord));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product result = productService.update(1, updateRequest);

        // Assert
        assertEquals("Is This It", result.getName());
        assertEquals(29.99, result.getPrice());
        assertEquals(2, result.getCategoryId());
        assertEquals("Classic Strokes album", result.getDescription());
        assertEquals("Alternative/Indie Rock", result.getSubCategory());
        assertEquals(6, result.getStock());
        assertTrue(result.isFeatured());
        assertEquals("is-this-it.jpg", result.getImageUrl());
    }
}
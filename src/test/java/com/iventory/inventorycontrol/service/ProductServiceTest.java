package com.iventory.inventorycontrol.service;



import com.iventory.inventorycontrol.entity.Product;
import com.iventory.inventorycontrol.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setCode("PRD001");
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.00"));
    }

    @Test
    void createProduct_WithValidData_ShouldReturnProduct() {
        when(productRepository.existsByCode(product.getCode())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product created = productService.createProduct(product);

        assertNotNull(created);
        assertEquals("PRD001", created.getCode());
        assertEquals("Test Product", created.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_WithExistingCode_ShouldThrowException() {
        when(productRepository.existsByCode(product.getCode())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(product);
        });

        assertTrue(exception.getMessage().contains("Já existe um produto com o código"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void getProductById_WithValidId_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> found = productService.getProductById(1L);

        assertTrue(found.isPresent());
        assertEquals("PRD001", found.get().getCode());
    }

    @Test
    void getProductById_WithInvalidId_ShouldReturnEmpty() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> found = productService.getProductById(99L);

        assertFalse(found.isPresent());
    }

    @Test
    void deleteProduct_WithValidId_ShouldDelete() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteProduct(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_WithInvalidId_ShouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(99L);
        });

        assertTrue(exception.getMessage().contains("Produto não encontrado"));
    }
}

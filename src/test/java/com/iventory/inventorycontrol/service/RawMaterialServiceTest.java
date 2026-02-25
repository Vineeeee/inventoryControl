package com.iventory.inventorycontrol.service;


import com.iventory.inventorycontrol.entity.RawMaterial;
import com.iventory.inventorycontrol.repository.RawMaterialRepository;
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
class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private RawMaterialService rawMaterialService;

    private RawMaterial rawMaterial;

    @BeforeEach
    void setUp() {
        rawMaterial = new RawMaterial();
        rawMaterial.setId(1L);
        rawMaterial.setCode("MP001");
        rawMaterial.setName("Test Material");
        rawMaterial.setStockQuantity(new BigDecimal("100.00"));
    }

    @Test
    void createRawMaterial_WithValidData_ShouldReturnMaterial() {
        when(rawMaterialRepository.existsByCode(rawMaterial.getCode())).thenReturn(false);
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenReturn(rawMaterial);

        RawMaterial created = rawMaterialService.createRawMaterial(rawMaterial);

        assertNotNull(created);
        assertEquals("MP001", created.getCode());
        assertEquals("Test Material", created.getName());
        verify(rawMaterialRepository).save(any(RawMaterial.class));
    }

    @Test
    void addStock_WithValidQuantity_ShouldIncreaseStock() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenReturn(rawMaterial);

        BigDecimal addQuantity = new BigDecimal("50.00");
        RawMaterial updated = rawMaterialService.addStock(1L, addQuantity);

        assertEquals(new BigDecimal("150.00"), updated.getStockQuantity());
    }

    @Test
    void addStock_WithNegativeQuantity_ShouldThrowException() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));

        BigDecimal addQuantity = new BigDecimal("-10.00");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rawMaterialService.addStock(1L, addQuantity);
        });

        assertTrue(exception.getMessage().contains("Quantidade deve ser maior que zero"));
    }

    @Test
    void removeStock_WithValidQuantity_ShouldDecreaseStock() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));
        when(rawMaterialRepository.save(any(RawMaterial.class))).thenReturn(rawMaterial);

        BigDecimal removeQuantity = new BigDecimal("30.00");
        RawMaterial updated = rawMaterialService.removeStock(1L, removeQuantity);

        assertEquals(new BigDecimal("70.00"), updated.getStockQuantity());
    }

    @Test
    void removeStock_WithInsufficientStock_ShouldThrowException() {
        when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rawMaterial));

        BigDecimal removeQuantity = new BigDecimal("200.00");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            rawMaterialService.removeStock(1L, removeQuantity);
        });

        assertTrue(exception.getMessage().contains("Estoque insuficiente"));
    }
}

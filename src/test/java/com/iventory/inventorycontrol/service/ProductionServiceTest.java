package com.iventory.inventorycontrol.service;


import com.iventory.inventorycontrol.dto.ProductionSuggestionDTO;
import com.iventory.inventorycontrol.entity.Product;
import com.iventory.inventorycontrol.entity.ProductRawMaterial;
import com.iventory.inventorycontrol.entity.RawMaterial;
import com.iventory.inventorycontrol.repository.ProductRawMaterialRepository;
import com.iventory.inventorycontrol.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductRawMaterialRepository productRawMaterialRepository;

    @Mock
    private RawMaterialService rawMaterialService;

    @InjectMocks
    private ProductionService productionService;

    private Product product1;
    private Product product2;
    private RawMaterial wood;
    private RawMaterial nails;
    private ProductRawMaterial association1;
    private ProductRawMaterial association2;

    @BeforeEach
    void setUp() {
        // Produtos
        product1 = new Product();
        product1.setId(1L);
        product1.setCode("PRD001");
        product1.setName("Cadeira");
        product1.setPrice(new BigDecimal("150.00"));

        product2 = new Product();
        product2.setId(2L);
        product2.setCode("PRD002");
        product2.setName("Mesa");
        product2.setPrice(new BigDecimal("350.00"));

        // Matérias-primas
        wood = new RawMaterial();
        wood.setId(1L);
        wood.setCode("MP001");
        wood.setName("Madeira");
        wood.setStockQuantity(new BigDecimal("100.00"));

        nails = new RawMaterial();
        nails.setId(2L);
        nails.setCode("MP002");
        nails.setName("Pregos");
        nails.setStockQuantity(new BigDecimal("500.00"));

        // Associações
        association1 = new ProductRawMaterial();
        association1.setProduct(product1);
        association1.setRawMaterial(wood);
        association1.setQuantityRequired(new BigDecimal("2.00"));

        association2 = new ProductRawMaterial();
        association2.setProduct(product1);
        association2.setRawMaterial(nails);
        association2.setQuantityRequired(new BigDecimal("10.00"));
    }

    @Test
    void calculateProductionForProduct_WithSufficientStock_ShouldReturnCorrectQuantity() {
        when(productRawMaterialRepository.findByProduct(product1))
                .thenReturn(Arrays.asList(association1, association2));

        List<RawMaterial> availableStock = Arrays.asList(wood, nails);

        ProductionSuggestionDTO suggestion = productionService
                .calculateProductionForProduct(product1, availableStock);

        assertEquals(50, suggestion.getPossibleQuantity()); // 100/2 = 50, 500/10 = 50 -> mínimo 50
        assertEquals(new BigDecimal("7500.00"), suggestion.getTotalValue()); // 150 * 50
    }

    @Test
    void calculateProductionForProduct_WithInsufficientStock_ShouldReturnZero() {
        wood.setStockQuantity(new BigDecimal("1.00")); // Só 1 madeira

        when(productRawMaterialRepository.findByProduct(product1))
                .thenReturn(Arrays.asList(association1, association2));

        List<RawMaterial> availableStock = Arrays.asList(wood, nails);

        ProductionSuggestionDTO suggestion = productionService
                .calculateProductionForProduct(product1, availableStock);

        assertEquals(0, suggestion.getPossibleQuantity()); // 1/2 = 0
        assertEquals(BigDecimal.ZERO, suggestion.getTotalValue());
    }

    @Test
    void suggestProduction_ShouldPrioritizeHigherValueProducts() {
        // Configura produtos em ordem de preço
        when(productRepository.findAllOrderByPriceDesc())
                .thenReturn(Arrays.asList(product2, product1)); // Mesa (350) primeiro, depois Cadeira (150)

        // Configura matérias-primas disponíveis
        List<RawMaterial> availableStock = Arrays.asList(wood, nails);
        when(rawMaterialService.getAllRawMaterials()).thenReturn(availableStock);

        // Configura associações para ambos os produtos
        when(productRawMaterialRepository.findByProduct(product2))
                .thenReturn(Arrays.asList(association1, association2));
        when(productRawMaterialRepository.findByProduct(product1))
                .thenReturn(Arrays.asList(association1, association2));

        List<ProductionSuggestionDTO> suggestions = productionService.suggestProduction();

        assertFalse(suggestions.isEmpty());

        // Verifica priorização: produto mais caro primeiro
        if (suggestions.size() >= 2) {
            assertEquals("Mesa", suggestions.get(0).getProductName());
            assertEquals("Cadeira", suggestions.get(1).getProductName());
        }
    }
}
package com.iventory.inventorycontrol.service;

import com.iventory.inventorycontrol.dto.ProductRawMaterialDTO;
import com.iventory.inventorycontrol.dto.ProductionSuggestionDTO;
import com.iventory.inventorycontrol.entity.Product;
import com.iventory.inventorycontrol.entity.ProductRawMaterial;
import com.iventory.inventorycontrol.entity.RawMaterial;
import com.iventory.inventorycontrol.repository.ProductRawMaterialRepository;
import com.iventory.inventorycontrol.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductionService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductRawMaterialRepository productRawMaterialRepository;

    @Autowired
    private RawMaterialService rawMaterialService;

    /**
     * Sugere quais produtos podem ser produzidos com o estoque atual
     * Priorizando os produtos de maior valor
     */
    public List<ProductionSuggestionDTO> suggestProduction() {
        // Pega todos os produtos ordenados por preço (maior primeiro)
        List<Product> products = productRepository.findAllOrderByPriceDesc();
        List<ProductionSuggestionDTO> suggestions = new ArrayList<>();

        // Cria uma cópia do estoque para simular o consumo
        List<RawMaterial> availableStock = rawMaterialService.getAllRawMaterials();

        for (Product product : products) {
            ProductionSuggestionDTO suggestion = calculateProductionForProduct(product, availableStock);
            if (suggestion.getPossibleQuantity() > 0) {
                suggestions.add(suggestion);
                // Atualiza o estoque disponível (consome as matérias-primas)
                consumeStock(availableStock, product, suggestion.getPossibleQuantity());
            }
        }

        return suggestions;
    }

    /**
     * Calcula quantas unidades de um produto específico podem ser produzidas
     */
    public ProductionSuggestionDTO calculateProductionForProduct(Product product, List<RawMaterial> availableStock) {
        List<ProductRawMaterial> requiredMaterials = productRawMaterialRepository.findByProduct(product);

        ProductionSuggestionDTO suggestion = new ProductionSuggestionDTO();
        suggestion.setProductId(product.getId());
        suggestion.setProductCode(product.getCode());
        suggestion.setProductName(product.getName());
        suggestion.setProductPrice(product.getPrice());

        if (requiredMaterials.isEmpty()) {
            suggestion.setPossibleQuantity(0);
            suggestion.setTotalValue(BigDecimal.ZERO);
            suggestion.setMaterialsRequired(new ArrayList<>());
            return suggestion;
        }

        // Calcula quantas unidades podem ser produzidas
        int maxUnits = Integer.MAX_VALUE;
        List<ProductRawMaterialDTO> materialsDTO = new ArrayList<>();

        for (ProductRawMaterial pm : requiredMaterials) {
            RawMaterial rawMaterial = pm.getRawMaterial();

            // Encontra o estoque disponível desta matéria-prima
            BigDecimal stock = findStockForMaterial(availableStock, rawMaterial.getId());
            BigDecimal required = pm.getQuantityRequired();

            ProductRawMaterialDTO dto = new ProductRawMaterialDTO(
                    rawMaterial.getId(),
                    rawMaterial.getCode(),
                    rawMaterial.getName(),
                    required
            );
            dto.setAvailableStock(stock);
            materialsDTO.add(dto);

            if (required.compareTo(BigDecimal.ZERO) > 0) {
                int possibleUnits = stock.divide(required, 0, java.math.RoundingMode.DOWN).intValue();
                if (possibleUnits < maxUnits) {
                    maxUnits = possibleUnits;
                }
            }
        }

        if (maxUnits == Integer.MAX_VALUE) {
            maxUnits = 0;
        }

        suggestion.setPossibleQuantity(maxUnits);
        suggestion.setMaterialsRequired(materialsDTO);
        suggestion.setTotalValue(product.getPrice().multiply(BigDecimal.valueOf(maxUnits)));

        return suggestion;
    }

    /**
     * Encontra o estoque disponível de uma matéria-prima na lista
     */
    private BigDecimal findStockForMaterial(List<RawMaterial> stockList, Long materialId) {
        return stockList.stream()
                .filter(rm -> rm.getId().equals(materialId))
                .findFirst()
                .map(RawMaterial::getStockQuantity)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Consome o estoque das matérias-primas baseado na produção
     */
    private void consumeStock(List<RawMaterial> stockList, Product product, int quantity) {
        List<ProductRawMaterial> requiredMaterials = productRawMaterialRepository.findByProduct(product);

        for (ProductRawMaterial pm : requiredMaterials) {
            BigDecimal totalRequired = pm.getQuantityRequired().multiply(BigDecimal.valueOf(quantity));

            // Encontra e atualiza o estoque na lista
            stockList.stream()
                    .filter(rm -> rm.getId().equals(pm.getRawMaterial().getId()))
                    .findFirst()
                    .ifPresent(rm -> rm.setStockQuantity(rm.getStockQuantity().subtract(totalRequired)));
        }
    }

    /**
     * Sugestão detalhada para um produto específico
     */
    public ProductionSuggestionDTO getSuggestionForProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        List<RawMaterial> availableStock = rawMaterialService.getAllRawMaterials();
        return calculateProductionForProduct(product, availableStock);
    }

    /**
     * Calcula o valor total que pode ser obtido com a produção sugerida
     */
    public BigDecimal calculateTotalProductionValue() {
        List<ProductionSuggestionDTO> suggestions = suggestProduction();
        return suggestions.stream()
                .map(ProductionSuggestionDTO::getTotalValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Sugestão resumida (apenas produto e quantidade)
     */
    public List<ProductionSuggestionDTO> getSimplifiedSuggestions() {
        return suggestProduction().stream()
                .map(s -> {
                    ProductionSuggestionDTO simplified = new ProductionSuggestionDTO();
                    simplified.setProductId(s.getProductId());
                    simplified.setProductCode(s.getProductCode());
                    simplified.setProductName(s.getProductName());
                    simplified.setPossibleQuantity(s.getPossibleQuantity());
                    simplified.setTotalValue(s.getTotalValue());
                    simplified.setMaterialsRequired(new ArrayList<>());
                    return simplified;
                })
                .collect(Collectors.toList());
    }
}
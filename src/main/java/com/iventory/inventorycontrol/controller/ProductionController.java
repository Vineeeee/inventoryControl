package com.iventory.inventorycontrol.controller;

import com.iventory.inventorycontrol.dto.ProductionSuggestionDTO;
import com.iventory.inventorycontrol.service.ProductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/production")
@CrossOrigin(origins = "*")
public class ProductionController {

    @Autowired
    private ProductionService productionService;

    /**
     * ENDPOINT PRINCIPAL - Sugere quais produtos podem ser produzidos
     * Priorizando os de maior valor
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<ProductionSuggestionDTO>> getProductionSuggestions() {
        List<ProductionSuggestionDTO> suggestions = productionService.suggestProduction();
        return new ResponseEntity<>(suggestions, HttpStatus.OK);
    }

    /**
     * Sugestão simplificada (apenas produto e quantidade)
     */
    @GetMapping("/suggestions/simplified")
    public ResponseEntity<List<ProductionSuggestionDTO>> getSimplifiedSuggestions() {
        List<ProductionSuggestionDTO> suggestions = productionService.getSimplifiedSuggestions();
        return new ResponseEntity<>(suggestions, HttpStatus.OK);
    }

    /**
     * Sugestão para um produto específico
     */
    @GetMapping("/suggestions/product/{productId}")
    public ResponseEntity<?> getSuggestionForProduct(@PathVariable Long productId) {
        try {
            ProductionSuggestionDTO suggestion = productionService.getSuggestionForProduct(productId);
            return new ResponseEntity<>(suggestion, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Valor total que pode ser obtido com a produção sugerida
     */
    @GetMapping("/suggestions/total-value")
    public ResponseEntity<BigDecimal> getTotalProductionValue() {
        BigDecimal totalValue = productionService.calculateTotalProductionValue();
        return new ResponseEntity<>(totalValue, HttpStatus.OK);
    }

    /**
     * Verifica se um produto específico pode ser produzido
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<?> checkProductProduction(@PathVariable Long productId) {
        try {
            ProductionSuggestionDTO suggestion = productionService.getSuggestionForProduct(productId);
            boolean canProduce = suggestion.getPossibleQuantity() > 0;

            return new ResponseEntity<>(
                    new ProductionCheckResponse(
                            canProduce,
                            suggestion.getPossibleQuantity(),
                            "Produto " + (canProduce ? "pode" : "não pode") + " ser produzido"
                    ),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // Classe interna para resposta do check
    private static class ProductionCheckResponse {
        private boolean canProduce;
        private int possibleQuantity;
        private String message;

        public ProductionCheckResponse(boolean canProduce, int possibleQuantity, String message) {
            this.canProduce = canProduce;
            this.possibleQuantity = possibleQuantity;
            this.message = message;
        }

        public boolean isCanProduce() { return canProduce; }
        public int getPossibleQuantity() { return possibleQuantity; }
        public String getMessage() { return message; }
    }
}
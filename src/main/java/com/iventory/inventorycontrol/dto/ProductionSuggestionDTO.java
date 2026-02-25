package com.iventory.inventorycontrol.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductionSuggestionDTO {
    private Long productId;
    private String productCode;
    private String productName;
    private BigDecimal productPrice;
    private Integer possibleQuantity;
    private BigDecimal totalValue;
    private List<ProductRawMaterialDTO> materialsRequired;

    public ProductionSuggestionDTO() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }

    public Integer getPossibleQuantity() { return possibleQuantity; }
    public void setPossibleQuantity(Integer possibleQuantity) { this.possibleQuantity = possibleQuantity; }

    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }

    public List<ProductRawMaterialDTO> getMaterialsRequired() { return materialsRequired; }
    public void setMaterialsRequired(List<ProductRawMaterialDTO> materialsRequired) { this.materialsRequired = materialsRequired; }
}

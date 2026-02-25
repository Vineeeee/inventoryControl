package com.iventory.inventorycontrol.dto;

import java.math.BigDecimal;

public class ProductRawMaterialDTO {
    private Long rawMaterialId;
    private String rawMaterialCode;
    private String rawMaterialName;
    private BigDecimal quantityRequired;
    private BigDecimal availableStock;

    public ProductRawMaterialDTO() {}

    public ProductRawMaterialDTO(Long rawMaterialId, String rawMaterialCode,
                                 String rawMaterialName, BigDecimal quantityRequired) {
        this.rawMaterialId = rawMaterialId;
        this.rawMaterialCode = rawMaterialCode;
        this.rawMaterialName = rawMaterialName;
        this.quantityRequired = quantityRequired;
    }

    public Long getRawMaterialId() { return rawMaterialId; }
    public void setRawMaterialId(Long rawMaterialId) { this.rawMaterialId = rawMaterialId; }

    public String getRawMaterialCode() { return rawMaterialCode; }
    public void setRawMaterialCode(String rawMaterialCode) { this.rawMaterialCode = rawMaterialCode; }

    public String getRawMaterialName() { return rawMaterialName; }
    public void setRawMaterialName(String rawMaterialName) { this.rawMaterialName = rawMaterialName; }

    public BigDecimal getQuantityRequired() { return quantityRequired; }
    public void setQuantityRequired(BigDecimal quantityRequired) { this.quantityRequired = quantityRequired; }

    public BigDecimal getAvailableStock() { return availableStock; }
    public void setAvailableStock(BigDecimal availableStock) { this.availableStock = availableStock; }
}

package com.astro.dto.workflow.InventoryModule;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetMasterDto {
    private Integer assetId;
    private String materialCode;
    private String materialDesc;
    private String assetDesc;
    private String makeNo;
    private String serialNo;
    private String modelNo;
    private String uomId;
    private String componentName;
    private Integer componentId;
    private BigDecimal initQuantity;
    private BigDecimal unitPrice;
    private BigDecimal depriciationRate;
    private String endOfLife;  // Changed from LocalDate to String
    private BigDecimal stockLevels;
    private String conditionOfGoods;
    private String shelfLife;
    private String createdBy;
    private String updatedBy;
    private Integer locatorId;
}
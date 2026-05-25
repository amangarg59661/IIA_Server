package com.astro.dto.workflow.InventoryModule;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class AssetFullResponseDto {

    private Integer assetId;
    private String assetCode;
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
    private String poId;
    private BigDecimal unitPrice;
    private BigDecimal depriciationRate;
    private LocalDate endOfLife;
    private BigDecimal stockLevels;
    private String conditionOfGoods;
    private String shelfLife;
    private LocalDateTime createDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private Long igpId;
    private String updatedBy;

    // Fields from ohq_master
    private String custodianId;
    private Integer locatorId;  // ← from OhqMasterEntity
    private BigDecimal quantity;
}

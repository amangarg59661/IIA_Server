package com.astro.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetDataForGtDto {
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

    private String custodianId;
    private Integer locatorId;
    private BigDecimal quantity;
    private BigDecimal bookValue;

    private List<String> serialNumbers = new ArrayList<>();
    public AssetDataForGtDto(
            Integer assetId,
            String assetCode,
            String materialCode,
            String materialDesc,
            String assetDesc,
            String makeNo,
            String serialNo,
            String modelNo,
            String uomId,
            String componentName,
            Integer componentId,
            BigDecimal initQuantity,
            String poId,
            BigDecimal unitPrice,
            BigDecimal depriciationRate,
            LocalDate endOfLife,
            BigDecimal stockLevels,
            String conditionOfGoods,
            String shelfLife,
            LocalDateTime createDate,
            String createdBy,
            LocalDateTime updatedDate,
            Long igpId,
            String updatedBy,
            String custodianId,
            Integer locatorId,
            BigDecimal quantity,
            BigDecimal bookValue
    ) {
        this.assetId = assetId;
        this.assetCode = assetCode;
        this.materialCode = materialCode;
        this.materialDesc = materialDesc;
        this.assetDesc = assetDesc;
        this.makeNo = makeNo;
        this.serialNo = serialNo;
        this.modelNo = modelNo;
        this.uomId = uomId;
        this.componentName = componentName;
        this.componentId = componentId;
        this.initQuantity = initQuantity;
        this.poId = poId;
        this.unitPrice = unitPrice;
        this.depriciationRate = depriciationRate;
        this.endOfLife = endOfLife;
        this.stockLevels = stockLevels;
        this.conditionOfGoods = conditionOfGoods;
        this.shelfLife = shelfLife;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.updatedDate = updatedDate;
        this.igpId = igpId;
        this.updatedBy = updatedBy;
        this.custodianId = custodianId;
        this.locatorId = locatorId;
        this.quantity = quantity;
        this.bookValue = bookValue;
    }



}

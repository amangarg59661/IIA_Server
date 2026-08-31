package com.astro.dto.workflow.InventoryModule;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AssetDisposalDetailDto {
    private Integer assetId;
    private String assetCode;
    private String assetDesc;
    private String uom;
    private BigDecimal quantity;
    private String disposalCategory;
    private String disposalMode;
    private String salesNoteFilename;
    private Integer locatorId;
    private Integer ohqId;
    private BigDecimal bookValue;
    private BigDecimal depriciationRate;
    private BigDecimal unitPrice;
    private String custodianId;
    private String custodianName;
    private BigDecimal poValue;
    private String reasonForDisposal;
    private String poId;
    private String poDate;
    private String serialNo;
    private String modelNo;
}

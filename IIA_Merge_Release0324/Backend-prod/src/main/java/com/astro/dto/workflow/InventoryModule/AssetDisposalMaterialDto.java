package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetDisposalMaterialDto {
    private Integer disposalDetailId;
    private Integer disposalId;
    private Integer assetId;
    private String assetDesc;
    private BigDecimal disposalQuantity;
    private Integer locatorId;
    private BigDecimal bookValue;
    private BigDecimal depriciationRate;
    private BigDecimal unitPrice;
    private String custodianId;
    private BigDecimal poValue;
    private String uom;
    private String poId;

    private String reasonForDisposal;

    private String disposalDate;

    private String locationId;

    private String status;

}

package com.astro.dto.workflow.InventoryModule.GiDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiMaterialDtlDto {
    private String materialCode;
    private Integer assetId;

    private String materialDesc;
    private String assetDesc;
    private String category;
    private String makeNo;
    private String rejectReason;
    private String modelNo;
    private String serialNo;
    private String uomId;
    private String installationReportFileName;
    private String installationReportBase64;
    private BigDecimal receivedQuantity;
    private BigDecimal acceptedQuantity;
    private BigDecimal rejectedQuantity;
    private String rejectionType;
    private String assetCode;
    private Boolean assetFlag;
}
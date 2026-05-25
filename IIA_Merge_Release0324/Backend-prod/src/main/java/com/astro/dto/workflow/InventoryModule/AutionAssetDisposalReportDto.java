package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import java.util.List;

@Data
public class AutionAssetDisposalReportDto {
    private Integer disposalId;
    private String disposalDate;
    private String locationId;
    private String status;
    private String custodianId;
    private String createdBy;
    private String createDate;
    private String action;
    private List<AssetDisposalMaterialDto> assets;
}

package com.astro.dto.workflow.InventoryModule.grn;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrnMaterialMasterDto {
    private List<GrnMaterialInDtlDto> materialDtlList;
    private String grnType;
    private String igpId;
    private String ogpId;
    private String igpDate;
    private String igpType;
    private Integer indentId;
    private String status;
    private String grnDate;
    private String locationId;
    private String createdBy;
}


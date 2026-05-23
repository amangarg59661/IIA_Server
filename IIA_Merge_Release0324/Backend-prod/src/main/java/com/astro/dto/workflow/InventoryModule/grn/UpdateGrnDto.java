package com.astro.dto.workflow.InventoryModule.grn;

import lombok.Data;

import java.util.List;

@Data
public class UpdateGrnDto {
    private Integer grnSubProcessId;
    private String grnType;
    private String giNo;
    private String grnNo;
    private String installationDate;
    private String commissioningDate;
    private String grnDate;
    private String createdBy;
    private String systemCreatedBy;
    private String locationId;
    private List<GrnMaterialDtlDto> materialDtlList;
}

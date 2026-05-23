package com.astro.dto.workflow.InventoryModule.ogp;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialIgpDto {
    private Long id;
    private String igpId;
    private String ogpId;
    private String igpDate;
    private List<IgpMaterialDetailDto> materialDtlList;
    private String igpType;
    private String createdBy;
    private Integer indentId;
    private String custodianName;
    private String status;
    private String locationId;
}
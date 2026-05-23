package com.astro.dto.workflow.InventoryModule.igp;

import java.util.List;
import lombok.Data;

@Data
public class IgpDto {
    private String ogpId;
    private String ogpType;
    private String igpType;
    private String igpId;
    private String igpDate;
    private String locationId;
    private String createdBy;
    private List<IgpMaterialDtlDto> materialDtlList;
}
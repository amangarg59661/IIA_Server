package com.astro.dto.workflow.InventoryModule.ogp;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class OgpPoResponseDto {
    private String ogpId;
    private String poId;
    private String ogpDate;
    private String locationId;
    private String createdBy;
    private String ogpType;
    private List<OgpPoMaterialDto> materialDtlList;
}
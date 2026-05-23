package com.astro.dto.workflow.InventoryModule.igp;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class IgpMaterialInReportDto {
    private Long id;                  // Master ID
    private String status;
    private String ogpId;
    private String igpDate;
    private String igpType;
    private Integer indentId;
    private String createdBy;
    private LocalDateTime createDate;
    private String locationId;
    private List<IgpMaterialInDetailReportDto> igpDetails;
}

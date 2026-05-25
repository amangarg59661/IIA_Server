package com.astro.dto.workflow.InventoryModule.igp;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class IgpReportDto {
    private String igpProcessId;
    private Integer igpSubProcessId;
    private Integer ogpSubProcessId;
    private String igpDate;
    private String locationId;
    private String createdBy;
    private LocalDateTime createDate;
    private List<IgpDetailReportDto> igpDetails;
}
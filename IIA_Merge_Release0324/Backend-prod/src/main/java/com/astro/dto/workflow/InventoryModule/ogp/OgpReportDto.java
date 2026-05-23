package com.astro.dto.workflow.InventoryModule.ogp;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OgpReportDto {
    private String ogpProcessId;
    private Integer ogpSubProcessId;
    private Integer issueNoteId;
    private String ogpDate;
    private String locationId;
    private String createdBy;
    private LocalDateTime createDate;
    private List<OgpDetailReportDto> ogpDetails;
}
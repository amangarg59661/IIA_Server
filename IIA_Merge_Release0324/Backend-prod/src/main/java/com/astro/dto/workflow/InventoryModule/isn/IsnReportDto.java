package com.astro.dto.workflow.InventoryModule.isn;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class IsnReportDto {
    private Integer issueNoteId;
    private String issueNoteType;
    private String issueDate;
    private String consigneeDetail;
    private String indentorName;
    private String fieldStation;
    private String createdBy;
    private String createDate;
    private String locationId;
    private List<IsnReportDetailDto> details;
}
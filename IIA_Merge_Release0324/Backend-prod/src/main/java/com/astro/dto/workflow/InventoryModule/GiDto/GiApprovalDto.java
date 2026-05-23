package com.astro.dto.workflow.InventoryModule.GiDto;

import lombok.Data;

@Data
public class GiApprovalDto {
    private String processNo;
    private String remarks;
    private String status;
    private String createdBy;
    private String rejectionReason;  // mandatory when SPO rejects
}

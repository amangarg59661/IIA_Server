package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

@Data
public class approvedTenderIdWithTitle {
    private String tenderId;
    private String title;
    /** Vendor's submission status for this tender: SUBMITTED, CHANGE_REQUESTED, null = pending */
    private String actionStatus;
}

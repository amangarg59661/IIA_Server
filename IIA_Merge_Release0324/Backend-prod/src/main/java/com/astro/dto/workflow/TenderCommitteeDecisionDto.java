package com.astro.dto.workflow;

import lombok.Data;

@Data
public class TenderCommitteeDecisionDto {
    private String tenderId;
    // APPROVED, REJECTED, or OVERRIDE
    private String decision;
    private String remarks;
    private Integer chairmanUserId;
    // Set to true if chairman is overriding committee majority
    private Boolean isOverride;
    private String overrideReason;
}
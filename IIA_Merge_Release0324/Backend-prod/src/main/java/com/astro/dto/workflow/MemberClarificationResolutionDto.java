package com.astro.dto.workflow;

import lombok.Data;

@Data
public class MemberClarificationResolutionDto {
    private String action;          // FORWARD_TO_VENDOR or REJECT
    private String remarks;         // Chairman's question for vendor (if forward) or rejection reason
    private Integer chairmanUserId;
}

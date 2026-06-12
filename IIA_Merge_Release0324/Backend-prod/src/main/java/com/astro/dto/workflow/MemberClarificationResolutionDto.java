package com.astro.dto.workflow;

import lombok.Data;

import java.util.List;

@Data
public class MemberClarificationResolutionDto {
    private String action;          // FORWARD_TO_VENDOR or DISMISS
    private String remarks;         // Chairman's question for vendor (if forward) or dismissal reason
    private Integer chairmanUserId;
    private List<Long> historyIds;  // specific clarification history IDs to resolve (null = all open)
}

package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response for GET /api/dashboard/approverSummary. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproverSummaryDto {
    private Long approvedThisMonth;
    private Long rejectedThisMonth;
    private Double avgApprovalTimeDays;
}

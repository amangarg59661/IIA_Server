package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response for GET /api/dashboard/purchasePersonnelSummary. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchasePersonnelSummaryDto {
    private Long totalPos;
    private Long approvedPos;
    private Long pendingApprovals;
    private Long rejectedPos;
}

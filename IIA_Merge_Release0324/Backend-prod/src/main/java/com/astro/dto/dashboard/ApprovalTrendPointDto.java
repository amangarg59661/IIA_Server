package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One row of GET /api/dashboard/approvalTrend. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalTrendPointDto {
    private String month;
    private Long approved;
    private Long rejected;
}

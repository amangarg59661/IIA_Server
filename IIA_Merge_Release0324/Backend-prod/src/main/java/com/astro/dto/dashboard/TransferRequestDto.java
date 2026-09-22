package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * One row of GET /api/dashboard/recentTransferRequests.
 *
 * from/to are raw location IDs (gt_master.sender_location_id /
 * receiver_location_id) — no Location master available yet to resolve these
 * to display names like "FSL"/"BGLR" from the reference screenshot.
 * itemCategory uses the first joined line item's material/asset description
 * (gt_dtl has no explicit category column either) — a multi-item transfer
 * only shows its first item here.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDto {
    private String requestNo;
    private LocalDateTime dateTime;
    private String itemCategory;
    private String from;
    private String to;
    private String status;
}

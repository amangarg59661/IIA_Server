package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** One row of GET /api/dashboard/indentorRecentRequests. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentIndentDto {
    private String requestId;
    private String item;
    private String status;
    private LocalDateTime requestedOn;
}

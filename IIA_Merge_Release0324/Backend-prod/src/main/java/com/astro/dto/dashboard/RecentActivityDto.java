package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One row of GET /api/dashboard/spoRecentActivity. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDto {
    private String time;
    private String activity;
    private String details;
}

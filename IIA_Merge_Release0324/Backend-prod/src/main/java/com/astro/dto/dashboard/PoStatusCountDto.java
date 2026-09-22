package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** One row of GET /api/dashboard/poStatusBreakdown. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PoStatusCountDto {
    private String status;
    private Long count;
}

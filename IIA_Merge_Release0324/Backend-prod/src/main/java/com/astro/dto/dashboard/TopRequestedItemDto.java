package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One row of GET /api/dashboard/indentorTopItems.
 * Covers material-type indent lines (MaterialDetails) only — job-type lines
 * (JobDetails) aren't included, so an indentor who mostly raises job/service
 * indents will see a thin or empty chart here.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopRequestedItemDto {
    private String itemName;
    private Long count;
}

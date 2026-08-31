package com.astro.dto.dashboard;

import lombok.Data;
@Data
public class CycleStatusSummaryDto {
    private String processName;
    private String status;
    private long count;

    public CycleStatusSummaryDto(String processName, String status, long count) {
        this.processName = processName;
        this.status = status;
        this.count = count;
    }
    // getters/setters
}

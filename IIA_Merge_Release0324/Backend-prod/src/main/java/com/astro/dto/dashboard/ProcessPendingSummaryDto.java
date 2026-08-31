package com.astro.dto.dashboard;
import lombok.Data;

@Data
public class ProcessPendingSummaryDto {
    private String processName;
    private long pendingCount;
    private long oldestPendingDays;

    public ProcessPendingSummaryDto(
            String processName,
            long pendingCount,
            long oldestPendingDays) {
        this.processName = processName;
        this.pendingCount = pendingCount;
        this.oldestPendingDays = oldestPendingDays;
    }
}
package com.astro.dto.dashboard;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PendingItemDetailDto {
    private String requestId;
    private long pendingDays;
    private LocalDateTime since;

    public PendingItemDetailDto(String requestId, long pendingDays, LocalDateTime since) {
        this.requestId = requestId;
        this.pendingDays = pendingDays;
        this.since = since;
    }
    // getters/setters
}
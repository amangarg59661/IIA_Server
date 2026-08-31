package com.astro.dto.dashboard;

import java.time.LocalDateTime;
import lombok.Data;
@Data
public class TodayActivityDto {
    private String processName;
    private String requestId;
    private String action;
    private String activityType; // "CREATED" or "ACTIONED"
    private LocalDateTime activityTime;

    public TodayActivityDto(String processName, String requestId, String action, String activityType, LocalDateTime activityTime) {
        this.processName = processName;
        this.requestId = requestId;
        this.action = action;
        this.activityType = activityType;
        this.activityTime = activityTime;
    }
    // getters/setters
}
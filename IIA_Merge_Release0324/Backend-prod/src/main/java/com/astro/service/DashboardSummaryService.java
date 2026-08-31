package com.astro.service;

import com.astro.dto.dashboard.CycleStatusSummaryDto;
import com.astro.dto.dashboard.PendingItemDetailDto;
import com.astro.dto.dashboard.ProcessPendingSummaryDto;
import com.astro.dto.dashboard.TodayActivityDto;

import java.util.List;

public interface DashboardSummaryService {
    List<ProcessPendingSummaryDto> getPendingSummary(String roleName);
    List<TodayActivityDto> getTodaysActivity(String roleName, Integer userId);
    List<CycleStatusSummaryDto> getCycleStatusSummary();
    List<PendingItemDetailDto> getPendingDetail(String roleName, String processName);
}
package com.astro.controller;

import com.astro.dto.dashboard.*;
import com.astro.service.DashboardSummaryService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardSummaryService dashboardSummaryService;

    @GetMapping("/dashboardPendingSummary")
    public ResponseEntity<Object> getPendingSummary(@RequestParam String roleName) {
        List<ProcessPendingSummaryDto> data = dashboardSummaryService.getPendingSummary(roleName);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/dashboardTodayActivity")
    public ResponseEntity<Object> getTodayActivity(@RequestParam String roleName, @RequestParam Integer userId) {
        List<TodayActivityDto> data = dashboardSummaryService.getTodaysActivity(roleName, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/dashboardCycleSummary")
    public ResponseEntity<Object> getCycleSummary() {
        List<CycleStatusSummaryDto> data = dashboardSummaryService.getCycleStatusSummary();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }
     @GetMapping("/dashboardPendingDetail")
    public ResponseEntity<Object> getPendingDetail(@RequestParam String roleName, @RequestParam String processName) {
        List<PendingItemDetailDto> data = dashboardSummaryService.getPendingDetail(roleName, processName);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }
}
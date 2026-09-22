// package com.astro.controller;

// import com.astro.dto.dashboard.*;
// import com.astro.service.DashboardSummaryService;
// import com.astro.util.ResponseBuilder;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/dashboard")
// public class DashboardController {

//     @Autowired
//     private DashboardSummaryService dashboardSummaryService;

//     @GetMapping("/dashboardPendingSummary")
//     public ResponseEntity<Object> getPendingSummary(@RequestParam String roleName) {
//         List<ProcessPendingSummaryDto> data = dashboardSummaryService.getPendingSummary(roleName);
//         return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
//     }

//     @GetMapping("/dashboardTodayActivity")
//     public ResponseEntity<Object> getTodayActivity(@RequestParam String roleName, @RequestParam Integer userId) {
//         List<TodayActivityDto> data = dashboardSummaryService.getTodaysActivity(roleName, userId);
//         return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
//     }

//     @GetMapping("/dashboardCycleSummary")
//     public ResponseEntity<Object> getCycleSummary() {
//         List<CycleStatusSummaryDto> data = dashboardSummaryService.getCycleStatusSummary();
//         return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
//     }
//      @GetMapping("/dashboardPendingDetail")
//     public ResponseEntity<Object> getPendingDetail(@RequestParam String roleName, @RequestParam String processName) {
//         List<PendingItemDetailDto> data = dashboardSummaryService.getPendingDetail(roleName, processName);
//         return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
//     }
// }

package com.astro.controller;

import com.astro.dto.dashboard.*;
import com.astro.service.DashboardSummaryService;
// NOTE: WorkflowService and QueueResponse package names below are inferred
// from WorkflowServiceImpl's @Override methods and mapToQueueResponse
// return type, not directly confirmed — adjust the imports if they differ.
import com.astro.service.WorkflowService;
import com.astro.dto.workflow.QueueResponse;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardSummaryService dashboardSummaryService;

    @Autowired
    private WorkflowService workflowService;

    // ---------- existing endpoints (unchanged) ----------

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

    // ---------- new: SPO / P&I Officer dashboard ----------

    @GetMapping("/spoSummary")
    public ResponseEntity<Object> getSpoSummary() {
        SpoSummaryDto data = dashboardSummaryService.getSpoSummary();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/spoSpendByCategory")
    public ResponseEntity<Object> getSpoSpendByCategory() {
        List<CategorySpendDto> data = dashboardSummaryService.getSpoSpendByCategory();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/spoRecentActivity")
    public ResponseEntity<Object> getSpoRecentActivity() {
        List<RecentActivityDto> data = dashboardSummaryService.getSpoRecentActivity();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    // ---------- new: Purchase Personnel dashboard ----------

    @GetMapping("/purchasePersonnelSummary")
    public ResponseEntity<Object> getPurchasePersonnelSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        PurchasePersonnelSummaryDto data = dashboardSummaryService.getPurchasePersonnelSummary(startDate, endDate);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/poStatusBreakdown")
    public ResponseEntity<Object> getPoStatusBreakdown(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<PoStatusCountDto> data = dashboardSummaryService.getPoStatusBreakdown(startDate, endDate);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/procurementTransactions")
    public ResponseEntity<Object> getProcurementTransactions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ProcurementTransactionDto> data = dashboardSummaryService.getProcurementTransactions(startDate, endDate);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    // ---------- new: Store Person dashboard ----------

    @GetMapping("/stockSummary")
    public ResponseEntity<Object> getStockSummary() {
        StockSummaryDto data = dashboardSummaryService.getStockSummary();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/poGrnPaymentStatus")
    public ResponseEntity<Object> getPoGrnPaymentStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        PoGrnPaymentStatusDto data = dashboardSummaryService.getPoGrnPaymentStatus(startDate, endDate);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    // ---------- new: Approver dashboard ----------

    @GetMapping("/approverSummary")
    public ResponseEntity<Object> getApproverSummary(@RequestParam Integer userId) {
        ApproverSummaryDto data = dashboardSummaryService.getApproverSummary(userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/approvalTrend")
    public ResponseEntity<Object> getApprovalTrend(@RequestParam Integer userId) {
        List<ApprovalTrendPointDto> data = dashboardSummaryService.getApprovalTrend(userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    // Delegates to the existing WorkflowServiceImpl.allPendingWorkflowTransitionINQueue
    // rather than reimplementing queue/location-filtering logic — see the class-level
    // note above about confirming WorkflowService/QueueResponse's real package names.
    @GetMapping("/approverQueue")
    public ResponseEntity<Object> getApproverQueue(@RequestParam String roleName, @RequestParam Integer userId) {
        List<QueueResponse> data = workflowService.allPendingWorkflowTransitionINQueue(roleName, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    //     @GetMapping("/indentorRecentRequests")
    // public ResponseEntity<Object> getIndentorRecentRequests(
    //         @RequestParam Integer userId,
    //         @RequestParam(defaultValue = "5") int limit) {
    //     List<RecentIndentDto> data = dashboardSummaryService.getIndentorRecentRequests(userId, limit);
    //     return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    // }

    // @GetMapping("/indentorTopItems")
    // public ResponseEntity<Object> getIndentorTopItems(@RequestParam Integer userId) {
    //     List<TopRequestedItemDto> data = dashboardSummaryService.getIndentorTopItems(userId);
    //     return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    // }

    // // ---------- new: Store Person dashboard — gate passes & transfers ----------

 @GetMapping("/indentorSummary")
    public ResponseEntity<Object> getIndentorSummary(@RequestParam Integer userId) {
        IndentorSummaryDto data = dashboardSummaryService.getIndentorSummary(userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/indentorRecentRequests")
    public ResponseEntity<Object> getIndentorRecentRequests(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "5") int limit) {
        List<RecentIndentDto> data = dashboardSummaryService.getIndentorRecentRequests(userId, limit);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/indentorTopItems")
    public ResponseEntity<Object> getIndentorTopItems(@RequestParam Integer userId) {
        List<TopRequestedItemDto> data = dashboardSummaryService.getIndentorTopItems(userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    // ---------- new: Store Person dashboard — gate passes & transfers ----------

    @GetMapping("/recentGatePasses")
    public ResponseEntity<Object> getRecentGatePasses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<GatePassDto> data = dashboardSummaryService.getRecentGatePasses(startDate, endDate);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }

    @GetMapping("/recentTransferRequests")
    public ResponseEntity<Object> getRecentTransferRequests(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<TransferRequestDto> data = dashboardSummaryService.getRecentTransferRequests(startDate, endDate);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(data), HttpStatus.OK);
    }
}



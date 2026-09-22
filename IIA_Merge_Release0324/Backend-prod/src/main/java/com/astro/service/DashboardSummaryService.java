// package com.astro.service;

// import com.astro.dto.dashboard.CycleStatusSummaryDto;
// import com.astro.dto.dashboard.PendingItemDetailDto;
// import com.astro.dto.dashboard.ProcessPendingSummaryDto;
// import com.astro.dto.dashboard.TodayActivityDto;

// import java.util.List;

// public interface DashboardSummaryService {
//     List<ProcessPendingSummaryDto> getPendingSummary(String roleName);
//     List<TodayActivityDto> getTodaysActivity(String roleName, Integer userId);
//     List<CycleStatusSummaryDto> getCycleStatusSummary();
//     List<PendingItemDetailDto> getPendingDetail(String roleName, String processName);
// }


package com.astro.service;

import com.astro.dto.dashboard.*;

import java.time.LocalDate;
import java.util.List;

/**
 * NOTE: I don't have your actual DashboardSummaryService.java, so the four
 * "existing" method signatures below are reconstructed from
 * DashboardController's calls and DashboardSummaryServiceImpl's @Override
 * methods rather than copied from source — double check them against the
 * real file before overwriting it. The ten methods under "new" are net
 * additions for the 5-dashboard redesign.
 */
public interface DashboardSummaryService {

    // ---- existing ----
    List<ProcessPendingSummaryDto> getPendingSummary(String roleName);
    List<TodayActivityDto> getTodaysActivity(String roleName, Integer userId);
    List<CycleStatusSummaryDto> getCycleStatusSummary();
    List<PendingItemDetailDto> getPendingDetail(String roleName, String processName);

    // ---- new: SPO / P&I Officer dashboard ----
    SpoSummaryDto getSpoSummary();
    List<CategorySpendDto> getSpoSpendByCategory();
    List<RecentActivityDto> getSpoRecentActivity();

    // ---- new: Purchase Personnel dashboard ----
    PurchasePersonnelSummaryDto getPurchasePersonnelSummary(LocalDate startDate, LocalDate endDate);
    List<PoStatusCountDto> getPoStatusBreakdown(LocalDate startDate, LocalDate endDate);
    List<ProcurementTransactionDto> getProcurementTransactions(LocalDate startDate, LocalDate endDate);

    // ---- new: Store Person dashboard ----
    StockSummaryDto getStockSummary();
    PoGrnPaymentStatusDto getPoGrnPaymentStatus(LocalDate startDate, LocalDate endDate);

    // ---- new: Approver dashboard ----
    ApproverSummaryDto getApproverSummary(Integer userId);
    List<ApprovalTrendPointDto> getApprovalTrend(Integer userId);

        // ---- new: Indentor dashboard ----
    IndentorSummaryDto getIndentorSummary(Integer userId);
    List<RecentIndentDto> getIndentorRecentRequests(Integer userId, int limit);
    List<TopRequestedItemDto> getIndentorTopItems(Integer userId);


     // ---- new: Store Person dashboard — gate passes & transfers ----
    List<GatePassDto> getRecentGatePasses(LocalDate startDate, LocalDate endDate);
    List<TransferRequestDto> getRecentTransferRequests(LocalDate startDate, LocalDate endDate);
}
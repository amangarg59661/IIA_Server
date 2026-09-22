package com.astro.service.impl;

import com.astro.dto.dashboard.*;
import com.astro.entity.WorkflowTransition;
import com.astro.entity.InventoryModule.*;
import com.astro.repository.WorkflowTransitionRepository;
import com.astro.repository.InventoryModule.GiRepository.*;
import com.astro.repository.InventoryModule.GprnRepository.GprnMasterRepository;
import com.astro.repository.InventoryModule.grn.*;
import com.astro.repository.InventoryModule.GoodsTransfer.GtMasterRepository;
import com.astro.repository.InventoryModule.igp.IgpMaterialMasterRepository;
import com.astro.repository.InventoryModule.ogp.*;
 import com.astro.repository.InventoryModule.*;
 import com.astro.entity.ProcurementModule.PurchaseOrder;
import com.astro.entity.ProcurementModule.PurchaseOrderAttributes;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository;   // adjust package if different
import com.astro.repository.ProcurementModule.ServiceOrderRepository.ServiceOrderRepository;    // adjust package if different
import com.astro.repository.ProcurementModule.ContigencyPurchaseRepository; // adjust package if different
import com.astro.repository.ProcurementModule.CpMaterialRepository;     // adjust package if different
import com.astro.repository.ohq.OhqMasterRepository;           // adjust package if different
import com.astro.repository.InventoryModule.OhqMasterConsumableRepository;
import com.astro.repository.InventoryModule.OhqConsumableStoreStockRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.astro.service.DashboardSummaryService;
import java.time.format.DateTimeFormatter;

import com.astro.entity.ProcurementModule.IndentCreation;
import com.astro.entity.ProcurementModule.MaterialDetails;
import com.astro.entity.ProcurementModule.JobDetails;
import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
import com.astro.repository.ProcurementModule.IndentCreation.MaterialDetailsRepository;
import org.springframework.data.domain.PageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;


import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DashboardSummaryServiceImpl  implements DashboardSummaryService {

    @Autowired private WorkflowTransitionRepository workflowTransitionRepository;
    @Autowired private GprnMasterRepository gprnMasterRepository;
    @Autowired private GiMasterRepository giMasterRepository;
    @Autowired private GrnMasterRepository grnMasterRepository;
    @Autowired private GtMasterRepository gtMasterRepository;
    @Autowired private IgpMaterialMasterRepository igpMaterialMasterRepository;
    @Autowired private OgpMasterRepository ogpMasterRepository;
    @Autowired private OgpMasterPoRepository ogpMasterPoRepository;
    @Autowired private OgpMasterRejectedGiRepository ogpMasterRejectedGiRepository;
    @Autowired private OgpGtMasterRepository ogpGtMasterRepository;
    @Autowired private DemandAndIssueMasterEntityRepository demandAndIssueMasterEntityRepository;
    @Autowired private AssetDisposalMasterRepository assetDisposalMasterRepository;
    @Autowired private OgpAssetDisposalRepository ogpAssetDisposalRepository;
    @Autowired private GiWorkflowStatusRepository giWorkflowStatusRepository;
    @Autowired private GrnWorkflowStatusRepository grnWorkflowStatusRepository;
    @Autowired private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired private ServiceOrderRepository serviceOrderRepository;
    @Autowired private ContigencyPurchaseRepository contigencyPurchaseRepository;
    @Autowired private CpMaterialRepository cpMaterialsRepository;
    @Autowired private OhqMasterRepository ohqMasterEntityRepository;
    @Autowired private OhqMasterConsumableRepository ohqMasterConsumableEntityRepository;
    @Autowired private OhqConsumableStoreStockRepository ohqMasterConsumableStoreStockEntityRepository;
    @Autowired private IndentCreationRepository indentCreationRepository;
    @Autowired private MaterialDetailsRepository materialDetailsRepository;


    private static final List<String> PURCHASE_STORES_ROLES = List.of(
        "Store Purchase Officer", "Purchase personnel", "Store Person", "Purchase Head",
        "CP Creator", "PO Creator", "SO Creator"
    );

    private boolean isPurchaseAndStoresRole(String roleName) {
        return PURCHASE_STORES_ROLES.contains(roleName);
    }

    // ---------- Pending-by-process ----------
    @Override
    public List<ProcessPendingSummaryDto> getPendingSummary(String roleName) {
        List<ProcessPendingSummaryDto> summary = new ArrayList<>(
            groupWorkflowTransitionsByProcess(workflowTransitionRepository.findPendingTransitionsByRole(roleName))
        );

        if (isPurchaseAndStoresRole(roleName)) {
            addIfPresent(summary, pendingSummaryFor("GPRN", gprnMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), GprnMasterEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryFor("GI", giMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), GiMasterEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryFor("GRN", grnMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), GrnMasterEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryFor("GT", gtMasterRepository.findByStatusInOrderByCreateDateAsc(List.of("PENDING RECEIVER APPROVAL", "AWAITING APPROVAL")), GtMasterEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryFor("IGP", igpMaterialMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), IgpMaterialMasterEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryFor("OGP", ogpMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), OgpMasterEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryFor("OGP (PO)", ogpMasterPoRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), OgpMasterPoEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryForLocalDate("OGP (Rejected GI)", ogpMasterRejectedGiRepository.findByStatusOrderByOgpDateAsc("AWAITING APPROVAL"), OgpMasterRejectedGiEntity::getOgpDate));
            addIfPresent(summary, pendingSummaryFor("OGP (GT)", ogpGtMasterRepository.findByStatusInOrderByCreateDateAsc(List.of("PENDING RECEIVER APPROVAL", "RECEIVER APPROVED")), OgpGtMasterEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryFor("Demand & Issue", demandAndIssueMasterEntityRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), DemandAndIssueMasterEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryFor("Asset Disposal", assetDisposalMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), AssetDisposalMasterEntity::getCreateDate));
            addIfPresent(summary, pendingSummaryFor("Asset Disposal (Auction)", ogpAssetDisposalRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), OgpAssetDisposal::getCreateDate));
        }
        return summary;
    }

    private List<ProcessPendingSummaryDto> groupWorkflowTransitionsByProcess(List<WorkflowTransition> transitions) {
        Map<String, List<WorkflowTransition>> byProcess = transitions.stream()
            .collect(Collectors.groupingBy(WorkflowTransition::getWorkflowName));
        List<ProcessPendingSummaryDto> result = new ArrayList<>();
        byProcess.forEach((processName, items) -> {
            long oldestDays = items.stream()
                .map(WorkflowTransition::getCreatedDate)
                .filter(Objects::nonNull)
                .mapToLong(d -> ChronoUnit.DAYS.between(d.toInstant(), Instant.now()))
                .max().orElse(0);
            result.add(new ProcessPendingSummaryDto(processName, items.size(), oldestDays));
        });
        return result;
    }

    private <T> ProcessPendingSummaryDto pendingSummaryFor(String processName, List<T> items, Function<T, LocalDateTime> dateExtractor) {
        long oldestDays = items.stream()
            .map(dateExtractor).filter(Objects::nonNull)
            .mapToLong(d -> ChronoUnit.DAYS.between(d, LocalDateTime.now()))
            .max().orElse(0);
        return new ProcessPendingSummaryDto(processName, items.size(), oldestDays);
    }

    private <T> ProcessPendingSummaryDto pendingSummaryForLocalDate(String processName, List<T> items, Function<T, LocalDate> dateExtractor) {
        long oldestDays = items.stream()
            .map(dateExtractor).filter(Objects::nonNull)
            .mapToLong(d -> ChronoUnit.DAYS.between(d, LocalDate.now()))
            .max().orElse(0);
        return new ProcessPendingSummaryDto(processName, items.size(), oldestDays);
    }

    private void addIfPresent(List<ProcessPendingSummaryDto> summary, ProcessPendingSummaryDto dto) {
        if (dto.getPendingCount() > 0) summary.add(dto);
    }

    // ---------- Today's activity ----------
    @Override
    public List<TodayActivityDto> getTodaysActivity(String roleName, Integer userId) {
        List<TodayActivityDto> activity = new ArrayList<>();
        String userIdStr = String.valueOf(userId);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        Date startUtil = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
        Date endUtil = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

        workflowTransitionRepository.findByCreatedByAndCreatedDateBetween(userIdStr, startUtil, endUtil)
            .forEach(wt -> activity.add(new TodayActivityDto(wt.getWorkflowName(), wt.getRequestId(), wt.getAction(), "CREATED", toLdt(wt.getCreatedDate()))));
        workflowTransitionRepository.findByUpdatedByAndModificationDateBetween(userIdStr, startUtil, endUtil)
            .forEach(wt -> activity.add(new TodayActivityDto(wt.getWorkflowName(), wt.getRequestId(), wt.getAction(), "ACTIONED", toLdt(wt.getModificationDate()))));

        giWorkflowStatusRepository.findByCreatedByAndCreateDateBetween(userIdStr, startOfDay, endOfDay)
            .forEach(g -> activity.add(new TodayActivityDto("GI", g.getProcessId() + "/" + g.getSubProcessId(), g.getAction(), "ACTIONED", g.getCreateDate())));
        grnWorkflowStatusRepository.findByCreatedByAndCreateDateBetween(userIdStr, startOfDay, endOfDay)
            .forEach(g -> activity.add(new TodayActivityDto("GRN", g.getProcessId() + "/" + g.getSubProcessId(), g.getAction(), "ACTIONED", g.getCreateDate())));

        if (isPurchaseAndStoresRole(roleName)) {
            addCreatedToday(activity, "GPRN", gprnMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), userIdStr, GprnMasterEntity::getCreatedBy, GprnMasterEntity::getCreateDate, e -> String.valueOf(e.getSubProcessId()), e -> "CREATED");
            addActionedToday(activity, "GPRN", gprnMasterRepository.findByUpdatedByAndUpdateDateBetween(userIdStr, startOfDay, endOfDay), e -> String.valueOf(e.getSubProcessId()), GprnMasterEntity::getStatus, GprnMasterEntity::getUpdateDate);

            addCreatedToday(activity, "GT", gtMasterRepository.findByStatusInOrderByCreateDateAsc(List.of("PENDING RECEIVER APPROVAL", "AWAITING APPROVAL")), userIdStr, GtMasterEntity::getCreatedBy, GtMasterEntity::getCreateDate, e -> String.valueOf(e.getId()), e -> "CREATED");
            addActionedToday(activity, "GT", gtMasterRepository.findByUpdatedByAndUpdateDateBetween(userIdStr, startOfDay, endOfDay), e -> String.valueOf(e.getId()), GtMasterEntity::getStatus, GtMasterEntity::getUpdateDate);

            addCreatedToday(activity, "IGP", igpMaterialMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), userIdStr, IgpMaterialMasterEntity::getCreatedBy, IgpMaterialMasterEntity::getCreateDate, e -> String.valueOf(e.getId()), e -> "CREATED");
            addActionedToday(activity, "IGP", igpMaterialMasterRepository.findByUpdatedByAndUpdateDateBetween(userIdStr, startOfDay, endOfDay), e -> String.valueOf(e.getId()), IgpMaterialMasterEntity::getStatus, IgpMaterialMasterEntity::getUpdateDate);

            addCreatedToday(activity, "OGP", ogpMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), userIdStr, OgpMasterEntity::getCreatedBy, OgpMasterEntity::getCreateDate, e -> String.valueOf(e.getOgpSubProcessId()), e -> "CREATED");
            addActionedToday(activity, "OGP", ogpMasterRepository.findByUpdatedByAndUpdateDateBetween(userIdStr, startOfDay, endOfDay), e -> String.valueOf(e.getOgpSubProcessId()), OgpMasterEntity::getStatus, OgpMasterEntity::getUpdateDate);

            addCreatedToday(activity, "Demand & Issue", demandAndIssueMasterEntityRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), userIdStr, DemandAndIssueMasterEntity::getCreatedBy, DemandAndIssueMasterEntity::getCreateDate, e -> String.valueOf(e.getId()), e -> "CREATED");
            addActionedToday(activity, "Demand & Issue", demandAndIssueMasterEntityRepository.findByUpdatedByAndUpdateDateBetween(userIdStr, startOfDay, endOfDay), e -> String.valueOf(e.getId()), DemandAndIssueMasterEntity::getStatus, DemandAndIssueMasterEntity::getUpdateDate);

            addCreatedToday(activity, "Asset Disposal", assetDisposalMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL"), userIdStr, AssetDisposalMasterEntity::getCreatedBy, AssetDisposalMasterEntity::getCreateDate, e -> String.valueOf(e.getDisposalId()), e -> "CREATED");
            addActionedToday(activity, "Asset Disposal", assetDisposalMasterRepository.findByUpdatedByAndUpdateDateBetween(userIdStr, startOfDay, endOfDay), e -> String.valueOf(e.getDisposalId()), AssetDisposalMasterEntity::getAction, AssetDisposalMasterEntity::getUpdateDate);

            // OGP (PO), OGP (Rejected GI), OGP (GT), Asset Disposal (Auction) follow the identical addCreatedToday/addActionedToday pattern shown above
        }
        return activity;
    }

    private LocalDateTime toLdt(Date d) {
        return d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private <T> void addCreatedToday(List<TodayActivityDto> activity, String processName, List<T> pendingItems, String userIdStr,
                                      Function<T, String> createdByExtractor, Function<T, LocalDateTime> createDateExtractor,
                                      Function<T, String> idExtractor, Function<T, String> actionLabel) {
        LocalDate today = LocalDate.now();
        pendingItems.stream()
            .filter(i -> userIdStr.equals(createdByExtractor.apply(i)))
            .filter(i -> createDateExtractor.apply(i) != null && createDateExtractor.apply(i).toLocalDate().equals(today))
            .forEach(i -> activity.add(new TodayActivityDto(processName, idExtractor.apply(i), actionLabel.apply(i), "CREATED", createDateExtractor.apply(i))));
    }

    private <T> void addActionedToday(List<TodayActivityDto> activity, String processName, List<T> updatedItems,
                                       Function<T, String> idExtractor, Function<T, String> statusExtractor, Function<T, LocalDateTime> updateDateExtractor) {
        updatedItems.forEach(i -> activity.add(new TodayActivityDto(processName, idExtractor.apply(i), statusExtractor.apply(i), "ACTIONED", updateDateExtractor.apply(i))));
    }

    // ---------- Purchase & Stores cycle-status summary ----------
    @Override
    public List<CycleStatusSummaryDto> getCycleStatusSummary() {
        List<CycleStatusSummaryDto> result = new ArrayList<>();
        addStatusCounts(result, "GPRN", gprnMasterRepository.findAll(), GprnMasterEntity::getStatus);
        addStatusCounts(result, "GI", giMasterRepository.findAll(), GiMasterEntity::getStatus);
        addStatusCounts(result, "GRN", grnMasterRepository.findAll(), GrnMasterEntity::getStatus);
        addStatusCounts(result, "GT", gtMasterRepository.findAll(), GtMasterEntity::getStatus);
        addStatusCounts(result, "IGP", igpMaterialMasterRepository.findAll(), IgpMaterialMasterEntity::getStatus);
        addStatusCounts(result, "OGP", ogpMasterRepository.findAll(), OgpMasterEntity::getStatus);
        addStatusCounts(result, "OGP (PO)", ogpMasterPoRepository.findAll(), OgpMasterPoEntity::getStatus);
        addStatusCounts(result, "Demand & Issue", demandAndIssueMasterEntityRepository.findAll(), DemandAndIssueMasterEntity::getStatus);
        addStatusCounts(result, "Asset Disposal", assetDisposalMasterRepository.findAll(), AssetDisposalMasterEntity::getStatus);
        addStatusCounts(result, "Asset Disposal (Auction)", ogpAssetDisposalRepository.findAll(), OgpAssetDisposal::getStatus);
        return result;
    }

    // private <T> void addStatusCounts(List<CycleStatusSummaryDto> result, String processName, List<T> items, Function<T, String> statusExtractor) {
    //     items.stream()
    //         .collect(Collectors.groupingBy(statusExtractor, Collectors.counting()))
    //         .forEach((status, count) -> result.add(new CycleStatusSummaryDto(processName, status, count)));
    // }
    private <T> void addStatusCounts(List<CycleStatusSummaryDto> result, String processName, List<T> items, Function<T, String> statusExtractor) {
    items.stream()
        .collect(Collectors.groupingBy(
            item -> Optional.ofNullable(statusExtractor.apply(item)).orElse("UNKNOWN"),
            Collectors.counting()
        ))
        .forEach((status, count) -> result.add(new CycleStatusSummaryDto(processName, status, count)));
}
@Override
public List<PendingItemDetailDto> getPendingDetail(String roleName, String processName) {
    List<WorkflowTransition> wtPending = workflowTransitionRepository.findPendingTransitionsByRole(roleName);
    boolean isWorkflowProcess = wtPending.stream().anyMatch(wt -> processName.equals(wt.getWorkflowName()));
    if (isWorkflowProcess) {
        return wtPending.stream()
            .filter(wt -> processName.equals(wt.getWorkflowName()))
            .map(wt -> new PendingItemDetailDto(wt.getRequestId(), daysBetween(wt.getCreatedDate()), toLdt(wt.getCreatedDate())))
            .collect(Collectors.toList());
    }

    switch (processName) {
        case "GPRN":
            return gprnMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLdt("INV" + e.getProcessId() + "/" + e.getSubProcessId(), e.getCreateDate()))
                .collect(Collectors.toList());
        case "GI":
            return giMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLdt("INV" + e.getGprnProcessId() + "/" + e.getInspectionSubProcessId(), e.getCreateDate()))
                .collect(Collectors.toList());
        case "GRN":
            return grnMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLdt("INV" + e.getGrnProcessId() + "/" + e.getGrnSubProcessId(), e.getCreateDate()))
                .collect(Collectors.toList());
        case "GT":
            return gtMasterRepository.findByStatusInOrderByCreateDateAsc(List.of("PENDING RECEIVER APPROVAL", "AWAITING APPROVAL")).stream()
                .map(e -> detailLdt("INV/" + e.getId(), e.getCreateDate()))
                .collect(Collectors.toList());
        case "IGP":
            return igpMaterialMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLdt("INV/" + e.getId(), e.getCreateDate()))
                .collect(Collectors.toList());
        case "OGP":
            return ogpMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLdt("INV/" + e.getOgpSubProcessId(), e.getCreateDate()))
                .collect(Collectors.toList());
        case "OGP (PO)":
            return ogpMasterPoRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLdt("INV/" + e.getOgpSubProcessId(), e.getCreateDate()))
                .collect(Collectors.toList());
        case "OGP (Rejected GI)":
            return ogpMasterRejectedGiRepository.findByStatusOrderByOgpDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLocalDate("INV/" + e.getOgpSubProcessId(), e.getOgpDate()))
                .collect(Collectors.toList());
        case "OGP (GT)":
            return ogpGtMasterRepository.findByStatusInOrderByCreateDateAsc(List.of("PENDING RECEIVER APPROVAL", "RECEIVER APPROVED")).stream()
                .map(e -> detailLdt("INV/" + e.getId(), e.getCreateDate()))
                .collect(Collectors.toList());
        case "Demand & Issue":
            return demandAndIssueMasterEntityRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLdt(String.valueOf(e.getId()), e.getCreateDate()))
                .collect(Collectors.toList());
        case "Asset Disposal":
            return assetDisposalMasterRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLdt(String.valueOf(e.getDisposalId()), e.getCreateDate()))
                .collect(Collectors.toList());
        case "Asset Disposal (Auction)":
            return ogpAssetDisposalRepository.findByStatusOrderByCreateDateAsc("AWAITING APPROVAL").stream()
                .map(e -> detailLdt("INV/" + e.getDisposalOgpId(), e.getCreateDate()))
                .collect(Collectors.toList());
        default:
            return Collections.emptyList();
    }
}

private PendingItemDetailDto detailLdt(String requestId, LocalDateTime createDate) {
    long days = createDate == null ? 0 : ChronoUnit.DAYS.between(createDate, LocalDateTime.now());
    return new PendingItemDetailDto(requestId, days, createDate);
}

private PendingItemDetailDto detailLocalDate(String requestId, LocalDate date) {
    long days = date == null ? 0 : ChronoUnit.DAYS.between(date, LocalDate.now());
    return new PendingItemDetailDto(requestId, days, date == null ? null : date.atStartOfDay());
}

private long daysBetween(Date createdDate) {
    return createdDate == null ? 0 : ChronoUnit.DAYS.between(createdDate.toInstant(), Instant.now());
}

@Override
public SpoSummaryDto getSpoSummary() {
    LocalDateTime yearStart = LocalDate.now().withDayOfYear(1).atStartOfDay();
    LocalDateTime now = LocalDateTime.now();
 
    BigDecimal poSpend = purchaseOrderRepository.sumTotalValueByCreatedDateBetween(yearStart, now);
    BigDecimal soSpend = serviceOrderRepository.sumTotalValueByCreatedDateBetween(yearStart, now);
    BigDecimal cpSpend = contigencyPurchaseRepository.sumTotalValueByCreatedDateBetween(yearStart, now);
    BigDecimal totalSpendYtd = nullSafeAdd(poSpend, soSpend, cpSpend);
 
    // PLACEHOLDER status values — confirm against real PurchaseOrder.currentStatus data
    Long activePos = purchaseOrderRepository.countByCurrentStatusIn(List.of("IN-PROGRESS", "APPROVED"));
 
    BigDecimal assetValue = ohqMasterEntityRepository.sumInventoryValue();
    BigDecimal consumableValue = ohqMasterConsumableEntityRepository.sumInventoryValue();
    BigDecimal inventoryValue = nullSafeAdd(assetValue, consumableValue);
 
    // No real data source exists for supplier performance yet (confirmed
    // against VendorMasterServiceImpl/VendorMasterUtilServiceImpl) — leave null.
    return new SpoSummaryDto(totalSpendYtd, null, activePos, inventoryValue, null);
}
 
private BigDecimal nullSafeAdd(BigDecimal... values) {
    BigDecimal sum = BigDecimal.ZERO;
    for (BigDecimal v : values) {
        if (v != null) sum = sum.add(v);
    }
    return sum;
}
 
@Override
public List<CategorySpendDto> getSpoSpendByCategory() {
    // PurchaseOrderAttributes has no category field today, so only
    // Contingency Purchase spend can be broken out by category until a
    // material-master join is available for PO/SO line items.
    return cpMaterialsRepository.sumAmountGroupByCategory();
}
 
@Override
public List<RecentActivityDto> getSpoRecentActivity() {
    LocalDateTime since = LocalDateTime.now().minusDays(1);
 
    List<PurchaseOrder> createdPos = purchaseOrderRepository.findTop10ByCreatedDateAfterOrderByCreatedDateDesc(since);
    // PLACEHOLDER status value — confirm "APPROVED" against real data
    List<PurchaseOrder> approvedPos = purchaseOrderRepository
        .findTop10ByUpdatedDateAfterAndCurrentStatusOrderByUpdatedDateDesc(since, "APPROVED");
 
    List<AbstractMap.SimpleEntry<LocalDateTime, RecentActivityDto>> rows = new ArrayList<>();
    for (PurchaseOrder po : createdPos) {
        rows.add(new AbstractMap.SimpleEntry<>(po.getCreatedDate(),
            new RecentActivityDto(formatTime(po.getCreatedDate()), "PO Created", "PO " + po.getPoId())));
    }
    for (PurchaseOrder po : approvedPos) {
        rows.add(new AbstractMap.SimpleEntry<>(po.getUpdatedDate(),
            new RecentActivityDto(formatTime(po.getUpdatedDate()), "PO Approved", "PO " + po.getPoId())));
    }
 
    // Stock-update / payment-processed / low-stock-alert entries from the
    // reference screenshot aren't covered here — they'd need the Payment
    // Voucher entity and a stock-movement log, neither of which I've seen.
    return rows.stream()
        .sorted(Comparator.comparing((AbstractMap.SimpleEntry<LocalDateTime, RecentActivityDto> e) -> e.getKey()).reversed())
        .limit(10)
        .map(AbstractMap.SimpleEntry::getValue)
        .collect(Collectors.toList());
}
 
private String formatTime(LocalDateTime dt) {
    return dt == null ? "--" : dt.format(DateTimeFormatter.ofPattern("hh:mm a"));
}
 
@Override
public PurchasePersonnelSummaryDto getPurchasePersonnelSummary(LocalDate startDate, LocalDate endDate) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.atTime(LocalTime.MAX);
 
    long total = purchaseOrderRepository.countByCreatedDateBetween(start, end);
    // PLACEHOLDER status values — confirm against real PurchaseOrder.currentStatus data
    long approved = purchaseOrderRepository.countByCreatedDateBetweenAndCurrentStatus(start, end, "APPROVED");
    long pending = purchaseOrderRepository.countByCreatedDateBetweenAndCurrentStatus(start, end, "PENDING APPROVAL");
    long rejected = purchaseOrderRepository.countByCreatedDateBetweenAndCurrentStatus(start, end, "REJECTED");
 
    return new PurchasePersonnelSummaryDto(total, approved, pending, rejected);
}
 
@Override
public List<PoStatusCountDto> getPoStatusBreakdown(LocalDate startDate, LocalDate endDate) {
    return purchaseOrderRepository.countGroupByStatus(startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
}
 
@Override
public List<ProcurementTransactionDto> getProcurementTransactions(LocalDate startDate, LocalDate endDate) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.atTime(LocalTime.MAX);
 
    List<PurchaseOrder> pos = purchaseOrderRepository.findTop50ByCreatedDateBetweenOrderByCreatedDateDesc(start, end);
    List<ProcurementTransactionDto> result = new ArrayList<>();
    for (PurchaseOrder po : pos) {
        List<PurchaseOrderAttributes> attrs = po.getPurchaseOrderAttributes();
        String item = (attrs != null && !attrs.isEmpty())
            ? attrs.get(0).getMaterialDescription()
                + (attrs.size() > 1 ? " (+" + (attrs.size() - 1) + " more)" : "")
            : "--";
        result.add(new ProcurementTransactionDto(
            po.getPoId(), po.getCreatedDate(), item, po.getVendorName(), po.getTotalValueOfPo(), po.getCurrentStatus()
        ));
    }
    return result;
}
 
@Override
public StockSummaryDto getStockSummary() {
    long totalItems = ohqMasterConsumableStoreStockEntityRepository.count() + ohqMasterEntityRepository.count();
 
    // Groups by materialCode today — swap for a real category field once a
    // material-master join is available for consumables.
    List<StockSummaryDto.CategoryQuantity> levels = ohqMasterConsumableStoreStockEntityRepository.sumQuantityGroupByMaterialCode();
 
    // "In Stock / Low Stock / Out of Stock" needs a reorder threshold to
    // classify quantity — there's no such field on OhqMasterConsumableEntity
    // today, so this uses a placeholder threshold (<=0 Out of Stock, <=10 Low
    // Stock). Replace with your real reorder-level logic once defined, and
    // move this to a DB-level query if the consumables table grows large —
    // loading all rows into Java is fine at today's scale but not at 10k+ rows.
    List<OhqConsumableStoreStockEntity> consumables = ohqMasterConsumableStoreStockEntityRepository.findAll();
    long outOfStock = consumables.stream()
        .filter(c -> c.getQuantity() == null || c.getQuantity().compareTo(BigDecimal.ZERO) <= 0).count();
    long lowStock = consumables.stream()
        .filter(c -> c.getQuantity() != null && c.getQuantity().compareTo(BigDecimal.ZERO) > 0
            && c.getQuantity().compareTo(BigDecimal.TEN) <= 0).count();
    long inStock = consumables.size() - outOfStock - lowStock;
 
    List<StockSummaryDto.StockStatusCount> statusCounts = List.of(
        new StockSummaryDto.StockStatusCount("In Stock", inStock),
        new StockSummaryDto.StockStatusCount("Low Stock", lowStock),
        new StockSummaryDto.StockStatusCount("Out of Stock", outOfStock)
    );
 
    return new StockSummaryDto(totalItems, levels, statusCounts);
}
 
@Override
public PoGrnPaymentStatusDto getPoGrnPaymentStatus(LocalDate startDate, LocalDate endDate) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.atTime(LocalTime.MAX);
 
    // PLACEHOLDER status value — confirm "APPROVED" against real data
    long posApproved = purchaseOrderRepository.countByCreatedDateBetweenAndCurrentStatus(start, end, "APPROVED");
    // Assumes GrnMasterEntity has createDate/status fields, matching the
    // pattern already used elsewhere in this class for the other inventory
    // repos (e.g. gprnMasterRepository.findByStatusOrderByCreateDateAsc).
    long grnCompleted = grnMasterRepository.countByCreateDateBetweenAndStatus(start, end, "COMPLETED");
 
    // Payment Voucher entity hasn't been reviewed yet — left at 0 rather
    // than guessed. Wire this up once that entity/repository is available.
    return new PoGrnPaymentStatusDto(posApproved, grnCompleted, 0L);
}
 
@Override
public ApproverSummaryDto getApproverSummary(Integer userId) {
    // WorkflowTransition.updatedBy is a String (per @LastModifiedBy /
    // @CreatedBy convention seen on the entity), not an Integer userId —
    // confirm how your app resolves userId -> that identifier string before
    // relying on this; String.valueOf(userId) is a placeholder.
    String actedBy = String.valueOf(userId);
    LocalDateTime monthStartLdt = LocalDate.now().withDayOfMonth(1).atStartOfDay();
    LocalDateTime nowLdt = LocalDateTime.now();
    Date monthStart = Date.from(monthStartLdt.atZone(ZoneId.systemDefault()).toInstant());
    Date now = Date.from(nowLdt.atZone(ZoneId.systemDefault()).toInstant());
 
    List<WorkflowTransition> actedThisMonth =
        workflowTransitionRepository.findByUpdatedByAndModificationDateBetween(actedBy, monthStart, now);
 
    // PLACEHOLDER action values — confirm against the APPROVE_TYPE/REJECT_TYPE
    // constants used in WorkflowServiceImpl.performTransitionAction
    // long approved = actedThisMonth.stream().filter(t -> "APPROVE".equalsIgnoreCase(t.getAction())).count();
    // long rejected = actedThisMonth.stream().filter(t -> "REJECT".equalsIgnoreCase(t.getAction())).count();

        // Confirmed via IndentCreationRepository's native queries (wt.action = 'Rejected')
    long approved = actedThisMonth.stream().filter(t -> "Approved".equalsIgnoreCase(t.getAction())).count();
    long rejected = actedThisMonth.stream().filter(t -> "Rejected".equalsIgnoreCase(t.getAction())).count();
 
    OptionalDouble avgHours = actedThisMonth.stream()
        .filter(t -> t.getCreatedDate() != null && t.getModificationDate() != null)
        .mapToLong(t -> Duration.between(t.getCreatedDate().toInstant(), t.getModificationDate().toInstant()).toHours())
        .average();
    Double avgDays = avgHours.isPresent() ? avgHours.getAsDouble() / 24.0 : null;
 
    return new ApproverSummaryDto(approved, rejected, avgDays);
}
 
@Override
public List<ApprovalTrendPointDto> getApprovalTrend(Integer userId) {
    String actedBy = String.valueOf(userId);
    LocalDateTime sixMonthsAgoLdt = LocalDateTime.now().minusMonths(6);
    Date sixMonthsAgo = Date.from(sixMonthsAgoLdt.atZone(ZoneId.systemDefault()).toInstant());
 
    List<WorkflowTransition> transitions =
        workflowTransitionRepository.findByUpdatedByAndModificationDateAfter(actedBy, sixMonthsAgo);
 
    Map<String, long[]> byMonth = new LinkedHashMap<>(); // [approved, rejected]
    DateTimeFormatter monthFmt = DateTimeFormatter.ofPattern("MMM");
 
    for (WorkflowTransition t : transitions) {
        if (t.getModificationDate() == null) continue;
        String month = t.getModificationDate().toInstant().atZone(ZoneId.systemDefault()).format(monthFmt);
        byMonth.putIfAbsent(month, new long[2]);
        if ("Approved".equalsIgnoreCase(t.getAction())) byMonth.get(month)[0]++;
        else if ("Rejected".equalsIgnoreCase(t.getAction())) byMonth.get(month)[1]++;
    }
 
    return byMonth.entrySet().stream()
        .map(e -> new ApprovalTrendPointDto(e.getKey(), e.getValue()[0], e.getValue()[1]))
        .collect(Collectors.toList());
}

@Override
public IndentorSummaryDto getIndentorSummary(Integer userId) {
    String createdBy = String.valueOf(userId);
    List<String> terminalStatuses = List.of("COMPLETED", "REJECTED", "CANCELLED");
    long myPurchaseRequests = indentCreationRepository.countByCreatedByAndCurrentStatusNotIn(createdBy, terminalStatuses);

    LocalDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay();
    long approvedThisMonth = indentCreationRepository.findByCreatedByAndCurrentStatus(createdBy, "APPROVED").stream()
        .filter(i -> i.getUpdatedDate() != null && i.getUpdatedDate().isAfter(monthStart))
        .count();

    return new IndentorSummaryDto(myPurchaseRequests, approvedThisMonth, null, null);
}

@Override
public List<RecentIndentDto> getIndentorRecentRequests(Integer userId, int limit) {
    String createdBy = String.valueOf(userId);
    List<IndentCreation> indents = indentCreationRepository
        .findByCreatedByOrderByCreatedDateDesc(createdBy, PageRequest.of(0, limit));

    return indents.stream()
        .map(i -> new RecentIndentDto(i.getIndentId(), firstItemLabel(i), i.getCurrentStatus(), i.getCreatedDate()))
        .collect(Collectors.toList());
}

private String firstItemLabel(IndentCreation indent) {
    if (indent.getMaterialDetails() != null && !indent.getMaterialDetails().isEmpty()) {
        MaterialDetails first = indent.getMaterialDetails().get(0);
        int extra = indent.getMaterialDetails().size() - 1;
        return first.getMaterialDescription() + (extra > 0 ? " (+" + extra + " more)" : "");
    }
    if (indent.getJobDetails() != null && !indent.getJobDetails().isEmpty()) {
        JobDetails first = indent.getJobDetails().get(0);
        int extra = indent.getJobDetails().size() - 1;
        return first.getJobDescription() + (extra > 0 ? " (+" + extra + " more)" : "");
    }
    return "--";
}

@Override
public List<TopRequestedItemDto> getIndentorTopItems(Integer userId) {
    return materialDetailsRepository.countGroupByMaterialForCreator(String.valueOf(userId));
}

@Override
public List<GatePassDto> getRecentGatePasses(LocalDate startDate, LocalDate endDate) {
    LocalDateTime start = startDate.atStartOfDay();
    LocalDateTime end = endDate.atTime(LocalTime.MAX);

    List<Object[]> rows = ogpMasterRepository.findRecentGatePassLines(start, end);
    List<GatePassDto> result = new ArrayList<>();
    for (Object[] row : rows) {
        result.add(new GatePassDto(
            String.valueOf(row[0]),
            row[1] instanceof java.sql.Timestamp ? ((java.sql.Timestamp) row[1]).toLocalDateTime() : null,
            // (String) row[2],
            row[2] == null ? null : new BigDecimal(row[3].toString()),
            row[3] == null ? "--" : String.valueOf(row[4])
        ));
    }
    return result;
}

@Override
public List<TransferRequestDto> getRecentTransferRequests(LocalDate startDate, LocalDate endDate) {
    List<Object[]> rows = gtMasterRepository.getGtReport(startDate, endDate);
    ObjectMapper mapper = new ObjectMapper();
    List<TransferRequestDto> result = new ArrayList<>();

    for (Object[] row : rows) {
        String itemCategory = "--";
        try {
            JsonNode details = mapper.readTree((String) row[9]);
            if (details.isArray() && details.size() > 0) {
                JsonNode first = details.get(0);
                itemCategory = first.hasNonNull("materialDesc") ? first.get("materialDesc").asText()
                    : first.hasNonNull("assetDesc") ? first.get("assetDesc").asText() : "--";
            }
        } catch (Exception e) {
            // leave itemCategory as "--" if the JSON shape doesn't match this
        }

        result.add(new TransferRequestDto(
            String.valueOf(row[0]),
            row[7] instanceof java.sql.Timestamp ? ((java.sql.Timestamp) row[7]).toLocalDateTime() : null,
            itemCategory,
            String.valueOf(row[1]),
            String.valueOf(row[2]),
            (String) row[5]
        ));
    }
    return result;
}
}
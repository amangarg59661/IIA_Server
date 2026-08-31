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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.astro.service.DashboardSummaryService;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
}
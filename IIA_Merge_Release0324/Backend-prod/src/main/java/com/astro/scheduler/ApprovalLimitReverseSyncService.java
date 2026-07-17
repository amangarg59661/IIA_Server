package com.astro.scheduler;

import com.astro.entity.AdminPanel.ApprovalLimitMaster;
import com.astro.entity.AdminPanel.ApproverMaster;
import com.astro.entity.AdminPanel.WorkflowBranchMaster;
import com.astro.repository.AdminPanel.ApprovalLimitMasterRepository;
import com.astro.repository.AdminPanel.ApproverMasterRepository;
import com.astro.repository.AdminPanel.WorkflowBranchMasterRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reverse sync — opposite direction of ApprovalLimitServiceImpl.syncBranchConditionConfig().
 *
 * That one runs on manual edit: ApprovalLimitMaster (role's min/max) -> pushed INTO
 * WorkflowBranchMaster.conditionConfig for branches where that role is the final approver.
 *
 * This one runs once a day: scans actual final approvers (max approvalLevel per branch) straight
 * from ApproverMaster/WorkflowBranchMaster, and reconciles ApprovalLimitMaster role coverage against
 * reality — catching branches whose final approver changed outside the approval-limit-edit flow
 * (e.g. new branch created directly, or approver reassigned) without ever touching branch config itself.
 *
 * Responsibilities, per workflow:
 *   - role is a final approver somewhere AND has no active ApprovalLimitMaster row -> create one
 *     (reusing an inactive row for that role if one exists, instead of duplicating)
 *   - role has an active ApprovalLimitMaster row but is no longer a final approver anywhere -> deactivate it
 *   - role already has an active row and is still a final approver -> left untouched (amounts on
 *     already-covered roles are NOT overwritten by this job; that's still the manual-edit flow's job)
 */
@Component
public class ApprovalLimitReverseSyncService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalLimitReverseSyncService.class);

    private static final String SYSTEM_USER = "SYSTEM_AUTO_SYNC";
    private static final String DEFAULT_CATEGORY = "ALL";
    private static final int DEFAULT_PRIORITY = 1; 

    @Autowired
    private ApproverMasterRepository approverMasterRepository;

    @Autowired
    private WorkflowBranchMasterRepository workflowBranchMasterRepository;

    @Autowired
    private ApprovalLimitMasterRepository approvalLimitMasterRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Default 2 AM daily. Override in application.properties:
     *   approval.reverse-sync.cron=0 0 3 * * *
     */
    @Scheduled(cron = "${approval.reverse-sync.cron:0 0 2 * * *}")
    public void runDailySync() {
        log.info("ApprovalLimitReverseSyncService: daily run started");

        List<Integer> workflowIds = approverMasterRepository.findDistinctWorkflowIdByStatus("Active");

        for (Integer workflowId : workflowIds) {
            try {
                syncWorkflow(workflowId);
            } catch (Exception e) {
                // one bad workflow must never block the rest
                log.error("ApprovalLimitReverseSyncService: failed for workflowId={}", workflowId, e);
            }
        }

        log.info("ApprovalLimitReverseSyncService: daily run finished, processed {} workflow(s)", workflowIds.size());
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncWorkflow(Integer workflowId) {

        // 1. final approver (max approvalLevel) per branch — same grouping as
        //    ApprovalLimitServiceImpl.syncBranchConditionConfig, kept identical on purpose
        List<ApproverMaster> approvers = approverMasterRepository.findByWorkflowIdAndStatus(workflowId, "Active");

        Map<Long, Optional<ApproverMaster>> topApproverByBranch = approvers.stream()
                .collect(Collectors.groupingBy(ApproverMaster::getBranchId,
                        Collectors.maxBy(Comparator.comparingInt(ApproverMaster::getApprovalLevel))));

        if (topApproverByBranch.isEmpty()) {
            log.info("ApprovalLimitReverseSyncService: workflowId={} has no active approvers, skipping", workflowId);
            return;
        }

        List<Long> branchIds = new ArrayList<>(topApproverByBranch.keySet());
        List<WorkflowBranchMaster> branches = workflowBranchMasterRepository
                .findByWorkflowIdAndIsActiveTrueAndBranchIdIn(workflowId, branchIds);

        Map<Long, WorkflowBranchMaster> branchById = branches.stream()
                .collect(Collectors.toMap(WorkflowBranchMaster::getBranchId, b -> b));

        // 2. roleId -> every branch's [min,max] where that role is the final approver
        Map<Integer, List<BigDecimal[]>> roleLimits = new HashMap<>();
        Map<Integer, String> roleNames = new HashMap<>();

        for (Map.Entry<Long, Optional<ApproverMaster>> entry : topApproverByBranch.entrySet()) {
            if (!entry.getValue().isPresent()) continue;

            Long branchId = entry.getKey();
            ApproverMaster topApprover = entry.getValue().get();

            WorkflowBranchMaster branch = branchById.get(branchId);
            if (branch == null) continue; // branch inactive/missing — not eligible for aggregation

            BigDecimal[] minMax = extractMinMax(branch.getConditionConfig());
            if (minMax == null) continue; // condition config not limit-shaped on this branch — skip

            Integer roleId = topApprover.getRoleId();
            roleLimits.computeIfAbsent(roleId, k -> new ArrayList<>()).add(minMax);
            roleNames.putIfAbsent(roleId, topApprover.getRoleName());
        }

        Set<Integer> currentFinalRoleIds = roleLimits.keySet();

        // 3. existing ApprovalLimitMaster coverage for this workflow
        List<ApprovalLimitMaster> activeRows = approvalLimitMasterRepository.findByWorkflowIdAndIsActiveTrue(workflowId);
        List<ApprovalLimitMaster> inactiveRows = approvalLimitMasterRepository.findByWorkflowIdAndIsActiveFalse(workflowId);

        Set<Integer> activeRoleIds = activeRows.stream()
                .map(ApprovalLimitMaster::getRoleId)
                .collect(Collectors.toSet());

        Map<Integer, List<ApprovalLimitMaster>> inactiveRowsByRole = inactiveRows.stream()
                .collect(Collectors.groupingBy(ApprovalLimitMaster::getRoleId));

        // 4. CREATE or REACTIVATE — role is a final approver today but not actively covered
        for (Integer roleId : currentFinalRoleIds) {
            if (activeRoleIds.contains(roleId)) {
                continue; // already covered — amounts left untouched, out of scope for this job
            }

            BigDecimal[] agg = aggregate(roleLimits.get(roleId));
            List<ApprovalLimitMaster> inactiveForRole = inactiveRowsByRole.get(roleId);

            if (inactiveForRole != null && !inactiveForRole.isEmpty()) {
                ApprovalLimitMaster row = inactiveForRole.stream()
                        .max(Comparator.comparing(ApprovalLimitMaster::getUpdatedDate,
                                Comparator.nullsFirst(Comparator.naturalOrder())))
                        .get();
                row.setIsActive(true);
                row.setMinAmount(agg[0] != null ? agg[0] : BigDecimal.ZERO);
                row.setMaxAmount(agg[1]);
                row.setUpdatedBy(SYSTEM_USER);
                approvalLimitMasterRepository.save(row);
                log.info("ApprovalLimitReverseSyncService: reactivated limitId={} roleId={} workflowId={}",
                        row.getLimitId(), roleId, workflowId);
            } else {
                ApprovalLimitMaster row = new ApprovalLimitMaster();
                row.setWorkflowId(workflowId);
                row.setRoleId(roleId);
                row.setRoleName(roleNames.get(roleId));
                row.setCategory(DEFAULT_CATEGORY);
                row.setDepartmentName(null);
                row.setLocation(null);
                row.setMinAmount(agg[0] != null ? agg[0] : BigDecimal.ZERO);
                row.setMaxAmount(agg[1]);
                row.setIsActive(true);
                row.setPriority(DEFAULT_PRIORITY);
                row.setCreatedBy(SYSTEM_USER);
                row.setUpdatedBy(SYSTEM_USER);
                approvalLimitMasterRepository.save(row);
                log.info("ApprovalLimitReverseSyncService: created new limit roleId={} workflowId={}",
                        roleId, workflowId);
            }
        }

        // 5. DEACTIVATE — role has an active row but is no longer a final approver anywhere in this workflow
        for (ApprovalLimitMaster row : activeRows) {
            if (!currentFinalRoleIds.contains(row.getRoleId())) {
                row.setIsActive(false);
                row.setUpdatedBy(SYSTEM_USER);
                approvalLimitMasterRepository.save(row);
                log.info("ApprovalLimitReverseSyncService: deactivated limitId={} roleId={} workflowId={} (no longer a final approver)",
                        row.getLimitId(), row.getRoleId(), workflowId);
            }
        }
    }

    /**
     * Pulls minAmount/maxAmount out of a branch's conditionConfig JSON.
     * Returns null if the JSON is missing, unparseable, or has neither field —
     * such a branch is simply excluded from aggregation for its final-approver role.
     */
    private BigDecimal[] extractMinMax(String conditionConfig) {
        if (conditionConfig == null || conditionConfig.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(conditionConfig);
            if (!node.has("minAmount") && !node.has("maxAmount")) {
                return null;
            }
            BigDecimal min = (node.has("minAmount") && !node.get("minAmount").isNull())
                    ? new BigDecimal(node.get("minAmount").asText()) : null;
            BigDecimal max = (node.has("maxAmount") && !node.get("maxAmount").isNull())
                    ? new BigDecimal(node.get("maxAmount").asText()) : null;
            return new BigDecimal[]{min, max};
        } catch (Exception e) {
            log.warn("ApprovalLimitReverseSyncService: failed to parse conditionConfig, skipping branch — {}", e.getMessage());
            return null;
        }
    }

    /**
     * min = MIN across all branches where this role is final approver — but ANY branch with a
     * null min makes the aggregate min null too (null wins).
     * max = MAX across all branches — same rule, ANY null max makes aggregate max null (unlimited wins).
     */
    private BigDecimal[] aggregate(List<BigDecimal[]> values) {
        BigDecimal min = null;
        BigDecimal max = null;
        boolean minNullWins = false;
        boolean maxNullWins = false;

        for (BigDecimal[] pair : values) {
            BigDecimal branchMin = pair[0];
            BigDecimal branchMax = pair[1];

            if (branchMin == null) {
                minNullWins = true;
            } else if (!minNullWins) {
                min = (min == null) ? branchMin : min.min(branchMin);
            }

            if (branchMax == null) {
                maxNullWins = true;
            } else if (!maxNullWins) {
                max = (max == null) ? branchMax : max.max(branchMax);
            }
        }

        return new BigDecimal[]{ minNullWins ? null : min, maxNullWins ? null : max };
    }
}

package com.astro.service.impl;

import com.astro.dto.workflow.EscalationCheckResultDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCreationResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.TenderWithIndentResponseDTO;
import com.astro.entity.AdminPanel.ApproverMaster;
import com.astro.entity.AdminPanel.DepartmentApproverMapping;
import com.astro.entity.AdminPanel.FieldStationApproverMaster;
import com.astro.entity.ProcurementModule.ContigencyPurchase;
import com.astro.entity.AdminPanel.WorkflowBranchMaster;
import com.astro.entity.ProcurementModule.IndentCreation; // added by abhinav
import com.astro.entity.ProcurementModule.IndentId; // added by abhinav
import com.astro.entity.ProcurementModule.PurchaseOrder; // added by abhinav
import com.astro.entity.ProcurementModule.TenderRequest; // added by abhinav
import com.astro.entity.ProjectMaster;
import com.astro.repository.AdminPanel.ApproverMasterRepository;
import com.astro.repository.AdminPanel.DepartmentApproverMappingRepository;
import com.astro.repository.AdminPanel.FieldStationApproverMasterRepository;
import com.astro.repository.ProcurementModule.ContigencyPurchaseRepository;
import com.astro.repository.AdminPanel.WorkflowBranchMasterRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.repository.ProjectMasterRepository;
import com.astro.service.AdminPanel.ApprovalLimitService;
import com.astro.service.BranchWorkflowService;
import com.astro.service.IndentCreationService;
import com.astro.service.TenderRequestService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Added by aman 
import com.astro.repository.ProcurementModule.ServiceOrderRepository.ServiceOrderRepository; 
import com.astro.entity.PaymentVoucher;
import com.astro.entity.ProcurementModule.ServiceOrder;
import com.astro.repository.InventoryModule.PaymentVoucherReposiotry;
// End

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BranchWorkflowServiceImpl implements BranchWorkflowService {

    @Autowired
    private WorkflowBranchMasterRepository branchRepository;

    @Autowired
    private ApproverMasterRepository approverRepository;
    // Added by aman 

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private PaymentVoucherReposiotry paymentVoucherReposiotry;

    @Autowired
    private ContigencyPurchaseRepository contigencyPurchaseRepository;

// End

    @Autowired
    private IndentCreationService indentService;

    @Autowired
    private TenderRequestService tenderService;
    // added by abhinav
    @Autowired
    private TenderRequestRepository tenderRepository;

    @Autowired
    private com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository indentRepository;

    @Autowired
    private ProjectMasterRepository projectMasterRepository;

    @Autowired
    private DepartmentApproverMappingRepository departmentApproverMappingRepository;

    @Autowired
    private FieldStationApproverMasterRepository fieldStationApproverMasterRepository;

    @Autowired
    private ApprovalLimitService approvalLimitService;

    // added by abhinav for brach conditions that require PO details
    @Autowired
    private com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository purchaseOrderRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WorkflowBranchMaster findMatchingBranch(Integer workflowId, Map<String, Object> conditions) {
        // Get all active branches for this workflow, ordered by display_order
        List<WorkflowBranchMaster> branches = branchRepository
                .findByWorkflowIdAndIsActiveTrue(workflowId)
                .stream()
                .sorted(Comparator.comparing(WorkflowBranchMaster::getDisplayOrder))
                .collect(Collectors.toList());

        // Find first matching branch
        for (WorkflowBranchMaster branch : branches) {
            if (matchesBranchCondition(branch, conditions)) {
                System.out.println("✅ Matched Branch: " + branch.getBranchCode() + " - " + branch.getBranchName());
                return branch;
            }
        }

        System.out.println("⚠️ No matching branch found for workflow " + workflowId);
        return null;
    }

    @Override
    public List<ApproverMaster> getApproversForBranch(Long branchId) {
        // Get all approvers for this branch by branchId only
        List<ApproverMaster> allApprovers = approverRepository.findAll();
        return allApprovers.stream()
                .filter(a -> a.getBranchId().equals(branchId) && "Active".equals(a.getStatus()))
                .sorted(Comparator
                        .comparing(ApproverMaster::getApprovalLevel)
                        .thenComparing(ApproverMaster::getApprovalSequence))
                .collect(Collectors.toList());
    }

    @Override
    public ApproverMaster getFirstApprover(Long branchId) {
        List<ApproverMaster> allApprovers = approverRepository.findAll();
        List<ApproverMaster> approvers = allApprovers.stream()
                .filter(a -> a.getBranchId().equals(branchId) && "Active".equals(a.getStatus()))
                .sorted(Comparator
                        .comparing(ApproverMaster::getApprovalLevel)
                        .thenComparing(ApproverMaster::getApprovalSequence))
                .collect(Collectors.toList());

        if (approvers.isEmpty()) {
            System.out.println("⚠️ No active approvers found for branch " + branchId);
            return null;
        }

        ApproverMaster firstApprover = approvers.get(0);
        System.out.println("First Approver: " + firstApprover.getRoleName()
                + " (Level: " + firstApprover.getApprovalLevel()
                + ", Seq: " + firstApprover.getApprovalSequence() + ")");
        return firstApprover;
    }

    // updated by abhinav
    @Override
    public ApproverMaster getFirstApproverForDepartment(Long branchId, String departmentName, BigDecimal indentValue) {
        List<ApproverMaster> allApprovers = getApproversForBranch(branchId);

        // If no approvers have DEPARTMENT_BASED condition, fall back to normal first approver
        boolean hasDeptBased = allApprovers.stream()
                .anyMatch(a -> "DEPARTMENT_BASED".equalsIgnoreCase(a.getConditionCheckType()));

        if (!hasDeptBased || departmentName == null || departmentName.trim().isEmpty()) {
            return getFirstApprover(branchId);
        }

        Map<String, Object> conditions = new HashMap<>();
        conditions.put("indentorDepartment", departmentName);
        conditions.put("totalAmount", indentValue);

        // Walk through all approvers in order and return the first one that should NOT be skipped
        for (ApproverMaster approver : allApprovers) {
            if (!shouldSkipApprover(approver, conditions)) {
                System.out.println("Department-aware first approver: " + approver.getRoleName()
                        + " for dept '" + departmentName + "'"
                        + " (Level: " + approver.getApprovalLevel()
                        + ", Seq: " + approver.getApprovalSequence() + ")");
                return approver;
            }
        }

        System.out.println("⚠️ No department-matching approver found for dept '" + departmentName
                + "' in branch " + branchId);
        return null;
    }

    @Override
    public ApproverMaster getNextApprover(Long branchId, Integer currentApprovalLevel, Integer currentApprovalSequence) {
        List<ApproverMaster> allApprovers = approverRepository.findAll();
        List<ApproverMaster> branchApprovers = allApprovers.stream()
                .filter(a -> a.getBranchId().equals(branchId) && "Active".equals(a.getStatus()))
                .sorted(Comparator
                        .comparing(ApproverMaster::getApprovalLevel)
                        .thenComparing(ApproverMaster::getApprovalSequence))
                .collect(Collectors.toList());

        System.out.println("🔍 Finding next approver for branch " + branchId);
        System.out.println("   Current Level: " + currentApprovalLevel + ", Current Seq: " + currentApprovalSequence);
        System.out.println("   Total approvers in branch: " + branchApprovers.size());

        if (branchApprovers.isEmpty()) {
            System.out.println("⚠️ No approvers found for branch " + branchId);
            return null;
        }

        // Find next approver after current level and sequence
        for (ApproverMaster approver : branchApprovers) {
            System.out.println("   Checking approver: " + approver.getRoleName()
                    + " (Level: " + approver.getApprovalLevel()
                    + ", Seq: " + approver.getApprovalSequence() + ")");

            boolean isNextLevel = approver.getApprovalLevel() > currentApprovalLevel;
            boolean isSameLevelNextSequence
                    = approver.getApprovalLevel().equals(currentApprovalLevel)
                    && approver.getApprovalSequence() > currentApprovalSequence;

            if (isNextLevel || isSameLevelNextSequence) {
                System.out.println("✅ Next Approver: " + approver.getRoleName()
                        + " (Level: " + approver.getApprovalLevel()
                        + ", Seq: " + approver.getApprovalSequence() + ")");
                return approver;
            }
        }

        System.out.println("✅ No more approvers - workflow complete for branch " + branchId);
        return null; // No more approvers - workflow complete
    }

    @Override
    public boolean matchesBranchCondition(WorkflowBranchMaster branch, Map<String, Object> conditions) {
        if (branch.getConditionConfig() == null || branch.getConditionConfig().trim().isEmpty()) {
            // No conditions means always match (default branch)
            return true;
        }

        try {
            // Parse JSON condition configuration
            Map<String, Object> branchConditions = objectMapper.readValue(
                    branch.getConditionConfig(),
                    new TypeReference<Map<String, Object>>() {
            }
            );

            System.out.println("Matching Branch: " + branch.getBranchCode());
            System.out.println("   Branch Conditions: " + branchConditions);
            System.out.println("   Actual Values: " + conditions);

            // Check amount range if specified
            if (branchConditions.containsKey("minAmount") || branchConditions.containsKey("maxAmount")) {
                if (!matchesAmountRange(branchConditions, conditions)) {
                    return false;
                }
            }

            // Check category if specified (supports both "category" and "materialCategory" keys)
            if (branchConditions.containsKey("category")) {
                if (!matchesCategory(branchConditions, conditions, "category")) {
                    return false;
                }
            }

            // Check materialCategory if specified
            if (branchConditions.containsKey("materialCategory")) {
                if (!matchesCategory(branchConditions, conditions, "materialCategory")) {
                    return false;
                }
            }

            // Check location if specified
            if (branchConditions.containsKey("location")) {
                if (!matchesLocation(branchConditions, conditions)) {
                    return false;
                }
            }

            // Check project if specified
            if (branchConditions.containsKey("projectName")) {
                if (!matchesProject(branchConditions, conditions)) {
                    return false;
                }
            }

            // Check isUnderProject / projectBased if specified (both keys are supported)
            if (branchConditions.containsKey("isUnderProject")) {
                if (!matchesIsUnderProject(branchConditions, conditions)) {
                    return false;
                }
            }
            if (branchConditions.containsKey("projectBased")) {
                if (!matchesProjectBased(branchConditions, conditions)) {
                    return false;
                }
            }
            if (branchConditions.containsKey("isProject")) {
                if (!matchesIsProject(branchConditions, conditions)) {
                    return false;
                }
            }

            // Check budget availability if specified
            if (branchConditions.containsKey("budgetCheck")) {
                if (!matchesBudgetCheck(branchConditions, conditions)) {
                    return false;
                }
            }

            // Check mode of procurement if specified
            if (branchConditions.containsKey("modeOfProcurement")) {
                if (!matchesModeOfProcurement(branchConditions, conditions)) {
                    return false;
                }
            }

            // Check indentorDepartment if specified (used to restrict a branch to a specific dept)
            if (branchConditions.containsKey("indentorDepartment")) {
                if (!matchesIndentorDepartment(branchConditions, conditions)) {
                    return false;
                }
            }

            // All conditions matched
            System.out.println("✅ ALL CONDITIONS MATCHED for branch: " + branch.getBranchCode());
            return true;

        } catch (Exception e) {
            System.err.println("❌ Error parsing branch condition for " + branch.getBranchCode() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean matchesAmountRange(Map<String, Object> branchConditions, Map<String, Object> actualConditions) {
        Object totalAmountObj = actualConditions.get("totalAmount");
        if (totalAmountObj == null) {
            return false;
        }

        BigDecimal totalAmount = convertToBigDecimal(totalAmountObj);
        if (totalAmount == null) {
            return false;
        }

        // Check min amount
        if (branchConditions.containsKey("minAmount")) {
            BigDecimal minAmount = convertToBigDecimal(branchConditions.get("minAmount"));
            if (minAmount != null && totalAmount.compareTo(minAmount) < 0) {
                System.out.println("   ❌ Amount " + totalAmount + " is less than min " + minAmount);
                return false;
            }
        }

        // Check max amount
        if (branchConditions.containsKey("maxAmount")) {
            BigDecimal maxAmount = convertToBigDecimal(branchConditions.get("maxAmount"));
            if (maxAmount != null && totalAmount.compareTo(maxAmount) > 0) {
                System.out.println("   ❌ Amount " + totalAmount + " exceeds max " + maxAmount);
                return false;
            }
        }

        System.out.println("   ✅ Amount " + totalAmount + " matches range");
        return true;
    }

    private boolean matchesCategory(Map<String, Object> branchConditions, Map<String, Object> actualConditions, String conditionKey) {
        String branchCategory = String.valueOf(branchConditions.get(conditionKey));
        // For materialCategory, we look for it in actual conditions as well
        String actualCategory = String.valueOf(actualConditions.get(conditionKey));

        // Also try to get from "category" key if materialCategory is not directly in actual conditions
        if ((actualCategory == null || "null".equals(actualCategory)) && "materialCategory".equals(conditionKey)) {
            actualCategory = String.valueOf(actualConditions.get("category"));
        }

        if (actualCategory == null || "null".equals(actualCategory)) {
            System.out.println("   ❌ Category: actualCategory is null for key: " + conditionKey);
            return false;
        }

        // Normalize the values for comparison (handle NON_COMPUTER vs non-computer)
        String normalizedBranch = branchCategory.trim().toUpperCase().replace("-", "_").replace(" ", "_");
        String normalizedActual = actualCategory.trim().toUpperCase().replace("-", "_").replace(" ", "_");

        boolean matches = normalizedBranch.equals(normalizedActual);
        System.out.println("   " + (matches ? "✅" : "❌") + " Category (" + conditionKey + "): '" + actualCategory
                + "' vs '" + branchCategory + "'");
        return matches;
    }

    private boolean matchesLocation(Map<String, Object> branchConditions, Map<String, Object> actualConditions) {
        String branchLocation = String.valueOf(branchConditions.get("location"));
        String actualLocation = String.valueOf(actualConditions.get("location"));

        if (actualLocation == null || "null".equals(actualLocation) || actualLocation.trim().isEmpty()) {
            System.out.println("   ❌ Location: actualLocation is null or empty");
            return false;
        }

        String normalizedBranchLocation = branchLocation.trim().toUpperCase();
        String normalizedActualLocation = actualLocation.trim().toUpperCase();

        boolean matches;

        // Handle special location patterns like NON_BANGALORE, NON-BANGALORE
        if (normalizedBranchLocation.startsWith("NON_") || normalizedBranchLocation.startsWith("NON-")) {
            // Extract the location to exclude (e.g., "NON_BANGALORE" -> "BANGALORE")
            String excludedLocation = normalizedBranchLocation.replace("NON_", "").replace("NON-", "");

            // Check if actual location is NOT the excluded location
            // Also check common abbreviations (BLR for BANGALORE)
            boolean isExcludedLocation = normalizedActualLocation.equalsIgnoreCase(excludedLocation)
                    || (excludedLocation.equals("BANGALORE") && normalizedActualLocation.equals("BLR"))
                    || (excludedLocation.equals("BLR") && normalizedActualLocation.equals("BANGALORE"));

            matches = !isExcludedLocation;
            System.out.println("   " + (matches ? "✅" : "❌") + " Location: '" + actualLocation.trim()
                    + "' is " + (matches ? "NOT " : "") + excludedLocation + " (branch requires: " + branchLocation + ")");
        } else {
            // Direct location match
            matches = normalizedBranchLocation.equals(normalizedActualLocation)
                    || // Handle abbreviation matching
                    (normalizedBranchLocation.equals("BANGALORE") && normalizedActualLocation.equals("BLR"))
                    || (normalizedBranchLocation.equals("BLR") && normalizedActualLocation.equals("BANGALORE"));
            System.out.println("   " + (matches ? "✅" : "❌") + " Location: '" + actualLocation.trim()
                    + "' vs '" + branchLocation.trim() + "'");
        }

        return matches;
    }

    private boolean matchesProject(Map<String, Object> branchConditions, Map<String, Object> actualConditions) {
        String branchProject = String.valueOf(branchConditions.get("projectName"));
        String actualProject = String.valueOf(actualConditions.get("projectName"));

        if (actualProject == null || "null".equals(actualProject)) {
            return false;
        }

        boolean matches = branchProject.equalsIgnoreCase(actualProject);
        System.out.println("   " + (matches ? "✅" : "❌") + " Project: " + actualProject
                + " vs " + branchProject);
        return matches;
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }

        try {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            } else if (value instanceof Number) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            } else {
                return new BigDecimal(value.toString());
            }
        } catch (Exception e) {
            System.err.println("❌ Cannot convert to BigDecimal: " + value);
            return null;
        }
    }

    @Override
    public Map<String, Object> buildIndentConditions(String requestId) {
        Map<String, Object> conditions = new HashMap<>();

        if (requestId == null || requestId.trim().isEmpty()) {
            System.err.println("❌ RequestId is null or empty!");
            return conditions;
        }

        try {
            // Use repository directly to avoid transaction issues
            com.astro.entity.ProcurementModule.IndentCreation indent = indentRepository.findById(requestId).orElse(null);

            if (indent == null) {
                System.err.println("❌ Indent not found: " + requestId);
                return conditions;
            }

            conditions.put("totalAmount", indent.getTotalIntentValue());
            conditions.put("category", indent.getMaterialCategoryType());
            // Also put materialCategory as alias (frontend branch config may use either key)
            conditions.put("materialCategory", indent.getMaterialCategoryType());
            conditions.put("location", indent.getConsignesLocation());
            conditions.put("projectName", indent.getProjectName());

            // Derive isUnderProject: if explicitly set to true use that,
            // otherwise infer from projectName (handles default=false and null cases)
            Boolean isUnderProject = indent.getIsUnderProject();
            boolean hasProjectName = indent.getProjectName() != null && !indent.getProjectName().trim().isEmpty();
            if (isUnderProject == null || (!isUnderProject && hasProjectName)) {
                // If isUnderProject is null/false but a project name exists, the indent IS project-based
                isUnderProject = hasProjectName;
            }
            conditions.put("isUnderProject", isUnderProject);
            conditions.put("projectCode", indent.getProjectCode());
            conditions.put("indentorDepartment", indent.getIndentorDepartment());

            // Mode of Procurement: check indent-level field first, then fall back to first material or job detail
            String mop = indent.getModeOfProcurement();
            if (mop == null || mop.trim().isEmpty()) {
                try {
                    if ("job".equalsIgnoreCase(indent.getIndentType())) {
                        if (indent.getJobDetails() != null && !indent.getJobDetails().isEmpty()) {
                            mop = indent.getJobDetails().get(0).getModeOfProcurement();
                            System.out.println("   ℹ️ ModeOfProcurement from job detail: " + mop);
                        }
                    } else {
                        if (indent.getMaterialDetails() != null && !indent.getMaterialDetails().isEmpty()) {
                            mop = indent.getMaterialDetails().get(0).getModeOfProcurement();
                            System.out.println("   ℹ️ ModeOfProcurement from material detail: " + mop);
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("   ⚠️ Could not load line item details for MOP: " + ex.getMessage());
                }
            }
            conditions.put("modeOfProcurement", mop);

            // Check project budget availability if under project
            if (Boolean.TRUE.equals(indent.getIsUnderProject()) && indent.getProjectCode() != null) {
                BigDecimal availableLimit = getAvailableProjectLimit(indent.getProjectCode());
                conditions.put("availableProjectLimit", availableLimit);
                boolean budgetAvailable = availableLimit != null
                        && indent.getTotalIntentValue() != null
                        && availableLimit.compareTo(indent.getTotalIntentValue()) >= 0;
                conditions.put("projectBudgetAvailable", budgetAvailable);
            }

            System.out.println("📋 Indent Conditions Built for " + requestId + ": " + conditions);
            System.out.println("   Total Amount: " + indent.getTotalIntentValue());
            System.out.println("   Category: " + indent.getMaterialCategoryType());
            System.out.println("   Location: " + indent.getConsignesLocation());
            System.out.println("   Project: " + indent.getProjectName());
            System.out.println("   Is Under Project (raw): " + indent.getIsUnderProject() + " → derived: " + isUnderProject);
            System.out.println("   Project Code: " + indent.getProjectCode());
            System.out.println("   Department: " + indent.getIndentorDepartment());
            System.out.println("   Mode of Procurement (raw): " + indent.getModeOfProcurement() + " → derived: " + mop);

        } catch (Exception e) {
            System.err.println("❌ Error building indent conditions for " + requestId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return conditions;
    }

    // @Override
    // public Map<String, Object> buildTenderConditions(String requestId) {
    //     Map<String, Object> conditions = new HashMap<>();
    //     try {
    //         TenderWithIndentResponseDTO tender = tenderService.getTenderRequestById(requestId);
    //         conditions.put("totalAmount", tender.getTotalTenderValue());
    //         // Extract project name and location from first indent if available
    //         if (tender.getIndentResponseDTO() != null && !tender.getIndentResponseDTO().isEmpty()) {
    //             String projectName = tender.getIndentResponseDTO().get(0).getProjectName();
    //             conditions.put("projectName", projectName);
    //             String location = tender.getIndentResponseDTO().get(0).getConsignesLocation();
    //             conditions.put("location", location);
    //         }
    //         System.out.println("📋 Tender Conditions Built: " + conditions);
    //     } catch (Exception e) {
    //         System.err.println("❌ Error building tender conditions: " + e.getMessage());
    //     }
    //     return conditions;
    // }
    @Override
    public Map<String, Object> buildTenderConditions(String requestId) {
        Map<String, Object> conditions = new HashMap<>();

        TenderRequest tender = tenderRepository.findById(requestId).orElse(null);

        if (tender == null) {
            System.err.println("❌ Tender not found: " + requestId);
            return conditions;
        }

        conditions.put("totalAmount", tender.getTotalTenderValue());

        // ✅ ADD THIS (MOST IMPORTANT FIX)
        String category = mapToCategory(tender.getIndentMaterials());
        conditions.put("category", category);
        conditions.put("materialCategory", category);

        // existing
        conditions.put("location", tender.getConsignes());
        conditions.put("projectName", tender.getProjectName());
        conditions.put("modeOfProcurement", tender.getModeOfProcurement());

        // ✅ DEBUG (MANDATORY)
        System.out.println("🔥 FINAL TENDER CONDITIONS: " + conditions);

        System.out.println("📋 Tender Conditions Built for " + requestId + ": " + conditions);
        System.out.println("   Total Amount: " + tender.getTotalTenderValue());
        System.out.println("   Category: " + tender.getIndentMaterials());
        System.out.println("   Location: " + tender.getConsignes());
        System.out.println("   Project: " + tender.getProjectName());
        System.out.println("   Mode of Procurement: " + tender.getModeOfProcurement());

        return conditions;

    }

    private String mapToCategory(String materials) {
        if (materials == null) {
            return null;
        }

        String m = materials.toLowerCase();

        if (m.contains("laptop") || m.contains("computer") || m.contains("printer")) {
            return "COMPUTER";
        }

        return "NON_COMPUTER";
    }

    // @Override
    // public Map<String, Object> buildPOConditions(String requestId) {
    //     Map<String, Object> conditions = new HashMap<>();
    //     // PO conditions will be based on tender/indent data
    //     // This can be implemented when PO workflow uses branches
    //     System.out.println("📋 PO Conditions Built: " + conditions);
    //     return conditions;
    // }
    // updated buildPOConditions to include actual conditions based on linked tender and indent
    @Override
    public Map<String, Object> buildPOConditions(String requestId) {

        Map<String, Object> conditions = new HashMap<>();

        try {
            // added by abhinav
            PurchaseOrder po = purchaseOrderRepository.findById(requestId).orElse(null);
// Modified by Aman
            // if (po == null) {
            //     System.err.println("❌ PO not found: " + requestId);
            //     return conditions;
            // }

            conditions.put("totalAmount", po.getTotalValueOfPo());
            conditions.put("projectName", po.getProjectName());
            // conditions.put("location", po.getConsignesAddress());

            // // Mode of Procurement from Tender
            // if (po.getTenderId() != null) {
            //     String mop = tenderRepository.findModeOfProcurementByTenderId(po.getTenderId());
            //     conditions.put("modeOfProcurement", mop);
            // }
// End 
            System.out.println("📋 PO Conditions Built: " + conditions);

        } catch (Exception e) {
            System.err.println("❌ Error building PO conditions: " + e.getMessage());
            e.printStackTrace();
        }

        return conditions;
    }

     //  added by Aman 
     @Override 
     public Map<String, Object> buildContingencyConditions(String requestId) {
        Map<String, Object> conditions = new HashMap<>();

        try {
            ContigencyPurchase contigency = contigencyPurchaseRepository.findById(requestId).orElse(null);
            if (contigency != null) {
                conditions.put("totalAmount", contigency.getTotalCpValue());
                conditions.put("projectName", contigency.getProjectName());
                // conditions.put("location", contigency.getConsignesLocation());
                //  conditions.put("category", indent.getMaterialCategoryType());
            // Also put materialCategory as alias (frontend branch config may use either key)
            // conditions.put("materialCategory", indent.getMaterialCategoryType());

            }
        } catch (Exception e) {
            System.err.println("❌ Error building Contingency conditions: " + e.getMessage());
            e.printStackTrace();
        }

        return conditions;
    }

    @Override
    public Map<String,Object> buildPaymentConditions(String requestId)
    {

         Map<String, Object> conditions = new HashMap<>();

        try {

            String[] splitforid = requestId.split("/");
            long id = Long.parseLong(splitforid[2]);
            
            PaymentVoucher pv = paymentVoucherReposiotry.findById(id).orElse(null);


            conditions.put("totalAmount", pv.getPaymentVoucherNetAmount());
            // conditions.put("projectName", pv.getProjectName());
         
            System.out.println("📋 Payment Voucher Conditions Built: " + conditions);

        } catch (Exception e) {
            System.err.println("❌ Error building Payment Voucher conditions: " + e.getMessage());
            e.printStackTrace();
        }

        return conditions;
    } 

    @Override
    public Map<String, Object> buildSOConditions(String requestId) {

        Map<String, Object> conditions = new HashMap<>();

        try {
           
            ServiceOrder so = serviceOrderRepository.findById(requestId).orElse(null);


            conditions.put("totalAmount", so.getTotalValueOfSo());
            // conditions.put("projectName", so.getProjectName());
       
            System.out.println("📋 SO Conditions Built: " + conditions);

        } catch (Exception e) {
            System.err.println("❌ Error building SO conditions: " + e.getMessage());
            e.printStackTrace();
        }

        return conditions;
    }
    // End

    // ==================== NEW DYNAMIC WORKFLOW METHODS ====================
    @Override
    public ApproverMaster getNextApproverWithLimitCheck(
            Long branchId,
            Integer currentLevel,
            Integer currentSequence,
            BigDecimal indentValue,
            String departmentName,
            String category,
            String location
    ) {
        // Get the basic next approver
        ApproverMaster nextApprover = getNextApprover(branchId, currentLevel, currentSequence);

        if (nextApprover == null) {
            return null; // Workflow complete
        }

        // Build conditions for checking skip/escalate
        Map<String, Object> conditions = new HashMap<>();
        conditions.put("totalAmount", indentValue);
        conditions.put("indentorDepartment", departmentName);
        conditions.put("category", category);
        conditions.put("location", location);

        // Check if this approver should be skipped
        while (nextApprover != null && shouldSkipApprover(nextApprover, conditions)) {
            System.out.println("⏩ Skipping approver: " + nextApprover.getRoleName());
            nextApprover = getNextApprover(branchId, nextApprover.getApprovalLevel(), nextApprover.getApprovalSequence());
        }

        return nextApprover;
    }

    @Override
    public EscalationCheckResultDTO checkEscalationRequired(
            String roleName,
            BigDecimal indentValue,
            String category,
            String departmentName,
            String location
    ) {
        return approvalLimitService.checkEscalation(roleName, category, departmentName, location, indentValue);
    }

    @Override
    public DepartmentApproverMapping getDepartmentApprover(String departmentName, BigDecimal indentValue) {
        if (departmentName == null || departmentName.trim().isEmpty()) {
            System.out.println("⚠️ Department name is null or empty");
            return null;
        }

        List<DepartmentApproverMapping> approvers = departmentApproverMappingRepository
                .findByDepartmentNameAndIsActiveTrue(departmentName);

        if (approvers.isEmpty()) {
            System.out.println("⚠️ No approvers configured for department: " + departmentName);
            return null;
        }

        // Find Head SEG first (lower limit = ₹1,00,000)
        Optional<DepartmentApproverMapping> headSEG = approvers.stream()
                .filter(a -> "HEAD_SEG".equalsIgnoreCase(a.getApproverType()))
                .findFirst();

        // Find Dean (higher limit = ₹1,50,000)
        Optional<DepartmentApproverMapping> dean = approvers.stream()
                .filter(a -> "DEAN".equalsIgnoreCase(a.getApproverType()))
                .findFirst();

        // Decision logic based on amount
        if (headSEG.isPresent() && !headSEG.get().exceedsLimit(indentValue)) {
            System.out.println("✅ Routing to Head SEG for department: " + departmentName);
            return headSEG.get();
        } else if (dean.isPresent()) {
            System.out.println("✅ Routing to Dean for department: " + departmentName);
            return dean.get();
        } else if (headSEG.isPresent()) {
            return headSEG.get();
        }

        return null;
    }

    @Override
    public FieldStationApproverMaster getFieldStationApprover(String location) {
        if (location == null || location.trim().isEmpty()) {
            return null;
        }

        String normalizedLocation = location.trim().toUpperCase();

        // Bangalore doesn't need field station in-charge
        if ("BANGALORE".equals(normalizedLocation) || "BLR".equals(normalizedLocation)) {
            System.out.println("📍 Location is Bangalore - no field station in-charge needed");
            return null;
        }

        return fieldStationApproverMasterRepository.findByFieldStationNameIgnoreCase(location)
                .orElseGet(() -> {
                    System.out.println("⚠️ No field station in-charge found for: " + location);
                    return null;
                });
    }

    @Override
    public boolean checkProjectBudgetAvailable(String projectCode, BigDecimal indentValue) {
        if (projectCode == null || indentValue == null) {
            return false;
        }

        BigDecimal availableLimit = getAvailableProjectLimit(projectCode);
        if (availableLimit == null) {
            return false;
        }

        boolean available = availableLimit.compareTo(indentValue) >= 0;
        System.out.println("💰 Project Budget Check: " + projectCode
                + " - Available: ₹" + availableLimit
                + ", Required: ₹" + indentValue
                + ", Result: " + (available ? "AVAILABLE" : "INSUFFICIENT"));
        return available;
    }

    @Override
    public BigDecimal getAvailableProjectLimit(String projectCode) {
        if (projectCode == null) {
            return null;
        }

        return projectMasterRepository.findById(projectCode)
                .map(ProjectMaster::getAvailableProjectLimit)
                .orElse(null);
    }

    @Override
    public boolean shouldSkipApprover(ApproverMaster approver, Map<String, Object> conditions) {
        // DEPARTMENT_BASED: skip this approver if its role doesn't handle the indentor's department
        if ("DEPARTMENT_BASED".equalsIgnoreCase(approver.getConditionCheckType())) {
            return shouldSkipDepartmentBasedApprover(approver, conditions);
        }

        if (approver.getSkipIfCondition() == null || approver.getSkipIfCondition().trim().isEmpty()) {
            return false;
        }

        try {
            Map<String, Object> skipConfig = objectMapper.readValue(
                    approver.getSkipIfCondition(),
                    new TypeReference<Map<String, Object>>() {
            }
            );

            String field = String.valueOf(skipConfig.get("field"));
            String operator = String.valueOf(skipConfig.get("operator"));
            Object conditionValue = skipConfig.get("value");

            Object actualValue = conditions.get(field);
            if (actualValue == null) {
                return false;
            }

            // Compare based on operator
            if ("totalAmount".equals(field) || field.contains("Amount") || field.contains("amount")) {
                BigDecimal actual = convertToBigDecimal(actualValue);
                BigDecimal threshold = convertToBigDecimal(conditionValue);

                if (actual == null || threshold == null) {
                    return false;
                }

                return switch (operator.toUpperCase()) {
                    case "LT" ->
                        actual.compareTo(threshold) < 0;
                    case "LTE" ->
                        actual.compareTo(threshold) <= 0;
                    case "GT" ->
                        actual.compareTo(threshold) > 0;
                    case "GTE" ->
                        actual.compareTo(threshold) >= 0;
                    case "EQ" ->
                        actual.compareTo(threshold) == 0;
                    default ->
                        false;
                };
            }

            return false;
        } catch (Exception e) {
            System.err.println("❌ Error checking skip condition: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns true if this DEPARTMENT_BASED approver should be skipped because
     * the indentor's department is not mapped to this approver's role type.
     */
    private boolean shouldSkipDepartmentBasedApprover(ApproverMaster approver, Map<String, Object> conditions) {
        String department = (String) conditions.get("indentorDepartment");
        if (department == null || department.trim().isEmpty()) {
            System.out.println("⚠️ No indentorDepartment in conditions – not skipping " + approver.getRoleName());
            return false;
        }

        String approverType = deriveApproverType(approver.getRoleName());
        if (approverType.isEmpty()) {
            System.out.println("⚠️ Cannot derive approver type from role '" + approver.getRoleName() + "' – not skipping");
            return false;
        }

        // All departments that are mapped to this approver type (e.g. DEAN or HEAD_SEG)
        List<String> handledDepartments = departmentApproverMappingRepository
                .findDepartmentsWithApproverType(approverType);

        // If no departments are configured for this approver type yet, don't skip —
        // treat it as "applies to all departments" (safe fallback so mandatory
        // approvers are never accidentally dropped from the chain).
        if (handledDepartments.isEmpty()) {
            System.out.println("⚠️ No department mappings configured for type '" + approverType
                    + "' – keeping " + approver.getRoleName() + " (applies to all depts by default)");
            return false;
        }

        boolean handles = handledDepartments.stream()
                .anyMatch(d -> d.trim().equalsIgnoreCase(department.trim()));

        if (handles) {
            System.out.println("✅ " + approver.getRoleName() + " handles dept '" + department + "' – keeping");
            return false;
        } else {
            System.out.println("⏩ Skipping " + approver.getRoleName() + " – dept '" + department + "' not mapped to " + approverType);
            return true;
        }
    }

    /**
     * Derives DEAN or HEAD_SEG from a role name (case-insensitive).
     */
    private String deriveApproverType(String roleName) {
        if (roleName == null) {
            return "";
        }
        String upper = roleName.trim().toUpperCase();
        if (upper.contains("DEAN")) {
            return "DEAN";
        }
        if (upper.contains("HEAD") && upper.contains("SEG")) {
            return "HEAD_SEG";
        }
        return "";
    }

    @Override
    public EscalationCheckResultDTO checkApproverEscalation(ApproverMaster approver, Map<String, Object> conditions) {
        if (approver.getEscalateIfCondition() == null || approver.getEscalateIfCondition().trim().isEmpty()) {
            return EscalationCheckResultDTO.noEscalation();
        }

        try {
            Map<String, Object> escalateConfig = objectMapper.readValue(
                    approver.getEscalateIfCondition(),
                    new TypeReference<Map<String, Object>>() {
            }
            );

            String field = String.valueOf(escalateConfig.get("field"));
            String operator = String.valueOf(escalateConfig.get("operator"));

            Object actualValue = conditions.get(field);
            if (actualValue == null) {
                return EscalationCheckResultDTO.noEscalation();
            }

            BigDecimal actual = convertToBigDecimal(actualValue);
            if (actual == null) {
                return EscalationCheckResultDTO.noEscalation();
            }

            // Get limit to compare against
            BigDecimal limit = null;
            if (escalateConfig.containsKey("value")) {
                limit = convertToBigDecimal(escalateConfig.get("value"));
            } else if (escalateConfig.containsKey("limitField")) {
                // Get limit from another field (e.g., approvalLimit)
                String limitField = String.valueOf(escalateConfig.get("limitField"));
                limit = convertToBigDecimal(conditions.get(limitField));
            }

            if (limit == null) {
                return EscalationCheckResultDTO.noEscalation();
            }

            boolean shouldEscalate = switch (operator.toUpperCase()) {
                case "GT" ->
                    actual.compareTo(limit) > 0;
                case "GTE" ->
                    actual.compareTo(limit) >= 0;
                case "LT" ->
                    actual.compareTo(limit) < 0;
                case "LTE" ->
                    actual.compareTo(limit) <= 0;
                default ->
                    false;
            };

            if (shouldEscalate) {
                String reason = String.format("Amount ₹%s exceeds %s's limit of ₹%s",
                        actual, approver.getRoleName(), limit);
                return EscalationCheckResultDTO.escalateTo("Director", reason, actual, limit);
            }

            return EscalationCheckResultDTO.noEscalation();

        } catch (Exception e) {
            System.err.println("❌ Error checking escalation condition: " + e.getMessage());
            return EscalationCheckResultDTO.noEscalation();
        }
    }

    // Enhanced branch matching that supports isUnderProject, projectBased, isProject and budget checks
    private boolean matchesIsUnderProject(Map<String, Object> branchConditions, Map<String, Object> actualConditions) {
        if (!branchConditions.containsKey("isUnderProject")) {
            return true;
        }

        Boolean branchRequiresUnderProject = toBoolean(branchConditions.get("isUnderProject"));
        Boolean actualIsUnderProject = toBoolean(actualConditions.get("isUnderProject"));

        if (actualIsUnderProject == null) {
            actualIsUnderProject = false;
        }

        boolean matches = branchRequiresUnderProject.equals(actualIsUnderProject);
        System.out.println("   " + (matches ? "✅" : "❌") + " isUnderProject: "
                + actualIsUnderProject + " vs required: " + branchRequiresUnderProject);
        return matches;
    }

    // Matches "projectBased" key (alias used by frontend for isUnderProject)
    private boolean matchesProjectBased(Map<String, Object> branchConditions, Map<String, Object> actualConditions) {
        Boolean branchRequiresProject = toBoolean(branchConditions.get("projectBased"));
        Boolean actualIsUnderProject = toBoolean(actualConditions.get("isUnderProject"));

        if (actualIsUnderProject == null) {
            actualIsUnderProject = false;
        }

        boolean matches = branchRequiresProject.equals(actualIsUnderProject);
        System.out.println("   " + (matches ? "✅" : "❌") + " projectBased: "
                + actualIsUnderProject + " vs required: " + branchRequiresProject);
        return matches;
    }

    // Matches "isProject" key (another alias)
    private boolean matchesIsProject(Map<String, Object> branchConditions, Map<String, Object> actualConditions) {
        Boolean branchRequiresProject = toBoolean(branchConditions.get("isProject"));
        Boolean actualIsUnderProject = toBoolean(actualConditions.get("isUnderProject"));

        if (actualIsUnderProject == null) {
            actualIsUnderProject = false;
        }

        boolean matches = branchRequiresProject.equals(actualIsUnderProject);
        System.out.println("   " + (matches ? "✅" : "❌") + " isProject: "
                + actualIsUnderProject + " vs required: " + branchRequiresProject);
        return matches;
    }

    // Safe Boolean conversion - handles Boolean, String "true"/"false", and Integer 0/1
    private Boolean toBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        return null;
    }

    private boolean matchesBudgetCheck(Map<String, Object> branchConditions, Map<String, Object> actualConditions) {
        if (!branchConditions.containsKey("budgetCheck")) {
            return true;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> budgetCheck = (Map<String, Object>) branchConditions.get("budgetCheck");
        if (budgetCheck == null) {
            return true;
        }

        String operator = String.valueOf(budgetCheck.get("operator"));

        // Get available budget from conditions
        Boolean budgetAvailable = (Boolean) actualConditions.get("projectBudgetAvailable");
        if (budgetAvailable == null) {
            return false;
        }

        // GTE means budget should be available (available >= required)
        // LT means budget should not be available (available < required)
        boolean matches;
        if ("GTE".equalsIgnoreCase(operator)) {
            matches = budgetAvailable;
        } else if ("LT".equalsIgnoreCase(operator)) {
            matches = !budgetAvailable;
        } else {
            matches = true;
        }

        System.out.println("   " + (matches ? "✅" : "❌") + " Budget Check: budgetAvailable="
                + budgetAvailable + ", operator=" + operator);
        return matches;
    }

    private boolean matchesIndentorDepartment(Map<String, Object> branchConditions, Map<String, Object> actualConditions) {
        String branchDept = String.valueOf(branchConditions.get("indentorDepartment"));
        String actualDept = String.valueOf(actualConditions.get("indentorDepartment"));

        if (actualDept == null || "null".equals(actualDept) || actualDept.trim().isEmpty()) {
            System.out.println("   ❌ IndentorDepartment: actualDept is null or empty");
            return false;
        }

        boolean matches = branchDept.trim().equalsIgnoreCase(actualDept.trim());
        System.out.println("   " + (matches ? "✅" : "❌") + " IndentorDepartment: '" + actualDept + "' vs '" + branchDept + "'");
        return matches;
    }

    private boolean matchesModeOfProcurement(Map<String, Object> branchConditions, Map<String, Object> actualConditions) {
        String branchMOP = String.valueOf(branchConditions.get("modeOfProcurement"));
        String actualMOP = String.valueOf(actualConditions.get("modeOfProcurement"));

        if (actualMOP == null || "null".equals(actualMOP) || actualMOP.trim().isEmpty()) {
            System.out.println("   ❌ Mode of Procurement: actualMOP is null or empty");
            return false;
        }

        // Normalize for comparison
        String normalizedBranch = branchMOP.trim().toUpperCase().replace(" ", "_").replace("-", "_");
        String normalizedActual = actualMOP.trim().toUpperCase().replace(" ", "_").replace("-", "_");

        boolean matches = normalizedBranch.equals(normalizedActual);
        System.out.println("   " + (matches ? "✅" : "❌") + " Mode of Procurement: '" + actualMOP
                + "' vs '" + branchMOP + "'");
        return matches;
    }
}

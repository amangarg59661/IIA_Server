package com.astro.service;

import com.astro.dto.workflow.EscalationCheckResultDTO;
import com.astro.entity.AdminPanel.ApproverMaster;
import com.astro.entity.AdminPanel.DepartmentApproverMapping;
import com.astro.entity.AdminPanel.FieldStationApproverMaster;
import com.astro.entity.AdminPanel.WorkflowBranchMaster;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Service for branch-based workflow routing This service handles matching
 * workflow branches based on conditions and determining the next approver in
 * the sequential approval chain
 */
public interface BranchWorkflowService {

    /**
     * Find matching branch for a workflow based on conditions
     *
     * @param workflowId Workflow ID
     * @param conditions Map of condition values (e.g., "totalAmount",
     * "category", "location")
     * @return Matching WorkflowBranchMaster or null if no match
     */
    WorkflowBranchMaster findMatchingBranch(Integer workflowId, Map<String, Object> conditions);

    /**
     * Get all approvers for a branch in sequential order
     *
     * @param branchId Branch ID
     * @return List of ApproverMaster ordered by level and sequence
     */
    List<ApproverMaster> getApproversForBranch(Long branchId);

    /**
     * Get the first approver for a branch
     *
     * @param branchId Branch ID
     * @return First ApproverMaster or null
     */
    ApproverMaster getFirstApprover(Long branchId);

    /**
     * Get the first approver for a branch, applying department-based skip
     * logic. When approvers have conditionCheckType = DEPARTMENT_BASED, the
     * method skips approvers whose role (Dean/Head SEG) is not mapped to the
     * indentor's department.
     *
     * @param branchId Branch ID
     * @param departmentName Indentor's department
     * @param indentValue Total indent value
     * @return First matching ApproverMaster or null
     */
    ApproverMaster getFirstApproverForDepartment(Long branchId, String departmentName, BigDecimal indentValue);

    /**
     * Get the next approver after current one
     *
     * @param branchId Branch ID
     * @param currentApprovalLevel Current approval level
     * @param currentApprovalSequence Current approval sequence
     * @return Next ApproverMaster or null if no more approvers
     */
    ApproverMaster getNextApprover(Long branchId, Integer currentApprovalLevel, Integer currentApprovalSequence);

    /**
     * Check if branch condition matches given values
     *
     * @param branch WorkflowBranchMaster with condition configuration
     * @param conditions Map of actual values to match against
     * @return true if branch matches
     */
    boolean matchesBranchCondition(WorkflowBranchMaster branch, Map<String, Object> conditions);

    /**
     * Build conditions map for indent workflow
     *
     * @param requestId Indent ID
     * @return Map of conditions extracted from indent
     */
    Map<String, Object> buildIndentConditions(String requestId);

    /**
     * Build conditions map for tender workflow
     *
     * @param requestId Tender ID
     * @return Map of conditions extracted from tender
     */
    Map<String, Object> buildTenderConditions(String requestId);

    /**
     * Build conditions map for PO workflow
     *
     * @param requestId PO ID
     * @return Map of conditions extracted from PO
     */
    Map<String, Object> buildPOConditions(String requestId);
//     Added by aman 
  /**
     * Build conditions map for PO workflow
     *
     * @param requestId SO ID
     * @return Map of conditions extracted from PO
     */
    Map<String, Object> buildSOConditions(String requestId);
 /** 
 @param requestId SO ID
     * @return Map of conditions extracted from PO
     */
    Map<String, Object> buildContingencyConditions(String requestId);
/**
     * Build conditions map for PO workflow
     *
     * @param requestId Payment ID
     * @return Map of conditions extracted from PO
     */    
        Map<String,Object> buildPaymentConditions(String requestId);
        // End

    // ==================== NEW DYNAMIC WORKFLOW METHODS ====================
    /**
     * Get next approver with limit checking - skips or escalates based on
     * approval limits
     *
     * @param branchId Branch ID
     * @param currentLevel Current approval level
     * @param currentSequence Current approval sequence
     * @param indentValue The indent value to check against limits
     * @param departmentName The indentor's department
     * @param category Material category (COMPUTER/NON_COMPUTER)
     * @param location Location (BANGALORE/NON_BANGALORE)
     * @return Next approver considering limits, or null if workflow complete
     */
    ApproverMaster getNextApproverWithLimitCheck(
            Long branchId,
            Integer currentLevel,
            Integer currentSequence,
            BigDecimal indentValue,
            String departmentName,
            String category,
            String location
    );

    /**
     * Check if escalation is required based on approval limits
     *
     * @param roleName Current approver role name
     * @param indentValue The indent value
     * @param category Material category
     * @param departmentName Indentor's department
     * @param location Location
     * @return Escalation check result with details
     */
    EscalationCheckResultDTO checkEscalationRequired(
            String roleName,
            BigDecimal indentValue,
            String category,
            String departmentName,
            String location
    );

    /**
     * Get department-specific approver (Dean or Head SEG) based on department
     * and amount
     *
     * @param departmentName The department name
     * @param indentValue The indent value (determines Dean vs Head SEG)
     * @return Department approver mapping or null
     */
    DepartmentApproverMapping getDepartmentApprover(String departmentName, BigDecimal indentValue);

    /**
     * Get field station in-charge for non-Bangalore locations
     *
     * @param location The location/field station name
     * @return Field station approver or null if Bangalore or not found
     */
    FieldStationApproverMaster getFieldStationApprover(String location);

    /**
     * Check if project budget is available for the indent
     *
     * @param projectCode The project code
     * @param indentValue The indent value
     * @return true if project has sufficient available limit
     */
    boolean checkProjectBudgetAvailable(String projectCode, BigDecimal indentValue);

    /**
     * Get available project limit for a project
     *
     * @param projectCode The project code
     * @return Available project limit or null if project not found
     */
    BigDecimal getAvailableProjectLimit(String projectCode);

    /**
     * Check if an approver should be skipped based on skip conditions
     *
     * @param approver The approver to check
     * @param conditions The workflow conditions
     * @return true if approver should be skipped
     */
    boolean shouldSkipApprover(ApproverMaster approver, Map<String, Object> conditions);

    /**
     * Check if workflow should escalate based on approver's escalation
     * conditions
     *
     * @param approver The approver to check
     * @param conditions The workflow conditions
     * @return Escalation result or null if no escalation needed
     */
    EscalationCheckResultDTO checkApproverEscalation(ApproverMaster approver, Map<String, Object> conditions);
}

package com.astro.service.AdminPanel;

import com.astro.dto.AdminPanel.ApprovalLimitDTO;
import com.astro.dto.workflow.EscalationCheckResultDTO;
import com.astro.entity.AdminPanel.ApprovalLimitMaster;

import java.math.BigDecimal;
import java.util.List;

public interface ApprovalLimitService {

    // CRUD operations
    ApprovalLimitDTO create(ApprovalLimitDTO dto);

    ApprovalLimitDTO update(Long limitId, ApprovalLimitDTO dto);

    void delete(Long limitId);

    ApprovalLimitDTO getById(Long limitId);

    List<ApprovalLimitDTO> getAll(Integer workflowId);
    /**
 * Alias for getAll(null) — backward compatible for callers that don't scope by workflow
 */
default List<ApprovalLimitDTO> getAll() {
    return getAll(null);
}

    List<ApprovalLimitDTO> getByRole(String roleName);

    List<ApprovalLimitDTO> getByRoleId(Integer roleId);

    List<ApprovalLimitDTO> getByCategory(String category);

    // Business logic
    /**
     * Find the most applicable limit for a given context
     */
    ApprovalLimitMaster findApplicableLimit(
            String roleName,
            String category,
            String departmentName,
            String location
    );

    /**
     * Check if the amount exceeds the applicable limit
     */
    boolean exceedsLimit(String roleName, String category, String departmentName, String location, BigDecimal amount);

    /**
     * Get escalation details when limit is exceeded
     */
    EscalationCheckResultDTO checkEscalation(
            String roleName,
            String category,
            String departmentName,
            String location,
            BigDecimal amount
    );

    /**
     * Get the maximum amount this role can approve for given context
     */
    BigDecimal getMaxApprovalAmount(String roleName, String category, String departmentName, String location);

    /**
     * Alias for getByRole - Get limits by role name
     */
    default List<ApprovalLimitDTO> getByRoleName(String roleName) {
        return getByRole(roleName);
    }

    /**
     * Alias for findApplicableLimit - Get applicable limit
     */
    default ApprovalLimitMaster getApplicableLimit(String roleName, String category, String departmentName, String location) {
        return findApplicableLimit(roleName, category, departmentName, location);
    }

    /**
     * Update the active status of an approval limit
     */
    ApprovalLimitDTO updateStatus(Long limitId, Boolean isActive, String updatedBy);

    /**
     * Get all distinct role names with configured limits
     */
    List<String> getDistinctRoles();

    /**
     * Get all distinct categories
     */
    List<String> getDistinctCategories();
}

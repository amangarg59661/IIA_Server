package com.astro.repository.AdminPanel;

import com.astro.entity.AdminPanel.WorkflowBranchMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowBranchMasterRepository extends JpaRepository<WorkflowBranchMaster, Long> {
    List<WorkflowBranchMaster> findByWorkflowId(Integer workflowId);
    List<WorkflowBranchMaster> findByWorkflowIdAndIsActiveTrue(Integer workflowId);
    Optional<WorkflowBranchMaster> findByWorkflowIdAndBranchCode(Integer workflowId, String branchCode);
    List<WorkflowBranchMaster> findByWorkflowIdOrderByDisplayOrderAsc(Integer workflowId);
    List<WorkflowBranchMaster> findByWorkflowIdAndIsActiveTrueAndBranchIdIn(Integer workflowId, List<Long> branchIds);
    // Check for duplicate condition config
    Optional<WorkflowBranchMaster> findByWorkflowIdAndConditionConfig(Integer workflowId, String conditionConfig);

    // Check for duplicate condition config excluding a specific branch (for updates)
    List<WorkflowBranchMaster> findByWorkflowIdAndConditionConfigAndBranchIdNot(Integer workflowId, String conditionConfig, Long branchId);
}

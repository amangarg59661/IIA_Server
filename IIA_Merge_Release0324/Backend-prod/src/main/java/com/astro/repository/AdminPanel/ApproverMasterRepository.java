package com.astro.repository.AdminPanel;

import com.astro.entity.AdminPanel.ApproverMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;  // added by abhinav
import org.springframework.data.jpa.repository.Query; // added by abhinav
import org.springframework.data.repository.query.Param; // added by abhinav
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApproverMasterRepository extends JpaRepository<ApproverMaster, Long> {

    List<ApproverMaster> findByWorkflowIdAndBranchId(Integer workflowId, Long branchId);

    List<ApproverMaster> findByWorkflowIdAndBranchIdAndStatus(Integer workflowId, Long branchId, String status);

    Optional<ApproverMaster> findByApproverCode(String approverCode);

    List<ApproverMaster> findByWorkflowIdAndBranchIdOrderByApprovalLevelAscApprovalSequenceAsc(Integer workflowId, Long branchId);

    List<ApproverMaster> findByWorkflowId(Integer workflowId);

    List<ApproverMaster> findByRoleId(Integer roleId);
    // added by abhinav starts

    List<ApproverMaster> findByBranchIdAndStatusOrderByApprovalLevelAscApprovalSequenceAsc(
            Long branchId,
            String status
    );

        List<ApproverMaster> findByWorkflowIdAndStatus(Integer workflowId, String status);

    // Get max approval level for a branch
    @Query("SELECT COALESCE(MAX(a.approvalLevel), 0) FROM ApproverMaster a WHERE a.workflowId = :workflowId AND a.branchId = :branchId")
    Integer findMaxApprovalLevel(@Param("workflowId") Integer workflowId, @Param("branchId") Long branchId);

    // Get max approval sequence for a branch
    @Query("SELECT COALESCE(MAX(a.approvalSequence), 0) FROM ApproverMaster a WHERE a.workflowId = :workflowId AND a.branchId = :branchId")
    Integer findMaxApprovalSequence(@Param("workflowId") Integer workflowId, @Param("branchId") Long branchId);

    // Find approvers with approval level >= specified level (for shifting)
    List<ApproverMaster> findByWorkflowIdAndBranchIdAndApprovalLevelGreaterThanEqual(
            Integer workflowId, Long branchId, Integer approvalLevel);

    // Increment approval levels for approvers at or above a certain level
    @Modifying
    @Query("UPDATE ApproverMaster a SET a.approvalLevel = a.approvalLevel + 1 "
            + "WHERE a.workflowId = :workflowId AND a.branchId = :branchId AND a.approvalLevel >= :fromLevel")
    int incrementApprovalLevels(@Param("workflowId") Integer workflowId,
            @Param("branchId") Long branchId,
            @Param("fromLevel") Integer fromLevel);


            @Query("SELECT DISTINCT a.workflowId FROM ApproverMaster a WHERE a.status = :status")
List<Integer> findDistinctWorkflowIdByStatus(@Param("status") String status);
}

package com.astro.repository;

import com.astro.dto.workflow.CompletedIndentsQueueResponse;
import com.astro.dto.workflow.ProcurementDtos.pendingRecordsDto;
import com.astro.dto.workflow.QueueResponse;
import com.astro.entity.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, Integer> {
    List<WorkflowTransition> findByWorkflowId(Integer workflowId);
    WorkflowTransition findByWorkflowIdAndTransitionOrder(Integer workflowId, Integer order);
    List<WorkflowTransition> findByWorkflowIdOrCreatedByOrRequestIdOrTransitionId(Integer workflowId, Integer createdBy, Integer requestId, Integer nextTransitionId);
    // WorkflowTransition findByWorkflowIdAndCreatedByAndRequestId(Integer workflowId, Integer createdBy, String requestId);
    List<WorkflowTransition> findByWorkflowIdAndCreatedByAndRequestId(
        Integer workflowId, Integer createdBy, String requestId);  // updated by abhinav
    List<WorkflowTransition> findByWorkflowIdOrCreatedByOrRequestId(Integer workflowId, Integer createdBy, Integer requestId);
    List<WorkflowTransition> findByWorkflowIdAndCurrentRole(Integer workflowId, String roleName);
    List<WorkflowTransition> findByRequestId(String requestId);
    WorkflowTransition findByWorkflowTransitionIdAndRequestId(Integer workflowTransitionId, String requestId);
    List<WorkflowTransition> findByNextActionAndNextRole(String pendingType, String roleName);
    List<WorkflowTransition> findByNextRole(String roleName);
    List<WorkflowTransition> findByWorkflowIdAndRequestId(Integer workflowId, String requestId);
    List<WorkflowTransition> findByWorkflowIdAndRequestIdAndCurrentRole(Integer workflowId, String requestId, String assignmentRole);
    List<WorkflowTransition> findByWorkflowIdAndRequestIdAndNextRole(Integer workflowId, String requestId, String assignmentRole);
    List<WorkflowTransition> findByModifiedBy(Integer modifiedBy);
   // Optional<WorkflowTransition> findTopByRequestIdOrderByCreatedDateDesc(String requestId);
   Optional<WorkflowTransition> findTopByRequestIdOrderByWorkflowTransitionIdDesc(String requestId);



    @Query("SELECT wt.requestId FROM WorkflowTransition wt WHERE wt.workflowName = 'Indent Workflow' AND wt.status = 'Completed' AND wt.nextAction IS NULL AND wt.requestId NOT IN (SELECT i.indentId FROM IndentId i WHERE i.tenderRequest IS NOT NULL)")
    List<String> findApprovedIndentRequestIds();

    //@Query("SELECT wt.requestId FROM WorkflowTransition wt WHERE wt.workflowName = 'Tender Approver Workflow' AND wt.status = 'Completed' AND wt.nextAction IS NULL")
    @Query("SELECT wt.requestId FROM WorkflowTransition wt WHERE wt.workflowName = 'Tender Approver Workflow' AND wt.status = 'Completed' AND wt.nextAction IS NULL AND wt.requestId NOT IN (SELECT swt.requestId FROM SubWorkflowTransition swt) AND wt.requestId NOT IN (SELECT po.tenderId FROM PurchaseOrder po) AND wt.requestId NOT IN (SELECT so.tenderId FROM ServiceOrder so)")
    List<String> findApprovedTenderRequestIds();
    @Query("""
  SELECT CASE WHEN COUNT(wt) > 0 THEN true ELSE false END
  FROM WorkflowTransition wt
  WHERE wt.workflowName = 'Tender Approver Workflow'
    AND wt.status = 'Completed'
    AND wt.nextAction IS NULL
    AND wt.requestId = :requestId
    AND wt.requestId NOT IN (SELECT swt.requestId FROM SubWorkflowTransition swt)
    AND wt.requestId NOT IN (SELECT po.tenderId FROM PurchaseOrder po)
    AND wt.requestId NOT IN (SELECT so.tenderId FROM ServiceOrder so)
""")
    boolean isApprovedTenderAndNotUsed(@Param("requestId") String requestId);



 //   @Query("Select wt.requestId from WorkflowTransition wt WHERE wt.workflowName = 'Tender Evaluator Workflow' AND wt.status = 'Completed' AND wt.nextAction IS NULL AND wt.requestId NOT IN (SELECT po.tenderId FROM PurchaseOrder po) AND wt.requestId NOT IN (SELECT so.tenderId FROM ServiceOrder so)")
 //   List<String> findApprovedTenderIdsForPOANDSO();
    @Query("Select wt.requestId from WorkflowTransition wt WHERE wt.workflowName = 'Tender Approver Workflow' AND wt.status = 'Completed' AND wt.nextAction IS NULL AND wt.requestId NOT IN (SELECT po.tenderId FROM PurchaseOrder po) AND wt.requestId NOT IN (SELECT so.tenderId FROM ServiceOrder so)")
    List<String> findApprovedTenderIdsForPOANDSO();

    @Query("SELECT wt.requestId FROM WorkflowTransition wt WHERE wt.workflowName = 'PO Workflow' AND wt.status = 'Completed' AND wt.nextAction IS NULL")
    List<String> findApprovedPoIds();

    @Query("SELECT wt.requestId FROM WorkflowTransition wt WHERE wt.workflowName = 'Tender Approver Workflow' AND wt.status = 'Completed' AND wt.nextAction IS NULL")
    List<String> findApprovedTenderIds();

    List<WorkflowTransition> findByWorkflowIdAndRequestIdOrderByWorkflowTransitionIdAsc(Integer workflowId, String requestId);

  //  List<WorkflowTransition> findByStatusAndWorkflowId(String completedType, int workflowId);
 /* @Query("""
SELECT wt 
FROM WorkflowTransition wt 
WHERE wt.status = :status 
  AND wt.workflowId = :workflowId 
  AND wt.requestId NOT IN (
    SELECT i.indentId 
    FROM IndentId i 
    WHERE i.tenderRequest IS NOT NULL
  )
""")
  List<WorkflowTransition> findValidTransitions(
          @Param("status") String status,
          @Param("workflowId") int workflowId
  );*/
  /*  @Query("""
SELECT wt
FROM WorkflowTransition wt
WHERE wt.workflowId = :workflowId
  AND wt.workflowTransitionId IN (
        SELECT MAX(wt2.workflowTransitionId)
        FROM WorkflowTransition wt2
        GROUP BY wt2.requestId
  )
  AND wt.status = :status
""")
    List<WorkflowTransition> findValidTransitions(
            @Param("status") String status,
            @Param("workflowId") int workflowId
    );*/
  @Query("""
SELECT wt
FROM WorkflowTransition wt
WHERE wt.workflowId = :workflowId
  AND wt.status = :status
  AND wt.workflowTransitionId = (
        SELECT MAX(wt2.workflowTransitionId)
        FROM WorkflowTransition wt2
        WHERE wt2.requestId = wt.requestId
  )
""")
  List<WorkflowTransition> findValidTransitions(
          @Param("status") String status,
          @Param("workflowId") int workflowId
  );




    // Import proper entity and package

    WorkflowTransition findTopByRequestIdOrderByWorkflowSequenceDesc(String requestId);

    @Query(value = """
    SELECT COALESCE((
        SELECT CASE 
            WHEN status = 'Completed' 
             AND (nextAction IS NULL OR TRIM(nextAction) = '') 
            THEN true 
            ELSE false 
        END
        FROM workflow_transition
        WHERE workflowName = 'PO Workflow' AND requestId = :requestId
        ORDER BY workflowTransitionId DESC
        LIMIT 1
    ), false)
    """, nativeQuery = true)
    Boolean isPoCompleted(@Param("requestId") String requestId);

    @Query("SELECT w FROM WorkflowTransition w " +
            "WHERE w.status NOT IN ('Completed','APPROVED','Rejected') " +
            "AND w.createdDate <= :threshold")
    List<WorkflowTransition> findPendingOlderThan(@Param("threshold") Date threshold);



    Optional<WorkflowTransition> findFirstByRequestIdOrderByWorkflowTransitionIdDesc(String requestId);

    WorkflowTransition findTopByRequestIdOrderByTransitionOrderDescWorkflowTransitionIdDesc(String tenderId);

    // added by abhinav
    @Query("""
    SELECT wt.createdDate
    FROM WorkflowTransition wt
    WHERE wt.requestId = :requestId
    AND wt.workflowName = 'PO Workflow'
    AND wt.workflowTransitionId = (
        SELECT MAX(wt2.workflowTransitionId)
        FROM WorkflowTransition wt2
        WHERE wt2.requestId = :requestId
        AND wt2.workflowName = 'PO Workflow'
    )
    """)
    LocalDateTime findLastCreatedDateByRequestId(@Param("requestId") String requestId);

    @Query("""
SELECT new com.astro.dto.workflow.CompletedIndentsQueueResponse(
    wt.workflowTransitionId, wt.workflowId, wt.workflowName,
    wt.transitionId, wt.requestId, wt.createdBy, wt.modifiedBy,
    wt.status, wt.nextAction, wt.action, wt.remarks,
    wt.transitionOrder, wt.transitionSubOrder, wt.currentRole, wt.nextRole,
    wt.workflowSequence, wt.modificationDate, wt.createdDate,
    ic.indentorName, ic.projectName, ic.totalIntentValue,
    md.budgetCode, md.modeOfProcurement, ic.consignesLocation
)
FROM WorkflowTransition wt
LEFT JOIN IndentCreation ic ON wt.requestId = ic.indentId
LEFT JOIN MaterialDetails md ON md.indentCreation = ic AND md.id = (
    SELECT MIN(md2.id) 
    FROM MaterialDetails md2 
    WHERE md2.indentCreation = ic
)
WHERE wt.workflowId = :workflowId
  AND wt.status = :status
  AND wt.action <> 'Rejected'
  AND ic.employeeId IS NULL
  AND wt.workflowTransitionId = (
        SELECT MAX(wt2.workflowTransitionId)
        FROM WorkflowTransition wt2
        WHERE wt2.requestId = wt.requestId
          AND wt2.status = :status
  )
ORDER BY wt.requestId, wt.createdDate
""")
    List<CompletedIndentsQueueResponse> findCompletedIndents(
            @Param("status") String status,
            @Param("workflowId") int workflowId
    );


    // @Query("SELECT new com.astro.dto.workflow.ProcurementDtos.pendingRecordsDto(w.status, w.requestId, w.workflowName) FROM WorkflowTransition w WHERE w.nextRole = :roleName")
    // List<pendingRecordsDto> findPendingByNextRole(@Param("roleName") String roleName);
    // updated by abhinav to fetch only the latest transition for each requestId where nextRole matches the given roleName and status is not in Completed or Canceled
    @Query("""
    SELECT new com.astro.dto.workflow.ProcurementDtos.pendingRecordsDto(
        w.status,
        w.requestId,
        w.workflowName
    )
    FROM WorkflowTransition w
    WHERE w.nextRole = :roleName
    AND w.workflowTransitionId = (
        SELECT MAX(w2.workflowTransitionId)
        FROM WorkflowTransition w2
        WHERE w2.requestId = w.requestId
    )
    """)
    // end here
    List<pendingRecordsDto> findPendingByNextRole(@Param("roleName") String roleName);

    @Query("SELECT wt.requestId FROM WorkflowTransition wt " +
            "WHERE wt.workflowName = 'SO Workflow' " +
            "AND wt.status = 'Completed' " +
            "AND wt.nextAction IS NULL")
    List<String> findApprovedSoIds();

    @Query("SELECT wt FROM WorkflowTransition wt WHERE wt.branchId IS NOT NULL " +
           "AND wt.approverId IS NOT NULL AND wt.nextAction = 'Pending' " +
           "AND wt.status NOT IN ('Completed', 'Canceled')")
    List<WorkflowTransition> findPendingBranchTransitions();

    // Reporting Officer: find pending transitions assigned to a specific user
    List<WorkflowTransition> findByNextActionAndNextRoleAndAssignedToUserId(
            String pendingType, String roleName, Integer assignedToUserId);

    // Reporting Officer: find pending transitions for a role where either unassigned (null) or assigned to specific user
    @Query("SELECT wt FROM WorkflowTransition wt WHERE wt.nextAction = :pendingType " +
           "AND wt.nextRole = :roleName " +
           "AND (wt.assignedToUserId IS NULL OR wt.assignedToUserId = :userId)")
    List<WorkflowTransition> findPendingByRoleAndOptionalUser(
            @Param("pendingType") String pendingType,
            @Param("roleName") String roleName,
            @Param("userId") Integer userId);

}

package com.astro.service;

import com.astro.dto.workflow.*;
import com.astro.dto.workflow.ProcurementDtos.pendingRecordsDto;
import com.astro.entity.WorkflowTransition; // added by abhinav for auto approval

import java.util.List;

public interface WorkflowService {

    public WorkflowDto workflowByWorkflowName(String workflowName);

    public List<TransitionDto> transitionsByWorkflowId(Integer workflowId);

    public TransitionDto transitionsByWorkflowIdAndOrder(Integer workflowId, Integer order, Integer subOrder);

    public WorkflowTransitionDto initiateWorkflow(String requestId, String workflowName, String createdBy);

    public List<WorkflowTransitionDto> workflowTransitionHistory(String requestId);

    public List<WorkflowTransitionDto> allWorkflowTransition(String roleName);

    public List<WorkflowTransitionDto> allPendingWorkflowTransition(String roleName);

    public List<WorkflowTransitionDto> allPendingWorkflowTransition(String roleName, Integer userId);

    public List<CompletedIndentsQueueResponse> allCompletedWorkflowTransition(String roleName);

    public List<String> allPreviousRoleWorkflowTransition(Integer workflowId, String requestId);

    public TransitionDto nextTransition(Integer workflowId, String workflowName, String currentRole, String requestId);

    public WorkflowTransitionDto performTransitionAction(TransitionActionReqDto transitionActionReqDto);

    public WorkflowTransitionDto submitWorkflow(Integer workflowTransitionId, Integer actionBy, String remarks);

    public List<WorkflowTransitionDto> approvedWorkflowTransition(String updatedBy);

    public List<SubWorkflowTransitionDto> getSubWorkflowTransition(String updatedBy);

    public void approveSubWorkflow(Integer subWorkflowTransitionId);

    public List<ApprovedIndentsDto> getApprovedIndents(Integer userId); // updated by abhinav to return List of ApprovedIndentsDto instead of List of String
    // public List<String> getApprovedTender();

    public List<ApprovedTenderDto> getApprovedTender();

    public ApprovedTenderDto getApprovedTenderId(String tenderId);
    // public List<ApprovedTenderDto> getApprovedTender(String roleName);

    public List<String> getApprovedTenderIdsForPOAndSO();

    public List<String> getApprovedTenderIdsForPO();

    public List<String> getApprovedTenderIdsForSO();

    // public List<ApprovedPoIdsDto> getApprovedPoIds();
    public List<String> getApprovedPoIds();

    public List<QueueResponse> allPendingWorkflowTransitionINQueue(String roleName);

    public List<QueueResponse> allPendingWorkflowTransitionINQueue(String roleName, Integer userId);

    public List<SubWorkflowQueueDto> getSubWorkflowQueue(String updatedBy);

    public List<WorkflowTransitionDto> performAllTransitionAction(List<TransitionActionReqDto> transitionActionReqDto);

    public List<QueueResponse> allCancelledIndents();

    public List<pendingRecordsDto> getPendingRecordsForRole(String roleName);

    public WorkflowTransitionDto performAutoApproval(WorkflowTransition pendingTransition, Integer autoApproveHours);

}

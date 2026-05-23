package com.astro.dto.workflow;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CompletedIndentsQueueResponse {

        private Integer workflowTransitionId;
        private Integer workflowId;
        private String workflowName;
        private Integer transitionId;
        private String requestId;
        private String createdBy;
        private String updatedBy;
        private String status;
        private String nextAction;
        private String action;
        private String remarks;
        private Integer transitionOrder;
        private Integer transitionSubOrder;
        private String currentRole;
        private String nextRole;
        private Integer workflowSequence;
        private Date modificationDate;
        private Date createdDate;
    private String indentorName;
    private String projectName;
    private BigDecimal amount;
    private String budgetName;
    private String modeOfProcurement;
    private String consignee;

        public CompletedIndentsQueueResponse(Integer workflowTransitionId, Integer workflowId, String workflowName,
                                             Integer transitionId, String requestId, String createdBy, String updatedBy,
                                             String status, String nextAction, String action, String remarks,
                                             Integer transitionOrder, Integer transitionSubOrder, String currentRole, String nextRole,
                                             Integer workflowSequence, Date modificationDate, Date createdDate,
                                             String indentorName, String projectName, BigDecimal amount,
                                             String budgetName, String modeOfProcurement, String consignee) {
            this.workflowTransitionId = workflowTransitionId;
            this.workflowId = workflowId;
            this.workflowName = workflowName;
            this.transitionId = transitionId;
            this.requestId = requestId;
            this.createdBy = createdBy;
            this.updatedBy = updatedBy;
            this.status = status;
            this.nextAction = nextAction;
            this.action = action;
            this.remarks = remarks;
            this.transitionOrder = transitionOrder;
            this.transitionSubOrder = transitionSubOrder;
            this.currentRole = currentRole;
            this.nextRole = nextRole;
            this.workflowSequence = workflowSequence;
            this.modificationDate = modificationDate;
            this.createdDate = createdDate;
            this.indentorName = indentorName;
            this.projectName = projectName;
            this.amount = amount;
            this.budgetName = budgetName;
            this.modeOfProcurement = modeOfProcurement;
            this.consignee = consignee;
        }
}

package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "WORKFLOW_TRANSITION")
@Data
@EntityListeners(AuditingEntityListener.class)
public class WorkflowTransition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WORKFLOWTRANSITIONID")
  //  @Column(name = "workflowTransitionId")
    private Integer workflowTransitionId;

    @Column(name = "WORKFLOWID")
  //  @Column(name="workflowId")
    private Integer workflowId;

    @Column(name = "WORKFLOWNAME")
    //@Column(name = "workflowName")
    private String workflowName;

    @Column(name = "TRANSITIONID")
   // @Column(name = "transitionId")
    private Integer transitionId;

    @Column(name = "REQUESTID")
   // @Column(name = "requestId")
    private String requestId;

   @Column(name = "CREATEDBY")
   // @Column(name = "createdBy")
    @CreatedBy
    private String createdBy;

     @Column(name = "MODIFIEDBY")
    //@Column(name = "modifiedBy")
    private String updatedBy;

    @Column(name = "STATUS")
   // @Column(name = "status")
    private String status;

    @Column(name = "NEXTACTION")
    //@Column(name = "nextAction")
    private String nextAction;

    @Column(name = "ACTION")
  //  @Column(name = "action")
    private String action;

    @Column(name = "CURRENTROLE")
   // @Column(name = "currentRole")
    private String currentRole;

    @Column(name = "NEXTROLE")
   // @Column(name = "nextRole")
    private String nextRole;

    @Column(name = "REMARKS")
   // @Column(name = "remarks")
    private String remarks;

    @Column(name = "TRANSITIONORDER")
    //@Column(name = "transitionOrder")
    private Integer transitionOrder;

    @Column(name = "WORKFLOWSEQUENCE")
   // @Column(name = "workflowSequence")
    private Integer workflowSequence;

    @Column(name = "TRANSITIONSUBORDER")
   // @Column(name = "transitionSubOrder")
    private Integer transitionSubOrder;

   @Column(name = "MODIFICATIONDATE")
   // @Column(name = "modificationDate")
    private Date modificationDate;

   @Column(name = "CREATEDDATE")
   // @Column(name = "createdDate")
    private Date createdDate;

    // Branch-based workflow fields
    @Column(name = "BRANCH_ID")
    private Long branchId; // Links to workflow_branch_master

    @Column(name = "APPROVER_ID")
    private Long approverId; // Links to approver_master

    @Column(name = "APPROVAL_LEVEL")
    private Integer approvalLevel; // Current approval level

    @Column(name = "APPROVAL_SEQUENCE")
    private Integer approvalSequence; // Current approval sequence

    // Reporting Officer assignment fields
    @Column(name = "ASSIGNED_TO_USER_ID")
    private Integer assignedToUserId; // Specific user this approval is assigned to

    @Column(name = "ASSIGNED_TO_EMPLOYEE_ID", length = 50)
    private String assignedToEmployeeId; // Employee ID of the assigned user (for reference)
}

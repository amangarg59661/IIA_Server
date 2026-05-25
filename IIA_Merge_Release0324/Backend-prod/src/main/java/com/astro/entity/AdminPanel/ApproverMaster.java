package com.astro.entity.AdminPanel;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "approver_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ApproverMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approver_code", nullable = false, unique = true, length = 50)
    private String approverCode; // Auto-generated: W{workflow_id}-B{branch_id}-{sequence}

    @Column(name = "workflow_id", nullable = false)
    private Integer workflowId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    // Role-based approver
    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    // Approval hierarchy
    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel = 1;

    @Column(name = "approval_sequence", nullable = false)
    private Integer approvalSequence = 1;

    // Approval logic
    @Column(name = "is_parallel_approval")
    private Boolean isParallelApproval = false; // OR logic if true

    @Column(name = "is_mandatory")
    private Boolean isMandatory = true;

    // Status
    @Column(name = "status", length = 50)
    private String status = "Active"; // Active, Inactive

    // Dynamic routing fields for conditional approval
    // // added by abhinav
    @Column(name = "condition_check_type", length = 50)
    private String conditionCheckType; // LIMIT_CHECK, BUDGET_CHECK, DEPARTMENT_BASED, NONE

    @Column(name = "limit_check_config", columnDefinition = "JSON")
    private String limitCheckConfig; // {"checkField": "totalIndentValue", "limitSource": "APPROVAL_LIMIT_MASTER"}

    @Column(name = "skip_if_condition", columnDefinition = "JSON")
    private String skipIfCondition; // {"field": "totalIndentValue", "operator": "LT", "value": 50000}

    @Column(name = "escalate_if_condition", columnDefinition = "JSON")
    private String escalateIfCondition; // {"field": "totalIndentValue", "operator": "GT", "limitField": "approvalLimit"}

    @Column(name = "escalation_approver_id")
    private Long escalationApproverId;

    @Column(name = "auto_approve_hours")
    private Integer autoApproveHours;

    // Audit
    @Column(name = "created_by", length = 100)
    @CreatedBy
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    @LastModifiedBy
    private String updatedBy;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}

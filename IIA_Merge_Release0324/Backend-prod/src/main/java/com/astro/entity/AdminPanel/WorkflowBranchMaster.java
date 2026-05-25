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
@Table(name = "workflow_branch_master",
       uniqueConstraints = @UniqueConstraint(columnNames = {"workflow_id", "branch_code"}))
@Data
@EntityListeners(AuditingEntityListener.class)
public class WorkflowBranchMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "workflow_id", nullable = false)
    private Integer workflowId;

    @Column(name = "branch_code", nullable = false, length = 50)
    private String branchCode;

    @Column(name = "branch_name", nullable = false, length = 200)
    private String branchName;

    @Column(name = "branch_description", columnDefinition = "TEXT")
    private String branchDescription;

    @Column(name = "condition_type", length = 50)
    private String conditionType; // CATEGORY, LOCATION, AMOUNT, CUSTOM

    @Column(name = "condition_config", columnDefinition = "JSON")
    private String conditionConfig; // JSON configuration

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    // Compound condition support
    @Column(name = "condition_logic", length = 10)
    private String conditionLogic = "AND"; // AND, OR for compound conditions

    @Column(name = "requires_budget_check")
    private Boolean requiresBudgetCheck = false;

    @Column(name = "budget_check_config", columnDefinition = "JSON")
    private String budgetCheckConfig; // {"budgetSource": "PROJECT", "checkField": "availableProjectLimit"}

    @Column(name = "created_by", length = 100)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;
}

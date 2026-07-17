package com.astro.entity.AdminPanel;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "approval_limit_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ApprovalLimitMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "limit_id")
    private Long limitId;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    @Column(name = "category", length = 50)
    private String category; // COMPUTER, NON_COMPUTER, PROJECT, ALL

    @Column(name = "department_name", length = 100)
    private String departmentName; // Null means applicable to all departments

    @Column(name = "location", length = 100)
    private String location; // BANGALORE, NON_BANGALORE, ALL

    @Column(name = "min_amount", precision = 15, scale = 2)
    private BigDecimal minAmount = BigDecimal.ZERO;

    @Column(name = "max_amount", precision = 15, scale = 2)
    private BigDecimal maxAmount; // Null means unlimited

    @Column(name = "escalation_role_id")
    private Integer escalationRoleId; // Role to escalate to when limit exceeded

    @Column(name = "escalation_role_name", length = 100)
    private String escalationRoleName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "priority")
    private Integer priority = 0; // For ordering when multiple limits match

    // Audit fields
    @Column(name = "created_by", length = 100)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "workflow_id")
    private Integer workflowId;

    @Column(name = "updated_by", length = 100)
    @LastModifiedBy
    private String updatedBy;

    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    /**
     * Check if the given amount exceeds this limit
     */
    public boolean exceedsLimit(BigDecimal amount) {
        if (amount == null) return false;
        if (maxAmount == null) return false; // No max limit means unlimited
        return amount.compareTo(maxAmount) > 0;
    }

    /**
     * Check if the given amount is within this limit's range
     */
    public boolean isWithinRange(BigDecimal amount) {
        if (amount == null) return false;

        boolean aboveMin = minAmount == null || amount.compareTo(minAmount) >= 0;
        boolean belowMax = maxAmount == null || amount.compareTo(maxAmount) <= 0;

        return aboveMin && belowMax;
    }
}

package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "cycle_count_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CycleCountMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "count_type")
    private String countType; // MANUAL or SWEEP

    @Column(name = "locator_id")
    private Integer locatorId; // location being counted

    // @Column(name = "sweep_custodian_id")
    // private String sweepCustodianId; // storekeeper for write-back on SWEEP counts only; null for MANUAL

    @Column(name = "status")
    private String status; // DRAFT / AWAITING APPROVAL / APPROVED / REJECTED

    @Column(name = "counted_by")
    private Integer countedBy;

    @Column(name = "count_date")
    private LocalDate countDate;

    @Column(name = "total_variance_value")
    private BigDecimal totalVarianceValue;

    @Column(name = "create_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}

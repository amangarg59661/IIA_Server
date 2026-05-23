package com.astro.entity.InventoryModule;

import javax.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "goods_inspection_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class GiMasterEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspection_sub_process_id")
    private Integer inspectionSubProcessId;
    
    @Column(name = "gprn_process_id", nullable = false)
    private String gprnProcessId;
    
    @Column(name = "gprn_sub_process_id", nullable = false)
    private Integer gprnSubProcessId;

    @Column(name = "spo_rejection_reason")
private String spoRejectionReason;

@Column(name = "spo_rejection_count", nullable = false)
private Integer spoRejectionCount = 0;
    
    @Column(name = "installation_date")
    private LocalDate installationDate;
    
    @Column(name = "commissioning_date")
    private LocalDate commissioningDate;

    @Column(name="create_date")
    @CreatedDate
    private LocalDateTime createDate;

    @Column(name="status")
    private String status;

    @Column(name="created_by")
    @CreatedBy
    private String createdBy;

    @Column(name="location_id")
    private String locationId;

    private BigDecimal poAmount;
    private BigDecimal gprnAmount;
}
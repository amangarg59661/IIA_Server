package com.astro.entity.ProcurementModule;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class WorkOrder {
    @Id
    @Column(name = "wo_id")
    private String woId;
    @Column(name = "tender_id")
    private String tenderId;
    @Column(name = "consignes_address")
    private String consignesAddress;
    @Column(name = "billing_address")
    private String billingAddress;
    @Column(name = "job_completion_period")
    private BigDecimal jobCompletionPeriod;
    @Column(name = "if_ld_clause_applicable")
    private Boolean ifLdClauseApplicable;
    @Column(name = "inco_terms")
    private String incoTerms;
    @Column(name = "payment_terms")
    private String paymentTerms;
    @Column(name = "vendor_name")
    private String vendorName;
    @Column(name = "vendor_address")
    private String vendorAddress;
    @Column(name = "applicable_pbg_to_be_submitted")
    private String applicablePBGToBeSubmitted;
    @Column(name = "vendors_account_no")
    private String vendorsAccountNo;
    @Column(name = "vendors_zrsc_code")
    private String vendorsZRSCCode;
    @Column(name = "vendors_account_name")
    private String vendorsAccountName;

  //  @OneToMany(cascade = CascadeType.ALL)
   // @JoinColumn(name = "work_order_id")
    @OneToMany(mappedBy = "workOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkOrderMaterial> materials;
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updatedDate;
}

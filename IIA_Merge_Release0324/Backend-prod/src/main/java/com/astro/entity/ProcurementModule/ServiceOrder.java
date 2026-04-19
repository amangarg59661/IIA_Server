package com.astro.entity.ProcurementModule;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "service_order")
@Data
public class ServiceOrder {

    @Id
    @Column(name = "so_id")
    private String soId;
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
    @Column(name = "vendor_id")
    private String vendorId;
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
    @Column(name = "total_value_of_so")
    private BigDecimal totalValueOfSo;
    @Column(name = "project_name")
    private String projectName;
    @Column(name = "current_status")
    private String currentStatus;

    @Column(name = "start_date_amc")
    private LocalDate startDateAmc;
    @Column(name = "end_date_amc")
    private LocalDate endDateAmc;
  //  @OneToMany(cascade = CascadeType.ALL)
   // @JoinColumn(name = "service_order_id")
    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceOrderMaterial> materials;
    @Column(name = "created_by")
    private Integer createdBy;
    @Column(name = "updated_by")
    private String updatedBy;

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate = LocalDateTime.now();



}

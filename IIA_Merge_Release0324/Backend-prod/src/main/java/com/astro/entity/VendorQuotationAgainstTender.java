package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "Vendor_quotation_against_tender")
public class VendorQuotationAgainstTender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenderId;
    private String vendorId;
 //   private String vendorName;
    private String quotationFileName;
    private String priceBidFileName;
    private String fileType;

    @Column(name = "status")
    private String status;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "version")
    private Integer version;

    @Column(name = "is_latest")
    private Boolean isLatest;

    @Column(name = "created_by")
    private Integer createdBy;
    @Column(name = "acceptance_status")
    private String acceptanceStatus;

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate = LocalDateTime.now();


    @Column(name = "indentor_status")
    private String indentorStatus; // values: "ACCEPTED", "REJECTED", "CHANGE_REQUESTED"

    @Column(name = "indentor_remarks")
    private String indentorRemarks;

    // SPO's decision / response
    @Column(name = "spo_status")
    private String spoStatus; // values: "ACCEPTED", "REJECTED", "CHANGE_REQUESTED_TO_INTENTOR"

    @Column(name = "spo_remarks")
    private String spoRemarks;

    @Column(name = "change_request_to_indentor")
    private Boolean changeRequestToIndentor = false;

   @Column(name = "modified_by")
   private Integer modifiedBy; // who performed this action/version

   @Enumerated(EnumType.STRING)
   @Column(name = "current_role")
   private WorkflowActorRole currentRole;

   @Enumerated(EnumType.STRING)
   @Column(name = "next_role")
   private WorkflowActorRole nextRole;

    @Column(name = "clarification_file_name")
    private String clarificationFileName;

    @Column(name = "vendor_response", columnDefinition = "TEXT")
    private String vendorResponse;

      
    @Column(name = "technical_status", length = 20)
    private String technicalStatus = "PENDING";

    @Column(name = "technical_remarks", length = 1000)
    private String technicalRemarks;

    @Column(name = "technical_evaluated_by")
    private Integer technicalEvaluatedBy;

    @Column(name = "technical_evaluated_date")
    private LocalDateTime technicalEvaluatedDate;

    /**
     * Controls whether the financial (price) bid is visible to the evaluator.
     * For SINGLE_BID: always true after submission.
     * For DOUBLE_BID: true only after technical approval.
     */
    @Column(name = "financial_bid_visible")
    private Boolean financialBidVisible = false;

    @Column(name = "financial_indentor_status", length = 30)
    private String financialIndentorStatus;

    @Column(name = "financial_indentor_remarks", length = 1000)
    private String financialIndentorRemarks;

    @Column(name = "financial_spo_status", length = 30)
    private String financialSpoStatus;

    @Column(name = "financial_spo_remarks", length = 1000)
    private String financialSpoRemarks;

    @Column(name = "registered_vendor_id", length = 100)
    private String registeredVendorId;

    @Column(name = "registered_vendor_name", length = 300)
    private String registeredVendorName;

    /** Rank assigned after financial evaluation (L1, L2, L3...) */
    @Column(name = "`rank`")
    private Integer rank;

   public enum WorkflowActorRole {
      VENDOR,
      INDENTOR,
      PURCHASE_PERSONNEL,
      STORE_PURCHASE_OFFICER
   }
}

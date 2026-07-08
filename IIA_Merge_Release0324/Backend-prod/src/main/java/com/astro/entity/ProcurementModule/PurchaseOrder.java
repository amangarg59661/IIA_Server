package com.astro.entity.ProcurementModule;


import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Date; // added by abhinav
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "purchase_order")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PurchaseOrder {


  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "po_id")
    private String poId;
    @Column(name = "tender_id")
    private String tenderId;
    @Column(name = "indent_id")
    private String indentId;
    @Column(name = "warranty")
    private String warranty;
    @Column(name = "consignes_address")
    private String consignesAddress;
    @Column(name = "parent_po_id")
    private String parentPoId;
    @Column(name = "billing_address")
    private String billingAddress;
    @Column(name = "delivery_period")
    private String deliveryPeriod; // updated by abhinavto string from BigDecimal
    @Column(name = "if_ld_clause_applicable")
    private  Boolean ifLdClauseApplicable;
    @Column(name = "inco_terms")
    private String incoTerms;
    @Column(name = "payment_terms")
    private String paymentTerms;
    @Column(name = "vendor_name")
    private String vendorName;
    @Column(name = "vendor_address")
    private String vendorAddress;
    @Column(name = "applicable_pbg_to_be_submitted")
    private String applicablePbgToBeSubmitted;
    @Column(name = "transporter_and_freight_for_warder_details")
    private String transporterAndFreightForWarderDetails;
    @Column(name = "vendor_id")
    private String vendorId;
    @Column(name = "vendor_account_number")
    private String vendorAccountNumber;
    @Column(name = "vendors_zfsc_code")
    private String vendorsZfscCode;
    @Column(name = "vendor_swift_code")
    private String vendorSwiftCode;
    @Column(name = "vendor_type")
    private String vendorType;
    @Column(name = "vendor_account_name")
    private String vendorAccountName;
    @Column(name = "total_value_of_po")
    private BigDecimal totalValueOfPo;
    @Column(name = "project_name")
    private String projectName;
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
    private String comparativeStatementFileName;
    @Column(name = "gem_contract_file_name")
    private String gemContractUpload;

    @Column(name = "type_of_security")
    private String typeOfSecurity;

    @Column(name = "security_number")
    private String securityNumber;

    @Column(name = "security_date")
    private LocalDate securityDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    private String quotationNumber;
    private LocalDate quotationDate;
    private String additionalTermsAndConditions;
    private BigDecimal buyBackAmount;
    // added by abhinav for workflow and versioning
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_cancelled")
    private Boolean isCancelled = false;

    @Column(name = "current_status")
    private String currentStatus;

    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(name = "locked_date")
    private Date lockedDate;

    @Column(name = "locked_by")
    private Integer lockedBy;

    @Column(name = "po_version")
    private Integer poVersion = 1;
    // added by abhinav end here
   // @OneToMany(cascade = CascadeType.ALL)
   // @JoinColumn(name = "purchase_order_id")
  /* @ManyToMany(cascade = CascadeType.PERSIST)
   @JoinTable(
           name = "purchase_order_attributes_mapping",
           joinColumns = @JoinColumn(name = "po_id"),
           inverseJoinColumns = @JoinColumn(name = "material_code")
   )

   */
    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderAttributes> purchaseOrderAttributes;
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "updated_by")
    @LastModifiedBy
    // private String updatedBy;
    private String updatedBy; //updated by abhinav to Integer to match createdBy type

    @Column(name = "created_date", updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;
}

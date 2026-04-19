package com.astro.entity.ProcurementModule;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class TenderRequest {

    @Id
    @Column(name = "tender_id")
    private String tenderId;

    @Column(name = "tender_number", unique = true)
    private Integer tenderNumber;
    @Column(name = "title_of_tender")
    private String titleOfTender;
    @Column(name = "opening_date")
    private LocalDate openingDate;
    @Column(name = "closing_date")
    private LocalDate closingDate;
    @OneToMany(mappedBy = "tenderRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IndentId> indentIds;
    //  @Column(name = "indent_id")
    //  private String indentId;
    @Column(name = "indent_materials")
    private String indentMaterials;
    @Column(name = "mode_of_procurement")
    private String modeOfProcurement;
    @Column(name = "bid_type")
    private String bidType;
    @Column(name = "last_date_of_submission")
    private LocalDate lastDateOfSubmission;
    @Column(name = "applicable_taxes")
    private String applicableTaxes;
    //@Column(name = "consignes_and_billinng_address")
    // private String consignesAndBillinngAddress;
    @Column(name = "billinng_address")
    private String billinngAddress;
    @Column(name = "consignes")
    private String consignes;
    @Column(name = "inco_terms")
    private String incoTerms;
    @Column(name = "payment_terms")
    private String paymentTerms;
    @Column(name = "ld_clause")
    private Boolean ldClause;
    // @Column(name = "applicable_performance")
    // private String applicablePerformance;
    @Column(name = "performance_and_warranty_security")
    private String performanceAndWarrantySecurity;
    @Column(name = "bid_security_declaration")
    private Boolean bidSecurityDeclaration;
    @Column(name = "mll_status_declaration")
    private Boolean mllStatusDeclaration;
    @Lob
    @Column(name = "upload_tender_documents")
    private byte[] uploadTenderDocuments;
    @Column(name = "single_and_multiple_vendors")
    private String singleAndMultipleVendors;
    @Lob
    @Column(name = "upload_general_terms_and_conditions")
    private byte[] uploadGeneralTermsAndConditions;
    @Column(name = "upload_tender_documents_file_name")
    private String uploadTenderDocumentsFileName;
    @Column(name = "upload_general_terms_and_conditions_file_name")
    private String uploadGeneralTermsAndConditionsFileName;
    @Column(name = "upload_specific_terms_and_conditions_file_name")
    private String uploadSpecificTermsAndConditionsFileName;

    @Column(name = "bid_security_declaration_file_name")
    private String bidSecurityDeclarationFileName;
    @Column(name = "mll_status_declaration_file_name")
    private String mllStatusDeclarationFileName;
    @Column(name = "vendor_id")
    private String vendorId;
    @Column(name = "quotation_file_name")
    private String quotationFileName;
    @Lob
    @Column(name = "upload_specific_terms_and_conditions")
    private byte[] uploadSpecificTermsAndConditions;
    @Column(name = "pre_bid_disscussions")
    private String preBidDisscussions;
    @Column(name = "total_tender_value")
    private BigDecimal totalTenderValue;
    @Column
    private String projectName;
    private String fileType;
    @Column(name = "cancel_status")
    private Boolean cancelStatus = false;

    @Column(name = "cancel_remarks")
    private String cancelRemarks;

    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_by")
    private Integer createdBy;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate = LocalDateTime.now();

    // TC_44: Tender Versioning
    @Column(name = "tender_version")
    private Integer tenderVersion = 1;

    // TC_46: Update Reason
    @Column(name = "update_reason", length = 1000)
    private String updateReason;

    // TC_47: Pre-bid Meeting Recording
    @Column(name = "pre_bid_meeting_status")
    private String preBidMeetingStatus; // "NOT_CONDUCTED", "SCHEDULED", "CONDUCTED"

    @Column(name = "pre_bid_meeting_discussion", length = 5000)
    private String preBidMeetingDiscussion;

    @Column(name = "pre_bid_meeting_date")
    private LocalDate preBidMeetingDate;

    // TC_48: Tender Lock after PO Creation
    @Column(name = "is_locked")
    private Boolean isLocked = false;

    @Column(name = "locked_reason", length = 500)
    private String lockedReason;

    @Column(name = "locked_for_po")
    private String lockedForPO; // PO ID that locked this tender

    @Column(name = "locked_date")
    private LocalDateTime lockedDate;
    @Column(name ="current_status")
    private String currentStatus;

    private Boolean buyBack;
    private String buyBackAmount;
    private String modelNumber;
    private String serialNumber;
    private LocalDate dateOfPurchase;
    private String uploadBuyBackFileNames;

}

package com.astro.entity.ProcurementModule;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
public class IndentCreation {

    @Id
    @Column(name = "indent_id", nullable = false, unique = true)
    private String indentId;

    @Column(name = "indent_number", unique = true)
    private Integer indentNumber;

    @Column(name = "indentor_name")
    private String indentorName;

    @Column(name = "indentor_mobile_no")
    private String indentorMobileNo;

    @Column(name = "indentor_email_address")
    private String indentorEmailAddress;

    @Column(name = "consignes_location")
    private String consignesLocation;

    @Lob
    @Column(name = "uploading_prior_approvals")
    private byte[] uploadingPriorApprovals;

    @Column(name = "project_name")
    private String projectName;

    @Lob
    @Column(name = "upload_tender_documents")
    private byte[] uploadTenderDocuments;

    @Column(name = "is_pre_bit_meeting_required")
    private Boolean isPreBitMeetingRequired;

    @Column(name = "pre_bid_meeting_date", nullable = true)
    private LocalDate preBidMeetingDate;

    @Column(name = "pre_bid_meeting_venue")
    private String preBidMeetingVenue;

    @Column(name = "is_it_a_rate_contract_indent")
    private Boolean isItARateContractIndent;

    @Column(name = "estimated_rate")
    private BigDecimal estimatedRate;

    @Column(name = "period_of_contract")
    private BigDecimal periodOfContract;

    // REMOVED: private String singleAndMultipleJob;
    // NEW: Store multiple job codes as comma-separated string
    @Column(name = "rate_contract_job_codes", length = 2000)
    private String rateContractJobCodes;

    @Lob
    @Column(name = "upload_goi_or_rfp")
    private byte[] uploadGOIOrRFP;

    @Lob
    @Column(name = "upload_pac_or_brand_pac")
    private byte[] uploadPACOrBrandPAC;

    @Column(name = "uploading_prior_approvals_file_name", length = 2000)
    private String uploadingPriorApprovalsFileName;

    @Column(name = "technical_specifications_file_name", length = 2000)
    private String technicalSpecificationsFileName;

    @Column(name = "draft_Eoi_Or_Rfp_file_name", length = 2000)
    private String draftEOIOrRFPFileName;

    @Column(name = "upload_pac_or_brand_pac_file_name", length = 2000)
    private String uploadPACOrBrandPACFileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "total_indent_value")
    private BigDecimal totalIntentValue;

    private String brandAndModel;
    private String justification;
    private Boolean brandPac;

    private String quarter;
    private String purpose;
    private String reason;
    private String proprietaryJustification;
    private Boolean buyBack;
    private String buyBackAmount;
    private String modelNumber;
    private String serialNumber;
    private LocalDate dateOfPurchase;
    @Column(length = 2000)
    private String uploadBuyBackFileNames;
    private String employeeDepartment;

    @Column(name = "proprietary_and_limited_declaration")
    private Boolean proprietaryAndLimitedDeclaration;

    // Material Details (existing)
    @OneToMany(mappedBy = "indentCreation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaterialDetails> materialDetails = new ArrayList<>();

    // Job/Service Details
    @OneToMany(mappedBy = "indentCreation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobDetails> jobDetails = new ArrayList<>();

    // Indent Type - "material" or "job"
    @Column(name = "indent_type")
    private String indentType;

    // Material Category Type - "all", "computer", or "non-computer"
    @Column(name = "material_category_type")
    private String materialCategoryType;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "cancel_status")
    private Boolean cancelStatus = false;

    @Column(name = "cancel_remarks")
    private String cancelRemarks;

    // Bug Fix 1 & 2: Track if indent can be edited and if it's locked after tender creation
    @Column(name = "is_editable")
    private Boolean isEditable = true;

    @Column(name = "is_locked_for_tender")
    private Boolean isLockedForTender = false;

    @Column(name = "locked_reason")
    private String lockedReason;

    // Bug Fix 3: Version tracking for indent
    @Column(name = "version")
    private Integer version = 1;

    @Column(name = "parent_indent_id")
    private String parentIndentId;

    // Bug Fix 4: Enhanced status tracking
    @Column(name = "current_status")
    private String currentStatus = "DRAFT";

    @Column(name = "current_stage")
    private String currentStage = "INDENT_CREATION";

    @Column(name = "approval_level")
    private Integer approvalLevel = 0;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate = LocalDateTime.now();

    // Add this column
@Column(name = "indentor_department")
private String indentorDepartment;
// ADD this field - the only new addition needed
@Column(name = "is_active")
private Boolean isActive = true;

    // Dynamic Workflow Fields - Project classification
    @Column(name = "is_under_project")
    private Boolean isUnderProject = false;

    @Column(name = "project_code", length = 50)
    private String projectCode;

    @Column(name = "workflow_branch_id")
    private Long workflowBranchId;

    @Column(name = "escalated_to_director")
    private Boolean escalatedToDirector = false;

    @Column(name = "escalation_reason", length = 500)
    private String escalationReason;

    // Mode of Procurement - Mandatory field for workflow
    // Values: GEM, OPEN_TENDER, LIMITED_TENDER, SINGLE_TENDER, PROPRIETARY, RATE_CONTRACT, etc.
    @Column(name = "mode_of_procurement", length = 100)
    private String modeOfProcurement;

    // Reporting Officer's determination of project status (set during RO approval)
    @Column(name = "ro_project_determination")
    private Boolean roProjectDetermination;

    @Column(name = "ro_project_determination_remarks", length = 500)
    private String roProjectDeterminationRemarks;

    // Helper methods to convert between List and comma-separated String
    public List<String> getRateContractJobCodesAsList() {
        if (rateContractJobCodes == null || rateContractJobCodes.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(java.util.Arrays.asList(rateContractJobCodes.split(",")));
    }
    
    public void setRateContractJobCodesFromList(List<String> jobCodes) {
        if (jobCodes == null || jobCodes.isEmpty()) {
            this.rateContractJobCodes = null;
        } else {
            this.rateContractJobCodes = String.join(",", jobCodes);
        }
    }
}
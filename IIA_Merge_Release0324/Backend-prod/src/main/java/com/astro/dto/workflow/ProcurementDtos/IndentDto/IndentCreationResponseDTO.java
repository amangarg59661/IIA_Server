package com.astro.dto.workflow.ProcurementDtos.IndentDto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class IndentCreationResponseDTO {

    private String indentorName;
    private String indentId;
    private String indentorMobileNo;
    private String indentorEmailAddress;
    private String consignesLocation;
    private String uploadingPriorApprovalsFileName;
    private String technicalSpecificationsFileName;
    private String draftEOIOrRFPFileName;
    private String uploadPACOrBrandPACFileName;
    private String fileType;
    private Boolean isActive;
    private String projectName;
    private Boolean isPreBidMeetingRequired;
    private String preBidMeetingDate;
    private String preBidMeetingVenue;
    private Boolean isItARateContractIndent;
    private BigDecimal estimatedRate;
    private BigDecimal periodOfContract;
    
    // REMOVED: private String singleAndMultipleJob;
    // NEW: Multiple job codes for rate contract - returns as List
    private List<String> rateContractJobCodes;
    
    private String materialCategory;
    private BigDecimal totalPriceOfAllMaterials;
    private BigDecimal projectLimit;
    
    // Material Details (existing)
    private List<MaterialDetailsResponseDTO> materialDetails;
    
    // Job/Service Details
    private List<JobDetailsResponseDTO> jobDetails;
    
    // Indent Type - "material" or "job"
    private String indentType;
    
    // Material Category Type - "all", "computer", or "non-computer"
    private String materialCategoryType;

    private String brandAndModel;
    private String justification;
    private Boolean brandPac;
    private String quarter;
    private String purpose;
    private String reason;
    private String modelNumber;
    private String serialNumber;
    private String dateOfPurchase;
    private String proprietaryJustification;
    private Boolean proprietaryAndLimitedDeclaration;
    private Boolean buyBack;
    private String buyBackAmount;
    private String uploadBuyBackFileNames;
    private String employeeDepartment;
    private Integer createdBy;
    private String updatedBy;

    private Boolean cancelStatus;
    private String cancelRemarks;
    private List<String> uploadBuyBackFile;
    private String employeeId;
    private String employeeName;
    private String employeeDept;
    // Add this field
    private String indentorDepartment;

    // Project-related fields
    private Boolean isUnderProject;
    private String projectCode;
    private String modeOfProcurement;

    // Bug Fix Fields
    private Boolean isEditable;
    private Boolean isLockedForTender;
    private String lockedReason;
    private Integer version;
    private String parentIndentId;
    private String currentStatus;
    private String currentStage;
    private Integer approvalLevel;

    // New fields for better status display
    private String statusMessage;  // User-friendly status message
    private Integer totalApprovalLevels;  // Total number of approval levels for this indent
    private Boolean isFullyApproved;  // True if all approvals are complete
}
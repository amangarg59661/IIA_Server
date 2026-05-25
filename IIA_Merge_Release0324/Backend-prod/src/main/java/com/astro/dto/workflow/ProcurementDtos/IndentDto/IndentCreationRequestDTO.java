package com.astro.dto.workflow.ProcurementDtos.IndentDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
public class IndentCreationRequestDTO {

    private String indentorName;
    private String indentorMobileNo;
    private String indentorEmailAddress;
    private String consignesLocation;
    private String fileType;
    private List<String> uploadingPriorApprovalsFileName;
    private List<String> technicalSpecificationsFileName;
    private List<String> draftEOIOrRFPFileName;
    private List<String> uploadPACOrBrandPACFileName;

    private String projectName;
    private Boolean isPreBidMeetingRequired;
    private String preBidMeetingDate;
    private String preBidMeetingVenue;
    private Boolean isItARateContractIndent;
    private BigDecimal estimatedRate;
    private BigDecimal periodOfContract;
    
    // REMOVED: private String singleAndMultipleJob;
    // NEW: Multiple job codes for rate contract
    private List<String> rateContractJobCodes;
    
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
    private String employeeDepartment;
    private String serialNumber;
    private String dateOfPurchase;
    private Boolean proprietaryAndLimitedDeclaration;
    private List<String> uploadBuyBackFileNames;
    private List<MaterialDetailsRequestDTO> materialDetails;

    private String updatedBy;
    private String createdBy;

    // Indent type - "material" or "job"
    private String indentType;
    
    // Material category type - "all", "computer", or "non-computer"
    private String materialCategoryType;
    
    // Job details list for job/service indents
    private List<JobDetailsRequestDTO> jobDetails;
    // Add this field
    private String indentorDepartment;

    // Project-related fields for workflow branch matching
    private Boolean isUnderProject;
    private String projectCode;
    private String modeOfProcurement;
}
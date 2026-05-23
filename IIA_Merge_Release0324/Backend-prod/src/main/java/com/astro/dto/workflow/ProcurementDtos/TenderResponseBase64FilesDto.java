package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TenderResponseBase64FilesDto {
    private String tenderId;
    private String titleOfTender;
    private String openingDate;
    private String closingDate;
    // private String indentId;
    private String indentMaterials;
    private String modeOfProcurement;
    private String bidType;
    private String lastDateOfSubmission;
    private String applicableTaxes;
    // private String consignesAndBillinngAddress;
    private String incoTerms;
    private String paymentTerms;
    private Boolean ldClause;
    // private String applicablePerformance;
    private String performanceAndWarrantySecurity;
    private Boolean bidSecurityDeclaration;
    private Boolean mllStatusDeclaration;
    private String singleAndMultipleVendors;
    private List<String> uploadTenderDocuments;
    private List<String> uploadGeneralTermsAndConditions;
    private List<String> uploadSpecificTermsAndConditions;
    private String fileType;
    private List<String> bidSecurityDeclarationFileName;
    private List<String> mllStatusDeclarationFileName;
    private String preBidDisscussions;
    private BigDecimal totalTenderValue;
    private String projectName;
    private BigDecimal projectLimit;
    private String billinngAddress;
    private String consignes;
    private String updatedBy;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<String> indentIds;
    private String uploadTenderDocumentsFileName;
    private String uploadGeneralTermsAndConditionsFileName;
    private String uploadSpecificTermsAndConditionsFileName;
    private String bidSecurityDeclarationFile;
    private String miiStatusDeclarationFileName;
    private String vendorName;
    //private String vendorAddress;
    private String status;
    private String processStage;


}

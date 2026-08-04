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
    private String specialTermsConditions;
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
    private Boolean isActive;
    private Integer tenderVersion;
    private Boolean isLocked;
    private String lockedReason;
    private String evaluationStatus;

public Boolean getIsActive() { return isActive; }
public void setIsActive(Boolean isActive) { this.isActive = isActive; }

public Integer getTenderVersion() { return tenderVersion; }
public void setTenderVersion(Integer tenderVersion) { this.tenderVersion = tenderVersion; }

public Boolean getIsLocked() { return isLocked; }
public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }

public String getLockedReason() { return lockedReason; }
public void setLockedReason(String lockedReason) { this.lockedReason = lockedReason; }

public String getEvaluationStatus() { return evaluationStatus; }
public void setEvaluationStatus(String evaluationStatus) { this.evaluationStatus = evaluationStatus; }



}

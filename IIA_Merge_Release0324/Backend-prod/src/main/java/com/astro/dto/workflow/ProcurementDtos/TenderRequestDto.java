package com.astro.dto.workflow.ProcurementDtos;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.List;


@Data
public class TenderRequestDto {

    //  private String tenderId;
    private String titleOfTender;
    private String openingDate;
    private String closingDate;
    // private String indentId;
    private String indentMaterials;
    private String modeOfProcurement;
    private String bidType;
    private String lastDateOfSubmission;
    private String applicableTaxes;
    //  private String consignesAndBillinngAddress;
    private String incoTerms;
    private String paymentTerms;
    private Boolean ldClause;
    // private String applicablePerformance;
    private String performanceAndWarrantySecurity;
    private Boolean bidSecurityDeclaration;
    private Boolean mllStatusDeclaration;
    private String vendorId;
    private String quotationFileName;
    //private MultipartFile uploadTenderDocuments;
    private String singleAndMultipleVendors;
    private List<String> uploadTenderDocuments;
    private List<String> uploadGeneralTermsAndConditions;
    private String billingAddress;
    private String consignes;
    private String fileType;
  //  private List<String> bidSecurityDownload;
    private List<String> bidSecurityDeclarationFileName;
    private List<String> mllStatusDeclarationFileName;

    //   private MultipartFile uploadGeneralTermsAndConditions;

    private List<String> uploadSpecificTermsAndConditions;
    private String preBidDisscussions;
    private String updatedBy;
    private String createdBy;
    private List<String> indentId; // Updated

    private Boolean buyBack;
    private String buyBackAmount;
    private String modelNumber;
    private String serialNumber;
    private String dateOfPurchase;
    private List<String> uploadBuyBackFileNames;

    // TC_46: Update Reason
    private String updateReason;

    // TC_47: Pre-bid Meeting Recording
    private String preBidMeetingStatus; // "NOT_CONDUCTED", "SCHEDULED", "CONDUCTED"
    private String preBidMeetingDiscussion;
    private String preBidMeetingDate;



}

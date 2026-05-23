package com.astro.dto.workflow.ProcurementDtos;

import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCreationResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.MaterialDetailsResponseDTO;
import io.swagger.models.auth.In;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TenderWithIndentResponseDTO {

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
    //  private String consignesAndBillinngAddress;
    private String incoTerms;
    private String paymentTerms;
    private Boolean ldClause;
    //  private String applicablePerformance;
    private String performanceAndWarrantySecurity;
    private Boolean bidSecurityDeclaration;
    private Boolean mllStatusDeclaration;
    private String uploadTenderDocuments;
    private String bidSecurityDeclarationFileName;
    private String mllStatusDeclarationFileName;
    private String singleAndMultipleVendors;
    private String uploadGeneralTermsAndConditions;
    private String uploadSpecificTermsAndConditions;
    private String preBidDisscussions;
    private String fileType;
    private String billinngAddress;
    private String consignes;
    private String updatedBy;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<IndentCreationResponseDTO> indentResponseDTO;
    private BigDecimal totalTenderValue;
    private String vendorId;
    private String quotationFileName;
    //   private List<IndentIdDto> indentIds;
    private String validityPeriod;
    private Boolean buyBack;
    private String buyBackAmount;
    private String modelNumber;
    private String serialNumber;
    private String dateOfPurchase;
    private String uploadBuyBackFileNames;

    // TC_44: Tender Versioning
    private Integer tenderVersion;

    // TC_46: Update Reason
    private String updateReason;

    // TC_47: Pre-bid Meeting Recording
    private String preBidMeetingStatus;
    private String preBidMeetingDiscussion;
    private String preBidMeetingDate;

    // TC_48: Tender Lock Status
    private Boolean isLocked;
    private String lockedReason;
    private String lockedForPO;
    private LocalDateTime lockedDate;

}

package com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto;

import com.astro.dto.workflow.ProcurementDtos.TenderWithIndentResponseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class soWithTenderAndIndentResponseDTO {
    private String soId;
    private String tenderId;
    private String consignesAddress;
    private String billingAddress;
    private BigDecimal jobCompletionPeriod;
    private Boolean ifLdClauseApplicable;
    private String incoTerms;
    private String paymentTerms;
    private String vendorName;
    private String vendorAddress;
    private String applicablePBGToBeSubmitted;
    private String vendorsAccountNo;
    private String vendorsZRSCCode;
    private String vendorsAccountName;
    private String vendorId;
    private BigDecimal totalValueOfSo;
    private String projectName;
    private BigDecimal projectLimit;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<ServiceOrderMaterialResponseDTO> materials;
    private String createdBy;
    private String updatedBy;
    private TenderWithIndentResponseDTO tenderDetails;


}

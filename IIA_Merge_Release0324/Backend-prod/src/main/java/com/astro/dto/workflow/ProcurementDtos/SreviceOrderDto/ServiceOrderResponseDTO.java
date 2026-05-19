package com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServiceOrderResponseDTO {

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
    private String vendorId;
    private Boolean isActive;
private Integer soVersion;
private String parentSoId;
    private String vendorsAccountNo;
    private String vendorsZRSCCode;
    private String vendorsAccountName;
    private BigDecimal totalValue;
    private String projectName;
    private BigDecimal projectLimit;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<ServiceOrderMaterialResponseDTO> materials;
    private Integer createdBy;
    private String updatedBy;

}

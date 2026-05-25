package com.astro.dto.workflow.ProcurementDtos.WorkOrderDto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WorkOrderResponseDTO{

    private String woId;
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
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private BigDecimal totalValue;
    private String projectName;
    private BigDecimal projectLimit;
    private List<WorkOrderMaterialResponseDTO> materials;
    private String createdBy;
    private String updatedBy;
}

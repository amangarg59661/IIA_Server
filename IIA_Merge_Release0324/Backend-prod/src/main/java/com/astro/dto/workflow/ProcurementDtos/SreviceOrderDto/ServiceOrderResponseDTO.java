package com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServiceOrderResponseDTO {

    private String soId;
    private String tenderId;
    private String indentId;
    private String warranty;
    private String consignesAddress;
    private String billingAddress;
    private BigDecimal jobCompletionPeriod;
    private String deliveryPeriod;
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
    private String vendorSwiftCode;
    private String vendorType;
    private String vendorsAccountName;
    private BigDecimal totalValue;
    private String projectName;
    private BigDecimal projectLimit;
    private String deliveryDate;
    private String comparativeStatementFileName;
    private List<String> gemContractFileName;
    private String quotationNumber;
    private String quotationDate;
    private String additionalTermsAndConditions;
    private BigDecimal buyBackAmount;
    private String transporterAndFreightForWarderDetails;
    private String typeOfSecurity;
    private String securityNumber;
    private String securityDate;
    private String expiryDate;
    private String startDateAmc;
    private String endDateAmc;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<ServiceOrderMaterialResponseDTO> materials;
    private String createdBy;
    private String updatedBy;
    private String currentStatus;

}

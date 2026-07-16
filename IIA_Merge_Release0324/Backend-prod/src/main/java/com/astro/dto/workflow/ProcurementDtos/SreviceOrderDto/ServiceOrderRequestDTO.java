package com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ServiceOrderRequestDTO {

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
    private String vendorsAccountNo;
    private String vendorsZRSCCode;
    private String vendorSwiftCode;
    private String vendorType;
    private String vendorsAccountName;
    private String startDateAmc;
    private String endDateAmc;
    private String projectName;
    private String deliveryDate;
    private String quotationNumber;
    private String quotationDate;
    private String additionalTermsAndConditions;
    private BigDecimal buyBackAmount;
    private String transporterAndFreightForWarderDetails;
    private List<String> comparativeStatementFileName;
    private List<String> gemContractFileName;
    private String typeOfSecurity;
    private String securityNumber;
    private String securityDate;
    private String expiryDate;
    private List<ServiceOrderMaterialRequestDTO> materials;
    private String createdBy;
    private String updatedBy;

}

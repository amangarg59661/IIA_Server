package com.astro.dto.workflow.ProcurementDtos.purchaseOrder;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderResponseDTO {

    private String poId;
    private String tenderId;
    private String indentId;
    private String warranty;
    private String consignesAddress;
    private String billingAddress;
    private String deliveryPeriod; // updated by abhinav to string from BigDecimal
    private Boolean ifLdClauseApplicable;
    private String incoTerms;
    private String paymentTerms;
    private String vendorName;
    private Boolean isActive;
private String parentPoId;
private Integer poVersion;
    private String vendorAddress;
    private String applicablePbgToBeSubmitted;
    private String transporterAndFreightForWarderDetails;
    private String vendorId;
    private String vendorAccountNumber;
    private String vendorsIfscCode;
    private String vendorAccountName;
    private BigDecimal totalValue;
    private String projectName;
    private BigDecimal projectLimit;
    private String deliveryDate;
    private String comparativeStatementFileName;
    private List<String> gemContractFileName;
    private BigDecimal buyBackAmount;
    private List<PurchaseOrderAttributesResponseDTO> purchaseOrderAttributes;
    private Integer createdBy;
    private Integer updatedBy; // updated by abhinav
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

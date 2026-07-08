package com.astro.dto.workflow.ProcurementDtos.purchaseOrder;

import com.astro.dto.workflow.ProcurementDtos.TenderWithIndentResponseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class poWithTenderAndIndentResponseDTO {

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
 private String vendorId;
 private String vendorName;
 private String vendorAddress;
 private String applicablePbgToBeSubmitted;
 private String transporterAndFreightForWarderDetails;
 private String vendorAccountNumber;
 private String vendorsIfscCode;
 private String vendorAccountName;
 private BigDecimal totalValueOfPo;
 private String projectName;
 private BigDecimal projectLimit;
 private List<String> indentIds;
 private String deliveryDate;
 private List<PurchaseOrderAttributesResponseDTO> purchaseOrderAttributes;
 private String createdBy;
//  private String updatedBy;
 private String updatedBy; //updated by abhinav to Integer to match createdBy type
 private String comparativeStatementFileName;
 private LocalDateTime createdDate;
 private LocalDateTime updatedDate;
 private List<String> gemContractFileName;
private String vendorSwiftCode;
private String vendorType;
 private TenderWithIndentResponseDTO tenderDetails;



}

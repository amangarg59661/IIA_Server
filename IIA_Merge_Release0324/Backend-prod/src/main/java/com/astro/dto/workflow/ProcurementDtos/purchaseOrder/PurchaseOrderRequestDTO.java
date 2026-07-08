package com.astro.dto.workflow.ProcurementDtos.purchaseOrder;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PurchaseOrderRequestDTO{

      //  private String poId;
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
        private String vendorAddress;
        private String applicablePbgToBeSubmitted;
        private String transporterAndFreightForWarderDetails;
        private String vendorAccountNumber;
        private String vendorsIfscCode;
        private String vendorSwiftCode;
        private String vendorType;
        private String vendorAccountName;
        private String deliveryDate;
       // private BigDecimal totalValueOfPo;
        private String projectName;
        private String vendorId;
        private List<String> comparativeStatementFileName;
        private List<PurchaseOrderAttributesDTO> purchaseOrderAttributes;
        // private String updatedBy;
        private String updatedBy;  // updated by abhinav
        private String createdBy;
        private List<String> gemContractFileName;

        private String typeOfSecurity;

        private String securityNumber;

        private String securityDate;

        private String expiryDate;
    private BigDecimal buyBackAmount;
        private String quotationNumber;
        private String quotationDate;
        private String additionalTermsAndConditions;



}

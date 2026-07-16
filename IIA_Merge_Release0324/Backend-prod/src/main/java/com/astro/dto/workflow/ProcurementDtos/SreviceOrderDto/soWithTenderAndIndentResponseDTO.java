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
    private String vendorSwiftCode;
    private String vendorType;
    private String indentId;
    private String warranty;
    private String deliveryPeriod;
    private String deliveryDate;
    private String quotationNumber;
    private String quotationDate;
    private String additionalTermsAndConditions;
    private BigDecimal buyBackAmount;
    private String transporterAndFreightForWarderDetails;
    private String comparativeStatementFileName;
    private List<String> gemContractFileName;
    private String typeOfSecurity;
    private String securityNumber;
    private String securityDate;
    private String expiryDate;
    private String startDateAmc;
    private String endDateAmc;
    private Boolean isActive;
    private Integer soVersion;
    private String parentSoId;
    private String currentStatus;
    private TenderWithIndentResponseDTO tenderDetails;
}

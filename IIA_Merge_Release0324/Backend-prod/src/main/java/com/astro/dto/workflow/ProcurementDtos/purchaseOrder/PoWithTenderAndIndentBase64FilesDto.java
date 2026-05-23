package com.astro.dto.workflow.ProcurementDtos.purchaseOrder;

import com.astro.dto.workflow.ProcurementDtos.PoFormateDto;
import com.astro.dto.workflow.ProcurementDtos.TenderWithIndentResponseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PoWithTenderAndIndentBase64FilesDto {

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
        private String updatedBy; //updated by abhinav
        private String comparativeStatementFileName;
        private List<String> comparativeStatementFileNameList;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
        private String status;
        private String processStage;

        private TenderWithIndentResponseDTO tenderDetails;
        private List<String> gemContractFileName;
        private String typeOfSecurity;

        private String securityNumber;

        private String securityDate;

        private String expiryDate;

        private PoFormateDto poFormateData;

        private List<PoFormateApprovalHistory> poHistory;

        private String currentStatus;
private Boolean isActive;
private Integer poVersion;

public String getCurrentStatus() { return currentStatus; }
public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
public Boolean getIsActive() { return isActive; }
public void setIsActive(Boolean isActive) { this.isActive = isActive; }
public Integer getPoVersion() { return poVersion; }
public void setPoVersion(Integer poVersion) { this.poVersion = poVersion; }
        

}

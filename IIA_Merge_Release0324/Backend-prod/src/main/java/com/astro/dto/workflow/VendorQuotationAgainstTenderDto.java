package com.astro.dto.workflow;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class VendorQuotationAgainstTenderDto {

    private String tenderId;
    private String vendorId;
    private String vendorName;
    private String quotationFileName;
    private String priceBidFileName;
    private String fileType;
    private String createdBy;
    private Integer version;
    private String remarks;
    private String status;
    private String indentorStatus;
    private String sopStatus;

    private boolean canIndentorAct;
    private boolean canSpoAct;
    private String clarificationFileName;
    private String vendorResponse;
    private String type;

    private String indentorRemarks;

    private String financialIndentorStatus;
    private String financialSpoStatus;
    private String financialIndentorRemarks;
    private String financialSpoRemarks;

    private String registeredVendorId;
    private String registeredVendorName;

    private String ppDocUploadRemarks;
    private BigDecimal enteredAmount;
    // Boolean (not primitive) so Jackson serializes the getter as "isL1Vendor"
    private Boolean isL1Vendor;

}

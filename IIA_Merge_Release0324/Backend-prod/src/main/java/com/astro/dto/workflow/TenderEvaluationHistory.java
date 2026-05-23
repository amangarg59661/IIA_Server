package com.astro.dto.workflow;

import com.astro.entity.VendorQuotationAgainstTender;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TenderEvaluationHistory {

    private Long id;
    private String tenderId;
    private String vendorId;
    private String quotationFileName;
    private String fileType;

    private String status;
    private String remarks;
    private Integer version;
    private Boolean isLatest;
    private String createdBy;
    private String acceptanceStatus;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private String indentorStatus;
    private String indentorRemarks;
    private String spoStatus;
    private String spoRemarks;
    private Boolean changeRequestToIndentor;

    private String updatedBy;
    private VendorQuotationAgainstTender.WorkflowActorRole currentRole;
    private VendorQuotationAgainstTender.WorkflowActorRole nextRole;
}

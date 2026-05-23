package com.astro.dto.workflow.ProcurementDtos.purchaseOrder;

import lombok.Data;

import java.util.Date;

@Data
public class PoFormateApprovalHistory {

    private String status;
    private String nextAction;
    private String action;
    private String remarks;
    private String createdBy;
    private String createdRole;
    private String updatedBy;
    private Date modificationDate;
    private Date createdDate;
}

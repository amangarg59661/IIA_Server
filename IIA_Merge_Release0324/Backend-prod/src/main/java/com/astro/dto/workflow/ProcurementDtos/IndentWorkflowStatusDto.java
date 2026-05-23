package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

import java.util.Date;

@Data
public class IndentWorkflowStatusDto {

    private String requestId;

    private String createdBy;

    private String updatedBy;

    private String status;

    private String nextAction;

    private String action;

    private String currentRole;

    private String nextRole;

    private String remarks;

    private Date modificationDate;

    private Date createdDate;


}

package com.astro.dto.workflow;

import lombok.Data;
import java.util.Date;

@Data
public class SubWorkflowTransitionDto {

    private Integer subWorkflowTransitionId;
    private Integer workflowId;
    private String workflowName;
    private String requestId;
    private String createdBy;
    private String updatedBy;
    private String status;
    private String action;
    private String remarks;
    private Integer actionOn;
    private Integer workflowSequence;
    private Date modificationDate;
    private Date createdDate;
}

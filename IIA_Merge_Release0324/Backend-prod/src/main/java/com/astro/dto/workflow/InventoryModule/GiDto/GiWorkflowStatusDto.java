package com.astro.dto.workflow.InventoryModule.GiDto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GiWorkflowStatusDto {

    private String processId;
    private Integer subProcessId;
    private String action;
    private String remarks;
    private String createdBy;
    private LocalDateTime createDate;
}

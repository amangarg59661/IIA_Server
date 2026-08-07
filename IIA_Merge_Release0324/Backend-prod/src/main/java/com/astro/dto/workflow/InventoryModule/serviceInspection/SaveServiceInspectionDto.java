package com.astro.dto.workflow.InventoryModule.serviceInspection;

import lombok.Data;

import java.util.List;

@Data
public class SaveServiceInspectionDto {

    private String soId;                 // required — which SO this inspection is against
    private String vendorName;
    private String projectName;
    private String inspectedBy;
    private String remarks;
    private String supportingDocBase64;  // completion certificate / photos
    private String createdBy;            // passed through to workflowService.initiateWorkflow

    private List<ServiceInspectionMaterialLineDto> materials;
}

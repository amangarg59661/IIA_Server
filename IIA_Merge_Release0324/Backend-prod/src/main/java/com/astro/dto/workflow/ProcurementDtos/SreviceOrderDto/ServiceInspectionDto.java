package com.astro.dto.workflow.InventoryModule.serviceInspection;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ServiceInspectionDto {

    private String inspectionProcessId;
    private String soId;
    private Integer subProcessId;
    private String vendorName;
    private String projectName;
    private String currentStatus;
    private String inspectedBy;
    private Date inspectionDate;
    private String remarks;
    private String supportingDocBase64;

    private List<ServiceInspectionMaterialLineDto> materials;
}

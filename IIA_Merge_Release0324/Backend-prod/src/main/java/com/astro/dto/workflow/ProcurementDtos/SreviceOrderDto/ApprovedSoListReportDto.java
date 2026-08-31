package com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto;

import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.PurchaseOrderAttributesResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class ApprovedSoListReportDto {

    private String approvedDate;
    private String soDate;
private String indentorName;
private String nonGemReason;
private String gemOrNonGem;
    private String soId;
    private String vendorName;
    private Double value;
    private String tenderId;
    private String project;
    private String vendorId;
    private String indentIds;
    private String modeOfProcurement;
    private List<ServiceOrderMaterialResponseDTO> materials;


}

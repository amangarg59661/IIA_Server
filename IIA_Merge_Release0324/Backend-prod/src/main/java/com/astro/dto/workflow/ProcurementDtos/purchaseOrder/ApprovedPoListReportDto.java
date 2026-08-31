package com.astro.dto.workflow.ProcurementDtos.purchaseOrder;

import lombok.Data;

import java.util.List;

@Data
public class ApprovedPoListReportDto {
    private String approvedDate;
    private String poId;
    private String vendorName;
    private Double value;
    private String tenderId;
    private String poDate;
private String indentorName;
private String nonGemReason;
private String gemOrNonGem;
    private String project;
    private String vendorId;
    private String indentIds;
    private String modeOfProcurement;
    private List<PurchaseOrderAttributesResponseDTO> purchaseOrderAttributes;


}

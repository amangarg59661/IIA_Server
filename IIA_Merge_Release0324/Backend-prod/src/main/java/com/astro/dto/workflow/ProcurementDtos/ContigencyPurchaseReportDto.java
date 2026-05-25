package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ContigencyPurchaseReportDto {

    private String contigencyId;
    private String vendorName;
    private String projectName;
    private String paymentToVendor;
    private String paymentToEmployee;
    private String purpose;
    private String createdBy;
    private List<CpMaterialRequestDto> cpMaterials;
    private String pendingWith;
    private String pendingFrom;
    private String status;
    private String action;

}

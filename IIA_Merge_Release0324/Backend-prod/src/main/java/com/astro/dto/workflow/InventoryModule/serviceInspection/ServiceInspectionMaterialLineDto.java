package com.astro.dto.workflow.InventoryModule.serviceInspection;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceInspectionMaterialLineDto {

private String jobCode;
private String jobDescription;
    // private String materialCode;
    // private String materialDescription;
    private BigDecimal orderedQty;
    private BigDecimal acceptedQty;
    private BigDecimal rejectedQty;
    private BigDecimal rate;
    private BigDecimal gst;
    private BigDecimal duties;
    private String remarks;
}

package com.astro.dto.workflow.ProcurementDtos.purchaseOrder;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderAttributesResponseDTO {

    private String materialCode;
    private String materialDescription;
    private BigDecimal quantity;
    private BigDecimal rate;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal gst;
    private BigDecimal duties;
    private BigDecimal freightCharge;
    private BigDecimal gstDuties;
    private BigDecimal gstFreight;
    private String budgetCode;
    private String uom;
    private BigDecimal unitPrice;
    private String category;
    private BigDecimal receivedQuantity;
    private BigDecimal totalQuantity;
    private String modeOfProcurement;
    private String materialDescriptionQuotation;
    private String projectName;
}

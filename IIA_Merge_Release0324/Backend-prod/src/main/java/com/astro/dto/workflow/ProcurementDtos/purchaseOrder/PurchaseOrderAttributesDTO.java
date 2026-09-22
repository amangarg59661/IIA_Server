package com.astro.dto.workflow.ProcurementDtos.purchaseOrder;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderAttributesDTO {

    private String materialCode;
    private String materialDescription;
    private BigDecimal quantity;
    private BigDecimal rate;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal gst;
    private BigDecimal duties;
    private BigDecimal gstDuties;
    private BigDecimal gstFreight;
    private BigDecimal freightCharge;
    private String budgetCode;
    private String materialDescriptionQuotation;
    private String projectName;

}

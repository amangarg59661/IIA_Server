package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class paymentVoucherDto {
    private String processId;
    private String vendorName;
    private String vendorInvoiceName;
    private String vendorInvoiceDate;
    private List<paymentVoucherMaterials> materialsList;
    private List<paymentVoucherJobs> jobsList;
    private BigDecimal totalAmount;
    private String paymentVoucherType;
    private BigDecimal partialAmountAlreadypaid;
    private BigDecimal partialBalanceAmount;
    private BigDecimal advanceAmountAlreadyPaid;
    private BigDecimal advanceBalanceAmount;




}

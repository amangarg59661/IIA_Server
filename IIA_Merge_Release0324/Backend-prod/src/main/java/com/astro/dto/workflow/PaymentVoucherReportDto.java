package com.astro.dto.workflow;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaymentVoucherReportDto {
    private String paymentVoucherNumber;
    private String paymentVoucherDate;
    private String paymentVoucherIsFor;
    private String purchaseOrderId;
    private String grnNumber;
  //  private String serviceOrderDetails;
    private String paymentVoucherType;
    private String vendorName;
    private String vendorInvoiceNumber;
    private String vendorInvoiceDate;
    private String currency;
    private String exchangeRate;
    private String remarks;
    private BigDecimal totalAmount;
    private BigDecimal partialAmount;
    private BigDecimal advanceAmount;
    private BigDecimal paidAmount;
    private String soId;
    private String cpDetails;
    private String createdBy;
    private LocalDateTime createdDate;
    private List<PaymentVoucherMaterialDto> materials;
}

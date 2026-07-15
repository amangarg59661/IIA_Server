package com.astro.dto.workflow;

import com.astro.entity.PaymentVoucherMaterials;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class paymentVoucherRequestDto {
   // private String paymentVoucherNumber;
    private String paymentVoucherDate;
    private String paymentVoucherIsFor;
    private String purchaseOrderId;
    private String grnNumber;
    private String serviceOrderDetails;
    private String paymentVoucherType;
    private String vendorName;
    private String vendorInvoiceNumber;
    private String vendorInvoiceDate;
    private String currency;
    private String exchangeRate;
    private String status;
    private String remarks;
    private BigDecimal totalAmount;
   private BigDecimal partialAmount;
   private BigDecimal advanceAmount;

   private BigDecimal tdsAmount;
   private BigDecimal paymentVoucherNetAmount;


   private String cpDetails;
   private String createdBy;

    private List<paymentVoucherMaterialRequestDto> materials;

}

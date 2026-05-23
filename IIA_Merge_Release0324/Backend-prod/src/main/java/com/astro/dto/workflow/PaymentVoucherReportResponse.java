package com.astro.dto.workflow;

import lombok.Data;

import java.util.List;

@Data
public class PaymentVoucherReportResponse {

    private ResponseStatus responseStatus;
    private List<PaymentVoucherData> responseData;

    @Data
    public static class ResponseStatus {
        private Integer statusCode;
        private String message;
        private String errorCode;
        private String errorType;
    }

    @Data
    public static class PaymentVoucherData {
        private String paymentVoucherNumber;
        private String paymentVoucherDate;
        private String paymentVoucherIsFor;
        private String purchaseOrderId;
        private String grnNumber;
        private String paymentVoucherType;
        private String vendorName;
        private String vendorInvoiceNumber;
        private String vendorInvoiceDate;
        private String currency;
        private String exchangeRate;
        private String remarks;
        private Double totalAmount;
        private Double partialAmount;
        private Double advanceAmount;
        private Double paidAmount;
        private String soId;
        private String createdBy;
        private String createdDate;
        private List<MaterialData> materials;
    }

    @Data
    public static class MaterialData {
        private String materialCode;
        private String materialDescription;
        private Double quantity;
        private Double unitPrice;
        private String currency;
        private Double exchangeRate;
        private Double gst;
    }
}

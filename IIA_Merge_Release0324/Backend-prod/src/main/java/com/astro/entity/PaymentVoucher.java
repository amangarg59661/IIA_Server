package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "payment_voucher")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PaymentVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_voucher_number")
    private String paymentVoucherNumber;

    @Column(name = "payment_voucher_date")
    private String paymentVoucherDate;

    @Column(name = "payment_voucher_is_for")
    private String paymentVoucherIsFor;

    @Column(name = "purchase_order_id")
    private String purchaseOrderId;

    @Column(name = "grn_number")
    private String grnNumber;

    @Column(name = "service_order_details")
    private String serviceOrderDetails;

    @Column(name = "payment_voucher_type", nullable = false)
    private String paymentVoucherType;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "vendor_invoice_number", nullable = false)
    private String vendorInvoiceNumber;

    @Column(name = "vendor_invoice_date")
    private String vendorInvoiceDate;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "exchange_rate")
    private String exchangeRate;

    @Column(name = "status")
    private String status;

    @Column(name = "remarks", length = 500)
    private String remarks;

    private BigDecimal totalAmount;
    private BigDecimal partialAmount;
    private BigDecimal advanceAmount;
    private BigDecimal paidAmount;
    private BigDecimal tdsAmount;
    private BigDecimal paymentVoucherNetAmount;

    private String soId;
    private String cpDetails;

    @CreatedBy
    private String createdBy;
    @CreatedDate
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "paymentVoucher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentVoucherMaterials> materialsList;

}

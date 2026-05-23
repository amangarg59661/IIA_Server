package com.astro.entity.ProcurementModule;

import com.astro.entity.ProcurementModule.PurchaseOrder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class PurchaseOrderAttributes {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "material_code")
    private String materialCode;
   // @Column(name = "po_id")
   // private String poId;
    @Column(name = "material_description")
    private String materialDescription;
    @Column(name = "quantity")
    private BigDecimal quantity;
    @Column(name = "rate")
    private BigDecimal rate;
    @Column(name = "currency")
    private String currency;
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;
    @Column(name = "gst")
    private BigDecimal gst;
    @Column(name = "duties")
    private BigDecimal duties;
    @Column(name = "freight_charge")
    private BigDecimal freightCharge;
    @Column(name = "budget_code")
    private String budgetCode;
    @Column(name = "received_quantity")
    private BigDecimal receivedQuantity;
    private BigDecimal totalPoMaterialPriceInInr;
   // @ManyToOne
   // @JoinColumn(name = "purchase_order_id", referencedColumnName = "po_id")
   // private PurchaseOrder purchaseOrder;
   @ManyToOne
   @JoinColumn(name = "po_id", referencedColumnName = "po_id")
   private PurchaseOrder purchaseOrder;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}

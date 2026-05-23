package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "ohq_consumable_store_stock_entity")
@Data
@EntityListeners(AuditingEntityListener.class)
public class OhqConsumableStoreStockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ohq_id")
    private Long ohqId;

    @Column(name = "material_code")
    private String materialCode;

    @Column(name = "locator_id")
    private Integer locatorId;

    @Column(name = "book_value")
    private BigDecimal bookValue;

    @Column(name = "depriciation_rate")
    private BigDecimal depriciationRate;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name="custodian_id")
    private String custodianId;

    @Column(name = "quantity")
    private BigDecimal quantity;

    private String uom;

    @Column(name = "create_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createDate;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}

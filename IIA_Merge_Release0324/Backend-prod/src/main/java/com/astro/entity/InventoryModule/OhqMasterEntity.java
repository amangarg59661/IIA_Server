package com.astro.entity.InventoryModule;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "ohq_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class OhqMasterEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ohq_id")
    private Integer ohqId;
    
    @Column(name = "asset_id", nullable = false)
    private Integer assetId;
    
    @Column(name = "locator_id", nullable = false)
    private Integer locatorId;
    
    @Column(name = "book_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal bookValue;
    
    @Column(name = "depriciation_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal depriciationRate;
    
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @Column(name="custodian_id")
    private String custodianId;

    private String assetCode;

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
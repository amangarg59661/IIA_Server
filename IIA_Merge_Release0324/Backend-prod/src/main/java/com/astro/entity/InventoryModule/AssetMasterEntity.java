package com.astro.entity.InventoryModule;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "asset_master")
@Data
public class AssetMasterEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer assetId;
    @Column(name = "asset_code", unique = true)
    private String assetCode;
    private String materialCode;
    private String materialDesc;
    private String assetDesc;
    private String makeNo;
    private String serialNo;
    private String modelNo;
    private String uomId;
    private String componentName;
    private Integer componentId;
    private BigDecimal initQuantity;
    private String poId;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "depriciation_rate")
    private BigDecimal depriciationRate;

    @Column(name = "end_of_life")
    private LocalDate endOfLife;

    @Column(name = "stock_levels")
    private BigDecimal stockLevels;

    @Column(name = "condition_of_goods")
    private String conditionOfGoods;

    @Column(name = "shelf_life")
    private String shelfLife;

    @Column(name = "locator_id")
    private Integer locatorId;

    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;
    
    private Integer createdBy;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name="igp_id")
    private Long igpId;
    
    @Column(name="grn_no")
    private String grnNumber;
    
    private Integer updatedBy;
}
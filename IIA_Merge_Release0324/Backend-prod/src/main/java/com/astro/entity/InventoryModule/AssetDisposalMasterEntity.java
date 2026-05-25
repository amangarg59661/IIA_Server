package com.astro.entity.InventoryModule;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "asset_disposal")
@Data
@EntityListeners(AuditingEntityListener.class)
public class AssetDisposalMasterEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disposal_id")
    private Integer disposalId;
    
    @Column(name = "disposal_date", nullable = false)
    private LocalDate disposalDate;

    private String custodianId;
    
    @Column(name = "created_by", nullable = false)
    @CreatedBy
    private String createdBy;
    
    @Column(name = "create_date", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createDate;
    
    @Column(name = "location_id", nullable = false)
    private String locationId;

    private String status;
    private String action;

    private String auctionId;
    private LocalDate auctionDate;
    private BigDecimal reservePrice;
    private BigDecimal auctionPrice;
    private String vendorName;


}
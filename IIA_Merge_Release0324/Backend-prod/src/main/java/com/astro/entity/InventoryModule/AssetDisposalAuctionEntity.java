package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "asset_disposal_auction")
@Data
@EntityListeners(AuditingEntityListener.class)
public class AssetDisposalAuctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_id")
    private Integer auctionId;

    @Column(name = "auction_code", nullable = false, unique = true)
    private String auctionCode;

    @Column(name = "auction_date", nullable = false)
    private LocalDate auctionDate;

    @Column(name = "reserve_price")
    private BigDecimal reservePrice;

    @Column(name = "auction_price")
    private BigDecimal auctionPrice;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "created_by", nullable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    private List<AssetDisposalAuctionDetailEntity> auctionDetails;
}

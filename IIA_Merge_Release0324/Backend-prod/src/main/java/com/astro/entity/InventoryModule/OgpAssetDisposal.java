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
@Table(name = "ogp_asset_disposal")
@Data
@EntityListeners(AuditingEntityListener.class)
public class OgpAssetDisposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disposal_ogp_id")
    private Integer disposalOgpId;

    @Column(name = "auction_id")
    private Integer auctionId;

    @Column(name = "auction_code")
    private String auctionCode;

    @Column(name = "auction_date")
    private LocalDate auctionDate;

    @Column(name = "reserve_price")
    private BigDecimal reservePrice;

    @Column(name = "auction_price")
    private BigDecimal auctionPrice;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "status")
    private String status;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "create_date")
    @CreatedDate
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "disposal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OgpAssetDisposalDetail> assets;
}

package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset_disposal_auction_detail")
@Data
@EntityListeners(AuditingEntityListener.class)
public class AssetDisposalAuctionDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auction_detail_id")
    private Integer auctionDetailId;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private AssetDisposalAuctionEntity auction;

    @Column(name = "disposal_id", nullable = false)
    private Integer disposalId;

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

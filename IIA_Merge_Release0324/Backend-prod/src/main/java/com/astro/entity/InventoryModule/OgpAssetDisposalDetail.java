package com.astro.entity.InventoryModule;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "ogp_asset_disposal_detail")
@Data
@EntityListeners(AuditingEntityListener.class)
public class OgpAssetDisposalDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ogp_disposal_detail_id")
    private Integer ogpDisposalDetailId;

    @ManyToOne
    @JoinColumn(name = "disposal_ogp_id", nullable = false)
    private OgpAssetDisposal disposal;

    @Column(name = "disposal_id")
    private Integer disposalId;

    @Column(name = "asset_id")
    private Integer assetId;
    @Column(name = "asset_code")
    private String assetCode;

    @Column(name = "asset_desc")
    private String assetDesc;

    @Column(name = "disposal_quantity", precision = 10, scale = 2)
    private BigDecimal disposalQuantity;

    @Column(name = "locator_id")
    private Integer locatorId;

    @Column(name = "book_value")
    private BigDecimal bookValue;

    @Column(name = "depriciation_rate")
    private BigDecimal depriciationRate;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;
    @Column(name = "serial_no")
    private String serialNo;

    @Column(name = "custodian_id")
    private String custodianId;

    @Column(name = "po_value")
    private BigDecimal poValue;

    @Column(name = "reason_for_disposal")
    private String reasonForDisposal;

    @Column(name = "disposal_date")
    private LocalDate disposalDate;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "status")
    private String status;

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

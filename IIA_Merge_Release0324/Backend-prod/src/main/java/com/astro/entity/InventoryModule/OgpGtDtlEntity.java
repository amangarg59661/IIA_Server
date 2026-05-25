package com.astro.entity.InventoryModule;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EntityListeners;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "ogp_gt_dtl")
@EntityListeners(AuditingEntityListener.class)
public class OgpGtDtlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="gt_id")
    private Long gtId;

    @Column(name = "asset_id")
    private Integer assetId;

    private String assetCode;

    @Column(name = "asset_desc", length = 500)
    private String assetDesc;

    @Column(name = "material_code", length = 100)
    private String materialCode;

    @Column(name = "material_desc", length = 500)
    private String materialDesc;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "receiver_locator_id")
    private Integer receiverLocatorId;

    @Column(name = "sender_locator_id")
    private Integer senderLocatorId;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    private String serialNo;

    @Column(name = "depriciation_rate")
    private BigDecimal depriciationRate;

    @Column(name = "book_value")
    private BigDecimal bookValue;

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
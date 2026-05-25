package com.astro.entity.InventoryModule;


import java.math.BigDecimal;

import javax.persistence.*;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "ogp_detail_rejected_gi")
@EntityListeners(AuditingEntityListener.class)
public class OgpDetailRejectedGiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    @Column(name = "ogp_sub_process_id", nullable = false)
    private Integer ogpSubprocessId;

    @Column(name = "material_code", length = 255)
    private String materialCode;

    @Column(name = "material_desc", length = 255)
    private String materialDesc;

    @Column(name = "asset_id")
    private Integer assetId;

    @Column(name = "asset_code")
    private String assetCode;

    @Column(name = "asset_desc", length = 255)
    private String assetDesc;

    @Column(name = "rejection_type", length = 50)
    private String rejectionType;

    @Column(name = "rejected_quantity", precision = 10, scale = 2, nullable = false)
    private BigDecimal rejectedQuantity;

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
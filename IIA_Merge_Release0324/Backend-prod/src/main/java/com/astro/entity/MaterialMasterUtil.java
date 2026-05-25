package com.astro.entity;

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
@Table(name = "material_master_util")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MaterialMasterUtil {

    @Id
    @Column(name = "material_code")
    private String materialCode;

    @Column(name = "material_number", unique = true)
    private Integer materialNumber;

    @Column(name = "category")
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "description")
    private String description;

    @Column(name = "uom")
    private String uom;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "currency")
    private String currency;

    @Column(name = "estimated_price_with_ccy")
    private BigDecimal estimatedPriceWithCcy;

    @Column(name = "upload_image_name")
    private String uploadImageName;

    @Column(name = "indigenous_or_imported")
    private Boolean indigenousOrImported;

    private String briefDescription;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    private String comments;

    public enum ApprovalStatus {

        APPROVED,
        REJECTED,
        AWAITING_APPROVAL,
        CHANGE_REQUEST

    }


    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;
    // Added by Aman
      @Column(name = "Asset_Flag")
    private Boolean assetFlag;
    // End

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updatedDate;



}

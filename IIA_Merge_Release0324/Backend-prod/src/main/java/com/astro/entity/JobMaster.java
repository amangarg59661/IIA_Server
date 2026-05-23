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
@Table(name = "job_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class JobMaster {

    @Id
    @Column(name = "job_code")
    private String jobCode;

    @Column(name = "category")
    private String category;

    private String subCategory;

    @Column(name = "job_description")
    private String jobDescription;

    @Column(name = "asset_id")
    private String assetId;

    @Column(name = "uom")
    private String uom;

    @Column(name = "value")
    private BigDecimal value;
    @Column(name = "currency")
    private String currency;

    @Column(name = "estimated_price_with_ccy")
    private BigDecimal estimatedPriceWithCcy;

    private String briefDescription;

   // @Column(name = "mode_of_procurement")
  //  private String modeOfProcurement;

   // @OneToMany(mappedBy = "jobCode", cascade = CascadeType.ALL, orphanRemoval = true)
   // private List<VendorNamesForJobWorkMaterial> vendorNames;

    @Column(name = "approval_status")
    private String approvalStatus = "AWAITING_APPROVAL";

    @Column(name = "comments")
    private String comments;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @Column(name= "origin")
    private String origin;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updatedDate;


}

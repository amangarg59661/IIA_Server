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
@Table(name = "asset_disposal_detail")
@Data
@EntityListeners(AuditingEntityListener.class)
public class AssetDisposalDetailEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disposal_detail_id")
    private Integer disposalDetailId;
    
    @Column(name = "disposal_id", nullable = false)
    private Integer disposalId;
    
    @Column(name = "asset_id", nullable = false)
    private Integer assetId;

    @Column(name = "asset_code", nullable = false)
    private String assetCode;
    
    @Column(name = "asset_desc", nullable = false)
    private String assetDesc;
    
    @Column(name = "disposal_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal disposalQuantity;
    
    @Column(name = "disposal_category", nullable = false)
    private String disposalCategory;
    
    @Column(name = "disposal_mode", nullable = false)
    private String disposalMode;
    
    @Column(name = "sales_note_filename")
    private String salesNoteFilename;



    private Integer ohqId;
 //   private Integer assetId;
  //  private String aseetDescription;
    private Integer locatorId;
    private BigDecimal bookValue;
    private BigDecimal depriciationRate;
    private BigDecimal unitPrice;
  //  private BigDecimal quantity;
    private String custodianId;
    private BigDecimal poValue;

    private String reasonForDisposal;


    private String poId;


    private LocalDate poDate;


    private String serialNo;

    private String modelNo;

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
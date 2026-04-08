package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "material_master")
@Data
public class MaterialMaster {

    @Id
    @Column(name = "material_code")
    private String materialCode;

    @Column(name = "category")
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "description")
    private String description;

    @Column(name = "uom")
    private String uom;
    @Column(name = "unit_price", precision = 19, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "currency")
    private String currency;

    @Column(name = "estimated_price_with_ccy")
    private BigDecimal estimatedPriceWithCcy;

    private String briefDescription;

  //  @Column(name = "mode_of_procurement")
  //  private String modeOfProcurement;

 //  @OneToMany(mappedBy = "jobCode", cascade = CascadeType.ALL, orphanRemoval = true)
  // private List<VendorNamesForJobWorkMaterial> vendorNames;

   /* @Column(name = "end_of_life")
    private String endOfLife;


    @Column(name = "depreciation_rate")
    private BigDecimal depreciationRate;

    @Column(name = "stock_levels")
    private BigDecimal stockLevels;



    @Column(name = "condition_of_goods")
    private String conditionOfGoods;

    @Column(name = "shelf_life")
    private String shelfLife;

    */

    @Lob
    @Column(name = "upload_image")
    private byte[] uploadImage;
    @Column(name = "upload_image_name")
    private String uploadImageName;

    @Column(name = "indigenous_or_imported")
    private Boolean indigenousOrImported;

    @Column(name = "status")
    private String status;

    private String remarks;

    @Column(name = "created_by")
    private Integer createdBy;
    @Column(name = "updated_by")
    private String updatedBy;

    private String statusOfMaterialActiveOrDeactive;

    private String reasonForDeactive;
     // Added by Aman
      @Column(name = "Asset_Flag")
    private Boolean assetFlag;
    // End   

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate = LocalDateTime.now();


}

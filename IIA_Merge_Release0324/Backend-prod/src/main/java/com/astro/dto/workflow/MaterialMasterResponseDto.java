package com.astro.dto.workflow;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MaterialMasterResponseDto {

    private String materialCode;
    private String category;
    private String subCategory;
    private String description;
    private String uom;
 //   private String modeOfProcurement;
 //   private String endOfLife;
 //   private BigDecimal depreciationRate;
 //   private BigDecimal stockLevels;
  //  private String conditionOfGoods;
 //   private String shelfLife;
    private String uploadImageFileName;
    private Boolean indigenousOrImported;
    private BigDecimal estimatedPriceWithCcy;
    private String briefDescription;
  //  private List<String> vendorNames;
    private BigDecimal unitPrice;
    private String currency;
    private Integer createdBy;
    private String updatedBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private List<String> materialFile;

    private String status;
    private String materialStatus;
    private String reasonForDeactive;
    // Added by Aman 
    private Boolean asset_Flag;
    // End

}

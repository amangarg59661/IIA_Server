package com.astro.dto.workflow;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MaterialMasterUtilRequestDto {

    private String category;
    private String subCategory;
    private String description;
    private String uom;
    private BigDecimal estimatedPriceWithCcy;
    private List<String> uploadImageFileName;
   // private String uploadImageFileName;
    private Boolean indigenousOrImported;
    private BigDecimal unitPrice;
    private String currency;
    private String briefDescription;
    private Integer createdBy;
    private String updatedBy;
      // Added by Aman 
    private Boolean asset_Flag;
    // End
}

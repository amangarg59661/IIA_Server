package com.astro.dto.workflow.InventoryModule.gprn;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GprnPendingInspectionDetailDto {
    private Integer detailId;
    private String materialCode;
    private String materialDesc;
    private String uomId;
    private BigDecimal receivedQuantity;
    private BigDecimal unitPrice;
    private String makeNo;
    private String serialNo;
    private String modelNo;
    private String warrantyTerms;
    private String note;
    private String photoPath;
    private Boolean assetFlag;
}
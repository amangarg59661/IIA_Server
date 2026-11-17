package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AssetSerialUpdateRequestDto {

    private Integer assetId;
    private String assetCode;
    private String poId;
    private String custodianId;
    private Integer locatorId;
    private String materialCode;
    private String materialDesc;
    private String assetDesc;
    private String makeNo;
    private String modelNo;
    private String uomId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private List<String> serialNumbers;private Boolean labelPrinted;
}

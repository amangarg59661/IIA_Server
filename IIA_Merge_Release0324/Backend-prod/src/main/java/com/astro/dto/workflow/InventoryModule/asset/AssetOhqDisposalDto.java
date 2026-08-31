package com.astro.dto.workflow.InventoryModule.asset;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AssetOhqDisposalDto {
    private Integer ohqId;
    private Integer assetId;
    private String aseetDescription;
    private Integer locatorId;
    private BigDecimal bookValue;
    private BigDecimal depriciationRate;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private String custodianId;
    private BigDecimal poValue;
    private String poId;
    private String gprnDate;
    private String serialNo;
    private String modelNo;
    private String assetCode;
    private String uom;

    private List<String> serialNumbers;

}

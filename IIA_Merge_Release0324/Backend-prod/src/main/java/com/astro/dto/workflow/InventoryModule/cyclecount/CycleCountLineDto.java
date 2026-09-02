package com.astro.dto.workflow.InventoryModule.cyclecount;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CycleCountLineDto {
    private Long dtlId;
    private String materialCode;
    private String materialDesc;
    private String uom;
    private Integer locatorId;
    // private String custodianId;
    private BigDecimal systemQtySnapshot;
    private BigDecimal unitPriceSnapshot;
    private BigDecimal countedQty;
    private BigDecimal varianceQty;
    private BigDecimal varianceValue;
    private Boolean multipleCustodianRows;
    private String remarks;
}

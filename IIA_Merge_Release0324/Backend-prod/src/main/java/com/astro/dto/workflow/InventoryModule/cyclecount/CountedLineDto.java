package com.astro.dto.workflow.InventoryModule.cyclecount;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CountedLineDto {
    private Long dtlId;
    private BigDecimal countedQty;
    private String remarks;
}

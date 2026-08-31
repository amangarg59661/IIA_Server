package com.astro.dto.workflow.InventoryModule.cyclecount;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PendingCycleCountDto {
    private String cycleCountId;
    private String countType;
    private Integer locatorId;
    private String status;
    private LocalDate countDate;
    private BigDecimal totalVarianceValue;
}

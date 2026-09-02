package com.astro.dto.workflow.InventoryModule.cyclecount;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class CycleCountDto {
    private String cycleCountId;
    private String countType;
    private Integer locatorId;
    // private String sweepCustodianId;
    private String status;
    private Integer countedBy;
    private LocalDate countDate;
    private BigDecimal totalVarianceValue;
    private List<CycleCountLineDto> lines;
}

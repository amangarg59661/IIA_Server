package com.astro.dto.workflow.InventoryModule.cyclecount;

import lombok.Data;

import java.util.List;

@Data
public class InitiateCycleCountDto {
    private String countType;        // MANUAL or SWEEP
    private Integer locatorId;
    // private String sweepCustodianId; // required for SWEEP, ignored for MANUAL
    private List<ManualCycleCountItemDto> manualItems; // required for MANUAL
}

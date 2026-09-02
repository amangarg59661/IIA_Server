package com.astro.dto.workflow.InventoryModule.cyclecount;

import lombok.Data;

@Data
public class ManualCycleCountItemDto {
    private String materialCode;
    private String materialDesc; // client-supplied, same convention as GtDtl / DemandAndIssueDtlEntity -- not looked up server-side
    private Integer locatorId;
    // private String custodianId;
}

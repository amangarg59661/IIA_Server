package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MaterialDto {
    private String materialCode;
    private String materialDesc;
    private BigDecimal orderQty;
    private BigDecimal receivedQty;
    private BigDecimal pendingQty;

}

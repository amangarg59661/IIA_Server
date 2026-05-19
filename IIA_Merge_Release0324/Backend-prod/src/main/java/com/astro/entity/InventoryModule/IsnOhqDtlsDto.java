package com.astro.entity.InventoryModule;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class IsnOhqDtlsDto {
    private Integer locatorId;
    private BigDecimal quantity;
    private Integer CustodianId;
    private BigDecimal bookValue;
}

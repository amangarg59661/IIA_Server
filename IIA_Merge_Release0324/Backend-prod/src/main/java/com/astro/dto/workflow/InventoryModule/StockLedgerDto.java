package com.astro.dto.workflow.InventoryModule;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StockLedgerDto {
    private String stockId;
    private String itemDescription;
    private String category;
    private String subCategory;

    private BigDecimal openingStock;
    private BigDecimal openingValue;

    private BigDecimal quantityReceived;
    private BigDecimal quantityIssued;

    private BigDecimal currentStock;
    private BigDecimal currentValue;

    private BigDecimal closingStock;
    private BigDecimal closingValue;

    private String uom;
    private String location;
    private LocalDateTime lastUpdatedDate;
}
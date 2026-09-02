package com.astro.dto.workflow.InventoryModule.cyclecount;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CycleCountReportDto {
    private LocalDate verificationDate;
    private String itemDescription;
    private String category;
    private String subCategory;
    private String location;
    private BigDecimal quantityAsPerRecords;
    private BigDecimal quantityFound;
    private BigDecimal discrepancyQty;
    private BigDecimal discrepancyValue;
    private Integer verifiedBy; // raw ID -- name resolution pending your answer above
    private Boolean verified;   // true only when status == APPROVED
    private String remarks;
}
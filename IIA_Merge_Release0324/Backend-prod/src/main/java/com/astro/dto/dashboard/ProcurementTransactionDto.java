package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One row of GET /api/dashboard/procurementTransactions. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcurementTransactionDto {
    private String poNumber;
    private LocalDateTime dateTime;
    private String item;
    private String vendor;
    private BigDecimal amount;
    private String status;
}

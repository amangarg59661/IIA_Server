package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response for GET /api/dashboard/spoSummary (SPO / P&I Officer dashboard).
 *
 * supplierPerformancePercent is always null today — confirmed against
 * VendorMasterServiceImpl / VendorMasterUtilServiceImpl that no rating,
 * score, or on-time-delivery field exists anywhere in the vendor module.
 * Leave it null until a real metric is built rather than fabricating one;
 * the frontend already renders "Not available" for a null value here.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpoSummaryDto {
    private BigDecimal totalSpendYtd;
    private Double spendYoyChangePercent;
    private Long activePos;
    private BigDecimal inventoryValue;
    private Double supplierPerformancePercent;
}

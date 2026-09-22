package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response for GET /api/dashboard/stockSummary (Store Person dashboard).
 * Backed by OhqMasterConsumableEntity / OhqMasterEntity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockSummaryDto {
    private Long totalStockItems;
    private List<CategoryQuantity> stockLevelsByCategory;
    private List<StockStatusCount> stockStatus;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryQuantity {
        private String category;
        private BigDecimal quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockStatusCount {
        private String status;
        private Long count;
    }
}

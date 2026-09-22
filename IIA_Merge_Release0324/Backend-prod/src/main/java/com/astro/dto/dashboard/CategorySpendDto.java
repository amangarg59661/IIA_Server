package com.astro.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** One row of GET /api/dashboard/spoSpendByCategory. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySpendDto {
    private String category;
    private BigDecimal amount;
}

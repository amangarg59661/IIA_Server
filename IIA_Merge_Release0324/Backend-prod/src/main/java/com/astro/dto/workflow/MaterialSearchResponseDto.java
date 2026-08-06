package com.astro.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialSearchResponseDto {
    private String materialCode;
    private String description;
    private String category;
    private String subCategory;
    private String uom;
    private BigDecimal unitPrice;
    private String currency;
    private Boolean indigenousOrImported;
}

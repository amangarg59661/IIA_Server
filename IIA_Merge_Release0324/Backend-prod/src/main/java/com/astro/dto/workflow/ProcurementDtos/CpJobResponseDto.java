package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CpJobResponseDto {
    private String jobCode;
    private String jobDescription;
    private BigDecimal quantity;
    private BigDecimal estimatedPrice;
    private String uom;
    private BigDecimal totalPrice;
    private String budgetCode;
    private String jobCategory;
    private String jobSubCategory;
    private String currency;
    private BigDecimal gst;
    private String countryOfOrigin;

}
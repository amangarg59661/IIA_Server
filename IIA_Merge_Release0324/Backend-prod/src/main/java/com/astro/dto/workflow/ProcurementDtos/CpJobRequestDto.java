package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CpJobRequestDto {

    private String jobCode;
    private String jobDescription;
    private BigDecimal quantity;
    private BigDecimal estimatedPrice;
    private String uom;
    private String budgetCode;
    private BigDecimal gst;
    private String jobCategory;
    private String jobSubCategory;
    private String currency;
    private String countryOfOrigin;
    private BigDecimal totalPrice;

}
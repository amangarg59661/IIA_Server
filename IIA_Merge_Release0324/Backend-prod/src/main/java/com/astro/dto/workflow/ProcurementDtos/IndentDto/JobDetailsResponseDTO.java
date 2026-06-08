package com.astro.dto.workflow.ProcurementDtos.IndentDto;

import lombok.Data;
import java.util.List;

import java.math.BigDecimal;

@Data
public class JobDetailsResponseDTO {

    private String jobCode;
    private String jobDescription;
    private String category;
    private String subCategory;
    private String uom;
    private BigDecimal quantity;
    private BigDecimal estimatedPrice;
    private BigDecimal totalPrice;
    private String currency;
    private String briefDescription;
    private String origin;
    private BigDecimal conversionRate;
    private String modeOfProcurement;
    private String budgetCode;
    private List<String> vendorNames;
}
package com.astro.dto.workflow.ProcurementDtos.IndentDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDetailsRequestDTO {

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
    private String modeOfProcurement;
    private String budgetCode;
    // private String vendorNames;
    private List<String> vendorNames;
}

package com.astro.dto.workflow;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class paymentVoucherDeductionRequestDto {
    private String deductionName;
    private BigDecimal deductionAmount;
    private String remarks;
}
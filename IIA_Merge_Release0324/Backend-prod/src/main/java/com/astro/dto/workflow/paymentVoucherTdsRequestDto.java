package com.astro.dto.workflow;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class paymentVoucherTdsRequestDto {
    private String tdsSection;
    private BigDecimal tdsAmount;
    private String remarks;
}
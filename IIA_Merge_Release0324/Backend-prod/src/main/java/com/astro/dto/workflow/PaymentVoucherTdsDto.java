package com.astro.dto.workflow;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentVoucherTdsDto {
    private String tdsSection;
    private BigDecimal tdsAmount;
    private String remarks;
}
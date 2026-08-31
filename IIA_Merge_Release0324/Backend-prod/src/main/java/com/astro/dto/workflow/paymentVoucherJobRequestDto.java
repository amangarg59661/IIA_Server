// paymentVoucherJobRequestDto.java
package com.astro.dto.workflow;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class paymentVoucherJobRequestDto {
    private String jobCode;
    private String jobDescription;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal gst;
}
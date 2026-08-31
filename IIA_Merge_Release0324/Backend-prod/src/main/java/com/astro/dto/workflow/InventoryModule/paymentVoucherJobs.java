// paymentVoucherJobs.java
package com.astro.dto.workflow.InventoryModule;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class paymentVoucherJobs {
    private String jobCode;
    private String jobDescription;
    private BigDecimal quantity;
    private String uom;
    private BigDecimal unitPrice;
    private String currency;
    private BigDecimal exchangeRate;
    private BigDecimal gst;
    private BigDecimal amount;
}
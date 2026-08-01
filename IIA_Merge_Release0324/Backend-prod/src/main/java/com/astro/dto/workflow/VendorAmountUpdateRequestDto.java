package com.astro.dto.workflow;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VendorAmountUpdateRequestDto {

    private String tenderId;
    private List<VendorAmountEntry> vendorAmounts;

    @Data
    public static class VendorAmountEntry {
        private String vendorId;
        private BigDecimal enteredAmount;
    }
}
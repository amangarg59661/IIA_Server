package com.astro.dto.workflow;

import lombok.Data;
import java.util.List;

@Data
public class BulkVendorQuotationRequest {
    private String tenderId;
    private List<VendorQuotationAgainstTenderDto> quotations;
}

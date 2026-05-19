
package com.astro.dto.workflow;

import lombok.Data;

@Data
public class VendorTechnicalDecisionDto {
    private String vendorId;
    // APPROVED or REJECTED
    private String decision;
    private String remarks;
    private Integer evaluatedByUserId;
}
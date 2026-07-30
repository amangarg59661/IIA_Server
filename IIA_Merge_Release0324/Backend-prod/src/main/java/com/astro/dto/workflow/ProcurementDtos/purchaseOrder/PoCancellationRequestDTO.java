package com.astro.dto.workflow.ProcurementDtos.purchaseOrder;

import lombok.Data;

@Data
public class PoCancellationRequestDTO {
    private String cancelledBy;
    private String cancellationReason;
}
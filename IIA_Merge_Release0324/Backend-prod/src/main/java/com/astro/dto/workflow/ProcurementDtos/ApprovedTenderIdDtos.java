package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

@Data
public class ApprovedTenderIdDtos {
    private String tenderId;
    private String title;
    private String bidType;

    public ApprovedTenderIdDtos(String tenderId, String title , String bidType) {
        this.tenderId = tenderId;
        this.title = title;
        this.bidType = bidType;
        
    }
}

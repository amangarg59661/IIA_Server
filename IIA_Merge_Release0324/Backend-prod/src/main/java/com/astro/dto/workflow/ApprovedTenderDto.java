package com.astro.dto.workflow;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApprovedTenderDto {

    private String tenderId;
    private String bidType;
    private BigDecimal totalValue;
    private Integer indentNumber;
    private String modeOfProcurement;

    // public ApprovedTenderDto(String tenderId, String bidType, BigDecimal totalValue, Integer indentNumber) {
      public ApprovedTenderDto(String tenderId, String bidType, BigDecimal totalValue,
                             Integer indentNumber, String modeOfProcurement) {
        this.tenderId = tenderId;
        this.bidType = bidType;
        this.totalValue = totalValue;
        // this.indentNumber=indentNumber;
        this.indentNumber = indentNumber;
        this.modeOfProcurement = modeOfProcurement;
    }


}

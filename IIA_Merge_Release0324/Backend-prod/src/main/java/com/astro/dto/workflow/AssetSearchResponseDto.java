package com.astro.dto.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AssetSearchResponseDto {
    private String assetCode;
    private Integer assetId;
    private String poId;
    private String custodianId;
    private Integer locatorId;
    private BigDecimal quantity;
    private String grnNumber;
    private String assetDesc;
    private String materialDesc;
}

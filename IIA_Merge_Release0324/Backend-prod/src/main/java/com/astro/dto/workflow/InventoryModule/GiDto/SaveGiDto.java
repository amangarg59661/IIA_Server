package com.astro.dto.workflow.InventoryModule.GiDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveGiDto {
    private String inspectionNo;
    private String gprnNo;
    private String installationDate;
    private String commissioningDate;
    private List<GiMaterialDtlDto> materialDtlList;
    private String locationId;
    private Integer createdBy;
    private BigDecimal gprnAmount;
    private BigDecimal poAmount;
    private String role;
}
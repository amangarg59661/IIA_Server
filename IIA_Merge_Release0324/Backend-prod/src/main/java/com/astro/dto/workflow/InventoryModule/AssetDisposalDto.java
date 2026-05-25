package com.astro.dto.workflow.InventoryModule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

import javax.persistence.Column;

@Data
public class AssetDisposalDto {
    private Integer disposalId;
    private String disposalDate;
    private String createdBy;
    private String locationId;
    private String custodianId;
    private String custodianName;
    private String action;
    private String status;
    private String auctionId;
    private String auctionDate;
    private BigDecimal reservePrice;
    private BigDecimal auctionPrice;
    private String vendorName;



    private List<AssetDisposalDetailDto> materialDtlList;



}
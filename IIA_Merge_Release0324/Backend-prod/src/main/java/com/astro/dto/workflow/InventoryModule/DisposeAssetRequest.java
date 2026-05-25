package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
public class DisposeAssetRequest {

    private List<Integer> disposalIds;
    private String auctionCode;
    private String auctionDate;
    private BigDecimal reservePrice;
    private BigDecimal auctionPrice;
    private String vendorName;
    private String updatedBy;
    private String locationId;
}

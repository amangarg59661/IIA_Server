package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class AssetsAuctionDto {
    private Integer disposalOgpId;
    private String auctionId;
    private String auctionCode;

    private String auctionDate;

    private BigDecimal reservePrice;

    private BigDecimal auctionPrice;

    private String vendorName;

    private String createdBy;

    private List<AutionAssetsDisposalsDto> assets;
}

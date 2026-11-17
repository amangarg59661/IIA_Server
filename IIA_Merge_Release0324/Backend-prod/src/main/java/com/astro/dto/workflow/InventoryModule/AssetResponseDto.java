package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssetResponseDto {



    private String assetCode;


    private String materialCode;


    private String description;


    private String uom;
private Boolean labelPrinted;
    private String makeNo;
    private String modelNo;
    private String serialNo;
    private String componentName;
    private String componentCode;
    private int quantity;


    private String locator;

    private String transactionHistory;


    private String currentCondition;

    private String updatedBy;
    private String createdBy;


    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;


}

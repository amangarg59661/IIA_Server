package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

@Data
public class AssetRequestDTO {


    private String assetCode;
    private String materialCode;
    private String description;
    private String uom;
    private String makeNo;
    private String modelNo;
    private String serialNo;
    private String componentName;
    private String componentCode;
    private int quantity;
    private String locator;
    private String transactionHistory;
    private String currentCondition;
private Boolean labelPrinted;
    private String updatedBy;
    private String createdBy;
}

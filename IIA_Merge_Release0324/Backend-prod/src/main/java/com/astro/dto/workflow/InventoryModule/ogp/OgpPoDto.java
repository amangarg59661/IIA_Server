package com.astro.dto.workflow.InventoryModule.ogp;

import java.util.List;

import lombok.Data;

@Data
public class OgpPoDto {
    private String poId;
    private String ogpDate;
    private String locationId;
    private String createdBy;
    private String ogpType;
    private String receiverName;
    private String senderName;
    private String receiverLocation;
    private String dateOfReturn;
    private List<OgpPoDtlDto> materialDtlList;
}

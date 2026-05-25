package com.astro.dto.workflow.InventoryModule.GoodsTransfer;

import java.util.List;

import lombok.Data;

@Data
public class GtMasterDto {
    private String id;
    private String gtId;
    private String senderLocationId;
    private String receiverLocationId;
    private Integer receiverCustodianId;
    private Integer senderCustodianId;
    private String senderCustodianName;
    private String receiverCustodianName;

    private String gtDate;
    private String status;
    private String createdBy;
    private List<GtDtl> materialDtlList;
}

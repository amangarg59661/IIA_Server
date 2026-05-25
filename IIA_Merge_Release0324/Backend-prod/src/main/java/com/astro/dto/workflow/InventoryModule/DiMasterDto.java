package com.astro.dto.workflow.InventoryModule;

import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtDtl;
import lombok.Data;

import java.util.List;

@Data
public class DiMasterDto {
    private String id;
    private String diId;
    private String senderLocationId;
    private String receiverLocationId;
    private Integer receiverCustodianId;
    private Integer senderCustodianId;
    private String senderCustodianName;
    private String receiverCustodianName;

    private String diDate;
    private String status;
    private String createdBy;
    private List<GtDtl> materialDtlList;
}

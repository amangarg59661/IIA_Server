package com.astro.dto.workflow.InventoryModule.ogp;

import lombok.Data;
import java.util.List;

@Data
public class OgpDto {
    private String ogpDate;
    private String issueNoteId;
    private String locationId;
    private String createdBy;
    private String senderName;
    private String receiverName;
    private String receiverLocation;
    private String dateOfReturn;
    private String ogpId;
    private String ogpType;

    private List<OgpMaterialDtlDto> materialDtlList;
}
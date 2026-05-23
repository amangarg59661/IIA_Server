package com.astro.dto.workflow.InventoryModule.isn;

import java.util.List;

import com.astro.entity.InventoryModule.IsnMasterEntity.IssueNoteType;

import lombok.Data;

@Data
public class IsnDto {
    private String issueNoteNo;
    private String issueDate;
    private String consigneeDetail;
    private String indentorName;
    private String fieldStation;
    private String createdBy;
    private String locationId;
    private String senderName;
    private List<IsnMaterialDtlDto> materialDtlList;
    private IssueNoteType issueNoteType;
}
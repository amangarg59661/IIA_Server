package com.astro.dto.workflow.InventoryModule.GprnDto;



import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaveGprnDto {
    private String processId;
    private String poId;
    private String date;
    private String challanNo;
    private String deliveryDate;
    private String vendorId;
    private String vendorEmail;
    private String vendorName;
    private String vendorContact;
    private String fieldStation;
    private String indentorName;
    private String supplyExpectedDate;
    private String consigneeDetail;
    private BigDecimal warrantyYears;
    private String warranty;
    private String project;
    private String receivedBy;
    private String receivedName;
    private String createdBy;
    private String locationId;
    private String indentId;
    private List<MaterialDtlDto> materialDtlList;
    private BigDecimal gprnAmount;
    private BigDecimal poAmount;
    private String giStatus;
    private String spoRejectionReason;
    private Integer spoRejectionCount;
    private String rejectionRemarks;
}

package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TenderEvaluationResponseWithBitTypeAndValueDto {

    private String tenderId;
    private String uploadQualifiedVendorsFileName;
    private String uploadTechnicallyQualifiedVendorsFileName;
    private String uploadCommeriallyQualifiedVendorsFileName;
    private String formationOfTechnoCommerialComitee;
    private String responseFileName;
    private String responseForTechnicallyQualifiedVendorsFileName;
    private String responseForCommeriallyQualifiedVendorsFileName;
    private String uploadQualifiedVendorsFileNameCreatedBy;
    private String uploadTechnicallyQualifiedVendorsFileNameCreatedBy;
    private String uploadCommeriallyQualifiedVendorsFileNameCreatedBy;
    private String formationOfTechnoCommerialComiteeCreatedBy;
    private String responseFileNameCreatedBy;
    private String responseForTechnicallyQualifiedVendorsFileNameCreatedBy;
    private String responseForCommeriallyQualifiedVendorsFileNameCreatedBy;
    private String fileType;
    private String bidType;
    private BigDecimal totalValueOfTender;
    private String updatedBy;
    private String createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;



}

package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

@Data
public class TenderEvaluationRequestDto {

    private String tenderId;
    private String uploadQualifiedVendorsFileName;
    private String uploadTechnicallyQualifiedVendorsFileName;
    private String uploadCommeriallyQualifiedVendorsFileName;
    private String formationOfTechnoCommerialComitee;
    private String responseFileName;
    private String responseForTechnicallyQualifiedVendorsFileName;
    private String responseForCommeriallyQualifiedVendorsFileName;
    private String fileType;
    private String updatedBy;
    private String createdBy;
    private String uploadQualifiedVendorsFileNameCreatedBy;
    private String uploadTechnicallyQualifiedVendorsFileNameCreatedBy;
    private String uploadCommeriallyQualifiedVendorsFileNameCreatedBy;
    private String formationOfTechnoCommerialComiteeCreatedBy;
    private String responseFileNameCreatedBy;
    private String responseForTechnicallyQualifiedVendorsFileNameCreatedBy;
    private String responseForCommeriallyQualifiedVendorsFileNameCreatedBy;

}

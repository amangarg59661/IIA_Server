package com.astro.dto.workflow.ProcurementDtos;

import lombok.Data;

import javax.persistence.Lob;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContigencyPurchaseResponseDto {

    private String contigencyId;

    private String vendorName;
    private String vendorInvoiceNo;
    private String Date;
    private String remarksForPurchase;
    private String uploadCopyOfInvoice;
    private String fileType;
    private String predifinedPurchaseStatement;
    private String projectDetail;
    private String projectName;
    private String paymentTo;
    private String paymentToVendor;
    private String paymentToEmployee;
    private String purpose;
    private Boolean declarationOne;
    private Boolean declarationTwo;
    private String updatedBy;
    private String createdBy;
    private String status;
    private String processStage;
    private LocalDateTime createdDate;
    private BigDecimal totalCpValue;

    private LocalDateTime updatedDate;
    private List<CpMaterialResponseDto> cpMaterials;


}

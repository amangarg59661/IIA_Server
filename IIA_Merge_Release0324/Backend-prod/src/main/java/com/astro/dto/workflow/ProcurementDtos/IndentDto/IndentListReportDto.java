package com.astro.dto.workflow.ProcurementDtos.IndentDto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class IndentListReportDto {

    private String indentId;
    private String indentorName;
    private String indentorMobileNo;
    private String indentorEmailAddress;
    private String consignesLocation;
    private String projectName;
    private String submittedDate;
    private String pendingWith;
    private String pendingFrom;
    private String status;
    private LocalDate asOnDate;
    private String createdBy;
    private BigDecimal indentValue;
    private List<IndentMaterialListReportDto> materialDetails;

}

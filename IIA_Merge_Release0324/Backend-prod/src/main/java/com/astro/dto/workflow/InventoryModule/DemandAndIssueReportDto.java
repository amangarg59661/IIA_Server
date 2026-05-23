package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class DemandAndIssueReportDto {
    private Long id;
    private String senderLocationId;
    private String status;

    private Integer senderCustodianId;

    private LocalDateTime createDate;

    private Date demandIssueDate;
    private String createdBy;

    private Date issueDate;
    private Integer issuedBy;

    private List<DemandMaterialsDto> materialDtos;
}

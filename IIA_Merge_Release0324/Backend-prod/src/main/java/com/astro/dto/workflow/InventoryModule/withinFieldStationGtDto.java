package com.astro.dto.workflow.InventoryModule;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class withinFieldStationGtDto {
    private Long gtId;
    private String senderLocationId;
    private String receiverLocationId;
    private Integer senderCustodianId;
    private Integer receiverCustodianId;
    private String status;
    private LocalDate gtDate;
    private LocalDateTime createDate;
    private String createdBy;
    private String type;
    private List<GtReportDtlDto> materialDetails;
}

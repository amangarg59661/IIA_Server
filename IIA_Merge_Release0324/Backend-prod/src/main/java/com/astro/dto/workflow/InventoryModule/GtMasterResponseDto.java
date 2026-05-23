package com.astro.dto.workflow.InventoryModule;

import io.swagger.models.auth.In;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GtMasterResponseDto {

    private String gtId;
    private String senderLocationId;
    private String receiverLocationId;
    private Integer senderCustodianId;
    private Integer receiverCustodianId;
    private String gtDate;
    private String status;
    private String createdBy;
    private String createDate;

    private List<GtDtlDto> materialDtlList;
}

package com.astro.dto.workflow.InventoryModule.cyclecount;

import lombok.Data;

import java.util.List;

@Data
public class SubmitCycleCountDto {
    private String cycleCountId;
    private String countedBy; // String, parsed to Integer on save -- matches DiMasterDto.createdBy convention
    private List<CountedLineDto> lines;
}

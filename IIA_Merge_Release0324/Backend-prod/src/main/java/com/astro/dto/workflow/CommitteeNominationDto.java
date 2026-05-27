package com.astro.dto.workflow;

import lombok.Data;

@Data
public class CommitteeNominationDto {
    private String tenderId;
    private Integer userId;
    private Integer nominatedBy;
}

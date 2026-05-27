package com.astro.dto.workflow;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommitteeVendorVoteDto {
    private Integer committeeUserId;
    private String memberName;
    private String decision;
    private String remarks;
    private LocalDateTime decisionDate;
}

package com.astro.dto.workflow;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class DirectorApprovalRequestDto {

    @NotBlank(message = "decision is required")
    @Pattern(regexp = "APPROVED|REJECTED", message = "decision must be APPROVED or REJECTED")
    private String decision;

    private String remarks;

    @NotNull(message = "directorUserId is required")
    private Integer directorUserId;

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getDirectorUserId() { return directorUserId; }
    public void setDirectorUserId(Integer directorUserId) { this.directorUserId = directorUserId; }
}

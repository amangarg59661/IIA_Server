package com.astro.dto.workflow;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class VendorDecisionRequestDto {

    @NotBlank(message = "decision is required")
    @Pattern(regexp = "ACCEPTED|REJECTED", message = "decision must be ACCEPTED or REJECTED")
    private String decision;

    private String remarks;

    @NotNull(message = "userId is required")
    private Integer userId;

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getRemarks() { return remarks != null ? remarks : ""; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}

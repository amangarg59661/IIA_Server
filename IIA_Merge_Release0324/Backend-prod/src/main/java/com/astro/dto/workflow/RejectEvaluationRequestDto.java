package com.astro.dto.workflow;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RejectEvaluationRequestDto {

    @NotBlank(message = "rejectedByRole is required")
    private String rejectedByRole;

    @NotNull(message = "userId is required")
    private Integer userId;

    @NotBlank(message = "remarks are required for rejection")
    private String remarks;

    public String getRejectedByRole() { return rejectedByRole; }
    public void setRejectedByRole(String rejectedByRole) { this.rejectedByRole = rejectedByRole; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}

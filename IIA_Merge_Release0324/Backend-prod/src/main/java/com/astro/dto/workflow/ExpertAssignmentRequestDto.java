package com.astro.dto.workflow;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ExpertAssignmentRequestDto {

    @NotNull(message = "expertUserId is required")
    private Integer expertUserId;

    @NotBlank(message = "expertName is required")
    private String expertName;

    @NotNull(message = "chairmanUserId is required")
    private Integer chairmanUserId;

    public Integer getExpertUserId() { return expertUserId; }
    public void setExpertUserId(Integer expertUserId) { this.expertUserId = expertUserId; }

    public String getExpertName() { return expertName; }
    public void setExpertName(String expertName) { this.expertName = expertName; }

    public Integer getChairmanUserId() { return chairmanUserId; }
    public void setChairmanUserId(Integer chairmanUserId) { this.chairmanUserId = chairmanUserId; }
}

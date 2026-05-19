package com.astro.dto.workflow;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class VendorSelectionRequestDto {

    @NotBlank(message = "vendorId is required")
    private String vendorId;

    private String remarks;

    @NotNull(message = "actionByUserId is required")
    private Integer actionByUserId;

    public String getVendorId() { return vendorId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Integer getActionByUserId() { return actionByUserId; }
    public void setActionByUserId(Integer actionByUserId) { this.actionByUserId = actionByUserId; }
}

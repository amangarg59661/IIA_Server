package com.astro.dto.workflow.InventoryModule;

public class EligibleSoDto {
    private String soId;
    private String vendorName;
    private String status; // "Not Started" | "Partial"

    public String getSoId() { return soId; }
    public void setSoId(String soId) { this.soId = soId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
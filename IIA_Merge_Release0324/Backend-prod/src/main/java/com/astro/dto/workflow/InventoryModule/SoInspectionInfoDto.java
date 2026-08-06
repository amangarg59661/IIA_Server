package com.astro.dto.workflow.InventoryModule;

public class SoInspectionInfoDto {
    private String soId;
    private String vendorName;
    private String projectName;

    public SoInspectionInfoDto() {}
    public SoInspectionInfoDto(String soId, String vendorName, String projectName) {
        this.soId = soId;
        this.vendorName = vendorName;
        this.projectName = projectName;
    }

    public String getSoId() { return soId; }
    public void setSoId(String soId) { this.soId = soId; }
    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }
    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }
}
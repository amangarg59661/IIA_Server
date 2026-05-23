package com.astro.dto.workflow;

import lombok.Data;

@Data
public class VendorStatusDto {

    private String vendorId;
    private String status;
    private String comments;
    private Boolean emailStatus;
    private String token;
    private Boolean isFirstLogin;
    private Boolean isTempPassword;
    private Boolean emailSent;
    private String createdDate;
}

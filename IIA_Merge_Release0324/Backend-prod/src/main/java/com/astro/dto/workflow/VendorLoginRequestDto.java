package com.astro.dto.workflow;

import lombok.Data;

@Data
public class VendorLoginRequestDto {
    private String vendorId;
    private String password;
}

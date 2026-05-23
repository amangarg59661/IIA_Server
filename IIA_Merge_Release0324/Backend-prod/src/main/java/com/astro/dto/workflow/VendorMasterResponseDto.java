package com.astro.dto.workflow;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VendorMasterResponseDto {

    private String vendorId;
    private String vendorType;
    private String vendorName;
    private String contactNo;
    private String emailAddress;
    private Boolean registeredPlatform;
    private String pfmsVendorCode;
    private String primaryBusiness;
    private String address;
    private String landline;
    //private String mobileNo;
    private String fax;
    private String panNo;
    private String gstNo;
    private String bankName;
    private String accountNo;
    private String ifscCode;
    private String purchaseHistory;
    private String status;
    private String remarks;
    private String alternateEmailOrPhoneNumber;
    private String swiftCode;
    private String bicCode;
    private String ibanAbaNumber;
    private String sortCode;
    private String bankRoutingNumber;
    private String bankAddress;
    private String country;
    private String state;
    private String place;
    private String updatedBy;
    private String createdBy;
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;


}

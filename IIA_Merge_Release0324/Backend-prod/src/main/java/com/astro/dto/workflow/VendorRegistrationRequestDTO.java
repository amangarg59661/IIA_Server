package com.astro.dto.workflow;

import io.swagger.models.auth.In;
import lombok.Data;

import javax.persistence.Column;

@Data
public class VendorRegistrationRequestDTO {

    private String vendorName;
    private String vendorType;
    private String contactNumber;
    private String emailAddress;
    private Boolean registeredPlatform;
    private String pfmsVendorCode;
    private String primaryBusiness;
    private String address;
  //  private String landlineNumber;
  //  private String mobileNumber;
    private String faxNumber;
    private String panNumber;
    private String gstNumber;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String updatedBy;
    private String createdBy;
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



}

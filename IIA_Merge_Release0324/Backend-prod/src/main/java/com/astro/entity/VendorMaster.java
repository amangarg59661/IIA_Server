package com.astro.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EntityListeners;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "vendor_master")
@EntityListeners(AuditingEntityListener.class)
public class VendorMaster {


    @Id
    @Column(name = "vendor_id")
    private String vendorId;

    @Column(name = "vendor_type")
    private String vendorType;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "registered_platform")
    private Boolean registeredPlatform;

    @Column(name = "pfms_vendor_code")
    private String pfmsVendorCode;

    @Column(name = "primary_business")
    private String primaryBusiness;

    @Column(name = "address")
    private String address;

   // @Column(name = "landline")
  //  private String landline;

   // @Column(name = "mobile_no")
  //  private String mobileNo;

    @Column(name = "fax")
    private String fax;

    @Column(name = "pan_no")
    private String panNo;

    @Column(name = "gst_no")
    private String gstNo;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "purchase_history")
    private String purchaseHistory;

    @Column(name = "status")
    private String status;

    private String remarks;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;
    @Column(name = "alternate_email_or_phone_number")
    private String alternateEmailOrPhoneNumber;

    @Column(name = "swift_code")
    private String swiftCode;

    @Column(name = "bic_code")
    private String bicCode;

    @Column(name = "iban_aba_number")
    private String ibanAbaNumber;

    @Column(name = "sort_code")
    private String sortCode;

    @Column(name = "bank_routing_number")
    private String bankRoutingNumber;

    @Column(name = "bank_address", length = 500)
    private String bankAddress;
    @Column(name = "country")
    private String country;

    @Column(name = "state")
    private String state;

    @Column(name = "place")
    private String place;

    private String statusOfVendorActiveOrDebar;

    private String reasonForDebar;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updatedDate;



}

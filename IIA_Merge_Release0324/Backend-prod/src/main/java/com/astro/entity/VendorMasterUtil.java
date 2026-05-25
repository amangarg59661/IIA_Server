package com.astro.entity;
import lombok.Data;
import org.apache.logging.log4j.message.StringFormattedMessage;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "vendor_master_util")
@EntityListeners(AuditingEntityListener.class)
public class VendorMasterUtil {

    @Id
    private String vendorId;
    @Column(name = "vendor_number")
    private Integer vendorNumber;
    private String vendorName;
    private String vendorType;
    private String contactNumber;
    @Column(name = "email_address", unique = true)
    private String emailAddress;
    private Boolean registeredPlatform;
    private String pfmsVendorCode;
    private String primaryBusiness;
    private String address;
  //  private String mobileNumber;
    private String faxNumber;
    @Column(name = "pan_number", unique = true)
    private String panNumber;
    private String gstNumber;
    private String bankName;
    private String accountNumber;
    private String ifscCode;
   //private String purchaseHistory;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    private String comments;
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;
    @Column(name = "alternate_email_or_phone_number", length = 255)
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



    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updatedDate;

    public enum ApprovalStatus {

        APPROVED,
        REJECTED,
        AWAITING_APPROVAL,
        CHANGE_REQUEST

    }


}

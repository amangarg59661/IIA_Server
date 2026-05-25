package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(name="vendor_login_details")
@EntityListeners(AuditingEntityListener.class)
public class VendorLoginDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vendorId;
    private String emailAddress;
    private String password;
    private Boolean emailSent;

    @Column(name = "is_first_login")
    private Boolean isFirstLogin = true;

    @Column(name = "is_temp_password")
    private Boolean isTempPassword = true;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @CreatedDate
    private LocalDateTime createdDate;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate;
}
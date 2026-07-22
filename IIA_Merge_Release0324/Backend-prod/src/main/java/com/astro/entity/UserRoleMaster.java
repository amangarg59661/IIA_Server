package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "user_role_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserRoleMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USERROLEID")
    private Integer userRoleId;

    @Column(name = "USERID")
    private Integer userId;

    @Column(name = "ROLEID")
    private Integer roleId;

    @Column(name = "READPERMISSION")
    private Boolean readPermission;

    @Column(name = "WRITEPERMISSION")
    private Boolean writePermission;

    @Column(name = "CREATEDBY")
    @CreatedBy
    private String createdBy;

    @Column(name = "CREATEDDATE")
    private Date createdDate;

    @Column(name = "IS_ACTIVE", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;
}

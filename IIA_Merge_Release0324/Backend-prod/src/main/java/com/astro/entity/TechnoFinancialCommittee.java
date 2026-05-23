package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "techno_financial_committee")
@EntityListeners(AuditingEntityListener.class)
public class TechnoFinancialCommittee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "employee_id", length = 50)
    private String employeeId;

    @Column(name = "member_name", nullable = false, length = 200)
    private String memberName;

    @Column(name = "designation", length = 200)
    private String designation;

    @Column(name = "email_address", length = 200)
    private String emailAddress;

    // MEMBER, CHAIRMAN, or CO_CHAIRMAN
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    // STEC_I or STEC_II – which standing committee this member belongs to
    @Column(name = "committee_type", length = 10)
    private String committeeType;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_by", length = 100)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;
}
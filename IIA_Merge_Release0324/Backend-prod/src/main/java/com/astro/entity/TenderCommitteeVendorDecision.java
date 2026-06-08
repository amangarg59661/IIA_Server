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
@Table(name = "tender_committee_vendor_decision",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_tender_vendor_member_phase",
           columnNames = {"tender_id", "vendor_id", "committee_user_id", "phase"}))
@EntityListeners(AuditingEntityListener.class)
public class TenderCommitteeVendorDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tender_id", nullable = false, length = 50)
    private String tenderId;

    @Column(name = "vendor_id", nullable = false, length = 50)
    private String vendorId;

    @Column(name = "committee_user_id", nullable = false)
    private Integer committeeUserId;

    @Column(name = "member_name", length = 255)
    private String memberName;

    @Column(name = "decision", length = 20)
    private String decision;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "phase", nullable = false, length = 20)
    private String phase;

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @Column(name = "confirmed")
    private Boolean confirmed = false;

    @Column(name = "confirmed_date")
    private LocalDateTime confirmedDate;

    @Column(name = "voter_role", length = 20)
    private String voterRole = "MEMBER";  // MEMBER, CHAIRMAN, DIRECTOR

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}

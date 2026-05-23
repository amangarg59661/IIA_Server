
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
@Table(name = "tender_committee_decision")
@EntityListeners(AuditingEntityListener.class)
public class TenderCommitteeDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tender_id", nullable = false, length = 100)
    private String tenderId;

    // Committee member vote
    @Column(name = "committee_user_id")
    private Integer committeeUserId;

    @Column(name = "committee_member_name", length = 200)
    private String committeeMemberName;

    // APPROVED or REJECTED
    @Column(name = "vote", length = 20)
    private String vote;

    @Column(name = "vote_remarks", length = 1000)
    private String voteRemarks;

    @Column(name = "voted_date")
    private LocalDateTime votedDate;

    // Dynamic expert elected by chairman for this tender
    @Column(name = "expert_user_id")
    private Integer expertUserId;

    @Column(name = "expert_name", length = 200)
    private String expertName;

    @Column(name = "expert_assigned_date")
    private LocalDateTime expertAssignedDate;

    // Chairman final decision
    // APPROVED, REJECTED, OVERRIDDEN
    @Column(name = "chairman_decision", length = 20)
    private String chairmanDecision;

    @Column(name = "chairman_remarks", length = 1000)
    private String chairmanRemarks;

    @Column(name = "chairman_override_used")
    private Boolean chairmanOverrideUsed = false;

    @Column(name = "chairman_decision_date")
    private LocalDateTime chairmanDecisionDate;

    // Director final decision
    // APPROVED or REJECTED
    @Column(name = "director_decision", length = 20)
    private String directorDecision;

    @Column(name = "director_remarks", length = 1000)
    private String directorRemarks;

    @Column(name = "director_decision_date")
    private LocalDateTime directorDecisionDate;

    @Column(name = "director_user_id")
    private Integer directorUserId;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;

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
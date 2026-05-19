package com.astro.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tender_clarification_history")
@Data
public class TenderClarificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tender_id", nullable = false, length = 100)
    private String tenderId;

    @Column(name = "round_number")
    private Integer roundNumber;

    @Column(name = "requested_by_role", length = 50)
    private String requestedByRole;

    @Column(name = "requested_by_user_id")
    private Integer requestedByUserId;

    /** VENDOR, ALL_VENDORS, INDENTOR, PURCHASE_PERSONNEL, SPECIFIC_MEMBER, ALL_MEMBERS */
    @Column(name = "clarification_target", length = 50)
    private String clarificationTarget;

    /** Populated when targeting a specific vendor */
    @Column(name = "target_vendor_id", length = 100)
    private String targetVendorId;

    @Column(name = "target_user_id")
    private Integer targetUserId;

    @Column(name = "target_user_name", length = 200)
    private String targetUserName;

    @Column(name = "question_remarks", columnDefinition = "TEXT")
    private String questionRemarks;

    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;

    @Column(name = "response_file_name", length = 500)
    private String responseFileName;

    @Column(name = "responded_by_role", length = 50)
    private String respondedByRole;

    @Column(name = "responded_by_id", length = 100)
    private String respondedById;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
}
package com.astro.entity.ProcurementModule;


// import io.swagger.models.auth.In;
import java.math.BigDecimal;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tender_evaluation")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TenderEvaluation {
    // @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    // private Long tenderEvaluationId;
    @Id
    private String tenderId;
    @Column(length = 500)
    private String uploadQualifiedVendorsFileName;
    @Column(length = 500)
    private String uploadTechnicallyQualifiedVendorsFileName;
    @Column(length = 500)
    private String uploadCommeriallyQualifiedVendorsFileName;
    @Column(length = 500)
    private String formationOfTechnoCommerialComitee;
    @Column(length = 500)
    private String responseFileName;
    @Column(length = 500)
    private String responseForTechnicallyQualifiedVendorsFileName;
    @Column(length = 500)
    private String responseForCommeriallyQualifiedVendorsFileName;
    private String uploadQualifiedVendorsFileNameCreatedBy;
    private String uploadTechnicallyQualifiedVendorsFileNameCreatedBy;
    private String uploadCommeriallyQualifiedVendorsFileNameCreatedBy;
    private String formationOfTechnoCommerialComiteeCreatedBy;
    private String responseFileNameCreatedBy;
    private String responseForTechnicallyQualifiedVendorsFileNameCreatedBy;
    private String responseForCommeriallyQualifiedVendorsFileNameCreatedBy;

    private String fileType;
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;
    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;
    @Column(name = "updated_date", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedDate;
// ── Tender Evaluation Flow Fields ──

    /**
     * PENDING_TECHNICAL, PENDING_FINANCIAL, PENDING_APPROVAL,
     * APPROVED, REJECTED
     */
    @Column(name = "evaluation_status", length = 50)
    private String evaluationStatus;

    /**
     * SINGLE_BID or DOUBLE_BID – copied from TenderRequest.bidType on initiation
     */
    @Column(name = "bid_type", length = 20)
    private String bidType;

    /**
     * Derived from TenderRequest.totalTenderValue:
     *   UNDER_10_LAKH              → < ₹10 Lakhs     → Indentor handles approval
     *   ABOVE_10_LAKH_UPTO_50_LAKH → ₹10L–₹50L       → STEC-I committee
     *   ABOVE_50_LAKH_UPTO_1_CRORE → ₹50L–₹1 Crore   → STEC-II committee
     *   ABOVE_1_CRORE              → > ₹1 Crore       → Ad hoc committee by Director
     */
    @Column(name = "amount_category", length = 30)
    private String amountCategory;

    /**
     * SINGLE_INDENT or MULTIPLE_INDENT – derived from IndentIds count in TenderRequest
     */
    @Column(name = "indent_category", length = 20)
    private String indentCategory;

    /** vendorId of the ultimately approved vendor */
    @Column(name = "approved_vendor_id", length = 100)
    private String approvedVendorId;

    @Column(name = "approved_vendor_name", length = 300)
    private String approvedVendorName;

    @Column(name = "approval_remarks", length = 1000)
    private String approvalRemarks;

    /** Whether the approved vendor has been registered/auto-registered on vendor portal */
    @Column(name = "vendor_portal_registered")
    private Boolean vendorPortalRegistered = false;

    /** Total value cached at evaluation initiation */
    @Column(name = "total_tender_value", precision = 18, scale = 2)
    private BigDecimal totalTenderValue;

    // ── Clarification Tracking ──

    /**
     * Who needs to respond to the pending clarification.
     * Values: VENDOR, INDENTOR, PURCHASE_PERSONNEL, SPECIFIC_MEMBER, ALL_MEMBERS
     */
    @Column(name = "clarification_pending_from", length = 50)
    private String clarificationPendingFrom;

    /** UserId of the specific person who should respond (used when SPECIFIC_MEMBER) */
    @Column(name = "clarification_pending_from_id")
    private Integer clarificationPendingFromId;

    /** Name of the specific person (display purposes) */
    @Column(name = "clarification_pending_from_name", length = 200)
    private String clarificationPendingFromName;

    /**
     * Role of the person who requested the clarification.
     * Values: INDENTOR, PURCHASE_PERSONNEL, SPO, CHAIRMAN, DIRECTOR
     */
    @Column(name = "clarification_requested_by_role", length = 50)
    private String clarificationRequestedByRole;

    /** The actual clarification question / remarks */
    @Column(name = "clarification_remarks", columnDefinition = "TEXT")
    private String clarificationRemarks;

    /** The evaluation status BEFORE clarification was sought — restored after response is received */
    @Column(name = "previous_evaluation_status", length = 50)
    private String previousEvaluationStatus;

    // ── Double Bid Under 10L: Financial Bid Phase ──

    /**
     * true when we are in the FINANCIAL BID evaluation round for a DOUBLE_BID / UNDER_10_LAKH tender.
     * Flow: SPO approves technical → financialBidPhase=true, status→PENDING_APPROVAL (Indentor evaluates financial)
     *       → Indentor approves → status→PENDING_SPO_APPROVAL → SPO final approve → APPROVED
     */
    @Column(name = "financial_bid_phase")
    private Boolean financialBidPhase = false;

    // ── Ad-Hoc Committee for ABOVE_1_CRORE (Director constitutes) ──

    @Column(name = "ad_hoc_chairman_user_id")
    private Integer adHocChairmanUserId;

    @Column(name = "ad_hoc_chairman_name", length = 200)
    private String adHocChairmanName;

    @Column(name = "ad_hoc_co_chairman_user_id")
    private Integer adHocCoChairmanUserId;

    @Column(name = "ad_hoc_co_chairman_name", length = 200)
    private String adHocCoChairmanName;

    @Column(name = "rejected_by_role", length = 50)
    private String rejectedByRole;

    @Column(name = "rejected_by_user_id")
    private Integer rejectedByUserId;

    /**
     * Evaluation initiation flag.
     * 0 = not yet initiated — all vendors may upload documents freely.
     * 1 = initiated — only vendors who already submitted a bid before initiation
     *     may still upload (seek-clarification path); new-bid uploads are blocked.
     */
    @Column(name = "initiated", nullable = false)
    private Integer initiated = 0;

}

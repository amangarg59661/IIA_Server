package com.astro.dto.workflow;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TenderEvaluationStatusDto {

    private String tenderId;
    private String evaluationStatus;   // PENDING_TECHNICAL, PENDING_FINANCIAL, PENDING_APPROVAL, APPROVED, REJECTED
    private String bidType;            // SINGLE_BID, DOUBLE_BID
    private String amountCategory;     // UNDER_10_LAKH, ABOVE_10_LAKH_UPTO_50_LAKH, ABOVE_50_LAKH_UPTO_1_CRORE, ABOVE_1_CRORE
    private String committeeType;      // null (under 10L) | STEC_I | STEC_II | ADHOC
    private String indentCategory;     // SINGLE_INDENT, MULTIPLE_INDENT
    private BigDecimal totalTenderValue;
    private String approvedVendorId;
    private String approvedVendorName;
    private String approvalRemarks;
    private Boolean vendorPortalRegistered;

    // Per-vendor quotation list
    private List<VendorQuotationEvalDto> vendors;

    // Committee info (above 10L only)
    private List<CommitteeVoteDto> committeeVotes;
    private String expertName;
    private Integer expertUserId;
    private String chairmanDecision;
    private String chairmanRemarks;
    private Boolean chairmanOverrideUsed;
    private String directorDecision;
    private String directorRemarks;

    // ── Clarification info ──
    private String clarificationPendingFrom;       // VENDOR, INDENTOR, PURCHASE_PERSONNEL, SPECIFIC_MEMBER, ALL_MEMBERS
    private String clarificationPendingFromName;
    private Integer clarificationPendingFromId;
    private String clarificationRequestedByRole;   // INDENTOR, PURCHASE_PERSONNEL, SPO, CHAIRMAN, DIRECTOR
    private String clarificationRemarks;
    private String previousEvaluationStatus;

    // ── Comparison sheet uploaded by Purchase Personnel ──
    private String comparisonSheetFileName;
    private String financialComparisonSheetFileName;

    // ── Double Bid Under 10L financial phase ──
    private Boolean financialBidPhase;

    // ── Ad-hoc committee (Above 1 Crore) ──
    private Integer adHocChairmanUserId;
    private String adHocChairmanName;
    private Integer adHocCoChairmanUserId;
    private String adHocCoChairmanName;

    @Data
    public static class VendorQuotationEvalDto {
        private String vendorId;
        private String vendorName;
        private String quotationFileName;
        private String priceBidFileName;      // financial bid - null if not visible
        private Boolean financialBidVisible;
        private String technicalStatus;       // PENDING, APPROVED, REJECTED
        private String technicalRemarks;
        private String status;                // overall quotation status
        private String indentorStatus;
        private String spoStatus;
        private Integer rank;
    }

    @Data
    public static class CommitteeVoteDto {
        private Integer committeeUserId;
        private String committeeMemberName;
        private String vote;                  // APPROVED, REJECTED, null if not voted yet
        private String voteRemarks;
    }
}
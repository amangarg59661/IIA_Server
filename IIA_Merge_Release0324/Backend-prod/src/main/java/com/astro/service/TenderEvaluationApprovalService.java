
package com.astro.service;

import com.astro.dto.workflow.DirectorFormCommitteeDto;
import com.astro.dto.workflow.RespondClarificationDto;
import com.astro.dto.workflow.SeekClarificationDto;
import com.astro.dto.workflow.TenderCommitteeDecisionDto;
import com.astro.dto.workflow.TenderEvaluationStatusDto;
import com.astro.dto.workflow.VendorTechnicalDecisionDto;
import com.astro.entity.TenderClarificationHistory;
import java.util.List;

public interface TenderEvaluationApprovalService {

    /**
     * Initiates evaluation for an approved tender.
     * Determines amountCategory (4 tiers per updated policy):
     *   UNDER_10_LAKH              → Indentor handles approval
     *   ABOVE_10_LAKH_UPTO_50_LAKH → STEC-I (Chair, Co-Chair, Accounts Officer, S&P Officer, Domain Expert)
     *   ABOVE_50_LAKH_UPTO_1_CRORE → STEC-II (same structure, different Chair/Co-Chair)
     *   ABOVE_1_CRORE              → Ad hoc committee constituted by Director
     * Also: Director has overriding authority over committee decisions for all above-10L tenders.
     */
    TenderEvaluationStatusDto initiateTenderEvaluation(String tenderId, Integer initiatedByUserId);

    /**
     * Returns full evaluation status for a tender.
     * Respects bid type: hides financial data for DOUBLE_BID if not technically approved.
     */
    TenderEvaluationStatusDto getEvaluationStatus(String tenderId, Integer requestingUserId, String requestingRole);

    /**
     * Purchase Person / Indentor evaluates technical bid for a specific vendor.
     * Only for DOUBLE_BID tenders.
     * If APPROVED → sets financialBidVisible = true for that vendor.
     */
    void evaluateTechnicalBid(String tenderId, String vendorId, VendorTechnicalDecisionDto dto);

    /**
     * Selects the final approved vendor and submits comparison statement.
     * Triggers:
     *   - UNDER_10_LAKH                 → routes to Indentor/Purchase Dept → SPO final approval
     *   - ABOVE_10_LAKH_UPTO_50_LAKH    → routes to STEC-I committee vote → Chairman → Director
     *   - ABOVE_50_LAKH_UPTO_1_CRORE    → routes to STEC-II committee vote → Chairman → Director
     *   - ABOVE_1_CRORE                 → routes to Director (ad hoc committee)
     */
    TenderEvaluationStatusDto selectApprovedVendor(String tenderId, String approvedVendorId,
                                                    String remarks, Integer actionByUserId);

    /**
     * Under 10L: Indentor or Purchase Dept gives final approval / rejection.
     */
    TenderEvaluationStatusDto approveByIndentorOrPurchaseDept(String tenderId, String decision,
                                                               String remarks, Integer approverUserId);

    /**
     * Under 10L: Store Purchase Officer gives final approval.
     */
    TenderEvaluationStatusDto approveByStorePurchaseOfficer(String tenderId, String decision,
                                                             String remarks, Integer spoUserId);

    /**
     * Above 10L: A committee member casts their vote.
     */
    TenderEvaluationStatusDto castCommitteeVote(String tenderId, String vote,
                                                 String remarks, Integer committeeUserId);

    /**
     * Above 10L: Chairman assigns a dynamic expert for this tender.
     */
    TenderEvaluationStatusDto assignExpert(String tenderId, Integer expertUserId,
                                           String expertName, Integer chairmanUserId);

    /**
     * Above 10L: Chairman gives final decision (approve/reject or override committee).
     */
    TenderEvaluationStatusDto chairmanDecide(String tenderId, TenderCommitteeDecisionDto dto);

    /**
     * Above 10L: Director gives final approval after chairman.
     */
    TenderEvaluationStatusDto directorApprove(String tenderId, String decision,
                                               String remarks, Integer directorUserId);

    /**
     * Checks if the approved vendor is registered on the vendor portal.
     * Auto-registers if not. Returns registration status.
     */
    boolean checkAndRegisterVendorOnPortal(String vendorId);

    // ──────────────────────────────────────────────────────────────────────
    // NEW: Clarification Flow
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Any approver (Indentor, SPO, Chairman, Director) seeks clarification.
     * Saves current status, sets new status (PENDING_VENDOR_CLARIFICATION /
     * PENDING_INDENTOR_CLARIFICATION / PENDING_MEMBER_REVOTE), records who
     * needs to respond.
     */
    TenderEvaluationStatusDto seekClarification(String tenderId, SeekClarificationDto dto);

    /**
     * Vendor, indentor, purchase personnel, or member responds to a clarification.
     * Restores the previous evaluation status so the flow continues.
     */
    TenderEvaluationStatusDto respondToClarification(String tenderId, RespondClarificationDto dto);

    // ──────────────────────────────────────────────────────────────────────
    // NEW: Director Forms Ad-Hoc Committee (Above 1 Crore — Cases 9 & 10)
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Director chooses members, chairman, and co-chairman for the ad-hoc committee.
     * Status transitions: PENDING_COMMITTEE_FORMATION → PENDING_APPROVAL (voting starts).
     */
    TenderEvaluationStatusDto directorFormCommittee(String tenderId, DirectorFormCommitteeDto dto);

    /**
     * Indent Creator confirms evaluation after reviewing quotations.
     * Only for UNDER_10_LAKH tenders (single intent flow with Proprietary/Limited Tender).
     * Status transitions: PENDING_FINANCIAL → PENDING_SPO_APPROVAL
     */
    TenderEvaluationStatusDto confirmByIndentor(String tenderId, Integer indentorUserId);

    /**
     * Returns full clarification history for a tender (all rounds, questions + responses).
     */
    List<TenderClarificationHistory> getClarificationHistory(String tenderId);
}
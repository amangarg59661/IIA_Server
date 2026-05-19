package com.astro.controller.ProcurementModuleController;

import com.astro.dto.workflow.CommitteeVoteRequestDto;
import com.astro.dto.workflow.ConfirmByIndentorRequestDto;
import com.astro.dto.workflow.DirectorApprovalRequestDto;
import com.astro.dto.workflow.DirectorFormCommitteeDto;
import com.astro.dto.workflow.ExpertAssignmentRequestDto;
import com.astro.dto.workflow.IndentorApprovalRequestDto;
import com.astro.dto.workflow.ProcurementDtos.TenderEvaluationRequestDto;
import com.astro.dto.workflow.ProcurementDtos.TenderEvaluationResponseDto;
import com.astro.dto.workflow.ProcurementDtos.TenderEvaluationResponseWithBitTypeAndValueDto;
import com.astro.dto.workflow.RespondClarificationDto;
import com.astro.dto.workflow.SeekClarificationDto;
import com.astro.dto.workflow.SpoApprovalRequestDto;
import com.astro.dto.workflow.TenderCommitteeDecisionDto;
import com.astro.dto.workflow.TenderEvaluationStatusDto;
import com.astro.dto.workflow.VendorDecisionRequestDto;
import com.astro.dto.workflow.VendorSelectionRequestDto;
import com.astro.dto.workflow.VendorTechnicalDecisionDto;
import com.astro.service.TenderEvaluationApprovalService;
import com.astro.service.TenderEvaluationService;
import com.astro.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tender-evaluation")
public class TenderEvaluationController {

    private static final Logger log = LoggerFactory.getLogger(TenderEvaluationController.class);

    private final TenderEvaluationService tenderEvaluationService;
    private final TenderEvaluationApprovalService approvalService;

    public TenderEvaluationController(TenderEvaluationService tenderEvaluationService,
                                      TenderEvaluationApprovalService approvalService) {
        this.tenderEvaluationService = tenderEvaluationService;
        this.approvalService = approvalService;
    }

    // ─── CRUD ────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<Object> createTenderEvaluation(
            @Valid @RequestBody TenderEvaluationRequestDto dto) {
        log.info("Create tender evaluation: {}", dto.getTenderId());
        TenderEvaluationResponseDto created = tenderEvaluationService.createTenderEvaluation(dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(created), HttpStatus.CREATED);
    }

    @PutMapping("/{tenderId}")
    public ResponseEntity<Object> updateTenderEvaluation(
            @PathVariable String tenderId,
            @Valid @RequestBody TenderEvaluationRequestDto dto) {
        log.info("Update tender evaluation: {}", tenderId);
        TenderEvaluationResponseDto response = tenderEvaluationService.updateTenderEvaluation(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> getAllTenderEvaluation() {
        List<TenderEvaluationResponseDto> response = tenderEvaluationService.getAllTenderEvaluations();
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @GetMapping("/{tenderId}")
    public ResponseEntity<Object> getTenderEvaluationById(@PathVariable String tenderId) {
        TenderEvaluationResponseWithBitTypeAndValueDto response =
                tenderEvaluationService.getTenderEvaluationById(tenderId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @DeleteMapping("/{tenderId}")
    public ResponseEntity<String> deleteTenderEvaluation(@PathVariable String tenderId) {
        log.info("Delete tender evaluation: {}", tenderId);
        tenderEvaluationService.deleteTenderEvaluation(tenderId);
        return ResponseEntity.ok("Tender Evaluation deleted successfully. tenderId: " + tenderId);
    }

    // ─── EVALUATION WORKFLOW ─────────────────────────────────────────

    /**
     * Step 1: Initiate evaluation for an approved tender.
     * POST /api/tender-evaluation/{tenderId}/initiate?userId=123
     */
    @PostMapping("/{tenderId}/initiate")
    public ResponseEntity<Object> initiateEvaluation(
            @PathVariable String tenderId,
            @RequestParam Integer userId) {
        log.info("Initiate evaluation tenderId={} userId={}", tenderId, userId);
        TenderEvaluationStatusDto status = approvalService.initiateTenderEvaluation(tenderId, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Get full evaluation status (respects financial bid visibility rules).
     * GET /api/tender-evaluation/{tenderId}/status?userId=123&role=PURCHASE_PERSONNEL
     */
    @GetMapping("/{tenderId}/status")
    public ResponseEntity<Object> getEvaluationStatus(
            @PathVariable String tenderId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String role) {
        TenderEvaluationStatusDto status = approvalService.getEvaluationStatus(tenderId, userId, role);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Step 2 (Double Bid only): Evaluate technical bid for a specific vendor.
     * PUT /api/tender-evaluation/{tenderId}/technical/{vendorId}
     */
    @PutMapping("/{tenderId}/technical/{vendorId}")
    public ResponseEntity<Object> evaluateTechnicalBid(
            @PathVariable String tenderId,
            @PathVariable String vendorId,
            @Valid @RequestBody VendorTechnicalDecisionDto dto) {
        log.info("Technical evaluation tenderId={} vendorId={} decision={}", tenderId, vendorId, dto.getDecision());
        dto.setVendorId(vendorId);
        approvalService.evaluateTechnicalBid(tenderId, vendorId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Technical evaluation saved."), HttpStatus.OK);
    }

    /**
     * Step 3: Select approved vendor after financial evaluation.
     * POST /api/tender-evaluation/{tenderId}/select-vendor
     */
    @PostMapping("/{tenderId}/select-vendor")
    public ResponseEntity<Object> selectApprovedVendor(
            @PathVariable String tenderId,
            @Valid @RequestBody VendorSelectionRequestDto dto) {
        log.info("Select vendor tenderId={} vendorId={}", tenderId, dto.getVendorId());
        TenderEvaluationStatusDto status = approvalService.selectApprovedVendor(
                tenderId, dto.getVendorId(), dto.getRemarks(), dto.getActionByUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Under 10L: Indentor or Purchase Dept approves/rejects.
     * POST /api/tender-evaluation/{tenderId}/approve/indentor-purchase
     */
    @PostMapping("/{tenderId}/approve/indentor-purchase")
    public ResponseEntity<Object> approveByIndentorOrPurchaseDept(
            @PathVariable String tenderId,
            @Valid @RequestBody IndentorApprovalRequestDto dto) {
        log.info("Indentor/PP approval tenderId={} decision={}", tenderId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.approveByIndentorOrPurchaseDept(
                tenderId, dto.getDecision(), dto.getRemarks(), dto.getApproverUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Under 10L: Store Purchase Officer final approval.
     * POST /api/tender-evaluation/{tenderId}/approve/spo
     */
    @PostMapping("/{tenderId}/approve/spo")
    public ResponseEntity<Object> approveByStorePurchaseOfficer(
            @PathVariable String tenderId,
            @Valid @RequestBody SpoApprovalRequestDto dto) {
        log.info("SPO approval tenderId={} decision={}", tenderId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.approveByStorePurchaseOfficer(
                tenderId, dto.getDecision(), dto.getRemarks(), dto.getSpoUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Above 10L: Committee member casts vote.
     * POST /api/tender-evaluation/{tenderId}/committee/vote
     */
    @PostMapping("/{tenderId}/committee/vote")
    public ResponseEntity<Object> castCommitteeVote(
            @PathVariable String tenderId,
            @Valid @RequestBody CommitteeVoteRequestDto dto) {
        log.info("Committee vote tenderId={} userId={} vote={}", tenderId, dto.getCommitteeUserId(), dto.getVote());
        TenderEvaluationStatusDto status = approvalService.castCommitteeVote(
                tenderId, dto.getVote(), dto.getRemarks(), dto.getCommitteeUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Above 10L: Chairman assigns a dynamic expert for this tender.
     * POST /api/tender-evaluation/{tenderId}/committee/expert
     */
    @PostMapping("/{tenderId}/committee/expert")
    public ResponseEntity<Object> assignExpert(
            @PathVariable String tenderId,
            @Valid @RequestBody ExpertAssignmentRequestDto dto) {
        log.info("Assign expert tenderId={} expertId={}", tenderId, dto.getExpertUserId());
        TenderEvaluationStatusDto status = approvalService.assignExpert(
                tenderId, dto.getExpertUserId(), dto.getExpertName(), dto.getChairmanUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Above 10L: Chairman gives final decision (can override committee).
     * POST /api/tender-evaluation/{tenderId}/committee/chairman-decision
     */
    @PostMapping("/{tenderId}/committee/chairman-decision")
    public ResponseEntity<Object> chairmanDecide(
            @PathVariable String tenderId,
            @Valid @RequestBody TenderCommitteeDecisionDto dto) {
        log.info("Chairman decision tenderId={} decision={}", tenderId, dto.getDecision());
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.chairmanDecide(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Above 10L: Director gives final approval.
     * POST /api/tender-evaluation/{tenderId}/director/approve
     */
    @PostMapping("/{tenderId}/director/approve")
    public ResponseEntity<Object> directorApprove(
            @PathVariable String tenderId,
            @Valid @RequestBody DirectorApprovalRequestDto dto) {
        log.info("Director approval tenderId={} decision={}", tenderId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.directorApprove(
                tenderId, dto.getDecision(), dto.getRemarks(), dto.getDirectorUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Check / auto-register a vendor on the vendor portal.
     * POST /api/tender-evaluation/vendor-portal-check/{vendorId}
     */
    @PostMapping("/vendor-portal-check/{vendorId}")
    public ResponseEntity<Object> checkVendorPortalRegistration(@PathVariable String vendorId) {
        boolean registered = approvalService.checkAndRegisterVendorOnPortal(vendorId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                Map.of("vendorId", vendorId, "registered", registered)), HttpStatus.OK);
    }

    // ─── CLARIFICATION ENDPOINTS ──────────────────────────────────────

    /**
     * Any approver seeks clarification.
     * POST /api/tender-evaluation/{tenderId}/seek-clarification
     */
    @PostMapping("/{tenderId}/seek-clarification")
    public ResponseEntity<Object> seekClarification(
            @PathVariable String tenderId,
            @Valid @RequestBody SeekClarificationDto dto) {
        log.info("Seek clarification tenderId={} by role={}", tenderId, dto.getRequestedByRole());
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.seekClarification(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Respond to a pending clarification.
     * POST /api/tender-evaluation/{tenderId}/respond-clarification
     */
    @PostMapping("/{tenderId}/respond-clarification")
    public ResponseEntity<Object> respondToClarification(
            @PathVariable String tenderId,
            @Valid @RequestBody RespondClarificationDto dto) {
        log.info("Respond to clarification tenderId={} by role={}", tenderId, dto.getRespondedByRole());
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.respondToClarification(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Under 10L: Indent Creator/Purchase Personnel confirms evaluation.
     * POST /api/tender-evaluation/{tenderId}/confirm-by-indentor
     */
    @PostMapping("/{tenderId}/confirm-by-indentor")
    public ResponseEntity<Object> confirmByIndentor(
            @PathVariable String tenderId,
            @Valid @RequestBody ConfirmByIndentorRequestDto dto) {
        log.info("Confirm by indentor tenderId={} userId={}", tenderId, dto.getIndentorUserId());
        TenderEvaluationStatusDto status = approvalService.confirmByIndentor(tenderId, dto.getIndentorUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    // ─── DIRECTOR COMMITTEE (Above 1 Crore) ──────────────────────────

    /**
     * Director forms the ad-hoc committee for tenders above ₹1 Crore.
     * POST /api/tender-evaluation/{tenderId}/director/form-committee
     */
    @PostMapping("/{tenderId}/director/form-committee")
    public ResponseEntity<Object> directorFormCommittee(
            @PathVariable String tenderId,
            @Valid @RequestBody DirectorFormCommitteeDto dto) {
        log.info("Director forms committee tenderId={}", tenderId);
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.directorFormCommittee(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    // ─── QUERY ENDPOINTS ─────────────────────────────────────────────

    /**
     * GET /api/tender-evaluation/{tenderId}/clarification-history
     */
    @GetMapping("/{tenderId}/clarification-history")
    public ResponseEntity<Object> getClarificationHistory(@PathVariable String tenderId) {
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getClarificationHistory(tenderId)),
                HttpStatus.OK);
    }

    /**
     * GET /api/tender-evaluation/{tenderId}/approved-vendors
     * Returns SPO-approved vendors for PO creation. Only valid when status = APPROVED.
     */
    @GetMapping("/{tenderId}/approved-vendors")
    public ResponseEntity<Object> getApprovedVendorsForPO(@PathVariable String tenderId) {
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getApprovedVendorsForPO(tenderId)),
                HttpStatus.OK);
    }

    // ─── PER-VENDOR DECISION ENDPOINTS ───────────────────────────────

    /**
     * Save per-vendor indentor/purchase-personnel decision (Accept or Reject) immediately.
     * Does NOT advance overall evaluation status.
     * POST /api/tender-evaluation/{tenderId}/vendor/{vendorId}/indentor-decision
     */
    @PostMapping("/{tenderId}/vendor/{vendorId}/indentor-decision")
    public ResponseEntity<Object> saveVendorIndentorDecision(
            @PathVariable String tenderId,
            @PathVariable String vendorId,
            @Valid @RequestBody VendorDecisionRequestDto dto) {
        log.info("Indentor decision tenderId={} vendorId={} decision={}", tenderId, vendorId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.saveVendorIndentorDecision(
                tenderId, vendorId, dto.getDecision(), dto.getRemarks(), dto.getUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Save per-vendor SPO decision (Accept or Reject) immediately.
     * Does NOT advance overall evaluation status.
     * POST /api/tender-evaluation/{tenderId}/vendor/{vendorId}/spo-decision
     */
    @PostMapping("/{tenderId}/vendor/{vendorId}/spo-decision")
    public ResponseEntity<Object> saveVendorSpoDecision(
            @PathVariable String tenderId,
            @PathVariable String vendorId,
            @Valid @RequestBody VendorDecisionRequestDto dto) {
        log.info("SPO per-vendor decision tenderId={} vendorId={} decision={}", tenderId, vendorId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.saveVendorSpoDecision(
                tenderId, vendorId, dto.getDecision(), dto.getRemarks(), dto.getUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }
}

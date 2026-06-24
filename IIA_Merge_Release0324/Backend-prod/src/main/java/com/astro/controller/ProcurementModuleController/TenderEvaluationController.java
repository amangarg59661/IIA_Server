package com.astro.controller.ProcurementModuleController;

import com.astro.dto.workflow.CommitteeVoteRequestDto;
import com.astro.dto.workflow.ConfirmByIndentorRequestDto;
import com.astro.dto.workflow.RejectEvaluationRequestDto;
import com.astro.dto.workflow.DirectorApprovalRequestDto;
import com.astro.dto.workflow.DirectorFormCommitteeDto;
import com.astro.dto.workflow.ExpertAssignmentRequestDto;
import com.astro.dto.workflow.IndentorApprovalRequestDto;
import com.astro.dto.workflow.ProcurementDtos.TenderEvaluationRequestDto;
import com.astro.dto.workflow.ProcurementDtos.TenderEvaluationResponseDto;
import com.astro.dto.workflow.ProcurementDtos.TenderEvaluationResponseWithBitTypeAndValueDto;
import com.astro.dto.workflow.RespondClarificationDto;
import com.astro.dto.workflow.MemberClarificationResolutionDto;
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

    @PutMapping
    public ResponseEntity<Object> updateTenderEvaluation(
            @RequestParam String tenderId,
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

    @GetMapping("/by-id")
    public ResponseEntity<Object> getTenderEvaluationById(@RequestParam String tenderId) {
        TenderEvaluationResponseWithBitTypeAndValueDto response =
                tenderEvaluationService.getTenderEvaluationById(tenderId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTenderEvaluation(@RequestParam String tenderId) {
        log.info("Delete tender evaluation: {}", tenderId);
        tenderEvaluationService.deleteTenderEvaluation(tenderId);
        return ResponseEntity.ok("Tender Evaluation deleted successfully. tenderId: " + tenderId);
    }

    // ─── EVALUATION WORKFLOW ─────────────────────────────────────────

    @PostMapping("/begin")
    public ResponseEntity<Object> beginEvaluation(
            @RequestParam String tenderId,
            @RequestParam Integer userId) {
        log.info("Begin evaluation (Phase 1) tenderId={} userId={}", tenderId, userId);
        TenderEvaluationStatusDto status = approvalService.beginEvaluation(tenderId, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/initiate")
    public ResponseEntity<Object> initiateEvaluation(
            @RequestParam String tenderId,
            @RequestParam Integer userId) {
        log.info("Initiate evaluation tenderId={} userId={}", tenderId, userId);
        TenderEvaluationStatusDto status = approvalService.initiateTenderEvaluation(tenderId, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<Object> getEvaluationStatus(
            @RequestParam String tenderId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String role) {
        TenderEvaluationStatusDto status = approvalService.getEvaluationStatus(tenderId, userId, role);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PutMapping("/technical")
    public ResponseEntity<Object> evaluateTechnicalBid(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @Valid @RequestBody VendorTechnicalDecisionDto dto) {
        log.info("Technical evaluation tenderId={} vendorId={} decision={}", tenderId, vendorId, dto.getDecision());
        dto.setVendorId(vendorId);
        approvalService.evaluateTechnicalBid(tenderId, vendorId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Technical evaluation saved."), HttpStatus.OK);
    }

    @PostMapping("/select-vendor")
    public ResponseEntity<Object> selectApprovedVendor(
            @RequestParam String tenderId,
            @Valid @RequestBody VendorSelectionRequestDto dto) {
        log.info("Select vendor tenderId={} vendorId={}", tenderId, dto.getVendorId());
        TenderEvaluationStatusDto status = approvalService.selectApprovedVendor(
                tenderId, dto.getVendorId(), dto.getRemarks(), dto.getActionByUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/approve/indentor-purchase")
    public ResponseEntity<Object> approveByIndentorOrPurchaseDept(
            @RequestParam String tenderId,
            @Valid @RequestBody IndentorApprovalRequestDto dto) {
        log.info("Indentor/PP approval tenderId={} decision={}", tenderId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.approveByIndentorOrPurchaseDept(
                tenderId, dto.getDecision(), dto.getRemarks(), dto.getApproverUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/approve/spo")
    public ResponseEntity<Object> approveByStorePurchaseOfficer(
            @RequestParam String tenderId,
            @Valid @RequestBody SpoApprovalRequestDto dto) {
        log.info("SPO approval tenderId={} decision={}", tenderId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.approveByStorePurchaseOfficer(
                tenderId, dto.getDecision(), dto.getRemarks(), dto.getSpoUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/committee/vote")
    public ResponseEntity<Object> castCommitteeVote(
            @RequestParam String tenderId,
            @Valid @RequestBody CommitteeVoteRequestDto dto) {
        log.info("Committee vote tenderId={} userId={} vote={}", tenderId, dto.getCommitteeUserId(), dto.getVote());
        TenderEvaluationStatusDto status = approvalService.castCommitteeVote(
                tenderId, dto.getVote(), dto.getRemarks(), dto.getCommitteeUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/chairman/select-expert")
    public ResponseEntity<Object> chairmanSelectExpert(
            @RequestParam String tenderId,
            @Valid @RequestBody ExpertAssignmentRequestDto dto) {
        log.info("Chairman select expert tenderId={} expertId={} chairmanUserId={}",
                tenderId, dto.getExpertUserId(), dto.getChairmanUserId());
        TenderEvaluationStatusDto status = approvalService.chairmanSelectExpert(
                tenderId, dto.getChairmanUserId(), dto.getExpertUserId(), dto.getExpertName());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/chairman/confirm-committee")
    public ResponseEntity<Object> chairmanConfirmCommittee(
            @RequestParam String tenderId,
            @RequestParam Integer chairmanUserId) {
        log.info("Chairman confirms committee tenderId={} chairmanUserId={}", tenderId, chairmanUserId);
        TenderEvaluationStatusDto status = approvalService.chairmanConfirmCommittee(tenderId, chairmanUserId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/committee/chairman-decision")
    public ResponseEntity<Object> chairmanDecide(
            @RequestParam String tenderId,
            @Valid @RequestBody TenderCommitteeDecisionDto dto) {
        log.info("Chairman decision tenderId={} decision={}", tenderId, dto.getDecision());
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.chairmanDecide(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/director/approve")
    public ResponseEntity<Object> directorApprove(
            @RequestParam String tenderId,
            @Valid @RequestBody DirectorApprovalRequestDto dto) {
        log.info("Director approval tenderId={} decision={}", tenderId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.directorApprove(
                tenderId, dto.getDecision(), dto.getRemarks(), dto.getDirectorUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    // ─── COMMITTEE PER-VENDOR DECISION (Above 10L, Double Bid) ───────

    @PostMapping("/committee/vendor-decision")
    public ResponseEntity<Object> committeeVendorDecision(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @RequestParam String decision,
            @RequestParam(required = false) String remarks,
            @RequestParam Integer committeeUserId) {
        log.info("Committee vendor decision tenderId={} vendorId={} decision={} userId={}",
                tenderId, vendorId, decision, committeeUserId);
        TenderEvaluationStatusDto status = approvalService.committeeVendorDecision(
                tenderId, vendorId, decision, remarks, committeeUserId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/chairman/vendor-resolve")
    public ResponseEntity<Object> chairmanVendorResolve(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @RequestParam String decision,
            @RequestParam(required = false) String remarks,
            @RequestParam Integer chairmanUserId) {
        log.info("Chairman vendor resolve tenderId={} vendorId={} decision={} chairmanUserId={}",
                tenderId, vendorId, decision, chairmanUserId);
        TenderEvaluationStatusDto status = approvalService.chairmanVendorResolve(
                tenderId, vendorId, decision, remarks, chairmanUserId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/chairman/resolve-member-clarification")
    public ResponseEntity<Object> resolveMemberClarification(
            @RequestParam String tenderId,
            @Valid @RequestBody MemberClarificationResolutionDto dto) {
        log.info("Chairman resolve member clarification tenderId={} action={} chairmanUserId={}",
                tenderId, dto.getAction(), dto.getChairmanUserId());
        TenderEvaluationStatusDto status = approvalService.resolveMemberClarification(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/pp/submit-document")
    public ResponseEntity<Object> ppSubmitDocument(@RequestParam String tenderId) {
        log.info("PP submit document tenderId={}", tenderId);
        TenderEvaluationStatusDto status = approvalService.ppSubmitDocument(tenderId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/committee/confirm-votes")
    public ResponseEntity<Object> confirmCommitteeVotes(
            @RequestParam String tenderId,
            @RequestParam Integer committeeUserId) {
        log.info("Confirm committee votes tenderId={} userId={}", tenderId, committeeUserId);
        TenderEvaluationStatusDto status = approvalService.confirmCommitteeVotes(tenderId, committeeUserId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/chairman/vendor-vote")
    public ResponseEntity<Object> chairmanVendorVote(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @RequestParam String decision,
            @RequestParam(required = false) String remarks,
            @RequestParam Integer chairmanUserId) {
        log.info("Chairman vendor vote tenderId={} vendorId={} decision={}", tenderId, vendorId, decision);
        TenderEvaluationStatusDto status = approvalService.chairmanVendorVote(
                tenderId, vendorId, decision, remarks, chairmanUserId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/director/vendor-vote")
    public ResponseEntity<Object> directorVendorVote(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @RequestParam String decision,
            @RequestParam(required = false) String remarks,
            @RequestParam Integer directorUserId) {
        log.info("Director vendor vote tenderId={} vendorId={} decision={}", tenderId, vendorId, decision);
        TenderEvaluationStatusDto status = approvalService.directorVendorVote(
                tenderId, vendorId, decision, remarks, directorUserId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @GetMapping("/committee/vendor-vote-grid")
    public ResponseEntity<Object> getVendorVoteGrid(
            @RequestParam String tenderId,
            @RequestParam(defaultValue = "TECHNICAL") String phase) {
        log.info("Get vendor vote grid tenderId={} phase={}", tenderId, phase);
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getVendorVoteGrid(tenderId, phase)),
                HttpStatus.OK);
    }

    @PostMapping("/vendor-portal-check")
    public ResponseEntity<Object> checkVendorPortalRegistration(@RequestParam String vendorId) {
        boolean registered = approvalService.checkAndRegisterVendorOnPortal(vendorId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                Map.of("vendorId", vendorId, "registered", registered)), HttpStatus.OK);
    }

    // ─── CLARIFICATION ENDPOINTS ──────────────────────────────────────

    @PostMapping("/seek-clarification")
    public ResponseEntity<Object> seekClarification(
            @RequestParam String tenderId,
            @Valid @RequestBody SeekClarificationDto dto) {
        log.info("Seek clarification tenderId={} by role={}", tenderId, dto.getRequestedByRole());
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.seekClarification(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/respond-clarification")
    public ResponseEntity<Object> respondToClarification(
            @RequestParam String tenderId,
            @Valid @RequestBody RespondClarificationDto dto) {
        log.info("Respond to clarification tenderId={} by role={}", tenderId, dto.getRespondedByRole());
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.respondToClarification(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/confirm-by-indentor")
    public ResponseEntity<Object> confirmByIndentor(
            @RequestParam String tenderId,
            @Valid @RequestBody ConfirmByIndentorRequestDto dto) {
        log.info("Confirm by indentor tenderId={} userId={}", tenderId, dto.getIndentorUserId());
        TenderEvaluationStatusDto status = approvalService.confirmByIndentor(tenderId, dto.getIndentorUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    // ─── DIRECTOR COMMITTEE (Above 1 Crore) ──────────────────────────

    @PostMapping("/director/form-committee")
    public ResponseEntity<Object> directorFormCommittee(
            @RequestParam String tenderId,
            @Valid @RequestBody DirectorFormCommitteeDto dto) {
        log.info("Director forms committee tenderId={}", tenderId);
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.directorFormCommittee(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @GetMapping("/director/committee-members")
    public ResponseEntity<Object> getAdHocCommitteeMembers(@RequestParam String tenderId) {
        log.info("Get ad-hoc committee members tenderId={}", tenderId);
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getAdHocCommitteeMembers(tenderId)),
                HttpStatus.OK);
    }

    @PostMapping("/director/add-member")
    public ResponseEntity<Object> directorAddCommitteeMember(
            @RequestParam String tenderId,
            @RequestParam Integer userId,
            @RequestParam Integer directorUserId,
            @RequestParam(defaultValue = "MEMBER") String role) {
        log.info("Director add member tenderId={} userId={} directorUserId={} role={}", tenderId, userId, directorUserId, role);
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.directorAddCommitteeMember(tenderId, userId, directorUserId, role)),
                HttpStatus.OK);
    }

    @PostMapping("/director/confirm-committee")
    public ResponseEntity<Object> directorConfirmCommittee(
            @RequestParam String tenderId,
            @RequestParam Integer directorUserId) {
        log.info("Director confirm committee tenderId={} directorUserId={}", tenderId, directorUserId);
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.directorConfirmCommittee(tenderId, directorUserId)),
                HttpStatus.OK);
    }

    @DeleteMapping("/director/remove-member")
    public ResponseEntity<Object> directorRemoveCommitteeMember(
            @RequestParam String tenderId,
            @RequestParam Integer userId,
            @RequestParam Integer directorUserId) {
        log.info("Director remove member tenderId={} userId={} directorUserId={}", tenderId, userId, directorUserId);
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.directorRemoveCommitteeMember(tenderId, userId, directorUserId)),
                HttpStatus.OK);
    }

    // ─── QUERY ENDPOINTS ─────────────────────────────────────────────

    @GetMapping("/clarification-history")
    public ResponseEntity<Object> getClarificationHistory(@RequestParam String tenderId) {
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getClarificationHistory(tenderId)),
                HttpStatus.OK);
    }

    @GetMapping("/open-clarifications")
    public ResponseEntity<Object> getOpenClarifications(
            @RequestParam String tenderId,
            @RequestParam String vendorId) {
        log.info("Get open clarifications tenderId={} vendorId={}", tenderId, vendorId);
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getOpenClarifications(tenderId, vendorId)),
                HttpStatus.OK);
    }

    @GetMapping("/open-indentor-clarifications")
    public ResponseEntity<Object> getOpenIndentorClarifications(@RequestParam String tenderId) {
        log.info("Get open indentor clarifications tenderId={}", tenderId);
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getOpenIndentorClarifications(tenderId)),
                HttpStatus.OK);
    }

    @GetMapping("/approved-vendors")
    public ResponseEntity<Object> getApprovedVendorsForPO(@RequestParam String tenderId) {
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getApprovedVendorsForPO(tenderId)),
                HttpStatus.OK);
    }

    // ─── PER-VENDOR DECISION ENDPOINTS ───────────────────────────────

    @PostMapping("/vendor/indentor-decision")
    public ResponseEntity<Object> saveVendorIndentorDecision(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @Valid @RequestBody VendorDecisionRequestDto dto) {
        log.info("Indentor decision tenderId={} vendorId={} decision={}", tenderId, vendorId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.saveVendorIndentorDecision(
                tenderId, vendorId, dto.getDecision(), dto.getRemarks(), dto.getUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/vendor/spo-decision")
    public ResponseEntity<Object> saveVendorSpoDecision(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @Valid @RequestBody VendorDecisionRequestDto dto) {
        log.info("SPO per-vendor decision tenderId={} vendorId={} decision={}", tenderId, vendorId, dto.getDecision());
        TenderEvaluationStatusDto status = approvalService.saveVendorSpoDecision(
                tenderId, vendorId, dto.getDecision(), dto.getRemarks(), dto.getUserId());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/reject-indentor-clarification")
    public ResponseEntity<Object> rejectIndentorClarification(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @Valid @RequestBody VendorDecisionRequestDto dto) {
        log.info("SPO reject indentor clarification tenderId={} vendorId={} userId={}", tenderId, vendorId, dto.getUserId());
        TenderEvaluationStatusDto status = approvalService.rejectIndentorClarification(
                tenderId, vendorId, dto.getUserId(), dto.getRemarks());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    // ─── REJECT / REOPEN EVALUATION ──────────────────────────────────

    @PostMapping("/reject")
    public ResponseEntity<Object> rejectEvaluation(
            @RequestParam String tenderId,
            @Valid @RequestBody RejectEvaluationRequestDto dto) {
        log.info("Reject evaluation tenderId={} by role={} userId={}", tenderId, dto.getRejectedByRole(), dto.getUserId());
        TenderEvaluationStatusDto status = approvalService.rejectEvaluation(
                tenderId, dto.getRejectedByRole(), dto.getUserId(), dto.getRemarks());
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PostMapping("/reopen")
    public ResponseEntity<Object> reopenEvaluation(
            @RequestParam String tenderId,
            @RequestParam Integer userId) {
        log.info("Reopen evaluation tenderId={} userId={}", tenderId, userId);
        TenderEvaluationStatusDto status = approvalService.reopenEvaluation(tenderId, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @PutMapping("/vendor/map-registered")
    public ResponseEntity<Object> mapRegisteredVendor(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @RequestParam String registeredVendorId) {
        log.info("Map registered vendor tenderId={} vendorId={} registeredVendorId={}",
                tenderId, vendorId, registeredVendorId);
        approvalService.mapRegisteredVendor(tenderId, vendorId, registeredVendorId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Registered vendor mapped successfully"), HttpStatus.OK);
    }
}

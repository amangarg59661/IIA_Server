package com.astro.controller.ProcurementModuleController;

import com.astro.dto.workflow.ProcurementDtos.TenderEvaluationRequestDto;
import com.astro.dto.workflow.ProcurementDtos.TenderEvaluationResponseDto;
import com.astro.dto.workflow.ProcurementDtos.TenderEvaluationResponseWithBitTypeAndValueDto;
// import com.astro.dto.workflow.WorkflowTransitionDto;
import com.astro.service.TenderEvaluationService;
import com.astro.service.WorkflowService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.astro.dto.workflow.DirectorFormCommitteeDto;
import com.astro.dto.workflow.RespondClarificationDto;
import com.astro.dto.workflow.SeekClarificationDto;
import com.astro.dto.workflow.TenderCommitteeDecisionDto;
import com.astro.dto.workflow.TenderEvaluationStatusDto;
import com.astro.dto.workflow.VendorTechnicalDecisionDto;
import com.astro.service.TenderEvaluationApprovalService;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/tender-evaluation")
public class TenderEvaluationController {

    @Autowired
    private TenderEvaluationService tenderEvaluationService;

    @Autowired
    private WorkflowService workflowService;

       @Autowired
    private TenderEvaluationApprovalService approvalService;

    @PostMapping
    public ResponseEntity<Object> createTenderEvaluation(
            @RequestBody TenderEvaluationRequestDto tenderEvaluationRequestDto){

        TenderEvaluationResponseDto created = tenderEvaluationService.createTenderEvaluation(tenderEvaluationRequestDto);

       // String requestId = created.getTenderId(); // Useing the indent ID as the request ID
     //   String workflowName = "Tender Evaluator Workflow";
     //   Integer userId = created.getCreatedBy();
        //initiateing Workflow API
      //  WorkflowTransitionDto workflowTransitionDto = workflowService.initiateWorkflow(requestId, workflowName, userId);

        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(created), HttpStatus.OK);

    }

    @PutMapping
    public ResponseEntity<Object> updateTenderEvaluation(
         @RequestParam String tenderId,
            @RequestBody TenderEvaluationRequestDto tenderEvaluationRequestDto) {
        TenderEvaluationResponseDto response = tenderEvaluationService.updateTenderEvaluation(tenderId, tenderEvaluationRequestDto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<Object> getAllTenderEvaluation() {
        List<TenderEvaluationResponseDto> response = tenderEvaluationService.getAllTenderEvaluations();
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @GetMapping("/byId")
    public ResponseEntity<Object> getTenderEvaluationById(@RequestParam String tenderId) {
        TenderEvaluationResponseWithBitTypeAndValueDto response = tenderEvaluationService.getTenderEvaluationById(tenderId);
        // return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(response), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteTenderEvaluation(@RequestParam String tenderId) {
        tenderEvaluationService.deleteTenderEvaluation(tenderId);
        return ResponseEntity.ok("Tender Evaluation deleted successfully. tenderId: " + tenderId);
        // return ResponseEntity.ok("Tender Evalulation deleted successfully. projectCode:"+" " + tenderId);
    }

// ─── NEW: Full Evaluation Flow Endpoints ─────────────────────────

    /**
     * Step 1: Initiate evaluation for an approved tender.
     * Determines bid type, amount category, indent category.
     * POST /api/tender-evaluation/{tenderId}/initiate?userId=123
     */
    @PostMapping("/initiate")
    public ResponseEntity<Object> initiateEvaluation(
            @RequestParam String tenderId,
            @RequestParam Integer userId) {
        TenderEvaluationStatusDto status = approvalService.initiateTenderEvaluation(tenderId, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Get full evaluation status (respects financial bid visibility rules).
     * GET /api/tender-evaluation/{tenderId}/status?userId=123&role=Purchase+personnel
     */
    @GetMapping("/status")
    public ResponseEntity<Object> getEvaluationStatus(
            @RequestParam String tenderId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String role) {
        TenderEvaluationStatusDto status = approvalService.getEvaluationStatus(tenderId, userId, role);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Step 2 (Double Bid only): Evaluate technical bid for a specific vendor.
     * PUT /api/tender-evaluation/{tenderId}/technical/{vendorId}
     * Body: { "vendorId":"V001", "decision":"APPROVED", "remarks":"...", "evaluatedByUserId":5 }
     */
    @PutMapping("/technical")
    public ResponseEntity<Object> evaluateTechnicalBid(
            @RequestParam String tenderId,
            @RequestParam String vendorId,
            @RequestBody VendorTechnicalDecisionDto dto) {
        dto.setVendorId(vendorId);
        approvalService.evaluateTechnicalBid(tenderId, vendorId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse("Technical evaluation saved."), HttpStatus.OK);
    }

    /**
     * Step 3: Select the approved vendor after financial evaluation.
     * POST /api/tender-evaluation/{tenderId}/select-vendor
     * Body: { "vendorId":"V001", "remarks":"L1 vendor", "actionByUserId":5 }
     */
    @PostMapping("/select-vendor")
    public ResponseEntity<Object> selectApprovedVendor(
            @RequestParam String tenderId,
            @RequestBody Map<String, Object> body) {
        String vendorId = (String) body.get("vendorId");
        String remarks = (String) body.get("remarks");
        Integer userId = (Integer) body.get("actionByUserId");
        TenderEvaluationStatusDto status = approvalService.selectApprovedVendor(tenderId, vendorId, remarks, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Under 10L: Indentor or Purchase Dept approves/rejects.
     * POST /api/tender-evaluation/{tenderId}/approve/indentor-purchase
     * Body: { "decision":"APPROVED", "remarks":"...", "approverUserId":5 }
     */
    @PostMapping("/approve/indentor-purchase")
    public ResponseEntity<Object> approveByIndentorOrPurchaseDept(
            @RequestParam String tenderId,
            @RequestBody Map<String, Object> body) {
        String decision = (String) body.get("decision");
        String remarks = (String) body.get("remarks");
        Integer userId = (Integer) body.get("approverUserId");
        TenderEvaluationStatusDto status = approvalService.approveByIndentorOrPurchaseDept(tenderId, decision, remarks, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Under 10L: Store Purchase Officer final approval.
     * POST /api/tender-evaluation/{tenderId}/approve/spo
     * Body: { "decision":"APPROVED", "remarks":"...", "spoUserId":5 }
     */
    @PostMapping("/approve/spo")
    public ResponseEntity<Object> approveByStorePurchaseOfficer(
            @RequestParam String tenderId,
            @RequestBody Map<String, Object> body) {
        String decision = (String) body.get("decision");
        String remarks = (String) body.get("remarks");
        Integer userId = (Integer) body.get("spoUserId");
        TenderEvaluationStatusDto status = approvalService.approveByStorePurchaseOfficer(tenderId, decision, remarks, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Above 10L: Committee member casts vote.
     * POST /api/tender-evaluation/{tenderId}/committee/vote
     * Body: { "vote":"APPROVED", "remarks":"...", "committeeUserId":10 }
     */
    @PostMapping("/committee/vote")
    public ResponseEntity<Object> castCommitteeVote(
            @RequestParam String tenderId,
            @RequestBody Map<String, Object> body) {
        String vote = (String) body.get("vote");
        String remarks = (String) body.get("remarks");
        Integer userId = (Integer) body.get("committeeUserId");
        TenderEvaluationStatusDto status = approvalService.castCommitteeVote(tenderId, vote, remarks, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Above 10L: Chairman assigns a dynamic expert for this tender.
     * POST /api/tender-evaluation/{tenderId}/committee/expert
     * Body: { "expertUserId":15, "expertName":"Dr. XYZ", "chairmanUserId":12 }
     */
    @PostMapping("/committee/expert")
    public ResponseEntity<Object> assignExpert(
            @RequestParam String tenderId,
            @RequestBody Map<String, Object> body) {
        Integer expertUserId = (Integer) body.get("expertUserId");
        String expertName = (String) body.get("expertName");
        Integer chairmanUserId = (Integer) body.get("chairmanUserId");
        TenderEvaluationStatusDto status = approvalService.assignExpert(tenderId, expertUserId, expertName, chairmanUserId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Above 10L: Chairman gives final decision (can override committee).
     * POST /api/tender-evaluation/{tenderId}/committee/chairman-decision
     * Body: { "decision":"APPROVED", "remarks":"...", "chairmanUserId":12, "isOverride":false }
     */
    @PostMapping("/committee/chairman-decision")
    public ResponseEntity<Object> chairmanDecide(
            @RequestParam String tenderId,
            @RequestBody TenderCommitteeDecisionDto dto) {
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.chairmanDecide(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Above 10L: Director gives final approval.
     * POST /api/tender-evaluation/{tenderId}/director/approve
     * Body: { "decision":"APPROVED", "remarks":"...", "directorUserId":20 }
     */
    @PostMapping("/director/approve")
    public ResponseEntity<Object> directorApprove(
            @RequestParam String tenderId,
            @RequestBody Map<String, Object> body) {
        String decision = (String) body.get("decision");
        String remarks = (String) body.get("remarks");
        Integer userId = (Integer) body.get("directorUserId");
        TenderEvaluationStatusDto status = approvalService.directorApprove(tenderId, decision, remarks, userId);
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

    // ─── Clarification Endpoints ──────────────────────────────────────

    /**
     * Any approver seeks clarification.
     * POST /api/tender-evaluation/{tenderId}/seek-clarification
     * Body: { "requestedByRole":"SPO", "requestedByUserId":5,
     *         "clarificationTarget":"VENDOR", "remarks":"Please clarify delivery terms",
     *         "targetUserId":null, "targetUserName":null }
     *
     * clarificationTarget values:
     *   VENDOR              → vendor portal gets the clarification request
     *   INDENTOR            → goes back to Indentor
     *   PURCHASE_PERSONNEL  → goes back to Purchase Personnel
     *   SPECIFIC_MEMBER     → only that committee member re-votes
     *   ALL_MEMBERS         → all committee members re-vote
     */
    @PostMapping("/seek-clarification")
    public ResponseEntity<Object> seekClarification(
            @RequestParam String tenderId,
            @RequestBody SeekClarificationDto dto) {
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.seekClarification(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Respond to a pending clarification (vendor, indentor, purchase personnel, or member).
     * POST /api/tender-evaluation/{tenderId}/respond-clarification
     * Body: { "respondedByRole":"VENDOR", "respondedById":"V001",
     *         "responseText":"Delivery will be within 30 days", "responseFileName":"clarif_V001.pdf" }
     */
    @PostMapping("/respond-clarification")
    public ResponseEntity<Object> respondToClarification(
            @RequestParam String tenderId,
            @RequestBody RespondClarificationDto dto) {
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.respondToClarification(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * Under 10L (Proprietary/Limited Tender): Indent Creator confirms evaluation.
     * Advances status PENDING_FINANCIAL → PENDING_SPO_APPROVAL.
     * POST /api/tender-evaluation/{tenderId}/confirm-by-indentor
     * Body: { "indentorUserId": 5 }
     */
    @PostMapping("/confirm-by-indentor")
    public ResponseEntity<Object> confirmByIndentor(
            @RequestParam String tenderId,
            @RequestBody Map<String, Object> body) {
        Integer userId = (Integer) body.get("indentorUserId");
        TenderEvaluationStatusDto status = approvalService.confirmByIndentor(tenderId, userId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    // ─── Director Forms Ad-Hoc Committee (Above 1 Crore) ─────────────

    /**
     * Director forms the ad-hoc committee for tenders above ₹1 Crore.
     * POST /api/tender-evaluation/{tenderId}/director/form-committee
     * Body: { "directorUserId":20, "chairmanUserId":12, "chairmanName":"XYZ",
     *         "coChairmanUserId":13, "coChairmanName":"ABC",
     *         "members":[ {"userId":14,"memberName":"PQR","designation":"Mgr"}, ... ] }
     */
    @PostMapping("/director/form-committee")
    public ResponseEntity<Object> directorFormCommittee(
            @RequestParam String tenderId,
            @RequestBody DirectorFormCommitteeDto dto) {
        dto.setTenderId(tenderId);
        TenderEvaluationStatusDto status = approvalService.directorFormCommittee(tenderId, dto);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    /**
     * GET /api/tender-evaluation/{tenderId}/clarification-history
     * Returns all seek-clarification rounds with questions and responses.
     */
    @GetMapping("/clarification-history")
    public ResponseEntity<Object> getClarificationHistory(@RequestParam String tenderId) {
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getClarificationHistory(tenderId)),
                HttpStatus.OK);
    }

    /**
     * GET /api/tender-evaluation/{tenderId}/approved-vendors
     * Returns SPO-approved vendors for use in PO vendor dropdown.
     * Only available when evaluationStatus = APPROVED.
     * Frontend must call this instead of the general vendor list when creating a PO.
     */
    @GetMapping("/approved-vendors")
    public ResponseEntity<Object> getApprovedVendorsForPO(@RequestParam String tenderId) {
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getApprovedVendorsForPO(tenderId)),
                HttpStatus.OK);
    }

}

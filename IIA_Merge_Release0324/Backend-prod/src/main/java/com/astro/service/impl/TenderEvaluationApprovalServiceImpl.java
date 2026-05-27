package com.astro.service.impl;

import com.astro.dto.workflow.DirectorFormCommitteeDto;
import com.astro.dto.workflow.RespondClarificationDto;
import com.astro.dto.workflow.SeekClarificationDto;
import com.astro.dto.workflow.TenderCommitteeDecisionDto;
import com.astro.dto.workflow.TenderEvaluationStatusDto;
import com.astro.dto.workflow.VendorTechnicalDecisionDto;
import com.astro.entity.TechnoFinancialCommittee;
import com.astro.entity.TenderClarificationHistory;
import com.astro.entity.TenderCommitteeDecision;
import com.astro.entity.VendorLoginDetails;
import com.astro.entity.VendorMaster;
import com.astro.entity.ProcurementModule.IndentCreation;
import com.astro.entity.ProcurementModule.IndentId;
import com.astro.entity.ProcurementModule.TenderEvaluation;
import com.astro.entity.ProcurementModule.TenderRequest;
import com.astro.entity.VendorQuotationAgainstTender;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.TechnoFinancialCommitteeRepository;
import com.astro.repository.TenderClarificationHistoryRepository;
import com.astro.repository.TenderCommitteeDecisionRepository;
import com.astro.repository.VendorLoginDetailsRepository;
import com.astro.repository.VendorMasterRepository;
import com.astro.repository.VendorQuotationAgainstTenderRepository;
import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
import com.astro.repository.ProcurementModule.TenderEvaluationRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.service.TenderEvaluationApprovalService;
import com.astro.util.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TenderEvaluationApprovalServiceImpl implements TenderEvaluationApprovalService {

    private static final Logger log = LoggerFactory.getLogger(TenderEvaluationApprovalServiceImpl.class);

    private static final BigDecimal TEN_LAKH   = new BigDecimal("1000000");
    private static final BigDecimal FIFTY_LAKH = new BigDecimal("5000000");
    private static final BigDecimal ONE_CRORE  = new BigDecimal("10000000");

    private final TenderEvaluationRepository tenderEvaluationRepository;
    private final TenderRequestRepository tenderRequestRepository;
    private final VendorQuotationAgainstTenderRepository quotationRepository;
    private final TechnoFinancialCommitteeRepository committeeRepository;
    private final TenderCommitteeDecisionRepository committeeDecisionRepository;
    private final VendorMasterRepository vendorMasterRepository;
    private final VendorLoginDetailsRepository vendorLoginDetailsRepository;
    private final TenderClarificationHistoryRepository clarificationHistoryRepository;
    private final IndentCreationRepository indentCreationRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final com.astro.repository.WorkflowTransitionRepository workflowTransitionRepository;

    public TenderEvaluationApprovalServiceImpl(
            TenderEvaluationRepository tenderEvaluationRepository,
            TenderRequestRepository tenderRequestRepository,
            VendorQuotationAgainstTenderRepository quotationRepository,
            TechnoFinancialCommitteeRepository committeeRepository,
            TenderCommitteeDecisionRepository committeeDecisionRepository,
            VendorMasterRepository vendorMasterRepository,
            VendorLoginDetailsRepository vendorLoginDetailsRepository,
            TenderClarificationHistoryRepository clarificationHistoryRepository,
            IndentCreationRepository indentCreationRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            com.astro.repository.WorkflowTransitionRepository workflowTransitionRepository) {
        this.tenderEvaluationRepository = tenderEvaluationRepository;
        this.tenderRequestRepository = tenderRequestRepository;
        this.quotationRepository = quotationRepository;
        this.committeeRepository = committeeRepository;
        this.committeeDecisionRepository = committeeDecisionRepository;
        this.vendorMasterRepository = vendorMasterRepository;
        this.vendorLoginDetailsRepository = vendorLoginDetailsRepository;
        this.clarificationHistoryRepository = clarificationHistoryRepository;
        this.indentCreationRepository = indentCreationRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.workflowTransitionRepository = workflowTransitionRepository;
    }

    @Value("${filePath:}")
    private String filePath;

    // ─────────────────────────────────────────────────────────────────
    // 1. INITIATE EVALUATION
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto initiateTenderEvaluation(String tenderId, Integer initiatedByUserId) {
        TenderRequest tender = requireTender(tenderId);

        // BR_000: Tender must be fully workflow-approved before evaluation can start.
        com.astro.entity.WorkflowTransition latestTransition =
                workflowTransitionRepository.findTopByRequestIdOrderByWorkflowTransitionIdDesc(tenderId)
                        .orElseThrow(() -> new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                                "Tender " + tenderId + " has no workflow transition record. It must be fully approved first.")));
        if (!"Completed".equalsIgnoreCase(latestTransition.getStatus())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Tender " + tenderId + " is not yet approved. Current workflow status: "
                    + latestTransition.getStatus() + ". Evaluation can only begin after final approval."));
        }

        TenderEvaluation eval = tenderEvaluationRepository.findById(tenderId)
                .orElseGet(() -> {
                    TenderEvaluation e = new TenderEvaluation();
                    e.setTenderId(tenderId);
                    e.setCreatedBy(String.valueOf(initiatedByUserId));
                    return e;
                });

        // Normalize bid type first so we can apply the right sheet rule below.
        String rawBidType = tender.getBidType() != null ? tender.getBidType().trim() : "";
        boolean isDoubleBid = rawBidType.toLowerCase().contains("double");

        // BR_001: For Double Bid, comparison sheet is MANDATORY.
        //         For Single Bid, it is OPTIONAL (show confirmation popup on frontend).
        if (isDoubleBid
                && (eval.getUploadQualifiedVendorsFileName() == null
                    || eval.getUploadQualifiedVendorsFileName().trim().isEmpty())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Technical Comparison Statement must be uploaded before initiating Double Bid evaluation."));
        }

        // Normalize and persist bid type
        String bidType = isDoubleBid ? "DOUBLE_BID" : "SINGLE_BID";
        eval.setBidType(bidType);

        // Amount category
        BigDecimal totalValue = tender.getTotalTenderValue() != null
                ? tender.getTotalTenderValue() : BigDecimal.ZERO;
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Tender total value is not set or is zero. Update the tender with the correct total value before initiating evaluation."));
        }
        eval.setTotalTenderValue(totalValue);
        String amtCat;
        if (totalValue.compareTo(TEN_LAKH) < 0) {
            amtCat = "UNDER_10_LAKH";
        } else if (totalValue.compareTo(FIFTY_LAKH) < 0) {
            amtCat = "ABOVE_10_LAKH_UPTO_50_LAKH";
        } else if (totalValue.compareTo(ONE_CRORE) < 0) {
            amtCat = "ABOVE_50_LAKH_UPTO_1_CRORE";
        } else {
            amtCat = "ABOVE_1_CRORE";
        }
        eval.setAmountCategory(amtCat);

        // Indent category
        int indentCount = tender.getIndentIds() != null ? tender.getIndentIds().size() : 0;
        eval.setIndentCategory(indentCount > 1 ? "MULTIPLE_INDENT" : "SINGLE_INDENT");

        // Reset clarification state on fresh initiation
        eval.setFinancialBidPhase(false);
        eval.setClarificationPendingFrom(null);
        eval.setPreviousEvaluationStatus(null);

        String indentCat = eval.getIndentCategory();

        // Set bid visibility and initial status
        if ("SINGLE_BID".equalsIgnoreCase(bidType)) {
            // Single bid: all vendors go directly to financial evaluation
            List<VendorQuotationAgainstTender> quotations =
                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
            quotations.forEach(q -> {
                q.setFinancialBidVisible(true);
                q.setTechnicalStatus("APPROVED");
            });
            quotationRepository.saveAll(quotations);
            eval.setEvaluationStatus("PENDING_FINANCIAL");
        } else if ("MULTIPLE_INDENT".equals(indentCat)) {
            // Double Bid + Multiple Indent (Case 4): technical phase first, PP acts as evaluator.
            List<VendorQuotationAgainstTender> quotations =
                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
            quotations.forEach(q -> {
                if (q.getTechnicalStatus() == null || q.getTechnicalStatus().isEmpty()) {
                    q.setTechnicalStatus("PENDING");
                }
                q.setFinancialBidVisible(false);
            });
            quotationRepository.saveAll(quotations);
            eval.setEvaluationStatus("PENDING_TECHNICAL");
        } else {
            // Double Bid + Single Indent (Case 2): technical phase first
            List<VendorQuotationAgainstTender> doubleQuotations =
                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
            doubleQuotations.forEach(q -> {
                if (q.getTechnicalStatus() == null || q.getTechnicalStatus().isEmpty()) {
                    q.setTechnicalStatus("PENDING");
                }
                q.setFinancialBidVisible(false);
            });
            quotationRepository.saveAll(doubleQuotations);
            eval.setEvaluationStatus("PENDING_TECHNICAL");
        }

        eval.setUpdatedDate(LocalDateTime.now());
        eval.setInitiated(1);   // lock vendor uploads for non-bidders from this point
        tenderEvaluationRepository.save(eval);

        // Pre-create committee vote rows for STEC-I / STEC-II
        boolean needsCommittee = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat)
                || "ABOVE_50_LAKH_UPTO_1_CRORE".equals(amtCat);
        if (needsCommittee && !committeeDecisionRepository.existsByTenderId(tenderId)) {
            String committeeType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) ? "STEC_I" : "STEC_II";
            List<TechnoFinancialCommittee> members =
                    committeeRepository.findByCommitteeTypeAndIsActiveTrue(committeeType);
            members.stream()
                    .filter(m -> !"CHAIRMAN".equalsIgnoreCase(m.getRole()))
                    .forEach(m -> {
                        TenderCommitteeDecision row = new TenderCommitteeDecision();
                        row.setTenderId(tenderId);
                        row.setCommitteeUserId(m.getUserId());
                        row.setCommitteeMemberName(m.getMemberName());
                        row.setCreatedDate(LocalDateTime.now());
                        row.setUpdatedDate(LocalDateTime.now());
                        committeeDecisionRepository.save(row);
                    });
        }

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 2. GET EVALUATION STATUS
    // ─────────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    @Override
    public TenderEvaluationStatusDto getEvaluationStatus(String tenderId,
                                                          Integer requestingUserId,
                                                          String requestingRole) {
        // If evaluation has not been initiated yet, return a minimal DTO with null evaluationStatus.
        // Frontend checks !selectedEval.evaluationStatus to show the Initiate button, so null is correct.
        java.util.Optional<TenderEvaluation> evalOpt = tenderEvaluationRepository.findById(tenderId);
        if (evalOpt.isEmpty()) {
            TenderEvaluationStatusDto notInitiated = new TenderEvaluationStatusDto();
            notInitiated.setTenderId(tenderId);
            notInitiated.setEvaluationStatus(null); // null → frontend shows "Initiate" button
            return notInitiated;
        }
        TenderEvaluation eval = evalOpt.get();
        TenderRequest tender = requireTender(tenderId);
        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 3. EVALUATE TECHNICAL BID (Double Bid only)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public void evaluateTechnicalBid(String tenderId, String vendorId,
                                     VendorTechnicalDecisionDto dto) {
        TenderEvaluation eval = requireEval(tenderId);

        if (!"DOUBLE_BID".equalsIgnoreCase(eval.getBidType())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Technical bid evaluation is only applicable for DOUBLE_BID tenders."));
        }

        VendorQuotationAgainstTender quotation = quotationRepository
                .findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "No quotation found for vendor " + vendorId + " in tender " + tenderId)));

        quotation.setTechnicalStatus(dto.getDecision().toUpperCase());
        quotation.setTechnicalRemarks(dto.getRemarks());
        quotation.setTechnicalEvaluatedBy(dto.getEvaluatedByUserId());
        quotation.setTechnicalEvaluatedDate(LocalDateTime.now());
        // Financial bid visibility is controlled by SPO (under-10L) or Director (above-10L),
        // not by the technical evaluator. Keep financialBidVisible as-is here.
        quotationRepository.save(quotation);

        // If all vendors evaluated → move to PENDING_FINANCIAL
        List<VendorQuotationAgainstTender> allLatest =
                quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
        boolean allEvaluated = allLatest.stream()
                .noneMatch(q -> "PENDING".equalsIgnoreCase(q.getTechnicalStatus()));

        if (allEvaluated) {
            eval.setEvaluationStatus("PENDING_FINANCIAL");
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 4. SELECT APPROVED VENDOR
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto selectApprovedVendor(String tenderId,
                                                           String approvedVendorId,
                                                           String remarks,
                                                           Integer actionByUserId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        VendorMaster vendor = vendorMasterRepository.findById(approvedVendorId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "Vendor not found: " + approvedVendorId)));

        eval.setApprovedVendorId(approvedVendorId);
        eval.setApprovedVendorName(vendor.getVendorName());
        eval.setApprovalRemarks(remarks);

        String amtCat = eval.getAmountCategory();
        String indentCat = eval.getIndentCategory();
        String bidType = eval.getBidType();

        if ("ABOVE_1_CRORE".equals(amtCat)) {
            // Director must form ad-hoc committee first
            eval.setEvaluationStatus("PENDING_COMMITTEE_FORMATION");
        } else if ("UNDER_10_LAKH".equals(amtCat) && "MULTIPLE_INDENT".equals(indentCat)) {
            // Case 3 & 4: Multiple indent, skip indentor, go directly to SPO
            eval.setEvaluationStatus("PENDING_SPO_APPROVAL");
        } else {
            // Under 10L single indent OR above 10L: → PENDING_APPROVAL (Indentor or Committee)
            eval.setEvaluationStatus("PENDING_APPROVAL");
        }

        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 5. APPROVE BY INDENTOR OR PURCHASE DEPT (Under 10L)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto approveByIndentorOrPurchaseDept(String tenderId,
                                                                      String decision,
                                                                      String remarks,
                                                                      Integer approverUserId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        if (!"UNDER_10_LAKH".equals(eval.getAmountCategory())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "This approval step is only for UNDER_10_LAKH tenders."));
        }

        if ("APPROVED".equalsIgnoreCase(decision)) {
            eval.setEvaluationStatus("PENDING_SPO_APPROVAL");
        } else if ("REJECTED".equalsIgnoreCase(decision)) {
            eval.setEvaluationStatus("REJECTED");
        } else {
            // SEEK_CLARIFICATION handled via seekClarification endpoint
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Invalid decision. Use APPROVED or REJECTED. For clarification use /seek-clarification endpoint."));
        }
        eval.setApprovalRemarks(remarks);
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 6. APPROVE BY STORE PURCHASE OFFICER (Under 10L)
    //    FIX: Handles Double Bid financial round (Cases 2 & 4)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto approveByStorePurchaseOfficer(String tenderId,
                                                                    String decision,
                                                                    String remarks,
                                                                    Integer spoUserId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        boolean isDoubleBid = "DOUBLE_BID".equalsIgnoreCase(eval.getBidType());
        boolean isFinancialPhase = Boolean.TRUE.equals(eval.getFinancialBidPhase());

        validateAllSpoDecided(tenderId, isDoubleBid && isFinancialPhase);

        if ("APPROVED".equalsIgnoreCase(decision)) {
            if (isDoubleBid && !isFinancialPhase) {
                // SPO approved TECHNICAL evaluation of double-bid tender.
                // Unlock financial bids and reset nextRole to INDENTOR so Accept button is enabled.
                List<VendorQuotationAgainstTender> quotations =
                        quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
                quotations.stream()
                        .filter(q -> "APPROVED".equalsIgnoreCase(q.getTechnicalStatus()))
                        .forEach(q -> {
                            q.setFinancialBidVisible(true);
                            q.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
                            q.setStatus("SUBMITTED");
                        });
                quotationRepository.saveAll(quotations);

                eval.setFinancialBidPhase(true);
                // Route back to PENDING_FINANCIAL_SHEET_UPLOAD so PP/Indentor can upload sheet first
                eval.setEvaluationStatus("PENDING_FINANCIAL_SHEET_UPLOAD");
            } else {
                // Final SPO approval (either single bid or double bid financial phase)
                boolean wasFinancialPhase = Boolean.TRUE.equals(eval.getFinancialBidPhase());
                eval.setEvaluationStatus("APPROVED");
                eval.setFinancialBidPhase(false);

                // Guard: all Indentor-accepted vendors must have an SPO decision before final approval
                List<VendorQuotationAgainstTender> allQuotations =
                        quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
                boolean anyPendingSpo = allQuotations.stream()
                        .filter(q -> {
                            if (wasFinancialPhase) {
                                return "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus())
                                    && "ACCEPTED".equalsIgnoreCase(q.getFinancialIndentorStatus());
                            }
                            return "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus());
                        })
                        .anyMatch(q -> {
                            String spSt = wasFinancialPhase ? q.getFinancialSpoStatus() : q.getSpoStatus();
                            return spSt == null ||
                                (!"ACCEPTED".equalsIgnoreCase(spSt) &&
                                 !"REJECTED".equalsIgnoreCase(spSt));
                        });
                if (anyPendingSpo) {
                    throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                            "All vendors accepted by the Indentor must be either Accepted or Rejected by SPO before final submission."));
                }

                // Mark vendors as Completed only if accepted in ALL rounds (technical + financial for double bid)
                allQuotations.forEach(q -> {
                    q.setNextRole(null);
                    if (wasFinancialPhase) {
                        if ("ACCEPTED".equalsIgnoreCase(q.getSpoStatus())
                                && "ACCEPTED".equalsIgnoreCase(q.getFinancialSpoStatus())) {
                            q.setStatus("Completed");
                            q.setAcceptanceStatus("ACCEPTED");
                        }
                    } else {
                        if ("ACCEPTED".equalsIgnoreCase(q.getSpoStatus())) {
                            q.setStatus("Completed");
                            q.setAcceptanceStatus("ACCEPTED");
                        }
                    }
                });
                quotationRepository.saveAll(allQuotations);

                // Set approvedVendorId on eval record (first fully-accepted vendor, for backward compat)
                if (eval.getApprovedVendorId() == null) {
                    allQuotations.stream()
                            .filter(q -> "Completed".equals(q.getStatus()))
                            .findFirst()
                            .ifPresent(q -> {
                                eval.setApprovedVendorId(q.getVendorId());
                                vendorMasterRepository.findById(q.getVendorId())
                                        .ifPresent(vm -> eval.setApprovedVendorName(vm.getVendorName()));
                            });
                }
                if (eval.getApprovedVendorId() != null) {
                    boolean registered = checkAndRegisterVendorOnPortal(eval.getApprovedVendorId());
                    eval.setVendorPortalRegistered(registered);
                }
            }
        } else if ("REJECTED".equalsIgnoreCase(decision)) {
            eval.setEvaluationStatus("REJECTED");
            eval.setFinancialBidPhase(false);
        } else {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Invalid decision. Use APPROVED or REJECTED. For clarification use /seek-clarification endpoint."));
        }
        eval.setApprovalRemarks(remarks);
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 7. CAST COMMITTEE VOTE (Above 10L)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto castCommitteeVote(String tenderId, String vote,
                                                        String remarks, Integer committeeUserId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        TenderCommitteeDecision decision = committeeDecisionRepository
                .findByTenderIdAndCommitteeUserId(tenderId, committeeUserId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "User " + committeeUserId + " is not a committee member for this tender.")));

        decision.setVote(vote.toUpperCase());
        decision.setVoteRemarks(remarks);
        decision.setVotedDate(LocalDateTime.now());
        decision.setUpdatedDate(LocalDateTime.now());
        committeeDecisionRepository.save(decision);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 8. ASSIGN EXPERT (Above 10L - Chairman only)
    //    FIX: Creates a separate vote row for the expert so they can vote
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto assignExpert(String tenderId, Integer expertUserId,
                                                   String expertName, Integer chairmanUserId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        // Store expert info on the chairman row
        TenderCommitteeDecision chairRow = committeeDecisionRepository
                .findByTenderIdAndCommitteeUserId(tenderId, chairmanUserId)
                .orElseGet(() -> {
                    TenderCommitteeDecision r = new TenderCommitteeDecision();
                    r.setTenderId(tenderId);
                    r.setCommitteeUserId(chairmanUserId);
                    r.setCreatedDate(LocalDateTime.now());
                    return r;
                });
        chairRow.setExpertUserId(expertUserId);
        chairRow.setExpertName(expertName);
        chairRow.setExpertAssignedDate(LocalDateTime.now());
        chairRow.setUpdatedDate(LocalDateTime.now());
        committeeDecisionRepository.save(chairRow);

        // Create a dedicated vote row for the expert (if not already present)
        boolean expertVoteRowExists = committeeDecisionRepository
                .findByTenderIdAndCommitteeUserId(tenderId, expertUserId).isPresent();
        if (!expertVoteRowExists) {
            TenderCommitteeDecision expertRow = new TenderCommitteeDecision();
            expertRow.setTenderId(tenderId);
            expertRow.setCommitteeUserId(expertUserId);
            expertRow.setCommitteeMemberName(expertName + " (Expert)");
            expertRow.setCreatedDate(LocalDateTime.now());
            expertRow.setUpdatedDate(LocalDateTime.now());
            committeeDecisionRepository.save(expertRow);
        }

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 9. CHAIRMAN DECISION (Above 10L)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto chairmanDecide(String tenderId,
                                                     TenderCommitteeDecisionDto dto) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        validateAllVendorsDecided(tenderId);

        // Validate chairman identity for STEC-I / STEC-II
        if ("ABOVE_10_LAKH_UPTO_50_LAKH".equals(eval.getAmountCategory())
                || "ABOVE_50_LAKH_UPTO_1_CRORE".equals(eval.getAmountCategory())) {
            String expectedType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(eval.getAmountCategory())
                    ? "STEC_I" : "STEC_II";
            TechnoFinancialCommittee configuredChair = committeeRepository
                    .findByRoleAndCommitteeTypeAndIsActiveTrue("CHAIRMAN", expectedType)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(400, 1, "CONFIGURATION_ERROR",
                            "No active Chairman configured for " + expectedType)));
            if (!configuredChair.getUserId().equals(dto.getChairmanUserId())) {
                throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "Only the " + expectedType + " Chairman (" + configuredChair.getMemberName()
                        + ") can make this decision."));
            }
        }
        // For ABOVE_1_CRORE ad-hoc: validate against adHocChairmanUserId
        if ("ABOVE_1_CRORE".equals(eval.getAmountCategory()) && eval.getAdHocChairmanUserId() != null) {
            if (!eval.getAdHocChairmanUserId().equals(dto.getChairmanUserId())) {
                throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "Only the designated ad-hoc Chairman can make this decision."));
            }
        }

        TenderCommitteeDecision chairRow = committeeDecisionRepository
                .findByTenderIdAndCommitteeUserId(tenderId, dto.getChairmanUserId())
                .orElseGet(() -> {
                    TenderCommitteeDecision r = new TenderCommitteeDecision();
                    r.setTenderId(tenderId);
                    r.setCommitteeUserId(dto.getChairmanUserId());
                    r.setCreatedDate(LocalDateTime.now());
                    return r;
                });

        chairRow.setChairmanDecision(dto.getDecision().toUpperCase());
        chairRow.setChairmanRemarks(dto.getRemarks());
        chairRow.setChairmanOverrideUsed(Boolean.TRUE.equals(dto.getIsOverride()));
        chairRow.setChairmanDecisionDate(LocalDateTime.now());
        chairRow.setUpdatedDate(LocalDateTime.now());
        committeeDecisionRepository.save(chairRow);

        if ("APPROVED".equalsIgnoreCase(dto.getDecision())
                || "OVERRIDE".equalsIgnoreCase(dto.getDecision())) {
            eval.setEvaluationStatus("PENDING_DIRECTOR_APPROVAL");
        } else {
            eval.setEvaluationStatus("REJECTED");
        }
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 10. DIRECTOR APPROVAL (Above 10L final step)
    //     FIX: Also handles double-bid financial phase for above-10L
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto directorApprove(String tenderId, String decision,
                                                      String remarks, Integer directorUserId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        validateAllVendorsDecided(tenderId);

        TenderCommitteeDecision dirRow = committeeDecisionRepository
                .findByTenderIdAndCommitteeUserId(tenderId, directorUserId)
                .orElseGet(() -> {
                    TenderCommitteeDecision r = new TenderCommitteeDecision();
                    r.setTenderId(tenderId);
                    r.setCommitteeUserId(directorUserId);
                    r.setCreatedDate(LocalDateTime.now());
                    return r;
                });

        dirRow.setDirectorDecision(decision.toUpperCase());
        dirRow.setDirectorRemarks(remarks);
        dirRow.setDirectorDecisionDate(LocalDateTime.now());
        dirRow.setDirectorUserId(directorUserId);
        dirRow.setUpdatedDate(LocalDateTime.now());
        committeeDecisionRepository.save(dirRow);

        boolean isDoubleBid = "DOUBLE_BID".equalsIgnoreCase(eval.getBidType());
        boolean isFinancialPhase = Boolean.TRUE.equals(eval.getFinancialBidPhase());

        if ("APPROVED".equalsIgnoreCase(decision) || "OVERRIDE".equalsIgnoreCase(decision)) {
            if (isDoubleBid && !isFinancialPhase) {
                // Director approved technical phase of double-bid above-10L tender.
                // Unlock financial bids and reset nextRole to INDENTOR so Accept button is enabled.
                List<VendorQuotationAgainstTender> quotations =
                        quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
                quotations.stream()
                        .filter(q -> "APPROVED".equalsIgnoreCase(q.getTechnicalStatus()))
                        .forEach(q -> {
                            q.setFinancialBidVisible(true);
                            q.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
                            q.setStatus("SUBMITTED");
                        });
                quotationRepository.saveAll(quotations);

                eval.setFinancialBidPhase(true);
                // Go back to PENDING_FINANCIAL so PP can select the approved vendor
                // (from technically-approved list) before committee votes on financial bids.
                eval.setEvaluationStatus("PENDING_FINANCIAL");
                // Reset committee votes for the financial phase
                List<TenderCommitteeDecision> existingVotes =
                        committeeDecisionRepository.findByTenderId(tenderId);
                existingVotes.stream()
                        .filter(v -> v.getCommitteeMemberName() != null && v.getVote() != null)
                        .forEach(v -> {
                            v.setVote(null);
                            v.setVoteRemarks(null);
                            v.setVotedDate(null);
                            v.setUpdatedDate(LocalDateTime.now());
                        });
                committeeDecisionRepository.saveAll(existingVotes);
            } else {
                eval.setEvaluationStatus("APPROVED");
                eval.setFinancialBidPhase(false);
                if (eval.getApprovedVendorId() != null) {
                    boolean registered = checkAndRegisterVendorOnPortal(eval.getApprovedVendorId());
                    eval.setVendorPortalRegistered(registered);
                    markApprovedVendorCompleted(tenderId, eval.getApprovedVendorId());
                }
            }
        } else {
            eval.setEvaluationStatus("REJECTED");
            eval.setFinancialBidPhase(false);
        }
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 11. VENDOR PORTAL REGISTRATION CHECK
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public boolean checkAndRegisterVendorOnPortal(String vendorId) {
        Optional<VendorLoginDetails> existing =
                vendorLoginDetailsRepository.findByVendorId(vendorId);
        if (existing.isPresent()) return true;

        Optional<VendorMaster> vendorOpt = vendorMasterRepository.findById(vendorId);
        if (vendorOpt.isEmpty()) return false;

        VendorMaster vendor = vendorOpt.get();
        String tempPassword = generateTempPassword();

        VendorLoginDetails login = new VendorLoginDetails();
        login.setVendorId(vendorId);
        login.setEmailAddress(vendor.getEmailAddress());
        login.setPassword(passwordEncoder.encode(tempPassword));
        login.setIsFirstLogin(true);
        login.setIsTempPassword(true);
        login.setCreatedDate(LocalDateTime.now());
        vendorLoginDetailsRepository.save(login);

        try {
            emailService.sendVendorPortalCredentials(vendor.getEmailAddress(),
                    vendor.getVendorName(), vendorId, tempPassword);
        } catch (Exception ignored) {
        }
        return true;
    }

    // ─────────────────────────────────────────────────────────────────
    // 12. SEEK CLARIFICATION (any approver)
    //     Supports: VENDOR (individual), ALL_VENDORS (bulk),
    //               INDENTOR, PURCHASE_PERSONNEL, SPECIFIC_MEMBER, ALL_MEMBERS
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto seekClarification(String tenderId, SeekClarificationDto dto) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        String originalTarget = dto.getClarificationTarget();

        // Auto-route based on role + amountCategory + modeOfProcurement
        String target = resolveClarificationTarget(originalTarget, dto, eval, tender);

        if (target == null || target.isBlank()) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "clarificationTarget must be specified: VENDOR, ALL_VENDORS, INDENTOR, PURCHASE_PERSONNEL, SPECIFIC_MEMBER, or ALL_MEMBERS"));
        }

        // GEM/OPEN/GLOBAL: original target was VENDOR/ALL_VENDORS but rerouted to PURCHASE_PERSONNEL.
        // Mark vendor quotations CHANGE_REQUESTED so PP knows which vendors need clarification.
        boolean reroutedVendorToPP = "PURCHASE_PERSONNEL".equalsIgnoreCase(target)
                && (originalTarget != null)
                && ("VENDOR".equalsIgnoreCase(originalTarget) || "ALL_VENDORS".equalsIgnoreCase(originalTarget));

        if (reroutedVendorToPP) {
            if ("VENDOR".equalsIgnoreCase(originalTarget)) {
                String specificVendorId = dto.getTargetVendorId();
                if (specificVendorId == null || specificVendorId.isBlank()) {
                    specificVendorId = eval.getApprovedVendorId();
                }
                if (specificVendorId != null) {
                    final String vid = specificVendorId;
                    quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vid)
                            .ifPresent(q -> {
                                q.setStatus("CHANGE_REQUESTED");
                                q.setRemarks(dto.getRemarks());
                                quotationRepository.save(q);
                            });
                }
            } else {
                List<VendorQuotationAgainstTender> allQuotations =
                        quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
                allQuotations.forEach(q -> {
                    q.setStatus("CHANGE_REQUESTED");
                    q.setRemarks(dto.getRemarks());
                });
                quotationRepository.saveAll(allQuotations);
            }
        }

        // Save where to return after clarification is received
        eval.setPreviousEvaluationStatus(eval.getEvaluationStatus());
        eval.setClarificationPendingFrom(target.toUpperCase());
        eval.setClarificationPendingFromId(dto.getTargetUserId());
        eval.setClarificationPendingFromName(dto.getTargetUserName());
        eval.setClarificationRequestedByRole(dto.getRequestedByRole());
        eval.setClarificationRemarks(dto.getRemarks());
        // Store vendor context for INDENTOR/PP clarifications (null if general/tender-level)
        eval.setClarificationTargetVendorId(dto.getTargetVendorId());

        switch (target.toUpperCase()) {
            case "VENDOR":
                // Mark a specific vendor's quotation as CHANGE_REQUESTED
                eval.setEvaluationStatus("PENDING_VENDOR_CLARIFICATION");
                String specificVendorId = dto.getTargetVendorId();
                if (specificVendorId == null || specificVendorId.isBlank()) {
                    specificVendorId = eval.getApprovedVendorId(); // fallback for single-vendor flows
                }
                if (specificVendorId != null) {
                    final String vid = specificVendorId;
                    quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vid)
                            .ifPresent(q -> {
                                q.setStatus("CHANGE_REQUESTED");
                                q.setRemarks(dto.getRemarks());
                                applyClarificationRoleFields(q, dto, eval);
                                quotationRepository.save(q);
                            });
                }
                break;
            case "ALL_VENDORS":
                // Mark ALL vendor quotations as CHANGE_REQUESTED (bulk clarification)
                eval.setEvaluationStatus("PENDING_VENDOR_CLARIFICATION");
                List<VendorQuotationAgainstTender> allQuotations =
                        quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
                allQuotations.forEach(q -> {
                    q.setStatus("CHANGE_REQUESTED");
                    q.setRemarks(dto.getRemarks());
                    applyClarificationRoleFields(q, dto, eval);
                });
                quotationRepository.saveAll(allQuotations);
                break;
            case "INDENTOR":
            case "PURCHASE_PERSONNEL":
            case "CHAIRMAN":
                eval.setEvaluationStatus("PENDING_INDENTOR_CLARIFICATION");
                // SPO seeking clarification from indentor for a specific vendor: reset indentor decision
                if ("SPO".equalsIgnoreCase(dto.getRequestedByRole())
                        && dto.getTargetVendorId() != null && !dto.getTargetVendorId().isBlank()) {
                    final String clarVid = dto.getTargetVendorId();
                    quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, clarVid)
                            .ifPresent(q -> {
                                if (Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
                                    q.setFinancialIndentorStatus(null);
                                    q.setFinancialIndentorRemarks(null);
                                } else {
                                    q.setIndentorStatus(null);
                                    q.setIndentorRemarks(null);
                                }
                                q.setStatus("CHANGE_REQUESTED");
                                q.setRemarks(dto.getRemarks());
                                q.setUpdatedDate(LocalDateTime.now());
                                quotationRepository.save(q);
                            });
                }
                break;
            case "SPECIFIC_MEMBER":
            case "ALL_MEMBERS":
                eval.setEvaluationStatus("PENDING_MEMBER_REVOTE");
                if ("ALL_MEMBERS".equalsIgnoreCase(target)) {
                    List<TenderCommitteeDecision> votes =
                            committeeDecisionRepository.findByTenderId(tenderId);
                    votes.stream()
                            .filter(v -> v.getCommitteeMemberName() != null)
                            .forEach(v -> {
                                v.setVote(null);
                                v.setVoteRemarks(null);
                                v.setVotedDate(null);
                                v.setUpdatedDate(LocalDateTime.now());
                            });
                    committeeDecisionRepository.saveAll(votes);
                }
                if ("SPECIFIC_MEMBER".equalsIgnoreCase(target) && dto.getTargetUserId() != null) {
                    committeeDecisionRepository
                            .findByTenderIdAndCommitteeUserId(tenderId, dto.getTargetUserId())
                            .ifPresent(v -> {
                                v.setVote(null);
                                v.setVoteRemarks(null);
                                v.setVotedDate(null);
                                v.setUpdatedDate(LocalDateTime.now());
                                committeeDecisionRepository.save(v);
                            });
                }
                break;
            default:
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Unknown clarification target: " + target));
        }

        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        // Save to clarification history (table may not exist on first deploy; non-fatal)
        try {
              clarificationHistoryRepository
                .findByTenderIdOrderByRequestedAtDesc(tenderId)
                .stream()
                .filter(h -> h.getRespondedAt() == null
                        && target.equalsIgnoreCase(h.getClarificationTarget())
                        && Objects.equals(dto.getTargetVendorId(), h.getTargetVendorId()))
                .forEach(h -> {
                    h.setRespondedAt(LocalDateTime.now());
                    h.setResponseText("[Superseded by new clarification round]");
                    clarificationHistoryRepository.save(h);
                });

            int nextRound = clarificationHistoryRepository.findMaxRoundByTenderId(tenderId) + 1;

            if (reroutedVendorToPP) {
                // GEM/OPEN/GLOBAL: create per-vendor history records so PP responses map cleanly
                List<VendorQuotationAgainstTender> markedQuotations =
                        quotationRepository.findByTenderIdAndIsLatestTrue(tenderId).stream()
                        .filter(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()))
                        .collect(Collectors.toList());
                for (VendorQuotationAgainstTender q : markedQuotations) {
                    TenderClarificationHistory history = new TenderClarificationHistory();
                    history.setTenderId(tenderId);
                    history.setRoundNumber(nextRound);
                    history.setRequestedByRole(dto.getRequestedByRole());
                    history.setRequestedByUserId(dto.getRequestedByUserId());
                    history.setClarificationTarget("PURCHASE_PERSONNEL");
                    history.setTargetVendorId(q.getVendorId());
                    history.setTargetUserId(dto.getTargetUserId());
                    history.setTargetUserName(dto.getTargetUserName());
                    history.setQuestionRemarks(dto.getRemarks());
                    history.setRequestedAt(LocalDateTime.now());
                    clarificationHistoryRepository.save(history);
                }
            } else {
                TenderClarificationHistory history = new TenderClarificationHistory();
                history.setTenderId(tenderId);
                history.setRoundNumber(nextRound);
                history.setRequestedByRole(dto.getRequestedByRole());
                history.setRequestedByUserId(dto.getRequestedByUserId());
                history.setClarificationTarget(target.toUpperCase());
                history.setTargetVendorId(dto.getTargetVendorId());
                history.setTargetUserId(dto.getTargetUserId());
                history.setTargetUserName(dto.getTargetUserName());
                history.setQuestionRemarks(dto.getRemarks());
                history.setRequestedAt(LocalDateTime.now());
                clarificationHistoryRepository.save(history);
            }
        } catch (Exception e) {
            log.warn("Clarification history save failed (restart backend if table missing): {}", e.getMessage());
        }

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPER: set role-specific status fields on quotation during clarification
    // ─────────────────────────────────────────────────────────────────
    private void applyClarificationRoleFields(VendorQuotationAgainstTender q,
                                               SeekClarificationDto dto,
                                               TenderEvaluation eval) {
        q.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.VENDOR);
        q.setUpdatedBy(dto.getRequestedByUserId() != null ? String.valueOf(dto.getRequestedByUserId()) : q.getUpdatedBy());
        q.setUpdatedDate(LocalDateTime.now());

        boolean financialPhase = Boolean.TRUE.equals(eval.getFinancialBidPhase());
        String role = dto.getRequestedByRole();
        if ("SPO".equalsIgnoreCase(role) || "Store Purchase Officer".equalsIgnoreCase(role)) {
            if (financialPhase) {
                q.setFinancialSpoStatus("CHANGE_REQUESTED");
                q.setFinancialSpoRemarks(dto.getRemarks());
            } else {
                q.setSpoStatus("CHANGE_REQUESTED");
                q.setSpoRemarks(dto.getRemarks());
            }
            q.setCurrentRole(VendorQuotationAgainstTender.WorkflowActorRole.STORE_PURCHASE_OFFICER);
        } else {
            if (financialPhase) {
                q.setFinancialIndentorStatus("CHANGE_REQUESTED");
                q.setFinancialIndentorRemarks(dto.getRemarks());
            } else {
                q.setIndentorStatus("CHANGE_REQUESTED");
                q.setIndentorRemarks(dto.getRemarks());
            }
            q.setCurrentRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // 13. RESPOND TO CLARIFICATION
    //     For VENDOR responses: only restores eval status when ALL
    //     CHANGE_REQUESTED vendor quotations have been responded to.
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto respondToClarification(String tenderId,
                                                             RespondClarificationDto dto) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        String currentStatus = eval.getEvaluationStatus();
        if (!"PENDING_VENDOR_CLARIFICATION".equals(currentStatus)
                && !"PENDING_INDENTOR_CLARIFICATION".equals(currentStatus)
                && !"PENDING_MEMBER_REVOTE".equals(currentStatus)) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Tender is not pending any clarification response. Current status: " + currentStatus));
        }

        String respondedByRole = dto.getRespondedByRole();

        if ("VENDOR".equalsIgnoreCase(respondedByRole)) {
            String vendorId = dto.getRespondedById();
            quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                    .ifPresent(q -> {
                        q.setVendorResponse(dto.getResponseText());
                        if (dto.getResponseFileName() != null) {
                            q.setClarificationFileName(dto.getResponseFileName());
                        }
                        q.setStatus("CHANGE_RESPONDED");
                        q.setUpdatedDate(LocalDateTime.now());
                        quotationRepository.save(q);
                    });

            // Update clarification history — PK-based when available, filtered fallback otherwise
            try {
                Optional<TenderClarificationHistory> historyRow;
                if (dto.getClarificationHistoryId() != null) {
                    historyRow = clarificationHistoryRepository.findById(dto.getClarificationHistoryId())
                            .filter(h -> tenderId.equals(h.getTenderId()) && h.getRespondedAt() == null);
                } else {
                    historyRow = clarificationHistoryRepository
                            .findByTenderIdOrderByRequestedAtDesc(tenderId).stream()
                            .filter(h -> h.getRespondedAt() == null
                                    && ("VENDOR".equals(h.getClarificationTarget())
                                        || "ALL_VENDORS".equals(h.getClarificationTarget()))
                                    && (vendorId.equals(h.getTargetVendorId())
                                        || h.getTargetVendorId() == null))
                            .findFirst();
                }
                historyRow.ifPresent(h -> {
                    h.setResponseText(dto.getResponseText());
                    h.setResponseFileName(dto.getResponseFileName());
                    h.setRespondedByRole(dto.getRespondedByRole());
                    h.setRespondedById(dto.getRespondedById());
                    h.setRespondedAt(LocalDateTime.now());
                    clarificationHistoryRepository.save(h);
                });
            } catch (Exception e) {
                log.warn("Clarification history update failed: {}", e.getMessage());
            }

        } else if ("PURCHASE_PERSONNEL".equalsIgnoreCase(respondedByRole)
                    && dto.getVendorId() != null && !dto.getVendorId().isBlank()) {
            // PP responding per-vendor on behalf of vendor (GEM/OPEN/GLOBAL mode)
            String ppVendorId = dto.getVendorId();
            quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, ppVendorId)
                    .ifPresent(q -> {
                        q.setVendorResponse(dto.getResponseText());
                        if (dto.getResponseFileName() != null) {
                            q.setClarificationFileName(dto.getResponseFileName());
                        }
                        q.setStatus("CHANGE_RESPONDED");
                        q.setUpdatedDate(LocalDateTime.now());
                        quotationRepository.save(q);
                    });

            // Update clarification history — PK-based when available, filtered fallback otherwise
            try {
                Optional<TenderClarificationHistory> historyRow;
                if (dto.getClarificationHistoryId() != null) {
                    historyRow = clarificationHistoryRepository.findById(dto.getClarificationHistoryId())
                            .filter(h -> tenderId.equals(h.getTenderId()) && h.getRespondedAt() == null);
                } else {
                    historyRow = clarificationHistoryRepository
                            .findByTenderIdOrderByRequestedAtDesc(tenderId).stream()
                            .filter(h -> h.getRespondedAt() == null
                                    && "PURCHASE_PERSONNEL".equals(h.getClarificationTarget())
                                    && ppVendorId.equals(h.getTargetVendorId()))
                            .findFirst();
                }
                historyRow.ifPresent(h -> {
                    h.setResponseText(dto.getResponseText());
                    h.setResponseFileName(dto.getResponseFileName());
                    h.setRespondedByRole("PURCHASE_PERSONNEL");
                    h.setRespondedById(dto.getRespondedById());
                    h.setRespondedAt(LocalDateTime.now());
                    clarificationHistoryRepository.save(h);
                });
            } catch (Exception e) {
                log.warn("Clarification history update failed: {}", e.getMessage());
            }

        } else {
            // Indentor/PP (global)/member response — PK-based when available, target-type filtered fallback
            try {
                Optional<TenderClarificationHistory> historyRow;
                if (dto.getClarificationHistoryId() != null) {
                    historyRow = clarificationHistoryRepository.findById(dto.getClarificationHistoryId())
                            .filter(h -> tenderId.equals(h.getTenderId()) && h.getRespondedAt() == null);
                } else {
                    Set<String> allowedTargets = Set.of("INDENTOR", "CHAIRMAN",
                            "PURCHASE_PERSONNEL", "SPECIFIC_MEMBER", "ALL_MEMBERS");
                    historyRow = clarificationHistoryRepository
                            .findByTenderIdOrderByRequestedAtDesc(tenderId).stream()
                            .filter(h -> h.getRespondedAt() == null
                                    && allowedTargets.contains(h.getClarificationTarget()))
                            .findFirst();
                }
                historyRow.ifPresent(h -> {
                    h.setResponseText(dto.getResponseText());
                    h.setResponseFileName(dto.getResponseFileName());
                    h.setRespondedByRole(dto.getRespondedByRole());
                    h.setRespondedById(dto.getRespondedById());
                    h.setRespondedAt(LocalDateTime.now());
                    clarificationHistoryRepository.save(h);
                });
            } catch (Exception e) {
                log.warn("Clarification history update failed: {}", e.getMessage());
            }
        }

        // ── Unified restore gate ────────────────────────────────────────
        // Only restore eval status when ALL clarifications are resolved:
        //   1. No quotations still CHANGE_REQUESTED (covers ALL_VENDORS flow)
        //   2. No history rows with respondedAt=null  (covers specific-row flows)
        boolean quotationsResolved = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                .stream()
                .noneMatch(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()));
        long openHistoryRows = clarificationHistoryRepository.countByTenderIdAndRespondedAtIsNull(tenderId);

        if (!quotationsResolved || openHistoryRows > 0) {
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);
            return buildStatusDto(eval, tender, tenderId);
        }

        // All resolved — restore the previous evaluation status
        String restoreStatus = eval.getPreviousEvaluationStatus();
        if (restoreStatus == null || restoreStatus.isBlank()) {
            restoreStatus = "PENDING_APPROVAL";
        }
        eval.setEvaluationStatus(restoreStatus);

        // Clear clarification fields
        eval.setPreviousEvaluationStatus(null);
        eval.setClarificationPendingFrom(null);
        eval.setClarificationPendingFromId(null);
        eval.setClarificationPendingFromName(null);
        eval.setClarificationRequestedByRole(null);
        eval.setClarificationRemarks(null);
        eval.setClarificationTargetVendorId(null);
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 14. DIRECTOR FORMS AD-HOC COMMITTEE (Above 1 Crore)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto directorFormCommittee(String tenderId,
                                                            DirectorFormCommitteeDto dto) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        if (!"ABOVE_1_CRORE".equals(eval.getAmountCategory())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Ad-hoc committee formation is only for ABOVE_1_CRORE tenders."));
        }
        if (!"PENDING_COMMITTEE_FORMATION".equals(eval.getEvaluationStatus())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Committee formation can only be done in PENDING_COMMITTEE_FORMATION status."));
        }

        // Save ad-hoc chairman and co-chairman on the eval record
        eval.setAdHocChairmanUserId(dto.getChairmanUserId());
        eval.setAdHocChairmanName(dto.getChairmanName());
        eval.setAdHocCoChairmanUserId(dto.getCoChairmanUserId());
        eval.setAdHocCoChairmanName(dto.getCoChairmanName());
        eval.setEvaluationStatus("PENDING_APPROVAL"); // Committee votes now
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        // Create vote rows for each ad-hoc member
        if (dto.getMembers() != null) {
            for (DirectorFormCommitteeDto.AdHocMemberDto member : dto.getMembers()) {
                boolean alreadyExists = committeeDecisionRepository
                        .findByTenderIdAndCommitteeUserId(tenderId, member.getUserId()).isPresent();
                if (!alreadyExists) {
                    TenderCommitteeDecision row = new TenderCommitteeDecision();
                    row.setTenderId(tenderId);
                    row.setCommitteeUserId(member.getUserId());
                    row.setCommitteeMemberName(member.getMemberName());
                    row.setCreatedDate(LocalDateTime.now());
                    row.setUpdatedDate(LocalDateTime.now());
                    committeeDecisionRepository.save(row);
                }
            }
        }

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 15. CONFIRM BY INDENTOR (Under 10L — Proprietary / Limited Tender)
    //     Indent Creator reviews quotations and confirms → PENDING_SPO_APPROVAL
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto confirmByIndentor(String tenderId, Integer indentorUserId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        if (!"UNDER_10_LAKH".equals(eval.getAmountCategory())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Indentor confirmation is only applicable for UNDER_10_LAKH tenders."));
        }
        if (!"PENDING_FINANCIAL".equals(eval.getEvaluationStatus())
                && !"PENDING_TECHNICAL".equals(eval.getEvaluationStatus())
                && !"PENDING_APPROVAL".equals(eval.getEvaluationStatus())
                && !"PENDING_INDENTOR_CLARIFICATION".equals(eval.getEvaluationStatus())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Evaluation must be in PENDING_FINANCIAL status for indentor confirmation. Current: "
                    + eval.getEvaluationStatus()));
        }

        validateAllVendorsDecided(tenderId, Boolean.TRUE.equals(eval.getFinancialBidPhase()));

        // BR_006A: For Double Bid financial phase, Financial Comparison Sheet must be uploaded first
        if ("DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                && Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
            if (eval.getUploadCommeriallyQualifiedVendorsFileName() == null
                    || eval.getUploadCommeriallyQualifiedVendorsFileName().trim().isEmpty()) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Financial Comparison Sheet must be uploaded before confirming financial evaluation."));
            }
        }

        eval.setEvaluationStatus("PENDING_SPO_APPROVAL");
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 16. GET CLARIFICATION HISTORY
    // ─────────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    @Override
    public List<TenderClarificationHistory> getClarificationHistory(String tenderId) {
        try {
            return clarificationHistoryRepository.findByTenderIdOrderByRequestedAtDesc(tenderId);
        } catch (Exception e) {
            log.warn("Clarification history fetch failed (table may not exist yet): {}", e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    @Override
    public List<TenderClarificationHistory> getOpenClarifications(String tenderId, String vendorId) {
        return clarificationHistoryRepository
                .findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(tenderId, vendorId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 17. GET APPROVED VENDORS FOR PO (SPO-accepted vendors only)
    //     Called by PO vendor dropdown — returns only vendors finally
    //     approved by SPO so that PO can only be raised for them.
    // ─────────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    @Override
    public List<TenderEvaluationStatusDto.VendorQuotationEvalDto> getApprovedVendorsForPO(String tenderId) {
        TenderEvaluation eval = tenderEvaluationRepository.findById(tenderId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "No tender evaluation found for tender: " + tenderId
                        + ". Evaluation must be completed before PO can be created.")));

        if (!"APPROVED".equals(eval.getEvaluationStatus())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Tender Evaluation is not yet completed for tender: " + tenderId
                    + ". Current status: " + eval.getEvaluationStatus()
                    + ". PO can only be created after Tender Evaluation is Completed."));
        }

        List<VendorQuotationAgainstTender> quotations =
                quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);

        boolean isDoubleBid = "DOUBLE_BID".equalsIgnoreCase(eval.getBidType());

        return quotations.stream()
                .filter(q -> "ACCEPTED".equalsIgnoreCase(q.getSpoStatus()))
                .filter(q -> !isDoubleBid || "ACCEPTED".equalsIgnoreCase(q.getFinancialSpoStatus()))
                .map(q -> {
                    TenderEvaluationStatusDto.VendorQuotationEvalDto dto =
                            new TenderEvaluationStatusDto.VendorQuotationEvalDto();
                    dto.setVendorId(q.getVendorId());
                    dto.setSpoStatus(q.getSpoStatus());
                    dto.setStatus(q.getStatus());
                    dto.setRank(q.getRank());
                    vendorMasterRepository.findById(q.getVendorId())
                            .ifPresent(vm -> {
                                dto.setVendorName(vm.getVendorName());
                            });
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────
    // 18. SAVE VENDOR INDENTOR DECISION (per-vendor, saved immediately)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto saveVendorIndentorDecision(String tenderId, String vendorId,
                                                                  String decision, String remarks,
                                                                  Integer evaluatorUserId) {
        log.info("Indentor decision tenderId={} vendorId={} decision={} by userId={}",
                tenderId, vendorId, decision, evaluatorUserId);
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        Set<String> lockedStatuses = Set.of("PENDING_SPO_APPROVAL", "APPROVED", "REJECTED",
                "PENDING_DIRECTOR_APPROVAL", "PENDING_COMMITTEE_FORMATION");
        if (lockedStatuses.contains(eval.getEvaluationStatus())) {
            throw new BusinessException(new ErrorDetails(400, 1, "LOCKED",
                    "Evaluator decisions are locked after Confirm Evaluation. Current status: "
                    + eval.getEvaluationStatus()));
        }

        VendorQuotationAgainstTender quotation = quotationRepository
                .findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "No quotation found for vendor " + vendorId + " in tender " + tenderId)));

        String normalizedDecision = decision.toUpperCase();
        if (!"ACCEPTED".equals(normalizedDecision) && !"REJECTED".equals(normalizedDecision)) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Decision must be ACCEPTED or REJECTED"));
        }
        if ("CHANGE_REQUESTED".equalsIgnoreCase(quotation.getStatus())) {
            if ("ACCEPTED".equals(normalizedDecision)) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Cannot accept a vendor that is under seek clarification. "
                        + "Resolve the clarification first, or reject the vendor."));
            }
            quotation.setStatus("SUBMITTED");
        }

        if (Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
            if ("REJECTED".equalsIgnoreCase(quotation.getIndentorStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Cannot evaluate vendor " + vendorId
                        + " in financial phase — already rejected in technical evaluation."));
            }
            quotation.setFinancialIndentorStatus(normalizedDecision);
            quotation.setFinancialIndentorRemarks(remarks);
        } else {
            quotation.setIndentorStatus(normalizedDecision);
            quotation.setIndentorRemarks(remarks);
        }
        quotation.setUpdatedBy(String.valueOf(evaluatorUserId));
        quotation.setUpdatedDate(LocalDateTime.now());
        quotationRepository.save(quotation);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 19. SAVE VENDOR SPO DECISION (per-vendor, saved immediately)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto saveVendorSpoDecision(String tenderId, String vendorId,
                                                             String decision, String remarks,
                                                             Integer spoUserId) {
        log.info("SPO decision tenderId={} vendorId={} decision={} by userId={}",
                tenderId, vendorId, decision, spoUserId);
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        if ("APPROVED".equals(eval.getEvaluationStatus()) || "REJECTED".equals(eval.getEvaluationStatus())) {
            throw new BusinessException(new ErrorDetails(400, 1, "LOCKED",
                    "SPO decisions are locked after Confirm Evaluation. Current status: "
                    + eval.getEvaluationStatus()));
        }

        VendorQuotationAgainstTender quotation = quotationRepository
                .findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "No quotation found for vendor " + vendorId + " in tender " + tenderId)));

        String normalizedDecision = decision.toUpperCase();
        if (!"ACCEPTED".equals(normalizedDecision) && !"REJECTED".equals(normalizedDecision)) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Decision must be ACCEPTED or REJECTED"));
        }
        if ("CHANGE_REQUESTED".equalsIgnoreCase(quotation.getStatus())) {
            if ("ACCEPTED".equals(normalizedDecision)) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Cannot accept a vendor that is under seek clarification. "
                        + "Resolve the clarification first, or reject the vendor."));
            }
            quotation.setStatus("SUBMITTED");
        }

        if (Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
            if ("REJECTED".equalsIgnoreCase(quotation.getIndentorStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Cannot evaluate vendor " + vendorId
                        + " in financial phase — already rejected in technical evaluation."));
            }
            quotation.setFinancialSpoStatus(normalizedDecision);
            quotation.setFinancialSpoRemarks(remarks);
        } else {
            quotation.setSpoStatus(normalizedDecision);
            quotation.setSpoRemarks(remarks);
        }
        quotation.setUpdatedBy(String.valueOf(spoUserId));
        quotation.setUpdatedDate(LocalDateTime.now());
        quotationRepository.save(quotation);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 20. REJECT EVALUATION (any role, any status except APPROVED)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto rejectEvaluation(String tenderId, String rejectedByRole,
                                                       Integer rejectedByUserId, String remarks) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        String currentStatus = eval.getEvaluationStatus();
        if ("APPROVED".equals(currentStatus)) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Cannot reject an already approved evaluation."));
        }
        if ("REJECTED".equals(currentStatus)) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Evaluation is already rejected."));
        }

        eval.setPreviousEvaluationStatus(currentStatus);
        eval.setEvaluationStatus("REJECTED");
        eval.setRejectedByRole(rejectedByRole);
        eval.setRejectedByUserId(rejectedByUserId);
        eval.setApprovalRemarks(remarks);
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // 21. REOPEN EVALUATION (restore previousEvaluationStatus)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto reopenEvaluation(String tenderId, Integer userId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        if (!"REJECTED".equals(eval.getEvaluationStatus())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Only REJECTED evaluations can be reopened. Current: " + eval.getEvaluationStatus()));
        }

        String restoreStatus = eval.getPreviousEvaluationStatus();
        if (restoreStatus == null || restoreStatus.isBlank()) {
            restoreStatus = "PENDING_APPROVAL";
        }

        eval.setEvaluationStatus(restoreStatus);
        eval.setPreviousEvaluationStatus(null);
        eval.setRejectedByRole(null);
        eval.setRejectedByUserId(null);
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────

    private void validateAllVendorsDecided(String tenderId) {
        validateAllVendorsDecided(tenderId, false);
    }

    private void validateAllVendorsDecided(String tenderId, boolean financialPhase) {
        List<VendorQuotationAgainstTender> quotations =
                quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
        if (quotations.isEmpty()) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "No vendor quotations found for this tender."));
        }
        List<String> underClarification = quotations.stream()
                .filter(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()))
                .map(VendorQuotationAgainstTender::getVendorId)
                .collect(Collectors.toList());
        if (!underClarification.isEmpty()) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Cannot confirm evaluation — vendor(s) under seek clarification: "
                    + String.join(", ", underClarification)
                    + ". Please resolve clarification or reject the vendor(s) first."));
        }
        List<String> pendingDecision = quotations.stream()
                .filter(q -> {
                    if (financialPhase && "REJECTED".equalsIgnoreCase(q.getIndentorStatus())) {
                        return false;
                    }
                    String status = financialPhase ? q.getFinancialIndentorStatus() : q.getIndentorStatus();
                    return status == null
                        || (!"ACCEPTED".equalsIgnoreCase(status)
                            && !"REJECTED".equalsIgnoreCase(status));
                })
                .map(VendorQuotationAgainstTender::getVendorId)
                .collect(Collectors.toList());
        if (!pendingDecision.isEmpty()) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Cannot confirm evaluation — vendor(s) without a decision (Accept/Reject): "
                    + String.join(", ", pendingDecision)
                    + ". All vendors must be accepted or rejected before confirming."));
        }
    }

    private void validateAllSpoDecided(String tenderId) {
        validateAllSpoDecided(tenderId, false);
    }

    private void validateAllSpoDecided(String tenderId, boolean financialPhase) {
        List<VendorQuotationAgainstTender> quotations =
                quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
        List<String> underClarification = quotations.stream()
                .filter(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()))
                .map(VendorQuotationAgainstTender::getVendorId)
                .collect(Collectors.toList());
        if (!underClarification.isEmpty()) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Cannot confirm — vendor(s) under seek clarification: "
                    + String.join(", ", underClarification)
                    + ". Please resolve clarification or reject the vendor(s) first."));
        }
        List<String> pendingSpo = quotations.stream()
                .filter(q -> {
                    if (financialPhase) {
                        // Financial phase: only check vendors accepted in both technical rounds
                        return "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus())
                            && "ACCEPTED".equalsIgnoreCase(q.getFinancialIndentorStatus());
                    }
                    return "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus());
                })
                .filter(q -> {
                    String spoSt = financialPhase ? q.getFinancialSpoStatus() : q.getSpoStatus();
                    return spoSt == null
                        || (!"ACCEPTED".equalsIgnoreCase(spoSt)
                            && !"REJECTED".equalsIgnoreCase(spoSt));
                })
                .map(VendorQuotationAgainstTender::getVendorId)
                .collect(Collectors.toList());
        if (!pendingSpo.isEmpty()) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Cannot confirm — Indentor-accepted vendor(s) without SPO decision: "
                    + String.join(", ", pendingSpo)
                    + ". All accepted vendors must have an SPO decision before confirming."));
        }
    }

    private void markApprovedVendorCompleted(String tenderId, String vendorId) {
        quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                .ifPresent(q -> {
                    q.setStatus("Completed");
                    q.setAcceptanceStatus("ACCEPTED");
                    quotationRepository.save(q);
                });
    }

    private TenderEvaluation requireEval(String tenderId) {
        return tenderEvaluationRepository.findById(tenderId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "Tender evaluation not found for tender: " + tenderId
                        + ". Please initiate evaluation first.")));
    }

    private TenderRequest requireTender(String tenderId) {
        return tenderRequestRepository.findByTenderId(tenderId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "Tender request not found: " + tenderId)));
    }

    private TenderEvaluationStatusDto buildStatusDto(TenderEvaluation eval,
                                                      TenderRequest tender,
                                                      String tenderId) {
        TenderEvaluationStatusDto dto = new TenderEvaluationStatusDto();
        dto.setTenderId(tenderId);
        dto.setEvaluationStatus(eval.getEvaluationStatus());
        dto.setBidType(eval.getBidType());
        dto.setAmountCategory(eval.getAmountCategory());

        String resolvedCommitteeType = null;
        if ("ABOVE_10_LAKH_UPTO_50_LAKH".equals(eval.getAmountCategory())) {
            resolvedCommitteeType = "STEC_I";
        } else if ("ABOVE_50_LAKH_UPTO_1_CRORE".equals(eval.getAmountCategory())) {
            resolvedCommitteeType = "STEC_II";
        } else if ("ABOVE_1_CRORE".equals(eval.getAmountCategory())) {
            resolvedCommitteeType = "ADHOC";
        }
        dto.setCommitteeType(resolvedCommitteeType);
        dto.setIndentCategory(eval.getIndentCategory());
        dto.setTotalTenderValue(eval.getTotalTenderValue());
        dto.setApprovedVendorId(eval.getApprovedVendorId());
        dto.setApprovedVendorName(eval.getApprovedVendorName());
        dto.setApprovalRemarks(eval.getApprovalRemarks());
        dto.setVendorPortalRegistered(eval.getVendorPortalRegistered());

        // Clarification fields
        dto.setClarificationPendingFrom(eval.getClarificationPendingFrom());
        dto.setClarificationPendingFromId(eval.getClarificationPendingFromId());
        dto.setClarificationPendingFromName(eval.getClarificationPendingFromName());
        dto.setClarificationRequestedByRole(eval.getClarificationRequestedByRole());
        dto.setClarificationRemarks(eval.getClarificationRemarks());
        dto.setPreviousEvaluationStatus(eval.getPreviousEvaluationStatus());
        dto.setClarificationTargetVendorId(eval.getClarificationTargetVendorId());
        dto.setRejectedByRole(eval.getRejectedByRole());
        dto.setRejectedByUserId(eval.getRejectedByUserId());
        dto.setFinancialBidPhase(eval.getFinancialBidPhase());
        dto.setComparisonSheetFileName(eval.getUploadQualifiedVendorsFileName());
        dto.setFinancialComparisonSheetFileName(eval.getUploadCommeriallyQualifiedVendorsFileName());

        // Ad-hoc committee
        dto.setAdHocChairmanUserId(eval.getAdHocChairmanUserId());
        dto.setAdHocChairmanName(eval.getAdHocChairmanName());
        dto.setAdHocCoChairmanUserId(eval.getAdHocCoChairmanUserId());
        dto.setAdHocCoChairmanName(eval.getAdHocCoChairmanName());

        // Vendor quotation list
        List<VendorQuotationAgainstTender> quotations =
                quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
        List<TenderEvaluationStatusDto.VendorQuotationEvalDto> vendorDtos = quotations.stream()
                .map(q -> {
                    TenderEvaluationStatusDto.VendorQuotationEvalDto v =
                            new TenderEvaluationStatusDto.VendorQuotationEvalDto();
                    v.setVendorId(q.getVendorId());
                    v.setQuotationFileName(q.getQuotationFileName());
                    v.setTechnicalStatus(q.getTechnicalStatus());
                    v.setTechnicalRemarks(q.getTechnicalRemarks());
                    v.setFinancialBidVisible(q.getFinancialBidVisible());
                    v.setStatus(q.getStatus());
                    v.setIndentorStatus(q.getIndentorStatus());
                    v.setSpoStatus(q.getSpoStatus());
                    v.setRank(q.getRank());
                    if (Boolean.TRUE.equals(q.getFinancialBidVisible())) {
                        v.setPriceBidFileName(q.getPriceBidFileName());
                    }
                    vendorMasterRepository.findById(q.getVendorId())
                            .ifPresent(vm -> v.setVendorName(vm.getVendorName()));
                    return v;
                }).collect(Collectors.toList());
        dto.setVendors(vendorDtos);

        // Committee data
        boolean hasCommittee = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(eval.getAmountCategory())
                || "ABOVE_50_LAKH_UPTO_1_CRORE".equals(eval.getAmountCategory())
                || "ABOVE_1_CRORE".equals(eval.getAmountCategory());
        if (hasCommittee) {
            List<TenderCommitteeDecision> decisions =
                    committeeDecisionRepository.findByTenderId(tenderId);

            List<TenderEvaluationStatusDto.CommitteeVoteDto> votes = decisions.stream()
                    .filter(d -> d.getCommitteeMemberName() != null)
                    .map(d -> {
                        TenderEvaluationStatusDto.CommitteeVoteDto v =
                                new TenderEvaluationStatusDto.CommitteeVoteDto();
                        v.setCommitteeUserId(d.getCommitteeUserId());
                        v.setCommitteeMemberName(d.getCommitteeMemberName());
                        v.setVote(d.getVote());
                        v.setVoteRemarks(d.getVoteRemarks());
                        return v;
                    }).collect(Collectors.toList());
            dto.setCommitteeVotes(votes);

            decisions.stream()
                    .filter(d -> d.getChairmanDecision() != null)
                    .findFirst()
                    .ifPresent(d -> {
                        dto.setChairmanDecision(d.getChairmanDecision());
                        dto.setChairmanRemarks(d.getChairmanRemarks());
                        dto.setChairmanOverrideUsed(d.getChairmanOverrideUsed());
                    });
            decisions.stream()
                    .filter(d -> d.getDirectorDecision() != null)
                    .findFirst()
                    .ifPresent(d -> {
                        dto.setDirectorDecision(d.getDirectorDecision());
                        dto.setDirectorRemarks(d.getDirectorRemarks());
                    });
            decisions.stream()
                    .filter(d -> d.getExpertUserId() != null)
                    .findFirst()
                    .ifPresent(d -> {
                        dto.setExpertUserId(d.getExpertUserId());
                        dto.setExpertName(d.getExpertName());
                    });
        }
        return dto;
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────
    // CLARIFICATION ROUTING: role + amountCategory + modeOfProcurement
    // ─────────────────────────────────────────────────────────────────

    private String resolveClarificationTarget(String target, SeekClarificationDto dto,
                                              TenderEvaluation eval, TenderRequest tender) {
        String role = dto.getRequestedByRole();
        String mode = tender.getModeOfProcurement();
        boolean under10L = "UNDER_10_LAKH".equals(eval.getAmountCategory());

        // INDENTOR under 10L: auto-route based on procurement mode
        if ("INDENTOR".equalsIgnoreCase(role) && under10L) {
            if (isLimitedOrProprietary(mode)) {
                // Preserve ALL_VENDORS so bulk clarification marks every quotation
                if ("ALL_VENDORS".equalsIgnoreCase(target)) {
                    return "ALL_VENDORS";
                }
                return "VENDOR";
            } else if (isGemOpenGlobal(mode)) {
                return "PURCHASE_PERSONNEL";
            }
        }

        // SPO under 10L: vendor clarification follows same mode-based routing
        if ("SPO".equalsIgnoreCase(role) && under10L) {
            if ("VENDOR".equalsIgnoreCase(target) || "ALL_VENDORS".equalsIgnoreCase(target)) {
                if (isGemOpenGlobal(mode)) {
                    return "PURCHASE_PERSONNEL";
                }
            }
        }

        // SPO seeking from indentor: resolve to specific indent creator userId
        if ("SPO".equalsIgnoreCase(role) && "INDENTOR".equalsIgnoreCase(target)) {
            Integer indentorUserId = resolveIndentCreatorUserId(tender);
            if (indentorUserId != null) {
                dto.setTargetUserId(indentorUserId);
            }
        }

        // COMMITTEE_MEMBER above 10L: route to chairman regardless of mode
        if ("COMMITTEE_MEMBER".equalsIgnoreCase(role) && !under10L) {
            Integer chairmanUserId = resolveChairmanUserId(eval);
            if (chairmanUserId != null) {
                dto.setTargetUserId(chairmanUserId);
            }
            return "CHAIRMAN";
        }

        // CHAIRMAN: mode-based routing (skip if targeting specific/all members)
        if ("CHAIRMAN".equalsIgnoreCase(role)) {
            if (!"SPECIFIC_MEMBER".equalsIgnoreCase(target)
                    && !"ALL_MEMBERS".equalsIgnoreCase(target)) {
                if (isLimitedOrProprietary(mode)) {
                    return "VENDOR";
                } else if (isGemOpenGlobal(mode)) {
                    return "PURCHASE_PERSONNEL";
                }
            }
        }

        // DIRECTOR any amount: can seek clarification from anyone in the tender
        if ("DIRECTOR".equalsIgnoreCase(role)) {
            if ("INDENTOR".equalsIgnoreCase(target)) {
                Integer indentorUserId = resolveIndentCreatorUserId(tender);
                if (indentorUserId != null) {
                    dto.setTargetUserId(indentorUserId);
                }
                return target;
            }
            if ("CHAIRMAN".equalsIgnoreCase(target)) {
                Integer chairmanUserId = resolveChairmanUserId(eval);
                if (chairmanUserId != null) {
                    dto.setTargetUserId(chairmanUserId);
                }
                return target;
            }
            // Honor explicit person-level targets as-is
            if ("SPECIFIC_MEMBER".equalsIgnoreCase(target)
                    || "ALL_MEMBERS".equalsIgnoreCase(target)
                    || "PURCHASE_PERSONNEL".equalsIgnoreCase(target)) {
                return target;
            }
            // VENDOR / ALL_VENDORS / empty → mode-based routing
            if (isLimitedOrProprietary(mode)) {
                return "VENDOR";
            } else if (isGemOpenGlobal(mode)) {
                return "PURCHASE_PERSONNEL";
            }
        }

        return target;
    }

    private boolean isLimitedOrProprietary(String mode) {
        if (mode == null) return false;
        String m = mode.toLowerCase();
        return m.contains("limited") || m.contains("proprietary");
    }

    private boolean isGemOpenGlobal(String mode) {
        if (mode == null) return false;
        String m = mode.toLowerCase();
        return m.contains("gem") || m.contains("open") || m.contains("global");
    }

    private Integer resolveIndentCreatorUserId(TenderRequest tender) {
        if (tender.getIndentIds() == null || tender.getIndentIds().isEmpty()) {
            return null;
        }
        String firstIndentId = tender.getIndentIds().get(0).getIndentId();
        IndentCreation indent = indentCreationRepository.findByIndentId(firstIndentId);
        if (indent == null || indent.getCreatedBy() == null) return null;
        try {
            return Integer.parseInt(indent.getCreatedBy());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void mapRegisteredVendor(String tenderId, String vendorId, String registeredVendorId) {
        VendorQuotationAgainstTender quotation = quotationRepository
                .findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "No quotation found for tenderId=" + tenderId + " vendorId=" + vendorId)));

        VendorMaster registeredVendor = vendorMasterRepository.findByVendorId(registeredVendorId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Registered vendor not found: " + registeredVendorId)));

        quotation.setRegisteredVendorId(registeredVendor.getVendorId());
        quotation.setRegisteredVendorName(registeredVendor.getVendorName());
        quotation.setVendorId(registeredVendor.getVendorId());
        quotation.setUpdatedDate(LocalDateTime.now());
        quotationRepository.save(quotation);
        log.info("Updated vendorId from {} to {} for tender {}", vendorId, registeredVendorId, tenderId);
    }

    private Integer resolveChairmanUserId(TenderEvaluation eval) {
        if ("ABOVE_1_CRORE".equals(eval.getAmountCategory())) {
            return eval.getAdHocChairmanUserId();
        }
        String stecType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(eval.getAmountCategory())
                ? "STEC_I" : "STEC_II";
        return committeeRepository.findByRoleAndCommitteeTypeAndIsActiveTrue("CHAIRMAN", stecType)
                .map(TechnoFinancialCommittee::getUserId)
                .orElse(null);
    }
}
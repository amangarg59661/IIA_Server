    package com.astro.service.impl;

    import com.astro.dto.workflow.CommitteeVendorVoteDto;
    import com.astro.dto.workflow.DirectorFormCommitteeDto;
    import com.astro.dto.workflow.RespondClarificationDto;
    import com.astro.dto.workflow.MemberClarificationResolutionDto;
    import com.astro.dto.workflow.SeekClarificationDto;
    import com.astro.dto.workflow.TenderCommitteeDecisionDto;
    import com.astro.dto.workflow.TenderEvaluationStatusDto;
    import com.astro.dto.workflow.VendorTechnicalDecisionDto;
    import com.astro.entity.TechnoFinancialCommittee;
    import com.astro.entity.TenderClarificationHistory;
    import com.astro.entity.TenderCommitteeDecision;
    import com.astro.entity.TenderCommitteeVendorDecision;
    import com.astro.entity.VendorLoginDetails;
    import com.astro.entity.VendorMaster;
    import com.astro.entity.ProcurementModule.IndentCreation;
    import com.astro.entity.ProcurementModule.IndentId;
    import com.astro.entity.ProcurementModule.TenderEvaluation;
    import com.astro.entity.ProcurementModule.TenderRequest;
    import com.astro.entity.VendorQuotationAgainstTender;
    import com.astro.exception.BusinessException;
    import com.astro.exception.ErrorDetails;
    import com.astro.entity.RoleMaster;
    import com.astro.entity.UserMaster;
    import com.astro.entity.UserRoleMaster;
    import com.astro.repository.RoleMasterRepository;
    import com.astro.repository.TechnoFinancialCommitteeRepository;
    import com.astro.repository.UserRoleMasterRepository;
    import com.astro.repository.TenderClarificationHistoryRepository;
    import com.astro.repository.TenderCommitteeDecisionRepository;
    import com.astro.repository.UserMasterRepository;
    import com.astro.repository.VendorLoginDetailsRepository;
    import com.astro.repository.VendorMasterRepository;
    import com.astro.repository.VendorQuotationAgainstTenderRepository;
    import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
    import com.astro.repository.ProcurementModule.TenderCommitteeVendorDecisionRepository;
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
    import java.util.Set;
    import java.util.stream.Collectors;

    @Service
    public class TenderEvaluationApprovalServiceImpl implements TenderEvaluationApprovalService {

        private static final Logger log = LoggerFactory.getLogger(TenderEvaluationApprovalServiceImpl.class);

        private static final BigDecimal TEN_LAKH   = new BigDecimal("1000000");
        private static final BigDecimal FIFTY_LAKH = new BigDecimal("5000000");
        private static final BigDecimal ONE_CRORE  = new BigDecimal("10000000");
        private static final String COMMITTEE_MEMBER_ROLE = "Committee Member";
        private static final String COMMITTEE_CHAIRMAN_ROLE = "Committee Chairman";

        private final TenderEvaluationRepository tenderEvaluationRepository;
        private final TenderRequestRepository tenderRequestRepository;
        private final VendorQuotationAgainstTenderRepository quotationRepository;
        private final TechnoFinancialCommitteeRepository committeeRepository;
        private final TenderCommitteeDecisionRepository committeeDecisionRepository;
        private final TenderCommitteeVendorDecisionRepository committeeVendorDecisionRepository;
        private final UserMasterRepository userMasterRepository;
        private final VendorMasterRepository vendorMasterRepository;
        private final VendorLoginDetailsRepository vendorLoginDetailsRepository;
        private final TenderClarificationHistoryRepository clarificationHistoryRepository;
        private final IndentCreationRepository indentCreationRepository;
        private final RoleMasterRepository roleMasterRepository;
        private final UserRoleMasterRepository userRoleMasterRepository;
        private final EmailService emailService;
        private final PasswordEncoder passwordEncoder;
        private final com.astro.repository.WorkflowTransitionRepository workflowTransitionRepository;

        public TenderEvaluationApprovalServiceImpl(
                TenderEvaluationRepository tenderEvaluationRepository,
                TenderRequestRepository tenderRequestRepository,
                VendorQuotationAgainstTenderRepository quotationRepository,
                TechnoFinancialCommitteeRepository committeeRepository,
                TenderCommitteeDecisionRepository committeeDecisionRepository,
                TenderCommitteeVendorDecisionRepository committeeVendorDecisionRepository,
                UserMasterRepository userMasterRepository,
                VendorMasterRepository vendorMasterRepository,
                VendorLoginDetailsRepository vendorLoginDetailsRepository,
                TenderClarificationHistoryRepository clarificationHistoryRepository,
                IndentCreationRepository indentCreationRepository,
                RoleMasterRepository roleMasterRepository,
                UserRoleMasterRepository userRoleMasterRepository,
                EmailService emailService,
                PasswordEncoder passwordEncoder,
                com.astro.repository.WorkflowTransitionRepository workflowTransitionRepository) {
            this.tenderEvaluationRepository = tenderEvaluationRepository;
            this.tenderRequestRepository = tenderRequestRepository;
            this.quotationRepository = quotationRepository;
            this.committeeRepository = committeeRepository;
            this.committeeDecisionRepository = committeeDecisionRepository;
            this.committeeVendorDecisionRepository = committeeVendorDecisionRepository;
            this.userMasterRepository = userMasterRepository;
            this.vendorMasterRepository = vendorMasterRepository;
            this.vendorLoginDetailsRepository = vendorLoginDetailsRepository;
            this.clarificationHistoryRepository = clarificationHistoryRepository;
            this.indentCreationRepository = indentCreationRepository;
            this.roleMasterRepository = roleMasterRepository;
            this.userRoleMasterRepository = userRoleMasterRepository;
            this.emailService = emailService;
            this.passwordEncoder = passwordEncoder;
            this.workflowTransitionRepository = workflowTransitionRepository;
        }

        @Value("${filePath:}")
        private String filePath;

        // ─────────────────────────────────────────────────────────────────
        // 0. BEGIN EVALUATION (Phase 1: PENDING_DOCUMENT_UPLOAD)
        // ─────────────────────────────────────────────────────────────────
        @Transactional
        @Override
        public TenderEvaluationStatusDto beginEvaluation(String tenderId, Integer initiatedByUserId) {
            TenderRequest tender = requireTender(tenderId);

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

            if (eval.getEvaluationStatus() != null) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Evaluation has already been initiated for tender " + tenderId
                        + ". Current status: " + eval.getEvaluationStatus()));
            }

            eval.setEvaluationStatus("PENDING_DOCUMENT_UPLOAD");
            eval.setInitiated(1);
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);

            return buildStatusDto(eval, tender, tenderId);
        }

        // ─────────────────────────────────────────────────────────────────
        // 1. INITIATE EVALUATION (Phase 2: transition from PENDING_DOCUMENT_UPLOAD)
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

            if (eval.getEvaluationStatus() != null
                    && !"PENDING_DOCUMENT_UPLOAD".equals(eval.getEvaluationStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Evaluation already initiated. Current status: " + eval.getEvaluationStatus()));
            }

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

            boolean needsChairmanReview = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat)
                    || "ABOVE_50_LAKH_UPTO_1_CRORE".equals(amtCat);
            boolean needsPpDocument = needsChairmanReview;

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
                eval.setEvaluationStatus(needsPpDocument ? "PENDING_CHAIRMAN_REVIEW" : "PENDING_FINANCIAL");
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
                eval.setEvaluationStatus(needsPpDocument ? "PENDING_CHAIRMAN_REVIEW" : "PENDING_TECHNICAL");
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
                eval.setEvaluationStatus(needsPpDocument ? "PENDING_CHAIRMAN_REVIEW" : "PENDING_TECHNICAL");
            }

            // ABOVE_1_CRORE: override status — Director must form committee first
            if ("ABOVE_1_CRORE".equals(amtCat)) {
                eval.setEvaluationStatus("PENDING_DIRECTOR_REVIEW");
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

            // Pre-create per-vendor committee decision rows for above-10L DOUBLE_BID
            if (("ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) || "ABOVE_50_LAKH_UPTO_1_CRORE".equals(amtCat))
                    && "DOUBLE_BID".equalsIgnoreCase(bidType)) {
                String stecType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) ? "STEC_I" : "STEC_II";
                List<TechnoFinancialCommittee> members = committeeRepository.findByCommitteeTypeAndIsActiveTrue(stecType)
                        .stream().filter(m -> !"CHAIRMAN".equalsIgnoreCase(m.getRole()))
                        .collect(Collectors.toList());
                List<VendorQuotationAgainstTender> vendors =
                        quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);

                for (TechnoFinancialCommittee member : members) {
                    for (VendorQuotationAgainstTender vendor : vendors) {
                        TenderCommitteeVendorDecision row = new TenderCommitteeVendorDecision();
                        row.setTenderId(tenderId);
                        row.setVendorId(vendor.getVendorId());
                        row.setCommitteeUserId(member.getUserId());
                        row.setMemberName(member.getMemberName());
                        row.setPhase("TECHNICAL");
                        row.setCreatedDate(LocalDateTime.now());
                        committeeVendorDecisionRepository.save(row);
                    }
                }
            }

            // Pre-create per-vendor committee decision rows for above-10L SINGLE_BID
    if (("ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) || "ABOVE_50_LAKH_UPTO_1_CRORE".equals(amtCat))
            && "SINGLE_BID".equalsIgnoreCase(bidType)) {
        String stecType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) ? "STEC_I" : "STEC_II";
        List<TechnoFinancialCommittee> members = committeeRepository
                .findByCommitteeTypeAndIsActiveTrue(stecType)
                .stream().filter(m -> !"CHAIRMAN".equalsIgnoreCase(m.getRole()))
                .collect(Collectors.toList());
        List<VendorQuotationAgainstTender> vendors =
                quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);

        for (TechnoFinancialCommittee member : members) {
            for (VendorQuotationAgainstTender vendor : vendors) {
                boolean exists = committeeVendorDecisionRepository
                        .findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(
                                tenderId, vendor.getVendorId(), member.getUserId(), "FINANCIAL")
                        .isPresent();
                if (!exists) {
                    TenderCommitteeVendorDecision row = new TenderCommitteeVendorDecision();
                    row.setTenderId(tenderId);
                    row.setVendorId(vendor.getVendorId());
                    row.setCommitteeUserId(member.getUserId());
                    row.setMemberName(member.getMemberName());
                    row.setPhase("FINANCIAL"); // single bid goes straight to financial
                    row.setCreatedDate(LocalDateTime.now());
                    committeeVendorDecisionRepository.save(row);
                }
            }
        }
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
                // Committee already formed during PENDING_DIRECTOR_REVIEW phase
                eval.setEvaluationStatus("PENDING_APPROVAL");
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
                            .filter(q -> "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus())
                                    && "ACCEPTED".equalsIgnoreCase(q.getSpoStatus()))
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
                                        && "ACCEPTED".equalsIgnoreCase(q.getSpoStatus())
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
                    // Check ALL approved vendors for portal registration (not just the first one)
                    List<VendorQuotationAgainstTender> completedVendors = allQuotations.stream()
                            .filter(q -> "Completed".equals(q.getStatus()))
                            .collect(Collectors.toList());
                    if (!completedVendors.isEmpty()) {
                        boolean allRegistered = completedVendors.stream()
                                .allMatch(q -> checkAndRegisterVendorOnPortal(q.getVendorId()));
                        eval.setVendorPortalRegistered(allRegistered);
                    } else if (eval.getApprovedVendorId() != null) {
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

        // Deprecated: expert assignment now handled by TechnoFinancialCommitteeService.nominateMember()
        // with expert=true flag. That method has stronger validation (chairman check, self-block,
        // PO lock guard, auto role assignment) and also records audit trail on chairman's row.
        // @Transactional
        // @Override
        // public TenderEvaluationStatusDto assignExpert(String tenderId, Integer expertUserId,
        //                                                String expertName, Integer chairmanUserId) { ... }

        // ─────────────────────────────────────────────────────────────────
        // 7b. CHAIRMAN CONFIRMS COMMITTEE (Above 10L — STEC-I / STEC-II)
        // ─────────────────────────────────────────────────────────────────
        @Transactional
        @Override
        public TenderEvaluationStatusDto chairmanConfirmCommittee(String tenderId, Integer chairmanUserId) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            if (!"PENDING_CHAIRMAN_REVIEW".equals(eval.getEvaluationStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Committee confirmation can only be done in PENDING_CHAIRMAN_REVIEW status. Current: "
                        + eval.getEvaluationStatus()));
            }

            String amtCat = eval.getAmountCategory();
            if ("ABOVE_1_CRORE".equals(amtCat)) {
                // Ad-hoc committee: validate against ad-hoc chairman
                if (eval.getAdHocChairmanUserId() == null) {
                    throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                            "No ad-hoc Chairman assigned yet for this ABOVE_1_CRORE tender."));
                }
                if (!eval.getAdHocChairmanUserId().equals(chairmanUserId)) {
                    throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                            "Only the ad-hoc Chairman (" + eval.getAdHocChairmanName()
                            + ") can confirm committee composition."));
                }
            } else {
                String committeeType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) ? "STEC_I" : "STEC_II";

                TechnoFinancialCommittee chairman = committeeRepository
                        .findByRoleAndCommitteeTypeAndIsActiveTrue("CHAIRMAN", committeeType)
                        .orElseThrow(() -> new BusinessException(new ErrorDetails(400, 1,
                                "CONFIGURATION_ERROR", "No active Chairman configured for " + committeeType)));

                if (!chairman.getUserId().equals(chairmanUserId)) {
                    throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                            "Only the " + committeeType + " Chairman (" + chairman.getMemberName()
                            + ") can confirm committee composition."));
                }
            }

            // Advance to member voting
            eval.setEvaluationStatus("PENDING_MEMBER_VOTING");
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);

            log.info("Chairman {} confirmed committee for tender {}. Status → PENDING_MEMBER_VOTING",
                    chairmanUserId, tenderId);

            return buildStatusDto(eval, tender, tenderId);
        }

        // ─────────────────────────────────────────────────────────────────
        // 8a. COMMITTEE VENDOR DECISION (Above 10L Double Bid)
        // ─────────────────────────────────────────────────────────────────
        @Transactional
        @Override
        public TenderEvaluationStatusDto committeeVendorDecision(String tenderId, String vendorId,
                                                                String decision, String remarks,
                                                                Integer committeeUserId) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            Set<String> lockedStatuses = Set.of("PENDING_SPO_APPROVAL", "APPROVED", "REJECTED",
                    "PENDING_DIRECTOR_APPROVAL", "PENDING_COMMITTEE_FORMATION",
                    "PENDING_CHAIRMAN_REVIEW", "PENDING_DIRECTOR_REVIEW");
            if (lockedStatuses.contains(eval.getEvaluationStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "LOCKED",
                        "Committee vendor decisions are locked. Current status: "
                        + eval.getEvaluationStatus()));
            }

            committeeDecisionRepository.findByTenderIdAndCommitteeUserId(tenderId, committeeUserId)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                            "User " + committeeUserId + " is not a committee member for tender " + tenderId)));

            quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                            "No quotation found for vendor " + vendorId + " in tender " + tenderId)));

            String normalizedDecision = decision.toUpperCase();
            if (!"ACCEPTED".equals(normalizedDecision) && !"REJECTED".equals(normalizedDecision)) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Decision must be ACCEPTED or REJECTED"));
            }

            String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";

            TenderCommitteeVendorDecision voteRow = committeeVendorDecisionRepository
                    .findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(tenderId, vendorId, committeeUserId, phase)
                    .orElseGet(() -> {
                        TenderCommitteeVendorDecision r = new TenderCommitteeVendorDecision();
                        r.setTenderId(tenderId);
                        r.setVendorId(vendorId);
                        r.setCommitteeUserId(committeeUserId);
                        r.setPhase(phase);
                        r.setCreatedDate(LocalDateTime.now());
                        return r;
                    });

            // Block changes if member already confirmed
            if (Boolean.TRUE.equals(voteRow.getConfirmed())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Your votes are confirmed and locked. Cannot change."));
            }

            UserMaster user = userMasterRepository.findByUserId(committeeUserId);
            voteRow.setMemberName(user != null ? user.getUserName() : String.valueOf(committeeUserId));
            voteRow.setDecision(normalizedDecision);
            voteRow.setRemarks(remarks);
            voteRow.setDecisionDate(LocalDateTime.now());
            voteRow.setUpdatedDate(LocalDateTime.now());
            committeeVendorDecisionRepository.save(voteRow);

            return buildStatusDto(eval, tender, tenderId);
        }

        // ─────────────────────────────────────────────────────────────────
        // 8b. GET VENDOR VOTE GRID (Above 10L Double Bid)
        // ─────────────────────────────────────────────────────────────────
        @Override
        public Map<String, List<CommitteeVendorVoteDto>> getVendorVoteGrid(String tenderId, String phase) {
            List<TenderCommitteeVendorDecision> rows =
                    committeeVendorDecisionRepository.findByTenderIdAndPhase(tenderId, phase);
            return rows.stream()
                    .map(r -> {
                        CommitteeVendorVoteDto dto = new CommitteeVendorVoteDto();
                        dto.setCommitteeUserId(r.getCommitteeUserId());
                        dto.setMemberName(r.getMemberName());
                        dto.setDecision(r.getDecision());
                        dto.setRemarks(r.getRemarks());
                        dto.setDecisionDate(r.getDecisionDate());
                        return Map.entry(r.getVendorId(), dto);
                    })
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        }

        // ─────────────────────────────────────────────────────────────────
        // 8c. CHAIRMAN VENDOR RESOLVE (Above 10L Double Bid)
        // ─────────────────────────────────────────────────────────────────
        @Transactional
        @Override
        public TenderEvaluationStatusDto chairmanVendorResolve(String tenderId, String vendorId,
                                                                String decision, String remarks,
                                                                Integer chairmanUserId) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            String amtCat = eval.getAmountCategory();
            if ("ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat)
                    || "ABOVE_50_LAKH_UPTO_1_CRORE".equals(amtCat)) {
                String expectedType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) ? "STEC_I" : "STEC_II";
                TechnoFinancialCommittee chairman = committeeRepository
                        .findByRoleAndCommitteeTypeAndIsActiveTrue("CHAIRMAN", expectedType)
                        .orElseThrow(() -> new BusinessException(new ErrorDetails(400, 1,
                                "CONFIGURATION_ERROR", "No active Chairman for " + expectedType)));
                if (!chairman.getUserId().equals(chairmanUserId)) {
                    throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                            "Only the " + expectedType + " Chairman can resolve vendor decisions."));
                }
            }

            String normalizedDecision = decision.toUpperCase();
            if (!"ACCEPTED".equals(normalizedDecision) && !"REJECTED".equals(normalizedDecision)) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Decision must be ACCEPTED or REJECTED"));
            }

            String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";

            VendorQuotationAgainstTender quotation = quotationRepository
                    .findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                            "No quotation found for vendor " + vendorId)));

            if ("FINANCIAL".equals(phase)) {
                quotation.setFinancialIndentorStatus(normalizedDecision);
                quotation.setFinancialIndentorRemarks(remarks);
            } else {
                quotation.setIndentorStatus(normalizedDecision);
                quotation.setIndentorRemarks(remarks);
            }
            quotation.setUpdatedBy(String.valueOf(chairmanUserId));
            quotation.setUpdatedDate(LocalDateTime.now());
            quotationRepository.save(quotation);

            // Audit: record chairman resolution
            TenderCommitteeVendorDecision chairVoteRow = committeeVendorDecisionRepository
                    .findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(tenderId, vendorId, chairmanUserId, phase)
                    .orElseGet(() -> {
                        TenderCommitteeVendorDecision r = new TenderCommitteeVendorDecision();
                        r.setTenderId(tenderId);
                        r.setVendorId(vendorId);
                        r.setCommitteeUserId(chairmanUserId);
                        r.setPhase(phase);
                        r.setCreatedDate(LocalDateTime.now());
                        return r;
                    });
            chairVoteRow.setMemberName("Chairman (Resolved)");
            chairVoteRow.setDecision(normalizedDecision);
            chairVoteRow.setRemarks(remarks);
            chairVoteRow.setDecisionDate(LocalDateTime.now());
            chairVoteRow.setUpdatedDate(LocalDateTime.now());
            committeeVendorDecisionRepository.save(chairVoteRow);

            // Check if ALL vendors resolved → auto-transition
            List<VendorQuotationAgainstTender> allQuotations =
                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
            boolean allResolved;
            if ("FINANCIAL".equals(phase)) {
                allResolved = allQuotations.stream()
                        .filter(q -> !("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                    || "REJECTED".equalsIgnoreCase(q.getSpoStatus())))
                        .allMatch(q -> q.getFinancialIndentorStatus() != null
                                && !"PENDING".equalsIgnoreCase(q.getFinancialIndentorStatus()));
            } else {
                allResolved = allQuotations.stream()
                        .allMatch(q -> q.getIndentorStatus() != null
                                && !"PENDING".equalsIgnoreCase(q.getIndentorStatus()));
            }

            if (allResolved) {
                eval.setEvaluationStatus("PENDING_DIRECTOR_APPROVAL");
                eval.setUpdatedDate(LocalDateTime.now());
                tenderEvaluationRepository.save(eval);
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

            validateNoClarificationPending(tenderId, Boolean.TRUE.equals(eval.getFinancialBidPhase()));

            // Validate chairman has voted on ALL eligible vendors via per-vendor flow
            String chairPhase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";
            validateAllPerVendorVotes(tenderId, chairPhase, "CHAIRMAN", dto.getChairmanUserId(), eval);

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

            validateNoClarificationPending(tenderId, Boolean.TRUE.equals(eval.getFinancialBidPhase()));

            // Validate director has voted on ALL eligible vendors via per-vendor flow
            String dirPhase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";
            validateAllPerVendorVotes(tenderId, dirPhase, "DIRECTOR", directorUserId, eval);

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
                            .filter(q -> !"REJECTED".equalsIgnoreCase(q.getIndentorStatus()))
                            .forEach(q -> {
                                q.setFinancialBidVisible(true);
                                q.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
                                q.setStatus("SUBMITTED");
                            });
                    quotationRepository.saveAll(quotations);

                    eval.setFinancialBidPhase(true);
                    // Go back to PENDING_FINANCIAL so PP can select the approved vendor
                    // (from technically-approved list) before committee votes on financial bids.
                    eval.setEvaluationStatus("PENDING_FINANCIAL_SHEET_UPLOAD");

                    // Clean up any stale FINANCIAL phase per-vendor rows
                    committeeVendorDecisionRepository.deleteByTenderIdAndPhase(tenderId, "FINANCIAL");

                    // Pre-create FINANCIAL phase rows for director-approved vendors only
                    List<TenderCommitteeDecision> committeeMembers =
                            committeeDecisionRepository.findByTenderId(tenderId).stream()
                            .filter(d -> d.getCommitteeMemberName() != null
                                    && d.getCommitteeUserId() != null)
                            .collect(Collectors.toList());

                    Set<String> directorApprovedVendorIds = committeeVendorDecisionRepository
                            .findByTenderIdAndPhaseAndVoterRole(tenderId, "TECHNICAL", "DIRECTOR")
                            .stream()
                            .filter(v -> "ACCEPTED".equalsIgnoreCase(v.getDecision()))
                            .map(TenderCommitteeVendorDecision::getVendorId)
                            .collect(Collectors.toSet());

                    List<VendorQuotationAgainstTender> approvedVendors = quotations.stream()
                            .filter(q -> "APPROVED".equalsIgnoreCase(q.getTechnicalStatus())
                                    && "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus())
                                    && directorApprovedVendorIds.contains(q.getVendorId()))
                            .collect(Collectors.toList());

                    for (TenderCommitteeDecision member : committeeMembers) {
                        for (VendorQuotationAgainstTender vendor : approvedVendors) {
                            TenderCommitteeVendorDecision row = new TenderCommitteeVendorDecision();
                            row.setTenderId(tenderId);
                            row.setVendorId(vendor.getVendorId());
                            row.setCommitteeUserId(member.getCommitteeUserId());
                            row.setMemberName(member.getCommitteeMemberName());
                            row.setPhase("FINANCIAL");
                            row.setCreatedDate(LocalDateTime.now());
                            committeeVendorDecisionRepository.save(row);
                        }
                    }
                } else {
                    eval.setEvaluationStatus("APPROVED");
                    eval.setFinancialBidPhase(false);
                    // Check all approved vendors for portal registration
                    List<VendorQuotationAgainstTender> dirCompletedVendors =
                            quotationRepository.findByTenderIdAndIsLatestTrue(tenderId).stream()
                                    .filter(q -> "Completed".equals(q.getStatus()))
                                    .collect(Collectors.toList());
                    if (!dirCompletedVendors.isEmpty()) {
                        boolean allRegistered = dirCompletedVendors.stream()
                                .allMatch(q -> checkAndRegisterVendorOnPortal(q.getVendorId()));
                        eval.setVendorPortalRegistered(allRegistered);
                    } else if (eval.getApprovedVendorId() != null) {
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

            // Cross-type validation: cannot mix VENDOR and INDENTOR clarification types
            if (Set.of("SPO", "CHAIRMAN", "DIRECTOR").contains(dto.getRequestedByRole().toUpperCase())) {
                String currentStatus = eval.getEvaluationStatus();
                boolean targetIsVendor = Set.of("VENDOR", "ALL_VENDORS").contains(target.toUpperCase());
                boolean targetIsIndentor = Set.of("INDENTOR", "PURCHASE_PERSONNEL").contains(target.toUpperCase());
                if (targetIsVendor && "PENDING_INDENTOR_CLARIFICATION".equals(currentStatus)) {
                    throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                            "Cannot seek vendor clarification while Indentor clarification is pending. Wait for Indentor response first."));
                }
                if (targetIsIndentor && "PENDING_VENDOR_CLARIFICATION".equals(currentStatus)) {
                    throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                            "Cannot seek Indentor clarification while vendor clarification is pending. Wait for vendor response first."));
                }
            }

            // GEM/OPEN/GLOBAL: original target was VENDOR/ALL_VENDORS but rerouted to PURCHASE_PERSONNEL.
            // Mark vendor quotations CHANGE_REQUESTED so PP knows which vendors need clarification.
            boolean reroutedVendorToPP = "PURCHASE_PERSONNEL".equalsIgnoreCase(target)
                    && (originalTarget != null)
                    && ("VENDOR".equalsIgnoreCase(originalTarget) || "ALL_VENDORS".equalsIgnoreCase(originalTarget));
boolean reroutedIndentorToPP = "PURCHASE_PERSONNEL".equalsIgnoreCase(target)
        && !reroutedVendorToPP
        && "SPO".equalsIgnoreCase(dto.getRequestedByRole())
        && (dto.getTargetVendorId() == null || dto.getTargetVendorId().isBlank())
        && "MULTIPLE_INDENT".equals(eval.getIndentCategory());

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
                    // Double-bid financial phase: skip technically disqualified vendors
                    if ("DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                            && Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
                        allQuotations = allQuotations.stream()
                                .filter(q -> !("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                            || "REJECTED".equalsIgnoreCase(q.getSpoStatus())))
                                .collect(Collectors.toList());
                    }
                    allQuotations.forEach(q -> {
                        q.setStatus("CHANGE_REQUESTED");
                        q.setRemarks(dto.getRemarks());
                    });
                    quotationRepository.saveAll(allQuotations);
                }
            }

            boolean isAbove10L = !"UNDER_10_LAKH".equals(eval.getAmountCategory());
            boolean isCommitteeMemberAbove10L = "COMMITTEE_MEMBER".equalsIgnoreCase(dto.getRequestedByRole()) && isAbove10L;

            // Committee member above 10L: per-member per-vendor tracking via history only — skip global metadata
            if (!isCommitteeMemberAbove10L) {
                // Save where to return after clarification is received
                // Only preserve the real pre-clarification status; do not overwrite it on subsequent seeks
                String currentStatus = eval.getEvaluationStatus();
                if (currentStatus == null
                        || (!currentStatus.contains("CLARIFICATION") && !currentStatus.contains("MEMBER_REVOTE"))) {
                    eval.setPreviousEvaluationStatus(currentStatus);
                }
                // Always update metadata to reflect the CURRENT/latest clarification
                eval.setClarificationPendingFrom(target.toUpperCase());
                eval.setClarificationPendingFromId(dto.getTargetUserId());
                eval.setClarificationPendingFromName(dto.getTargetUserName());
                eval.setClarificationRequestedByRole(dto.getRequestedByRole());
                eval.setClarificationRemarks(dto.getRemarks());
                eval.setClarificationTargetVendorId(dto.getTargetVendorId());
            }

            switch (target.toUpperCase()) {
                case "VENDOR":
                    // Mark a specific vendor's quotation as CHANGE_REQUESTED
                    // Above 10L: don't change eval status for single-vendor clarification —
                    // other vendors can still be voted on while this one is pending clarification
                    if (!isAbove10L || eval.getEvaluationStatus().equalsIgnoreCase("PENDING_DOCUMENT_UPLOAD")) {
                        eval.setEvaluationStatus("PENDING_VENDOR_CLARIFICATION");
                    }
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
                    // Double-bid financial phase: skip technically disqualified vendors
                    if ("DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                            && Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
                        allQuotations = allQuotations.stream()
                                .filter(q -> !("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                            || "REJECTED".equalsIgnoreCase(q.getSpoStatus())))
                                .collect(Collectors.toList());
                    }
                    allQuotations.forEach(q -> {
                        q.setStatus("CHANGE_REQUESTED");
                        q.setRemarks(dto.getRemarks());
                        applyClarificationRoleFields(q, dto, eval);
                    });
                    quotationRepository.saveAll(allQuotations);
                    break;
                    
                    
                    
                case "PURCHASE_PERSONNEL":
                    if (reroutedVendorToPP) {
                        eval.setEvaluationStatus("PENDING_VENDOR_CLARIFICATION");
                        // Apply role-specific status fields on the rerouted vendor quotations
                        if ("VENDOR".equalsIgnoreCase(originalTarget)) {
                            String vid = dto.getTargetVendorId();
                            if (vid == null || vid.isBlank()) vid = eval.getApprovedVendorId();
                            if (vid != null) {
                                final String fVid = vid;
                                quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, fVid)
                                        .ifPresent(q -> {
                                            applyClarificationRoleFields(q, dto, eval);
                                            quotationRepository.save(q);
                                        });
                            }
                        } else {
                            List<VendorQuotationAgainstTender> rerouted =
                                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
                            if ("DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                                    && Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
                                rerouted = rerouted.stream()
                                        .filter(q -> !("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                                    || "REJECTED".equalsIgnoreCase(q.getSpoStatus())))
                                        .collect(Collectors.toList());
                            }
                            rerouted.forEach(q -> applyClarificationRoleFields(q, dto, eval));
                            quotationRepository.saveAll(rerouted);
                        }
                    } else {
                        eval.setEvaluationStatus("PENDING_INDENTOR_CLARIFICATION");
                        // ADD THIS — mirror the INDENTOR reset logic for SPO→PP (multiple indent)
        if ("SPO".equalsIgnoreCase(dto.getRequestedByRole())) {
            if (dto.getTargetVendorId() != null && !dto.getTargetVendorId().isBlank()) {
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
            } else {
                List<VendorQuotationAgainstTender> allQ =
                        quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
                if ("DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                        && Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
                    allQ = allQ.stream()
                            .filter(q -> "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus())
                                    && "ACCEPTED".equalsIgnoreCase(q.getSpoStatus()))
                            .collect(Collectors.toList());
                }
                allQ.forEach(q -> {
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
                });
                quotationRepository.saveAll(allQ);
            }
        }
                    }

                    // SPO seeking clarification from indentor/PP — NOT for vendor clarifications rerouted to PP (GEM/OPEN/GLOBAL)
                    if (!reroutedVendorToPP
                            && "SPO".equalsIgnoreCase(dto.getRequestedByRole())) {
                        if (dto.getTargetVendorId() != null && !dto.getTargetVendorId().isBlank()) {
                            // Specific vendor: reset that vendor's indentor decision
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
                        } else {
                            // All vendors: reset indentor decisions (multi-indent SPO→PP clarification)
                            List<VendorQuotationAgainstTender> allQ =
                                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
                            if ("DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                                    && Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
                                allQ = allQ.stream()
                                        .filter(q -> "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus())
                                                && "ACCEPTED".equalsIgnoreCase(q.getSpoStatus()))
                                        .collect(Collectors.toList());
                            }
                            allQ.forEach(q -> {
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
                            });
                            quotationRepository.saveAll(allQ);
                        }
                    }
                    break;
                case "INDENTOR":
                case "CHAIRMAN":
                    if ("COMMITTEE_MEMBER".equalsIgnoreCase(dto.getRequestedByRole())) {
                        // Above 10L: no global status change — per-member per-vendor via history records.
                        // Reset this member's per-vendor vote if they had already decided.
                        if (isAbove10L && dto.getTargetVendorId() != null && !dto.getTargetVendorId().isBlank()) {
                            String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";
                            committeeVendorDecisionRepository
                                    .findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(
                                            tenderId, dto.getTargetVendorId(), dto.getRequestedByUserId(), phase)
                                    .ifPresent(v -> {
                                        v.setDecision(null);
                                        v.setRemarks(null);
                                        v.setDecisionDate(null);
                                        v.setConfirmed(false);
                                        v.setUpdatedDate(LocalDateTime.now());
                                        committeeVendorDecisionRepository.save(v);
                                    });
                        }
                        break;
                    }
                    if ("DIRECTOR".equalsIgnoreCase(dto.getRequestedByRole())
                            && "CHAIRMAN".equalsIgnoreCase(target)) {
                        eval.setEvaluationStatus("PENDING_CHAIRMAN_CLARIFICATION_REVIEW");
                        break;
                    }
                    eval.setEvaluationStatus("PENDING_INDENTOR_CLARIFICATION");
                    if ("SPO".equalsIgnoreCase(dto.getRequestedByRole())) {
                        if (dto.getTargetVendorId() != null && !dto.getTargetVendorId().isBlank()) {
                            // SPO seeking clarification for a specific vendor: reset that vendor's indentor decision
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
                        } else {
                            // SPO seeking revision for ALL vendors: reset indentor decisions
                            // In financial phase: only affect vendors that passed both technical rounds
                            List<VendorQuotationAgainstTender> allQ =
                                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
                            if ("DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                                    && Boolean.TRUE.equals(eval.getFinancialBidPhase())) {
                                allQ = allQ.stream()
                                        .filter(q -> "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus())
                                                && "ACCEPTED".equalsIgnoreCase(q.getSpoStatus()))
                                        .collect(Collectors.toList());
                            }
                            allQ.forEach(q -> {
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
                            });
                            quotationRepository.saveAll(allQ);
                        }
                    }
                    break;
                case "SPECIFIC_MEMBER":
                case "ALL_MEMBERS":
                    eval.setEvaluationStatus("PENDING_MEMBER_REVOTE");
                    String revotePhase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";
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
                        // Reset per-vendor member decisions (above 10L)
                        List<TenderCommitteeVendorDecision> vendorVotes =
                                committeeVendorDecisionRepository.findByTenderIdAndPhase(tenderId, revotePhase);
                        vendorVotes.stream()
                                .filter(v -> "MEMBER".equals(v.getVoterRole()))
                                .forEach(v -> {
                                    v.setDecision(null);
                                    v.setRemarks(null);
                                    v.setDecisionDate(null);
                                    v.setConfirmed(false);
                                    v.setConfirmedDate(null);
                                    v.setUpdatedDate(LocalDateTime.now());
                                });
                        committeeVendorDecisionRepository.saveAll(vendorVotes);
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
                        // Reset per-vendor decisions for specific member
                        List<TenderCommitteeVendorDecision> memberVendorVotes =
                                committeeVendorDecisionRepository.findByTenderIdAndCommitteeUserIdAndPhase(
                                        tenderId, dto.getTargetUserId(), revotePhase);
                        memberVendorVotes.forEach(v -> {
                            v.setDecision(null);
                            v.setRemarks(null);
                            v.setDecisionDate(null);
                            v.setConfirmed(false);
                            v.setConfirmedDate(null);
                            v.setUpdatedDate(LocalDateTime.now());
                        });
                        committeeVendorDecisionRepository.saveAll(memberVendorVotes);
                    }
                    break;
                default:
                    throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                            "Unknown clarification target: " + target));
            }

            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);

            // Save to clarification history
            try {
                int nextRound = clarificationHistoryRepository.findMaxRoundByTenderId(tenderId) + 1;

                // Determine which vendors need history rows
                boolean isAllVendors = "ALL_VENDORS".equalsIgnoreCase(originalTarget);
                boolean isVendorTarget = "VENDOR".equalsIgnoreCase(originalTarget)
                        || "ALL_VENDORS".equalsIgnoreCase(originalTarget);

                if (isVendorTarget || reroutedVendorToPP) {
                    // Vendor-targeted clarifications: create per-vendor rows
                    List<String> vendorIds;
                    if (isAllVendors) {
                        // ALL_VENDORS: explode into one row per vendor
                        boolean isDoubleBidFinancial = "DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                                && Boolean.TRUE.equals(eval.getFinancialBidPhase());
                        vendorIds = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                                .stream()
                                .filter(q -> !isDoubleBidFinancial
                                        || "APPROVED".equalsIgnoreCase(q.getTechnicalStatus())
                                        || "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus()))
                                .map(VendorQuotationAgainstTender::getVendorId)
                                .collect(Collectors.toList());
                    } else {
                        // Specific VENDOR: single row
                        String vid = dto.getTargetVendorId();
                        if (vid == null || vid.isBlank()) {
                            vid = eval.getApprovedVendorId();
                        }
                        vendorIds = vid != null ? List.of(vid) : Collections.emptyList();
                    }

                    String historyTarget = reroutedVendorToPP ? "PURCHASE_PERSONNEL" : target.toUpperCase();

                    for (String vid : vendorIds) {
                        TenderClarificationHistory history = new TenderClarificationHistory();
                        history.setTenderId(tenderId);
                        history.setRoundNumber(nextRound);
                        history.setRequestedByRole(dto.getRequestedByRole());
                        history.setRequestedByUserId(dto.getRequestedByUserId());
                        history.setClarificationTarget(historyTarget);
                        history.setTargetVendorId(vid);
                        history.setTargetUserId(dto.getTargetUserId());
                        history.setTargetUserName(dto.getTargetUserName());
                        history.setQuestionRemarks(dto.getRemarks());
                        history.setRequestedAt(LocalDateTime.now());
                        clarificationHistoryRepository.save(history);
                    }
                } else if ("SPO".equalsIgnoreCase(dto.getRequestedByRole())
                        && ("INDENTOR".equalsIgnoreCase(target) || "INDENTOR".equalsIgnoreCase(originalTarget))
                        && (dto.getTargetVendorId() == null || dto.getTargetVendorId().isBlank())) {
                    // SPO all-vendor INDENTOR revision: create per-vendor history rows
                    boolean isDoubleBidFinancial = "DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                            && Boolean.TRUE.equals(eval.getFinancialBidPhase());
                    List<String> affectedVendorIds = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                            .stream()
                            .filter(q -> !isDoubleBidFinancial
                                    || ("ACCEPTED".equalsIgnoreCase(q.getIndentorStatus())
                                        && "ACCEPTED".equalsIgnoreCase(q.getSpoStatus())))
                            .map(VendorQuotationAgainstTender::getVendorId)
                            .collect(Collectors.toList());
                    for (String vid : affectedVendorIds) {
                        TenderClarificationHistory history = new TenderClarificationHistory();
                        history.setTenderId(tenderId);
                        history.setRoundNumber(nextRound);
                        history.setRequestedByRole(dto.getRequestedByRole());
                        history.setRequestedByUserId(dto.getRequestedByUserId());
                        history.setClarificationTarget(target.toUpperCase());
                        history.setTargetVendorId(vid);
                        history.setTargetUserId(dto.getTargetUserId());
                        history.setTargetUserName(dto.getTargetUserName());
                        history.setQuestionRemarks(dto.getRemarks());
                        history.setRequestedAt(LocalDateTime.now());
                        clarificationHistoryRepository.save(history);
                    }
                } else if (reroutedIndentorToPP) {
    // MULTIPLE_INDENT: SPO→INDENTOR rerouted to PP → per-vendor rows
    boolean isDoubleBidFinancial = "DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
            && Boolean.TRUE.equals(eval.getFinancialBidPhase());
    List<String> affectedVendorIds = quotationRepository
            .findByTenderIdAndIsLatestTrue(tenderId)
            .stream()
            .filter(q -> !isDoubleBidFinancial
                    || ("ACCEPTED".equalsIgnoreCase(q.getIndentorStatus())
                        && "ACCEPTED".equalsIgnoreCase(q.getSpoStatus())))
            .map(VendorQuotationAgainstTender::getVendorId)
            .collect(Collectors.toList());
    for (String vid : affectedVendorIds) {
        TenderClarificationHistory h = new TenderClarificationHistory();
        h.setTenderId(tenderId);
        h.setRoundNumber(nextRound);
        h.setRequestedByRole(dto.getRequestedByRole());
        h.setRequestedByUserId(dto.getRequestedByUserId());
        h.setClarificationTarget("PURCHASE_PERSONNEL");
        h.setTargetVendorId(vid);
        h.setTargetUserId(dto.getTargetUserId());
        h.setTargetUserName(dto.getTargetUserName());
        h.setQuestionRemarks(dto.getRemarks());
        h.setRequestedAt(LocalDateTime.now());
        clarificationHistoryRepository.save(h);
    }
} 
                else {
                    // Non-vendor targets (INDENTOR per-vendor, CHAIRMAN, PURCHASE_PERSONNEL direct,
                    // SPECIFIC_MEMBER, ALL_MEMBERS): single row
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
                    && !"PENDING_MEMBER_REVOTE".equals(currentStatus)
                    && !"PENDING_CHAIRMAN_CLARIFICATION_REVIEW".equals(currentStatus)) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Tender is not pending any clarification response. Current status: " + currentStatus));
            }

            String respondedByRole = dto.getRespondedByRole();

            // Chairman responding to Director's clarification request
            if ("CHAIRMAN".equalsIgnoreCase(respondedByRole)
                    && "PENDING_CHAIRMAN_CLARIFICATION_REVIEW".equals(currentStatus)) {
                List<TenderClarificationHistory> openRows = clarificationHistoryRepository
                        .findByTenderIdAndClarificationTargetAndRespondedAtIsNull(tenderId, "CHAIRMAN");
                openRows.forEach(h -> {
                    h.setResponseText(dto.getResponseText());
                    h.setResponseFileName(dto.getResponseFileName());
                    h.setRespondedByRole("CHAIRMAN");
                    h.setRespondedById(String.valueOf(dto.getRespondedById()));
                    h.setRespondedAt(LocalDateTime.now());
                });
                clarificationHistoryRepository.saveAll(openRows);

                String prev = eval.getPreviousEvaluationStatus();
                eval.setEvaluationStatus(prev != null ? prev : "PENDING_DIRECTOR_APPROVAL");
                eval.setClarificationPendingFrom(null);
                eval.setClarificationPendingFromId(null);
                eval.setClarificationPendingFromName(null);
                eval.setClarificationRequestedByRole(null);
                eval.setClarificationRemarks(null);
                eval.setClarificationTargetVendorId(null);
                eval.setPreviousEvaluationStatus(null);
                eval.setUpdatedDate(LocalDateTime.now());
                tenderEvaluationRepository.save(eval);
                return buildStatusDto(eval, tender, tenderId);
            }

            if ("VENDOR".equalsIgnoreCase(respondedByRole)) {
                String vendorId = dto.getRespondedById();

                // 1. Update clarification history FIRST — PK-based when available, filtered fallback otherwise
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
                        clarificationHistoryRepository.saveAndFlush(h);
                    });
                } catch (Exception e) {
                    log.warn("Clarification history update failed: {}", e.getMessage());
                }

                // 2. Check remaining open VENDOR-targeted questions for this vendor
                List<TenderClarificationHistory> remainingOpen =
                        clarificationHistoryRepository.findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(tenderId, vendorId)
                                .stream()
                                .filter(h -> Set.of("VENDOR", "ALL_VENDORS").contains(h.getClarificationTarget()))
                                .collect(Collectors.toList());

                // 3. Only mark quotation as CHANGE_RESPONDED if NO more open questions
            if (remainingOpen.isEmpty()) {
                    quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                            .ifPresent(q -> {
                                q.setVendorResponse(dto.getResponseText());
                                if (dto.getResponseFileName() != null) {
                                    q.setClarificationFileName(dto.getResponseFileName());
                                }
                                q.setStatus("CHANGE_RESPONDED");
                                // If SPO was the one who sought this clarification,
                                // reset spoStatus back so SPO buttons become enabled again
                                if ("CHANGE_REQUESTED".equalsIgnoreCase(q.getSpoStatus())) {
                                    q.setSpoStatus("PENDING");
                                }
                                if ("CHANGE_REQUESTED".equalsIgnoreCase(q.getFinancialSpoStatus())) {
                                    q.setFinancialSpoStatus("PENDING");
                                }
                                q.setUpdatedDate(LocalDateTime.now());
                                quotationRepository.save(q);
                            });
                } else {
                    // Still has open questions — update response text on quotation but keep CHANGE_REQUESTED
                    quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                            .ifPresent(q -> {
                                q.setVendorResponse(dto.getResponseText());
                                if (dto.getResponseFileName() != null) {
                                    q.setClarificationFileName(dto.getResponseFileName());
                                }
                                q.setUpdatedDate(LocalDateTime.now());
                                quotationRepository.save(q);
                            });
                }
                // Fall through to unified restore gate below
            } else if ("PURCHASE_PERSONNEL".equalsIgnoreCase(respondedByRole)
                        && dto.getVendorId() != null && !dto.getVendorId().isBlank()) {
                // PP responding per-vendor on behalf of vendor (GEM/OPEN/GLOBAL mode)
                String ppVendorId = dto.getVendorId();
                log.info("PP respond-clarification: tenderId={}, ppVendorId={}, historyId={}", tenderId, ppVendorId, dto.getClarificationHistoryId());

                // 1. Update clarification history FIRST — PK-based when available, filtered fallback otherwise
                boolean historyMarkedResponded = false;
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
                    if (historyRow.isPresent()) {
                        TenderClarificationHistory h = historyRow.get();
                        log.info("PP respond: found history row id={}, target={}, vendorId={}", h.getId(), h.getClarificationTarget(), h.getTargetVendorId());
                        h.setResponseText(dto.getResponseText());
                        h.setResponseFileName(dto.getResponseFileName());
                        h.setRespondedByRole("PURCHASE_PERSONNEL");
                        h.setRespondedById(dto.getRespondedById());
                        h.setRespondedAt(LocalDateTime.now());
                        clarificationHistoryRepository.saveAndFlush(h);
                        historyMarkedResponded = true;
                    } else {
                        log.warn("PP respond: NO history row found for tenderId={}, vendorId={}, historyId={}", tenderId, ppVendorId, dto.getClarificationHistoryId());
                    }
                } catch (Exception e) {
                    log.warn("Clarification history update failed: {}", e.getMessage(), e);
                }

                // 2. Check remaining open PP-targeted questions for this vendor
                List<TenderClarificationHistory> allOpenForVendor =
                        clarificationHistoryRepository.findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(tenderId, ppVendorId);
                log.info("PP respond: allOpenForVendor count={}, targets={}", allOpenForVendor.size(),
                        allOpenForVendor.stream().map(TenderClarificationHistory::getClarificationTarget).collect(Collectors.toList()));
                List<TenderClarificationHistory> remainingOpen = allOpenForVendor
                                .stream()
                                .filter(h -> "PURCHASE_PERSONNEL".equals(h.getClarificationTarget()))
                                .collect(Collectors.toList());
                log.info("PP respond: remainingOpen (PP-targeted only) count={}", remainingOpen.size());

                // 3. Only mark quotation as CHANGE_RESPONDED if NO more open questions
                if (remainingOpen.isEmpty()) {
                    log.info("PP respond: marking quotation CHANGE_RESPONDED for vendorId={}", ppVendorId);
                    quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, ppVendorId)
                            .ifPresent(q -> {
                                q.setVendorResponse(dto.getResponseText());
                                if (dto.getResponseFileName() != null) {
                                    q.setClarificationFileName(dto.getResponseFileName());
                                }
                                q.setStatus("CHANGE_RESPONDED");
                                // If SPO was the one who sought this clarification,
                                // reset spoStatus back so SPO buttons become enabled again
                                if ("CHANGE_REQUESTED".equalsIgnoreCase(q.getSpoStatus())) {
                                    q.setSpoStatus("PENDING");
                                }
                                if ("CHANGE_REQUESTED".equalsIgnoreCase(q.getFinancialSpoStatus())) {
                                    q.setFinancialSpoStatus("PENDING");
                                }
                                q.setUpdatedDate(LocalDateTime.now());
                                quotationRepository.saveAndFlush(q);
                            });
                } else {
                    log.info("PP respond: keeping CHANGE_REQUESTED, still {} open PP questions", remainingOpen.size());
                    // Still has open questions — update response text on quotation but keep CHANGE_REQUESTED
                    quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, ppVendorId)
                            .ifPresent(q -> {
                                q.setVendorResponse(dto.getResponseText());
                                if (dto.getResponseFileName() != null) {
                                    q.setClarificationFileName(dto.getResponseFileName());
                                }
                                q.setUpdatedDate(LocalDateTime.now());
                                quotationRepository.save(q);
                            });
                }
                // Fall through to unified restore gate below
            // } else {
            //     // Indentor/PP (global)/member response — PK-based when available, filtered fallback otherwise
            //     try {
            //         Optional<TenderClarificationHistory> historyRow;
            //         if (dto.getClarificationHistoryId() != null) {
            //             historyRow = clarificationHistoryRepository.findById(dto.getClarificationHistoryId())
            //                     .filter(h -> tenderId.equals(h.getTenderId()) && h.getRespondedAt() == null);
            //         } else {
            //             Set<String> elseTargets = Set.of("INDENTOR", "CHAIRMAN",
            //                     "PURCHASE_PERSONNEL", "SPECIFIC_MEMBER", "ALL_MEMBERS");
            //             historyRow = clarificationHistoryRepository
            //                     .findByTenderIdOrderByRequestedAtDesc(tenderId).stream()
            //                     .filter(h -> h.getRespondedAt() == null
            //                             && elseTargets.contains(h.getClarificationTarget()))
            //                     .findFirst();
            //         }
            //         historyRow.ifPresent(h -> {
            //             h.setResponseText(dto.getResponseText());
            //             h.setResponseFileName(dto.getResponseFileName());
            //             h.setRespondedByRole(dto.getRespondedByRole());
            //             h.setRespondedById(dto.getRespondedById());
            //             h.setRespondedAt(LocalDateTime.now());
            //             clarificationHistoryRepository.save(h);
            //         });
            //     } catch (Exception e) {
            //         log.warn("Clarification history update failed: {}", e.getMessage());
            //     }
            // }

            } else {
                // Indentor/PP (global)/member response — PK-based when available, filtered fallback otherwise
                try {
                    if (dto.getClarificationHistoryId() != null) {
                        // PK-based: single known row — close just that one
                        clarificationHistoryRepository.findById(dto.getClarificationHistoryId())
                                .filter(h -> tenderId.equals(h.getTenderId()) && h.getRespondedAt() == null)
                                .ifPresent(h -> {
                                    h.setResponseText(dto.getResponseText());
                                    h.setResponseFileName(dto.getResponseFileName());
                                    h.setRespondedByRole(dto.getRespondedByRole());
                                    h.setRespondedById(dto.getRespondedById());
                                    h.setRespondedAt(LocalDateTime.now());
                                    clarificationHistoryRepository.save(h);
                                });
                    } else {
                        // Role-targeted: close ALL open rows whose clarificationTarget
                        // matches the responding role — prevents cross-contamination
                        // when multiple concurrent clarification types are open
                        // (e.g. INDENTOR + SPECIFIC_MEMBER rows both open simultaneously).
                        String expectedTarget = resolveExpectedClarificationTarget(respondedByRole);
                        List<TenderClarificationHistory> rowsToClose = clarificationHistoryRepository
                                .findByTenderIdOrderByRequestedAtDesc(tenderId).stream()
                                .filter(h -> h.getRespondedAt() == null
                                        && expectedTarget.equals(h.getClarificationTarget()))
                                .collect(Collectors.toList());
                        rowsToClose.forEach(h -> {
                            h.setResponseText(dto.getResponseText());
                            h.setResponseFileName(dto.getResponseFileName());
                            h.setRespondedByRole(dto.getRespondedByRole());
                            h.setRespondedById(dto.getRespondedById());
                            h.setRespondedAt(LocalDateTime.now());
                        });
                        if (!rowsToClose.isEmpty()) {
                            clarificationHistoryRepository.saveAll(rowsToClose);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Clarification history update failed: {}", e.getMessage());
                }
            }

            // When Indentor or PP (multi-indent) responds to SPO's clarification request,
            // reset the vendor's quotation status from CHANGE_REQUESTED → SUBMITTED
            // so SPO buttons become enabled again.
            if (("INDENTOR".equalsIgnoreCase(respondedByRole) || "PURCHASE_PERSONNEL".equalsIgnoreCase(respondedByRole))
                    && "PENDING_INDENTOR_CLARIFICATION".equals(currentStatus)) {

                // Per-question response: determine vendor from the answered history row
                String targetVid = null;
                if (dto.getClarificationHistoryId() != null) {
                    Optional<TenderClarificationHistory> answeredRow =
                            clarificationHistoryRepository.findById(dto.getClarificationHistoryId());
                    if (answeredRow.isPresent() && answeredRow.get().getTargetVendorId() != null) {
                        targetVid = answeredRow.get().getTargetVendorId();
                    }
                }
                // Fallback to eval-level target vendor
                if (targetVid == null || targetVid.isBlank()) {
                    targetVid = eval.getClarificationTargetVendorId();
                }

                if (targetVid != null && !targetVid.isBlank()) {
                    // Check if all INDENTOR-targeted questions for this vendor are answered
                    final String vid = targetVid;
                    long openForVendor = clarificationHistoryRepository
                            .findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(tenderId, vid)
                            .stream()
                            .filter(h -> "INDENTOR".equals(h.getClarificationTarget())
                                    || "PURCHASE_PERSONNEL".equals(h.getClarificationTarget()))
                            .count();
                    if (openForVendor == 0) {
                        quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vid)
                                .ifPresent(q -> {
                                    if ("CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus())) {
                                        q.setStatus("SUBMITTED");
                                        q.setUpdatedDate(LocalDateTime.now());
                                        quotationRepository.save(q);
                                    }
                                });
                    }
                } else {
                    // No specific vendor — check each CHANGE_REQUESTED vendor
                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                            .stream()
                            .filter(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()))
                            .forEach(q -> {
                                long openForVendor = clarificationHistoryRepository
                                        .findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(tenderId, q.getVendorId())
                                        .stream()
                                        .filter(h -> "INDENTOR".equals(h.getClarificationTarget())
                                                || "PURCHASE_PERSONNEL".equals(h.getClarificationTarget()))
                                        .count();
                                if (openForVendor == 0) {
                                    q.setStatus("SUBMITTED");
                                    q.setUpdatedDate(LocalDateTime.now());
                                    quotationRepository.save(q);
                                }
                            });
                }
            }

            // ── Unified restore gate ────────────────────────────────────────
            boolean quotationsResolved = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                    .stream()
                    .noneMatch(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()));
            long openHistoryRows;
            if (quotationsResolved) {
                openHistoryRows = clarificationHistoryRepository
                        .findByTenderIdOrderByRequestedAtDesc(tenderId)
                        .stream()
                        .filter(h -> h.getRespondedAt() == null)
                        .filter(h -> !Set.of("VENDOR", "ALL_VENDORS", "PURCHASE_PERSONNEL")
                                .contains(h.getClarificationTarget()))
                        .count();
            } else {
                openHistoryRows = clarificationHistoryRepository.countByTenderIdAndRespondedAtIsNull(tenderId);
            }



            if (!quotationsResolved || openHistoryRows > 0) {
                // Vendor-side resolved but non-vendor clarifications still open:
                // revert status from PENDING_VENDOR_CLARIFICATION to match what's actually pending
                if (quotationsResolved && "PENDING_VENDOR_CLARIFICATION".equals(eval.getEvaluationStatus())) {
                    List<TenderClarificationHistory> openRows =
                            clarificationHistoryRepository.findByTenderIdOrderByRequestedAtDesc(tenderId)
                                    .stream()
                                    .filter(h -> h.getRespondedAt() == null)
                                    .collect(Collectors.toList());

                    boolean hasVendorPending = openRows.stream()
                            .anyMatch(h -> Set.of("VENDOR", "ALL_VENDORS", "PURCHASE_PERSONNEL")
                                    .contains(h.getClarificationTarget()));

                    if (!hasVendorPending) {
                        boolean hasIndentorPending = openRows.stream()
                                .anyMatch(h -> Set.of("INDENTOR", "CHAIRMAN")
                                        .contains(h.getClarificationTarget()));
                        boolean hasMemberPending = openRows.stream()
                                .anyMatch(h -> Set.of("SPECIFIC_MEMBER", "ALL_MEMBERS")
                                        .contains(h.getClarificationTarget()));

                        if (hasIndentorPending) {
                            eval.setEvaluationStatus("PENDING_INDENTOR_CLARIFICATION");
                            openRows.stream()
                                    .filter(h -> Set.of("INDENTOR", "CHAIRMAN").contains(h.getClarificationTarget()))
                                    .findFirst()
                                    .ifPresent(h -> {
                                        eval.setClarificationPendingFrom(h.getClarificationTarget());
                                        eval.setClarificationPendingFromId(h.getTargetUserId());
                                        eval.setClarificationPendingFromName(h.getTargetUserName());
                                        eval.setClarificationRequestedByRole(h.getRequestedByRole());
                                        eval.setClarificationRemarks(h.getQuestionRemarks());
                                    });
                        } else if (hasMemberPending) {
                            eval.setEvaluationStatus("PENDING_MEMBER_REVOTE");
                        }
                    }
                }
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
            if (!"PENDING_DIRECTOR_REVIEW".equals(eval.getEvaluationStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Committee formation can only be done in PENDING_DIRECTOR_REVIEW status. Current: "
                        + eval.getEvaluationStatus()));
            }

            // Save ad-hoc chairman and co-chairman on the eval record
            eval.setAdHocChairmanUserId(dto.getChairmanUserId());
            eval.setAdHocChairmanName(dto.getChairmanName());
            eval.setAdHocCoChairmanUserId(dto.getCoChairmanUserId());
            eval.setAdHocCoChairmanName(dto.getCoChairmanName());
            eval.setEvaluationStatus("PENDING_CHAIRMAN_REVIEW"); // Chairman selects expert next
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);

            // Auto-assign Committee Chairman role to chairman, Member role to co-chairman
            ensureCommitteeChairmanRole(dto.getChairmanUserId());
            if (dto.getCoChairmanUserId() != null) {
                ensureCommitteeMemberRole(dto.getCoChairmanUserId());
            }

            // Create vote rows for each ad-hoc member + auto-assign role
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
                    ensureCommitteeMemberRole(member.getUserId());
                }
            }

            return buildStatusDto(eval, tender, tenderId);
        }

        // ─────────────────────────────────────────────────────────────────
        // 14b. CHAIRMAN SELECTS EXPERT (Above 1 Crore)
        //      Chairman picks domain expert → added to committee → evaluation starts
        // ─────────────────────────────────────────────────────────────────
        @Transactional
        @Override
        public TenderEvaluationStatusDto chairmanSelectExpert(String tenderId,
                                                            Integer chairmanUserId,
                                                            Integer expertUserId,
                                                            String expertName) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            if (!"ABOVE_1_CRORE".equals(eval.getAmountCategory())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Expert selection is only for ABOVE_1_CRORE tenders."));
            }
            if (!"PENDING_CHAIRMAN_REVIEW".equals(eval.getEvaluationStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Expert selection can only be done in PENDING_CHAIRMAN_REVIEW status. Current: "
                        + eval.getEvaluationStatus()));
            }
            if (!chairmanUserId.equals(eval.getAdHocChairmanUserId())) {
                throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "Only the ad-hoc Chairman (" + eval.getAdHocChairmanName()
                        + ") can select the expert."));
            }

            eval.setExpertUserId(expertUserId);
            eval.setExpertName(expertName);

            // Add expert as committee member if not already present + auto-assign role
            boolean expertExists = committeeDecisionRepository
                    .findByTenderIdAndCommitteeUserId(tenderId, expertUserId).isPresent();
            if (!expertExists) {
                TenderCommitteeDecision row = new TenderCommitteeDecision();
                row.setTenderId(tenderId);
                row.setCommitteeUserId(expertUserId);
                row.setCommitteeMemberName(expertName + " (Expert)");
                row.setCreatedDate(LocalDateTime.now());
                row.setUpdatedDate(LocalDateTime.now());
                committeeDecisionRepository.save(row);
            }
            ensureCommitteeMemberRole(expertUserId);

            // Advance: Double Bid → technical evaluation, Single Bid → financial evaluation
            if ("DOUBLE_BID".equalsIgnoreCase(eval.getBidType())) {
                eval.setEvaluationStatus("PENDING_TECHNICAL");
            } else {
                eval.setEvaluationStatus("PENDING_FINANCIAL");
            }

            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);

            log.info("Chairman {} selected expert {} for tender {}. Status → {}",
                    chairmanUserId, expertUserId, tenderId, eval.getEvaluationStatus());

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

        // ─────────────────────────────────────────────────────────────────
        // 16b. GET OPEN CLARIFICATIONS FOR A VENDOR
        // ─────────────────────────────────────────────────────────────────
        @Transactional(readOnly = true)
        @Override
        public List<TenderClarificationHistory> getOpenClarifications(String tenderId, String vendorId) {
            // Only return vendor/PP-targeted open questions (not INDENTOR/CHAIRMAN rows that happen to share the vendorId)
            return clarificationHistoryRepository
                    .findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(tenderId, vendorId)
                    .stream()
                    .filter(h -> Set.of("VENDOR", "ALL_VENDORS", "PURCHASE_PERSONNEL").contains(h.getClarificationTarget()))
                    .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        @Override
        // public List<TenderClarificationHistory> getOpenIndentorClarifications(String tenderId) {
        //     return clarificationHistoryRepository
        //             .findByTenderIdAndClarificationTargetAndRespondedAtIsNull(tenderId, "INDENTOR");
        // }
        // AFTER
    public List<TenderClarificationHistory> getOpenIndentorClarifications(String tenderId) {
        return clarificationHistoryRepository
                .findByTenderIdOrderByRequestedAtDesc(tenderId)
                .stream()
                .filter(h -> h.getRespondedAt() == null)
                .filter(h -> "INDENTOR".equalsIgnoreCase(h.getClarificationTarget())
                        || "PURCHASE_PERSONNEL".equalsIgnoreCase(h.getClarificationTarget()))
                .collect(Collectors.toList());
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
                    "PENDING_DIRECTOR_APPROVAL", "PENDING_COMMITTEE_FORMATION",
                    "PENDING_CHAIRMAN_REVIEW", "PENDING_DIRECTOR_REVIEW");
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
            boolean wasChangeRequested = "CHANGE_REQUESTED".equalsIgnoreCase(quotation.getStatus());
            if (wasChangeRequested) {
                boolean canIndentorAct = VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR
                                    .equals(quotation.getNextRole());
                // if ("ACCEPTED".equals(normalizedDecision) && !canIndentorAct ) {
                //     throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                //             "Cannot accept a vendor that is under seek clarification. "
                //             + "Resolve the clarification first, or reject the vendor."));
                // }
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

            // // Close pending clarifications when a CHANGE_REQUESTED vendor is accepted or rejected
            // if (wasChangeRequested) {
            //     closePendingClarificationsForVendor(tenderId, vendorId,
            //             "INDENTOR", evaluatorUserId, remarks, eval);
            // }

            // Close pending vendor clarifications when indentor rejects a CHANGE_REQUESTED vendor.
            // This applies in any status where the vendor was awaiting clarification — not just
            // PENDING_VENDOR_CLARIFICATION. In double bid, the eval status may be PENDING_INITIATION,
            // PENDING_FINANCIAL, etc. while individual vendors have CHANGE_REQUESTED status.
            // Skip only PENDING_INDENTOR_CLARIFICATION when the clarification is targeted at indentor
            // (SPO→indentor flow) — indentor must still submit their response card in that case.
            if (wasChangeRequested && "REJECTED".equals(normalizedDecision)) {
                boolean isIndentorClarifTarget = "PENDING_INDENTOR_CLARIFICATION".equals(eval.getEvaluationStatus())
                        && clarificationHistoryRepository.findByTenderIdOrderByRequestedAtDesc(tenderId)
                                .stream()
                                .anyMatch(h -> h.getRespondedAt() == null
                                        && vendorId.equals(h.getTargetVendorId())
                                        && ("INDENTOR".equalsIgnoreCase(h.getClarificationTarget())
                                            || "PURCHASE_PERSONNEL".equalsIgnoreCase(h.getClarificationTarget())));
                if (!isIndentorClarifTarget) {
                    closePendingClarificationsForVendor(tenderId, vendorId,
                            "INDENTOR", evaluatorUserId, remarks, eval);
                }
            }

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
            boolean wasChangeRequested = "CHANGE_REQUESTED".equalsIgnoreCase(quotation.getStatus());
            if (wasChangeRequested) {
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

            // Close pending clarifications when a CHANGE_REQUESTED vendor is rejected
            if (wasChangeRequested && "REJECTED".equals(normalizedDecision)) {
                closePendingClarificationsForVendor(tenderId, vendorId,
                        "SPO", spoUserId, remarks, eval);
            }

            return buildStatusDto(eval, tender, tenderId);
        }

        // ─────────────────────────────────────────────────────────────────
        // HELPER: Close pending clarifications when a vendor is rejected
        // ─────────────────────────────────────────────────────────────────
        /**
         * Closes all open {@link TenderClarificationHistory} rows for a specific vendor,
         * then runs the unified restore gate to potentially restore the evaluation status
         * if no other clarifications remain open.
         *
         * <p>Called from {@code saveVendorIndentorDecision} and {@code saveVendorSpoDecision}
         * when a CHANGE_REQUESTED vendor is rejected.</p>
         */
        private void closePendingClarificationsForVendor(String tenderId,
                                                        String vendorId,
                                                        String rejectingRole,
                                                        Integer rejectingUserId,
                                                        String remarks,
                                                        TenderEvaluation eval) {
            // 1. Close all open clarification history rows for the vendor
            List<TenderClarificationHistory> rowsToClose =
                    clarificationHistoryRepository.findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(
                            tenderId, vendorId);

            for (TenderClarificationHistory h : rowsToClose) {
                h.setRespondedAt(LocalDateTime.now());
                h.setRespondedByRole(rejectingRole);
                h.setRespondedById(String.valueOf(rejectingUserId));
                h.setResponseText("Vendor rejected by " + rejectingRole + ": "
                        + (remarks != null ? remarks : ""));
            }
            if (!rowsToClose.isEmpty()) {
                clarificationHistoryRepository.saveAll(rowsToClose);
            }

            // 2. Unified restore gate — restore evaluation status if ALL
            //    clarifications and CHANGE_REQUESTED quotations are resolved
            boolean quotationsResolved = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                    .stream()
                    .noneMatch(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()));
            // long openHistoryRows = clarificationHistoryRepository
            //         .countByTenderIdAndRespondedAtIsNull(tenderId);
    long openHistoryRows;
            if (quotationsResolved) {
                // All vendor quotations resolved — only block restore if non-vendor rows are still open
                // (e.g. a separate INDENTOR/CHAIRMAN clarification round still pending)
                openHistoryRows = clarificationHistoryRepository
                        .findByTenderIdOrderByRequestedAtDesc(tenderId)
                        .stream()
                        .filter(h -> h.getRespondedAt() == null)
                        .filter(h -> !Set.of("VENDOR", "ALL_VENDORS", "PURCHASE_PERSONNEL")
                                .contains(h.getClarificationTarget()))
                        .count();
            } else {
                openHistoryRows = clarificationHistoryRepository
                        .countByTenderIdAndRespondedAtIsNull(tenderId);
            }

            if (!quotationsResolved || openHistoryRows > 0) {
                // Not fully resolved yet — just save timestamp, caller continues
                eval.setUpdatedDate(LocalDateTime.now());
                tenderEvaluationRepository.save(eval);
                return;
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
        }

        // ─────────────────────────────────────────────────────────────────
        // 19b. SPO REJECTS INDENTOR CLARIFICATION (closes history, restores flow)
        // ─────────────────────────────────────────────────────────────────
        @Transactional
        @Override
        public TenderEvaluationStatusDto rejectIndentorClarification(String tenderId, String vendorId,
                                                                    Integer spoUserId, String remarks) {
            log.info("SPO reject indentor clarification tenderId={} vendorId={} by userId={}",
                    tenderId, vendorId, spoUserId);
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            // 1. Close all open clarification history rows targeted at INDENTOR for this vendor
            List<TenderClarificationHistory> rowsToClose =
                    clarificationHistoryRepository.findByTenderIdOrderByRequestedAtDesc(tenderId)
                            .stream()
                            .filter(h -> h.getRespondedAt() == null)
                            .filter(h -> "INDENTOR".equalsIgnoreCase(h.getClarificationTarget())
                                    || "PURCHASE_PERSONNEL".equalsIgnoreCase(h.getClarificationTarget()))
                            .filter(h -> vendorId.equals(h.getTargetVendorId()))
                            .collect(Collectors.toList());

            for (TenderClarificationHistory h : rowsToClose) {
                h.setRespondedAt(LocalDateTime.now());
                h.setRespondedByRole("SPO");
                h.setRespondedById(String.valueOf(spoUserId));
                h.setResponseText("Indentor clarification rejected by SPO: "
                        + (remarks != null ? remarks : ""));
            }
            if (!rowsToClose.isEmpty()) {
                clarificationHistoryRepository.saveAll(rowsToClose);
            }

            // 2. Restore the vendor quotation status from CHANGE_REQUESTED
            quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                    .ifPresent(q -> {
                        if ("CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus())) {
                            q.setStatus("SUBMITTED");
                        }
                        q.setUpdatedBy(String.valueOf(spoUserId));
                        q.setUpdatedDate(LocalDateTime.now());
                        quotationRepository.save(q);
                    });

            // 3. Unified restore gate — restore evaluation status if all clarifications resolved
            boolean quotationsResolved = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                    .stream()
                    .noneMatch(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()));

            long openHistoryRows;
            if (quotationsResolved) {
                openHistoryRows = clarificationHistoryRepository
                        .findByTenderIdOrderByRequestedAtDesc(tenderId)
                        .stream()
                        .filter(h -> h.getRespondedAt() == null)
                        .count();
            } else {
                openHistoryRows = clarificationHistoryRepository
                        .countByTenderIdAndRespondedAtIsNull(tenderId);
            }

            if (quotationsResolved && openHistoryRows == 0) {
                String restoreStatus = eval.getPreviousEvaluationStatus();
                if (restoreStatus == null || restoreStatus.isBlank()) {
                    restoreStatus = "PENDING_SPO_APPROVAL";
                }
                eval.setEvaluationStatus(restoreStatus);
                eval.setPreviousEvaluationStatus(null);
                eval.setClarificationPendingFrom(null);
                eval.setClarificationPendingFromId(null);
                eval.setClarificationPendingFromName(null);
                eval.setClarificationRequestedByRole(null);
                eval.setClarificationRemarks(null);
                eval.setClarificationTargetVendorId(null);
            }
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);

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

        private void validateNoClarificationPending(String tenderId, boolean financialPhase) {
            List<VendorQuotationAgainstTender> quotations =
                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
            if (quotations.isEmpty()) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "No vendor quotations found for this tender."));
            }
            List<String> underClarification = quotations.stream()
                    .filter(q -> {
                        if (financialPhase
                                && ("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                    || "REJECTED".equalsIgnoreCase(q.getSpoStatus()))) {
                            return false;
                        }
                        return "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus());
                    })
                    .map(VendorQuotationAgainstTender::getVendorId)
                    .collect(Collectors.toList());
            if (!underClarification.isEmpty()) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Cannot confirm evaluation — vendor(s) under seek clarification: "
                        + String.join(", ", underClarification)
                        + ". Please resolve clarification or reject the vendor(s) first."));
            }
        }

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
                    .filter(q -> {
                        if (financialPhase
                                && ("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                    || "REJECTED".equalsIgnoreCase(q.getSpoStatus()))) {
                            return false;
                        }
                        return "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus());
                    })
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
                        if (financialPhase
                                && ("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                    || "REJECTED".equalsIgnoreCase(q.getSpoStatus()))) {
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
                    .filter(q -> {
                        if (financialPhase
                                && ("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                    || "REJECTED".equalsIgnoreCase(q.getSpoStatus()))) {
                            return false;
                        }
                        return "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus());
                    })
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

        private void validateAllPerVendorVotes(String tenderId, String phase,
                                                String voterRole, Integer voterId,
                                                TenderEvaluation eval) {
            List<VendorQuotationAgainstTender> quotations =
                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
            long eligibleCount = "FINANCIAL".equals(phase)
                    ? quotations.stream()
                        .filter(q -> !("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                    || "REJECTED".equalsIgnoreCase(q.getSpoStatus())))
                        .count()
                    : quotations.size();
            List<TenderCommitteeVendorDecision> votes = committeeVendorDecisionRepository
                    .findByTenderIdAndPhaseAndVoterRole(tenderId, phase, voterRole);
            long decidedCount = votes.stream().filter(v -> v.getDecision() != null).count();
            if (decidedCount < eligibleCount) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Please Accept or Reject all vendors before confirming. "
                        + voterRole + " voted: " + decidedCount + "/" + eligibleCount));
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
            dto.setExpertUserId(eval.getExpertUserId());
            dto.setExpertName(eval.getExpertName());

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

                Integer chairmanId = eval.getAdHocChairmanUserId() != null
                        ? eval.getAdHocChairmanUserId()
                        : resolveChairmanUserId(eval);
                Integer coChairmanId = eval.getAdHocCoChairmanUserId();

                List<TenderEvaluationStatusDto.CommitteeVoteDto> votes = decisions.stream()
                        .filter(d -> d.getCommitteeMemberName() != null)
                        .map(d -> {
                            TenderEvaluationStatusDto.CommitteeVoteDto v =
                                    new TenderEvaluationStatusDto.CommitteeVoteDto();
                            v.setCommitteeUserId(d.getCommitteeUserId());
                            v.setCommitteeMemberName(d.getCommitteeMemberName());
                            if (d.getCommitteeUserId() != null && d.getCommitteeUserId().equals(chairmanId)) {
                                v.setRole("CHAIRMAN");
                            } else if (coChairmanId != null && d.getCommitteeUserId() != null
                                    && d.getCommitteeUserId().equals(coChairmanId)) {
                                v.setRole("CO_CHAIRMAN");
                            } else {
                                v.setRole("MEMBER");
                            }
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

                // Pending member→chairman clarifications (per-member per-vendor)
                List<TenderClarificationHistory> pendingMemberClarifs =
                        clarificationHistoryRepository.findByTenderIdAndRequestedByRoleAndClarificationTargetAndRespondedAtIsNull(
                                tenderId, "COMMITTEE_MEMBER", "CHAIRMAN");
                if (!pendingMemberClarifs.isEmpty()) {
                    List<TenderEvaluationStatusDto.PendingMemberClarificationDto> pmcList = pendingMemberClarifs.stream()
                            .map(h -> {
                                TenderEvaluationStatusDto.PendingMemberClarificationDto pmc =
                                        new TenderEvaluationStatusDto.PendingMemberClarificationDto();
                                pmc.setHistoryId(h.getId());
                                pmc.setMemberId(h.getRequestedByUserId());
                                UserMaster mu = h.getRequestedByUserId() != null
                                        ? userMasterRepository.findByUserId(h.getRequestedByUserId()) : null;
                                pmc.setMemberName(mu != null ? mu.getUserName() : String.valueOf(h.getRequestedByUserId()));
                                pmc.setVendorId(h.getTargetVendorId());
                                pmc.setRemarks(h.getQuestionRemarks());
                                pmc.setRequestedAt(h.getRequestedAt());
                                return pmc;
                            }).collect(Collectors.toList());
                    dto.setPendingMemberClarifications(pmcList);
                }

                // Per-vendor committee votes (above-10L)
                boolean isDoubleBidFinancial = "DOUBLE_BID".equalsIgnoreCase(eval.getBidType())
                        && Boolean.TRUE.equals(eval.getFinancialBidPhase());

                List<TenderCommitteeVendorDecision> vendorVotes;
                if (isDoubleBidFinancial) {
                    // Return BOTH phases so frontend can show technical history + financial voting
                    List<TenderCommitteeVendorDecision> techVotes =
                            committeeVendorDecisionRepository.findByTenderIdAndPhase(tenderId, "TECHNICAL");
                    List<TenderCommitteeVendorDecision> finVotes =
                            committeeVendorDecisionRepository.findByTenderIdAndPhase(tenderId, "FINANCIAL");
                    vendorVotes = new java.util.ArrayList<>(techVotes);
                    vendorVotes.addAll(finVotes);
                } else {
                    String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";
                    vendorVotes = committeeVendorDecisionRepository.findByTenderIdAndPhase(tenderId, phase);
                }

                Map<String, List<CommitteeVendorVoteDto>> voteMap = vendorVotes.stream()
                        .map(v -> {
                            CommitteeVendorVoteDto vDto = new CommitteeVendorVoteDto();
                            vDto.setCommitteeUserId(v.getCommitteeUserId());
                            vDto.setMemberName(v.getMemberName());
                            vDto.setDecision(v.getDecision());
                            vDto.setRemarks(v.getRemarks());
                            vDto.setDecisionDate(v.getDecisionDate());
                            vDto.setConfirmed(v.getConfirmed());
                            vDto.setVoterRole(v.getVoterRole());
                            vDto.setPhase(v.getPhase());
                            return Map.entry(v.getVendorId(), vDto);
                        })
                        .collect(Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
                dto.setCommitteeVendorVotes(voteMap);
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

            // PURCHASE_PERSONNEL: gem/open/global vendor clarification routes back to self
            if ("PURCHASE_PERSONNEL".equalsIgnoreCase(role)) {
                if ("VENDOR".equalsIgnoreCase(target) || "ALL_VENDORS".equalsIgnoreCase(target)) {
                    if (isGemOpenGlobal(mode)) {
                        return "PURCHASE_PERSONNEL";
                    }
                }
            }

            // SPO seeking from indentor: multiple indent → PP handles; single indent → specific indentor
            if ("SPO".equalsIgnoreCase(role) && "INDENTOR".equalsIgnoreCase(target)) {
                if ("MULTIPLE_INDENT".equals(eval.getIndentCategory())) {
                    return "PURCHASE_PERSONNEL";
                }
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

            // Update committee vote records to use the new vendorId
            for (String phase : List.of("TECHNICAL", "FINANCIAL")) {
                List<TenderCommitteeVendorDecision> votes =
                        committeeVendorDecisionRepository.findByTenderIdAndVendorIdAndPhase(tenderId, vendorId, phase);
                for (TenderCommitteeVendorDecision vote : votes) {
                    vote.setVendorId(registeredVendor.getVendorId());
                }
                if (!votes.isEmpty()) {
                    committeeVendorDecisionRepository.saveAll(votes);
                }
            }

            log.info("Updated vendorId from {} to {} for tender {}", vendorId, registeredVendorId, tenderId);

            // Recalculate vendorPortalRegistered — check all approved vendors
            TenderEvaluation eval = requireEval(tenderId);
            List<VendorQuotationAgainstTender> completedVendors =
                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId).stream()
                            .filter(q -> "Completed".equals(q.getStatus()))
                            .collect(Collectors.toList());
            if (!completedVendors.isEmpty()) {
                boolean allRegistered = completedVendors.stream()
                        .allMatch(q -> checkAndRegisterVendorOnPortal(q.getVendorId()));
                eval.setVendorPortalRegistered(allRegistered);
                eval.setUpdatedDate(LocalDateTime.now());
                tenderEvaluationRepository.save(eval);
            }
        }

        // ─────────────────────────────────────────────────────────────────
        // ABOVE 10L GATED FLOW: PP Document → Member Voting → Chairman → Director
        // ─────────────────────────────────────────────────────────────────

        @Transactional
        @Override
        public TenderEvaluationStatusDto ppSubmitDocument(String tenderId) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            if (!"PENDING_PP_DOCUMENT".equals(eval.getEvaluationStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Tender is not in PENDING_PP_DOCUMENT status."));
            }

            if (eval.getUploadQualifiedVendorsFileName() == null
                    || eval.getUploadQualifiedVendorsFileName().trim().isEmpty()) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Comparison statement must be uploaded before submitting."));
            }

            eval.setEvaluationStatus("PENDING_CHAIRMAN_REVIEW");
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);

            return buildStatusDto(eval, tender, tenderId);
        }

        @Transactional
        @Override
        public TenderEvaluationStatusDto confirmCommitteeVotes(String tenderId, Integer committeeUserId) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            String status = eval.getEvaluationStatus();
            if (!"PENDING_MEMBER_VOTING".equals(status) && !"PENDING_MEMBER_REVOTE".equals(status)) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Tender is not in PENDING_MEMBER_VOTING or PENDING_MEMBER_REVOTE status."));
            }

            String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";
            List<TenderCommitteeVendorDecision> myVotes = committeeVendorDecisionRepository
                    .findByTenderIdAndCommitteeUserIdAndPhase(tenderId, committeeUserId, phase);

            if (myVotes.isEmpty()) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "No vendor decisions found. Please vote on all vendors before confirming."));
            }

            // Check all vendors have a decision
            List<VendorQuotationAgainstTender> quotations = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
            long expectedVendorCount;
            if ("FINANCIAL".equals(phase)) {
                expectedVendorCount = quotations.stream()
                        .filter(q -> !("REJECTED".equalsIgnoreCase(q.getIndentorStatus())
                                    || "REJECTED".equalsIgnoreCase(q.getSpoStatus())))
                        .count();
            } else {
                expectedVendorCount = quotations.size();
            }
            long decidedCount = myVotes.stream().filter(v -> v.getDecision() != null).count();
            if (decidedCount < expectedVendorCount) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Please vote on all vendors before confirming. Voted: " + decidedCount + "/" + expectedVendorCount));
            }

            // Lock votes
            myVotes.forEach(v -> {
                v.setConfirmed(true);
                v.setConfirmedDate(LocalDateTime.now());
            });
            committeeVendorDecisionRepository.saveAll(myVotes);

            // Check if ALL members confirmed → auto-transition to PENDING_CHAIRMAN_VOTING
            List<TenderCommitteeVendorDecision> allMemberVotes = committeeVendorDecisionRepository
                    .findByTenderIdAndPhaseAndVoterRole(tenderId, phase, "MEMBER");
            boolean allConfirmed = !allMemberVotes.isEmpty() && allMemberVotes.stream()
                    .allMatch(v -> Boolean.TRUE.equals(v.getConfirmed()));

            // Also check that all committee members have voted (not just those with rows)
            List<TenderCommitteeDecision> committeeMembers = committeeDecisionRepository.findByTenderId(tenderId);
            long memberCount = committeeMembers.stream()
                    .filter(m -> m.getCommitteeMemberName() != null).count();
            Set<Integer> votedMemberIds = allMemberVotes.stream()
                    .map(TenderCommitteeVendorDecision::getCommitteeUserId)
                    .collect(Collectors.toSet());

            if (allConfirmed && votedMemberIds.size() >= memberCount) {
                eval.setEvaluationStatus("PENDING_CHAIRMAN_VOTING");
                eval.setUpdatedDate(LocalDateTime.now());
                tenderEvaluationRepository.save(eval);
            }

            return buildStatusDto(eval, tender, tenderId);
        }

        @Transactional
        @Override
        public TenderEvaluationStatusDto chairmanVendorVote(String tenderId, String vendorId,
                                                            String decision, String remarks,
                                                            Integer chairmanUserId) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            String evalSt = eval.getEvaluationStatus();
            boolean isChairmanClarif = ("PENDING_VENDOR_CLARIFICATION".equals(evalSt)
                    || "PENDING_INDENTOR_CLARIFICATION".equals(evalSt))
                    && "CHAIRMAN".equals(eval.getClarificationRequestedByRole());
            if (!"PENDING_CHAIRMAN_VOTING".equals(evalSt)
                    && !(isChairmanClarif && "REJECTED".equalsIgnoreCase(decision))) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Tender is not in PENDING_CHAIRMAN_VOTING status."));
            }

            Integer expectedChairman = resolveChairmanUserId(eval);
            if (expectedChairman == null || !expectedChairman.equals(chairmanUserId)) {
                throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "Only the designated Chairman can vote."));
            }

            String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";
            String normalizedDecision = decision.toUpperCase();

            TenderCommitteeVendorDecision voteRow = committeeVendorDecisionRepository
                    .findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(tenderId, vendorId, chairmanUserId, phase)
                    .orElseGet(() -> {
                        TenderCommitteeVendorDecision r = new TenderCommitteeVendorDecision();
                        r.setTenderId(tenderId);
                        r.setVendorId(vendorId);
                        r.setCommitteeUserId(chairmanUserId);
                        r.setPhase(phase);
                        r.setVoterRole("CHAIRMAN");
                        r.setCreatedDate(LocalDateTime.now());
                        return r;
                    });

            UserMaster user = userMasterRepository.findByUserId(chairmanUserId);
            voteRow.setMemberName(user != null ? user.getUserName() : String.valueOf(chairmanUserId));
            voteRow.setDecision(normalizedDecision);
            voteRow.setRemarks(remarks);
            voteRow.setVoterRole("CHAIRMAN");
            voteRow.setDecisionDate(LocalDateTime.now());
            voteRow.setUpdatedDate(LocalDateTime.now());
            committeeVendorDecisionRepository.save(voteRow);

            VendorQuotationAgainstTender cqt = quotationRepository
                    .findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId).orElse(null);
            if (cqt != null && "CHANGE_REQUESTED".equalsIgnoreCase(cqt.getStatus())
                    && "REJECTED".equalsIgnoreCase(normalizedDecision)) {
                cqt.setStatus("SUBMITTED");
                quotationRepository.save(cqt);
                closePendingClarificationsForVendor(tenderId, vendorId,
                        "CHAIRMAN", chairmanUserId, remarks, eval);
            }

            // No auto-transition — chairman must click "Confirm Evaluation" explicitly

            return buildStatusDto(eval, tender, tenderId);
        }

        @Transactional
        @Override
        public TenderEvaluationStatusDto directorVendorVote(String tenderId, String vendorId,
                                                            String decision, String remarks,
                                                            Integer directorUserId) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            String evalSt = eval.getEvaluationStatus();
            boolean isDirectorClarif = ("PENDING_VENDOR_CLARIFICATION".equals(evalSt)
                    || "PENDING_INDENTOR_CLARIFICATION".equals(evalSt))
                    && "DIRECTOR".equals(eval.getClarificationRequestedByRole());
            if (!"PENDING_DIRECTOR_APPROVAL".equals(evalSt)
                    && !(isDirectorClarif && "REJECTED".equalsIgnoreCase(decision))) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Tender is not in PENDING_DIRECTOR_APPROVAL status."));
            }

            String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";
            String normalizedDecision = decision.toUpperCase();

            TenderCommitteeVendorDecision voteRow = committeeVendorDecisionRepository
                    .findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(tenderId, vendorId, directorUserId, phase)
                    .orElseGet(() -> {
                        TenderCommitteeVendorDecision r = new TenderCommitteeVendorDecision();
                        r.setTenderId(tenderId);
                        r.setVendorId(vendorId);
                        r.setCommitteeUserId(directorUserId);
                        r.setPhase(phase);
                        r.setVoterRole("DIRECTOR");
                        r.setCreatedDate(LocalDateTime.now());
                        return r;
                    });

            UserMaster user = userMasterRepository.findByUserId(directorUserId);
            voteRow.setMemberName(user != null ? user.getUserName() : String.valueOf(directorUserId));
            voteRow.setDecision(normalizedDecision);
            voteRow.setRemarks(remarks);
            voteRow.setVoterRole("DIRECTOR");
            voteRow.setDecisionDate(LocalDateTime.now());
            voteRow.setUpdatedDate(LocalDateTime.now());
            committeeVendorDecisionRepository.save(voteRow);

            VendorQuotationAgainstTender dqt = quotationRepository
                    .findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId).orElse(null);
            if (dqt != null && "CHANGE_REQUESTED".equalsIgnoreCase(dqt.getStatus())
                    && "REJECTED".equalsIgnoreCase(normalizedDecision)) {
                dqt.setStatus("SUBMITTED");
                quotationRepository.save(dqt);
                closePendingClarificationsForVendor(tenderId, vendorId,
                        "DIRECTOR", directorUserId, remarks, eval);
            }

            // No auto-transition — director must click "Confirm Evaluation" explicitly

            return buildStatusDto(eval, tender, tenderId);
        }

        @Transactional
        @Override
        public TenderEvaluationStatusDto resolveMemberClarification(String tenderId, MemberClarificationResolutionDto dto) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            // Legacy support: if global status is PENDING_CHAIRMAN_CLARIFICATION_REVIEW (Director flow), allow it.
            // New flow: member clarifications don't change global status — just need open history rows.

            Integer expectedChairman = resolveChairmanUserId(eval);
            if (expectedChairman == null || !expectedChairman.equals(dto.getChairmanUserId())) {
                throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "Only the designated Chairman can resolve member clarification requests."));
            }

            // Resolve specific history records if IDs provided, otherwise all open member→chairman rows
            List<TenderClarificationHistory> targetRows;
            if (dto.getHistoryIds() != null && !dto.getHistoryIds().isEmpty()) {
                targetRows = dto.getHistoryIds().stream()
                        .map(id -> clarificationHistoryRepository.findById(id).orElse(null))
                        .filter(h -> h != null && tenderId.equals(h.getTenderId()) && h.getRespondedAt() == null
                                && "CHAIRMAN".equals(h.getClarificationTarget()))
                        .collect(Collectors.toList());
                if (targetRows.isEmpty()) {
                    throw new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                            "No open clarification history found for the provided IDs."));
                }
            } else {
                targetRows = clarificationHistoryRepository
                        .findByTenderIdAndClarificationTargetAndRespondedAtIsNull(tenderId, "CHAIRMAN");
            }

            String action = dto.getAction();

            if ("FORWARD_TO_VENDOR".equalsIgnoreCase(action)) {
                // Close selected history rows
                targetRows.forEach(h -> {
                    h.setResponseText("Forwarded to vendor: " + dto.getRemarks());
                    h.setRespondedAt(LocalDateTime.now());
                    h.setRespondedByRole("CHAIRMAN");
                    h.setRespondedById(String.valueOf(dto.getChairmanUserId()));
                });
                clarificationHistoryRepository.saveAll(targetRows);

                // Collect distinct vendor IDs from the selected rows
                Set<String> vendorIds = targetRows.stream()
                        .map(TenderClarificationHistory::getTargetVendorId)
                        .filter(v -> v != null && !v.isBlank())
                        .collect(Collectors.toSet());

                // Forward to each vendor using seekClarification
                TenderEvaluationStatusDto result = null;
                if (vendorIds.isEmpty()) {
                    SeekClarificationDto seekDto = new SeekClarificationDto();
                    seekDto.setTenderId(tenderId);
                    seekDto.setRequestedByRole("CHAIRMAN");
                    seekDto.setRequestedByUserId(dto.getChairmanUserId());
                    seekDto.setClarificationTarget("ALL_VENDORS");
                    seekDto.setRemarks(dto.getRemarks());
                    result = seekClarification(tenderId, seekDto);
                } else {
                    for (String vid : vendorIds) {
                        SeekClarificationDto seekDto = new SeekClarificationDto();
                        seekDto.setTenderId(tenderId);
                        seekDto.setRequestedByRole("CHAIRMAN");
                        seekDto.setRequestedByUserId(dto.getChairmanUserId());
                        seekDto.setClarificationTarget("VENDOR");
                        seekDto.setTargetVendorId(vid);
                        seekDto.setRemarks(dto.getRemarks());
                        result = seekClarification(tenderId, seekDto);
                    }
                }
                return result;

            } else if ("DISMISS".equalsIgnoreCase(action) || "REJECT".equalsIgnoreCase(action)) {
                // Close selected history rows
                targetRows.forEach(h -> {
                    h.setResponseText("Dismissed: " + (dto.getRemarks() != null ? dto.getRemarks() : ""));
                    h.setRespondedAt(LocalDateTime.now());
                    h.setRespondedByRole("CHAIRMAN");
                    h.setRespondedById(String.valueOf(dto.getChairmanUserId()));
                });
                clarificationHistoryRepository.saveAll(targetRows);

                // If global status is PENDING_CHAIRMAN_CLARIFICATION_REVIEW (legacy/Director flow),
                // check if there are still open rows. If none, restore previous status.
                if ("PENDING_CHAIRMAN_CLARIFICATION_REVIEW".equals(eval.getEvaluationStatus())) {
                    long remaining = clarificationHistoryRepository
                            .findByTenderIdAndClarificationTargetAndRespondedAtIsNull(tenderId, "CHAIRMAN")
                            .size();
                    if (remaining == 0) {
                        String prev = eval.getPreviousEvaluationStatus();
                        eval.setEvaluationStatus(prev != null ? prev : "PENDING_APPROVAL");
                        eval.setClarificationPendingFrom(null);
                        eval.setClarificationPendingFromId(null);
                        eval.setClarificationPendingFromName(null);
                        eval.setClarificationRequestedByRole(null);
                        eval.setClarificationRemarks(null);
                        eval.setClarificationTargetVendorId(null);
                        eval.setPreviousEvaluationStatus(null);
                        eval.setUpdatedDate(LocalDateTime.now());
                        tenderEvaluationRepository.save(eval);
                    }
                }

                return buildStatusDto(eval, tender, tenderId);

            } else {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Invalid action. Must be FORWARD_TO_VENDOR or DISMISS."));
            }
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

        // ADD after line 2858, before the class-closing }:

        /**
         * Maps the respondedByRole to the clarificationTarget string stored in
         * TenderClarificationHistory rows, so the restore gate closes exactly the
         * right rows without touching unrelated concurrent clarification rounds.
         *
         * SPO → INDENTOR (all vendors) creates N rows with target="INDENTOR".
         * INDENTOR responding must close ALL N rows, not just the first.
         */
        private String resolveExpectedClarificationTarget(String respondedByRole) {
            if (respondedByRole == null) return "INDENTOR";
            return switch (respondedByRole.toUpperCase()) {
                case "INDENTOR"           -> "INDENTOR";
                case "PURCHASE_PERSONNEL" -> "PURCHASE_PERSONNEL";
                case "CHAIRMAN"           -> "CHAIRMAN";
                case "COMMITTEE_MEMBER"   -> "CHAIRMAN";   // member responds to CHAIRMAN-targeted row
                case "DIRECTOR"           -> "INDENTOR";
                default                   -> "INDENTOR";
            };
        }

        // ─────────────────────────────────────────────────────────────────
        // ROLE MANAGEMENT HELPERS (auto-assign / auto-remove Committee Member role)
        // ─────────────────────────────────────────────────────────────────

        private boolean ensureCommitteeMemberRole(Integer userId) {
            return ensureRole(userId, COMMITTEE_MEMBER_ROLE);
        }

        private boolean ensureCommitteeChairmanRole(Integer userId) {
            return ensureRole(userId, COMMITTEE_CHAIRMAN_ROLE);
        }

        private boolean ensureRole(Integer userId, String roleName) {
            RoleMaster role = roleMasterRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(500, 1,
                            "CONFIGURATION_ERROR",
                            "Role '" + roleName + "' not found in ROLE_MASTER.")));

            UserRoleMaster existing = userRoleMasterRepository.findByRoleIdAndUserId(role.getRoleId(), userId);
            if (existing != null) {
                if (Boolean.TRUE.equals(existing.getIsActive())) {
                    return false;
                }
                existing.setIsActive(true);
                userRoleMasterRepository.save(existing);
                log.info("Reactivated {} role for userId {}", roleName, userId);
                return true;
            }

            UserRoleMaster newRole = new UserRoleMaster();
            newRole.setUserId(userId);
            newRole.setRoleId(role.getRoleId());
            newRole.setReadPermission(true);
            newRole.setWritePermission(true);
            newRole.setIsActive(true);
            newRole.setCreatedDate(new java.util.Date());
            userRoleMasterRepository.save(newRole);
            log.info("Assigned {} role to userId {}", roleName, userId);
            return true;
        }

        private void removeCommitteeMemberRoleIfSafe(Integer userId, String excludeTenderId) {
            // Skip permanent STEC members
            if (committeeRepository.findByUserIdAndIsActiveTrue(userId).isPresent()) {
                log.debug("User {} is permanent STEC member. Keeping role.", userId);
                return;
            }

            // Skip if user has other active tender committee assignments
            int activeCount = committeeDecisionRepository
                    .countActiveAssignmentsExcludingTender(userId, excludeTenderId);
            if (activeCount > 0) {
                log.debug("User {} has {} other committee assignments. Keeping role.", userId, activeCount);
                return;
            }

            deactivateRole(userId, COMMITTEE_MEMBER_ROLE, excludeTenderId);
        }

        private void removeCommitteeChairmanRoleIfSafe(Integer userId, String excludeTenderId) {
            // Skip permanent STEC chairman
            if (committeeRepository.findByUserIdAndIsActiveTrue(userId).isPresent()) {
                log.debug("User {} is permanent STEC member. Keeping chairman role.", userId);
                return;
            }

            // Check if user is ad-hoc chairman on any other active tender
            List<TenderEvaluation> otherChairmanships = tenderEvaluationRepository.findAll().stream()
                    .filter(e -> userId.equals(e.getAdHocChairmanUserId())
                            && !e.getTenderId().equals(excludeTenderId)
                            && !"APPROVED".equals(e.getEvaluationStatus())
                            && !"REJECTED".equals(e.getEvaluationStatus()))
                    .toList();
            if (!otherChairmanships.isEmpty()) {
                log.debug("User {} is ad-hoc chairman on {} other tenders. Keeping chairman role.",
                        userId, otherChairmanships.size());
                return;
            }

            deactivateRole(userId, COMMITTEE_CHAIRMAN_ROLE, excludeTenderId);
        }

        private void deactivateRole(Integer userId, String roleName, String excludeTenderId) {
            RoleMaster role = roleMasterRepository.findByRoleName(roleName).orElse(null);
            if (role == null) return;

            UserRoleMaster urm = userRoleMasterRepository.findByRoleIdAndUserId(role.getRoleId(), userId);
            if (urm != null && Boolean.TRUE.equals(urm.getIsActive())) {
                urm.setIsActive(false);
                userRoleMasterRepository.save(urm);
                log.info("Deactivated {} role for userId {} (removed from tender {})", roleName, userId, excludeTenderId);
            }
        }

        // ─────────────────────────────────────────────────────────────────
        // DIRECTOR AD-HOC COMMITTEE MANAGEMENT (Above 1 Crore)
        // ─────────────────────────────────────────────────────────────────

        @Transactional(readOnly = true)
        @Override
        public List<Map<String, Object>> getAdHocCommitteeMembers(String tenderId) {
            TenderEvaluation eval = requireEval(tenderId);
            if (!"ABOVE_1_CRORE".equals(eval.getAmountCategory())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Ad-hoc committee is only for ABOVE_1_CRORE tenders."));
            }

            List<TenderCommitteeDecision> decisions = committeeDecisionRepository.findByTenderId(tenderId);
            List<Map<String, Object>> result = new ArrayList<>();

            // Add chairman
            if (eval.getAdHocChairmanUserId() != null) {
                Map<String, Object> chairMap = new LinkedHashMap<>();
                chairMap.put("userId", eval.getAdHocChairmanUserId());
                chairMap.put("memberName", eval.getAdHocChairmanName());
                chairMap.put("role", "CHAIRMAN");
                chairMap.put("isExpert", false);
                result.add(chairMap);
            }

            // Add co-chairman
            if (eval.getAdHocCoChairmanUserId() != null) {
                Map<String, Object> coChairMap = new LinkedHashMap<>();
                coChairMap.put("userId", eval.getAdHocCoChairmanUserId());
                coChairMap.put("memberName", eval.getAdHocCoChairmanName());
                coChairMap.put("role", "CO_CHAIRMAN");
                coChairMap.put("isExpert", false);
                result.add(coChairMap);
            }

            // Add members
            Set<Integer> leaderIds = new HashSet<>();
            if (eval.getAdHocChairmanUserId() != null) leaderIds.add(eval.getAdHocChairmanUserId());
            if (eval.getAdHocCoChairmanUserId() != null) leaderIds.add(eval.getAdHocCoChairmanUserId());

            for (TenderCommitteeDecision d : decisions) {
                if (d.getCommitteeUserId() == null || leaderIds.contains(d.getCommitteeUserId())) continue;
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("userId", d.getCommitteeUserId());
                m.put("memberName", d.getCommitteeMemberName());
                m.put("role", "MEMBER");
                m.put("isExpert", eval.getExpertUserId() != null
                        && eval.getExpertUserId().equals(d.getCommitteeUserId()));
                m.put("vote", d.getVote());
                result.add(m);
            }

            return result;
        }

        @Transactional
        @Override
        public Map<String, Object> directorAddCommitteeMember(String tenderId, Integer userId,
                                                                Integer directorUserId, String role) {
            TenderEvaluation eval = requireEval(tenderId);

            if (!"ABOVE_1_CRORE".equals(eval.getAmountCategory())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Ad-hoc committee management is only for ABOVE_1_CRORE tenders."));
            }
            if (!"PENDING_DIRECTOR_REVIEW".equals(eval.getEvaluationStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Members can only be added in PENDING_DIRECTOR_REVIEW status. Current: "
                        + eval.getEvaluationStatus()));
            }

            // Block duplicate
            if (committeeDecisionRepository.findByTenderIdAndCommitteeUserId(tenderId, userId).isPresent()) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "User is already a committee member for this tender."));
            }

            UserMaster user = userMasterRepository.findByUserId(userId);
            if (user == null) {
                throw new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "User not found: " + userId));
            }

            String assignedRole = "MEMBER";
            if ("CHAIRMAN".equalsIgnoreCase(role)) {
                if (eval.getAdHocChairmanUserId() != null) {
                    throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                            "A Chairman is already assigned. Remove the existing Chairman first."));
                }
                eval.setAdHocChairmanUserId(userId);
                eval.setAdHocChairmanName(user.getUserName());
                tenderEvaluationRepository.save(eval);
                assignedRole = "CHAIRMAN";
            }

            // Create committee decision row
            TenderCommitteeDecision row = new TenderCommitteeDecision();
            row.setTenderId(tenderId);
            row.setCommitteeUserId(userId);
            row.setCommitteeMemberName(user.getUserName());
            row.setCreatedDate(LocalDateTime.now());
            row.setUpdatedDate(LocalDateTime.now());
            committeeDecisionRepository.save(row);

            // Auto-assign role based on committee position
            boolean roleAssigned;
            if ("CHAIRMAN".equals(assignedRole)) {
                roleAssigned = ensureCommitteeChairmanRole(userId);
            } else {
                roleAssigned = ensureCommitteeMemberRole(userId);
            }

            log.info("Director {} added user {} as {} to ad-hoc committee for tender {}. Role assigned: {}",
                    directorUserId, userId, assignedRole, tenderId, roleAssigned);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("userId", userId);
            result.put("userName", user.getUserName());
            result.put("tenderId", tenderId);
            result.put("role", assignedRole);
            result.put("roleAssigned", roleAssigned);
            return result;
        }

        @Transactional
        @Override
        public TenderEvaluationStatusDto directorConfirmCommittee(String tenderId, Integer directorUserId) {
            TenderEvaluation eval = requireEval(tenderId);
            TenderRequest tender = requireTender(tenderId);

            if (!"ABOVE_1_CRORE".equals(eval.getAmountCategory())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Ad-hoc committee confirmation is only for ABOVE_1_CRORE tenders."));
            }
            if (!"PENDING_DIRECTOR_REVIEW".equals(eval.getEvaluationStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Committee can only be confirmed in PENDING_DIRECTOR_REVIEW status. Current: "
                        + eval.getEvaluationStatus()));
            }
            if (eval.getAdHocChairmanUserId() == null) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "A Chairman must be assigned before confirming the committee."));
            }

            List<TenderCommitteeDecision> decisions = committeeDecisionRepository.findByTenderId(tenderId);
            if (decisions.isEmpty()) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "At least one committee member must be added before confirming."));
            }

            eval.setEvaluationStatus("PENDING_CHAIRMAN_REVIEW");
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);

            log.info("Director {} confirmed ad-hoc committee for tender {}. Transitioning to PENDING_CHAIRMAN_REVIEW.",
                    directorUserId, tenderId);

            return buildStatusDto(eval, tender, tenderId);
        }

        @Transactional
        @Override
        public Map<String, Object> directorRemoveCommitteeMember(String tenderId, Integer userId,
                                                                Integer directorUserId) {
            TenderEvaluation eval = requireEval(tenderId);

            if (!"ABOVE_1_CRORE".equals(eval.getAmountCategory())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Ad-hoc committee management is only for ABOVE_1_CRORE tenders."));
            }
            if (!"PENDING_DIRECTOR_REVIEW".equals(eval.getEvaluationStatus())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Members can only be removed in PENDING_DIRECTOR_REVIEW status. Current: "
                        + eval.getEvaluationStatus()));
            }

            // Cannot remove co-chairman via this endpoint
            if (userId.equals(eval.getAdHocCoChairmanUserId())) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "Cannot remove Co-Chairman. Use form-committee to change leadership."));
            }

            TenderCommitteeDecision decision = committeeDecisionRepository
                    .findByTenderIdAndCommitteeUserId(tenderId, userId)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                            "User " + userId + " is not a committee member for this tender.")));

            committeeDecisionRepository.delete(decision);

            boolean wasChairman = userId.equals(eval.getAdHocChairmanUserId());

            // Auto-remove role if no other assignments
            if (wasChairman) {
                removeCommitteeChairmanRoleIfSafe(userId, tenderId);
            } else {
                removeCommitteeMemberRoleIfSafe(userId, tenderId);
            }

            // Clear chairman if removing the chairman
            if (wasChairman) {
                eval.setAdHocChairmanUserId(null);
                eval.setAdHocChairmanName(null);
                eval.setUpdatedDate(LocalDateTime.now());
                tenderEvaluationRepository.save(eval);
            }

            // Clear expert if removing the expert
            if (userId.equals(eval.getExpertUserId())) {
                eval.setExpertUserId(null);
                eval.setExpertName(null);
                eval.setUpdatedDate(LocalDateTime.now());
                tenderEvaluationRepository.save(eval);
            }

            log.info("Director {} removed user {} from ad-hoc committee for tender {}",
                    directorUserId, userId, tenderId);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("userId", userId);
            result.put("tenderId", tenderId);
            result.put("removed", true);
            return result;
        }
    }
package com.astro.util;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.ProcurementDtos.CancelTenderRequestDto;
import com.astro.dto.workflow.ProcurementDtos.TenderWithIndentResponseDTO;
import com.astro.dto.workflow.TransitionActionReqDto;
import com.astro.entity.ProcurementModule.PurchaseOrder; // added by abhinav
import com.astro.entity.ProcurementModule.ServiceOrder; // added by abhinav
import com.astro.entity.ProcurementModule.TenderRequest;
import com.astro.entity.VendorQuotationAgainstTender;
import com.astro.entity.WorkflowTransition;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
import com.astro.repository.ProcurementModule.IndentIdRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.repository.VendorQuotationAgainstTenderRepository;
import com.astro.repository.WorkflowTransitionRepository;
import com.astro.service.IndentCreationService;
import com.astro.service.TenderRequestService;
import com.astro.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository;  // added by abhinav
import com.astro.repository.ProcurementModule.ServiceOrderRepository.ServiceOrderRepository;  // added by abhinav

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UtilProcurementService {
    @Autowired
    private TenderRequestRepository TRrepo;
    @Autowired
    private IndentCreationService indentCreationService;
    @Autowired
    private IndentIdRepository indentIdRepository;
    @Autowired
    private IndentCreationRepository indentCreationRepository;
    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private VendorQuotationAgainstTenderRepository vendorQuotationAgainstTenderRepository;
    @Autowired
    private TenderRequestService tenderRequestService;
    @Autowired
    private TenderEmailService tenderEmailService;
    // added by abhinav
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository; // added
    @Autowired
    private ServiceOrderRepository serviceOrderRepository;  //  added

    public String cancelTender(CancelTenderRequestDto request) {
        TenderRequest tenderRequest = TRrepo.findById(request.getTenderId())
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Tender not found for the provided ID."
                        )
                ));

        // TC_50: Check if there is an active Purchase Order for this tender
        // String poRequestId = request.getTenderId().replace("T", "PO");
        String baseTenderId = request.getTenderId().contains("/")
        ? request.getTenderId().substring(0, request.getTenderId().indexOf('/'))
        : request.getTenderId();
String poRequestId = baseTenderId.replace("T", "PO");
        WorkflowTransition poWorkflow = workflowTransitionRepository
                .findTopByRequestIdOrderByWorkflowSequenceDesc(poRequestId);

        if (poWorkflow != null && !"Canceled".equalsIgnoreCase(poWorkflow.getStatus()) &&
            !"Rejected".equalsIgnoreCase(poWorkflow.getStatus())) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Cannot cancel tender. An active Purchase Order (PO) exists for this tender. Please cancel the PO first before cancelling the tender. PO ID: " + poRequestId)
            );
        }

        //  Fetch all indent IDs related to this tender
        List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderRequest.getTenderId());

        // Check if any indent is NOT cancelled in workflow
        boolean hasActiveIndents = indentIds.stream().anyMatch(indentId -> {
            WorkflowTransition lastTransition = workflowTransitionRepository
                    .findTopByRequestIdOrderByTransitionOrderDescWorkflowTransitionIdDesc(indentId);
            return lastTransition != null && !"Canceled".equalsIgnoreCase(lastTransition.getStatus());
        });


        //  All indents are cancelled → proceed to cancel tender
        tenderRequest.setCancelStatus(request.getCancelStatus());
        tenderRequest.setCancelRemarks(request.getCancelRemarks());
        TRrepo.save(tenderRequest);

        // latest workflow transition tender id
        WorkflowTransition latestTransition = workflowTransitionRepository
                .findTopByRequestIdOrderByTransitionOrderDescWorkflowTransitionIdDesc(request.getTenderId());

        TransitionActionReqDto transitionDto = new TransitionActionReqDto();
        transitionDto.setWorkflowTransitionId(latestTransition.getWorkflowTransitionId());
        transitionDto.setRequestId(latestTransition.getRequestId());
        transitionDto.setActionBy(request.getActionBy());
        transitionDto.setAction("REJECTED");
        transitionDto.setRemarks(request.getCancelRemarks());
        transitionDto.setAssignmentRole(latestTransition.getCurrentRole());

        // 7️⃣ Perform workflow action
        workflowService.performTransitionAction(transitionDto);

        // Fetch lastest existing vendor quotations for this tender
        // Fetch all latest vendor quotations for this tender
        List<VendorQuotationAgainstTender> latestQuotations =
                vendorQuotationAgainstTenderRepository.findAllLatestByTenderId(tenderRequest.getTenderId());

        for (VendorQuotationAgainstTender v : latestQuotations) {
            // Mark old as not latest
            v.setIsLatest(false);
            vendorQuotationAgainstTenderRepository.save(v);

            // Create a new CANCELLED quotation
            VendorQuotationAgainstTender cancelledQuotation = new VendorQuotationAgainstTender();
            cancelledQuotation.setTenderId(v.getTenderId());
            cancelledQuotation.setVendorId(v.getVendorId());
            cancelledQuotation.setQuotationFileName(v.getQuotationFileName());
            cancelledQuotation.setPriceBidFileName(v.getPriceBidFileName());
            cancelledQuotation.setFileType(v.getFileType());
            cancelledQuotation.setClarificationFileName(v.getClarificationFileName());
            cancelledQuotation.setVendorResponse(v.getVendorResponse());
            cancelledQuotation.setCreatedBy(v.getCreatedBy());
            cancelledQuotation.setVersion(v.getVersion() + 1);
            cancelledQuotation.setIsLatest(true);
            cancelledQuotation.setStatus("CANCELLED");
            cancelledQuotation.setModifiedBy(request.getActionBy());
            cancelledQuotation.setCurrentRole(v.getCurrentRole());
            cancelledQuotation.setNextRole(v.getNextRole());
            cancelledQuotation.setCreatedDate(LocalDateTime.now());
            cancelledQuotation.setUpdatedDate(LocalDateTime.now());

            vendorQuotationAgainstTenderRepository.save(cancelledQuotation);
        }

        // TC_51: Send email notification to vendors about tender cancellation
        try {
            TenderWithIndentResponseDTO tenderData = tenderRequestService.getTenderRequestById(request.getTenderId());
            tenderEmailService.handleTenderCancellationEmail(
                    request.getTenderId(),
                    tenderData,
                    request.getCancelRemarks()
            );
            System.out.println("Tender cancellation email notifications sent to vendors.");
        } catch (Exception e) {
            System.err.println("Failed to send tender cancellation notifications: " + e.getMessage());
            // Don't fail the cancellation if email fails
        }

        return "Tender cancelled successfully.";
    }
}

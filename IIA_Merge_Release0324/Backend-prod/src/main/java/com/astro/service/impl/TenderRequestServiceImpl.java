package com.astro.service.impl;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.astro.constant.AppConstant;
import com.astro.constant.WorkflowName; // added by abhinav
import com.astro.dto.workflow.ApprovedIndentsDto;
import com.astro.dto.workflow.ApprovedTenderDto;
import com.astro.dto.workflow.ProcurementDtos.*;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.CancelIndentRequestDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCreationResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto;
import com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto.ServiceOrderMaterialRequestDTO;
import com.astro.dto.workflow.TransitionActionReqDto;
import com.astro.entity.ProcurementModule.*;
import com.astro.entity.ProjectMaster;
import com.astro.entity.VendorQuotationAgainstTender;
import com.astro.entity.WorkflowTransition;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.*;
import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
import com.astro.repository.ProcurementModule.IndentCreation.MaterialDetailsRepository;
import com.astro.repository.ProcurementModule.IndentIdRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.service.IndentCreationService;
import com.astro.service.TenderRequestService;
import com.astro.service.VendorQuotationAgainstTenderService;
import com.astro.service.WorkflowService;
import com.astro.util.CommonUtils;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.TheFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Lazy; //added by abhinav

@Service
public class TenderRequestServiceImpl implements TenderRequestService {

    @Autowired
    private TenderRequestRepository TRrepo;
    @Autowired
    private IndentCreationService indentCreationService;
    @Autowired
    private IndentIdRepository indentIdRepository;
    @Autowired
    private IndentCreationRepository indentCreationRepository;
    @Autowired
    private ProjectMasterRepository projectMasterRepository;
    @Autowired
    private MaterialDetailsRepository materialDetailsRepository;
    @Autowired
    private VendorNamesForJobWorkMaterialRepository vendorNameRepository;
    @Autowired
    private VendorQuotationAgainstTenderRepository vendorQuotationAgainstTenderRepository;
    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;
    @Autowired
    private UserMasterRepository userRepository;
    @Autowired
    private VendorQuotationAgainstTenderService vqService;
    // added  by abhinav
    @Autowired
    @Lazy
    private WorkflowService workflowService;

    @Value("${filePath}")
    private String bp;
    private final String basePath;

    public TenderRequestServiceImpl(@Value("${filePath}") String bp) {
        this.basePath = bp + "/Tender";
    }

    @Override
    public TenderResponseDto createTenderRequest(TenderRequestDto tenderRequestDto) {

        // Check if the indentorId already exists
        /*   if (TRrepo.existsById(tenderRequestDto.getTenderId())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Tender Request ID", "Tender ID " + tenderRequestDto.getTenderId() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }
         */
        // added by abhinav line start here
        //  Validate that at least one indent ID is provided
        if (tenderRequestDto.getIndentId() == null || tenderRequestDto.getIndentId().isEmpty()) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "At least one approved indent is required to create a tender."
                    )
            );
        }

        for (String indentIdStr : tenderRequestDto.getIndentId()) {

            IndentCreation indent = indentCreationRepository.findById(indentIdStr)
                    .orElseThrow(() -> new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "Indent not found: " + indentIdStr
                    )
            ));

            //  Check if indent is approved
            // if (indent.getCurrentStatus() == null
            //         || !"APPROVED".equalsIgnoreCase(indent.getCurrentStatus())) {
            //     throw new BusinessException(
            //             new ErrorDetails(
            //                     AppConstant.ERROR_CODE_RESOURCE,
            //                     AppConstant.ERROR_TYPE_CODE_RESOURCE,
            //                     AppConstant.ERROR_TYPE_RESOURCE,
            //                     "Tender can only be created for APPROVED indents. Current status: "
            //                     + indent.getCurrentStatus()
            //             )
            //     );
            // }
            // Check approval from workflow table instead of indent table
            WorkflowTransition latestTransition
                    = workflowTransitionRepository
                            .findTopByRequestIdOrderByWorkflowSequenceDesc(indentIdStr);

            if (latestTransition == null
                    || !"Completed".equalsIgnoreCase(latestTransition.getStatus())) {

                throw new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Tender can only be created for fully approved indents. Current workflow status: "
                                + (latestTransition != null
                                        ? latestTransition.getStatus()
                                        : "NOT_FOUND")
                        )
                );
            }

            //  Check if indent is cancelled
            if (Boolean.TRUE.equals(indent.getCancelStatus())) {

                throw new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Cannot create tender for cancelled indent: " + indentIdStr
                        )
                );
            }

            // Check if indent already used in another tender
            if (Boolean.TRUE.equals(indent.getIsLockedForTender())) {

                throw new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Indent already used in another tender: " + indentIdStr
                        )
                );
            }
        }

        // added by abhinav line end here
        Integer maxNumber = TRrepo.findMaxTenderNumber();
        int nextNumber = (maxNumber == null) ? 1001 : maxNumber + 1;

        String tenderId = "T" + nextNumber;

        TenderRequest tenderRequest = new TenderRequest();

        //  String tenderId = "T" + System.currentTimeMillis();
        // tenderRequest.setTenderId(tenderRequestDto.getTenderId());
        tenderRequest.setTenderId(tenderId);
        System.out.println("tenderId:" + tenderId);
        tenderRequest.setTenderNumber(nextNumber);
        tenderRequest.setTitleOfTender(tenderRequestDto.getTitleOfTender());
        String openingDate = tenderRequestDto.getOpeningDate();
        if (openingDate != null && !openingDate.trim().isEmpty()) {
            tenderRequest.setOpeningDate(CommonUtils.convertStringToDateObject(openingDate));
        } else {
            tenderRequest.setOpeningDate(null);
        }

        String closingDate = tenderRequestDto.getClosingDate();
        if (closingDate != null && !closingDate.trim().isEmpty()) {
            tenderRequest.setClosingDate(CommonUtils.convertStringToDateObject(closingDate));
        } else {
            tenderRequest.setClosingDate(null);
        }

        //  tenderRequest.setIndentId(tenderRequestDto.getIndentId());

        tenderRequest.setIndentMaterials(tenderRequestDto.getIndentMaterials());
        String anyIndentId = tenderRequestDto.getIndentId().get(0);
        List<MaterialDetails> mdList = materialDetailsRepository.findByIndentCreation_IndentId(anyIndentId);
        if (!mdList.isEmpty()) {
            MaterialDetails m = mdList.get(0);
            tenderRequest.setModeOfProcurement(m.getModeOfProcurement());
        } else {
            tenderRequest.setModeOfProcurement(null);
        }

        tenderRequest.setBidType(tenderRequestDto.getBidType());
        String LastDateOfSubmission = tenderRequestDto.getLastDateOfSubmission();
        tenderRequest.setLastDateOfSubmission(CommonUtils.convertStringToDateObject(LastDateOfSubmission));
        tenderRequest.setApplicableTaxes(tenderRequestDto.getApplicableTaxes());
        //  tenderRequest.setConsignesAndBillinngAddress(tenderRequestDto.getConsignesAndBillinngAddress());
        tenderRequest.setConsignes(tenderRequestDto.getConsignes());
        tenderRequest.setBillinngAddress(tenderRequestDto.getBillingAddress());
        tenderRequest.setIncoTerms(tenderRequestDto.getIncoTerms());
        tenderRequest.setPaymentTerms(tenderRequestDto.getPaymentTerms());
        tenderRequest.setLdClause(tenderRequestDto.getLdClause());
        //  tenderRequest.setApplicablePerformance(tenderRequestDto.getApplicablePerformance());
        tenderRequest.setPerformanceAndWarrantySecurity(tenderRequestDto.getPerformanceAndWarrantySecurity());
        tenderRequest.setTenderVersion(1);
tenderRequest.setIsActive(true);        // ADD
tenderRequest.setParentTenderId(null);  // ADD
        tenderRequest.setBidSecurityDeclaration(tenderRequestDto.getBidSecurityDeclaration());
        tenderRequest.setMllStatusDeclaration(tenderRequestDto.getMllStatusDeclaration());
        tenderRequest.setSingleAndMultipleVendors(tenderRequestDto.getSingleAndMultipleVendors());
        tenderRequest.setPreBidDisscussions(tenderRequestDto.getPreBidDisscussions());
        tenderRequest.setUpdatedBy(tenderRequestDto.getUpdatedBy());
        tenderRequest.setCreatedBy(tenderRequestDto.getCreatedBy());

        // TC_44: Initialize version to 1
        tenderRequest.setTenderVersion(1);

        // TC_47: Pre-bid Meeting fields
        tenderRequest.setPreBidMeetingStatus(tenderRequestDto.getPreBidMeetingStatus() != null ?
            tenderRequestDto.getPreBidMeetingStatus() : "NOT_CONDUCTED");
        tenderRequest.setPreBidMeetingDiscussion(tenderRequestDto.getPreBidMeetingDiscussion());
        if (tenderRequestDto.getPreBidMeetingDate() != null && !tenderRequestDto.getPreBidMeetingDate().isEmpty()) {
            tenderRequest.setPreBidMeetingDate(CommonUtils.convertStringToDateObject(tenderRequestDto.getPreBidMeetingDate()));
        }

        if(tenderRequestDto.getBuyBack()){
            tenderRequest.setBuyBack(tenderRequestDto.getBuyBack());
            tenderRequest.setModelNumber(tenderRequestDto.getModelNumber());
            tenderRequest.setSerialNumber(tenderRequestDto.getSerialNumber());
            tenderRequest.setDateOfPurchase(CommonUtils.convertStringToDateObject(tenderRequestDto.getDateOfPurchase()));
            tenderRequest.setBuyBackAmount(tenderRequestDto.getBuyBackAmount());
            if (tenderRequestDto.getUploadBuyBackFileNames() == null || tenderRequestDto.getUploadBuyBackFileNames().isEmpty()) {
                tenderRequest.setUploadBuyBackFileNames(null);

            } else {
                String uploadBuyBack = saveBase64Files(tenderRequestDto.getUploadBuyBackFileNames(), basePath);
                tenderRequest.setUploadBuyBackFileNames(uploadBuyBack);
            }
        }
        //tenderRequest.setUploadTenderDocumentsFileName(tenderRequestDto.getUploadTenderDocuments());
        // tenderRequest.setUploadSpecificTermsAndConditionsFileName(tenderRequestDto.getUploadGeneralTermsAndConditions());
        //  tenderRequest.setUploadGeneralTermsAndConditionsFileName(tenderRequestDto.getUploadGeneralTermsAndConditions());
        tenderRequest.setFileType(tenderRequestDto.getFileType());

        if (tenderRequestDto.getUploadSpecificTermsAndConditions() == null || tenderRequestDto.getUploadSpecificTermsAndConditions().isEmpty()) {
            tenderRequest.setUploadSpecificTermsAndConditionsFileName(null);

        } else {
            String uploadSpecificTermsAndConditionsFileName = saveBase64Files(tenderRequestDto.getUploadSpecificTermsAndConditions(), basePath);
            tenderRequest.setUploadSpecificTermsAndConditionsFileName(uploadSpecificTermsAndConditionsFileName);
        }
        if (tenderRequestDto.getUploadTenderDocuments() == null || tenderRequestDto.getUploadTenderDocuments().isEmpty()) {
            tenderRequest.setUploadTenderDocumentsFileName(null);
        } else {
            String tenderDoc = saveBase64Files(tenderRequestDto.getUploadTenderDocuments(), basePath);
            tenderRequest.setUploadTenderDocumentsFileName(tenderDoc);
        }
        if (tenderRequestDto.getUploadGeneralTermsAndConditions() == null || tenderRequestDto.getUploadGeneralTermsAndConditions().isEmpty()) {
            tenderRequest.setUploadGeneralTermsAndConditionsFileName(null);
        } else {
            String generalDoc = saveBase64Files(tenderRequestDto.getUploadGeneralTermsAndConditions(), basePath);
            tenderRequest.setUploadGeneralTermsAndConditionsFileName(generalDoc);
        }
        if (tenderRequestDto.getBidSecurityDeclarationFileName() == null || tenderRequestDto.getBidSecurityDeclarationFileName().isEmpty()) {
            tenderRequest.setBidSecurityDeclarationFileName(null);
        } else {
            String bidDoc = saveBase64Files(tenderRequestDto.getBidSecurityDeclarationFileName(), basePath);
            tenderRequest.setBidSecurityDeclarationFileName(bidDoc);
        }
        if (tenderRequestDto.getMllStatusDeclarationFileName() == null || tenderRequestDto.getMllStatusDeclarationFileName().isEmpty()) {
            tenderRequest.setMllStatusDeclarationFileName(null);
        } else {
            String mllDoc = saveBase64Files(tenderRequestDto.getMllStatusDeclarationFileName(), basePath);
            tenderRequest.setMllStatusDeclarationFileName(mllDoc);
        }
        // Convert List<String> indentIds from DTO into List<IndentId> entities
        List<IndentId> indentIdList = tenderRequestDto.getIndentId().stream().map(indentIdStr -> {
            IndentId indentId = new IndentId();
            indentId.setIndentId(indentIdStr); // Directly assign the string value
            indentId.setTenderRequest(tenderRequest);
            return indentId;
        }).collect(Collectors.toList());

// Set indentIds in TenderRequest
        tenderRequest.setIndentIds(indentIdList);
        List<String> projectNames = indentCreationRepository.findDistinctProjectNames(tenderRequestDto.getIndentId());
        //at least one project name exists, assign the first one. If no project name exists, set it to null
        if (!projectNames.isEmpty()) {
            tenderRequest.setProjectName(projectNames.get(0));
        } else {
            tenderRequest.setProjectName(null); // No project name found, set as null
        }
        // Fetch Indent Data
        List<IndentCreationResponseDTO> indentDataList = tenderRequest.getIndentIds().stream()
                .map(indentId -> indentCreationService.getIndentById(indentId.getIndentId()))
                .collect(Collectors.toList());

        // Calculate totalTenderValue
        BigDecimal totalTenderValue = indentDataList.stream()
                .map(IndentCreationResponseDTO::getTotalPriceOfAllMaterials)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        tenderRequest.setTotalTenderValue(totalTenderValue);


        TRrepo.save(tenderRequest);

        // INITIATE TENDER WORKFLOW AFTER CREATION
        workflowService.initiateWorkflow( // added this line by abhinav
                tenderRequest.getTenderId(),
                WorkflowName.TENDER_APPROVER.getValue(),
                tenderRequestDto.getCreatedBy()
        );

        // Bug Fix 2: Lock all indents associated with this tender
        for (String indentIdStr : tenderRequestDto.getIndentId()) {
            indentCreationRepository.findById(indentIdStr).ifPresent(indent -> {
                indent.setIsLockedForTender(true);
                indent.setLockedReason("Tender " + tenderId + " has been created for this indent");
                indent.setCurrentStatus("TENDER_CREATED");
                indent.setCurrentStage("TENDER_GENERATION");
                indentCreationRepository.save(indent);
            });
        }

        return mapToResponseDTO(tenderRequest);
    }

    public String saveBase64Files(List<String> base64Files, String basePath) {
        try {
            List<String> fileNames = new ArrayList<>();
            for (String base64File : base64Files) {
                String fileName = CommonUtils.saveBase64Image(base64File, basePath);
                fileNames.add(fileName);
            }
            return String.join(",", fileNames);
        } catch (Exception e) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.FILE_UPLOAD_ERROR,
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CORRUPTED,
                    "Error while uploading files."));
        }
    }

private String extractBaseTenderId(String tenderId) {
    if (tenderId == null) return null;
    int slashIdx = tenderId.indexOf('/');
    return slashIdx >= 0 ? tenderId.substring(0, slashIdx) : tenderId;
}
// @Override
// public List<TenderResponseDto> getTenderVersionHistory(String tenderId) {
// return ;
// }
@Override
public TenderResponseDto updateTenderRequest(String tenderId, TenderRequestDto tenderRequestDto) {

    // 1. Load existing active tender
    TenderRequest old = TRrepo.findById(tenderId)
            .orElseThrow(() -> new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE, AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION, "Tender request not found for the provided ID.")));

    // 2. Guard: locked after PO creation
    if (Boolean.TRUE.equals(old.getIsLocked())) {
        throw new BusinessException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE, AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Tender is locked. Cannot update after Purchase Order has been created. " + old.getLockedReason()));
    }

    // 3. Guard: only original creator can edit
    if (!old.getCreatedBy().equals(tenderRequestDto.getCreatedBy())) {
        throw new BusinessException(new ErrorDetails(
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Only the original Tender Creator can edit this tender."));
    }

    // 4. Deactivate old version
    old.setIsActive(false);
    TRrepo.save(old);

    // 5. Supersede old version's pending workflow transitions
    List<WorkflowTransition> pendingTransitions =
            workflowTransitionRepository.findPendingTransitionsByRequestId(old.getTenderId());
    for (WorkflowTransition wt : pendingTransitions) {
        wt.setStatus("SUPERSEDED");
        wt.setNextAction(null);
        wt.setRemarks("Superseded by new version: " + extractBaseTenderId(old.getTenderId())
                + "/" + (old.getTenderVersion() + 1));
        workflowTransitionRepository.save(wt);
    }

    // 6. Compute new tender ID e.g. T1001 -> T1001/2, T1001/2 -> T1001/3
    String baseId = extractBaseTenderId(old.getTenderId());
    int newVersion = old.getTenderVersion() + 1;
    String newTenderId = baseId + "/" + newVersion;

    // 7. Build new TenderRequest (copy-new pattern)
    TenderRequest newTR = new TenderRequest();
    newTR.setTenderId(newTenderId);
    newTR.setTenderNumber(old.getTenderNumber());   // same number, new suffix
    newTR.setTenderVersion(newVersion);
    newTR.setIsActive(true);
    newTR.setParentTenderId(old.getTenderId());
    newTR.setCreatedBy(old.getCreatedBy());          // original creator always
    newTR.setUpdatedBy(tenderRequestDto.getUpdatedBy());
    newTR.setCreatedDate(old.getCreatedDate());
    newTR.setUpdatedDate(LocalDateTime.now());

    // Reset lock state fresh
    newTR.setIsLocked(false);
    newTR.setLockedReason(null);
    newTR.setLockedForPO(null);
    newTR.setLockedDate(null);
    newTR.setCancelStatus(false);

    // 8. Copy all fields from request
    newTR.setTitleOfTender(tenderRequestDto.getTitleOfTender());
    newTR.setUpdateReason(tenderRequestDto.getUpdateReason());

    String openingDate = tenderRequestDto.getOpeningDate();
    newTR.setOpeningDate(openingDate != null && !openingDate.trim().isEmpty()
            ? CommonUtils.convertStringToDateObject(openingDate) : null);

    String closingDate = tenderRequestDto.getClosingDate();
    newTR.setClosingDate(closingDate != null && !closingDate.trim().isEmpty()
            ? CommonUtils.convertStringToDateObject(closingDate) : null);

    newTR.setIndentMaterials(tenderRequestDto.getIndentMaterials());
    newTR.setModeOfProcurement(tenderRequestDto.getModeOfProcurement());
    newTR.setBidType(tenderRequestDto.getBidType());

    String lastDate = tenderRequestDto.getLastDateOfSubmission();
    newTR.setLastDateOfSubmission(lastDate != null ? CommonUtils.convertStringToDateObject(lastDate) : null);

    newTR.setApplicableTaxes(tenderRequestDto.getApplicableTaxes());
    newTR.setConsignes(tenderRequestDto.getConsignes());
    newTR.setBillinngAddress(tenderRequestDto.getBillingAddress());
    newTR.setIncoTerms(tenderRequestDto.getIncoTerms());
    newTR.setPaymentTerms(tenderRequestDto.getPaymentTerms());
    newTR.setLdClause(tenderRequestDto.getLdClause());
    newTR.setPerformanceAndWarrantySecurity(tenderRequestDto.getPerformanceAndWarrantySecurity());
    newTR.setBidSecurityDeclaration(tenderRequestDto.getBidSecurityDeclaration());
    newTR.setMllStatusDeclaration(tenderRequestDto.getMllStatusDeclaration());
    newTR.setSingleAndMultipleVendors(tenderRequestDto.getSingleAndMultipleVendors());
    newTR.setPreBidDisscussions(tenderRequestDto.getPreBidDisscussions());
    newTR.setVendorId(tenderRequestDto.getVendorId());
    newTR.setFileType(tenderRequestDto.getFileType());

    // Pre-bid meeting fields
    newTR.setPreBidMeetingStatus(tenderRequestDto.getPreBidMeetingStatus());
    newTR.setPreBidMeetingDiscussion(tenderRequestDto.getPreBidMeetingDiscussion());
    if (tenderRequestDto.getPreBidMeetingDate() != null && !tenderRequestDto.getPreBidMeetingDate().isEmpty()) {
        newTR.setPreBidMeetingDate(CommonUtils.convertStringToDateObject(tenderRequestDto.getPreBidMeetingDate()));
    }

    // Buy back fields
    newTR.setBuyBack(tenderRequestDto.getBuyBack());
    newTR.setBuyBackAmount(tenderRequestDto.getBuyBackAmount());
    newTR.setModelNumber(tenderRequestDto.getModelNumber());
    newTR.setSerialNumber(tenderRequestDto.getSerialNumber());
    if (tenderRequestDto.getDateOfPurchase() != null && !tenderRequestDto.getDateOfPurchase().isEmpty()) {
        newTR.setDateOfPurchase(CommonUtils.convertStringToDateObject(tenderRequestDto.getDateOfPurchase()));
    }

    // File handling
    if (tenderRequestDto.getUploadTenderDocuments() == null || tenderRequestDto.getUploadTenderDocuments().isEmpty()) {
        newTR.setUploadTenderDocumentsFileName(null);
    } else {
        newTR.setUploadTenderDocumentsFileName(saveBase64Files(tenderRequestDto.getUploadTenderDocuments(), basePath));
    }
    if (tenderRequestDto.getUploadGeneralTermsAndConditions() == null || tenderRequestDto.getUploadGeneralTermsAndConditions().isEmpty()) {
        newTR.setUploadGeneralTermsAndConditionsFileName(null);
    } else {
        newTR.setUploadGeneralTermsAndConditionsFileName(saveBase64Files(tenderRequestDto.getUploadGeneralTermsAndConditions(), basePath));
    }
    if (tenderRequestDto.getUploadSpecificTermsAndConditions() == null || tenderRequestDto.getUploadSpecificTermsAndConditions().isEmpty()) {
        newTR.setUploadSpecificTermsAndConditionsFileName(null);
    } else {
        newTR.setUploadSpecificTermsAndConditionsFileName(saveBase64Files(tenderRequestDto.getUploadSpecificTermsAndConditions(), basePath));
    }
    if (tenderRequestDto.getBidSecurityDeclarationFileName() == null || tenderRequestDto.getBidSecurityDeclarationFileName().isEmpty()) {
        newTR.setBidSecurityDeclarationFileName(null);
    } else {
        newTR.setBidSecurityDeclarationFileName(saveBase64Files(tenderRequestDto.getBidSecurityDeclarationFileName(), basePath));
    }
    if (tenderRequestDto.getMllStatusDeclarationFileName() == null || tenderRequestDto.getMllStatusDeclarationFileName().isEmpty()) {
        newTR.setMllStatusDeclarationFileName(null);
    } else {
        newTR.setMllStatusDeclarationFileName(saveBase64Files(tenderRequestDto.getMllStatusDeclarationFileName(), basePath));
    }
    if (tenderRequestDto.getUploadBuyBackFileNames() == null || tenderRequestDto.getUploadBuyBackFileNames().isEmpty()) {
        newTR.setUploadBuyBackFileNames(null);
    } else {
        newTR.setUploadBuyBackFileNames(saveBase64Files(tenderRequestDto.getUploadBuyBackFileNames(), basePath));
    }

    // 9. Indent IDs — link to new tender
    List<IndentId> indentIdList = tenderRequestDto.getIndentId().stream().map(indentIdStr -> {
        IndentId indentId = new IndentId();
        indentId.setIndentId(indentIdStr);
        indentId.setTenderRequest(newTR);
        return indentId;
    }).collect(Collectors.toList());
    newTR.setIndentIds(indentIdList);

    // 10. Recalculate project name and total tender value
    List<String> projectNames = indentCreationRepository.findDistinctProjectNames(tenderRequestDto.getIndentId());
    newTR.setProjectName(projectNames.isEmpty() ? null : projectNames.get(0));

    List<IndentCreationResponseDTO> indentDataList = tenderRequestDto.getIndentId().stream()
            .map(indentCreationService::getIndentById)
            .collect(Collectors.toList());
    BigDecimal totalTenderValue = indentDataList.stream()
            .map(IndentCreationResponseDTO::getTotalPriceOfAllMaterials)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    newTR.setTotalTenderValue(totalTenderValue);

    // 11. Save new version
    TRrepo.save(newTR);

    return mapToResponseDTO(newTR);
}
    // @Override
    // public TenderResponseDto updateTenderRequest(String tenderId, TenderRequestDto tenderRequestDto) {
    //     //   ,String uploadTenderDocumentsFileName,String uploadGeneralTermsAndConditionsFileName  , String uploadSpecificTermsAndConditionsFileName) {
    //     TenderRequest existingTR = TRrepo.findById(tenderId)
    //             .orElseThrow(() -> new BusinessException(
    //                     new ErrorDetails(
    //                             AppConstant.ERROR_CODE_RESOURCE,
    //                             AppConstant.ERROR_TYPE_CODE_RESOURCE,
    //                             AppConstant.ERROR_TYPE_VALIDATION,
    //                             "Tender request not found for the provided asset ID.")
    //             ));

    //     // TC_48: Check if tender is locked (PO created)
    //     if (Boolean.TRUE.equals(existingTR.getIsLocked())) {
    //         throw new BusinessException(
    //                 new ErrorDetails(
    //                         AppConstant.ERROR_CODE_RESOURCE,
    //                         AppConstant.ERROR_TYPE_CODE_RESOURCE,
    //                         AppConstant.ERROR_TYPE_VALIDATION,
    //                         "Tender is locked. Cannot update tender after Purchase Order has been created. " + existingTR.getLockedReason())
    //         );
    //     }

    //     // TC_44: Increment version
    //     existingTR.setTenderVersion(existingTR.getTenderVersion() != null ? existingTR.getTenderVersion() + 1 : 2);

    //     // TC_46: Set update reason
    //     existingTR.setUpdateReason(tenderRequestDto.getUpdateReason());
    //     existingTR.setUpdatedDate(LocalDateTime.now());

    //     existingTR.setTitleOfTender(tenderRequestDto.getTitleOfTender());
    //     String openingDate = tenderRequestDto.getOpeningDate();
    //     existingTR.setOpeningDate(CommonUtils.convertStringToDateObject(openingDate));
    //     String closeingDate = tenderRequestDto.getClosingDate();
    //     existingTR.setClosingDate(CommonUtils.convertStringToDateObject(closeingDate));
    //     //  existingTR.setIndentId(tenderRequestDto.getIndentId());
    //     existingTR.setIndentMaterials(tenderRequestDto.getIndentMaterials());
    //     existingTR.setModeOfProcurement(tenderRequestDto.getModeOfProcurement());
    //     existingTR.setBidType(tenderRequestDto.getBidType());
    //     String LastDateOfSubmission = tenderRequestDto.getLastDateOfSubmission();
    //     existingTR.setLastDateOfSubmission(CommonUtils.convertStringToDateObject(LastDateOfSubmission));
    //     existingTR.setApplicableTaxes(tenderRequestDto.getApplicableTaxes());
    //     // existingTR.setConsignesAndBillinngAddress(tenderRequestDto.getConsignesAndBillinngAddress());
    //     existingTR.setBillinngAddress(tenderRequestDto.getBillingAddress());
    //     existingTR.setConsignes(tenderRequestDto.getConsignes());
    //     existingTR.setIncoTerms(tenderRequestDto.getIncoTerms());
    //     existingTR.setPaymentTerms(tenderRequestDto.getPaymentTerms());
    //     existingTR.setLdClause(tenderRequestDto.getLdClause());
    //     existingTR.setVendorId(tenderRequestDto.getVendorId());
    //     existingTR.setQuotationFileName(tenderRequestDto.getQuotationFileName());
    //     // existingTR.setApplicablePerformance(tenderRequestDto.getApplicablePerformance());
    //     existingTR.setPerformanceAndWarrantySecurity(tenderRequestDto.getPerformanceAndWarrantySecurity());
    //     existingTR.setBidSecurityDeclaration(tenderRequestDto.getBidSecurityDeclaration());
    //     existingTR.setMllStatusDeclaration(tenderRequestDto.getMllStatusDeclaration());
    //     existingTR.setSingleAndMultipleVendors(tenderRequestDto.getSingleAndMultipleVendors());
    //     existingTR.setPreBidDisscussions(tenderRequestDto.getPreBidDisscussions());

    //     // TC_47: Update Pre-bid Meeting fields
    //     existingTR.setPreBidMeetingStatus(tenderRequestDto.getPreBidMeetingStatus());
    //     existingTR.setPreBidMeetingDiscussion(tenderRequestDto.getPreBidMeetingDiscussion());
    //     if (tenderRequestDto.getPreBidMeetingDate() != null && !tenderRequestDto.getPreBidMeetingDate().isEmpty()) {
    //         existingTR.setPreBidMeetingDate(CommonUtils.convertStringToDateObject(tenderRequestDto.getPreBidMeetingDate()));
    //     }

    //     existingTR.setUpdatedBy(tenderRequestDto.getUpdatedBy());
    //     existingTR.setCreatedBy(tenderRequestDto.getCreatedBy());
    //     // existingTR.setUploadTenderDocumentsFileName(tenderRequestDto.getUploadTenderDocuments());
    //     //  existingTR.setUploadSpecificTermsAndConditionsFileName(tenderRequestDto.getUploadGeneralTermsAndConditions());
    //     // existingTR.setUploadGeneralTermsAndConditionsFileName(tenderRequestDto.getUploadGeneralTermsAndConditions());
    //     if (tenderRequestDto.getUploadSpecificTermsAndConditions() == null || tenderRequestDto.getUploadSpecificTermsAndConditions().isEmpty()) {
    //         existingTR.setUploadSpecificTermsAndConditionsFileName(null);

    //     } else {
    //         String uploadSpecificTermsAndConditionsFileName = saveBase64Files(tenderRequestDto.getUploadSpecificTermsAndConditions(), basePath);
    //         existingTR.setUploadSpecificTermsAndConditionsFileName(uploadSpecificTermsAndConditionsFileName);
    //     }
    //     if (tenderRequestDto.getUploadTenderDocuments() == null || tenderRequestDto.getUploadTenderDocuments().isEmpty()) {
    //         existingTR.setUploadTenderDocumentsFileName(null);
    //     } else {
    //         String tenderDoc = saveBase64Files(tenderRequestDto.getUploadTenderDocuments(), basePath);
    //         existingTR.setUploadTenderDocumentsFileName(tenderDoc);
    //     }
    //     if (tenderRequestDto.getUploadGeneralTermsAndConditions() == null || tenderRequestDto.getUploadGeneralTermsAndConditions().isEmpty()) {
    //         existingTR.setUploadGeneralTermsAndConditionsFileName(null);
    //     } else {
    //         String generalDoc = saveBase64Files(tenderRequestDto.getUploadGeneralTermsAndConditions(), basePath);
    //         existingTR.setUploadGeneralTermsAndConditionsFileName(generalDoc);
    //     }

    //     if (tenderRequestDto.getBidSecurityDeclarationFileName() == null || tenderRequestDto.getBidSecurityDeclarationFileName().isEmpty()) {
    //         existingTR.setBidSecurityDeclarationFileName(null);
    //     } else {
    //         String bidDoc = saveBase64Files(tenderRequestDto.getBidSecurityDeclarationFileName(), basePath);
    //         existingTR.setBidSecurityDeclarationFileName(bidDoc);
    //     }
    //     if (tenderRequestDto.getMllStatusDeclarationFileName() == null || tenderRequestDto.getMllStatusDeclarationFileName().isEmpty()) {
    //         existingTR.setMllStatusDeclarationFileName(null);
    //     } else {
    //         String mllDoc = saveBase64Files(tenderRequestDto.getMllStatusDeclarationFileName(), basePath);
    //         existingTR.setMllStatusDeclarationFileName(mllDoc);
    //     }
    //     existingTR.setFileType(tenderRequestDto.getFileType());

    //     // Update Indent IDs
    //     List<String> newIndentIds = tenderRequestDto.getIndentId();

    //     // Remove old indent IDs that are no longer in the updated list
    //     existingTR.getIndentIds().removeIf(indentId -> !newIndentIds.contains(indentId.getIndentId()));

    //     // Add only new indent IDs that are not already in the existing list
    //     List<String> existingIndentIdStrings = existingTR.getIndentIds().stream()
    //             .map(IndentId::getIndentId)
    //             .collect(Collectors.toList());

    //     List<IndentId> indentIdList = newIndentIds.stream()
    //             .filter(id -> !existingIndentIdStrings.contains(id)) // Avoid duplicates
    //             .map(id -> {
    //                 IndentId indentId = new IndentId();
    //                 indentId.setIndentId(id);
    //                 indentId.setTenderRequest(existingTR);
    //                 return indentId;
    //             }).collect(Collectors.toList());

    //     existingTR.getIndentIds().addAll(indentIdList);
    //     TenderRequest savedTR = TRrepo.save(existingTR);

    //     // TC_45: Send email notification to vendors about tender amendment
    //     // Only send if tender was already approved (has quotations submitted)
    //     if (tenderRequestDto.getUpdateReason() != null && !tenderRequestDto.getUpdateReason().isEmpty()) {
    //         try {
    //             TenderWithIndentResponseDTO tenderData = getTenderRequestById(tenderId);
    //             // Note: Email service will be called asynchronously
    //             // The TenderEmailService.handleTenderAmendmentEmail() method should be invoked
    //             // This can be done via event publishing or direct call (will be async)
    //             System.out.println("Tender amendment notification should be sent to vendors for tender: " + tenderId);
    //         } catch (Exception e) {
    //             System.err.println("Failed to send tender amendment notification: " + e.getMessage());
    //         }
    //     }

    //     return mapToResponseDTO(savedTR);
    // }

    @Override
    public TenderResponseDto updateTender(String tenderId, tenderUpdateDto dto) {

        TenderRequest existing = TRrepo.findById(tenderId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Tender request not found for the provided ID."
                        )
                ));

        existing.setVendorId(dto.getVendorId());
        existing.setQuotationFileName(dto.getQuotationFileName());


        TenderRequest saved = TRrepo.save(existing);


        return mapToResponseDTO(saved);
    }

  /*  @Override
    public VendorQualificationResponseDto vendorCheck(String tenderId, String vendorId) {
        TenderRequest tenderRequest = TRrepo.findById(tenderId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Tender not found for the provided asset ID.")
                ));
        String vendor = tenderRequest.getVendorId();
        VendorQualificationResponseDto resp = new VendorQualificationResponseDto();

        if (vendor != null && !vendor.isEmpty() && vendor.equals(vendorId)) {
            // return vendorId;
            resp.setVendorId(vendor);
            resp.setQualified(true);
            resp.setRemarks("null");
        } else {
            Optional<VendorQuotationAgainstTender> quotationOpt =
                    vendorQuotationAgainstTenderRepository.findByTenderIdAndVendorId(tenderId, vendorId);

            if (quotationOpt.isPresent()) {
                VendorQuotationAgainstTender quotation = quotationOpt.get();
                resp.setVendorId(null);
                resp.setQualified(false);
                resp.setRemarks(quotation.getRemarks()); // Fetch remarks from the entity
            } else {
                resp.setVendorId(vendorId);
                resp.setQualified(false);
                resp.setRemarks(null);
            }
        }
        return resp;
    }*/

  private String resolveRoleName(Integer userId) {
      if (userId == null) return null;
      return userRepository.findById(userId)
              .map(u -> u.getRoleName())
              .orElse("User " + userId);
  }

  @Override
  public VendorQualificationResponseDto vendorCheck(String tenderId, String vendorId) {
      TenderRequest tenderRequest = TRrepo.findById(tenderId)
              .orElseThrow(() -> new BusinessException(
                      new ErrorDetails(
                              AppConstant.ERROR_CODE_RESOURCE,
                              AppConstant.ERROR_TYPE_CODE_RESOURCE,
                              AppConstant.ERROR_TYPE_RESOURCE,
                              "Tender not found for the provided asset ID.")
              ));

      VendorQualificationResponseDto resp = new VendorQualificationResponseDto();
      resp.setVendorId(vendorId);
      resp.setQualified(false);
      resp.setChangeRequest(false);
      resp.setRemarks(null);
      resp.setActionTakenBy(null);
      resp.setActionStatus(null);  //  "ACCEPTED", "REJECTED", "CHANGE_REQUESTED"

      // Direct match with tender's vendor => qualified (initial qualification)
   /*   if (tenderRequest.getVendorId() != null && tenderRequest.getVendorId().equalsIgnoreCase(vendorId)) {
          resp.setQualified(true);
          resp.setActionStatus("VENDOR QULIFIED");
          resp.setActionTakenBy("Store Purchase Officer");
          return resp;
      }*/
      /*
      if (tenderRequest.getVendorId() != null && tenderRequest.getVendorId().equalsIgnoreCase(vendorId)) {
          resp.setQualified(true);
          resp.setActionTakenBy("Store Purchase Officer");

         // Boolean po = workflowTransitionRepository.isPoCompleted(tenderId);
          String poRequestId = tenderId.replace("T", "PO");
         WorkflowTransition wt = workflowTransitionRepository.findTopByRequestIdOrderByWorkflowSequenceDesc(poRequestId);



          if (wt.getStatus().equalsIgnoreCase("Completed")) {
              resp.setActionStatus("PO Completed");
          } else {
              resp.setActionStatus("PO Raised");
          }

          return resp;
      }*/



      // Fetch latest quotation version
      Optional<VendorQuotationAgainstTender> latestOpt =
              vendorQuotationAgainstTenderRepository
                      .findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc(tenderId, vendorId);

      VendorQuotationAgainstTender va = latestOpt.orElse(null);

      if (tenderRequest.getVendorId() != null && va != null) {
          System.out.println("Vendor Status:"+va.getStatus());

          if ("Completed".equalsIgnoreCase(va.getStatus().trim())) {

              if (tenderRequest.getVendorId().equalsIgnoreCase(vendorId)) {
                  resp.setQualified(true);
                  resp.setActionTakenBy("Store Purchase Officer");

                  String poRequestId = tenderId.replace("T", "PO");
                  WorkflowTransition wt = workflowTransitionRepository
                          .findTopByRequestIdOrderByWorkflowSequenceDesc(poRequestId);

                  if (wt != null && "Completed".equalsIgnoreCase(wt.getStatus())) {
                      resp.setActionStatus("PO Completed");
                  } else {
                      resp.setActionStatus("PO Raised");
                  }
                  resp.setPOVendorId(tenderRequest.getVendorId());

              } else {
                  resp.setQualified(false);
                  resp.setActionTakenBy("Store Purchase Officer");

                  String poRequestId = tenderId.replace("T", "PO");
                  WorkflowTransition wt = workflowTransitionRepository
                          .findTopByRequestIdOrderByWorkflowSequenceDesc(poRequestId);

                  if (wt != null && "Completed".equalsIgnoreCase(wt.getStatus())) {
                     // resp.setActionStatus("PO Completed");
                      resp.setActionStatus("PO Raised");
                      resp.setApprovedVendorPoData("PO Completed");
                  } else {
                      resp.setActionStatus("PO Raised");
                  }
                  resp.setPOVendorId(tenderRequest.getVendorId());

              }

              return resp;

          } else if ("REJECTED".equalsIgnoreCase(va.getStatus().trim())) {
              resp.setQualified(false);
              resp.setActionTakenBy("Store Purchase Officer");

              String poRequestId = tenderId.replace("T", "PO");
              WorkflowTransition wt = workflowTransitionRepository
                      .findTopByRequestIdOrderByWorkflowSequenceDesc(poRequestId);

              if (wt != null && "Completed".equalsIgnoreCase(wt.getStatus())) {
                 // resp.setActionStatus("PO Completed");
                  resp.setActionStatus("PO Raised");
                  resp.setApprovedVendorPoData("PO Completed");
              } else {
                  resp.setActionStatus("PO Raised");
              }

              resp.setPOVendorId(tenderRequest.getVendorId());
              resp.setActionStatusAfterPoGenerated("Rejected");
              resp.setRemarks(va.getRemarks());
              return resp;
          }
      }


      if (latestOpt.isPresent()) {
          VendorQuotationAgainstTender latest = latestOpt.get();

          String spoStatus = latest.getSpoStatus() != null ? latest.getSpoStatus().trim().toUpperCase() : null;
          String indentorStatus = latest.getIndentorStatus() != null ? latest.getIndentorStatus().trim().toUpperCase() : null;

          final Integer modifiedBy = latest.getModifiedBy();
          final String roleName;
          if(modifiedBy==1){
              roleName="Vendor";
          }else{
              roleName = resolveRoleName(modifiedBy);
          }

          //  Indentor → Vendor change request first
          if ("CHANGE_REQUESTED".equalsIgnoreCase(indentorStatus)
                  && latest.getCurrentRole() == VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR
                  && latest.getNextRole() == VendorQuotationAgainstTender.WorkflowActorRole.VENDOR) {

             // resp.setActionTakenBy(latest.getModifiedBy());
              resp.setActionTakenBy(roleName);
              resp.setActionStatus("CHANGE_REQUESTED");
              resp.setRemarks(latest.getIndentorRemarks());
              resp.setQualified(true);
              resp.setChangeRequest(true);

          }
          // Then check SPO's decision
          else if (spoStatus != null && !spoStatus.isBlank()) {
              //resp.setActionTakenBy(latest.getModifiedBy());
              resp.setActionTakenBy(roleName);
              resp.setActionStatus(spoStatus);
              resp.setRemarks(latest.getSpoRemarks());

              switch (spoStatus) {
                  case "ACCEPTED":
                      resp.setQualified(true);
                      break;
                  case "REJECTED":
                      resp.setQualified(false);
                      List<String> vendorIds = vqService.getVendorsWithCompletedQuotation(tenderId);
                      resp.setVendorIds(vendorIds);
                      break;
                  case "CHANGE_REQUESTED_TO_INTENTOR":
                      resp.setQualified(false);
                      resp.setChangeRequest(true);
                      break;
                  default:
                      resp.setQualified(false);
              }
          }
          // Fallback to Indentor decision
          else if (indentorStatus != null && !indentorStatus.isBlank()) {
             // resp.setActionTakenBy(latest.getModifiedBy());
              resp.setActionTakenBy(roleName);
              resp.setActionStatus(indentorStatus);
              resp.setRemarks(latest.getIndentorRemarks());

              switch (indentorStatus) {
                  case "ACCEPTED":
                      resp.setQualified(false);
                      break;
                  case "REJECTED":
                      resp.setQualified(false);
                      break;
                  case "CHANGE_REQUESTED":
                      resp.setQualified(true);
                      resp.setChangeRequest(true);
                      break;
                  default:
                      resp.setQualified(false);
              }
          }
          // No explicit statuses, fallback to generic
          else {
              String status = latest.getStatus() != null ? latest.getStatus().trim().toUpperCase() : null;
            //  resp.setActionTakenBy(latest.getModifiedBy());
              resp.setActionTakenBy(roleName);
              resp.setActionStatus(status);
              resp.setRemarks(latest.getRemarks());
              resp.setQualified(false);
          }
      }

      return resp;
  }






    @Override
    public  TenderWithIndentResponseDTO getTenderRequestById(String tenderId) {
        TenderRequest tenderRequest =TRrepo.findById(tenderId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Tender not found for the provided asset ID.")
                ));

        // Fetch Indent Data
        List<IndentCreationResponseDTO> indentDataList = tenderRequest.getIndentIds().stream()
                .map(indentId -> indentCreationService.getIndentById(indentId.getIndentId()))
                .collect(Collectors.toList());

        // Calculate totalTenderValue
        BigDecimal totalTenderValue = indentDataList.stream()
                .map(IndentCreationResponseDTO::getTotalPriceOfAllMaterials)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // Combine both Tender and Indent data into a single response DTO
        TenderWithIndentResponseDTO responseDTO = new TenderWithIndentResponseDTO();
        // Set Tender Details
        responseDTO.setTenderId(tenderRequest.getTenderId());
        responseDTO.setTitleOfTender(tenderRequest.getTitleOfTender());
        LocalDate openingDate = tenderRequest.getOpeningDate();
        responseDTO.setOpeningDate(CommonUtils.convertDateToString(openingDate));
        LocalDate closeingDate = tenderRequest.getClosingDate();
        responseDTO.setClosingDate(CommonUtils.convertDateToString(closeingDate));
        //responseDTO.setIndentId(tenderRequest.getIndentId());
        responseDTO.setIndentMaterials(tenderRequest.getIndentMaterials());
        responseDTO.setModeOfProcurement(tenderRequest.getModeOfProcurement());
        responseDTO.setBidType(tenderRequest.getBidType());
        LocalDate LastDateOfSubmission = tenderRequest.getLastDateOfSubmission();
        responseDTO.setLastDateOfSubmission(CommonUtils.convertDateToString(LastDateOfSubmission));
        responseDTO.setApplicableTaxes(tenderRequest.getApplicableTaxes());
       // responseDTO.setConsignesAndBillinngAddress(tenderRequest.getConsignesAndBillinngAddress());
        responseDTO.setConsignes(tenderRequest.getConsignes());
        responseDTO.setBillinngAddress(tenderRequest.getBillinngAddress());
        responseDTO.setIncoTerms(tenderRequest.getIncoTerms());
        responseDTO.setPaymentTerms(tenderRequest.getPaymentTerms());
        responseDTO.setLdClause(tenderRequest.getLdClause());
        responseDTO.setVendorId(tenderRequest.getVendorId());
        responseDTO.setQuotationFileName(tenderRequest.getQuotationFileName());
        responseDTO.setMllStatusDeclarationFileName(tenderRequest.getMllStatusDeclarationFileName());
        responseDTO.setBidSecurityDeclarationFileName(tenderRequest.getBidSecurityDeclarationFileName());

        //  responseDTO.setApplicablePerformance(tenderRequest.getApplicablePerformance());
        responseDTO.setPerformanceAndWarrantySecurity(tenderRequest.getPerformanceAndWarrantySecurity());
        responseDTO.setBidSecurityDeclaration(tenderRequest.getBidSecurityDeclaration());
        responseDTO.setMllStatusDeclaration(tenderRequest.getMllStatusDeclaration());
        responseDTO.setUploadTenderDocuments(tenderRequest.getUploadTenderDocumentsFileName());
        responseDTO.setSingleAndMultipleVendors(tenderRequest.getSingleAndMultipleVendors());
        responseDTO.setUploadGeneralTermsAndConditions(tenderRequest.getUploadGeneralTermsAndConditionsFileName());
        responseDTO.setUploadSpecificTermsAndConditions(tenderRequest.getUploadSpecificTermsAndConditionsFileName());
        responseDTO.setFileType(tenderRequest.getFileType());
        responseDTO.setPreBidDisscussions(tenderRequest.getPreBidDisscussions());
        responseDTO.setUpdatedBy(tenderRequest.getUpdatedBy());
        responseDTO.setCreatedBy(tenderRequest.getCreatedBy());
        responseDTO.setCreatedDate(tenderRequest.getCreatedDate());
        responseDTO.setUpdatedDate(tenderRequest.getUpdatedDate());
      //  responseDTO.setIndentResponseDTO(indentData);
        responseDTO.setIndentResponseDTO(indentDataList); //Updated to list
        responseDTO.setTotalTenderValue(totalTenderValue); // Calculated total
       // responseDTO.setTotalTenderValue(totalTenderValue);
        responseDTO.setBuyBack(tenderRequest.getBuyBack());
        responseDTO.setModelNumber(tenderRequest.getModelNumber());
        responseDTO.setSerialNumber(tenderRequest.getSerialNumber());
        responseDTO.setDateOfPurchase(CommonUtils.convertDateToString(tenderRequest.getDateOfPurchase()));
        responseDTO.setBuyBackAmount(tenderRequest.getBuyBackAmount());
        responseDTO.setUploadBuyBackFileNames(tenderRequest.getUploadBuyBackFileNames());

        // TC_44, TC_46, TC_47, TC_48: Add new fields to response
        responseDTO.setTenderVersion(tenderRequest.getTenderVersion());
        responseDTO.setUpdateReason(tenderRequest.getUpdateReason());
        responseDTO.setPreBidMeetingStatus(tenderRequest.getPreBidMeetingStatus());
        responseDTO.setPreBidMeetingDiscussion(tenderRequest.getPreBidMeetingDiscussion());
        responseDTO.setPreBidMeetingDate(CommonUtils.convertDateToString(tenderRequest.getPreBidMeetingDate()));
        responseDTO.setIsLocked(tenderRequest.getIsLocked());
        responseDTO.setLockedReason(tenderRequest.getLockedReason());
        responseDTO.setLockedForPO(tenderRequest.getLockedForPO());
        responseDTO.setLockedDate(tenderRequest.getLockedDate());

        return responseDTO;

    }

    @Override
    public TenderResponseDto getTenderData(String tenderId) {
        TenderRequest tender= TRrepo.findByTenderId(tenderId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Tender not found for the provided Tender ID.")
                ));
        return mapToResponseDTO(tender);
    }
    @Override
    public TenderResponseBase64FilesDto getTenderDataWithBase64Files(String tenderId) throws IOException {
        TenderRequest tenderRequest= TRrepo.findByTenderId(tenderId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Tender not found for the provided Tender ID.")
                ));
        TenderResponseBase64FilesDto tenderResponseDto = new TenderResponseBase64FilesDto();

        tenderResponseDto.setTenderId(tenderRequest.getTenderId());
        tenderResponseDto.setTitleOfTender(tenderRequest.getTitleOfTender());
        LocalDate openingDate = tenderRequest.getOpeningDate();
        tenderResponseDto.setOpeningDate(CommonUtils.convertDateToString(openingDate));
        LocalDate closeingDate = tenderRequest.getClosingDate();
        tenderResponseDto.setClosingDate(CommonUtils.convertDateToString(closeingDate));
        tenderResponseDto.setIndentMaterials(tenderRequest.getIndentMaterials());
        tenderResponseDto.setModeOfProcurement(tenderRequest.getModeOfProcurement());
        tenderResponseDto.setBidType(tenderRequest.getBidType());
        LocalDate LastDateOfSubmission = tenderRequest.getLastDateOfSubmission();
        tenderResponseDto.setLastDateOfSubmission(CommonUtils.convertDateToString(LastDateOfSubmission));
        tenderResponseDto.setApplicableTaxes(tenderRequest.getApplicableTaxes());
        tenderResponseDto.setBillinngAddress(tenderRequest.getBillinngAddress());
        tenderResponseDto.setConsignes(tenderRequest.getConsignes());
        tenderResponseDto.setIncoTerms(tenderRequest.getIncoTerms());
        tenderResponseDto.setPaymentTerms(tenderRequest.getPaymentTerms());
        tenderResponseDto.setLdClause(tenderRequest.getLdClause());
        tenderResponseDto.setPerformanceAndWarrantySecurity(tenderRequest.getPerformanceAndWarrantySecurity());
        tenderResponseDto.setBidSecurityDeclaration(tenderRequest.getBidSecurityDeclaration());
        tenderResponseDto.setMllStatusDeclaration(tenderRequest.getMllStatusDeclaration());
       // tenderResponseDto.setUploadTenderDocuments(tenderRequest.getUploadTenderDocumentsFileName());
        tenderResponseDto.setSingleAndMultipleVendors(tenderRequest.getSingleAndMultipleVendors());
       // tenderResponseDto.setUploadGeneralTermsAndConditions(tenderRequest.getUploadGeneralTermsAndConditionsFileName());
      //  tenderResponseDto.setUploadSpecificTermsAndConditions(tenderRequest.getUploadSpecificTermsAndConditionsFileName());
       // tenderResponseDto.setMllStatusDeclarationFileName(tenderRequest.getMllStatusDeclarationFileName());
        //tenderResponseDto.setBidSecurityDeclarationFileName(tenderRequest.getBidSecurityDeclarationFileName());
        tenderResponseDto.setPreBidDisscussions(tenderRequest.getPreBidDisscussions());
        tenderResponseDto.setFileType(tenderRequest.getFileType());
        if (tenderRequest.getMllStatusDeclarationFileName() == null || tenderRequest.getMllStatusDeclarationFileName().isEmpty()) {
            tenderResponseDto.setMllStatusDeclarationFileName(null);
        } else {
            tenderResponseDto.setMllStatusDeclarationFileName(
                    convertFilesToBase64(tenderRequest.getMllStatusDeclarationFileName(), basePath));
        }
        if (tenderRequest.getBidSecurityDeclarationFileName() == null || tenderRequest.getBidSecurityDeclarationFileName().isEmpty()) {
            tenderResponseDto.setBidSecurityDeclarationFileName(null);
        } else {
            tenderResponseDto.setBidSecurityDeclarationFileName(
                    convertFilesToBase64(tenderRequest.getBidSecurityDeclarationFileName(), basePath));
        }
        if (tenderRequest.getUploadTenderDocumentsFileName() == null || tenderRequest.getUploadTenderDocumentsFileName().isEmpty()) {
            tenderResponseDto.setUploadTenderDocuments(null);
        } else {
            tenderResponseDto.setUploadTenderDocuments(
                    convertFilesToBase64(tenderRequest.getUploadTenderDocumentsFileName(), basePath));
        }
        if (tenderRequest.getUploadGeneralTermsAndConditionsFileName() == null || tenderRequest.getUploadGeneralTermsAndConditionsFileName().isEmpty()) {
            tenderResponseDto.setUploadGeneralTermsAndConditions(null);
        } else {
            tenderResponseDto.setUploadGeneralTermsAndConditions(
                    convertFilesToBase64(tenderRequest.getUploadGeneralTermsAndConditionsFileName(), basePath));
        }
        if (tenderRequest.getUploadSpecificTermsAndConditionsFileName() == null || tenderRequest.getUploadSpecificTermsAndConditionsFileName().isEmpty()) {
            tenderResponseDto.setUploadSpecificTermsAndConditions(null);
        } else {
            tenderResponseDto.setUploadSpecificTermsAndConditions(
                    convertFilesToBase64(tenderRequest.getUploadSpecificTermsAndConditionsFileName(), basePath));
        }
        tenderResponseDto.setUpdatedBy(tenderRequest.getUpdatedBy());
        tenderResponseDto.setCreatedBy(tenderRequest.getCreatedBy());
        tenderResponseDto.setCreatedDate(tenderRequest.getCreatedDate());
        tenderResponseDto.setUpdatedDate(tenderRequest.getUpdatedDate());
        tenderResponseDto.setUploadTenderDocumentsFileName(tenderRequest.getUploadTenderDocumentsFileName());
        tenderResponseDto.setUploadSpecificTermsAndConditionsFileName(tenderRequest.getUploadSpecificTermsAndConditionsFileName());
        tenderResponseDto.setUploadGeneralTermsAndConditionsFileName(tenderRequest.getUploadGeneralTermsAndConditionsFileName());
        tenderResponseDto.setMiiStatusDeclarationFileName(tenderRequest.getMllStatusDeclarationFileName());
        tenderResponseDto.setBidSecurityDeclarationFile(tenderRequest.getBidSecurityDeclarationFileName());
        tenderResponseDto.setVendorName(tenderRequest.getVendorId());
        List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderRequest.getTenderId());

        tenderResponseDto.setIndentIds(indentIds);
        WorkflowTransition wt = workflowTransitionRepository.findTopByRequestIdOrderByWorkflowSequenceDesc(tenderId);
        tenderResponseDto.setStatus(wt.getStatus());
        tenderResponseDto.setProcessStage(wt.getNextRole());

        tenderResponseDto.setProjectName(tenderRequest.getProjectName());
        System.out.println(tenderRequest.getProjectName());
        BigDecimal totalTenderValue = indentIds.stream()
                .map(indentCreationService::getIndentById) // Fetch Indent data
                .map(IndentCreationResponseDTO::getTotalPriceOfAllMaterials) // Extract total price
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up values
        tenderResponseDto.setTotalTenderValue(totalTenderValue);
        System.out.println("tottalTenderValue"+ totalTenderValue);
        String projectName = tenderRequest.getProjectName();
        BigDecimal allocatedAmount = projectMasterRepository
                .findByProjectNameDescription(projectName)
                .map(ProjectMaster::getAllocatedAmount)
                .orElse(BigDecimal.ZERO);
        tenderResponseDto.setProjectLimit(allocatedAmount);
        System.out.println("allocatedAmount: " + allocatedAmount);

        return tenderResponseDto;
    }
    public static List<String> convertFilesToBase64(String fileNames, String basePath) throws IOException {
        List<String> base64List = new ArrayList<>();

        if (fileNames != null && !fileNames.isEmpty()) {
            String[] fileNameArray = fileNames.split(",");

            for (String fileName : fileNameArray) {
                String trimmedFileName = fileName.trim();
                if (!trimmedFileName.isEmpty()) {
                    String base64 = CommonUtils.convertImageToBase64(trimmedFileName, basePath);
                    base64List.add(base64);
                }
            }
        }

        return base64List;
    }



    @Override
    public List<TenderResponseDto> getAllTenderRequests() {
        List<TenderRequest> tenderRequests = TRrepo.findAll();
        return tenderRequests.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTenderRequest(String tenderId) {

        TenderRequest tenderRequest=TRrepo.findById(tenderId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "tender not found for the provided ID."
                        )
                ));
        try {
           TRrepo.delete(tenderRequest);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the tender."
                    ),
                    ex
            );
        }
    }

    private TenderResponseDto mapToResponseDTO(TenderRequest tenderRequest) {

        TenderResponseDto tenderResponseDto = new TenderResponseDto();

        tenderResponseDto.setTenderId(tenderRequest.getTenderId());
        tenderResponseDto.setTitleOfTender(tenderRequest.getTitleOfTender());
        LocalDate openingDate = tenderRequest.getOpeningDate();
        tenderResponseDto.setOpeningDate(CommonUtils.convertDateToString(openingDate));
        LocalDate closeingDate = tenderRequest.getClosingDate();
        tenderResponseDto.setClosingDate(CommonUtils.convertDateToString(closeingDate));
      //  tenderResponseDto.setIndentId(tenderRequest.getIndentId());
        tenderResponseDto.setIndentMaterials(tenderRequest.getIndentMaterials());
        tenderResponseDto.setModeOfProcurement(tenderRequest.getModeOfProcurement());
        tenderResponseDto.setBidType(tenderRequest.getBidType());
        LocalDate LastDateOfSubmission = tenderRequest.getLastDateOfSubmission();
        tenderResponseDto.setLastDateOfSubmission(CommonUtils.convertDateToString(LastDateOfSubmission));
        tenderResponseDto.setApplicableTaxes(tenderRequest.getApplicableTaxes());
      //  tenderResponseDto.setConsignesAndBillinngAddress(tenderRequest.getConsignesAndBillinngAddress());
        tenderResponseDto.setBillinngAddress(tenderRequest.getBillinngAddress());
        tenderResponseDto.setConsignes(tenderRequest.getConsignes());
        tenderResponseDto.setIncoTerms(tenderRequest.getIncoTerms());
        tenderResponseDto.setPaymentTerms(tenderRequest.getPaymentTerms());
        tenderResponseDto.setLdClause(tenderRequest.getLdClause());
    //    tenderResponseDto.setApplicablePerformance(tenderRequest.getApplicablePerformance());
        tenderResponseDto.setPerformanceAndWarrantySecurity(tenderRequest.getPerformanceAndWarrantySecurity());
        tenderResponseDto.setBidSecurityDeclaration(tenderRequest.getBidSecurityDeclaration());
        tenderResponseDto.setMllStatusDeclaration(tenderRequest.getMllStatusDeclaration());
        tenderResponseDto.setUploadTenderDocuments(tenderRequest.getUploadTenderDocumentsFileName());
        tenderResponseDto.setSingleAndMultipleVendors(tenderRequest.getSingleAndMultipleVendors());
        tenderResponseDto.setUploadGeneralTermsAndConditions(tenderRequest.getUploadGeneralTermsAndConditionsFileName());
        tenderResponseDto.setUploadSpecificTermsAndConditions(tenderRequest.getUploadSpecificTermsAndConditionsFileName());
        tenderResponseDto.setMllStatusDeclarationFileName(tenderRequest.getMllStatusDeclarationFileName());
        tenderResponseDto.setBidSecurityDeclarationFileName(tenderRequest.getBidSecurityDeclarationFileName());
        tenderResponseDto.setPreBidDisscussions(tenderRequest.getPreBidDisscussions());
        tenderResponseDto.setFileType(tenderRequest.getFileType());
        tenderResponseDto.setUpdatedBy(tenderRequest.getUpdatedBy());
        tenderResponseDto.setCreatedBy(tenderRequest.getCreatedBy());
        tenderResponseDto.setCreatedDate(tenderRequest.getCreatedDate());
        tenderResponseDto.setUpdatedDate(tenderRequest.getUpdatedDate());
        // Convert indentIds to List<String> and set in response
      //  List<String> indentIds = tenderRequest.getIndentIds().stream()
             //  .map(IndentId::getIndentId)
              //  .collect(Collectors.toList());
        // Fetch indent IDs based on tenderId
        List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderRequest.getTenderId());

          tenderResponseDto.setIndentIds(indentIds);
       // List<String> projectNames = indentCreationRepository.findDistinctProjectNames(indentIds);
       // if (!projectNames.isEmpty()) {
        //    tenderResponseDto.setProjectName(projectNames.get(0)); // Assign only the first project name
       // }
        tenderResponseDto.setProjectName(tenderRequest.getProjectName());
        System.out.println(tenderRequest.getProjectName());

        // Calculate total tender value by summing totalPriceOfAllMaterials of all indents
        BigDecimal totalTenderValue = indentIds.stream()
                .map(indentCreationService::getIndentById) // Fetch Indent data
                .map(IndentCreationResponseDTO::getTotalPriceOfAllMaterials) // Extract total price
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up values
        tenderResponseDto.setTotalTenderValue(totalTenderValue);
        System.out.println("tottalTenderValue"+ totalTenderValue);
        String projectName = tenderRequest.getProjectName();
        BigDecimal allocatedAmount = projectMasterRepository
                .findByProjectNameDescription(projectName)
                .map(ProjectMaster::getAllocatedAmount)
                .orElse(BigDecimal.ZERO);
        tenderResponseDto.setProjectLimit(allocatedAmount);
        System.out.println("allocatedAmount: " + allocatedAmount);

        // TC_44, TC_46, TC_47, TC_48: Add new fields to response
        tenderResponseDto.setTenderVersion(tenderRequest.getTenderVersion());
        tenderResponseDto.setUpdateReason(tenderRequest.getUpdateReason());
        tenderResponseDto.setPreBidMeetingStatus(tenderRequest.getPreBidMeetingStatus());
        tenderResponseDto.setPreBidMeetingDiscussion(tenderRequest.getPreBidMeetingDiscussion());
        tenderResponseDto.setPreBidMeetingDate(CommonUtils.convertDateToString(tenderRequest.getPreBidMeetingDate()));
        tenderResponseDto.setIsLocked(tenderRequest.getIsLocked());
        tenderResponseDto.setLockedReason(tenderRequest.getLockedReason());
        tenderResponseDto.setLockedForPO(tenderRequest.getLockedForPO());
        tenderResponseDto.setLockedDate(tenderRequest.getLockedDate());
        tenderResponseDto.setIsActive(tenderRequest.getIsActive());
tenderResponseDto.setParentTenderId(tenderRequest.getParentTenderId());

        return tenderResponseDto;

    }
/*
public List<SearchTenderIdDto> searchTenderIds(String type, String value) {
    List<SearchIndentIdDto> indentDtos = new ArrayList<>();
    List<SearchTenderIdDto> result = new ArrayList<>();

    switch (type.toLowerCase()) {

        case "tenderid":
            result = TRrepo.findTenderIdLike(value);
            break;

        case "submitteddate":
            try {
                LocalDate date = LocalDate.parse(value);
                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
                result = TRrepo.findTenderIdsBySubmittedDate(startOfDay, endOfDay);
            } catch (Exception e) {
                throw new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Invalid submitted date format. Expected yyyy-MM-dd"
                ));
            }
            break;
        case "materialdescription":
            indentDtos = indentCreationRepository.findByMaterialDescription(value);
            break;

        case "vendorname":
            indentDtos = vendorNameRepository.findIndentIdsByVendorName(value);
            break;

        case "indentorname":
            indentDtos = indentCreationRepository.findByIndentorName(value);
            break;

        default:
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Invalid search type: " + type
            ));
    }

    List<String> indentIds = indentDtos.stream()
            .map(SearchIndentIdDto::getIndentId)
            .collect(Collectors.toList());

    if (indentIds.isEmpty()) {
        throw new BusinessException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "No indentIds found for given search criteria."
        ));
    }
    System.out.println(indentIds);
    result = indentIdRepository.findTenderIdsByIndentIds(indentIds);
    System.out.println(result);
    if (result == null || result.isEmpty()) {
        throw new BusinessException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "No matching tenders found for the given search criteria."
        ));
    }

    return result;
}*/
public List<SearchTenderIdDto> searchTenderIds(String type, String value) {
    List<SearchTenderIdDto> result = new ArrayList<>();

    switch (type.toLowerCase()) {
        case "processid":
            return TRrepo.findTenderIdLike(value);

        case "submitteddate":
            try {
                LocalDate date = LocalDate.parse(value);
                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
                return TRrepo.findTenderIdsBySubmittedDate(startOfDay, endOfDay);
            } catch (Exception e) {
                throw new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Invalid submitted date format. Expected yyyy-MM-dd"
                ));
            }

        case "materialdescription":
            List<String> indentIds1 = indentCreationRepository.findByMaterialDescription(value)
                    .stream().map(SearchIndentIdDto::getIndentId).collect(Collectors.toList());
            if (indentIds1.isEmpty()) {
                throw new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "No indent IDs found for material description."
                ));
            }

            return indentIdRepository.findTenderIdsByIndentIds(indentIds1);

        case "vendorname":
            List<String> indentIds2 = vendorNameRepository.findIndentIdsByVendorName(value)
                    .stream().map(SearchIndentIdDto::getIndentId).collect(Collectors.toList());
            if (indentIds2.isEmpty()) {
                throw new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "No indent IDs found for material description."
                ));
            }

            return indentIdRepository.findTenderIdsByIndentIds(indentIds2);

        case "indentorname":
            List<String> indentIds3 = indentCreationRepository.findByIndentorName(value)
                    .stream().map(SearchIndentIdDto::getIndentId).collect(Collectors.toList());
            if (indentIds3.isEmpty()) {
                throw new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "No indent IDs found for material description."
                ));
            }

            return indentIdRepository.findTenderIdsByIndentIds(indentIds3);

        default:
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Invalid search type: " + type
            ));
    }
}

  /*  @Override
    public List<ApprovedTenderIdDtos> getApprovedTenderIdsForTenderEvaluation() {
       List<String> tenderIds = workflowTransitionRepository.findApprovedTenderIdsForPOANDSO();
        List<ApprovedTenderIdDtos> dtoList = new ArrayList<>();
       for(String tenderId : tenderIds){
           TenderRequest tenderRequest = TRrepo.findById(tenderId)
                   .orElseThrow(() -> new BusinessException(
                           new ErrorDetails(
                                   AppConstant.ERROR_CODE_RESOURCE,
                                   AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                   AppConstant.ERROR_TYPE_RESOURCE,
                                   "Tender not found for the provided asset ID.")
                   ));
           ApprovedTenderIdDtos dto = new ApprovedTenderIdDtos();
           dto.setTenderId(tenderId);
           dto.setTitle(tenderRequest.getTitleOfTender());

           dtoList.add(dto);

       }
       return dtoList;
    }*/
  @Override
  public List<ApprovedTenderIdDtos> getApprovedTenderIdsForTenderEvaluation() {
      return TRrepo.findApprovedTenderIdsAndTitlesForPOANDSO();
  }
  @Override
  public List<ApprovedTenderIdDtos> getApprovedTenderIdsForGemTenderEvaluation() {
        return TRrepo.findApprovedTenderIdsForGemAndTitlesForPOANDSO();
  }











}

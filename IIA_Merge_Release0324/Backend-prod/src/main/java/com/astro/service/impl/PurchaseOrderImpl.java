package com.astro.service.impl;


import com.astro.constant.AppConstant;

import com.astro.dto.workflow.MaterialTransitionHistory;
import com.astro.dto.workflow.ProcurementDtos.*;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCreationResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.MaterialDetailsResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.SearchIndentIdDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.materialHistoryDto;
import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.*;
import com.astro.dto.workflow.VendorContractReportDTO;
import com.astro.dto.workflow.WorkflowTransitionDto;
import com.astro.dto.workflow.poMaterialHistoryDto;
import com.astro.entity.*;
import com.astro.entity.ProcurementModule.MaterialDetails;
import com.astro.entity.ProcurementModule.PurchaseOrder;
import com.astro.entity.ProcurementModule.PurchaseOrderAttributes;
import com.astro.entity.ProcurementModule.TenderRequest;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
// added by abhinav
import com.astro.repository.*;
import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
import com.astro.repository.ProcurementModule.IndentCreation.MaterialDetailsRepository;
import com.astro.repository.ProcurementModule.IndentIdRepository;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderAttributesRepository;

import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository;

import com.astro.repository.ProcurementModule.ServiceOrderRepository.ServiceOrderRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.repository.InventoryModule.GprnRepository.GprnMaterialDtlRepository;
import com.astro.service.IndentCreationService;
import com.astro.service.PurchaseOrderService;
import com.astro.service.TenderRequestService;
import com.astro.service.WorkflowService;
import com.astro.util.CommonUtils;
import com.azure.core.http.rest.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.models.auth.In;

import com.astro.entity.ProcurementModule.PurchaseOrderHistory; // added by abhinav
import net.bytebuddy.ClassFileVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderAttributesRepository purchaseOrderAttributesRepository;
    @Autowired
    private IndentCreationService indentCreationService;
    @Autowired
    private TenderRequestService tenderRequestService;
    @Autowired
    private IndentIdRepository indentIdRepository;
    @Autowired
    private TenderRequestRepository tenderRequestRepository;
    @Autowired
    private ProjectMasterRepository projectMasterRepository;
    @Autowired
    private ServiceOrderRepository serviceOrderRepository;
    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;
    @Autowired
    private TenderRequestRepository trRepo;
    @Autowired
    private VendorMasterRepository vendorMasterRepository;
    @Autowired
    private IndentCreationRepository indentCreationRepository;
    @Autowired
    private MaterialMasterRepository materialMasterRepository;
    @Autowired
    private GemVendorIdTrackerRepository gemVendorIdTrackerRepository;
    @Autowired
    private IiaAddressForConsigneeLocationRepository iiaAddressForConsigneeLocationRepository;
    @Autowired
    private IiaFreightForwarderDetailsRepository iiaFreightForwarderDetailsRepository;
    @Autowired
    private OfficerSignatureRepository officerSignatureRepository;

    // added new by abhinav
    @Autowired
    private PurchaseOrderHistoryRepository purchaseOrderHistoryRepository;

    @Value("${filePath}")
    private String bp;
    private final String basePath;

    public PurchaseOrderImpl(@Value("${filePath}") String bp) {
        this.basePath = bp + "/Tender";
    }




    public PurchaseOrderResponseDTO createPurchaseOrder(PurchaseOrderRequestDTO purchaseOrderRequestDTO) {

        // Check if the indentorId already exists
     /*   if (purchaseOrderRepository.existsById(purchaseOrderRequestDTO.getPoId())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Purchase Order ID", "PO ID " + purchaseOrderRequestDTO.getPoId() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }

      */


/*
        // Iterate over materialDetails and check if materialCode already exists
        for (PurchaseOrderAttributesDTO materialRequest : purchaseOrderRequestDTO.getPurchaseOrderAttributes()) {
            if (purchaseOrderAttributesRepository.existsById(materialRequest.getMaterialCode())) {
                ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Material Code",
                        "Material Code " + materialRequest.getMaterialCode() + " already exists.");
                throw new InvalidInputException(errorDetails);
            }
        }

 */
        PurchaseOrder purchaseOrder = new PurchaseOrder();

        String tenderId = purchaseOrderRequestDTO.getTenderId();
        String poId = generatePoId(tenderId);

        //  purchaseOrder.setPoId(purchaseOrderRequestDTO.getPoId());
        purchaseOrder.setPoId(poId);
        purchaseOrder.setPoVersion(1);
purchaseOrder.setIsActive(true);
purchaseOrder.setParentPoId(null);
        purchaseOrder.setTenderId(purchaseOrderRequestDTO.getTenderId());
        purchaseOrder.setIndentId(purchaseOrderRequestDTO.getIndentId());
        purchaseOrder.setWarranty(purchaseOrderRequestDTO.getWarranty());
        purchaseOrder.setConsignesAddress(purchaseOrderRequestDTO.getConsignesAddress());
        purchaseOrder.setBillingAddress(purchaseOrderRequestDTO.getBillingAddress());
        purchaseOrder.setDeliveryPeriod(purchaseOrderRequestDTO.getDeliveryPeriod());
        purchaseOrder.setIfLdClauseApplicable(purchaseOrderRequestDTO.getIfLdClauseApplicable());
        purchaseOrder.setIncoTerms(purchaseOrderRequestDTO.getIncoTerms());
        purchaseOrder.setPaymentTerms(purchaseOrderRequestDTO.getPaymentTerms());
        purchaseOrder.setVendorName(purchaseOrderRequestDTO.getVendorName());
        purchaseOrder.setVendorAddress(purchaseOrderRequestDTO.getVendorAddress());
        purchaseOrder.setApplicablePbgToBeSubmitted(purchaseOrderRequestDTO.getApplicablePbgToBeSubmitted());
        purchaseOrder.setTransporterAndFreightForWarderDetails(purchaseOrderRequestDTO.getTransporterAndFreightForWarderDetails());
        purchaseOrder.setVendorAccountNumber(purchaseOrderRequestDTO.getVendorAccountNumber());
        purchaseOrder.setVendorsZfscCode(purchaseOrderRequestDTO.getVendorsIfscCode());
        purchaseOrder.setVendorAccountName(purchaseOrderRequestDTO.getVendorAccountName());
        purchaseOrder.setVendorId(purchaseOrderRequestDTO.getVendorId());
        purchaseOrder.setQuotationNumber(purchaseOrderRequestDTO.getQuotationNumber());
        purchaseOrder.setAdditionalTermsAndConditions(purchaseOrderRequestDTO.getAdditionalTermsAndConditions());
        purchaseOrder.setBuyBackAmount(purchaseOrderRequestDTO.getBuyBackAmount());
        //  purchaseOrder.setTotalValueOfPo(purchaseOrderRequestDTO.getTotalValueOfPo());
        String Date = purchaseOrderRequestDTO.getDeliveryDate();
        if (Date != null) {
            purchaseOrder.setDeliveryDate(CommonUtils.convertStringToDateObject(Date));
        } else {
            purchaseOrder.setDeliveryDate(null);
        }
        String quotationDate = purchaseOrderRequestDTO.getQuotationDate();
        if (Date != null) {
            purchaseOrder.setQuotationDate(CommonUtils.convertStringToDateObject(quotationDate));
        } else {
            purchaseOrder.setQuotationDate(null);
        }
        if (purchaseOrderRequestDTO.getComparativeStatementFileName() == null || purchaseOrderRequestDTO.getComparativeStatementFileName().isEmpty()) {
            purchaseOrder.setComparativeStatementFileName(null);
        } else {
            String saved = saveBase64Files(purchaseOrderRequestDTO.getComparativeStatementFileName(), basePath);
            purchaseOrder.setComparativeStatementFileName(saved);
        }
        purchaseOrder.setProjectName(purchaseOrderRequestDTO.getProjectName());
        purchaseOrder.setCreatedBy(purchaseOrderRequestDTO.getCreatedBy());
        purchaseOrder.setUpdatedBy(purchaseOrderRequestDTO.getUpdatedBy());
        List<PurchaseOrderAttributes> purchaseOrderAttributes = purchaseOrderRequestDTO.getPurchaseOrderAttributes().stream()
                .map(dto -> {

                    PurchaseOrderAttributes attribute = new PurchaseOrderAttributes();
                    attribute.setMaterialCode(dto.getMaterialCode());
                    // attribute.setPoId(purchaseOrderRequestDTO.getPoId());
                    //  attribute.setPoId(poId);
                    attribute.setMaterialDescription(dto.getMaterialDescription());
                    attribute.setQuantity(dto.getQuantity());
                    attribute.setRate(dto.getRate());
                    attribute.setCurrency(dto.getCurrency());
                    attribute.setExchangeRate(dto.getExchangeRate());
                    attribute.setGst(dto.getGst());
                    attribute.setDuties(dto.getDuties());
                    attribute.setFreightCharge(dto.getFreightCharge());
                    attribute.setBudgetCode(dto.getBudgetCode());
                    BigDecimal total = calculateTotalPriceInInr(
                            dto.getRate(),
                            dto.getExchangeRate(),
                            dto.getCurrency(),
                            dto.getQuantity(),
                            dto.getGst(),
                            dto.getDuties(),
                            dto.getFreightCharge()
                    );
                    attribute.setTotalPoMaterialPriceInInr(total);
                    attribute.setPurchaseOrder(purchaseOrder);
                    return attribute;
                })
                .collect(Collectors.toList());
        // purchaseOrder.setPurchaseOrderAttributes(purchaseOrderAttributes);
        // purchaseOrderRepository.save(purchaseOrder);
        // Set attributes and save order
        BigDecimal totalPoValue = purchaseOrderAttributes.stream()
                .map(PurchaseOrderAttributes::getTotalPoMaterialPriceInInr)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        purchaseOrder.setTotalValueOfPo(totalPoValue);

        purchaseOrder.setPurchaseOrderAttributes(purchaseOrderAttributes);
       // List<String> indentIds = indentIdRepository.findTenderWithIndent(purchaseOrder.getTenderId());

        // Calculate total tender value by summing totalPriceOfAllMaterials of all indents
       /* BigDecimal totalTenderValue = indentIds.stream()
                .map(indentCreationService::getIndentById) // Fetch Indent data
                .map(IndentCreationResponseDTO::getTotalPriceOfAllMaterials) // Extract total price
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up values
        purchaseOrder.setTotalValueOfPo(totalTenderValue);
        System.out.println("tottalTenderValue" + totalTenderValue);*/
        purchaseOrderRepository.save(purchaseOrder);
        TenderRequest existing = trRepo.findById(tenderId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Tender request not found for the provided ID."
                        )
                ));

        existing.setVendorId(purchaseOrderRequestDTO.getVendorId());
      //  existing.setQuotationFileName(dto.getQuotationFileName());

        // TC_48: Lock the tender after PO creation to prevent further updates
        existing.setIsLocked(true);
        existing.setLockedReason("Purchase Order " + poId + " has been created for this tender");
        existing.setLockedForPO(poId);
        existing.setLockedDate(LocalDateTime.now());

        TenderRequest saved = trRepo.save(existing);
        return mapToResponseDTO(purchaseOrder);
    }

    public String generatePoId(String tenderId) {
        String numericPart = tenderId.replaceAll("\\D+", "");
        return "PO" + numericPart;
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
@Override
public List<PurchaseOrderResponseDTO> getPoVersionHistory(String poId) {
    String baseId = extractBasePoId(poId);
    return purchaseOrderRepository.findAllVersionsByBaseId(baseId)
            .stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
}
@Override
public PurchaseOrderResponseDTO updatePurchaseOrder(String poId, PurchaseOrderRequestDTO dto) {

    // 1. Load existing active PO
    PurchaseOrder old = purchaseOrderRepository.findById(poId)
            .orElseThrow(() -> new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE, AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION, "Purchase order not found for the provided ID.")));

    // 2. Guard: only original creator can edit
    if (!old.getCreatedBy().equals(dto.getCreatedBy())) {
        throw new BusinessException(new ErrorDetails(
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION, "Only the original PO Creator can edit this Purchase Order."));
    }

    // 3. Guard: locked PO cannot be edited
    if (Boolean.TRUE.equals(old.getIsLocked())) {
        throw new BusinessException(new ErrorDetails(
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION, "Purchase Order is locked and cannot be edited."));
    }

    // 4. Save history snapshot of old version
    PurchaseOrderHistory history = new PurchaseOrderHistory();
    history.setPoId(old.getPoId());
    history.setVersion(old.getPoVersion());
    history.setModifiedBy(dto.getUpdatedBy());
    history.setModifiedDate(new java.util.Date());
    try {
        ObjectMapper mapper = new ObjectMapper();
        history.setSnapshotJson(mapper.writeValueAsString(old));
    } catch (Exception e) {
        e.printStackTrace();
    }
    purchaseOrderHistoryRepository.save(history);

    // 5. Deactivate old version
    old.setIsActive(false);
    purchaseOrderRepository.save(old);

    // 6. Supersede old version's pending workflow transitions
    List<WorkflowTransition> pendingTransitions =
            workflowTransitionRepository.findPendingTransitionsByRequestId(old.getPoId());
    for (WorkflowTransition wt : pendingTransitions) {
        wt.setStatus("SUPERSEDED");
        wt.setNextAction(null);
        wt.setRemarks("Superseded by new version: " + extractBasePoId(old.getPoId()) + "/" + (old.getPoVersion() + 1));
        workflowTransitionRepository.save(wt);
    }

    // 7. Compute new PO ID e.g. PO1001 -> PO1001/2, PO1001/2 -> PO1001/3
    String baseId = extractBasePoId(old.getPoId());
    int newVersion = old.getPoVersion() + 1;
    String newPoId = baseId + "/" + newVersion;

    // 8. Build new PurchaseOrder (copy-new pattern)
    PurchaseOrder newPO = new PurchaseOrder();
    newPO.setPoId(newPoId);
    newPO.setPoVersion(newVersion);
    newPO.setIsActive(true);
    newPO.setParentPoId(old.getPoId());
    newPO.setCreatedBy(old.getCreatedBy());
    newPO.setUpdatedBy(dto.getUpdatedBy());
    newPO.setIsLocked(false);
    newPO.setIsCancelled(false);
    newPO.setCurrentStatus("DRAFT");

    // Copy all fields from request
    newPO.setTenderId(dto.getTenderId());
    newPO.setIndentId(dto.getIndentId());
    newPO.setWarranty(dto.getWarranty());
    newPO.setConsignesAddress(dto.getConsignesAddress());
    newPO.setBillingAddress(dto.getBillingAddress());
    newPO.setDeliveryPeriod(dto.getDeliveryPeriod());
    newPO.setIfLdClauseApplicable(dto.getIfLdClauseApplicable());
    newPO.setIncoTerms(dto.getIncoTerms());
    newPO.setPaymentTerms(dto.getPaymentTerms());
    newPO.setVendorName(dto.getVendorName());
    newPO.setVendorAddress(dto.getVendorAddress());
    newPO.setApplicablePbgToBeSubmitted(dto.getApplicablePbgToBeSubmitted());
    newPO.setTransporterAndFreightForWarderDetails(dto.getTransporterAndFreightForWarderDetails());
    newPO.setVendorAccountNumber(dto.getVendorAccountNumber());
    newPO.setVendorsZfscCode(dto.getVendorsIfscCode());
    newPO.setVendorAccountName(dto.getVendorAccountName());
    newPO.setVendorId(dto.getVendorId());
    newPO.setProjectName(dto.getProjectName());
    newPO.setQuotationNumber(dto.getQuotationNumber());
    newPO.setAdditionalTermsAndConditions(dto.getAdditionalTermsAndConditions());
    newPO.setBuyBackAmount(dto.getBuyBackAmount());
    newPO.setTypeOfSecurity(dto.getTypeOfSecurity());
    newPO.setSecurityNumber(dto.getSecurityNumber());

    String deliveryDate = dto.getDeliveryDate();
    newPO.setDeliveryDate(deliveryDate != null ? CommonUtils.convertStringToDateObject(deliveryDate) : null);

    String securityDate = dto.getSecurityDate();
    newPO.setSecurityDate(securityDate != null ? CommonUtils.convertStringToDateObject(securityDate) : null);

    String expiryDate = dto.getExpiryDate();
    newPO.setExpiryDate(expiryDate != null ? CommonUtils.convertStringToDateObject(expiryDate) : null);

    String quotationDate = dto.getQuotationDate();
    newPO.setQuotationDate(quotationDate != null ? CommonUtils.convertStringToDateObject(quotationDate) : null);

    // Files
    if (dto.getComparativeStatementFileName() == null || dto.getComparativeStatementFileName().isEmpty()) {
        newPO.setComparativeStatementFileName(null);
    } else {
        newPO.setComparativeStatementFileName(saveBase64Files(dto.getComparativeStatementFileName(), basePath));
    }
    if (dto.getGemContractFileName() == null || dto.getGemContractFileName().isEmpty()) {
        newPO.setGemContractUpload(null);
    } else {
        newPO.setGemContractUpload(saveBase64Files(dto.getGemContractFileName(), basePath));
    }

    // 9. Build new attributes
    List<PurchaseOrderAttributes> newAttributes = dto.getPurchaseOrderAttributes().stream()
            .map(attrDto -> {
                PurchaseOrderAttributes attr = new PurchaseOrderAttributes();
                attr.setMaterialCode(attrDto.getMaterialCode());
                attr.setMaterialDescription(attrDto.getMaterialDescription());
                attr.setQuantity(attrDto.getQuantity());
                attr.setRate(attrDto.getRate());
                attr.setCurrency(attrDto.getCurrency());
                attr.setExchangeRate(attrDto.getExchangeRate());
                attr.setGst(attrDto.getGst());
                attr.setDuties(attrDto.getDuties());
                attr.setFreightCharge(attrDto.getFreightCharge());
                attr.setBudgetCode(attrDto.getBudgetCode());
                attr.setPurchaseOrder(newPO);
                BigDecimal total = calculateTotalPriceInInr(
                        attrDto.getRate(), attrDto.getExchangeRate(), attrDto.getCurrency(),
                        attrDto.getQuantity(), attrDto.getGst(), attrDto.getDuties(), attrDto.getFreightCharge());
                attr.setTotalPoMaterialPriceInInr(total);
                return attr;
            }).collect(Collectors.toList());

    newPO.setPurchaseOrderAttributes(newAttributes);

    BigDecimal totalPoValue = newAttributes.stream()
            .map(PurchaseOrderAttributes::getTotalPoMaterialPriceInInr)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    newPO.setTotalValueOfPo(totalPoValue);

    // 10. Save new PO version
    purchaseOrderRepository.save(newPO);

    return mapToResponseDTO(newPO);
}
//     public PurchaseOrderResponseDTO updatePurchaseOrder(String poId, PurchaseOrderRequestDTO purchaseOrderRequestDTO) {
//         PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
//                 .orElseThrow(() -> new BusinessException(
//                         new ErrorDetails(
//                                 AppConstant.ERROR_CODE_RESOURCE,
//                                 AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                                 AppConstant.ERROR_TYPE_VALIDATION,
//                                 "Purchase order not found for the provided asset ID.")
//                 ));
//         // added by abhinav starts
//         PurchaseOrderHistory history = new PurchaseOrderHistory();

//         history.setPoId(purchaseOrder.getPoId());
//         history.setVersion(purchaseOrder.getPoVersion());
//         history.setModifiedBy(purchaseOrderRequestDTO.getUpdatedBy());
//         history.setModifiedDate(new java.util.Date());

//         try {
//             ObjectMapper mapper = new ObjectMapper();
//             history.setSnapshotJson(mapper.writeValueAsString(purchaseOrder));
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//         purchaseOrderHistoryRepository.save(history);

//         // INCREMENT VERSION
//         if (purchaseOrder.getPoVersion() == null) {
//             purchaseOrder.setPoVersion(1);
//         } else {
//             purchaseOrder.setPoVersion(purchaseOrder.getPoVersion() + 1);
//         }
//         //  LOCK CHECK
//         // if (purchaseOrder.getIsLocked() != null && purchaseOrder.getIsLocked()) {
//         //     throw new RuntimeException("PO is locked and cannot be edited.");
//         // }
//         //added by abhinav ends

//         // Update basic fields
//         purchaseOrder.setTenderId(purchaseOrderRequestDTO.getTenderId());
//         purchaseOrder.setIndentId(purchaseOrderRequestDTO.getIndentId());
//         purchaseOrder.setWarranty(purchaseOrderRequestDTO.getWarranty());
//         purchaseOrder.setConsignesAddress(purchaseOrderRequestDTO.getConsignesAddress());
//         purchaseOrder.setBillingAddress(purchaseOrderRequestDTO.getBillingAddress());
//         purchaseOrder.setDeliveryPeriod(purchaseOrderRequestDTO.getDeliveryPeriod());
//         purchaseOrder.setIfLdClauseApplicable(purchaseOrderRequestDTO.getIfLdClauseApplicable());
//         purchaseOrder.setIncoTerms(purchaseOrderRequestDTO.getIncoTerms());
//         purchaseOrder.setPaymentTerms(purchaseOrderRequestDTO.getPaymentTerms());
//         purchaseOrder.setVendorName(purchaseOrderRequestDTO.getVendorName());
//         purchaseOrder.setVendorAddress(purchaseOrderRequestDTO.getVendorAddress());
//         purchaseOrder.setApplicablePbgToBeSubmitted(purchaseOrderRequestDTO.getApplicablePbgToBeSubmitted());
//         purchaseOrder.setTransporterAndFreightForWarderDetails(purchaseOrderRequestDTO.getTransporterAndFreightForWarderDetails());
//         purchaseOrder.setVendorAccountNumber(purchaseOrderRequestDTO.getVendorAccountNumber());
//         purchaseOrder.setVendorsZfscCode(purchaseOrderRequestDTO.getVendorsIfscCode());
//         purchaseOrder.setVendorAccountName(purchaseOrderRequestDTO.getVendorAccountName());
//         purchaseOrder.setProjectName(purchaseOrderRequestDTO.getProjectName());
//         purchaseOrder.setVendorId(purchaseOrderRequestDTO.getVendorId());
//         purchaseOrder.setUpdatedBy(purchaseOrderRequestDTO.getUpdatedBy());
//         purchaseOrder.setCreatedBy(purchaseOrderRequestDTO.getCreatedBy());
//         if (purchaseOrderRequestDTO.getComparativeStatementFileName() == null || purchaseOrderRequestDTO.getComparativeStatementFileName().isEmpty()) {
//             purchaseOrder.setComparativeStatementFileName(null);
//         } else {
//             String saved = saveBase64Files(purchaseOrderRequestDTO.getComparativeStatementFileName(), basePath);
//             purchaseOrder.setComparativeStatementFileName(saved);
//         }
//         if (purchaseOrderRequestDTO.getGemContractFileName() == null || purchaseOrderRequestDTO.getGemContractFileName().isEmpty()) {
//             purchaseOrder.setGemContractUpload(null);
//         } else {
//             String saved = saveBase64Files(purchaseOrderRequestDTO.getGemContractFileName(), basePath);
//             purchaseOrder.setGemContractUpload(saved);
//         }
//         purchaseOrder.setTypeOfSecurity(purchaseOrderRequestDTO.getTypeOfSecurity());
//         purchaseOrder.setSecurityNumber(purchaseOrderRequestDTO.getSecurityNumber());
//         String Date = purchaseOrderRequestDTO.getSecurityDate();
//         if (Date != null) {
//             purchaseOrder.setSecurityDate(CommonUtils.convertStringToDateObject(Date));
//         } else {
//             purchaseOrder.setSecurityDate(null);
//         }
//         String expiryDate = purchaseOrderRequestDTO.getExpiryDate();
//         if(Date != null){
//             purchaseOrder.setExpiryDate(CommonUtils.convertStringToDateObject(expiryDate));
//         } else {
//             purchaseOrder.setExpiryDate(null);
//         }


//         String date = purchaseOrderRequestDTO.getDeliveryDate();
//         purchaseOrder.setDeliveryDate(date != null ? CommonUtils.convertStringToDateObject(date) : null);

//         // Remove old attributes
//         purchaseOrder.getPurchaseOrderAttributes().clear();

//         // Add new attributes
//         List<PurchaseOrderAttributes> newAttributes = purchaseOrderRequestDTO.getPurchaseOrderAttributes().stream()
//                 .map(dto -> {
//                     PurchaseOrderAttributes attr = new PurchaseOrderAttributes();
//                     attr.setMaterialCode(dto.getMaterialCode());
//                     attr.setMaterialDescription(dto.getMaterialDescription());
//                     attr.setQuantity(dto.getQuantity());
//                     attr.setRate(dto.getRate());
//                     attr.setCurrency(dto.getCurrency());
//                     attr.setExchangeRate(dto.getExchangeRate());
//                     attr.setGst(dto.getGst());
//                     attr.setDuties(dto.getDuties());
//                     attr.setFreightCharge(dto.getFreightCharge());
//                     attr.setBudgetCode(dto.getBudgetCode());
//                     attr.setPurchaseOrder(purchaseOrder);  // Associate back
//                     BigDecimal total = calculateTotalPriceInInr(
//                             dto.getRate(),
//                             dto.getExchangeRate(),
//                             dto.getCurrency(),
//                             dto.getQuantity(),
//                             dto.getGst(),
//                             dto.getDuties(),
//                             dto.getFreightCharge()
//                     );
//                     attr.setTotalPoMaterialPriceInInr(total);
//                     return attr;
//                 })
//                 .collect(Collectors.toList());

//         purchaseOrder.getPurchaseOrderAttributes().addAll(newAttributes);
//       //  List<String> indentIds = indentIdRepository.findTenderWithIndent(purchaseOrder.getTenderId());
// /*
//         BigDecimal totalTenderValue = indentIds.stream()
//                 .map(indentCreationService::getIndentById)
//                 .map(IndentCreationResponseDTO::getTotalPriceOfAllMaterials)
//                 .reduce(BigDecimal.ZERO, BigDecimal::add);
//         purchaseOrder.setTotalValueOfPo(totalTenderValue);*/
//         BigDecimal totalPoValue = newAttributes.stream()
//                 .map(PurchaseOrderAttributes::getTotalPoMaterialPriceInInr)
//                 .filter(Objects::nonNull)
//                 .reduce(BigDecimal.ZERO, BigDecimal::add);
//         purchaseOrder.setTotalValueOfPo(totalPoValue);

//         // Save
//         purchaseOrderRepository.save(purchaseOrder);

//         return mapToResponseDTO(purchaseOrder);
//     }


    @Autowired
    private GprnMaterialDtlRepository gprnMaterialDtlRepository;

    public poWithTenderAndIndentResponseDTO getPurchaseOrderById(String poId)  {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Purchase order not found for the provided asset ID.")
                ));

        // Fetch related Tender & Indent
        TenderWithIndentResponseDTO tenderWithIndent = tenderRequestService.getTenderRequestById(purchaseOrder.getTenderId());
        Map<String, MaterialDetailsResponseDTO> indentMaterialMap = new HashMap<>();

        for (IndentCreationResponseDTO indent : tenderWithIndent.getIndentResponseDTO()) {
            for (MaterialDetailsResponseDTO material : indent.getMaterialDetails()) {
                indentMaterialMap.put(material.getMaterialCode(), material);
            }
        }


        poWithTenderAndIndentResponseDTO responseDTO = new poWithTenderAndIndentResponseDTO();
        responseDTO.setPoId(purchaseOrder.getPoId());
        responseDTO.setTenderId(purchaseOrder.getTenderId());
        responseDTO.setIndentId(purchaseOrder.getIndentId());
        responseDTO.setWarranty(purchaseOrder.getWarranty());
        responseDTO.setConsignesAddress(purchaseOrder.getConsignesAddress());
        responseDTO.setBillingAddress(purchaseOrder.getBillingAddress());
        responseDTO.setDeliveryPeriod(purchaseOrder.getDeliveryPeriod());
        responseDTO.setIfLdClauseApplicable(purchaseOrder.getIfLdClauseApplicable());
        responseDTO.setIncoTerms(purchaseOrder.getIncoTerms());
        responseDTO.setPaymentTerms(purchaseOrder.getPaymentTerms());
        responseDTO.setVendorName(purchaseOrder.getVendorName());
        responseDTO.setVendorAddress(purchaseOrder.getVendorAddress());
        responseDTO.setApplicablePbgToBeSubmitted(purchaseOrder.getApplicablePbgToBeSubmitted());
        responseDTO.setTransporterAndFreightForWarderDetails(purchaseOrder.getTransporterAndFreightForWarderDetails());
        responseDTO.setVendorAccountNumber(purchaseOrder.getVendorAccountNumber());
        responseDTO.setVendorsIfscCode(purchaseOrder.getVendorsZfscCode());
        responseDTO.setVendorAccountName(purchaseOrder.getVendorAccountName());
        responseDTO.setVendorId(purchaseOrder.getVendorId());
        responseDTO.setComparativeStatementFileName(purchaseOrder.getComparativeStatementFileName());
       // responseDTO.setGemContractFileName(purchaseOrder.getGemContractUpload());
        if (purchaseOrder.getGemContractUpload() == null || purchaseOrder.getGemContractUpload().isEmpty()) {
            responseDTO.setGemContractFileName(null);
        } else {
            try {
                responseDTO.setGemContractFileName(
                        convertFilesToBase64(purchaseOrder.getGemContractUpload(), basePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //  responseDTO.setProjectName(purchaseOrder.getProjectName());
       // responseDTO.setTotalValueOfPo(tenderWithIndent.getTotalTenderValue());
        responseDTO.setTotalValueOfPo(purchaseOrder.getTotalValueOfPo());
        LocalDate date = purchaseOrder.getDeliveryDate();
        if (date != null) {
            responseDTO.setDeliveryDate(CommonUtils.convertDateToString(date));
        } else {
            responseDTO.setDeliveryDate(null);
        }
        responseDTO.setCreatedBy(purchaseOrder.getCreatedBy());
        responseDTO.setUpdatedBy(purchaseOrder.getUpdatedBy());
        responseDTO.setCreatedDate(purchaseOrder.getCreatedDate());
        responseDTO.setUpdatedDate(purchaseOrder.getUpdatedDate());
        List<String> indentIds = indentIdRepository.findTenderWithIndent(purchaseOrder.getTenderId());

        responseDTO.setIndentIds(indentIds);

        responseDTO.setPurchaseOrderAttributes(purchaseOrder.getPurchaseOrderAttributes().stream()
                .map(attribute -> {
                    PurchaseOrderAttributesResponseDTO attributeDTO = new PurchaseOrderAttributesResponseDTO();
                    attributeDTO.setMaterialCode(attribute.getMaterialCode());
                    attributeDTO.setMaterialDescription(attribute.getMaterialDescription());

                    // Get sum of GPRN quantities for this material
                  /*  BigDecimal gprnQuantity = gprnMaterialDtlRepository
                            .findByPoIdAndMaterialCode(poId, attribute.getMaterialCode())
                            .stream()
                            .map(gprn -> gprn.getReceivedQuantity())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
*/
                    // Set remaining quantity
                  //  attributeDTO.setQuantity(attribute.getQuantity().subtract(gprnQuantity));

                    attributeDTO.setTotalQuantity(attribute.getQuantity());
                    attributeDTO.setReceivedQuantity(attribute.getReceivedQuantity());
                    attributeDTO.setRate(attribute.getRate());
                    attributeDTO.setCurrency(attribute.getCurrency());
                    attributeDTO.setExchangeRate(attribute.getExchangeRate());
                    attributeDTO.setGst(attribute.getGst());
                    attributeDTO.setDuties(attribute.getDuties());
                    attributeDTO.setFreightCharge(attribute.getFreightCharge());
                    attributeDTO.setBudgetCode(attribute.getBudgetCode());
                    MaterialDetailsResponseDTO indentMaterial = indentMaterialMap.get(attribute.getMaterialCode());
                    attributeDTO.setUnitPrice(indentMaterial.getUnitPrice());
                    attributeDTO.setUom(indentMaterial.getUom());
                    attributeDTO.setCategory(indentMaterial.getMaterialCategory());
                    return attributeDTO;
                })
                .collect(Collectors.toList()));
        String projectName = tenderWithIndent.getIndentResponseDTO()
                .stream()
                .findFirst()
                .map(IndentCreationResponseDTO::getProjectName)
                .orElse(null);
        BigDecimal projectLimit = tenderWithIndent.getIndentResponseDTO()
                .stream()
                .findFirst()
                .map(IndentCreationResponseDTO::getProjectLimit)
                .orElse(null);
        responseDTO.setProjectName(projectName);
        responseDTO.setProjectLimit(projectLimit);
        // Set Tender & Indent details
        responseDTO.setTenderDetails(tenderWithIndent);
        return responseDTO;

    }
    public PoWithTenderAndIndentBase64FilesDto getPurchaseOrderBase64FilesById(String poId) throws IOException {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Purchase order not found for the provided asset ID.")
                ));

        // Fetch related Tender & Indent
        TenderWithIndentResponseDTO tenderWithIndent = tenderRequestService.getTenderRequestById(purchaseOrder.getTenderId());
        Map<String, MaterialDetailsResponseDTO> indentMaterialMap = new HashMap<>();

        for (IndentCreationResponseDTO indent : tenderWithIndent.getIndentResponseDTO()) {
            for (MaterialDetailsResponseDTO material : indent.getMaterialDetails()) {
                indentMaterialMap.put(material.getMaterialCode(), material);
            }
        }


        PoWithTenderAndIndentBase64FilesDto responseDTO = new PoWithTenderAndIndentBase64FilesDto();
        responseDTO.setPoId(purchaseOrder.getPoId());
        responseDTO.setTenderId(purchaseOrder.getTenderId());
        responseDTO.setIndentId(purchaseOrder.getIndentId());
        responseDTO.setWarranty(purchaseOrder.getWarranty());
        responseDTO.setConsignesAddress(purchaseOrder.getConsignesAddress());
        responseDTO.setBillingAddress(purchaseOrder.getBillingAddress());
        responseDTO.setDeliveryPeriod(purchaseOrder.getDeliveryPeriod());
        responseDTO.setIfLdClauseApplicable(purchaseOrder.getIfLdClauseApplicable());
        responseDTO.setIncoTerms(purchaseOrder.getIncoTerms());
        responseDTO.setPaymentTerms(purchaseOrder.getPaymentTerms());
        responseDTO.setVendorName(purchaseOrder.getVendorName());
        responseDTO.setVendorAddress(purchaseOrder.getVendorAddress());
        responseDTO.setApplicablePbgToBeSubmitted(purchaseOrder.getApplicablePbgToBeSubmitted());
        responseDTO.setTransporterAndFreightForWarderDetails(purchaseOrder.getTransporterAndFreightForWarderDetails());
        responseDTO.setVendorAccountNumber(purchaseOrder.getVendorAccountNumber());
        responseDTO.setVendorsIfscCode(purchaseOrder.getVendorsZfscCode());
        responseDTO.setVendorAccountName(purchaseOrder.getVendorAccountName());
        responseDTO.setVendorId(purchaseOrder.getVendorId());
        responseDTO.setComparativeStatementFileName(purchaseOrder.getComparativeStatementFileName());
        responseDTO.setTypeOfSecurity(purchaseOrder.getTypeOfSecurity());
        responseDTO.setSecurityNumber(purchaseOrder.getSecurityNumber());
        responseDTO.setSecurityDate(CommonUtils.convertDateToString(purchaseOrder.getSecurityDate()));
        responseDTO.setExpiryDate(CommonUtils.convertDateToString(purchaseOrder.getExpiryDate()));
        //  responseDTO.setProjectName(purchaseOrder.getProjectName());
        // responseDTO.setTotalValueOfPo(tenderWithIndent.getTotalTenderValue());
        if (purchaseOrder.getGemContractUpload() == null || purchaseOrder.getGemContractUpload().isEmpty()) {
            responseDTO.setGemContractFileName(null);
        } else {
            try {
                responseDTO.setGemContractFileName(
                        convertFilesToBase64(purchaseOrder.getGemContractUpload(), basePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        responseDTO.setTotalValueOfPo(purchaseOrder.getTotalValueOfPo());
        LocalDate date = purchaseOrder.getDeliveryDate();
        if (date != null) {
            responseDTO.setDeliveryDate(CommonUtils.convertDateToString(date));
        } else {
            responseDTO.setDeliveryDate(null);
        }
        if (purchaseOrder.getComparativeStatementFileName() == null || purchaseOrder.getComparativeStatementFileName().isEmpty()) {
            responseDTO.setComparativeStatementFileNameList(null);
        } else {
            responseDTO.setComparativeStatementFileNameList(
                    convertFilesToBase64(purchaseOrder.getComparativeStatementFileName(), basePath));
        }
        responseDTO.setCreatedBy(purchaseOrder.getCreatedBy());
        responseDTO.setUpdatedBy(purchaseOrder.getUpdatedBy());
        responseDTO.setCreatedDate(purchaseOrder.getCreatedDate());
        responseDTO.setUpdatedDate(purchaseOrder.getUpdatedDate());
        List<String> indentIds = indentIdRepository.findTenderWithIndent(purchaseOrder.getTenderId());

        responseDTO.setIndentIds(indentIds);

        responseDTO.setPurchaseOrderAttributes(purchaseOrder.getPurchaseOrderAttributes().stream()
                .map(attribute -> {
                    PurchaseOrderAttributesResponseDTO attributeDTO = new PurchaseOrderAttributesResponseDTO();
                    attributeDTO.setMaterialCode(attribute.getMaterialCode());
                    attributeDTO.setMaterialDescription(attribute.getMaterialDescription());

                    // Get sum of GPRN quantities for this material
                    BigDecimal gprnQuantity = gprnMaterialDtlRepository
                            .findByPoIdAndMaterialCode(poId, attribute.getMaterialCode())
                            .stream()
                            .map(gprn -> gprn.getReceivedQuantity())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    // Set remaining quantity
                    attributeDTO.setQuantity(attribute.getQuantity().subtract(gprnQuantity));
                    attributeDTO.setReceivedQuantity(attribute.getReceivedQuantity());
                    attributeDTO.setRate(attribute.getRate());
                    attributeDTO.setCurrency(attribute.getCurrency());
                    attributeDTO.setExchangeRate(attribute.getExchangeRate());
                    attributeDTO.setGst(attribute.getGst());
                    attributeDTO.setDuties(attribute.getDuties());
                    attributeDTO.setFreightCharge(attribute.getFreightCharge());
                    attributeDTO.setBudgetCode(attribute.getBudgetCode());
                    MaterialDetailsResponseDTO indentMaterial = indentMaterialMap.get(attribute.getMaterialCode());
                    attributeDTO.setUnitPrice(indentMaterial.getUnitPrice());
                    attributeDTO.setUom(indentMaterial.getUom());
                    attributeDTO.setCategory(indentMaterial.getMaterialCategory());
                    return attributeDTO;
                })
                .collect(Collectors.toList()));
        String projectName = tenderWithIndent.getIndentResponseDTO()
                .stream()
                .findFirst()
                .map(IndentCreationResponseDTO::getProjectName)
                .orElse(null);
        BigDecimal projectLimit = tenderWithIndent.getIndentResponseDTO()
                .stream()
                .findFirst()
                .map(IndentCreationResponseDTO::getProjectLimit)
                .orElse(null);
        responseDTO.setProjectName(projectName);
        responseDTO.setProjectLimit(projectLimit);
        WorkflowTransition wt = workflowTransitionRepository.findTopByRequestIdOrderByWorkflowSequenceDesc(poId);
        responseDTO.setStatus(wt.getStatus());
        responseDTO.setProcessStage(wt.getNextRole());

        // Set Tender & Indent details
        responseDTO.setTenderDetails(tenderWithIndent);
       PoFormateDto dtp = getPoFormatDetails(poId);
       responseDTO.setPoFormateData(dtp);
     List<WorkflowTransitionDto> wtd =workflowTransitionHistory(poId);
        List<PoFormateApprovalHistory> historyList = wtd.stream().map(dto -> {
            PoFormateApprovalHistory history = new PoFormateApprovalHistory();
            history.setStatus(dto.getStatus());
            history.setNextAction(dto.getNextAction());
            history.setAction(dto.getAction());
            history.setRemarks(dto.getRemarks());
            history.setCreatedBy(dto.getCreatedBy());
            history.setCreatedRole(dto.getCreatedRole());
            history.setModifiedBy(dto.getModifiedBy());
            history.setModificationDate(dto.getModificationDate());
            history.setCreatedDate(dto.getCreatedDate());
            return history;
        }).toList();

        responseDTO.setPoHistory(historyList);

        return responseDTO;
    }
    public List<WorkflowTransitionDto> workflowTransitionHistory(String requestId) {

        List<WorkflowTransitionDto> workflowTransitionDtoList = new ArrayList<>();
        List<WorkflowTransition> workflowTransitionList = null;
        workflowTransitionList = workflowTransitionRepository.findByRequestId(requestId);
        if (Objects.nonNull(workflowTransitionList) && !workflowTransitionList.isEmpty()) {
            workflowTransitionDtoList = workflowTransitionList.stream().sorted(Comparator.comparing(WorkflowTransition::getWorkflowSequence)).map(e -> {
                return mapWorkflowTransitionDto(e);
            }).collect(Collectors.toList());
        }

        return workflowTransitionDtoList;
    } private WorkflowTransitionDto mapWorkflowTransitionDto(WorkflowTransition workflowTransition) {
        WorkflowTransitionDto workflowTransitionDto = new WorkflowTransitionDto();
        workflowTransitionDto.setWorkflowTransitionId(workflowTransition.getWorkflowTransitionId());
        workflowTransitionDto.setTransitionId(workflowTransition.getTransitionId());
        workflowTransitionDto.setWorkflowId(workflowTransition.getWorkflowId());
        workflowTransitionDto.setWorkflowName(workflowTransition.getWorkflowName());
        workflowTransitionDto.setModificationDate(workflowTransition.getModificationDate());
        workflowTransitionDto.setCreatedBy(workflowTransition.getCreatedBy());
        workflowTransitionDto.setTransitionOrder(workflowTransition.getTransitionOrder());
        workflowTransitionDto.setRequestId(workflowTransition.getRequestId());
        workflowTransitionDto.setStatus(workflowTransition.getStatus());
        workflowTransitionDto.setTransitionSubOrder(workflowTransition.getTransitionSubOrder());
        workflowTransitionDto.setCreatedDate(workflowTransition.getCreatedDate());
        workflowTransitionDto.setModifiedBy(workflowTransition.getModifiedBy());
        workflowTransitionDto.setNextAction(workflowTransition.getNextAction());
        workflowTransitionDto.setCurrentRole(workflowTransition.getCurrentRole());
        workflowTransitionDto.setNextRole(workflowTransition.getNextRole());
        workflowTransitionDto.setWorkflowSequence(workflowTransition.getWorkflowSequence());
        workflowTransitionDto.setAction(workflowTransition.getAction());
        workflowTransitionDto.setRemarks(workflowTransition.getRemarks());
        return workflowTransitionDto;
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

    private String extractBasePoId(String poId) {
    if (poId == null) return null;
    int slashIdx = poId.indexOf('/');
    return slashIdx >= 0 ? poId.substring(0, slashIdx) : poId;
}

    @Override
    public List<PurchaseOrderResponseDTO> getAllPurchaseOrders() {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll();
        return purchaseOrders.stream().map(this::mapToResponseDTO).collect(Collectors.toList());  // Map each PurchaseOrder to its DTO
    }


    public void deletePurchaseOrder(String poId) {

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Purchase order not found for the provided ID."
                        )
                ));
        try {
            purchaseOrderRepository.delete(purchaseOrder);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the  po."
                    ),
                    ex
            );
        }

    }


    private PurchaseOrderResponseDTO mapToResponseDTO(PurchaseOrder purchaseOrder) {
        PurchaseOrderResponseDTO responseDTO = new PurchaseOrderResponseDTO();
        responseDTO.setPoId(purchaseOrder.getPoId());
        responseDTO.setTenderId(purchaseOrder.getTenderId());
        responseDTO.setIndentId(purchaseOrder.getIndentId());
        responseDTO.setWarranty(purchaseOrder.getWarranty());
        responseDTO.setConsignesAddress(purchaseOrder.getConsignesAddress());
        responseDTO.setBillingAddress(purchaseOrder.getBillingAddress());
        responseDTO.setDeliveryPeriod(purchaseOrder.getDeliveryPeriod());
        responseDTO.setIfLdClauseApplicable(purchaseOrder.getIfLdClauseApplicable());
        responseDTO.setIncoTerms(purchaseOrder.getIncoTerms());
        responseDTO.setPaymentTerms(purchaseOrder.getPaymentTerms());
        responseDTO.setVendorName(purchaseOrder.getVendorName());
        responseDTO.setVendorAddress(purchaseOrder.getVendorAddress());
        responseDTO.setApplicablePbgToBeSubmitted(purchaseOrder.getApplicablePbgToBeSubmitted());
        responseDTO.setTransporterAndFreightForWarderDetails(purchaseOrder.getTransporterAndFreightForWarderDetails());
        responseDTO.setVendorAccountNumber(purchaseOrder.getVendorAccountNumber());
        responseDTO.setVendorsIfscCode(purchaseOrder.getVendorsZfscCode());
        responseDTO.setVendorAccountName(purchaseOrder.getVendorAccountName());
        responseDTO.setVendorId(purchaseOrder.getVendorId());
        responseDTO.setProjectName(purchaseOrder.getProjectName());
        responseDTO.setComparativeStatementFileName(purchaseOrder.getComparativeStatementFileName());
        //  responseDTO.setTotalValueOfPo(purchaseOrder.getTotalValueOfPo());
        LocalDate date = purchaseOrder.getDeliveryDate();
        if (date != null) {
            responseDTO.setDeliveryDate(CommonUtils.convertDateToString(date));
        } else {
            responseDTO.setDeliveryDate(null);
        }
        responseDTO.setCreatedBy(purchaseOrder.getCreatedBy());
        responseDTO.setUpdatedBy(purchaseOrder.getUpdatedBy());
        responseDTO.setCreatedDate(purchaseOrder.getCreatedDate());
        responseDTO.setUpdatedDate(purchaseOrder.getUpdatedDate());

        responseDTO.setPurchaseOrderAttributes(purchaseOrder.getPurchaseOrderAttributes().stream()
                .map(attribute -> {
                    PurchaseOrderAttributesResponseDTO attributeDTO = new PurchaseOrderAttributesResponseDTO();
                    attributeDTO.setMaterialCode(attribute.getMaterialCode());
                    attributeDTO.setMaterialDescription(attribute.getMaterialDescription());
                    attributeDTO.setQuantity(attribute.getQuantity());
                    attributeDTO.setRate(attribute.getRate());
                    attributeDTO.setCurrency(attribute.getCurrency());
                    attributeDTO.setExchangeRate(attribute.getExchangeRate());
                    attributeDTO.setGst(attribute.getGst());
                    attributeDTO.setDuties(attribute.getDuties());
                    attributeDTO.setFreightCharge(attribute.getFreightCharge());
                    attributeDTO.setBudgetCode(attribute.getBudgetCode());
                    return attributeDTO;
                })
                .collect(Collectors.toList()));
      /*  List<String> indentIds = indentIdRepository.findTenderWithIndent(purchaseOrder.getTenderId());

        // Calculate total tender value by summing totalPriceOfAllMaterials of all indents
        BigDecimal totalTenderValue = indentIds.stream()
                .map(indentCreationService::getIndentById) // Fetch Indent data
                .map(IndentCreationResponseDTO::getTotalPriceOfAllMaterials) // Extract total price
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up values
        responseDTO.setTotalValue(totalTenderValue);
        System.out.println("tottalTenderValue" + totalTenderValue);*/
        responseDTO.setTotalValue(purchaseOrder.getTotalValueOfPo());

        Optional<TenderRequest> tenderRequest = tenderRequestRepository.findByTenderId(purchaseOrder.getTenderId());

        String projectName = tenderRequest.map(TenderRequest::getProjectName).orElse(null);
        responseDTO.setProjectName(projectName);
        System.out.println("projectName:" + projectName);
        BigDecimal allocatedAmount = projectMasterRepository
                .findByProjectNameDescription(projectName)
                .map(ProjectMaster::getAllocatedAmount)
                .orElse(BigDecimal.ZERO);
        responseDTO.setProjectLimit(allocatedAmount);
        System.out.println("allocatedAmount: " + allocatedAmount);
        return responseDTO;
    }

    @Override
    public List<VendorContractReportDTO> getVendorContractDetails(String startDate, String endDate) {
        List<Object[]> results = purchaseOrderRepository.getVendorContractDetails(
                CommonUtils.convertStringToDateObject(startDate),
                CommonUtils.convertStringToDateObject(endDate)
        );

        return results.stream().map(row -> new VendorContractReportDTO(
                (row[0] != null) ? row[0].toString() : "",  // orderId
                (row[1] != null) ? row[1].toString() : "",  // modeOfProcurement
                (row[2] != null) ? row[2].toString() : "",  // underAMC
                (row[3] != null) ? row[3].toString() : "",  // amcFor
                (row[4] != null) ? row[4].toString() : "",  // endUser
                (row[5] != null) ? ((Number) row[5]).intValue() : null,  // noOfParticipants
                (row[6] != null) ? ((Number) row[6]).doubleValue() : null,  // value
                (row[7] != null) ? row[7].toString() : "",  // location
                (row[8] != null) ? row[8].toString() : "",  // vendorName
                (row[9] != null) ? row[9].toString() : "",  // previouslyRenewedAMCs
                (row[10] != null) ? row[10].toString() : "",  // categoryOfSecurity
                (row[11] != null) ? row[11].toString() : ""  // validityOfSecurity
        )).collect(Collectors.toList());
    }

    @Override
    public List<ProcurementActivityReportResponse> getProcurementActivityReport(String startDate, String endDate) {
        List<Object[]> results = purchaseOrderRepository.getProcurementActivityReport(CommonUtils.convertStringToDateObject(startDate),
                CommonUtils.convertStringToDateObject(endDate));

        return results.stream().map(row ->
                new ProcurementActivityReportResponse(
                        (String) row[0],
                        (String) row[1],
                        (String) row[2],
                        (BigDecimal) row[3],
                        (String) row[4],
                        (String) row[5]
                )
        ).collect(Collectors.toList());
    }


    @Override
    public List<ApprovedPoListReportDto> getApprovedPoReport(String startDate, String endDate, Integer userId, String roleName) {
        LocalDate from = CommonUtils.convertStringToDateObject(startDate);
        LocalDate to = CommonUtils.convertStringToDateObject(endDate);

     //   List<Object[]> rows = purchaseOrderRepository.getApprovedPoReport(from, to);
        List<Object[]> rows;
        if ("Indent Creator".equalsIgnoreCase(roleName)) {
            rows = purchaseOrderRepository.getApprovedPoReportByIndentCreator(from, to, userId);
            System.out.println(roleName);
        } else {
            rows = purchaseOrderRepository.getApprovedPoReport(from, to);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return rows.stream().map(row -> {
            ApprovedPoListReportDto dto = new ApprovedPoListReportDto();

            dto.setApprovedDate(CommonUtils.convertDateToString(row[0] != null
                    ? ((Timestamp) row[0]).toLocalDateTime().toLocalDate()
                    : null));
            dto.setPoId((String) row[1]);
            dto.setVendorName((String) row[2]);
            dto.setValue(((BigDecimal) row[3]).doubleValue());
            dto.setTenderId((String) row[4]);
            dto.setProject((String) row[5]);
            dto.setVendorId((String) row[6]);
            dto.setIndentIds((String) row[7]);
            dto.setModeOfProcurement((String) row[8]);

            // Parse JSON array of attributes (column index 9)
            String json = (String) row[9];
            try {
                List<PurchaseOrderAttributesResponseDTO> attrs = mapper.readValue(
                        json,
                        mapper.getTypeFactory().constructCollectionType(
                                List.class,
                                PurchaseOrderAttributesResponseDTO.class
                        )
                );
                dto.setPurchaseOrderAttributes(attrs);
            } catch (Exception e) {
                dto.setPurchaseOrderAttributes(new ArrayList<>());
            }

            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<pendingPoReportDto> getPendingPoReport(String startDate, String endDate, Integer userId, String roleName) {

        LocalDate from = CommonUtils.convertStringToDateObject(startDate);
        LocalDate to = CommonUtils.convertStringToDateObject(endDate);

       // List<Object[]> rows = purchaseOrderRepository.getPendingPoReport(from, to);
        List<Object[]> rows;

        if ("Indent Creator".equalsIgnoreCase(roleName) && userId != null) {
            // Call the new query that filters by userId for indent creation
            rows = purchaseOrderRepository.getPendingPoReportForIndentCreator(from, to, userId);
        } else {
            // Call the normal query for other roles
            rows = purchaseOrderRepository.getPendingPoReport(from, to);
        }



        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return rows.stream().map(row -> {
            pendingPoReportDto dto = new pendingPoReportDto();
            dto.setPoId((String) row[0]);
            dto.setTenderId((String) row[1]);
            dto.setIndentIds((String) row[2]);
            dto.setValue(((BigDecimal) row[3]).doubleValue());
            dto.setVendorName((String) row[4]);

            if (row[5] != null) {
                LocalDate submitted = ((Timestamp) row[5]).toLocalDateTime().toLocalDate();
                dto.setSubmittedDate(CommonUtils.convertDateToString(submitted));
            }
            dto.setPendingWith((String) row[6]);
            if (row[7] != null) {
                LocalDate pending = ((Timestamp) row[7]).toLocalDateTime().toLocalDate();
                dto.setPendingFrom(CommonUtils.convertDateToString(pending));
            }

            dto.setStatus((String) row[8]);
            dto.setAsOnDate(LocalDate.now());

            // parse the JSON array of attributes
            String json = (String) row[9];
            try {
                List<PurchaseOrderAttributesResponseDTO> attrs = mapper.readValue(
                        json,
                        mapper.getTypeFactory().constructCollectionType(
                                List.class,
                                PurchaseOrderAttributesResponseDTO.class
                        )
                );
                dto.setPurchaseOrderAttributes(attrs);
            } catch (Exception e) {
                dto.setPurchaseOrderAttributes(new ArrayList<>());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QuarterlyVigilanceReportDto> getQuarterlyVigilanceReport() {
        LocalDate[] range = CommonUtils.getPreviousQuarterRange();
        LocalDateTime start = range[0].atStartOfDay();
        LocalDateTime end = range[1].atTime(23, 59, 59);

        List<Object[]> results = purchaseOrderRepository.findQuarterlyVigilanceReportDto(start, end);
        List<Object[]> serviceresult = serviceOrderRepository.findQuarterlyVigilanceSoReportDto(start, end);

        List<QuarterlyVigilanceReportDto> orders = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        List<Object[]> combined = new ArrayList<>();
        combined.addAll(results);
        combined.addAll(serviceresult);

        for (Object[] row : combined) {
            QuarterlyVigilanceReportDto dto = new QuarterlyVigilanceReportDto();

            dto.setOrderNo((String) row[0]);

            if (row[1] instanceof Date) {
                dto.setOrderDate(((Date) row[1]).toLocalDate());
            } else if (row[1] instanceof LocalDate) {
                dto.setOrderDate((LocalDate) row[1]);
            }

            dto.setValue(row[2] != null ? ((Number) row[2]).doubleValue() : null);

            try {
                String descriptionsJson = (String) row[3];
                List<PoMaterialReport> descriptions = mapper.readValue(
                        descriptionsJson,
                        mapper.getTypeFactory().constructCollectionType(List.class, PoMaterialReport.class)
                );
                dto.setDescriptions(descriptions);
            } catch (Exception e) {
                dto.setDescriptions(new ArrayList<>());
            }

            dto.setVendorName((String) row[4]);
            dto.setLocation((String) row[5]);

            //  deliveryDate may or may not be present
            if (row.length > 6 && row[6] != null) {
                dto.setDeliveryDate(CommonUtils.convertDateToString(((Date) row[6]).toLocalDate()));
            } else {
                dto.setDeliveryDate(null);
            }

            orders.add(dto);
        }

        return orders;
    }

    @Override
    public List<ShortClosedCancelledOrderReportDto> getShortClosedCancelledOrders(String startDate, String endDate) {

        List<Object[]> poOrder = purchaseOrderRepository.findShortClosedCancelledOrder(
                CommonUtils.convertStringToDateObject(startDate),
                CommonUtils.convertStringToDateObject(endDate)
        );

        List<Object[]> soOrder = serviceOrderRepository.findShortClosedCancelledSoOrders(
                CommonUtils.convertStringToDateObject(startDate),
                CommonUtils.convertStringToDateObject(endDate)
        );


        ObjectMapper mapper = new ObjectMapper();
        List<ShortClosedCancelledOrderReportDto> result = new ArrayList<>();


        processOrders(poOrder, result, mapper);

        processOrders(soOrder, result, mapper);

        return result;
    }


    private void processOrders(List<Object[]> orders, List<ShortClosedCancelledOrderReportDto> result, ObjectMapper mapper) {
        for (Object[] row : orders) {
            ShortClosedCancelledOrderReportDto dto = new ShortClosedCancelledOrderReportDto();

            dto.setPoId((String) row[0]);
            dto.setTenderId((String) row[1]);
            dto.setIndentIds((String) row[2]);
            dto.setValue(row[3] != null ? ((Number) row[3]).doubleValue() : null);
            dto.setVendorName((String) row[4]);

            if (row[5] != null) {
                LocalDate submitted = ((Timestamp) row[5]).toLocalDateTime().toLocalDate();
                dto.setSubmittedDate(CommonUtils.convertDateToString(submitted));
            }

            String materialJson = (String) row[6];
            try {
                List<PoMaterialReport> materials = mapper.readValue(
                        materialJson,
                        mapper.getTypeFactory().constructCollectionType(List.class, PoMaterialReport.class)
                );
                dto.setMaterials(materials);
            } catch (Exception e) {
                dto.setMaterials(new ArrayList<>());
            }

            dto.setReason((String) row[7]);

            result.add(dto);
        }
    }

    @Override
    public List<MonthlyProcurementReportDto> getMonthlyProcurementReport(String startDate, String endDate) {

        List<Object[]> rows = purchaseOrderRepository.getMonthlyProcurementReport(CommonUtils.convertStringToDateObject(startDate),
                CommonUtils.convertStringToDateObject(endDate));
        System.out.println(rows);
        List<MonthlyProcurementReportDto> reports = new ArrayList<>();

        for (Object[] row : rows) {
            MonthlyProcurementReportDto dto = new MonthlyProcurementReportDto();
            dto.setMonth(row[0] != null ? row[0].toString() : "Unknown");
            dto.setPoNumber((String) row[1]);
            dto.setDate(row[2].toString());
            dto.setIndentIds((String) row[3]);
            dto.setValue(row[4] != null ? ((Number) row[4]).doubleValue() : null);
            dto.setVendorName((String) row[5]);

            String mode = (String) row[6];
            String mappedMode = null;
            if (mode != null) {
                mappedMode = switch (mode) {
                    case "GeM" -> "GeM";
                    case "Proprietary/Single Tender" -> "Non-GeM (Proprietary/Single Tender)";
                    case "Limited Pre Approved Vendor Tender" -> "Non-GeM (Limited Pre Approved Vendor Tender)";
                    case "Brand PAC" -> "Non-GeM (Brand PAC)";
                    case "Open Tender" -> "Non-GeM (Open Tender)";
                    case "Global Tender" -> "Non-GeM (Global Tender)";
                    default -> "Other";
                };
            } else {
                dto.setModeOfProcurement(null);
            }

            dto.setModeOfProcurement(mappedMode);

            reports.add(dto);
        }

        return reports;

    }
    public BigDecimal calculateTotalPriceInInr(
            BigDecimal rate,
            BigDecimal exchangeRate,
            String currency,
            BigDecimal quantity,
            BigDecimal gst,
            BigDecimal duties,
            BigDecimal freightCharge
    ) {
        if (rate == null || quantity == null) return BigDecimal.ZERO;

        // Default values if null
        exchangeRate = exchangeRate != null ? exchangeRate : BigDecimal.ONE;
        gst = gst != null ? gst : BigDecimal.ZERO;
        duties = duties != null ? duties : BigDecimal.ZERO;
        freightCharge = freightCharge != null ? freightCharge : BigDecimal.ZERO;

        // Convert rate to INR if not already INR
        BigDecimal baseRate = "INR".equalsIgnoreCase(currency) ? rate : rate.multiply(exchangeRate);

        // Base Amount
        BigDecimal baseAmount = baseRate.multiply(quantity);

        // GST & Duties Amounts
        BigDecimal gstAmount = baseAmount.multiply(gst).divide(BigDecimal.valueOf(100));
        BigDecimal dutiesAmount = baseAmount.multiply(duties).divide(BigDecimal.valueOf(100));

        // Total = Base + GST + Duties + Freight
        return baseAmount.add(gstAmount).add(dutiesAmount).add(freightCharge);
    }

    public List<SearchPOIdDto> searchPOIds(String type, String value) {
        List<SearchPOIdDto> result;

        switch (type.toLowerCase()) {
            case "processid":
                result = purchaseOrderRepository.findByPoIdContainingIgnoreCase(value);
                break;
            case "submitteddate":
                try {
                    LocalDate date = LocalDate.parse(value);
                    LocalDateTime startOfDay = date.atStartOfDay();
                    LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

                    result = purchaseOrderRepository.findByCreatedDateBetween(startOfDay, endOfDay);
                } catch (Exception e) {
                    throw new BusinessException(
                            new ErrorDetails(
                                    AppConstant.ERROR_CODE_RESOURCE,
                                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                    AppConstant.ERROR_TYPE_RESOURCE,
                                    "Invalid submitted date format. Expected yyyy-MM-dd"
                            )
                    );
                }
                break;
            case "materialdescription":
                result = purchaseOrderRepository.findPoIdByMaterialDescription(value);
                break;
            case "vendorname":
                result = purchaseOrderRepository.findPoIdsByVendorName(value);
                break;
           /* case "indentorname":
                result = purchaseOrderRepository.findByIndentorName(value);
                break;*/

            default:
                throw new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Invalid search type: " + type
                        )
                );
        }

        if (result == null || result.isEmpty()) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "No matching POs found for the given search criteria."
                    )
            );
        }

        return result;
    }


 /*   public List<materialHistoryDto> materialHistory(String materialCode) {

        Pageable pageable = PageRequest.of(0, 10);
        Page<materialHistoryDto> history = purchaseOrderRepository.findMaterialHistory(materialCode, pageable);
        return history.getContent();
    }*/

    @Override
    public List<poMaterialHistoryDto> getLatestPurchaseOrders(String materialCode) {
        Pageable limit = PageRequest.of(0, 10); // latest 10 records
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        return purchaseOrderAttributesRepository.findLatestPOByMaterialCode(materialCode, oneYearAgo);
    }


    @Override
    public List<performanceWarrsntySecurityReportDto> getPerformanceSecurityReport(String startDate, String endDate) {
        List<LocalDateTime> range = CommonUtils.getDateRenge(startDate, endDate);
        LocalDateTime start = range.get(0);
        LocalDateTime end = range.get(1);

        List<performanceWarrsntySecurityReportDto> report = purchaseOrderRepository.getPerformanceSecurityAndWarrantyReport(start, end);

        for (performanceWarrsntySecurityReportDto dto : report) {
            String pbgValue = dto.getApplicablePbgToBeSubmitted();
            BigDecimal performanceSecurity = BigDecimal.ZERO;

            if (pbgValue != null && !pbgValue.equalsIgnoreCase("NA")) {
                int pbg = Integer.parseInt(pbgValue);
                if (pbg > 0) {
                    performanceSecurity = dto.getTotalValueOfPo()
                            .multiply(BigDecimal.valueOf(pbg))
                            .divide(BigDecimal.valueOf(100));
                }
            }

            dto.setSecurityAmount(performanceSecurity);
        }

        return report;
    }

    public PoFormateDto getPoFormatDetails(String poId) throws IOException {
        System.out.println("PoId:" +poId);
        PurchaseOrder po = purchaseOrderRepository.findById(poId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Purchase order not found for the provided ID."
                        )
                ));
       TenderRequest tr = tenderRequestRepository.findByTenderId(po.getTenderId())
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Tender not found for the provided ID."
                        )
                ));
     /*   VendorMaster vendor = vendorMasterRepository.findById(po.getVendorId())
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "vendor not found for the vendor ID."
                        )
                ));*/

        //  Map PurchaseOrderAttributes → poFormateMaterial

        List<poFormateMaterial> materialList = po.getPurchaseOrderAttributes().stream()
                .map(attr -> {
                    String uom = materialMasterRepository.findUomByMaterialCode(attr.getMaterialCode());
                    poFormateMaterial dto = new poFormateMaterial();
                    dto.setMaterialDescription(attr.getMaterialDescription());
                    dto.setQuantity(attr.getQuantity());
                    dto.setUom(uom);
                    dto.setCurrency(attr.getCurrency());
                    dto.setUnitPrice(attr.getRate());
                    dto.setGstRate(attr.getGst());

                    // dto.setTotalMaterialPrice(attr.getTotalPoMaterialPriceInInr());
                    return dto;
                }).collect(Collectors.toList());
        // Calculate totals
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;

        for (poFormateMaterial mat : materialList) {
            BigDecimal quantity = mat.getQuantity() != null ? mat.getQuantity() : BigDecimal.ZERO;
            BigDecimal unitPrice = mat.getUnitPrice() != null ? mat.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal gstRate = mat.getGstRate() != null ? mat.getGstRate() : BigDecimal.ZERO;

            BigDecimal materialTotal = quantity.multiply(unitPrice);
          //  BigDecimal materialGst = materialTotal.multiply(gstRate).divide(BigDecimal.valueOf(100));
            BigDecimal materialGst = materialTotal.multiply(gstRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            totalAmount = totalAmount.add(materialTotal.setScale(2, RoundingMode.HALF_UP));
            totalGst = totalGst.add(materialGst);

            //mat.setTotalMaterialPrice(materialTotal);
            mat.setTotalMaterialPrice(materialTotal.setScale(2, RoundingMode.HALF_UP));
        }

       // BigDecimal grandTotal = totalAmount.add(totalGst);
        BigDecimal grandTotal = totalAmount.add(totalGst).setScale(2, RoundingMode.HALF_UP);
        PoFormateDto dto = new PoFormateDto();
        if (po.getVendorId() != null) {
            if (!po.getVendorId().startsWith("GEM")) {
                VendorMaster vendor = vendorMasterRepository.findById(po.getVendorId())
                        .orElseThrow(() -> new BusinessException(
                                new ErrorDetails(
                                        AppConstant.ERROR_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_RESOURCE,
                                        "Vendor not found for the vendor ID: " + po.getVendorId()
                                )
                        ));
                if (vendor != null) {
                    dto.setVendorCode(vendor.getVendorId());
                    dto.setVendorName(vendor.getVendorName());
                    dto.setVendorAddress(vendor.getAddress());
                    dto.setGstin(vendor.getGstNo());
                    dto.setContactNumber(vendor.getContactNo());
                    dto.setEmail(vendor.getEmailAddress());
                }
                // use vendor details
            } else if (po.getVendorId().startsWith("GEM")) {
                GemVendorIdTracker gem = gemVendorIdTrackerRepository.findByGemVendorId(po.getVendorId())
                        .orElseThrow(() -> new BusinessException(
                                new ErrorDetails(
                                        AppConstant.ERROR_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_RESOURCE,
                                        "GEM vendor not found for the vendor ID: " + po.getVendorId()
                                )
                        ));
                if (gem != null) {
                    dto.setVendorCode(po.getVendorId());
                    dto.setVendorName(gem.getVendorName());
                    //  dto.setVendorAddress();
                    //  dto.setGstin(vendor.getGstNo());
                    //   dto.setContactNumber(vendor.getContactNo());
                    dto.setEmail("kudaykiran.9949@gmail.com");  //send to purchase dept mail
                }
                // use vq details
            } else {
                throw new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Invalid vendor ID format: " + po.getVendorId()
                        )
                );
            }
        }
        dto.setPoNumber(po.getPoId());
        LocalDateTime finalApprovedPoDate = workflowTransitionRepository.findLastCreatedDateByRequestId(po.getPoId());
       // dto.setPoDate(LocalDate.from(finalApprovedPoDate));
        dto.setTenderNumber(po.getTenderId());
      //  dto.setTenderDate(LocalDate.from(tr.getCreatedDate()));
        dto.setQuotationNo(po.getQuotationNumber());
     //   dto.setQuotationDate(po.getQuotationDate());
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

        dto.setPoDate(finalApprovedPoDate != null ? finalApprovedPoDate.format(dateFormatter) : null);
        dto.setTenderDate(tr.getCreatedDate() != null ? tr.getCreatedDate().format(dateFormatter) : null);
        dto.setQuotationDate(po.getQuotationDate() != null ? po.getQuotationDate().format(dateFormatter) : null);

        // dto.setDeliveryPeriod(String.valueOf(po.getDeliveryPeriod().setScale(0, RoundingMode.HALF_UP)));
        dto.setDeliveryPeriod(po.getDeliveryPeriod()); //updated by abhinav because of String
        List<String> indentIds = indentIdRepository.findTenderWithIndent(tr.getTenderId());
        List<LocalDateTime> createdDates = indentCreationRepository.findCreatedDatesByIndentIds(indentIds);
        String indentIndss = indentIds.stream().collect(Collectors.joining(", "));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

       /* String createdDatesStr = createdDates.stream()
                .map(LocalDateTime::toLocalDate)
                .map(date -> date.format(formatter)) // format as String
                .collect(Collectors.joining(", "));*/
        String createdDatesStr = createdDates.stream()
                .map(LocalDateTime::toLocalDate)
                .map(date -> date.format(dateFormatter))
                .collect(Collectors.joining(", "));

        dto.setIndentDates(createdDatesStr);


        // List<String> buyBackAmount = indentCreationRepository.findBuyBackAmountsByIndentIds(indentIds);
      //  BigDecimal totalBuyBackAmount = buyBackAmount.stream()
           //     .filter(a -> a != null && !a.isBlank())
            //    .map(BigDecimal::new)
              //  .reduce(BigDecimal.ZERO, BigDecimal::add);
        String consigneeLocation = iiaAddressForConsigneeLocationRepository.findIiaAddressByConsignee(po.getConsignesAddress());
        dto.setConsigneeLocation(consigneeLocation);
        //dto.setBuyBackAmount(po.getBuyBackAmount());


        dto.setIndentIds(indentIndss);
        String forwardDetails = iiaFreightForwarderDetailsRepository
                .findFreightForwarderDetailsByCountryName(po.getTransporterAndFreightForWarderDetails());
        dto.setFreightForwarderDetails(forwardDetails);
        dto.setIndentDates(createdDatesStr);
        dto.setProjectName(po.getProjectName());
        dto.setBudgetCode("");
        dto.setTotalAmount(totalAmount);
        dto.setTotalGst(totalGst);
        BigDecimal buyBackAmount = po.getBuyBackAmount() != null ? po.getBuyBackAmount() : BigDecimal.ZERO;


        BigDecimal grandT = grandTotal.subtract(buyBackAmount);

// Set values in DTO
        dto.setBuyBackAmount(buyBackAmount);
        dto.setGrandTotal(grandT);
        dto.setCurrencyOfMaterial(po.getPurchaseOrderAttributes().get(0).getCurrency());

        dto.setWarranty(po.getWarranty());
        dto.setAdditionalTermsAndConditions(po.getAdditionalTermsAndConditions());
        String pbgValue = po.getApplicablePbgToBeSubmitted();
        BigDecimal performanceSecurity = BigDecimal.ZERO;

        if (pbgValue != null && !pbgValue.equalsIgnoreCase("NA")) {

                int pbg = Integer.parseInt(pbgValue);
                if (pbg > 0) {
                    performanceSecurity = grandTotal
                            .multiply(BigDecimal.valueOf(pbg))
                            .divide(BigDecimal.valueOf(100));
                }
        }
        dto.setPerformanceAndWarrantySecurity(String.valueOf(performanceSecurity.setScale(2, RoundingMode.HALF_UP)));


        dto.setPerformanceAndWarranty(po.getApplicablePbgToBeSubmitted());
       // dto.setWarranty(po.getWarranty());
        dto.setIncoTerms(po.getIncoTerms());
        dto.setPaymentTerms(po.getPaymentTerms());
        Long weeks = ChronoUnit.WEEKS.between(LocalDate.from(finalApprovedPoDate), po.getDeliveryDate());
        dto.setDeliveryPeriodWeeks(weeks);
       // dto.setPerformanceAndWarrantySecurity(po.getApplicablePbgToBeSubmitted());

        dto.setMaterialDetails(materialList);
        BigDecimal totalDuties = po.getPurchaseOrderAttributes().stream()
                .map(attr -> attr.getDuties() != null ? attr.getDuties() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dto.setDuties(totalDuties.setScale(0, RoundingMode.HALF_UP));
        String fileName = officerSignatureRepository.findSignaturePathByDesignation("Store Purchase");
        dto.setOfficerSignatureFileName(fileName);
      //  String base64 = convertImageToBase64(fileName, basePath);
       // dto.setOfficerSignatureBase64(base64);
        if (fileName != null && !fileName.isEmpty()) {
            String base64 = convertImageToBase64(fileName, basePath);
            dto.setOfficerSignatureBase64(base64);
        } else {
            dto.setOfficerSignatureBase64(null);
        }


        return dto;
    }
    public static String convertImageToBase64(String fileName, String basePath) throws IOException {
        String filePath = basePath + File.separator + fileName;
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new IOException("File not found: " + filePath);
        }
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = fis.readAllBytes();
            // Only return base64 string, NO prefix
            return Base64.getEncoder().encodeToString(fileBytes);
        }
    }




}




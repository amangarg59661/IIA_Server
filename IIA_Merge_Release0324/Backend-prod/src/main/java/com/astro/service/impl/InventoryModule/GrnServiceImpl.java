package com.astro.service.impl.InventoryModule;

import com.astro.dto.workflow.InventoryModule.*;
import com.astro.dto.workflow.InventoryModule.GiDto.GiApprovalDto;
import com.astro.dto.workflow.InventoryModule.GiDto.GiWorkflowStatusDto;
import com.astro.dto.workflow.PaymentVoucherPoSearchDto;
import com.astro.entity.PaymentVoucher;
import com.astro.entity.PaymentVoucherMaterials;
import com.astro.entity.ProcurementModule.PurchaseOrderAttributes;
import com.astro.entity.ProcurementModule.ServiceOrder;
import com.astro.repository.InventoryModule.*;
import com.astro.repository.InventoryModule.GiRepository.GiMasterRepository;
import com.astro.repository.InventoryModule.GprnRepository.GprnMasterRepository;
import com.astro.repository.InventoryModule.GprnRepository.GprnMaterialDtlRepository;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderAttributesRepository;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository;
import com.astro.repository.ProcurementModule.ServiceOrderRepository.ServiceOrderRepository;
import com.astro.repository.WorkflowTransitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.Map;

import com.astro.service.InventoryModule.GrnService;
import com.astro.service.InventoryModule.IgpService;
import com.astro.service.InventoryModule.GiService;
import com.astro.repository.InventoryModule.grn.*;
import com.astro.repository.InventoryModule.igp.IgpMasterRepository;
import com.astro.repository.InventoryModule.GiRepository.GiMaterialDtlRepository;
import com.astro.repository.ohq.OhqMasterRepository;
import com.astro.entity.InventoryModule.*;
import com.astro.dto.workflow.InventoryModule.grn.*;
import com.astro.exception.*;
import com.astro.constant.AppConstant;
import com.astro.util.CommonUtils;
import org.modelmapper.ModelMapper;
import org.springframework.util.StringUtils;

@Service
public class GrnServiceImpl implements GrnService {
    @Autowired
    private GrnMasterRepository grnmr;

    @Autowired
    private IgpService igpService;

    @Autowired
    private GrnMaterialDtlRepository grnmdr;

    @Autowired
    private GiService giService;

    @Autowired
    private GiMaterialDtlRepository gimdr;

    @Autowired
    private AssetMasterRepository amr;

    @Autowired
    private OhqMasterRepository ohqmr;

    @Autowired
    private IgpMasterRepository igpMasterRepository;

    @Autowired
    private GoodsInspectionConsumableDetailRepository gicdr;

    @Autowired
    private GrnConsumableDtlRepository gcdr;

    @Autowired
    private OhqMasterConsumableRepository omcr;
    @Autowired
    private GiMasterRepository gimr;
    @Autowired
    private GrnWorkflowStatusRepository grnWorkRepo;
    @Autowired
    private OhqConsumableStoreStockRepository ohqStockrepo;
    @Autowired
    private GprnMasterRepository gprnMasterRepository;
    @Autowired
    private GprnMaterialDtlRepository gprnMaterialDtlRepository;
    @Autowired
    private PurchaseOrderAttributesRepository purchaseOrderAttributesRepo;
    @Autowired
    private PaymentVoucherReposiotry paymentVoucherReposiotry;
    @Autowired
    private PaymentVoucherMaterialsRepository paymentVoucherMaterialsRepository;
    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;
    @Autowired
    private ServiceOrderRepository serviceOrderRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional
    public String saveGrn(GrnDto req) {
        if("MATERIAL_IN".equalsIgnoreCase(req.getGrnType())){
            igpService.validateMaterialIgp(req.getIgpId());
        }
        if ("GI".equalsIgnoreCase(req.getGrnType())) {
          //  giService.validateGiSubProcessId(req.getGiNo());
            giService.validateGiIsApproved(req.getGiNo());
             

        }

        ModelMapper mapper = new ModelMapper();
        GrnMasterEntity grnMaster = new GrnMasterEntity();
        grnMaster.setCustodianId(Integer.valueOf(req.getCustodianId()));

        // Handle dates with null checks
        if (req.getGrnDate() != null && !req.getGrnDate().trim().isEmpty()) {
            grnMaster.setGrnDate(CommonUtils.convertStringToDateObject(req.getGrnDate()));
        }

        grnMaster.setCreatedBy(req.getCreatedBy());
        grnMaster.setSystemCreatedBy(Integer.parseInt(req.getCreatedBy()));
        grnMaster.setCreateDate(LocalDateTime.now());
        grnMaster.setLocationId(req.getLocationId());
        grnMaster.setGrnType(req.getGrnType());
        grnMaster.setStatus("APPROVED");
        if(!req.getGrnType().equalsIgnoreCase("MATERIAL_IN")){
            // grnMaster.setGrnProcessId(req.getGiNo().split("/")[0].substring(3));
            grnMaster.setGrnProcessId(extractProcessId(req.getGiNo()));
        }

        if ("GI".equalsIgnoreCase(req.getGrnType())) {
            if (req.getInstallationDate() != null && !req.getInstallationDate().trim().isEmpty()) {
                grnMaster.setInstallationDate(CommonUtils.convertStringToDateObject(req.getInstallationDate()));
            }
            if (req.getCommissioningDate() != null && !req.getCommissioningDate().trim().isEmpty()) {
                grnMaster.setCommissioningDate(CommonUtils.convertStringToDateObject(req.getCommissioningDate()));
            }
            grnMaster.setGiProcessId(extractProcessId(req.getGiNo()));
            // grnMaster.setGiProcessId(req.getGiNo().split("/")[0].substring(3));
            // grnMaster.setGiSubProcessId(Integer.parseInt(req.getGiNo().split("/")[1]));
            grnMaster.setGiSubProcessId(extractSubProcessId(req.getGiNo()));
        } else {
            // grnMaster.setIgpProcessId(req.getGiNo().split("/")[0].substring(3));
            grnMaster.setIgpProcessId(req.getIgpId());

        }

        GiMasterEntity giMaster = gimr.findById(
            extractSubProcessId(req.getGiNo())).orElse(null);
            // Integer.parseInt(req.getGiNo().split("/")[1])).orElse(null);
    if (giMaster != null) {
        GprnMasterEntity gprnEntity = gprnMasterRepository
                .findBySubProcessId(giMaster.getGprnSubProcessId());
        if (gprnEntity != null && gprnEntity.getPoId() != null) {
            String consigneeName = resolveConsigneeName(
                    gprnEntity.getPoId(), req.getConsigneeName());
            grnMaster.setConsigneeName(consigneeName);
        }
    }

        grnMaster = grnmr.save(grnMaster);

        List<GrnMaterialDtlEntity> grnMaterialDtlList = new ArrayList<>();
        List<GrnConsumableDtlEntity> gcdeList = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();
        Boolean errorFound = false;

        if ("GI".equalsIgnoreCase(req.getGrnType())) {
            // GI validation logic
            List<GiMaterialDtlEntity> giMaterialList = gimdr.findByInspectionSubProcessId(
                    // Integer.parseInt(req.getGiNo().split("/")[1]));
                    extractSubProcessId(req.getGiNo()));
            List<GoodsInspectionConsumableDetailEntity> giConsumableList = gicdr.findByInspectionSubProcessId(
                extractSubProcessId(req.getGiNo())
                // Integer.parseInt(req.getGiNo().split("/")[1])
                );

            for (GrnMaterialDtlDto materialDtl : req.getMaterialDtlList()) {
                if(Objects.nonNull(materialDtl.getAssetId())){


                Optional<GiMaterialDtlEntity> giMaterial = giMaterialList.stream()
                        .filter(gi -> gi.getAssetId().equals(materialDtl.getAssetId()))
                        .findFirst();

                if (giMaterial.isEmpty()) {
                    errorMessage.append("Asset ID " + materialDtl.getAssetId() + " not found in GI. ");
                    errorFound = true;
                    continue;
                }

                BigDecimal previouslyReceivedQty = grnmdr.findByGiSubProcessIdAndAssetId(
                        // Integer.parseInt(req.getGiNo().split("/")[1]),
                        extractSubProcessId(req.getGiNo()),
                        materialDtl.getAssetId())
                        .stream()
                        .map(GrnMaterialDtlEntity::getQuantity)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalReceivedQty = previouslyReceivedQty.add(materialDtl.getAcceptedQuantity());

                if (totalReceivedQty.compareTo(giMaterial.get().getAcceptedQuantity()) > 0) {
                    errorMessage.append("Total received quantity for Asset ID " + materialDtl.getAssetId() +
                            " exceeds accepted quantity in GI. ");
                    errorFound = true;
                    continue;
                }

                GrnMaterialDtlEntity grnMaterialDtl = new GrnMaterialDtlEntity();
                mapper.map(materialDtl, grnMaterialDtl);
                grnMaterialDtl.setQuantity(materialDtl.getAcceptedQuantity());
                grnMaterialDtl.setGrnProcessId(grnMaster.getGrnProcessId());
                // grnMaterialDtl.setGiSubProcessId(Integer.parseInt(req.getGiNo().split("/")[1]));
                grnMaterialDtl.setGiSubProcessId(extractSubProcessId(req.getGiNo()));
                grnMaterialDtl.setGrnSubProcessId(grnMaster.getGrnSubProcessId());
                grnMaterialDtl.setAssetCode(materialDtl.getAssetCode());
                grnMaterialDtlList.add(grnMaterialDtl);

                grnMaster = grnmr.save(grnMaster);

// Build GRN number once here, after save (so subProcessId is available)
String grnNumber = "INV" + grnMaster.getGrnProcessId() + "/" + grnMaster.getGrnSubProcessId();
                updateAssetAndOhq(materialDtl, req.getCustodianId() , grnNumber);
            }

            else {

                    System.out.println("NOT ASSET ID");
                    Optional<GoodsInspectionConsumableDetailEntity> giConsumable = giConsumableList.stream()
                            .filter(consumable -> consumable.getMaterialCode().equals(materialDtl.getMaterialCode()))
                            .findFirst();
                    if (giConsumable.isEmpty()) {
                        errorMessage.append("Material Code " + materialDtl.getMaterialCode() + " not found in GI. ");
                        errorFound = true;
                        continue;
                    }
                    BigDecimal prevRecQuant = gcdr.findByGiSubProcessIdAndMaterialCode(
                        extractSubProcessId(req.getGiNo()),
                                    // Integer.parseInt(req.getGiNo().split("/")[1]),
                                    materialDtl.getMaterialCode())
                            .stream()
                            .map(GrnConsumableDtlEntity::getQuantity)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal totRecQuant = prevRecQuant.add(materialDtl.getAcceptedQuantity());

                    if (totRecQuant.compareTo(giConsumable.get().getAcceptedQuantity()) > 0) {
                        errorMessage.append("Total received quantity for Asset ID " + materialDtl.getAssetId() +
                                " exceeds accepted quantity in GI. ");
                        errorFound = true;
                        continue;
                    }

                    GrnConsumableDtlEntity gcde = new GrnConsumableDtlEntity();
                    mapper.map(materialDtl, gcde);  // Change from mapping giConsumable to mapping materialDtl
                    gcde.setQuantity(materialDtl.getAcceptedQuantity());
                    gcde.setGrnProcessId(grnMaster.getGrnProcessId());
                    gcde.setGiSubProcessId(extractSubProcessId(req.getGiNo()));
                    // gcde.setGiSubProcessId(Integer.parseInt(req.getGiNo().split("/")[1]));
                    gcde.setGrnSubProcessId(grnMaster.getGrnSubProcessId());
                    gcde.setBookValue(materialDtl.getBookValue());         // Set book value
                    gcde.setDepriciationRate(materialDtl.getDepriciationRate()); // Set depreciation rate
                    gcdeList.add(gcde);

                    System.out.println("ADDED TO LIST");
                    if (Boolean.TRUE.equals(req.getStoresStock())) {
                        //if stores stock is true then saveing in store stock ohq other wise consumableohq
                       OhqConsumableStoreStockEntity storeStock = new OhqConsumableStoreStockEntity();
                        storeStock.setMaterialCode(materialDtl.getMaterialCode());
                        storeStock.setLocatorId(materialDtl.getLocatorId());
                        storeStock.setQuantity(materialDtl.getAcceptedQuantity());

                        storeStock.setBookValue(materialDtl.getBookValue());
                        storeStock.setDepriciationRate(materialDtl.getDepriciationRate());
                        storeStock.setCustodianId(req.getCustodianId());
                        storeStock.setUom(materialDtl.getUomId());
                        storeStock.setCreateDate(LocalDateTime.now());
                        ohqStockrepo.save(storeStock);
                        System.out.println("STORE STOCK ENTRY SAVED");
                    } else {
                        // UPDATE FUNC FOR CONSUMABLE OHQ
                        Optional<OhqMasterConsumableEntity> existingOhq = omcr.findByMaterialCodeAndLocatorIdAndCustodianId(
                                materialDtl.getMaterialCode(),
                                materialDtl.getLocatorId(),
                                req.getCustodianId());

                        OhqMasterConsumableEntity ohq;
                        if (existingOhq.isPresent()) {
                            ohq = existingOhq.get();
                            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
                            ohq.setQuantity(currentQty.add(materialDtl.getAcceptedQuantity()));
                        } else {
                            ohq = new OhqMasterConsumableEntity();
                            ohq.setCustodianId(req.getCustodianId());
                            ohq.setMaterialCode(materialDtl.getMaterialCode());
                            ohq.setLocatorId(materialDtl.getLocatorId());
                            ohq.setQuantity(materialDtl.getAcceptedQuantity());
                            System.out.println("INSIDE ELSE ABOVE BV");
                            ohq.setBookValue(materialDtl.getBookValue());
                            System.out.println("INSIDE ELSE BELOW BV");
                            ohq.setDepriciationRate(materialDtl.getDepriciationRate());
                            ohq.setUnitPrice(materialDtl.getBookValue());
                        }
                        System.out.println("BEFORE OMCR SAVING");
                        omcr.save(ohq);
                        System.out.println("AFTER OMCR SAVING");
                    }
                }
            }
        } else {
            // IGP validation logic - skip GI-specific validations
            for (GrnMaterialDtlDto materialDtl : req.getMaterialDtlList()) {
                if(Objects.isNull(materialDtl.getAssetId())){
                    GrnConsumableDtlEntity grnConsumableDtl = new GrnConsumableDtlEntity();
                    
                }
                GrnMaterialDtlEntity grnMaterialDtl = new GrnMaterialDtlEntity();
                mapper.map(materialDtl, grnMaterialDtl);
                grnMaterialDtl.setQuantity(materialDtl.getAcceptedQuantity());
                grnMaterialDtl.setGrnProcessId(grnMaster.getGrnProcessId());
                grnMaterialDtl.setGrnSubProcessId(grnMaster.getGrnSubProcessId());
                // grnMaterialDtl.setIgpSubProcessId(Integer.parseInt(req.getIgpId().split("/")[1]));
                grnMaterialDtl.setIgpSubProcessId(extractSubProcessId(req.getIgpId()));


                // Copy financial values from existing OHQ if available
                Optional<OhqMasterEntity> existingOhq = ohqmr.findByAssetIdAndLocatorIdAndCustodianId(
                    materialDtl.getAssetId(),
                    materialDtl.getLocatorId(),
                    req.getCustodianId());

                if (existingOhq.isPresent()) {
                    OhqMasterEntity ohq = existingOhq.get();
                    grnMaterialDtl.setBookValue(ohq.getBookValue());
                    grnMaterialDtl.setDepriciationRate(ohq.getDepriciationRate());
                } else {
                    // If no existing OHQ, use values from materialDtl
                    grnMaterialDtl.setBookValue(materialDtl.getBookValue());
                    grnMaterialDtl.setDepriciationRate(materialDtl.getDepriciationRate());
                }

                grnMaterialDtlList.add(grnMaterialDtl);
                grnMaster = grnmr.save(grnMaster);

// Build GRN number once here, after save (so subProcessId is available)
String grnNumber = "INV" + grnMaster.getGrnProcessId() + "/" + grnMaster.getGrnSubProcessId();
                updateAssetAndOhq(materialDtl, req.getCustodianId(), grnNumber);
            }
        }

        if (errorFound) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    errorMessage.toString()));
        }

        grnmdr.saveAll(grnMaterialDtlList);
        System.out.println("BEFORE GCDR SAVING");
        gcdr.saveAll(gcdeList);
        System.out.println("AFTER GCDR SAVING");

        GrnWorkflowStatus workflowStatus = new GrnWorkflowStatus();
        workflowStatus.setProcessId("INV" + grnMaster.getGrnProcessId());
        workflowStatus.setSubProcessId(grnMaster.getGrnSubProcessId());
        workflowStatus.setAction("CREATED");
        workflowStatus.setRemarks("GRN Created");
        workflowStatus.setCreatedBy(Integer.parseInt(req.getCreatedBy()));
        workflowStatus.setCreateDate(LocalDateTime.now());

        grnWorkRepo.save(workflowStatus);

        return "INV" + grnMaster.getGrnProcessId() + "/" + grnMaster.getGrnSubProcessId();
    }

private String extractProcessId(String processNo) {
    int lastSlash = processNo.lastIndexOf("/");
    return processNo.substring(3, lastSlash);
}

private int extractSubProcessId(String processNo) {
    return Integer.parseInt(processNo.substring(processNo.lastIndexOf("/") + 1));
}

    private void updateAssetAndOhq(GrnMaterialDtlDto materialDtl, String custodianId, String grnNumber) {
        System.out.println("UPDATE CALLED");
        AssetMasterEntity asset = amr.findById(materialDtl.getAssetId())
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Asset not found with ID: " + materialDtl.getAssetId())));

        if (asset.getInitQuantity() == null || asset.getInitQuantity().compareTo(BigDecimal.ZERO) == 0) {
            asset.setInitQuantity(materialDtl.getAcceptedQuantity());
            asset.setGrnNumber(grnNumber);
            amr.save(asset);
        }

        List<OhqMasterEntity> ohqList = ohqmr.findByAssetId(asset.getAssetId());

        Optional<OhqMasterEntity> existingOhq = ohqmr.findByAssetIdAndLocatorIdAndCustodianId(
                materialDtl.getAssetId(),
                materialDtl.getLocatorId(),
                custodianId);

        System.out.println("EXISTUNGOHQ CALLED");

        OhqMasterEntity ohq;
        if (existingOhq.isPresent()) {
            System.out.println("EXISTING OHQ PRESENT");
            ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.add(materialDtl.getAcceptedQuantity()));
        } else {
            System.out.println("EXISTING OHQ NOT PRESENT");
            ohq = new OhqMasterEntity();
            ohq.setCustodianId(custodianId);
            ohq.setAssetId(materialDtl.getAssetId());
            ohq.setAssetCode(materialDtl.getAssetCode());
            ohq.setLocatorId(materialDtl.getLocatorId());
            ohq.setQuantity(materialDtl.getAcceptedQuantity());

            if (!ohqList.isEmpty()) {
                System.out.println("NOT EMPTY");
                OhqMasterEntity existingOhqRecord = ohqList.get(0);
                ohq.setBookValue(existingOhqRecord.getBookValue());
                ohq.setDepriciationRate(existingOhqRecord.getDepriciationRate());
                ohq.setUnitPrice(existingOhqRecord.getUnitPrice());
            } else {
                System.out.println("EMTOTY");
                ohq.setBookValue(materialDtl.getBookValue());
                ohq.setDepriciationRate(materialDtl.getDepriciationRate());
                ohq.setUnitPrice(materialDtl.getBookValue());
            }
        }
        ohqmr.save(ohq);
    }

    @Override
    public Map<String, Object> getGrnDtls(String processNo) {
        ModelMapper mapper = new ModelMapper();
        String[] processNoSplit = processNo.split("/");

        // if (processNoSplit.length != 2) {
        //     throw new InvalidInputException(new ErrorDetails(
        //             AppConstant.USER_INVALID_INPUT,
        //             AppConstant.ERROR_TYPE_CODE_VALIDATION,
        //             AppConstant.ERROR_TYPE_VALIDATION,
        //             "Invalid process ID"));
        // }

         Integer grnSubProcessId ;
        if (3 == processNoSplit.length ){
             grnSubProcessId = Integer.parseInt(processNoSplit[2]);
        }else if(2 == processNoSplit.length) {
             grnSubProcessId = Integer.parseInt(processNoSplit[1]);
        }else {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process ID"));
        }

        // Integer grnSubProcessId = Integer.parseInt(processNoSplit[1]);

        GrnMasterEntity grnMaster = grnmr.findById(grnSubProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "GRN not found for the provided process ID.")));

        List<GrnMaterialDtlEntity> grnMaterialList = grnmdr.findByGrnSubProcessId(grnMaster.getGrnSubProcessId());

       /* List<GrnMaterialDtlDto> materialDtlListRes = grnMaterialList.stream()
                .map(material -> mapper.map(material, GrnMaterialDtlDto.class))
                .collect(Collectors.toList());*/
        List<GrnMaterialDtlDto> materialDtlListRes;
        if (grnMaterialList.isEmpty()) {
            // If no materials, fetch from consumable details
            List<GrnConsumableDtlEntity> consumableList = gcdr.findByGrnSubProcessId(grnSubProcessId);
            materialDtlListRes = consumableList.stream()
                    .map(consumable -> {
                        GrnMaterialDtlDto dto = new GrnMaterialDtlDto();
                        dto.setMaterialCode(consumable.getMaterialCode());
                        dto.setReceivedQuantity(consumable.getQuantity());
                        dto.setAcceptedQuantity(consumable.getQuantity()); // default accepted
                        dto.setLocatorId(consumable.getLocatorId());
                        dto.setBookValue(consumable.getBookValue());
                        dto.setDepriciationRate(consumable.getDepriciationRate());
                        return dto;
                    })
                    .collect(Collectors.toList());
        } else {
            materialDtlListRes = grnMaterialList.stream()
                    .map(material -> mapper.map(material, GrnMaterialDtlDto.class))
                    .collect(Collectors.toList());
        }

        GrnDto grnRes = new GrnDto();
        grnRes.setGrnNo(processNo);
        grnRes.setGiNo("INV" + grnMaster.getGiProcessId() + "/" + grnMaster.getGiSubProcessId());
        grnRes.setGrnDate(CommonUtils.convertDateToString(grnMaster.getGrnDate()));
        grnRes.setInstallationDate(CommonUtils.convertDateToString(grnMaster.getInstallationDate()));
        grnRes.setCommissioningDate(CommonUtils.convertDateToString(grnMaster.getCommissioningDate()));
        grnRes.setCreatedBy(grnMaster.getCreatedBy());
        grnRes.setSystemCreatedBy(grnMaster.getSystemCreatedBy());
        grnRes.setLocationId(grnMaster.getLocationId());
        grnRes.setCustodianId(String.valueOf(grnMaster.getCustodianId()));
        grnRes.setMaterialDtlList(materialDtlListRes);

        Map<String, Object> giDetails = giService.getGiDtls("INV" + grnMaster.getGiProcessId() + "/" +
                grnMaster.getGiSubProcessId());

        Map<String, Object> combinedRes = new HashMap<>();
        combinedRes.put("grnDtls", grnRes);
        combinedRes.put("giDtls", giDetails.get("giDtls"));
        combinedRes.put("gprnDtls", giDetails.get("gprnDtls"));

        return combinedRes;
    }

    // private void validateIgp(String processNo) {
    //     String[] processNoSplit = processNo.split("/");
    //     if (processNoSplit.length != 2 || !processNoSplit[0].startsWith("INV")) {
    //         throw new InvalidInputException(new ErrorDetails(
    //                 AppConstant.USER_INVALID_INPUT,
    //                 AppConstant.ERROR_TYPE_CODE_VALIDATION,
    //                 AppConstant.ERROR_TYPE_VALIDATION,
    //                 "Invalid IGP No. Format should be IGP{number}/{number}"));
    //     }

    //     try {
    //         Integer subProcessId = Integer.parseInt(processNoSplit[1]);
    //         if (!igpMasterRepository.existsById(subProcessId)) {
    //             throw new BusinessException(new ErrorDetails(
    //                     AppConstant.ERROR_CODE_RESOURCE,
    //                     AppConstant.ERROR_TYPE_CODE_RESOURCE,
    //                     AppConstant.ERROR_TYPE_RESOURCE,
    //                     "IGP not found with the provided ID"));
    //         }
    //     } catch (NumberFormatException e) {
    //         throw new InvalidInputException(new ErrorDetails(
    //                 AppConstant.USER_INVALID_INPUT,
    //                 AppConstant.ERROR_TYPE_CODE_VALIDATION,
    //                 AppConstant.ERROR_TYPE_VALIDATION,
    //                 "Invalid IGP number format"));
    //     }
    // }

    @Override
    @Transactional
    public void approveGrn(GiApprovalDto req) {
        updateGrnStatusAndRemarks(req);
    }

    @Override
    @Transactional
    public void rejectGrn(GiApprovalDto req) {
        updateGrnStatusAndRemarks(req);
    }

    @Override
    @Transactional
    public void changeReqGrn(GiApprovalDto req) {
        updateGrnStatusAndRemarks(req);
    }

    private void updateGrnStatusAndRemarks(GiApprovalDto req) {
        String[] processNoSplit = req.getProcessNo().split("/");
        // if (processNoSplit.length != 2) {
         if (processNoSplit.length < 2) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid process number format"));
        }
        Integer grnSubProcessId = extractSubProcessId(req.getProcessNo());
        // Integer grnSubProcessId = Integer.parseInt(processNoSplit[1]);
        GrnMasterEntity grnMaster = grnmr.findById(grnSubProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "GRN not found")));

        grnMaster.setStatus(req.getStatus());
        grnmr.save(grnMaster);

        GrnWorkflowStatus history = new GrnWorkflowStatus();
        // history.setProcessId(processNoSplit[0]);
        history.setProcessId("INV" + extractProcessId(req.getProcessNo()));
        history.setSubProcessId(grnSubProcessId);
        history.setAction(req.getStatus());
        history.setRemarks(req.getRemarks());
        history.setCreatedBy(req.getCreatedBy());
        history.setCreateDate(LocalDateTime.now());

        grnWorkRepo.save(history);
    }


    public List<GrnMasterEntity> getGrnByStatuses() {
        List<String> statuses = Arrays.asList("AWAITING APPROVAL");
        return grnmr.findByStatusIn(statuses);
    }
    public List<GrnMasterEntity> getGrnByStorePresonStatuses() {
        List<String> statuses = Arrays.asList("REJECTED", "CHANGE REQUEST");
        return grnmr.findByStatusIn(statuses);
    }
/*
    @Override
    @Transactional
    public String updateGrn(GrnDto req) {
        String[] processNoSplit = req.getGrnNo().split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid GRN No format"));
        }

        Integer subProcessId = Integer.parseInt(processNoSplit[1]);
        GrnMasterEntity grnMaster = grnmr.findByGrnSubProcessId(subProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "GRN not found for given GRN No.")));

        ModelMapper mapper = new ModelMapper();
        mapper.map(req, grnMaster);
        grnMaster.setStatus("AWAITING APPROVAL");
        grnMaster.setCreatedBy(req.getCreatedBy());
        grnMaster.setSystemCreatedBy(Integer.parseInt(req.getCreatedBy()));

        if (StringUtils.hasText(req.getGrnDate())) {
            grnMaster.setGrnDate(CommonUtils.convertStringToDateObject(req.getGrnDate()));
        }
        if (StringUtils.hasText(req.getInstallationDate())) {
            grnMaster.setInstallationDate(CommonUtils.convertStringToDateObject(req.getInstallationDate()));
        }
        if (StringUtils.hasText(req.getCommissioningDate())) {
            grnMaster.setCommissioningDate(CommonUtils.convertStringToDateObject(req.getCommissioningDate()));
        }
        grnMaster.setCreateDate(LocalDateTime.now());
        grnmr.save(grnMaster);

        List<GrnMaterialDtlEntity> existingMaterialList = grnmdr.findByGrnSubProcessId(subProcessId);
        List<GrnConsumableDtlEntity> existingConsumableList = gcdr.findByGrnSubProcessId(subProcessId);

        for (GrnMaterialDtlDto materialDtl : req.getMaterialDtlList()) {
            if (Objects.nonNull(materialDtl.getAssetId())) {
                existingMaterialList.stream()
                        .filter(existing -> existing.getAssetId().equals(materialDtl.getAssetId()))
                        .findFirst()
                        .ifPresent(existing -> {
                            BigDecimal oldQty = existing.getQuantity();
                            existing.setQuantity(materialDtl.getAcceptedQuantity());
                            existing.setBookValue(materialDtl.getBookValue());
                            existing.setDepriciationRate(materialDtl.getDepriciationRate());

                            ohqmr.findByAssetIdAndLocatorId(existing.getAssetId(), existing.getLocatorId())
                                    .ifPresent(ohq -> {
                                        BigDecimal diff = materialDtl.getAcceptedQuantity().subtract(oldQty);
                                        ohq.setQuantity(ohq.getQuantity().add(diff));
                                        ohqmr.save(ohq);
                                    });
                        });
            } else {
                existingConsumableList.stream()
                        .filter(existing -> existing.getMaterialCode().equals(materialDtl.getMaterialCode()))
                        .findFirst()
                        .ifPresent(existing -> {
                            BigDecimal oldQty = existing.getQuantity();
                            existing.setQuantity(materialDtl.getAcceptedQuantity());
                            existing.setBookValue(materialDtl.getBookValue());
                            existing.setDepriciationRate(materialDtl.getDepriciationRate());

                            omcr.findByMaterialCodeAndLocatorId(existing.getMaterialCode(), existing.getLocatorId())
                                    .ifPresent(ohq -> {
                                        BigDecimal diff = materialDtl.getAcceptedQuantity().subtract(oldQty);
                                        ohq.setQuantity(ohq.getQuantity().add(diff));
                                        ohq.setBookValue(materialDtl.getBookValue());
                                        ohq.setDepriciationRate(materialDtl.getDepriciationRate());
                                        ohq.setUnitPrice(materialDtl.getBookValue());
                                        omcr.save(ohq);
                                    });
                        });
            }
        }

        grnmdr.saveAll(existingMaterialList);
        gcdr.saveAll(existingConsumableList);

        GrnWorkflowStatus workflowStatus = new GrnWorkflowStatus();
        workflowStatus.setProcessId("INV" + grnMaster.getGrnProcessId());
        workflowStatus.setSubProcessId(grnMaster.getGrnSubProcessId());
        workflowStatus.setAction("Updated");
        workflowStatus.setRemarks("GRN updated");
        workflowStatus.setCreatedBy(Integer.parseInt(req.getCreatedBy()));
        workflowStatus.setCreateDate(LocalDateTime.now());

        grnWorkRepo.save(workflowStatus);

        return "INV" + grnMaster.getGrnProcessId() + "/" + grnMaster.getGrnSubProcessId();
    }*/

    @Override
    @Transactional
    public String updateGrn(GrnDto req) {
        String[] processNoSplit = req.getGrnNo().split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid GRN No format"));
        }

        Integer subProcessId = Integer.parseInt(processNoSplit[1]);
        GrnMasterEntity grnMaster = grnmr.findByGrnSubProcessId(subProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "GRN not found for given GRN No.")));

        ModelMapper mapper = new ModelMapper();
        mapper.map(req, grnMaster);
        grnMaster.setStatus("AWAITING APPROVAL");
        grnMaster.setCreatedBy(req.getCreatedBy());
        grnMaster.setSystemCreatedBy(Integer.parseInt(req.getCreatedBy()));
        if (StringUtils.hasText(req.getGrnDate())) {
            grnMaster.setGrnDate(CommonUtils.convertStringToDateObject(req.getGrnDate()));
        }
        if (StringUtils.hasText(req.getInstallationDate())) {
            grnMaster.setInstallationDate(CommonUtils.convertStringToDateObject(req.getInstallationDate()));
        }
        if (StringUtils.hasText(req.getCommissioningDate())) {
            grnMaster.setCommissioningDate(CommonUtils.convertStringToDateObject(req.getCommissioningDate()));
        }
        grnMaster.setCreateDate(LocalDateTime.now());
        grnmr.save(grnMaster);

        List<GrnMaterialDtlEntity> existingMaterialList = grnmdr.findByGrnSubProcessId(subProcessId);
        List<GrnConsumableDtlEntity> existingConsumableList = gcdr.findByGrnSubProcessId(subProcessId);

        for (GrnMaterialDtlDto materialDtl : req.getMaterialDtlList()) {
            if (Objects.nonNull(materialDtl.getAssetId())) {
                existingMaterialList.stream()
                        .filter(existing -> existing.getAssetId().equals(materialDtl.getAssetId()))
                        .findFirst()
                        .ifPresent(existing -> {

                            // Store old values first
                            BigDecimal oldQty = existing.getQuantity();
                            Integer oldLocatorId = existing.getLocatorId();

                            // Reduce from old OHQ (old locator)
                            ohqmr.findByAssetIdAndLocatorIdAndCustodianId(existing.getAssetId(), oldLocatorId, req.getCustodianId())
                                    .ifPresent(ohq -> {
                                        ohq.setQuantity(ohq.getQuantity().subtract(oldQty));
                                        ohqmr.save(ohq);
                                    });

                            // Update material details AFTER reducing OHQ
                            existing.setLocatorId(materialDtl.getLocatorId());
                            existing.setQuantity(materialDtl.getAcceptedQuantity());
                            existing.setBookValue(materialDtl.getBookValue());
                            existing.setDepriciationRate(materialDtl.getDepriciationRate());

                            // Update OHQ for new locator
                            ohqmr.findByAssetIdAndLocatorIdAndCustodianId(existing.getAssetId(), materialDtl.getLocatorId(), req.getCustodianId())
                                    .ifPresentOrElse(ohq -> {
                                        ohq.setQuantity(ohq.getQuantity().add(materialDtl.getAcceptedQuantity()));
                                        ohq.setBookValue(materialDtl.getBookValue());
                                        ohq.setDepriciationRate(materialDtl.getDepriciationRate());
                                        ohq.setUnitPrice(materialDtl.getBookValue());
                                        ohqmr.save(ohq);
                                    }, () -> {
                                        OhqMasterEntity ohq = new OhqMasterEntity();
                                        ohq.setAssetId(existing.getAssetId());
                                        ohq.setLocatorId(materialDtl.getLocatorId());
                                        ohq.setQuantity(materialDtl.getAcceptedQuantity());
                                        ohq.setBookValue(materialDtl.getBookValue());
                                        ohq.setDepriciationRate(materialDtl.getDepriciationRate());
                                        ohq.setUnitPrice(materialDtl.getBookValue());
                                        ohqmr.save(ohq);
                                    });
                        });
            } else {
                existingConsumableList.stream()
                        .filter(existing -> existing.getMaterialCode().equals(materialDtl.getMaterialCode()))
                        .findFirst()
                        .ifPresent(existing -> {

                            // Store old values first
                            BigDecimal oldQty = existing.getQuantity();
                            Integer oldLocatorId = existing.getLocatorId();

                            // Reduce from old OHQ (old locator)
                            omcr.findByMaterialCodeAndLocatorIdAndCustodianId(existing.getMaterialCode(), oldLocatorId, req.getCustodianId())
                                    .ifPresent(ohq -> {
                                        ohq.setQuantity(ohq.getQuantity().subtract(oldQty));
                                        omcr.save(ohq);
                                    });

                            // Update consumable details AFTER reducing OHQ
                            existing.setLocatorId(materialDtl.getLocatorId());
                            existing.setQuantity(materialDtl.getAcceptedQuantity());
                            existing.setBookValue(materialDtl.getBookValue());
                            existing.setDepriciationRate(materialDtl.getDepriciationRate());

                            // Update OHQ for new locator
                            omcr.findByMaterialCodeAndLocatorIdAndCustodianId(existing.getMaterialCode(), materialDtl.getLocatorId(), req.getCustodianId()) 
                                    .ifPresentOrElse(ohq -> {
                                        ohq.setQuantity(ohq.getQuantity().add(materialDtl.getAcceptedQuantity()));
                                        ohq.setBookValue(materialDtl.getBookValue());
                                        ohq.setDepriciationRate(materialDtl.getDepriciationRate());
                                        ohq.setUnitPrice(materialDtl.getBookValue());
                                        omcr.save(ohq);
                                    }, () -> {
                                        OhqMasterConsumableEntity ohq = new OhqMasterConsumableEntity();
                                        ohq.setMaterialCode(existing.getMaterialCode());
                                        ohq.setLocatorId(materialDtl.getLocatorId());
                                        ohq.setQuantity(materialDtl.getAcceptedQuantity());
                                        ohq.setBookValue(materialDtl.getBookValue());
                                        ohq.setDepriciationRate(materialDtl.getDepriciationRate());
                                        ohq.setUnitPrice(materialDtl.getBookValue());
                                        omcr.save(ohq);
                                    });
                        });
            }
        }

        grnmdr.saveAll(existingMaterialList);
        gcdr.saveAll(existingConsumableList);

        GrnWorkflowStatus workflowStatus = new GrnWorkflowStatus();
        workflowStatus.setProcessId("INV" + grnMaster.getGrnProcessId());
        workflowStatus.setSubProcessId(grnMaster.getGrnSubProcessId());
        workflowStatus.setAction("Updated");
        workflowStatus.setRemarks("GRN updated");
        workflowStatus.setCreatedBy(Integer.parseInt(req.getCreatedBy()));
        workflowStatus.setCreateDate(LocalDateTime.now());
        grnWorkRepo.save(workflowStatus);

        return "INV" + grnMaster.getGrnProcessId() + "/" + grnMaster.getGrnSubProcessId();
    }

    @Override
    public List<GiWorkflowStatusDto> getGrnHistoryByProcessId(String processId, Integer subProcessId) {
        List<GrnWorkflowStatus> historyList = grnWorkRepo.findByProcessIdAndSubProcessIdOrderByIdAsc(processId, subProcessId);
        return historyList.stream().map(status -> {
            GiWorkflowStatusDto dto = new GiWorkflowStatusDto();
            dto.setProcessId(status.getProcessId());
            dto.setSubProcessId(status.getSubProcessId());
            dto.setAction(status.getAction());
            dto.setRemarks(status.getRemarks());
            dto.setCreatedBy(status.getCreatedBy());
            dto.setCreateDate(status.getCreateDate());
            return dto;
        }).toList();
    }



    @Override
    @Transactional
    public String saveMaterialGrn(GrnMaterialMasterDto req){
        igpService.validateMaterialIgp(req.getIgpId());

        GrnMasterEntity grnMaster = new GrnMasterEntity();
        grnMaster.setIgpProcessId(req.getIgpId().split("/")[1]);
        grnMaster.setGrnDate(CommonUtils.convertStringToDateObject(req.getGrnDate()));
        grnMaster.setGrnType(req.getGrnType());
        grnMaster.setStatus("APPROVED");
        grnMaster.setSystemCreatedBy(req.getCreatedBy());
        grnMaster.setLocationId(req.getLocationId());
        grnMaster.setCreatedBy(req.getCreatedBy().toString());
        grnMaster.setCreateDate(LocalDateTime.now());
        grnMaster.setGrnProcessId("N/A");

        grnMaster = grnmr.save(grnMaster);
        
        ModelMapper mapper = new ModelMapper();
        List<GrnMaterialDtlEntity> grnMaterialDtlList = new ArrayList<>();
        List<GrnConsumableDtlEntity> gcdeList = new ArrayList<>();

        for(GrnMaterialInDtlDto materialDtl : req.getMaterialDtlList()){
            if(materialDtl.getAssetId() == null){
                GrnConsumableDtlEntity gcde = new GrnConsumableDtlEntity();
                mapper.map(materialDtl, gcde);
                gcde.setQuantity(materialDtl.getQuantity());
                gcde.setGrnProcessId(grnMaster.getGrnProcessId());
                gcde.setGrnSubProcessId(grnMaster.getGrnSubProcessId());
                gcde.setIgpSubProcessId(Integer.parseInt(req.getIgpId().split("/")[1]));
                gcdeList.add(gcde);

                Integer locatorId = materialDtl.getLocatorId();
                String custodianId = req.getIndentId().toString();
                Optional<OhqMasterConsumableEntity> existingOhq = omcr.findByMaterialCodeAndLocatorIdAndCustodianId(
                    materialDtl.getMaterialCode(), locatorId, custodianId);

                OhqMasterConsumableEntity ohq;
                if (existingOhq.isPresent()) {
                    ohq = existingOhq.get();
                    BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
                    ohq.setQuantity(currentQty.add(materialDtl.getQuantity()));
                } else {
                    ohq = new OhqMasterConsumableEntity();
                    ohq.setCustodianId(custodianId);
                    ohq.setMaterialCode(materialDtl.getMaterialCode());
                    ohq.setLocatorId(locatorId);
                    ohq.setQuantity(materialDtl.getQuantity());
                   // ohq.setBookValue(materialDtl.getBookValue());
                  //  ohq.setDepriciationRate(materialDtl.getDepriciationRate());
                    ohq.setBookValue(materialDtl.getBookValue() != null ? materialDtl.getBookValue() : BigDecimal.ZERO);
                    ohq.setDepriciationRate(materialDtl.getDepriciationRate() != null ? materialDtl.getDepriciationRate() : BigDecimal.ZERO);
                    ohq.setUnitPrice(materialDtl.getUnitPrice());
                }
                omcr.save(ohq);
            } else {
                GrnMaterialDtlEntity grnMaterialDtl = new GrnMaterialDtlEntity();
                mapper.map(materialDtl, grnMaterialDtl);
                grnMaterialDtl.setQuantity(materialDtl.getQuantity());
                grnMaterialDtl.setGrnProcessId(grnMaster.getGrnProcessId());
                grnMaterialDtl.setGrnSubProcessId(grnMaster.getGrnSubProcessId());
                grnMaterialDtl.setIgpSubProcessId(Integer.parseInt(req.getIgpId().split("/")[1]));

                Integer locatorId = materialDtl.getLocatorId();
                String custodianId = req.getIndentId().toString();
                Optional<OhqMasterEntity> existingOhq = ohqmr.findByAssetIdAndLocatorIdAndCustodianId(
                    materialDtl.getAssetId(), locatorId, custodianId);

                if (existingOhq.isPresent()) {
                    OhqMasterEntity ohq = existingOhq.get();
                   /* grnMaterialDtl.setBookValue(ohq.getBookValue());
                    grnMaterialDtl.setDepriciationRate(ohq.getDepriciationRate());
                } else {
                    grnMaterialDtl.setBookValue(null);
                    grnMaterialDtl.setDepriciationRate(null);
                }*/  grnMaterialDtl.setBookValue(ohq.getBookValue() != null ? ohq.getBookValue() : BigDecimal.ZERO);
                    grnMaterialDtl.setDepriciationRate(ohq.getDepriciationRate() != null ? ohq.getDepriciationRate() : BigDecimal.ZERO);
                } else {
                    grnMaterialDtl.setBookValue(BigDecimal.ZERO);
                    grnMaterialDtl.setDepriciationRate(BigDecimal.ZERO);
                }

                grnMaterialDtlList.add(grnMaterialDtl);
                updateAssetAndOhq1(materialDtl, custodianId);
            }
        }

        grnmdr.saveAll(grnMaterialDtlList);
        gcdr.saveAll(gcdeList);

        return "INV/" + grnMaster.getGrnSubProcessId();

        // GrnWorkflowStatus workflowStatus = new GrnWorkflowStatus();
        // workflowStatus.setProcessId("INV" + grnMaster.getGrnProcessId());
        // workflowStatus.setSubProcessId(grnMaster.getGrnSubProcessId());
        // workflowStatus.setAction("CREATED");
        // workflowStatus.setRemarks("GRN Created");
        // workflowStatus.setCreatedBy(req.getCreatedBy());
        // workflowStatus.setCreateDate(LocalDateTime.now());

        // grnWorkRepo.save(workflowStatus);
    }



    private void updateAssetAndOhq1(GrnMaterialInDtlDto materialDtl, String custodianId) {
        System.out.println("UPDATE CALLED");
        AssetMasterEntity asset = amr.findById(materialDtl.getAssetId())
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Asset not found with ID: " + materialDtl.getAssetId())));

        if (asset.getInitQuantity() == null || asset.getInitQuantity().compareTo(BigDecimal.ZERO) == 0) {
            asset.setInitQuantity(materialDtl.getQuantity());
            amr.save(asset);
        }

        List<OhqMasterEntity> ohqList = ohqmr.findByAssetId(asset.getAssetId());

        Optional<OhqMasterEntity> existingOhq = ohqmr.findByAssetIdAndLocatorIdAndCustodianId(
                materialDtl.getAssetId(),
                materialDtl.getLocatorId(),
                custodianId);

        OhqMasterEntity ohq;
        if (existingOhq.isPresent()) {
            ohq = existingOhq.get();
            BigDecimal currentQty = ohq.getQuantity() != null ? ohq.getQuantity() : BigDecimal.ZERO;
            ohq.setQuantity(currentQty.add(materialDtl.getQuantity()));
        } else {
            ohq = new OhqMasterEntity();
            ohq.setCustodianId(custodianId);
            ohq.setAssetId(materialDtl.getAssetId());
            ohq.setLocatorId(materialDtl.getLocatorId());
            ohq.setQuantity(materialDtl.getQuantity());

            if (!ohqList.isEmpty()) {
                OhqMasterEntity existingOhqRecord = ohqList.get(0);
                ohq.setBookValue(existingOhqRecord.getBookValue());
                ohq.setDepriciationRate(existingOhqRecord.getDepriciationRate());
                ohq.setUnitPrice(existingOhqRecord.getUnitPrice());
            } else {
             //   String priceStr = materialDtl.getEstimatedPriceWithCcy().replaceAll("[^\\d.]", "");
             //   String priceStr = String.valueOf(materialDtl.getUnitPrice()).replaceAll("[^\\d.]", "");
                BigDecimal bookValue = BigDecimal.ZERO;
                if (materialDtl.getUnitPrice() != null) {
                    String priceStr = String.valueOf(materialDtl.getUnitPrice()).replaceAll("[^\\d.]", "");
                    if (!priceStr.isEmpty()) {
                        bookValue = new BigDecimal(priceStr);
                    }
                }
              //  BigDecimal bookValue = new BigDecimal(priceStr);
                ohq.setBookValue(bookValue);
                ohq.setDepriciationRate(BigDecimal.ZERO);
                ohq.setUnitPrice(bookValue);
            }
        }
        ohqmr.save(ohq);
    }

//    @Override
//    public List<String> getDistinctGrnProcessIdsForGIAndApproved() {
//        return grnmr.findDistinctGrnProcessIdsForGIAndApproved();
//    }
@Override
public List<PoGrnInfoDto> getDistinctGrnProcessIdsForGIAndApproved() {

    List<Object[]> rows = grnmr.findPoDetailsForGIApproved();

    Map<String, PoGrnInfoDto> map = new LinkedHashMap<>();

    for (Object[] r : rows) {

        String poId = (String) r[0];
        String vendorName = (String) r[1];
        String projectName = (String) r[2];
        LocalDateTime createdDate = (LocalDateTime) r[3];
        String materialDesc = (String) r[4];

        map.computeIfAbsent(poId, id ->
                new PoGrnInfoDto(poId, vendorName, projectName, createdDate, new ArrayList<>())
        );

        if (materialDesc != null)
            map.get(poId).getMaterialDescriptions().add(materialDesc);
    }

    return new ArrayList<>(map.values());
}

    @Override
    public List<String> getApprovedSoIds() {
        return workflowTransitionRepository.findApprovedSoIds();
    }


    @Override
    public List<String> getGrnDetailsByProcessId(String grnProcessId) {
        List<String> list = new ArrayList<String>();
        String grnId = grnProcessId.replace("PO", "");

      List<GrnMasterEntity>  grns = grnmr.findByGrn(grnId);
      for(GrnMasterEntity grn :grns ){
          String processId = "INV"+grn.getGrnProcessId() +"/"+ grn.getGrnSubProcessId();
          boolean exists = paymentVoucherReposiotry.existsByGrnNumberAndPaymentVoucherType(processId, "Full Payment");
          if(!exists) {
              list.add(processId);
          }
      }

      return list;
    }

    public paymentVoucherDto getPaymentVoucherData(String grnProcessId) {
        Integer subProcessId = null;

        if (grnProcessId != null && grnProcessId.contains("/")) {
            String[] processNoSplit = grnProcessId.split("/");
            if (2 == processNoSplit.length){
            subProcessId = Integer.parseInt(processNoSplit[1]);}
            else{
                subProcessId = Integer.parseInt(processNoSplit[2]);
            }
        }

        System.out.println("Sub Process ID: " + subProcessId);
        //Fetch GRN Master
        Optional<GrnMasterEntity> grnMasterOpt = grnmr.findByGrnSubProcessId(subProcessId);
        if (!grnMasterOpt.isPresent()) {
            throw new RuntimeException("GRN Master not found for processId: " + grnProcessId);
        }

        GrnMasterEntity grn = grnMasterOpt.get();

        // Get GI Master using giSubProcessId
        Optional<GiMasterEntity> giMasterOpt = gimr.findByInspectionSubProcessId(grn.getGiSubProcessId());
        if (!giMasterOpt.isPresent()) {
            throw new RuntimeException("GI Master not found for subProcessId: " + grn.getGiSubProcessId());
        }

        GiMasterEntity gi = giMasterOpt.get();

        // Get GPRN Master using gprnProcessId from GI Master
        GprnMasterEntity gprnMaster = gprnMasterRepository.findBySubProcessId(gi.getGprnSubProcessId());
        if (gprnMaster == null) {
            throw new RuntimeException("GPRN Master not found for processId: " + gi.getGprnProcessId());
        }


        // Prepare DTO
        paymentVoucherDto dto = new paymentVoucherDto();
        dto.setVendorName(gprnMaster.getVendorId());
        dto.setVendorInvoiceName(gprnMaster.getChallanNo());
        dto.setVendorInvoiceDate(CommonUtils.convertDateToString(gprnMaster.getDate()));
        System.out.print(grn.getGrnSubProcessId());
        // Fetch Material Details
        List<GrnMaterialDtlEntity> grnMaterials = grnmdr.findByGrnSubProcessId(grn.getGrnSubProcessId());
        List<GprnMaterialDtlEntity> gprnMaterials = gprnMaterialDtlRepository.findByProcessId(gi.getGprnProcessId());

        System.out.println(grnMaterials);
        System.out.println(gprnMaterials);
        if (grnMaterials == null || grnMaterials.isEmpty()) {
            List<GrnConsumableDtlEntity> grnConsumables = gcdr.findByGrnSubProcessId(grn.getGrnSubProcessId());
            grnMaterials = grnConsumables.stream().map(consumable -> {
                GrnMaterialDtlEntity mat = new GrnMaterialDtlEntity();
             //   mat.setMaterialCode(consumable.getMaterialCode());
                mat.setQuantity(consumable.getQuantity());
             //   mat.setUomId(consumable.getLocatorId()); // map appropriately
             //   mat.setUnitPrice(consumable.getBookValue()); // default or book value
              //  mat.setDepriciationRate(consumable.getDepriciationRate());
                return mat;
            }).collect(Collectors.toList());
        }
        // Merge Material Data into DTO
        // dto.setMaterialsList(
        //         grnMaterials.stream().map(grns -> {
        //             // Find matching GPRN record for the same asset/material if applicable
        //             GprnMaterialDtlEntity gprn = gprnMaterials.stream()
        //                     .filter(g -> g.getSubProcessId().equals(gi.getGprnSubProcessId()))
        //                     .findFirst().orElse(null);

        //             paymentVoucherMaterials mat = new paymentVoucherMaterials();
        //             mat.setMaterialCode(gprn.getMaterialCode());
        //             mat.setMaterialDescription(gprn.getMaterialDesc());
        //             String grnNumber="INV"+grn.getGrnProcessId()+"/"+grn.getGrnSubProcessId();
        //             System.out.println("ufahkl"+ grnNumber);
        //            /* PaymentVoucher pv = paymentVoucherReposiotry.findByGrnNumber(grnNumber);
        //             if(pv!= null) {

        //                 PaymentVoucherMaterials m = paymentVoucherMaterialsRepository
        //                         .findByMaterialCodeAndPaymentVoucherId(gprn.getMaterialCode(), pv.getId());
        //                 BigDecimal finalQuantity = (m != null && m.getQuantity() != null)
        //                         ? grns.getQuantity().subtract(m.getQuantity())
        //                         : grns.getQuantity();
        //                 mat.setQuantity(finalQuantity);
        //             }else{
        //                 mat.setQuantity(grns.getQuantity());
        //             }*/
        //             //  String grnNumber = "INV" + grn.getGrnProcessId() + "/" + grn.getGrnSubProcessId();
        //             System.out.println("GRN Number: " + grnNumber);


        //           //  BigDecimal totalReceivedQty = paymentVoucherMaterialsRepository
        //                    // .getTotalReceivedQuantity(grnNumber, gprn.getMaterialCode());

        //           //  BigDecimal finalQuantity = grns.getQuantity().subtract(totalReceivedQty);
        //            // mat.setQuantity(finalQuantity);


        //             BigDecimal gst = purchaseOrderAttributesRepo
        //                     .findGstByMaterialCodeAndPoId(gprn.getMaterialCode(), gprnMaster.getPoId());
        //             System.out.println("GST: " + gst);
        //             BigDecimal exchangeRate = purchaseOrderAttributesRepo
        //                     .findExchangeRateByMaterialCodeAndPoId(gprn.getMaterialCode(), gprnMaster.getPoId());

        //             String currency = purchaseOrderAttributesRepo
        //                     .findCurrencyByMaterialCodeAndPoId(gprn.getMaterialCode(), gprnMaster.getPoId());

        //             if (gprn != null) {
        //                 mat.setUom(gprn.getUomId());
        //                 mat.setUnitPrice(gprn.getUnitPrice());
        //                 mat.setCurrency(currency);
        //                 mat.setExchangeRate(exchangeRate != null ? exchangeRate : BigDecimal.ONE);
        //                 mat.setGst(gst);
        //                 mat.setQuantity(grns.getQuantity());
        //                 BigDecimal amount = grns.getQuantity().multiply(gprn.getUnitPrice());
        //                 mat.setAmount(amount);
        //             }
        //             return mat;
        //         }).collect(Collectors.toList())



        // );
        // ── STEP 1: Resolve materialCode for each GRN row via AssetMaster
//    Group GRN rows by materialCode, summing quantities
Map<String, BigDecimal> qtyByMaterialCode = new LinkedHashMap<>();

for (GrnMaterialDtlEntity grns : grnMaterials) {
    String materialCode = null;

    if (grns.getAssetId() != null) {
        // Look up materialCode from AssetMaster
        AssetMasterEntity asset = amr.findById(grns.getAssetId()).orElse(null);
        if (asset != null) {
            materialCode = asset.getMaterialCode(); // confirm this field name in AssetMasterEntity
        }
    }

    // Fallback: try to match from GPRN by subProcessId (consumable path)
    if (materialCode == null) {
        GprnMaterialDtlEntity fallbackGprn = gprnMaterials.stream()
                .filter(g -> g.getSubProcessId().equals(gi.getGprnSubProcessId()))
                .findFirst().orElse(null);
        if (fallbackGprn != null) {
            materialCode = fallbackGprn.getMaterialCode();
        }
    }

    if (materialCode == null) continue;

    // Sum quantities per materialCode
    qtyByMaterialCode.merge(materialCode, 
        grns.getQuantity() != null ? grns.getQuantity() : BigDecimal.ZERO, 
        BigDecimal::add);
}

// ── STEP 2: Build one paymentVoucherMaterials line per unique materialCode
List<paymentVoucherMaterials> mergedMaterials = new ArrayList<>();

for (Map.Entry<String, BigDecimal> entry : qtyByMaterialCode.entrySet()) {
    String materialCode = entry.getKey();
    BigDecimal totalQty = entry.getValue();

    // Find matching GPRN material for description, unit price, uom
    GprnMaterialDtlEntity gprn = gprnMaterials.stream()
            .filter(g -> materialCode.equals(g.getMaterialCode()))
            .findFirst().orElse(null);

    if (gprn == null) continue;

    BigDecimal gst = purchaseOrderAttributesRepo
            .findGstByMaterialCodeAndPoId(materialCode, gprnMaster.getPoId());
    BigDecimal exchangeRate = purchaseOrderAttributesRepo
            .findExchangeRateByMaterialCodeAndPoId(materialCode, gprnMaster.getPoId());
    String currency = purchaseOrderAttributesRepo
            .findCurrencyByMaterialCodeAndPoId(materialCode, gprnMaster.getPoId());

    paymentVoucherMaterials mat = new paymentVoucherMaterials();
    mat.setMaterialCode(materialCode);
    mat.setMaterialDescription(gprn.getMaterialDesc());
    mat.setUom(gprn.getUomId());
    mat.setUnitPrice(gprn.getUnitPrice());
    mat.setCurrency(currency);
    mat.setExchangeRate(exchangeRate != null ? exchangeRate : BigDecimal.ONE);
    mat.setGst(gst);
    mat.setQuantity(totalQty);
    mat.setAmount(totalQty.multiply(gprn.getUnitPrice()));

    mergedMaterials.add(mat);
}

dto.setMaterialsList(mergedMaterials);


        BigDecimal totalAmount = dto.getMaterialsList().stream()
                .map(m -> {
                    BigDecimal amount = m.getAmount() != null ? m.getAmount() : BigDecimal.ZERO;
                    BigDecimal gst = m.getGst() != null ? m.getGst() : BigDecimal.ZERO;
                    BigDecimal gstAmount = amount.multiply(gst).divide(BigDecimal.valueOf(100));
                    return amount.add(gstAmount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        dto.setTotalAmount(totalAmount);

        Optional<PaymentVoucher> existingVoucherOpt = paymentVoucherReposiotry.findTopByGrnNumberOrderByIdDesc(grnProcessId);

        if (existingVoucherOpt.isPresent()) {
            PaymentVoucher existingVoucher = existingVoucherOpt.get();
            String type = existingVoucher.getPaymentVoucherType();

            if ("Partial".equalsIgnoreCase(type)) {
                BigDecimal partialPaid = existingVoucher.getPaidAmount() != null ? existingVoucher.getPaidAmount() : BigDecimal.ZERO;
                dto.setPaymentVoucherType("Partial");
                dto.setPartialAmountAlreadypaid(partialPaid);
                dto.setPartialBalanceAmount(totalAmount.subtract(partialPaid));
            } else if ("Advance".equalsIgnoreCase(type)) {
                BigDecimal advancePaid = existingVoucher.getPaidAmount() != null ? existingVoucher.getPaidAmount() : BigDecimal.ZERO;
                dto.setPaymentVoucherType("Advance");
                dto.setAdvanceAmountAlreadyPaid(advancePaid);
                dto.setAdvanceBalanceAmount(totalAmount.subtract(advancePaid));
            }
        }
        return dto;
    }


    public paymentVoucherDto getPaymentVoucherDtoBySoId(String soId) {
        ServiceOrder so = serviceOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("Service Order not found: " + soId));

        paymentVoucherDto dto = new paymentVoucherDto();
        dto.setProcessId(so.getSoId());
        dto.setVendorName(so.getVendorName());



        List<paymentVoucherMaterials> materials = so.getMaterials().stream().map(mat -> {
            paymentVoucherMaterials m = new paymentVoucherMaterials();
            m.setMaterialCode(mat.getMaterialCode());
            m.setMaterialDescription(mat.getMaterialDescription());
            m.setQuantity(mat.getQuantity());
            m.setUnitPrice(mat.getRate());
            m.setCurrency(mat.getCurrency());
            m.setExchangeRate(mat.getExchangeRate());
            m.setGst(mat.getGst());
            m.setAmount(mat.getQuantity().multiply(mat.getRate()));
            return m;
        }).collect(Collectors.toList());

        dto.setMaterialsList(materials);

        BigDecimal totalAmount = dto.getMaterialsList().stream()
                .map(m -> {
                    BigDecimal amount = m.getAmount() != null ? m.getAmount() : BigDecimal.ZERO;
                    BigDecimal gst = m.getGst() != null ? m.getGst() : BigDecimal.ZERO;
                    BigDecimal gstAmount = amount.multiply(gst).divide(BigDecimal.valueOf(100));
                    return amount.add(gstAmount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(totalAmount);
        Optional<PaymentVoucher> existingVoucherOpt = paymentVoucherReposiotry.findTopByServiceOrderDetailsOrderByIdDesc(soId);

        if (existingVoucherOpt.isPresent()) {
            PaymentVoucher existingVoucher = existingVoucherOpt.get();
            String type = existingVoucher.getPaymentVoucherType();

            if ("Partial".equalsIgnoreCase(type)) {
                BigDecimal partialPaid = existingVoucher.getPaidAmount() != null ? existingVoucher.getPaidAmount() : BigDecimal.ZERO;
                dto.setPaymentVoucherType("Partial");
                dto.setPartialAmountAlreadypaid(partialPaid);
                dto.setPartialBalanceAmount(totalAmount.subtract(partialPaid));
            } else if ("Advance".equalsIgnoreCase(type)) {
                BigDecimal advancePaid = existingVoucher.getPaidAmount() != null ? existingVoucher.getPaidAmount() : BigDecimal.ZERO;
                dto.setPaymentVoucherType("Advance");
                dto.setAdvanceAmountAlreadyPaid(advancePaid);
                dto.setAdvanceBalanceAmount(totalAmount.subtract(advancePaid));
            }
        }
        return dto;
    }

    public List<GrnDropdownDto> getPendingGrns() {

       /* List<GiMasterEntity> pendingGiList = gimr.findAll().stream()
                .filter(gi -> !grnmr.existsByGiSubProcessId(gi.getInspectionSubProcessId()))
                .collect(Collectors.toList());*/
        List<GiMasterEntity> pendingGiList = gimr.findAll().stream()
                .filter(gi -> "APPROVED".equalsIgnoreCase(gi.getStatus()))
                .filter(gi -> !grnmr.existsByGiSubProcessId(gi.getInspectionSubProcessId()))
                .collect(Collectors.toList());


        return pendingGiList.stream()
                .map(gi -> {

               //   GprnMasterEntity gprn = gprnMasterRepository.findBySubProcessId(gi.getGprnSubProcessId());

                    GprnPoVendorDto gprnDto = gprnMasterRepository.findPoIdAndVendorIdBySubProcessId(gi.getGprnSubProcessId());

                    List<String> materialDescriptions = gimdr
                            .findMaterialDescriptionsByInspectionSubProcessId(gi.getInspectionSubProcessId());

                    if (materialDescriptions == null || materialDescriptions.isEmpty()) {
                        materialDescriptions = gicdr
                                .findByInspectionSubProcessId(gi.getInspectionSubProcessId())
                                .stream()
                                .map(GoodsInspectionConsumableDetailEntity::getMaterialDesc)
                                .collect(Collectors.toList());
                    }
                    return new GrnDropdownDto(
                            gi.getInspectionSubProcessId(),
                            "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId(), // gprnProcessId
                            gprnDto.getPoId(),
                            gprnDto.getVendorId(),
                            materialDescriptions
                    );
                })
                .collect(Collectors.toList());
    }
private String resolveConsigneeName(String poId, String storePersonName) {
    String tenderId = gprnMasterRepository.findTenderIdByPoId(poId);
    if (tenderId == null) return storePersonName;

    int indentCount = gprnMasterRepository.countIndentsByTenderId(tenderId);

    if (indentCount == 1) {
        String indentorName = gprnMasterRepository.findSingleIndentorNameForTender(tenderId);
        return indentorName != null ? indentorName : storePersonName;
    }
    return storePersonName;
}


}
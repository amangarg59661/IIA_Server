package com.astro.service.impl.InventoryModule;

import com.astro.dto.workflow.InventoryModule.MaterialDto;
import com.astro.dto.workflow.InventoryModule.PendingGprnPoDto;
import com.astro.entity.ProcurementModule.PurchaseOrder;
import com.astro.entity.ProcurementModule.PurchaseOrderAttributes;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderAttributesRepository;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository;
import com.astro.repository.UserMasterRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;

import com.astro.service.InventoryModule.GprnService;
import com.astro.repository.InventoryModule.GprnRepository.GprnMasterRepository;
import com.astro.repository.InventoryModule.GprnRepository.GprnMaterialDtlRepository;
import com.astro.repository.VendorMasterRepository;
import com.astro.entity.InventoryModule.GprnMasterEntity;
import com.astro.entity.InventoryModule.GprnMaterialDtlEntity;
import com.astro.entity.VendorMaster;
import com.astro.dto.workflow.InventoryModule.GprnDto.*;
import com.astro.exception.*;
import com.astro.constant.AppConstant;
import com.astro.util.CommonUtils;
import org.modelmapper.ModelMapper;

@Service
public class GprnServiceImpl implements GprnService {
    @Value("${filePath}")
    private String bp;
    
    @Autowired
    private GprnMasterRepository gmr;
    
    @Autowired
    private GprnMaterialDtlRepository gmdr;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    
    @Autowired
    private VendorMasterRepository vmr;
    @Autowired
    private PurchaseOrderAttributesRepository poMaterialRepo;
    @Autowired
    private UserMasterRepository userMasterRepository;

    private final String basePath;

    public GprnServiceImpl(@Value("${filePath}") String bp) {
        this.basePath = bp + "/INV";
    }
    private String extractProcessId(String processNo) {
    int lastSlash = processNo.lastIndexOf("/");
    return processNo.substring(3, lastSlash); // strips "INV" prefix and "/{subProcessId}" suffix
}

private int extractSubProcessId(String processNo) {
    return Integer.parseInt(processNo.substring(processNo.lastIndexOf("/") + 1));
}
    @Override
    @Transactional
    public String saveGprn(SaveGprnDto req) {
        GprnMasterEntity gme = new GprnMasterEntity();
        ModelMapper mapper = new ModelMapper();
        mapper.map(req, gme);
        
        gme.setDate(CommonUtils.convertStringToDateObject(req.getDate()));
        gme.setCreateDate(LocalDateTime.now());
        gme.setDeliveryDate(CommonUtils.convertStringToDateObject(req.getDeliveryDate()));
        gme.setSupplyExpectedDate(CommonUtils.convertStringToDateObject(req.getSupplyExpectedDate()));
        gme.setUpdateDate(LocalDateTime.now());
        // gme.setProcessId(req.getPoId().substring(2));
        gme.setProcessId(req.getPoId().substring(2)); 
        gme.setLocationId(req.getLocationId());
        gme.setCreatedBy(req.getCreatedBy());
        gme.setIndentId(req.getIndentId());
        gme.setStatus("APPROVED");

        gme = gmr.save(gme);

        List<GprnMaterialDtlEntity> saveDtlEntityList = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();
        Boolean errorFound = false;

        for (MaterialDtlDto dtl : req.getMaterialDtlList()) {
            List<GprnMaterialDtlEntity> gmdeList = gmdr.findByPoIdAndMaterialCode(req.getPoId(), dtl.getMaterialCode());
    
            // BigDecimal prevRecQuant = gmdeList.stream()
            //     .map(GprnMaterialDtlEntity::getReceivedQuantity)
            //     .reduce(BigDecimal.ZERO, BigDecimal::add);
            // System.out.println("PREV REC QUANT: " + prevRecQuant);
            // System.out.println("NEW WUANT: " + dtl.getReceivedQuantity());
    
            // BigDecimal totalRecQuant = prevRecQuant.add(dtl.getReceivedQuantity());
            if (dtl.getOrderedQuantity().compareTo(dtl.getReceivedQuantity()) < 0) {
                errorMessage.append("Received quantity for " + dtl.getMaterialCode() + " is more than ordered quantity.");
                errorFound = true;
                continue;
            }
    
            GprnMaterialDtlEntity gmde = new GprnMaterialDtlEntity();
            mapper.map(dtl, gmde);
            gmde.setProcessId(gme.getProcessId());
            gmde.setPoId(gme.getPoId());
            gmde.setSubProcessId(gme.getSubProcessId());
            
            try {
                List<String> imageFileNames = new ArrayList<>();
                for (String base64Image : dtl.getImageBase64()) {
                    String imageFileName = CommonUtils.saveBase64Image(base64Image, basePath);
                    imageFileNames.add(imageFileName);
                }
                gmde.setFileName(String.join(",", imageFileNames));
            } catch (Exception e) {
                throw new InvalidInputException(new ErrorDetails(
                    AppConstant.FILE_UPLOAD_ERROR,
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CORRUPTED,
                    "Error while uploading images."));
            }
    
            saveDtlEntityList.add(gmde);
            Optional<PurchaseOrderAttributes> poMaterial = poMaterialRepo.findByPurchaseOrder_PoIdAndMaterialCode(gme.getPoId(), dtl.getMaterialCode());

            if (poMaterial.isPresent()) {
                PurchaseOrderAttributes pom = poMaterial.get();
                BigDecimal existingQty = pom.getReceivedQuantity() != null
                        ? pom.getReceivedQuantity()
                        : BigDecimal.ZERO;
                BigDecimal updatedReceivedQty = existingQty.add(dtl.getReceivedQuantity());
                pom.setReceivedQuantity(updatedReceivedQty);
                poMaterialRepo.save(pom);
               // BigDecimal updatedReceivedQty = pom.getReceivedQuantity().add(dtl.getReceivedQuantity());
              //  pom.setReceivedQuantity(updatedReceivedQty);
               // poMaterialRepo.save(pom);
            }

        }

        if (errorFound) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                errorMessage.toString()));
        }

        gmdr.saveAll(saveDtlEntityList);


return "INV" + gme.getProcessId() + "/" + gme.getSubProcessId();
// Was: INV001/1 (PO-borrowed)
// Now: INV0001/1 (GPRN-own sequence)
        // return "INV" + gme.getProcessId() + "/" + gme.getSubProcessId();
    }

    @Override
    public SaveGprnDto getGprnDtls(String processNo) {
        ModelMapper mapper = new ModelMapper();
        String[] processNoSplit = processNo.split("/");
        
        // if (processNoSplit.length != 2) {
        if (processNoSplit.length < 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process ID"));
        }

        Integer subProcessId = extractSubProcessId(processNo);
        // Integer subProcessId = Integer.parseInt(processNoSplit[1]);
        GprnMasterEntity gme = gmr.findById(subProcessId)
            .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "Process not found for the provided process ID.")));

        PurchaseOrder po = purchaseOrderRepository.findByPoId(gme.getPoId());

        BigDecimal totalMaterialAmount = BigDecimal.ZERO;
        List<GprnMaterialDtlEntity> gmdeList = gmdr.findBySubProcessId(gme.getSubProcessId());
        List<MaterialDtlDto> materialDtlListRes = gmdeList.stream()
            .map(gmde -> {
                MaterialDtlDto mdd = mapper.map(gmde, MaterialDtlDto.class);
                try {
                    List<String> imageBase64List = new ArrayList<>();
                    String[] fileNames = gmde.getFileName().split(",");
                    for (String fileName : fileNames) {
                        String imageBase64 = CommonUtils.convertImageToBase64(fileName.trim(), basePath);
                        imageBase64List.add(imageBase64);
                    }
                    mdd.setImageBase64(imageBase64List);

                } catch (Exception e) {
                    // Log exception
                }

                return mdd;
            })
            .collect(Collectors.toList());
        totalMaterialAmount = gmdeList.stream()
                .filter(gmde -> gmde.getReceivedQuantity() != null && gmde.getUnitPrice() != null)
                .map(gmde -> gmde.getReceivedQuantity().multiply(gmde.getUnitPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        VendorMaster vm = vmr.findById(gme.getVendorId())
            .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "Vendor not found for the provided vendor ID.")));

        SaveGprnDto gprnRes = mapper.map(gme, SaveGprnDto.class);
        gprnRes.setProcessId(processNo);
        gprnRes.setVendorEmail(vm.getEmailAddress());
        gprnRes.setVendorName(vm.getVendorName());
        gprnRes.setVendorContact(vm.getContactNo());
        gprnRes.setDate(CommonUtils.convertDateToString(gme.getDate()));
        gprnRes.setSupplyExpectedDate(CommonUtils.convertDateToString(gme.getSupplyExpectedDate()));
        gprnRes.setDeliveryDate(CommonUtils.convertDateToString(gme.getDeliveryDate()));
        gprnRes.setPoAmount(po.getTotalValueOfPo());

        gprnRes.setGprnAmount(totalMaterialAmount);
        gprnRes.setMaterialDtlList(materialDtlListRes);

        String custodainName = userMasterRepository.findUserNameByUserId(Integer.valueOf(gme.getReceivedBy()));

        gprnRes.setReceivedName(custodainName);
        return gprnRes;
    }

    @Override
    public List<String> getPendingGprn() {
        List<String> pendingGprnList = gmr.findPoIdsWithIncompleteGprn();
        return pendingGprnList;
    }
    @Override
    public List<PendingGprnPoDto> getPendingGprnDetails() {

        List<Object[]> rows = gmr.findPendingGprnDetailedRows();
        Map<String, PendingGprnPoDto> poMap = new HashMap<>();

        for (Object[] r : rows) {

            String poId = (String) r[0];
            PendingGprnPoDto dto = poMap.getOrDefault(poId, new PendingGprnPoDto());

            dto.setPoId(poId);
            dto.setVendorName((String) r[1]);
            dto.setProjectName((String) r[2]);

            // Created Date
            Object createdDateObj = r[3];
            if (createdDateObj instanceof Timestamp) {
                dto.setCreatedDate(((Timestamp) createdDateObj).toLocalDateTime());
            } else if (createdDateObj instanceof LocalDateTime) {
                dto.setCreatedDate((LocalDateTime) createdDateObj);
            }

            // Indent IDs
            if (dto.getIndentIds() == null) dto.setIndentIds(new ArrayList<>());
            String indentId = (String) r[4];

            if (indentId != null && !dto.getIndentIds().contains(indentId)) {
                dto.getIndentIds().add(indentId);
            }

            // Materials
            if (dto.getMaterials() == null) dto.setMaterials(new ArrayList<>());

            MaterialDto material = new MaterialDto();
            material.setMaterialDesc((String) r[5]);

            // Safely convert all quantities using BigDecimal
            BigDecimal orderQty   = toBigDecimal(r[6]);
            BigDecimal receivedQty = toBigDecimal(r[7]);
            BigDecimal pendingQty  = toBigDecimal(r[8]);

            material.setOrderQty(orderQty);
            material.setReceivedQty(receivedQty);
            material.setPendingQty(pendingQty);

            dto.getMaterials().add(material);

            poMap.put(poId, dto);
        }

        return new ArrayList<>(poMap.values());
    }

    /** --- Utility method to safely convert Object → BigDecimal --- */
    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;  // fallback to avoid crash
        }
    }



    @Override
    public void validateGprnSubProcessId(String processNo) {
        String[] processNoSplit = processNo.split("/");
        // if (processNoSplit.length != 2) {
        if (processNoSplit.length < 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process ID"));
        }

        Integer subProcessId = extractSubProcessId(processNo);
        // Integer subProcessId = Integer.parseInt(processNoSplit[1]);
        GprnMasterEntity gprnMaster = gmr.findById(subProcessId)
            .orElseThrow(() -> new BusinessException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "Provided GPRN No. is not valid.")));

        if (!"APPROVED".equals(gprnMaster.getStatus())) {
            throw new BusinessException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "GPRN must be approved before proceeding."));
        }
    }


    @Override
    @Transactional
    public void rejectGprn(String processNo) {
        String[] processNoSplit = processNo.split("/");
        // if (processNoSplit.length != 2) {
        if (processNoSplit.length < 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process number format"));
        }
        Integer inspectionId = extractSubProcessId(processNo);
        // Integer inspectionId = Integer.parseInt(processNoSplit[1]);
        
        GprnMasterEntity giMaster = gmr.findById(inspectionId)
            .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "Goods Inspection not found")));

        giMaster.setStatus("REJECTED");
        gmr.save(giMaster);
    }

    @Override
    @Transactional
    public void approveGprn(String processNo) {
        String[] processNoSplit = processNo.split("/");
        // if (processNoSplit.length != 2) {
        if (processNoSplit.length < 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process number format"));
        }
            Integer inspectionId = extractSubProcessId(processNo);
        // Integer inspectionId = Integer.parseInt(processNoSplit[1]);
        
        GprnMasterEntity giMaster = gmr.findById(inspectionId)
            .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "Goods Inspection not found")));

        giMaster.setStatus("APPROVED");
        gmr.save(giMaster);
    }

    @Override
    @Transactional
    public void changeReqGprn(String processNo) {
        String[] processNoSplit = processNo.split("/");
        // if (processNoSplit.length != 2) {
        if (processNoSplit.length < 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process number format"));
        }

        // Integer inspectionId = Integer.parseInt(processNoSplit[1]);
        Integer inspectionId = extractSubProcessId(processNo);
        GprnMasterEntity giMaster = gmr.findById(inspectionId)
            .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "Goods Inspection not found")));

        giMaster.setStatus("CHANGE REQUEST");
        gmr.save(giMaster);
    }

    @Override
    @Transactional
    public void updateGprn(SaveGprnDto updateRequest) {
        String[] processNoSplit = updateRequest.getProcessId().split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process number format"));
        }
    
        Integer subProcessId = Integer.parseInt(processNoSplit[1]);
        
        GprnMasterEntity gprnMaster = gmr.findById(subProcessId)
            .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "GPRN not found for the provided process ID.")));
        
        // Update master fields
        ModelMapper mapper = new ModelMapper();
        String createdBy = gprnMaster.getCreatedBy();
        mapper.map(updateRequest, gprnMaster);
        gprnMaster.setCreatedBy(createdBy);
        gprnMaster.setProcessId(extractProcessId(updateRequest.getProcessId()));
        // gprnMaster.setProcessId(processNoSplit[0].substring(3));
        gprnMaster.setStatus("AWAITING APPROVAL");
        
        
        // Handle date fields separately
        if (updateRequest.getDate() != null) {
            gprnMaster.setDate(CommonUtils.convertStringToDateObject(updateRequest.getDate()));
        }
        if (updateRequest.getDeliveryDate() != null) {
            gprnMaster.setDeliveryDate(CommonUtils.convertStringToDateObject(updateRequest.getDeliveryDate()));
        }
        if (updateRequest.getSupplyExpectedDate() != null) {
            gprnMaster.setSupplyExpectedDate(CommonUtils.convertStringToDateObject(updateRequest.getSupplyExpectedDate()));
        }
        
        gprnMaster.setUpdateDate(LocalDateTime.now());
        
        // Save the updated master
        gmr.save(gprnMaster);
        
        // Process material details
        if (updateRequest.getMaterialDtlList() != null && !updateRequest.getMaterialDtlList().isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            Boolean errorFound = false;
            
            for (MaterialDtlDto updatedMaterial : updateRequest.getMaterialDtlList()) {
                // Find the existing GPRN material detail
                List<GprnMaterialDtlEntity> existingGprnMaterials = gmdr.findBySubProcessIdAndMaterialCode(
                    subProcessId, updatedMaterial.getMaterialCode());
                
                if (existingGprnMaterials.isEmpty()) {
                    errorMessage.append("Material code ").append(updatedMaterial.getMaterialCode())
                        .append(" not found in the GPRN. ");
                    errorFound = true;
                    continue;
                }
                
                GprnMaterialDtlEntity existingGprnMaterial = existingGprnMaterials.get(0);
                
                // Find the corresponding PO material
                Optional<PurchaseOrderAttributes> poMaterialOpt = poMaterialRepo.findByPurchaseOrder_PoIdAndMaterialCode(
                    gprnMaster.getPoId(), updatedMaterial.getMaterialCode());
                
                if (!poMaterialOpt.isPresent()) {
                    errorMessage.append("Material code ").append(updatedMaterial.getMaterialCode())
                        .append(" not found in the Purchase Order. ");
                    errorFound = true;
                    continue;
                }
                
                PurchaseOrderAttributes poMaterial = poMaterialOpt.get();
                
                // Calculate the difference between the updated and existing received quantities
                BigDecimal existingReceivedQty = existingGprnMaterial.getReceivedQuantity();
                BigDecimal updatedReceivedQty = updatedMaterial.getReceivedQuantity();
                BigDecimal quantityDifference = updatedReceivedQty.subtract(existingReceivedQty);
                
                // Check if the updated quantity is valid
                BigDecimal totalOrderedQty = poMaterial.getQuantity();
                BigDecimal currentReceivedQty = poMaterial.getReceivedQuantity();
                BigDecimal newTotalReceivedQty = currentReceivedQty.add(quantityDifference);
                
                if (newTotalReceivedQty.compareTo(BigDecimal.ZERO) < 0) {
                    errorMessage.append("Updated quantity for ").append(updatedMaterial.getMaterialCode())
                        .append(" results in negative received quantity. ");
                    errorFound = true;
                    continue;
                }
                
                if (newTotalReceivedQty.compareTo(totalOrderedQty) > 0) {
                    errorMessage.append("Updated quantity for ").append(updatedMaterial.getMaterialCode())
                        .append(" exceeds ordered quantity. Ordered: ").append(totalOrderedQty)
                        .append(", Total received would be: ").append(newTotalReceivedQty).append(". ");
                    errorFound = true;
                    continue;
                }
                
                // Update the GPRN material detail
                mapper.map(updatedMaterial, existingGprnMaterial);
                
                // Handle image updates if provided
                if (updatedMaterial.getImageBase64() != null && !updatedMaterial.getImageBase64().isEmpty()) {
                    try {
                        List<String> imageFileNames = new ArrayList<>();
                        for (String base64Image : updatedMaterial.getImageBase64()) {
                            String imageFileName = CommonUtils.saveBase64Image(base64Image, basePath);
                            imageFileNames.add(imageFileName);
                        }
                        existingGprnMaterial.setFileName(String.join(",", imageFileNames));
                    } catch (Exception e) {
                        throw new InvalidInputException(new ErrorDetails(
                            AppConstant.FILE_UPLOAD_ERROR,
                            AppConstant.USER_INVALID_INPUT,
                            AppConstant.ERROR_TYPE_CORRUPTED,
                            "Error while uploading images."));
                    }
                }
                
                gmdr.save(existingGprnMaterial);
                
                // Update the PO material received quantity
                poMaterial.setReceivedQuantity(newTotalReceivedQty);
                poMaterialRepo.save(poMaterial);
            }
            
            if (errorFound) {
                throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    errorMessage.toString()));
            }
        }
    }
}
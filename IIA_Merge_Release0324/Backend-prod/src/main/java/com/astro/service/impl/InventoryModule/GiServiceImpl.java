// package com.astro.service.impl.InventoryModule;

// import com.astro.dto.workflow.InventoryModule.AssetResponseDto;
// import com.astro.dto.workflow.InventoryModule.GprnDropdownDto;
// import com.astro.dto.workflow.InventoryModule.GprnPoVendorDto;
// import com.astro.dto.workflow.NewAssetResponseDto;
// import com.astro.entity.UserMaster;
// import com.astro.repository.InventoryModule.GprnRepository.GprnMasterRepository;
// import com.astro.repository.InventoryModule.GprnRepository.GprnMaterialDtlRepository;
// import com.astro.repository.InventoryModule.isn.IssueNoteMasterRepository;
// import com.astro.repository.InventoryModule.ogp.OgpMasterRejectedGiRepository;
// import com.astro.repository.ProcurementModule.IndentCreation.MaterialDetailsRepository;
// import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderAttributesRepository;

// import com.astro.repository.UserMasterRepository;
// import com.astro.util.EmailService;
// import org.springframework.stereotype.Service;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;

// import javax.transaction.Transactional;
// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.*;
// import java.math.BigDecimal;
// import java.util.stream.Collectors;

// import com.astro.service.InventoryModule.GiService;
// import com.astro.service.InventoryModule.GprnService;
// import com.astro.repository.InventoryModule.GiRepository.*;
// import com.astro.repository.MaterialMasterRepository;
// import com.astro.repository.InventoryModule.AssetMasterRepository;
// import com.astro.repository.InventoryModule.GoodsInspectionConsumableDetailRepository;
// import com.astro.entity.MaterialMaster;
// import com.astro.entity.InventoryModule.*;
// import com.astro.entity.ProcurementModule.PurchaseOrderAttributes;
// import com.astro.dto.workflow.InventoryModule.GiDto.*;
// import com.astro.dto.workflow.InventoryModule.GprnDto.SaveGprnDto;
// import com.astro.dto.workflow.InventoryModule.gprn.GprnPendingInspectionDetailDto;
// import com.astro.dto.workflow.InventoryModule.gprn.GprnPendingInspectionDto;
// import com.astro.exception.*;
// import com.astro.constant.AppConstant;
// import com.astro.util.CommonUtils;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializationFeature;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// import org.modelmapper.ModelMapper;

// @Service
// public class GiServiceImpl implements GiService {
//     @Value("${filePath}")
//     private String bp;

//     @Autowired
//     private GiMasterRepository gimr;

//     @Autowired
//     private GiMaterialDtlRepository gimdr;

//     @Autowired
//     private GprnService gprnService;

//     @Autowired
//     private AssetMasterRepository amr;

//     @Autowired
//     private MaterialMasterRepository mmr;

//     @Autowired
//     private IssueNoteMasterRepository issueNoteMasterRepository;

//     @Autowired
//     private GoodsInspectionConsumableDetailRepository gicdr;
//     @Autowired
//     private GiWorkflowStatusRepository gistausRepo;

//     @Autowired
//     private PurchaseOrderAttributesRepository poar;
//     @Autowired
//     private GprnMasterRepository gprnMasterRepository;
//     @Autowired
//     private GprnMaterialDtlRepository gprnMaterialDtlRepository;
//     @Autowired
//     private OgpMasterRejectedGiRepository ogpMasterRejectedGiRepository;
//     @Autowired
//     private MaterialDetailsRepository materialDetailsRepository;
//     @Autowired
//     private UserMasterRepository userMasterRepository;
//     @Autowired
//     private EmailService emailService;

//     private final String basePath;

//     public GiServiceImpl(@Value("${filePath}") String bp) {
//         this.basePath = bp + "/INV";
//     }

//     @Override
//     @Transactional
//     public String saveGi(SaveGiDto req) {
//         gprnService.validateGprnSubProcessId(req.getGprnNo());
//         SaveGprnDto gprnDto = gprnService.getGprnDtls(req.getGprnNo());

//         ModelMapper mapper = new ModelMapper();
//         GiMasterEntity gime = new GiMasterEntity();
//         gime.setCommissioningDate(CommonUtils.convertStringToDateObject(req.getCommissioningDate()));
//         gime.setInstallationDate(CommonUtils.convertStringToDateObject(req.getCommissioningDate()));
//         gime.setGprnProcessId(req.getGprnNo().split("/")[0].substring(3));
//         gime.setGprnSubProcessId(Integer.parseInt(req.getGprnNo().split("/")[1]));
//         gime.setCreateDate(LocalDateTime.now());
//         gime.setCreatedBy(req.getCreatedBy());
//         gime.setLocationId(req.getLocationId());
//         gime.setStatus("AWAITING APPROVAL");
//         gime.setGprnAmount(req.getGprnAmount());
//         gime.setPoAmount(req.getPoAmount());

//         gime = gimr.save(gime);

//         List<GiMaterialDtlEntity> gimdeList = new ArrayList<>();
//         List<GoodsInspectionConsumableDetailEntity> gicdeList = new ArrayList<>();
//         StringBuilder errorMessage = new StringBuilder();
//         Boolean errorFound = false;

//         for (GiMaterialDtlDto gmdd : req.getMaterialDtlList()) {

//             if (gmdd.getCategory().equalsIgnoreCase("consumable")) {
//                 Optional<GoodsInspectionConsumableDetailEntity> gicdeOpt = gicdr.findByGprnSubProcessIdAndMaterialCode(
//                         Integer.parseInt(req.getGprnNo().split("/")[1]), gmdd.getMaterialCode());

//                 if (gicdeOpt.isPresent()) {
//                     errorMessage.append("Inspection already done for the provided GPRN No. " + req.getGprnNo()
//                             + " and Material Code " + gmdd.getMaterialCode());
//                     errorFound = true;
//                     continue;
//                 } else if (!gicdeOpt.isPresent()
//                         && (gmdd.getReceivedQuantity()
//                                 .compareTo(gmdd.getAcceptedQuantity().add(gmdd.getRejectedQuantity())) != 0)) {
//                     errorMessage.append("Total received quantity for " + gmdd.getMaterialCode()
//                             + " is not equal to accepted quantity + rejected quantity.");
//                     errorFound = true;
//                     continue;
//                 }

//                 GoodsInspectionConsumableDetailEntity gicde = new GoodsInspectionConsumableDetailEntity();
//                 mapper.map(gmdd, gicde);

//                 gicde.setInspectionSubProcessId(gime.getInspectionSubProcessId());
//                 gicde.setGprnSubProcessId(Integer.parseInt(req.getGprnNo().split("/")[1]));
//                 gicde.setGprnProcessId(Integer.parseInt(req.getGprnNo().split("/")[0].substring(3)));

//                 try {
//                     String instlRepFileName = CommonUtils.saveBase64Image(gmdd.getInstallationReportBase64(), basePath);
//                     gicde.setInstallationReportFilename(instlRepFileName);
//                   //  System.out.print("Install report for con"+ instlRepFileName);
//                 } catch (Exception e) {
//                     // Log error
//                 }

//                 gicdeList.add(gicde);
//             } else {
//                 Optional<GiMaterialDtlEntity> gimdeOpt = gimdr.findByGprnSubProcessIdAndMaterialCode(
//                         Integer.parseInt(req.getGprnNo().split("/")[1]), gmdd.getMaterialCode());

//                 if (gimdeOpt.isPresent()) {
//                     errorMessage.append("Inspection already done for the provided GPRN No. " + req.getGprnNo()
//                             + " and Material Code " + gmdd.getMaterialCode());
//                     errorFound = true;
//                     continue;
//                 } else if (!gimdeOpt.isPresent()
//                         && (gmdd.getReceivedQuantity()
//                                 .compareTo(gmdd.getAcceptedQuantity().add(gmdd.getRejectedQuantity())) != 0)) {
//                     errorMessage.append("Total received quantity for " + gmdd.getMaterialCode()
//                             + " is not equal to accepted quantity + rejected quantity.");
//                     errorFound = true;
//                     continue;
//                 }

//                // Integer assetId = null;
//                 NewAssetResponseDto asset = null;
//                 if (gmdd.getAcceptedQuantity().compareTo(BigDecimal.ZERO) >= 0) {
//                   //  assetId = createNewAsset(gmdd, req.getCreatedBy(), gprnDto.getPoId());
//                      asset = createNewAsset(gmdd, req.getCreatedBy(), gprnDto.getPoId(), gprnDto.getLocationId());
//                 }

//                 System.out.println(asset);
//                 GiMaterialDtlEntity gimde = new GiMaterialDtlEntity();
//                 mapper.map(gmdd, gimde);
//                // gimde.setAssetId(assetId);
//                 if (asset != null) {
//                     gimde.setAssetId(asset.getAssetId());
//                     gimde.setAssetCode(asset.getAssetCode());
//                 }
//                 gimde.setInspectionSubProcessId(gime.getInspectionSubProcessId());
//                 gimde.setGprnSubProcessId(Integer.parseInt(req.getGprnNo().split("/")[1]));
//                 gimde.setGprnProcessId(Integer.parseInt(req.getGprnNo().split("/")[0].substring(3)));

//                 try {
//                     String instlRepFileName = CommonUtils.saveBase64Image(gmdd.getInstallationReportBase64(), basePath);
//                     gimde.setInstallationReportFileName(instlRepFileName);

//                  //   System.out.print("installing report:"+ instlRepFileName);
//                 } catch (Exception e) {
//                     // Log error
//                 }

//                 gimdeList.add(gimde);
//             }
//         }

//         if (errorFound) {
//             throw new InvalidInputException(new ErrorDetails(
//                     AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     errorMessage.toString()));
//         }

//         gimdr.saveAll(gimdeList);
//         gicdr.saveAll(gicdeList);
//         GiWorkflowStatus workflowStatus = new GiWorkflowStatus();
//         workflowStatus.setProcessId("INV" + gime.getGprnProcessId());
//         workflowStatus.setSubProcessId(gime.getInspectionSubProcessId());
//         workflowStatus.setAction("Created");
//         workflowStatus.setRemarks("GI Created");
//         workflowStatus.setCreatedBy(req.getCreatedBy());
//         workflowStatus.setCreateDate(LocalDateTime.now());

//         gistausRepo.save(workflowStatus);

//         return "INV" + gime.getGprnProcessId() + "/" + gime.getInspectionSubProcessId();
//     }

//     @Override
//     public Map<String, Object> getGiDtls(String processNo) {
//         ModelMapper mapper = new ModelMapper();
//         String[] processNoSplit = processNo.split("/");

//         if (processNoSplit.length != 2) {
//             throw new InvalidInputException(new ErrorDetails(
//                     AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     "Invalid process ID"));
//         }

//         Integer inspectionId = Integer.parseInt(processNoSplit[1]);
//         GiMasterEntity gime = gimr.findById(inspectionId)
//                 .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
//                         AppConstant.ERROR_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_RESOURCE,
//                         "Goods Inspection not found for the provided process ID.")));

//         List<GiMaterialDtlEntity> gimdeList = gimdr.findByInspectionSubProcessId(gime.getInspectionSubProcessId());
//         List<GoodsInspectionConsumableDetailEntity> gicdeList = gicdr
//                 .findByInspectionSubProcessId(gime.getInspectionSubProcessId());
//         List<GiMaterialDtlDto> materialDtlListRes = gicdeList.stream()
//                 .map(gicde -> {
//                     GiMaterialDtlDto gmdd = mapper.map(gicde, GiMaterialDtlDto.class);

//                     try {
//                         String imageBase64 = CommonUtils.convertImageToBase64(gicde.getInstallationReportFilename(),
//                                 basePath);
//                         gmdd.setInstallationReportBase64(imageBase64);
//                         gmdd.setInstallationReportFileName(gicde.getInstallationReportFilename());
//                     } catch (Exception e) {
//                         // Log error
//                     }
//                     return gmdd;
//                 }).collect(Collectors.toList());

//         List<GiMaterialDtlDto> materialDtlListRes1 = gimdeList.stream()
//                 .map(gimde -> {
//                     GiMaterialDtlDto gmdd = mapper.map(gimde, GiMaterialDtlDto.class);
//                     gmdd.setAssetId(gimde.getAssetId());
//                     gmdd.setRejectReason(gimde.getRejectReason());

//                     gmdd.setAssetCode(gimde.getAssetCode());
//                     gmdd.setInstallationReportFileName(gimde.getInstallationReportFileName());
//                     Optional<AssetMasterEntity> aeOpt = amr.findById(gimde.getAssetId());
//                     if (aeOpt.isPresent()) {
//                         gmdd.setAssetDesc(aeOpt.get().getAssetDesc());
//                         gmdd.setUomId(aeOpt.get().getUomId());
//                     }

//                     try {
//                         String imageBase64 = CommonUtils.convertImageToBase64(gimde.getInstallationReportFileName(),
//                                 basePath);
//                         gmdd.setInstallationReportBase64(imageBase64);
//                     } catch (Exception e) {
//                         // Log error
//                     }
//                     return gmdd;
//                 })
//                 .collect(Collectors.toList());

//         SaveGiDto giRes = new SaveGiDto();
//         giRes.setInspectionNo(processNo);
//         giRes.setGprnNo(processNo);
//         giRes.setInstallationDate(CommonUtils.convertDateToString(gime.getInstallationDate()));
//         giRes.setCommissioningDate(CommonUtils.convertDateToString(gime.getCommissioningDate()));
//         giRes.setGprnAmount(gime.getGprnAmount());
//         giRes.setPoAmount(gime.getPoAmount());
//         materialDtlListRes.addAll(materialDtlListRes1);
//         giRes.setMaterialDtlList(materialDtlListRes);

//         Map<String, Object> combinedRes = new HashMap<>();
//         combinedRes.put("giDtls", giRes);
//         combinedRes.put("gprnDtls",
//                 gprnService.getGprnDtls(processNo.split("/")[0] + "/" + gime.getGprnSubProcessId()));
// //System.out.print(combinedRes);
//         return combinedRes;
//     }

//     private NewAssetResponseDto createNewAsset(GiMaterialDtlDto materialDtl, Integer createdBy, String poId, String locationId) {
//         MaterialMaster mme = mmr.findById(materialDtl.getMaterialCode())
//                 .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
//                         AppConstant.ERROR_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_RESOURCE,
//                         "Material not found for the provided material code.")));
//         Optional<AssetMasterEntity> ameOpt = amr
//                 .findByMaterialCodeAndMaterialDescAndMakeNoAndModelNoAndSerialNoAndUomIdAndPoId(
//                         materialDtl.getMaterialCode(),
//                         materialDtl.getMaterialDesc(),
//                         materialDtl.getMakeNo(),
//                         materialDtl.getModelNo(),
//                         materialDtl.getSerialNo(),
//                         materialDtl.getUomId(),
//                         poId);

//         if (ameOpt.isEmpty()) {
//             AssetMasterEntity ame = new ModelMapper().map(materialDtl, AssetMasterEntity.class);
//             ame.setAssetDesc(materialDtl.getMaterialDesc());
//             ame.setCreateDate(LocalDateTime.now());
//             ame.setCreatedBy(createdBy);
//             ame.setUpdatedDate(LocalDateTime.now());
//             ame.setUnitPrice(mme.getUnitPrice());
//             ame.setPoId(poId);


//            // String subCategory = materialDetailsRepository.findSubCategoryByMaterialCode(materialDtl.getMaterialCode());

//             String fieldStation = locationId;
//             String subCat = mme.getSubCategory().substring(0, 3);
//             String assetCode = generateAssetCode(fieldStation, subCat);
//             ame.setAssetCode(assetCode);
            
//             amr.save(ame);
//             //   return ame.getAssetId();
//             return new NewAssetResponseDto(ame.getAssetId(), ame.getAssetCode());
//         }

//        // return ameOpt.get().getAssetId();
//         AssetMasterEntity existing = ameOpt.get();
//         return new NewAssetResponseDto(existing.getAssetId(), existing.getAssetCode());
//     }

//  /*   private String generateAssetCode(String fieldStation, String subCategory) {
//         String financialYear = getFinancialYear();
//         String prefix = fieldStation + subCategory + financialYear + "-";

//         String maxAssetCode = amr.findMaxAssetCodeByPrefix(prefix);
//         int nextSeq = 1;
//         if (maxAssetCode != null) {
//             String seqPart = maxAssetCode.substring(maxAssetCode.lastIndexOf('-') + 1);
//             nextSeq = Integer.parseInt(seqPart) + 1;
//         }

//         return prefix + String.format("%03d", nextSeq);
//     }*/
//  private String generateAssetCode(String fieldStation, String subCategory) {
//      String financialYear = getFinancialYear();
//      String prefix = (fieldStation + subCategory + financialYear + "-").toUpperCase();

//      //  Get the current max asset_id from DB
//      Integer maxAssetId = amr.findMaxAssetId();
//      int nextSeq = (maxAssetId != null ? maxAssetId + 1 : 1);

//      return prefix + String.format("%03d", nextSeq);
//  }


//     private String getFinancialYear() {
//         LocalDate today = LocalDate.now();
//         int startYear = today.getMonthValue() >= 4 ? today.getYear() % 100 : (today.getYear() - 1) % 100;
//         int endYear = (startYear + 1) % 100;
//         return String.format("%02d%02d", startYear, endYear);
//     }


//     @Override
//     public void validateGiIsApproved(String processNo) {
//         String[] processNoSplit = processNo.split("/");
//         if (processNoSplit.length != 2) {
//             throw new InvalidInputException(new ErrorDetails(
//                     AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     "Invalid GI No."));
//         }

//         // Integer processId = Integer.parseInt(processNoSplit[0].substring(3));
//         String processId = processNoSplit[0].substring(3);
//         Integer subProcessId = Integer.parseInt(processNoSplit[1]);

//         // GiMasterEntity giMaster =
//         // gimr.findByGprnProcessIdAndInspectionSubProcessId(processId, subProcessId)
//         GiMasterEntity giMaster = gimr.findByGprnProcessIdAndInspectionSubProcessId(processId, subProcessId)

//                 .orElseThrow(() -> new BusinessException(new ErrorDetails(
//                         AppConstant.ERROR_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_RESOURCE,
//                         "Provided GI No. is not valid.")));

//         if (!"APPROVED".equalsIgnoreCase(giMaster.getStatus())) {
//             throw new InvalidInputException(new ErrorDetails(
//                     AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     "GI is not approved. Cannot create GRN."));
//         }
//     }

//     @Override
//     public void validateGiSubProcessId(String processNo) {
//         String[] processNoSplit = processNo.split("/");
//         if (processNoSplit.length != 2) {
//             throw new InvalidInputException(new ErrorDetails(
//                     AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     "Invalid GI No."));
//         }

//         Integer subProcessId = Integer.parseInt(processNoSplit[1]);
//         if (!gimr.existsById(subProcessId)) {
//             throw new BusinessException(new ErrorDetails(
//                     AppConstant.ERROR_CODE_RESOURCE,
//                     AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                     AppConstant.ERROR_TYPE_RESOURCE,
//                     "Provided GI No. is not valid."));
//         }

//     }

//     public List<GprnPendingInspectionDto> getGiStatusWise(String status, Optional<String> createdBy) {
//         // List<Object[]> results = gimr.getGiStatusWise(status);
//         List<Object[]> results = new ArrayList<>();
//         if (createdBy.isPresent()) {
//             results = gimr.getGiStatusWiseAndCreatedBy(status, createdBy.get());
//         } else {
//             results = gimr.getGiStatusWise(status);
//         }
//         ObjectMapper mapper = new ObjectMapper();
//         mapper.registerModule(new JavaTimeModule());
//         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

//         return results.stream().map(row -> {
//             GprnPendingInspectionDto dto = new GprnPendingInspectionDto();
//             dto.setProcessId((String) row[0]);
//             dto.setSubProcessId((Integer) row[1]);
//             dto.setPoId((String) row[2]);
//             dto.setLocationId((String) row[3]);
//             dto.setDate(CommonUtils.convertSqlDateToString((Date) row[4]));
//             dto.setChallanNo((String) row[5]);
//             dto.setDeliveryDate(CommonUtils.convertSqlDateToString((Date) row[6]));
//             dto.setVendorId((String) row[7]);
//             dto.setFieldStation((String) row[8]);
//             dto.setIndentorName((String) row[9]);
//             dto.setSupplyExpectedDate(CommonUtils.convertSqlDateToString((Date) row[10]));
//             dto.setConsigneeDetail((String) row[11]);
//             dto.setWarrantyYears((BigDecimal) row[12]);
//             dto.setProject((String) row[13]);
//             dto.setReceivedBy((String) row[14]);

//             try {
//                 String detailsJson = (String) row[15];
//                 if (detailsJson != null && !detailsJson.isEmpty()) {
//                     List<GprnPendingInspectionDetailDto> details = mapper.readValue(
//                             detailsJson,
//                             mapper.getTypeFactory().constructCollectionType(
//                                     List.class,
//                                     GprnPendingInspectionDetailDto.class));
//                     dto.setMaterialDetails(details);
//                 } else {
//                     dto.setMaterialDetails(new ArrayList<>());
//                 }
//             } catch (Exception e) {
//                 dto.setMaterialDetails(new ArrayList<>());
//             }

//             dto.setStatus((String) row[16]);

//             return dto;
//         }).collect(Collectors.toList());
//     }

//     @Override
//     @Transactional
//     public void approveGi(GiApprovalDto req) {
//         // updateGiStatusAndRemarks(req, "APPROVED");
//         updateGiStatusAndRemarks(req);

//         updatePoBasedonRejectionType(req);
//         giMailSender(req.getProcessNo());

//     }

//     public String giMailSender(String processNumber) {
//         try {
//             String[] parts = processNumber.split("/");
//             Integer inspectionSubProcessId = Integer.parseInt(parts[1]);


//             GiMasterEntity giMaster = gimr.findById(inspectionSubProcessId)
//                     .orElseThrow(() -> new BusinessException(new ErrorDetails(
//                             AppConstant.ERROR_CODE_RESOURCE,
//                             AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                             AppConstant.ERROR_TYPE_RESOURCE,
//                             "GI not found")));

//             Integer gprnSubProcessId = giMaster.getGprnSubProcessId();

//             GprnMasterEntity gprnMaster = gprnMasterRepository.findById(gprnSubProcessId)
//                     .orElseThrow(() -> new BusinessException(new ErrorDetails(
//                             AppConstant.ERROR_CODE_RESOURCE,
//                             AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                             AppConstant.ERROR_TYPE_RESOURCE,
//                             "Gprn not found")));




//          UserMaster um = userMasterRepository.findByUserId(Integer.valueOf(gprnMaster.getReceivedBy()));

//             String custodainName= um.getUserName();
//             Integer custodainId= Integer.valueOf(gprnMaster.getReceivedBy());
//             String   emailId = um.getEmail();
//             emailService.sendGiMails(
//                     emailId,
//                     custodainName,
//                     custodainId,
//                     String.valueOf(giMaster.getInspectionSubProcessId()),
//                     giMaster.getGprnProcessId(),
//                     giMaster.getStatus()
//             );

//         } catch (Exception e) {
//             return "Invalid process number format.";
//         }
//         return  "";
//     }
// /*
//     private void updatePoBasedonRejectionType(GiApprovalDto req){
//         String[] processNoSplit = req.getProcessNo().split("/");
//         String poId = "PO" + processNoSplit[0].substring(3);
//         Integer inspectionId = Integer.parseInt(processNoSplit[1]);
//         List<GiMaterialDtlEntity> gimdeList = gimdr.findByInspectionSubProcessId(inspectionId);
//         List <GoodsInspectionConsumableDetailEntity> gicdeList = gicdr.findByInspectionSubProcessId(inspectionId);

//         for(GiMaterialDtlEntity gimde : gimdeList){
//             System.out.println("ADDED AGAIN NON CONSUMABLE");
//             String rejectionType = gimde.getRejectionType();
//          //  if(gimde.getRejectionType().equalsIgnoreCase("replacement")){
//             if ("replacement".equalsIgnoreCase(rejectionType)) {
//                 // add the quantity to po again
//             PurchaseOrderAttributes poa = poar.findByPurchaseOrder_PoIdAndMaterialCode(poId, gimde.getMaterialCode())
//                                         .orElseThrow(() -> new BusinessException(new ErrorDetails(
//                                                 AppConstant.ERROR_CODE_RESOURCE,
//                                                 AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                                                 AppConstant.ERROR_TYPE_RESOURCE,
//                                                 "Purchase Order not found")));
//             poa.setReceivedQuantity(poa.getReceivedQuantity().subtract(gimde.getRejectedQuantity()));
//             poar.save(poa);
//            }
//         }
//         for(GoodsInspectionConsumableDetailEntity gicde : gicdeList){
//             System.out.println("ADDED AGAIN CONSUMABLE");
//            // if(gicde.getRejectionType().equalsIgnoreCase("replacement")){
//             String rejectionType = gicde.getRejectionType();
//             if ("replacement".equalsIgnoreCase(rejectionType)) {
//                 // add the quantity to po again
//                 PurchaseOrderAttributes poa = poar.findByPurchaseOrder_PoIdAndMaterialCode(poId, gicde.getMaterialCode())
//                                         .orElseThrow(() -> new BusinessException(new ErrorDetails(
//                                                 AppConstant.ERROR_CODE_RESOURCE,
//                                                 AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                                                 AppConstant.ERROR_TYPE_RESOURCE,
//                                                 "Purchase Order not found")));
//                 poa.setReceivedQuantity(poa.getReceivedQuantity().subtract(gicde.getRejectedQuantity()));
//                 poar.save(poa);
//             }
//         }
//     }
// */
// private void updatePoBasedonRejectionType(GiApprovalDto req) {

//     String[] processNoSplit = req.getProcessNo().split("/");
//     String poId = "PO" + processNoSplit[0].substring(3);
//     Integer inspectionId = Integer.parseInt(processNoSplit[1]);

//     List<GiMaterialDtlEntity> gimdeList = gimdr.findByInspectionSubProcessId(inspectionId);
//     List<GoodsInspectionConsumableDetailEntity> gicdeList = gicdr.findByInspectionSubProcessId(inspectionId);

//     for (GiMaterialDtlEntity gimde : gimdeList) {

//         if ("replacement".equalsIgnoreCase(gimde.getRejectionType())) {

//             PurchaseOrderAttributes poa = poar
//                     .findByPurchaseOrder_PoIdAndMaterialCode(poId, gimde.getMaterialCode())
//                     .orElseThrow(() -> new BusinessException(new ErrorDetails(
//                             AppConstant.ERROR_CODE_RESOURCE,
//                             AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                             AppConstant.ERROR_TYPE_RESOURCE,
//                             "Purchase Order not found")));

//             BigDecimal receivedQty = poa.getReceivedQuantity() != null
//                     ? poa.getReceivedQuantity()
//                     : BigDecimal.ZERO;

//             BigDecimal rejectedQty = gimde.getRejectedQuantity() != null
//                     ? gimde.getRejectedQuantity()
//                     : BigDecimal.ZERO;

//             poa.setReceivedQuantity(receivedQty.subtract(rejectedQty));

//             poar.save(poa);
//         }
//     }

//     for (GoodsInspectionConsumableDetailEntity gicde : gicdeList) {

//         if ("replacement".equalsIgnoreCase(gicde.getRejectionType())) {

//             PurchaseOrderAttributes poa = poar
//                     .findByPurchaseOrder_PoIdAndMaterialCode(poId, gicde.getMaterialCode())
//                     .orElseThrow(() -> new BusinessException(new ErrorDetails(
//                             AppConstant.ERROR_CODE_RESOURCE,
//                             AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                             AppConstant.ERROR_TYPE_RESOURCE,
//                             "Purchase Order not found")));

//             BigDecimal receivedQty = poa.getReceivedQuantity() != null
//                     ? poa.getReceivedQuantity()
//                     : BigDecimal.ZERO;

//             BigDecimal rejectedQty = gicde.getRejectedQuantity() != null
//                     ? gicde.getRejectedQuantity()
//                     : BigDecimal.ZERO;

//             poa.setReceivedQuantity(receivedQty.subtract(rejectedQty));

//             poar.save(poa);
//         }
//     }
// }


//     @Override
//     @Transactional
//     public void rejectGi(GiApprovalDto req) {
//         // updateGiStatusAndRemarks(req, "REJECTED");
//         updateGiStatusAndRemarks(req);

//         giMailSender(req.getProcessNo());
//     }

//     @Override
//     @Transactional
//     public void changeReqGi(GiApprovalDto req) {
//         // updateGiStatusAndRemarks(req, "CHANGE REQUEST");
//         updateGiStatusAndRemarks(req);

//         giMailSender(req.getProcessNo());
//     }

//     private void updateGiStatusAndRemarks(GiApprovalDto req) {
//         String[] processNoSplit = req.getProcessNo().split("/");
//         if (processNoSplit.length != 2) {
//             throw new InvalidInputException(new ErrorDetails(
//                     AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     "Invalid process number format"));
//         }

//         Integer inspectionId = Integer.parseInt(processNoSplit[1]);
//         GiMasterEntity giMaster = gimr.findById(inspectionId)
//                 .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
//                         AppConstant.ERROR_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_RESOURCE,
//                         "Goods Inspection not found")));

//         giMaster.setStatus(req.getStatus());
//         gimr.save(giMaster);

//         GiWorkflowStatus history = new GiWorkflowStatus();
//         history.setProcessId(processNoSplit[0]);
//         history.setSubProcessId(inspectionId);
//         history.setAction(req.getStatus());
//         history.setRemarks(req.getRemarks());
//         history.setCreatedBy(req.getCreatedBy());
//         history.setCreateDate(LocalDateTime.now());

//         gistausRepo.save(history);
//     }

//     public List<GiMasterEntity> getGiByStatuses() {
//         List<String> statuses = Arrays.asList("AWAITING APPROVAL");
//         return gimr.findByStatusIn(statuses);
//     }

//     public List<GiMasterEntity> getGiByIndentorStatuses() {
//         List<String> statuses = Arrays.asList("REJECTED", "CHANGE REQUEST");
//         return gimr.findByStatusIn(statuses);
//     }

//     @Override
//     @Transactional
//     public String updateGi(SaveGiDto req) {
//         String[] processNoSplit = req.getGprnNo().split("/");
//         if (processNoSplit.length != 2) {
//             throw new InvalidInputException(new ErrorDetails(
//                     AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     "Invalid process number format"));
//         }

//         Integer subProcessId = Integer.parseInt(processNoSplit[1]);
//         GiMasterEntity gime = gimr.findByGprnSubProcessId(subProcessId)
//                 .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
//                         AppConstant.ERROR_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                         AppConstant.ERROR_TYPE_RESOURCE,
//                         "GI Master not found for given GPRN No.")));

//         gime.setCommissioningDate(CommonUtils.convertStringToDateObject(req.getCommissioningDate()));
//         gime.setInstallationDate(CommonUtils.convertStringToDateObject(req.getInstallationDate()));
//         gime.setLocationId(req.getLocationId());
//         gime.setStatus("AWAITING APPROVAL");
//         gimr.save(gime);

//         ModelMapper mapper = new ModelMapper();
//         StringBuilder errorMessage = new StringBuilder();
//         boolean errorFound = false;

//         for (GiMaterialDtlDto gmdd : req.getMaterialDtlList()) {

//             Optional<GoodsInspectionConsumableDetailEntity> gicdeOpt = gicdr
//                     .findByGprnSubProcessIdAndMaterialCode(subProcessId, gmdd.getMaterialCode());

//             if (gicdeOpt.isPresent()) {
//                 // Consumable
//                 GoodsInspectionConsumableDetailEntity gicde = gicdeOpt.get();
//                 mapper.map(gmdd, gicde);

//                 if (!gmdd.getReceivedQuantity().equals(gmdd.getAcceptedQuantity().add(gmdd.getRejectedQuantity()))) {
//                     errorMessage.append("Received quantity mismatch for " + gmdd.getMaterialCode() + ". ");
//                     errorFound = true;
//                     continue;
//                 }

//                 try {
//                     String fileName = CommonUtils.saveBase64Image(gmdd.getInstallationReportBase64(), basePath);
//                     gicde.setInstallationReportFilename(fileName);
//                 } catch (Exception e) {
//                     // log error
//                 }
//                 gicdr.save(gicde);
//                 continue;
//             }

//             Optional<GiMaterialDtlEntity> gimdeOpt = gimdr.findByGprnSubProcessIdAndMaterialCode(subProcessId,
//                     gmdd.getMaterialCode());

//             if (gimdeOpt.isPresent()) {
//                 // Non-Consumable
//                 GiMaterialDtlEntity gimde = gimdeOpt.get();
//                 mapper.map(gmdd, gimde);

//                 if (!gmdd.getReceivedQuantity().equals(gmdd.getAcceptedQuantity().add(gmdd.getRejectedQuantity()))) {
//                     errorMessage.append("Received quantity mismatch for " + gmdd.getMaterialCode() + ". ");
//                     errorFound = true;
//                     continue;
//                 }

//                 if (gmdd.getAcceptedQuantity().compareTo(BigDecimal.ZERO) > 0 && gimde.getAssetId() == null) {
//                     SaveGprnDto gprnDto = gprnService.getGprnDtls(req.getGprnNo());
//                   //  gimde.setAssetId(createNewAsset(gmdd, req.getCreatedBy(), gprnDto.getPoId()));
//                   NewAssetResponseDto asset =  createNewAsset(gmdd, req.getCreatedBy(), gprnDto.getPoId(), gprnDto.getLocationId());
//                   gimde.setAssetCode(asset.getAssetCode());
//                   gimde.setAssetId(asset.getAssetId());
//                 }

//                 try {
//                     String fileName = CommonUtils.saveBase64Image(gmdd.getInstallationReportBase64(), basePath);
//                     gimde.setInstallationReportFileName(fileName);
//                 } catch (Exception e) {
//                     // log error
//                 }
//                 gimdr.save(gimde);
//                 continue;
//             }

//             // If neither found
//             errorMessage.append("Material Code " + gmdd.getMaterialCode() + " not found in GI tables. ");
//             errorFound = true;
//         }

//         if (errorFound) {
//             throw new InvalidInputException(new ErrorDetails(
//                     AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     errorMessage.toString()));
//         }
//         GiWorkflowStatus workflowStatus = new GiWorkflowStatus();
//         workflowStatus.setProcessId("INV" + gime.getGprnProcessId());
//         workflowStatus.setSubProcessId(gime.getInspectionSubProcessId());
//         workflowStatus.setAction("UPDATED");
//         workflowStatus.setRemarks("GI updated");
//         workflowStatus.setCreatedBy(req.getCreatedBy());
//         workflowStatus.setCreateDate(LocalDateTime.now());

//         gistausRepo.save(workflowStatus);

//         return "INV" + gime.getGprnProcessId() + "/" + gime.getInspectionSubProcessId();
//     }

//     public List<GiWorkflowStatusDto> getGiHistoryByProcessId(String processId, Integer subProcessId) {
//         List<GiWorkflowStatus> historyList = gistausRepo.findByProcessIdAndSubProcessIdOrderByIdAsc(processId,
//                 subProcessId);
//         return historyList.stream().map(status -> {
//             GiWorkflowStatusDto dto = new GiWorkflowStatusDto();
//             dto.setProcessId(status.getProcessId());
//             dto.setSubProcessId(status.getSubProcessId());
//             dto.setAction(status.getAction());
//             dto.setRemarks(status.getRemarks());
//             dto.setCreatedBy(status.getCreatedBy());
//             dto.setCreateDate(status.getCreateDate());
//             return dto;
//         }).toList();
//     }

//     public List<GprnDropdownDto> getPendingGprnsForGI() {
//         List<GprnMasterEntity> pendingGprns = gprnMasterRepository.findPendingGprnsWithMaterial();

//         return pendingGprns.stream()
//                 .map(g -> new GprnDropdownDto(
//                         g.getSubProcessId(),
//                       "INV" + g.getProcessId() + "/" + g.getSubProcessId(),
//                         g.getPoId(),
//                         g.getVendorId(),
//                         gprnMaterialDtlRepository.findMaterialDescriptionsBySubProcessId(g.getSubProcessId())
//                 ))
//                 .collect(Collectors.toList());
//     }
// /*
//     public List<GprnDropdownDto> getPendingRejectedGis() {

//         List<GprnDropdownDto> normalGis = gimdr.findByRejectionType("replacement")
//                 .stream()
//                 .filter(gi -> {
//                     String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
//                     return !ogpMasterRejectedGiRepository.existsByGiId(giNo);
//                 })
//                 .map(gi -> {
//                     String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
//                     List<String> materialList = gimdr
//                             .findMaterialDescriptionsByInspectionSubProcessId(gi.getInspectionSubProcessId());

//                     GprnPoVendorDto gprnDto = gprnMasterRepository.findPoIdAndVendorIdBySubProcessId(gi.getGprnSubProcessId());

//                     return new GprnDropdownDto(
//                             gi.getInspectionSubProcessId(),
//                             giNo,
//                             gprnDto.getPoId(),
//                             gprnDto.getVendorId(),
//                             materialList
//                     );
//                 }).collect(Collectors.toList());

//         List<GprnDropdownDto> consumableGis = gicdr.findByRejectionType("replacement")
//                 .stream()
//                 .filter(gi -> {
//                     String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
//                     return !ogpMasterRejectedGiRepository.existsByGiId(giNo);
//                 })
//                 .map(gi -> {
//                     String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
//                     List<String> materialList = gicdr
//                             .findMaterialDescriptionsByInspectionSubProcessId(gi.getInspectionSubProcessId());

//                     GprnPoVendorDto gprnDto = gprnMasterRepository.findPoIdAndVendorIdBySubProcessId(gi.getGprnSubProcessId());

//                     return new GprnDropdownDto(
//                             gi.getInspectionSubProcessId(),
//                             giNo,
//                             gprnDto.getPoId(),
//                             gprnDto.getVendorId(),
//                             materialList
//                     );
//                 }).collect(Collectors.toList());

//         // Combine both lists
//         normalGis.addAll(consumableGis);

//         return normalGis;
//     }*/
// public List<GprnDropdownDto> getPendingRejectedGis() {

//     // Fetch replacement + permanent for NORMAL
//     List<GiMaterialDtlEntity> normalEntities = new ArrayList<>();
//     normalEntities.addAll(gimdr.findByRejectionType("replacement"));
//     normalEntities.addAll(gimdr.findByRejectionType("permanent"));

//     List<GprnDropdownDto> normalGis = normalEntities.stream()
//             .filter(gi -> {
//                 String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
//                 return !ogpMasterRejectedGiRepository.existsByGiId(giNo);
//             })
//             .map(gi -> {
//                 String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
//                 List<String> materialList = gimdr
//                         .findMaterialDescriptionsByInspectionSubProcessId(gi.getInspectionSubProcessId());

//                 GprnPoVendorDto gprnDto = gprnMasterRepository
//                         .findPoIdAndVendorIdBySubProcessId(gi.getGprnSubProcessId());

//                 return new GprnDropdownDto(
//                         gi.getInspectionSubProcessId(),
//                         giNo,
//                         gprnDto.getPoId(),
//                         gprnDto.getVendorId(),
//                         materialList
//                 );
//             }).collect(Collectors.toList());



//     // Fetch replacement + permanent for CONSUMABLE
//     List<GoodsInspectionConsumableDetailEntity> consumableEntities = new ArrayList<>();
//     consumableEntities.addAll(gicdr.findByRejectionType("replacement"));
//     consumableEntities.addAll(gicdr.findByRejectionType("permanent"));

//     List<GprnDropdownDto> consumableGis = consumableEntities.stream()
//             .filter(gi -> {
//                 String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
//                 return !ogpMasterRejectedGiRepository.existsByGiId(giNo);
//             })
//             .map(gi -> {
//                 String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
//                 List<String> materialList = gicdr
//                         .findMaterialDescriptionsByInspectionSubProcessId(gi.getInspectionSubProcessId());

//                 GprnPoVendorDto gprnDto = gprnMasterRepository
//                         .findPoIdAndVendorIdBySubProcessId(gi.getGprnSubProcessId());

//                 return new GprnDropdownDto(
//                         gi.getInspectionSubProcessId(),
//                         giNo,
//                         gprnDto.getPoId(),
//                         gprnDto.getVendorId(),
//                         materialList
//                 );
//             }).collect(Collectors.toList());


//     normalGis.addAll(consumableGis);

//     return normalGis;
// }





// }





package com.astro.service.impl.InventoryModule;

import com.astro.dto.workflow.InventoryModule.AssetResponseDto;
import com.astro.dto.workflow.InventoryModule.GprnDropdownDto;
import com.astro.dto.workflow.InventoryModule.GprnPoVendorDto;
import com.astro.dto.workflow.NewAssetResponseDto;
import com.astro.entity.UserMaster;
import com.astro.repository.InventoryModule.GprnRepository.GprnMasterRepository;
import com.astro.repository.InventoryModule.GprnRepository.GprnMaterialDtlRepository;
import com.astro.repository.InventoryModule.isn.IssueNoteMasterRepository;
import com.astro.repository.InventoryModule.ogp.OgpMasterRejectedGiRepository;
import com.astro.repository.ProcurementModule.IndentCreation.MaterialDetailsRepository;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderAttributesRepository;

import com.astro.repository.UserMasterRepository;
import com.astro.util.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;

import com.astro.service.InventoryModule.GiService;
import com.astro.service.InventoryModule.GprnService;
import com.astro.repository.InventoryModule.GiRepository.*;
import com.astro.repository.MaterialMasterRepository;
import com.astro.repository.InventoryModule.AssetMasterRepository;
import com.astro.repository.InventoryModule.GoodsInspectionConsumableDetailRepository;
import com.astro.entity.MaterialMaster;
import com.astro.entity.InventoryModule.*;
import com.astro.entity.ProcurementModule.PurchaseOrderAttributes;
import com.astro.dto.workflow.InventoryModule.GiDto.*;
import com.astro.dto.workflow.InventoryModule.GprnDto.SaveGprnDto;
import com.astro.dto.workflow.InventoryModule.gprn.GprnPendingInspectionDetailDto;
import com.astro.dto.workflow.InventoryModule.gprn.GprnPendingInspectionDto;
import com.astro.exception.*;
import com.astro.constant.AppConstant;
import com.astro.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.modelmapper.ModelMapper;

@Service
public class GiServiceImpl implements GiService {
    @Value("${filePath}")
    private String bp;

    @Autowired
    private GiMasterRepository gimr;

    @Autowired
    private GiMaterialDtlRepository gimdr;

    @Autowired
    private GprnService gprnService;

    @Autowired
    private AssetMasterRepository amr;

    @Autowired
    private MaterialMasterRepository mmr;

    @Autowired
    private IssueNoteMasterRepository issueNoteMasterRepository;

    @Autowired
    private GoodsInspectionConsumableDetailRepository gicdr;
    @Autowired
    private GiWorkflowStatusRepository gistausRepo;

    @Autowired
    private PurchaseOrderAttributesRepository poar;
    @Autowired
    private GprnMasterRepository gprnMasterRepository;
    @Autowired
    private GprnMaterialDtlRepository gprnMaterialDtlRepository;
    @Autowired
    private OgpMasterRejectedGiRepository ogpMasterRejectedGiRepository;
    @Autowired
    private MaterialDetailsRepository materialDetailsRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;
    @Autowired
    private EmailService emailService;

    private final String basePath;

    public GiServiceImpl(@Value("${filePath}") String bp) {
        this.basePath = bp + "/INV";
    }

    @Override
    @Transactional
    public String saveGi(SaveGiDto req) {
        gprnService.validateGprnSubProcessId(req.getGprnNo());
        SaveGprnDto gprnDto = gprnService.getGprnDtls(req.getGprnNo());

        ModelMapper mapper = new ModelMapper();
        GiMasterEntity gime = new GiMasterEntity();
        gime.setCommissioningDate(CommonUtils.convertStringToDateObject(req.getCommissioningDate()));
        gime.setInstallationDate(CommonUtils.convertStringToDateObject(req.getCommissioningDate()));
        gime.setGprnProcessId(req.getGprnNo().split("/")[0].substring(3));
        gime.setGprnSubProcessId(Integer.parseInt(req.getGprnNo().split("/")[1]));
        gime.setCreateDate(LocalDateTime.now());
        gime.setCreatedBy(req.getCreatedBy());
        gime.setLocationId(req.getLocationId());
        gime.setStatus("AWAITING APPROVAL");
        gime.setGprnAmount(req.getGprnAmount());
        gime.setPoAmount(req.getPoAmount());

        gime = gimr.save(gime);

        List<GiMaterialDtlEntity> gimdeList = new ArrayList<>();
        List<GoodsInspectionConsumableDetailEntity> gicdeList = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();
        Boolean errorFound = false;

        for (GiMaterialDtlDto gmdd : req.getMaterialDtlList()) {

            if (gmdd.getCategory().equalsIgnoreCase("consumable")) {
                Optional<GoodsInspectionConsumableDetailEntity> gicdeOpt = gicdr.findByGprnSubProcessIdAndMaterialCode(
                        Integer.parseInt(req.getGprnNo().split("/")[1]), gmdd.getMaterialCode());

                if (gicdeOpt.isPresent()) {
                    errorMessage.append("Inspection already done for the provided GPRN No. " + req.getGprnNo()
                            + " and Material Code " + gmdd.getMaterialCode());
                    errorFound = true;
                    continue;
                } else if (!gicdeOpt.isPresent()
                        && (gmdd.getReceivedQuantity()
                                .compareTo(gmdd.getAcceptedQuantity().add(gmdd.getRejectedQuantity())) != 0)) {
                    errorMessage.append("Total received quantity for " + gmdd.getMaterialCode()
                            + " is not equal to accepted quantity + rejected quantity.");
                    errorFound = true;
                    continue;
                }

                GoodsInspectionConsumableDetailEntity gicde = new GoodsInspectionConsumableDetailEntity();
                mapper.map(gmdd, gicde);

                gicde.setInspectionSubProcessId(gime.getInspectionSubProcessId());
                gicde.setGprnSubProcessId(Integer.parseInt(req.getGprnNo().split("/")[1]));
                gicde.setGprnProcessId(Integer.parseInt(req.getGprnNo().split("/")[0].substring(3)));

                try {
                    String instlRepFileName = CommonUtils.saveBase64Image(gmdd.getInstallationReportBase64(), basePath);
                    gicde.setInstallationReportFilename(instlRepFileName);
                  //  System.out.print("Install report for con"+ instlRepFileName);
                } catch (Exception e) {
                    // Log error
                }

                gicdeList.add(gicde);
            } else {
                Optional<GiMaterialDtlEntity> gimdeOpt = gimdr.findByGprnSubProcessIdAndMaterialCode(
                        Integer.parseInt(req.getGprnNo().split("/")[1]), gmdd.getMaterialCode());

                if (gimdeOpt.isPresent()) {
                    errorMessage.append("Inspection already done for the provided GPRN No. " + req.getGprnNo()
                            + " and Material Code " + gmdd.getMaterialCode());
                    errorFound = true;
                    continue;
                } else if (!gimdeOpt.isPresent()
                        && (gmdd.getReceivedQuantity()
                                .compareTo(gmdd.getAcceptedQuantity().add(gmdd.getRejectedQuantity())) != 0)) {
                    errorMessage.append("Total received quantity for " + gmdd.getMaterialCode()
                            + " is not equal to accepted quantity + rejected quantity.");
                    errorFound = true;
                    continue;
                }

                // Fetch assetFlag from MaterialMaster to decide whether to create assets
                MaterialMaster materialMaster = mmr.findById(gmdd.getMaterialCode()).orElse(null);
                boolean shouldCreateAsset = materialMaster != null
                        && Boolean.TRUE.equals(materialMaster.getAssetFlag())
                        && gmdd.getAcceptedQuantity().compareTo(BigDecimal.ZERO) > 0;

                // Save installation report file once (shared across all asset rows for this material)
                String instlRepFileName = null;
                try {
                    instlRepFileName = CommonUtils.saveBase64Image(gmdd.getInstallationReportBase64(), basePath);
                } catch (Exception e) {
                    // Log error
                }

                if (shouldCreateAsset) {
                    // Create one asset (and one GiMaterialDtlEntity row) per accepted unit
                    int qty = gmdd.getAcceptedQuantity().intValue();
                    for (int i = 0; i < qty; i++) {
                        NewAssetResponseDto asset = createNewAsset(gmdd, req.getCreatedBy(), gprnDto.getPoId(), gprnDto.getLocationId());
                        System.out.println("Created asset for unit " + (i + 1) + ": " + asset);

                        GiMaterialDtlEntity gimde = new GiMaterialDtlEntity();
                        mapper.map(gmdd, gimde);
                        gimde.setAcceptedQuantity(BigDecimal.ONE); 
                        gimde.setAssetId(asset.getAssetId());
                        gimde.setAssetCode(asset.getAssetCode());
                        gimde.setInspectionSubProcessId(gime.getInspectionSubProcessId());
                        gimde.setGprnSubProcessId(Integer.parseInt(req.getGprnNo().split("/")[1]));
                        gimde.setGprnProcessId(Integer.parseInt(req.getGprnNo().split("/")[0].substring(3)));
                        gimde.setInstallationReportFileName(instlRepFileName);
                        gimdeList.add(gimde);
                    }
                } else {
                    // assetFlag is false/null — save a single row with no asset linked
                    GiMaterialDtlEntity gimde = new GiMaterialDtlEntity();
                    mapper.map(gmdd, gimde);
                    gimde.setInspectionSubProcessId(gime.getInspectionSubProcessId());
                    gimde.setGprnSubProcessId(Integer.parseInt(req.getGprnNo().split("/")[1]));
                    gimde.setGprnProcessId(Integer.parseInt(req.getGprnNo().split("/")[0].substring(3)));
                    gimde.setInstallationReportFileName(instlRepFileName);
                    gimdeList.add(gimde);
                }
            }
        }

        if (errorFound) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    errorMessage.toString()));
        }

        gimdr.saveAll(gimdeList);
        gicdr.saveAll(gicdeList);
        GiWorkflowStatus workflowStatus = new GiWorkflowStatus();
        workflowStatus.setProcessId("INV" + gime.getGprnProcessId());
        workflowStatus.setSubProcessId(gime.getInspectionSubProcessId());
        workflowStatus.setAction("Created");
        workflowStatus.setRemarks("GI Created");
        workflowStatus.setCreatedBy(req.getCreatedBy());
        workflowStatus.setCreateDate(LocalDateTime.now());

        gistausRepo.save(workflowStatus);

        return "INV" + gime.getGprnProcessId() + "/" + gime.getInspectionSubProcessId();
    }

    @Override
    public Map<String, Object> getGiDtls(String processNo) {
        ModelMapper mapper = new ModelMapper();
        String[] processNoSplit = processNo.split("/");

        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid process ID"));
        }

        Integer inspectionId = Integer.parseInt(processNoSplit[1]);
        GiMasterEntity gime = gimr.findById(inspectionId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Goods Inspection not found for the provided process ID.")));

        List<GiMaterialDtlEntity> gimdeList = gimdr.findByInspectionSubProcessId(gime.getInspectionSubProcessId());
        List<GoodsInspectionConsumableDetailEntity> gicdeList = gicdr
                .findByInspectionSubProcessId(gime.getInspectionSubProcessId());
        List<GiMaterialDtlDto> materialDtlListRes = gicdeList.stream()
                .map(gicde -> {
                    GiMaterialDtlDto gmdd = mapper.map(gicde, GiMaterialDtlDto.class);

                    try {
                        String imageBase64 = CommonUtils.convertImageToBase64(gicde.getInstallationReportFilename(),
                                basePath);
                        gmdd.setInstallationReportBase64(imageBase64);
                        gmdd.setInstallationReportFileName(gicde.getInstallationReportFilename());
                    } catch (Exception e) {
                        // Log error
                    }
                    return gmdd;
                }).collect(Collectors.toList());

        List<GiMaterialDtlDto> materialDtlListRes1 = gimdeList.stream()
                .map(gimde -> {
                    GiMaterialDtlDto gmdd = mapper.map(gimde, GiMaterialDtlDto.class);
                    gmdd.setAssetId(gimde.getAssetId());
                    gmdd.setRejectReason(gimde.getRejectReason());

                    gmdd.setAssetCode(gimde.getAssetCode());
                    gmdd.setInstallationReportFileName(gimde.getInstallationReportFileName());
                    Optional<AssetMasterEntity> aeOpt = amr.findById(gimde.getAssetId());
                    if (aeOpt.isPresent()) {
                        gmdd.setAssetDesc(aeOpt.get().getAssetDesc());
                        gmdd.setUomId(aeOpt.get().getUomId());
                    }

                    try {
                        String imageBase64 = CommonUtils.convertImageToBase64(gimde.getInstallationReportFileName(),
                                basePath);
                        gmdd.setInstallationReportBase64(imageBase64);
                    } catch (Exception e) {
                        // Log error
                    }
                    return gmdd;
                })
                .collect(Collectors.toList());

        SaveGiDto giRes = new SaveGiDto();
        giRes.setInspectionNo(processNo);
        giRes.setGprnNo(processNo);
        giRes.setInstallationDate(CommonUtils.convertDateToString(gime.getInstallationDate()));
        giRes.setCommissioningDate(CommonUtils.convertDateToString(gime.getCommissioningDate()));
        giRes.setGprnAmount(gime.getGprnAmount());
        giRes.setPoAmount(gime.getPoAmount());
        materialDtlListRes.addAll(materialDtlListRes1);
        giRes.setMaterialDtlList(materialDtlListRes);

        Map<String, Object> combinedRes = new HashMap<>();
        combinedRes.put("giDtls", giRes);
        combinedRes.put("gprnDtls",
                gprnService.getGprnDtls(processNo.split("/")[0] + "/" + gime.getGprnSubProcessId()));
//System.out.print(combinedRes);
        return combinedRes;
    }

    private NewAssetResponseDto createNewAsset(GiMaterialDtlDto materialDtl, Integer createdBy, String poId, String locationId) {
        MaterialMaster mme = mmr.findById(materialDtl.getMaterialCode())
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Material not found for the provided material code.")));

        // Always create a new asset — each unit gets its own unique asset code
        AssetMasterEntity ame = new ModelMapper().map(materialDtl, AssetMasterEntity.class);
        ame.setAssetDesc(materialDtl.getMaterialDesc());
        ame.setCreateDate(LocalDateTime.now());
        ame.setCreatedBy(createdBy);
        ame.setUpdatedDate(LocalDateTime.now());
        ame.setUnitPrice(mme.getUnitPrice());
        ame.setPoId(poId);

        String subCat = mme.getSubCategory().substring(0, 3);
        String assetCode = generateAssetCode(locationId, subCat);
        ame.setAssetCode(assetCode);

        amr.save(ame);
        return new NewAssetResponseDto(ame.getAssetId(), ame.getAssetCode());
    }

 /*   private String generateAssetCode(String fieldStation, String subCategory) {
        String financialYear = getFinancialYear();
        String prefix = fieldStation + subCategory + financialYear + "-";

        String maxAssetCode = amr.findMaxAssetCodeByPrefix(prefix);
        int nextSeq = 1;
        if (maxAssetCode != null) {
            String seqPart = maxAssetCode.substring(maxAssetCode.lastIndexOf('-') + 1);
            nextSeq = Integer.parseInt(seqPart) + 1;
        }

        return prefix + String.format("%03d", nextSeq);
    }*/
 private String generateAssetCode(String fieldStation, String subCategory) {
     String financialYear = getFinancialYear();
     String prefix = (fieldStation + subCategory + financialYear + "-").toUpperCase();

     //  Get the current max asset_id from DB
     Integer maxAssetId = amr.findMaxAssetId();
     int nextSeq = (maxAssetId != null ? maxAssetId + 1 : 1);

     return prefix + String.format("%03d", nextSeq);
 }


    private String getFinancialYear() {
        LocalDate today = LocalDate.now();
        int startYear = today.getMonthValue() >= 4 ? today.getYear() % 100 : (today.getYear() - 1) % 100;
        int endYear = (startYear + 1) % 100;
        return String.format("%02d%02d", startYear, endYear);
    }


    @Override
    public void validateGiIsApproved(String processNo) {
        String[] processNoSplit = processNo.split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid GI No."));
        }

        // Integer processId = Integer.parseInt(processNoSplit[0].substring(3));
        String processId = processNoSplit[0].substring(3);
        Integer subProcessId = Integer.parseInt(processNoSplit[1]);

        // GiMasterEntity giMaster =
        // gimr.findByGprnProcessIdAndInspectionSubProcessId(processId, subProcessId)
        GiMasterEntity giMaster = gimr.findByGprnProcessIdAndInspectionSubProcessId(processId, subProcessId)

                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Provided GI No. is not valid.")));

        if (!"APPROVED".equalsIgnoreCase(giMaster.getStatus())) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "GI is not approved. Cannot create GRN."));
        }
    }

    @Override
    public void validateGiSubProcessId(String processNo) {
        String[] processNoSplit = processNo.split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid GI No."));
        }

        Integer subProcessId = Integer.parseInt(processNoSplit[1]);
        if (!gimr.existsById(subProcessId)) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Provided GI No. is not valid."));
        }

    }

    public List<GprnPendingInspectionDto> getGiStatusWise(String status, Optional<String> createdBy) {
        // List<Object[]> results = gimr.getGiStatusWise(status);
        List<Object[]> results = new ArrayList<>();
        if (createdBy.isPresent()) {
            results = gimr.getGiStatusWiseAndCreatedBy(status, createdBy.get());
        } else {
            results = gimr.getGiStatusWise(status);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return results.stream().map(row -> {
            GprnPendingInspectionDto dto = new GprnPendingInspectionDto();
            dto.setProcessId((String) row[0]);
            dto.setSubProcessId((Integer) row[1]);
            dto.setPoId((String) row[2]);
            dto.setLocationId((String) row[3]);
            dto.setDate(CommonUtils.convertSqlDateToString((Date) row[4]));
            dto.setChallanNo((String) row[5]);
            dto.setDeliveryDate(CommonUtils.convertSqlDateToString((Date) row[6]));
            dto.setVendorId((String) row[7]);
            dto.setFieldStation((String) row[8]);
            dto.setIndentorName((String) row[9]);
            dto.setSupplyExpectedDate(CommonUtils.convertSqlDateToString((Date) row[10]));
            dto.setConsigneeDetail((String) row[11]);
            dto.setWarrantyYears((BigDecimal) row[12]);
            dto.setProject((String) row[13]);
            dto.setReceivedBy((String) row[14]);

            try {
                String detailsJson = (String) row[15];
                if (detailsJson != null && !detailsJson.isEmpty()) {
                    List<GprnPendingInspectionDetailDto> details = mapper.readValue(
                            detailsJson,
                            mapper.getTypeFactory().constructCollectionType(
                                    List.class,
                                    GprnPendingInspectionDetailDto.class));
                    dto.setMaterialDetails(details);
                } else {
                    dto.setMaterialDetails(new ArrayList<>());
                }
            } catch (Exception e) {
                dto.setMaterialDetails(new ArrayList<>());
            }

            dto.setStatus((String) row[16]);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveGi(GiApprovalDto req) {
        // updateGiStatusAndRemarks(req, "APPROVED");
        updateGiStatusAndRemarks(req);

        updatePoBasedonRejectionType(req);
        giMailSender(req.getProcessNo());

    }

    public String giMailSender(String processNumber) {
        try {
            String[] parts = processNumber.split("/");
            Integer inspectionSubProcessId = Integer.parseInt(parts[1]);


            GiMasterEntity giMaster = gimr.findById(inspectionSubProcessId)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "GI not found")));

            Integer gprnSubProcessId = giMaster.getGprnSubProcessId();

            GprnMasterEntity gprnMaster = gprnMasterRepository.findById(gprnSubProcessId)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "Gprn not found")));




         UserMaster um = userMasterRepository.findByUserId(Integer.valueOf(gprnMaster.getReceivedBy()));

            String custodainName= um.getUserName();
            Integer custodainId= Integer.valueOf(gprnMaster.getReceivedBy());
            String   emailId = um.getEmail();
            emailService.sendGiMails(
                    emailId,
                    custodainName,
                    custodainId,
                    String.valueOf(giMaster.getInspectionSubProcessId()),
                    giMaster.getGprnProcessId(),
                    giMaster.getStatus()
            );

        } catch (Exception e) {
            return "Invalid process number format.";
        }
        return  "";
    }
/*
    private void updatePoBasedonRejectionType(GiApprovalDto req){
        String[] processNoSplit = req.getProcessNo().split("/");
        String poId = "PO" + processNoSplit[0].substring(3);
        Integer inspectionId = Integer.parseInt(processNoSplit[1]);
        List<GiMaterialDtlEntity> gimdeList = gimdr.findByInspectionSubProcessId(inspectionId);
        List <GoodsInspectionConsumableDetailEntity> gicdeList = gicdr.findByInspectionSubProcessId(inspectionId);

        for(GiMaterialDtlEntity gimde : gimdeList){
            System.out.println("ADDED AGAIN NON CONSUMABLE");
            String rejectionType = gimde.getRejectionType();
         //  if(gimde.getRejectionType().equalsIgnoreCase("replacement")){
            if ("replacement".equalsIgnoreCase(rejectionType)) {
                // add the quantity to po again
            PurchaseOrderAttributes poa = poar.findByPurchaseOrder_PoIdAndMaterialCode(poId, gimde.getMaterialCode())
                                        .orElseThrow(() -> new BusinessException(new ErrorDetails(
                                                AppConstant.ERROR_CODE_RESOURCE,
                                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                                AppConstant.ERROR_TYPE_RESOURCE,
                                                "Purchase Order not found")));
            poa.setReceivedQuantity(poa.getReceivedQuantity().subtract(gimde.getRejectedQuantity()));
            poar.save(poa);
           }
        }
        for(GoodsInspectionConsumableDetailEntity gicde : gicdeList){
            System.out.println("ADDED AGAIN CONSUMABLE");
           // if(gicde.getRejectionType().equalsIgnoreCase("replacement")){
            String rejectionType = gicde.getRejectionType();
            if ("replacement".equalsIgnoreCase(rejectionType)) {
                // add the quantity to po again
                PurchaseOrderAttributes poa = poar.findByPurchaseOrder_PoIdAndMaterialCode(poId, gicde.getMaterialCode())
                                        .orElseThrow(() -> new BusinessException(new ErrorDetails(
                                                AppConstant.ERROR_CODE_RESOURCE,
                                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                                AppConstant.ERROR_TYPE_RESOURCE,
                                                "Purchase Order not found")));
                poa.setReceivedQuantity(poa.getReceivedQuantity().subtract(gicde.getRejectedQuantity()));
                poar.save(poa);
            }
        }
    }
*/
private void updatePoBasedonRejectionType(GiApprovalDto req) {

    String[] processNoSplit = req.getProcessNo().split("/");
    String poId = "PO" + processNoSplit[0].substring(3);
    Integer inspectionId = Integer.parseInt(processNoSplit[1]);

    List<GiMaterialDtlEntity> gimdeList = gimdr.findByInspectionSubProcessId(inspectionId);
    List<GoodsInspectionConsumableDetailEntity> gicdeList = gicdr.findByInspectionSubProcessId(inspectionId);

    for (GiMaterialDtlEntity gimde : gimdeList) {

        if ("replacement".equalsIgnoreCase(gimde.getRejectionType())) {

            PurchaseOrderAttributes poa = poar
                    .findByPurchaseOrder_PoIdAndMaterialCode(poId, gimde.getMaterialCode())
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "Purchase Order not found")));

            BigDecimal receivedQty = poa.getReceivedQuantity() != null
                    ? poa.getReceivedQuantity()
                    : BigDecimal.ZERO;

            BigDecimal rejectedQty = gimde.getRejectedQuantity() != null
                    ? gimde.getRejectedQuantity()
                    : BigDecimal.ZERO;

            poa.setReceivedQuantity(receivedQty.subtract(rejectedQty));

            poar.save(poa);
        }
    }

    for (GoodsInspectionConsumableDetailEntity gicde : gicdeList) {

        if ("replacement".equalsIgnoreCase(gicde.getRejectionType())) {

            PurchaseOrderAttributes poa = poar
                    .findByPurchaseOrder_PoIdAndMaterialCode(poId, gicde.getMaterialCode())
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "Purchase Order not found")));

            BigDecimal receivedQty = poa.getReceivedQuantity() != null
                    ? poa.getReceivedQuantity()
                    : BigDecimal.ZERO;

            BigDecimal rejectedQty = gicde.getRejectedQuantity() != null
                    ? gicde.getRejectedQuantity()
                    : BigDecimal.ZERO;

            poa.setReceivedQuantity(receivedQty.subtract(rejectedQty));

            poar.save(poa);
        }
    }
}


    @Override
    @Transactional
    public void rejectGi(GiApprovalDto req) {
        // updateGiStatusAndRemarks(req, "REJECTED");
        updateGiStatusAndRemarks(req);

        giMailSender(req.getProcessNo());
    }

    @Override
    @Transactional
    public void changeReqGi(GiApprovalDto req) {
        // updateGiStatusAndRemarks(req, "CHANGE REQUEST");
        updateGiStatusAndRemarks(req);

        giMailSender(req.getProcessNo());
    }

    private void updateGiStatusAndRemarks(GiApprovalDto req) {
        String[] processNoSplit = req.getProcessNo().split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid process number format"));
        }

        Integer inspectionId = Integer.parseInt(processNoSplit[1]);
        GiMasterEntity giMaster = gimr.findById(inspectionId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Goods Inspection not found")));

        giMaster.setStatus(req.getStatus());
        gimr.save(giMaster);

        GiWorkflowStatus history = new GiWorkflowStatus();
        history.setProcessId(processNoSplit[0]);
        history.setSubProcessId(inspectionId);
        history.setAction(req.getStatus());
        history.setRemarks(req.getRemarks());
        history.setCreatedBy(req.getCreatedBy());
        history.setCreateDate(LocalDateTime.now());

        gistausRepo.save(history);
    }

    public List<GiMasterEntity> getGiByStatuses() {
        List<String> statuses = Arrays.asList("AWAITING APPROVAL");
        return gimr.findByStatusIn(statuses);
    }

    public List<GiMasterEntity> getGiByIndentorStatuses() {
        List<String> statuses = Arrays.asList("REJECTED", "CHANGE REQUEST");
        return gimr.findByStatusIn(statuses);
    }

    @Override
    @Transactional
    public String updateGi(SaveGiDto req) {
        String[] processNoSplit = req.getGprnNo().split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid process number format"));
        }

        Integer subProcessId = Integer.parseInt(processNoSplit[1]);
        GiMasterEntity gime = gimr.findByGprnSubProcessId(subProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "GI Master not found for given GPRN No.")));

        gime.setCommissioningDate(CommonUtils.convertStringToDateObject(req.getCommissioningDate()));
        gime.setInstallationDate(CommonUtils.convertStringToDateObject(req.getInstallationDate()));
        gime.setLocationId(req.getLocationId());
        gime.setStatus("AWAITING APPROVAL");
        gimr.save(gime);

        ModelMapper mapper = new ModelMapper();
        StringBuilder errorMessage = new StringBuilder();
        boolean errorFound = false;

        for (GiMaterialDtlDto gmdd : req.getMaterialDtlList()) {

            Optional<GoodsInspectionConsumableDetailEntity> gicdeOpt = gicdr
                    .findByGprnSubProcessIdAndMaterialCode(subProcessId, gmdd.getMaterialCode());

            if (gicdeOpt.isPresent()) {
                // Consumable
                GoodsInspectionConsumableDetailEntity gicde = gicdeOpt.get();
                mapper.map(gmdd, gicde);

                if (!gmdd.getReceivedQuantity().equals(gmdd.getAcceptedQuantity().add(gmdd.getRejectedQuantity()))) {
                    errorMessage.append("Received quantity mismatch for " + gmdd.getMaterialCode() + ". ");
                    errorFound = true;
                    continue;
                }

                try {
                    String fileName = CommonUtils.saveBase64Image(gmdd.getInstallationReportBase64(), basePath);
                    gicde.setInstallationReportFilename(fileName);
                } catch (Exception e) {
                    // log error
                }
                gicdr.save(gicde);
                continue;
            }

            Optional<GiMaterialDtlEntity> gimdeOpt = gimdr.findByGprnSubProcessIdAndMaterialCode(subProcessId,
                    gmdd.getMaterialCode());

            if (gimdeOpt.isPresent()) {
                // Non-Consumable
                GiMaterialDtlEntity gimde = gimdeOpt.get();
                mapper.map(gmdd, gimde);

                if (!gmdd.getReceivedQuantity().equals(gmdd.getAcceptedQuantity().add(gmdd.getRejectedQuantity()))) {
                    errorMessage.append("Received quantity mismatch for " + gmdd.getMaterialCode() + ". ");
                    errorFound = true;
                    continue;
                }

                if (gmdd.getAcceptedQuantity().compareTo(BigDecimal.ZERO) > 0 && gimde.getAssetId() == null) {
                    SaveGprnDto gprnDto = gprnService.getGprnDtls(req.getGprnNo());
                  //  gimde.setAssetId(createNewAsset(gmdd, req.getCreatedBy(), gprnDto.getPoId()));
                  NewAssetResponseDto asset =  createNewAsset(gmdd, req.getCreatedBy(), gprnDto.getPoId(), gprnDto.getLocationId());
                  gimde.setAssetCode(asset.getAssetCode());
                  gimde.setAssetId(asset.getAssetId());
                }

                try {
                    String fileName = CommonUtils.saveBase64Image(gmdd.getInstallationReportBase64(), basePath);
                    gimde.setInstallationReportFileName(fileName);
                } catch (Exception e) {
                    // log error
                }
                gimdr.save(gimde);
                continue;
            }

            // If neither found
            errorMessage.append("Material Code " + gmdd.getMaterialCode() + " not found in GI tables. ");
            errorFound = true;
        }

        if (errorFound) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    errorMessage.toString()));
        }
        GiWorkflowStatus workflowStatus = new GiWorkflowStatus();
        workflowStatus.setProcessId("INV" + gime.getGprnProcessId());
        workflowStatus.setSubProcessId(gime.getInspectionSubProcessId());
        workflowStatus.setAction("UPDATED");
        workflowStatus.setRemarks("GI updated");
        workflowStatus.setCreatedBy(req.getCreatedBy());
        workflowStatus.setCreateDate(LocalDateTime.now());

        gistausRepo.save(workflowStatus);

        return "INV" + gime.getGprnProcessId() + "/" + gime.getInspectionSubProcessId();
    }

    public List<GiWorkflowStatusDto> getGiHistoryByProcessId(String processId, Integer subProcessId) {
        List<GiWorkflowStatus> historyList = gistausRepo.findByProcessIdAndSubProcessIdOrderByIdAsc(processId,
                subProcessId);
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

    public List<GprnDropdownDto> getPendingGprnsForGI() {
        List<GprnMasterEntity> pendingGprns = gprnMasterRepository.findPendingGprnsWithMaterial();

        return pendingGprns.stream()
                .map(g -> new GprnDropdownDto(
                        g.getSubProcessId(),
                      "INV" + g.getProcessId() + "/" + g.getSubProcessId(),
                        g.getPoId(),
                        g.getVendorId(),
                        gprnMaterialDtlRepository.findMaterialDescriptionsBySubProcessId(g.getSubProcessId())
                ))
                .collect(Collectors.toList());
    }
/*
    public List<GprnDropdownDto> getPendingRejectedGis() {

        List<GprnDropdownDto> normalGis = gimdr.findByRejectionType("replacement")
                .stream()
                .filter(gi -> {
                    String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
                    return !ogpMasterRejectedGiRepository.existsByGiId(giNo);
                })
                .map(gi -> {
                    String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
                    List<String> materialList = gimdr
                            .findMaterialDescriptionsByInspectionSubProcessId(gi.getInspectionSubProcessId());

                    GprnPoVendorDto gprnDto = gprnMasterRepository.findPoIdAndVendorIdBySubProcessId(gi.getGprnSubProcessId());

                    return new GprnDropdownDto(
                            gi.getInspectionSubProcessId(),
                            giNo,
                            gprnDto.getPoId(),
                            gprnDto.getVendorId(),
                            materialList
                    );
                }).collect(Collectors.toList());

        List<GprnDropdownDto> consumableGis = gicdr.findByRejectionType("replacement")
                .stream()
                .filter(gi -> {
                    String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
                    return !ogpMasterRejectedGiRepository.existsByGiId(giNo);
                })
                .map(gi -> {
                    String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
                    List<String> materialList = gicdr
                            .findMaterialDescriptionsByInspectionSubProcessId(gi.getInspectionSubProcessId());

                    GprnPoVendorDto gprnDto = gprnMasterRepository.findPoIdAndVendorIdBySubProcessId(gi.getGprnSubProcessId());

                    return new GprnDropdownDto(
                            gi.getInspectionSubProcessId(),
                            giNo,
                            gprnDto.getPoId(),
                            gprnDto.getVendorId(),
                            materialList
                    );
                }).collect(Collectors.toList());

        // Combine both lists
        normalGis.addAll(consumableGis);

        return normalGis;
    }*/
public List<GprnDropdownDto> getPendingRejectedGis() {

    // Fetch replacement + permanent for NORMAL
    List<GiMaterialDtlEntity> normalEntities = new ArrayList<>();
    normalEntities.addAll(gimdr.findByRejectionType("replacement"));
    normalEntities.addAll(gimdr.findByRejectionType("permanent"));

    List<GprnDropdownDto> normalGis = normalEntities.stream()
            .filter(gi -> {
                String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
                return !ogpMasterRejectedGiRepository.existsByGiId(giNo);
            })
            .map(gi -> {
                String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
                List<String> materialList = gimdr
                        .findMaterialDescriptionsByInspectionSubProcessId(gi.getInspectionSubProcessId());

                GprnPoVendorDto gprnDto = gprnMasterRepository
                        .findPoIdAndVendorIdBySubProcessId(gi.getGprnSubProcessId());

                return new GprnDropdownDto(
                        gi.getInspectionSubProcessId(),
                        giNo,
                        gprnDto.getPoId(),
                        gprnDto.getVendorId(),
                        materialList
                );
            }).collect(Collectors.toList());



    // Fetch replacement + permanent for CONSUMABLE
    List<GoodsInspectionConsumableDetailEntity> consumableEntities = new ArrayList<>();
    consumableEntities.addAll(gicdr.findByRejectionType("replacement"));
    consumableEntities.addAll(gicdr.findByRejectionType("permanent"));

    List<GprnDropdownDto> consumableGis = consumableEntities.stream()
            .filter(gi -> {
                String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
                return !ogpMasterRejectedGiRepository.existsByGiId(giNo);
            })
            .map(gi -> {
                String giNo = "INV" + gi.getGprnProcessId() + "/" + gi.getInspectionSubProcessId();
                List<String> materialList = gicdr
                        .findMaterialDescriptionsByInspectionSubProcessId(gi.getInspectionSubProcessId());

                GprnPoVendorDto gprnDto = gprnMasterRepository
                        .findPoIdAndVendorIdBySubProcessId(gi.getGprnSubProcessId());

                return new GprnDropdownDto(
                        gi.getInspectionSubProcessId(),
                        giNo,
                        gprnDto.getPoId(),
                        gprnDto.getVendorId(),
                        materialList
                );
            }).collect(Collectors.toList());


    normalGis.addAll(consumableGis);

    return normalGis;
}





}
package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.ApprovalAndRejectionRequestDTO;
import com.astro.dto.workflow.MaterialMasterUtilRequestDto;
import com.astro.dto.workflow.MaterialMasterUtilResponseDto;
import com.astro.dto.workflow.MaterialTransitionHistory;
import com.astro.entity.*;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.*;
import com.astro.service.MaterialMasterUtilService;
import com.astro.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaterialMasterUtilServiceImpl implements MaterialMasterUtilService {

    @Autowired
    private MaterialMasterUtilRepository materialMasterUtilRepository;

    @Autowired
    private UserRoleMasterRepository userRoleMasterRepository;

    @Autowired
    private MaterialMasterRepository materialMasterRepository;
    @Autowired
    private MaterialStatusRepository materialStatusRepository;
    @Autowired
    private MaterialIdSequenceRepository materialIdRepo;
    @Value("${filePath}")
    private String bp;
    private final String basePath;
    public MaterialMasterUtilServiceImpl(@Value("${filePath}") String bp) {
        this.basePath = bp + "/Material";
    }


    @Override
    public MaterialMasterUtilResponseDto createMaterial(MaterialMasterUtilRequestDto dto) {
        MaterialMasterUtil material = new MaterialMasterUtil();

       // Integer maxNumber = materialMasterUtilRepository.findMaxMaterialNumber();
      //  int nextNumber = (maxNumber == null) ? 1001 : maxNumber + 1;

      //  String materialId = "M" + nextNumber;
        //String materialId = "M" + System.currentTimeMillis();
        Integer maxNumber = materialIdRepo.findMaxMaterialId();
        int nextNumber = (maxNumber == null) ? 1100 : maxNumber + 1;

        String materialId = "M" + nextNumber;

        // String vendorId = "V" + System.currentTimeMillis();
        MaterialIdSequence materialIdSequence = new MaterialIdSequence();
        materialIdSequence.setMaterialId(nextNumber);
        materialIdRepo.save(materialIdSequence);

        material.setMaterialCode(materialId);
        material.setMaterialNumber(0);
        material.setCategory(dto.getCategory());
        material.setSubCategory(dto.getSubCategory());
        material.setDescription(dto.getDescription());
        material.setUom(dto.getUom());
        material.setUnitPrice(dto.getUnitPrice());
        material.setCurrency(dto.getCurrency());
        material.setEstimatedPriceWithCcy(dto.getEstimatedPriceWithCcy());
      //  material.setUploadImageName(dto.getUploadImageFileName());
        material.setIndigenousOrImported(dto.getIndigenousOrImported());
        material.setApprovalStatus(MaterialMasterUtil.ApprovalStatus.AWAITING_APPROVAL);
        material.setComments(null);
        // Added by Aman
        material.setAssetFlag(dto.getAsset_Flag());
        // End
        material.setBriefDescription(dto.getBriefDescription());
        material.setCreatedBy(dto.getCreatedBy());
        material.setUpdatedBy(dto.getUpdatedBy());
        if (dto.getUploadImageFileName() == null || dto.getUploadImageFileName().isEmpty()) {
            material.setUploadImageName(null);
        } else {
            String fileName = saveBase64Files(dto.getUploadImageFileName(), basePath);
            material.setUploadImageName(fileName);
        }

        material = materialMasterUtilRepository.save(material);

       saveMaterialTracking(material.getMaterialCode(), "CREATED", material.getApprovalStatus().name(), material.getComments() , dto.getCreatedBy());

       // saveMaterialTracking(material.getMaterialCode(), "CREATED", request.getAction(), "Material created in MasterUtil", request.getActionBy());

        return mapToResponse(material);
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
    public List<MaterialMasterUtilResponseDto> getAllAwaitingApprovalMaterials() {
        List<MaterialMasterUtil> materials =materialMasterUtilRepository.findByApprovalStatus(MaterialMasterUtil.ApprovalStatus.AWAITING_APPROVAL);

        return materials.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<MaterialMasterUtilResponseDto> getAllChangeRequestMaterials() {
        List<MaterialMasterUtil> materials =materialMasterUtilRepository.findByApprovalStatus(MaterialMasterUtil.ApprovalStatus.CHANGE_REQUEST);

        return materials.stream().map(this::mapToResponse).collect(Collectors.toList());
    }


    private MaterialMasterUtilResponseDto mapToResponse(MaterialMasterUtil material) {

        MaterialMasterUtilResponseDto response = new MaterialMasterUtilResponseDto();

        response.setMaterialCode(material.getMaterialCode());
        response.setCategory(material.getCategory());
        response.setSubCategory(material.getSubCategory());
        response.setDescription(material.getDescription());
        response.setUom(material.getUom());
        response.setUnitPrice(material.getUnitPrice());
        response.setCurrency(material.getCurrency());
        response.setEstimatedPriceWithCcy(material.getEstimatedPriceWithCcy());
        response.setUploadImageFileName(material.getUploadImageName());
        response.setIndigenousOrImported(material.getIndigenousOrImported());
        response.setApprovalStatus(material.getApprovalStatus().name());
        response.setComments(material.getComments());
        response.setBriefDescription(material.getBriefDescription());
        response.setCreatedBy(material.getCreatedBy());
        response.setUpdatedBy(material.getUpdatedBy());
        response.setCreatedDate(material.getCreatedDate());
        response.setUpdatedDate(material.getUpdatedDate());
        // Added by Aman 
        response.setAsset_Flag(material.getAssetFlag());
        // End

        return response;
    }

   /* @Override
    public String performActionForMaterial(ApprovalAndRejectionRequestDTO request) {

        validateUserRoles(request.getActionBy());

        // Fetch vendor (Ensure it exists)
        MaterialMasterUtil material = materialMasterUtilRepository
                .findById(request.getRequestId())
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Material ID not found!"
                )));


        if ("APPROVED".equalsIgnoreCase(request.getAction())) {
            int actionBy =request.getActionBy();
            String remarks = request.getRemarks();
            return approveMaterial(material, actionBy, remarks);
        } else if ("REJECTED".equalsIgnoreCase(request.getAction())) {
            int actionBy =request.getActionBy();
            return rejectMaterial(material, request.getRemarks(),actionBy);
        } else if ("CHANGE REQUEST".equalsIgnoreCase(request.getAction())) {
            return changeRequestVendor(material, request.getRemarks(), request.getActionBy());

        } else {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.INVALID_ACTION,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid action. Use 'APPROVED' or 'REJECTED'."
            ));
        }
    }


    */
   @Override
   public String performActionForMaterial(ApprovalAndRejectionRequestDTO request) {
       // Fetch all roleIds for the user (user can have multiple roles)
       List<Integer> roleIds = userRoleMasterRepository.findAllRoleIdsByUserId(request.getActionBy());
       if (roleIds == null || roleIds.isEmpty()) {
           throw new InvalidInputException(new ErrorDetails(
                   AppConstant.ERROR_TYPE_CODE_VALIDATION,
                   AppConstant.ERROR_TYPE_CODE_VALIDATION,
                   AppConstant.ERROR_TYPE_VALIDATION,
                   "Unauthorised User!"
           ));
       }

       MaterialMasterUtil material = materialMasterUtilRepository
               .findById(request.getRequestId())
               .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                       AppConstant.ERROR_CODE_RESOURCE,
                       AppConstant.ERROR_TYPE_CODE_VALIDATION,
                       AppConstant.ERROR_TYPE_VALIDATION,
                       "Material ID not found!"
               )));

       int actionBy = request.getActionBy();
       String remarks = request.getRemarks();

       boolean isStorePurchaseOfficer = roleIds.contains(11);
       boolean isIndentCreator = roleIds.contains(1);

       switch (request.getAction().toUpperCase()) {
           case "APPROVED":
               if (isStorePurchaseOfficer) {  // Store Purchase Officer
                   return approveMaterial(material, actionBy, remarks);
               } else if (isIndentCreator && material.getApprovalStatus() == MaterialMasterUtil.ApprovalStatus.CHANGE_REQUEST) {
                   material.setComments(remarks);
                   material.setApprovalStatus(MaterialMasterUtil.ApprovalStatus.AWAITING_APPROVAL);
                   materialMasterUtilRepository.save(material);
                   saveMaterialTracking(material.getMaterialCode(), "PENDING", "APPROVED", remarks, actionBy);

                   return "Material " + material.getMaterialCode() + " has been updated to AWAITING APPROVAL.";
               }
               break;

           case "REJECTED":
               return rejectMaterial(material, remarks, actionBy);

           case "CHANGE REQUEST":
               if (isStorePurchaseOfficer) {  // Store Purchase Officer
                   return changeRequestVendor(material, remarks, actionBy);
               }
               break;

           default:
               throw new InvalidInputException(new ErrorDetails(
                       AppConstant.INVALID_ACTION,
                       AppConstant.ERROR_TYPE_CODE_VALIDATION,
                       AppConstant.ERROR_TYPE_VALIDATION,
                       "Invalid action. Use 'APPROVED', 'REJECTED', or 'CHANGE REQUEST'."
               ));
       }

       return "Invalid operation for user role.";
   }

    @Override
    public String performAllActionForMaterial(List<ApprovalAndRejectionRequestDTO> request) {
       String response = null;
       for(ApprovalAndRejectionRequestDTO dto : request){
          response =performActionForMaterial(dto);
       }
        return response;
    }


    private String changeRequestVendor(MaterialMasterUtil material, String remarks, Integer actionBy) {

        material.setApprovalStatus(MaterialMasterUtil.ApprovalStatus.CHANGE_REQUEST);
        material.setComments(remarks);

        materialMasterUtilRepository.save(material);
        saveMaterialTracking(material.getMaterialCode(), "PENDING","CHANGE_REQUEST", remarks, actionBy);

        return "Vendor " + material.getMaterialCode() + " Change Requested.";

    }

    private String rejectMaterial(MaterialMasterUtil material, String remarks, Integer actionBy) {

        material.setApprovalStatus(MaterialMasterUtil.ApprovalStatus.REJECTED);
        material.setComments(remarks);

        materialMasterUtilRepository.save(material);
        saveMaterialTracking(material.getMaterialCode(), "REJECTED","REJECTED", remarks, actionBy);

        return "Material " + material.getMaterialCode() + " has been REJECTED.";


    }

    private String approveMaterial(MaterialMasterUtil material,int actionBy, String remarks ) {

        MaterialMaster materialMaster= new MaterialMaster();
        materialMaster.setMaterialCode(material.getMaterialCode());
        materialMaster.setCategory(material.getCategory());
        materialMaster.setSubCategory(material.getSubCategory());
        materialMaster.setDescription(material.getDescription());
        materialMaster.setUom(material.getUom());
        materialMaster.setUnitPrice(material.getUnitPrice());
        materialMaster.setCurrency(material.getCurrency());
        materialMaster.setUploadImageName(material.getUploadImageName());
        materialMaster.setIndigenousOrImported(material.getIndigenousOrImported());
        materialMaster.setEstimatedPriceWithCcy(material.getEstimatedPriceWithCcy());
        materialMaster.setRemarks(material.getComments());
        materialMaster.setBriefDescription(material.getBriefDescription());
        materialMaster.setStatus("APPROVED");
        materialMaster.setStatusOfMaterialActiveOrDeactive("Active");
        materialMaster.setCreatedBy(actionBy);
        materialMaster.setUpdatedBy(material.getUpdatedBy());
        // Added by Aman 
        materialMaster.setAssetFlag(material.getAssetFlag());
        // End
        materialMasterRepository.save(materialMaster);
        saveMaterialTracking(material.getMaterialCode(), "COMPLETED","APPROVED", remarks, actionBy);

        //approved materials are saved in material master before deleting in material master util
        materialMasterUtilRepository.deleteById(material.getMaterialCode());
        return "Material " + material.getMaterialCode() + " has been APPROVED and added the material data to material master, deleted from material master util.";

    }
/*
    private void saveMaterialTracking(String materialCode,String Status, String action, String remarks, int actionBy) {
            MaterialStatus materialStatus= new MaterialStatus();
            materialStatus.setMaterialCode(materialCode);
            materialStatus.setStatus(status);
            materialStatus.setAction(action);
            materialStatus.setComments(remarks);
            materialStatus.setCreatedBy(actionBy);


            materialStatusRepository.save(materialStatus);

    }

 */
private void saveMaterialTracking(String materialCode, String status, String action, String remarks, int actionBy) {
    MaterialStatus materialStatus = new MaterialStatus();
    materialStatus.setMaterialCode(materialCode);
    materialStatus.setStatus(status);
    materialStatus.setAction(action);
    materialStatus.setComments(remarks);
    materialStatus.setCreatedBy(actionBy);

    materialStatusRepository.save(materialStatus);
}



    private void validateUserRoles(Integer userId) {
        // Check if the user has role 11 (Store Purchase Officer) or role 1 (Indent Creator)
        boolean hasValidRole = userRoleMasterRepository.existsByRoleIdAndUserId(11, userId) ||
                userRoleMasterRepository.existsByRoleIdAndUserId(1, userId);

        if (!hasValidRole) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.UNAUTHORIZED_ACTION,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Unauthorized user"
            ));
        }
    }

    @Override
    public List<MaterialTransitionHistory> getMaterialStatusByCode(String materialCode) {
        List<MaterialStatus> materialStatusList = materialStatusRepository.findByMaterialCode(materialCode);

        return materialStatusList.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public MaterialMasterUtilResponseDto updateMaterialMasterUtil(String materialCode, MaterialMasterUtilRequestDto dto) {

        // Try to find in the pending/util table first (Queue edit flow)
        MaterialMasterUtil material = materialMasterUtilRepository.findById(materialCode).orElse(null);

        if (material == null) {
            // Not in pending table — check if it's an approved material in MaterialMaster (Masters page search flow)
            MaterialMaster approved = materialMasterRepository.findById(materialCode)
                    .orElseThrow(() -> new BusinessException(
                            new ErrorDetails(
                                    AppConstant.ERROR_CODE_RESOURCE,
                                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                    AppConstant.ERROR_TYPE_VALIDATION,
                                    "Material not found for the provided Material code.")
                    ));
            // Copy approved record into the pending table so it goes through approval again
            material = new MaterialMasterUtil();
            material.setMaterialCode(approved.getMaterialCode());
            material.setCreatedBy(dto.getCreatedBy());
        }

        System.out.println("SubCa:"+ dto.getSubCategory());
        material.setCategory(dto.getCategory());
        material.setSubCategory(dto.getSubCategory());
        material.setDescription(dto.getDescription());
        material.setUom(dto.getUom());
        material.setUnitPrice(dto.getUnitPrice());
        material.setCurrency(dto.getCurrency());
        material.setEstimatedPriceWithCcy(dto.getEstimatedPriceWithCcy());
      //  material.setUploadImageName(dto.getUploadImageFileName());
        material.setIndigenousOrImported(dto.getIndigenousOrImported());
        material.setBriefDescription(dto.getBriefDescription());
        material.setApprovalStatus(MaterialMasterUtil.ApprovalStatus.AWAITING_APPROVAL);
        material.setComments(null);
        material.setCreatedBy(dto.getCreatedBy());
        material.setUpdatedBy(dto.getUpdatedBy());
        // Added by Aman 
        material.setAssetFlag(dto.getAsset_Flag());
        // End

        materialMasterUtilRepository.save(material);

        Integer actionBy = dto.getCreatedBy();
        saveMaterialTracking(material.getMaterialCode(), "PENDING","UPDATED", null, actionBy );


        return mapToResponse(material);
    }

    @Override
    public MaterialMasterUtilResponseDto getMaterialMasterUtilById(String materialCode) {

        MaterialMasterUtil material= materialMasterUtilRepository.findById(materialCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "material master not found for the provided materialcode.")
                ));
        return mapToResponse(material);
    }

    @Override
    public MaterialMasterUtilResponseDto getMaterialMasterUtilByIdbase(String materialCode) throws IOException {

        MaterialMasterUtil material= materialMasterUtilRepository.findById(materialCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "material master not found for the provided materialcode.")
                ));
        MaterialMasterUtilResponseDto response = new MaterialMasterUtilResponseDto();

        response.setMaterialCode(material.getMaterialCode());
        response.setCategory(material.getCategory());
        response.setSubCategory(material.getSubCategory());
        response.setDescription(material.getDescription());
        response.setUom(material.getUom());
        response.setUnitPrice(material.getUnitPrice());
        response.setCurrency(material.getCurrency());
        response.setEstimatedPriceWithCcy(material.getEstimatedPriceWithCcy());
        response.setUploadImageFileName(material.getUploadImageName());
        response.setIndigenousOrImported(material.getIndigenousOrImported());
        response.setApprovalStatus(material.getApprovalStatus().name());
        response.setComments(material.getComments());
        response.setBriefDescription(material.getBriefDescription());
        response.setCreatedBy(material.getCreatedBy());
        response.setUpdatedBy(material.getUpdatedBy());
        response.setCreatedDate(material.getCreatedDate());
        response.setUpdatedDate(material.getUpdatedDate());

        response.setStatus(String.valueOf(material.getApprovalStatus()));
   //     response.setMaterialFile(material.getUploadImageName());
        if (material.getUploadImageName() == null || material.getUploadImageName().isEmpty()) {
            response.setMaterialFile(null);
        } else {
            response.setMaterialFile(convertFilesToBase64(material.getUploadImageName(), basePath));
        }
        return response;

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

    private MaterialTransitionHistory mapToResponseDto(MaterialStatus status) {

        MaterialTransitionHistory history = new MaterialTransitionHistory();

        history.setId(status.getId());
        history.setMaterialCode(status.getMaterialCode());
        history.setAction(status.getAction());
        history.setStatus(status.getStatus());
        history.setComments(status.getComments());
        history.setCreatedBy(status.getCreatedBy());
        history.setUpdatedBy(status.getUpdatedBy());
        history.setCreatedDate(status.getCreatedDate());
        history.setUpdatedDate(status.getUpdatedDate());

        return history;
    }



}

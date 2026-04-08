package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.MaterialMasterRequestDto;
import com.astro.dto.workflow.MaterialMasterResponseDto;
import com.astro.dto.workflow.MaterialSearchResponseDto;
import com.astro.entity.MaterialMaster;
import com.astro.entity.MaterialMasterUtil;
import com.astro.entity.ProcurementModule.IndentCreation;
import com.astro.entity.VendorNamesForJobWorkMaterial;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.MaterialMasterRepository;
import com.astro.repository.MaterialMasterUtilRepository;
import com.astro.repository.VendorNamesForJobWorkMaterialRepository;
import com.astro.service.MaterialMasterService;
import com.astro.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class MaterialMasterServiceImpl implements MaterialMasterService {

    @Autowired
    private MaterialMasterRepository materialMasterRepository;
    @Autowired
    private MaterialMasterUtilRepository materialMasterUtilRepository;

    @Autowired
    private VendorNamesForJobWorkMaterialRepository vendorNameRepository;
    @Value("${filePath}")
    private String bp;
    private final String basePath;

    public MaterialMasterServiceImpl(@Value("${filePath}") String bp) {
        this.basePath = bp + "/Material";
    }

    @Override
    public MaterialMasterResponseDto createMaterialMaster(MaterialMasterRequestDto materialMasterRequestDto) {
        // Check for duplicate entry: same description + category + sub-category
        if (materialMasterRepository.existsByCategoryAndSubCategoryAndDescriptionIgnoreCase(
                materialMasterRequestDto.getCategory(),
                materialMasterRequestDto.getSubCategory(),
                materialMasterRequestDto.getDescription())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Material Entry",
                    "A material with the same description, category, and sub-category already exists.");
            throw new InvalidInputException(errorDetails);
        }

        String materialCode = "M" + System.currentTimeMillis();
        MaterialMaster materialMaster= new MaterialMaster();
        materialMaster.setMaterialCode(materialCode);
        materialMaster.setCategory(materialMasterRequestDto.getCategory());
        materialMaster.setSubCategory(materialMasterRequestDto.getSubCategory());
        materialMaster.setDescription(materialMasterRequestDto.getDescription());
        materialMaster.setUom(materialMasterRequestDto.getUom());
     //   materialMaster.setEndOfLife(materialMasterRequestDto.getEndOfLife());
     //   materialMaster.setModeOfProcurement(materialMasterRequestDto.getModeOfProcurement());
    //    materialMaster.setDepreciationRate(materialMasterRequestDto.getDepreciationRate());
       // materialMaster.setStockLevels(materialMasterRequestDto.getStockLevels());
      //  materialMaster.setConditionOfGoods(materialMasterRequestDto.getConditionOfGoods());
     //   materialMaster.setShelfLife(materialMasterRequestDto.getShelfLife());
        materialMaster.setUnitPrice(materialMasterRequestDto.getUnitPrice());
        materialMaster.setCurrency(materialMasterRequestDto.getCurrency());
     //   materialMaster.setUploadImageName(materialMasterRequestDto.getUploadImageFileName());
        materialMaster.setIndigenousOrImported(materialMasterRequestDto.getIndigenousOrImported());
        materialMaster.setEstimatedPriceWithCcy(materialMasterRequestDto.getEstimatedPriceWithCcy());
        materialMaster.setCreatedBy(materialMasterRequestDto.getCreatedBy());
        materialMaster.setUpdatedBy(materialMasterRequestDto.getUpdatedBy());
        materialMasterRepository.save(materialMaster);
      /*  // Saveing Vendornames in different table
        if (materialMasterRequestDto.getVendorNames() != null && !materialMasterRequestDto.getVendorNames().isEmpty()) {
            List<VendorNamesForJobWorkMaterial> vendors = materialMasterRequestDto.getVendorNames().stream().map(vendorName -> {
                VendorNamesForJobWorkMaterial vendor = new VendorNamesForJobWorkMaterial();
                vendor.setVendorName(vendorName);
                vendor.setMaterialCode(materialCode);
                return vendor;
            }).collect(Collectors.toList());

            vendorNameRepository.saveAll(vendors);
        }

       */
        return mapToResponseDTO(materialMaster);
    }


   /* @Override
    public MaterialMasterResponseDto updateMaterialMaster(String materialCode, MaterialMasterRequestDto materialMasterRequestDto) {
        MaterialMaster materialMaster = materialMasterRepository.findById(materialCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Materail master not found for the provided Material master ID.")
                ));

        materialMaster.setCategory(materialMasterRequestDto.getCategory());
        materialMaster.setSubCategory(materialMasterRequestDto.getSubCategory());
        materialMaster.setDescription(materialMasterRequestDto.getDescription());
        materialMaster.setUom(materialMasterRequestDto.getUom());
        materialMaster.setUnitPrice(materialMasterRequestDto.getUnitPrice());
        materialMaster.setCurrency(materialMasterRequestDto.getCurrency());
        materialMaster.setUnitPrice(materialMasterRequestDto.getUnitPrice());
        String existingFiles = materialMaster.getUploadImageName();
        String newFiles = materialMasterRequestDto.getUploadImageFileName();

        if (existingFiles != null && !existingFiles.isEmpty()) {
            materialMaster.setUploadImageName(existingFiles + "," + newFiles);
        } else {
            materialMaster.setUploadImageName(newFiles);
        }
        materialMaster.setIndigenousOrImported(materialMasterRequestDto.getIndigenousOrImported());
        materialMaster.setEstimatedPriceWithCcy(materialMasterRequestDto.getEstimatedPriceWithCcy());
        materialMaster.setUpdatedBy(materialMasterRequestDto.getUpdatedBy());
        materialMasterRepository.save(materialMaster);

        return mapToResponseDTO(materialMaster);

    }*/
  /* @Override
   public MaterialMasterResponseDto updateMaterialMaster(
           String materialCode, MaterialMasterRequestDto materialMasterRequestDto) {

       MaterialMaster materialMaster = materialMasterRepository.findById(materialCode)
               .orElseThrow(() -> new BusinessException(
                       new ErrorDetails(
                               AppConstant.ERROR_CODE_RESOURCE,
                               AppConstant.ERROR_TYPE_CODE_RESOURCE,
                               AppConstant.ERROR_TYPE_VALIDATION,
                               "Material master not found for the provided Material master ID.")
               ));

       materialMaster.setCategory(materialMasterRequestDto.getCategory());
       materialMaster.setSubCategory(materialMasterRequestDto.getSubCategory());
       materialMaster.setDescription(materialMasterRequestDto.getDescription());
       materialMaster.setUom(materialMasterRequestDto.getUom());
       materialMaster.setUnitPrice(materialMasterRequestDto.getUnitPrice());
       materialMaster.setCurrency(materialMasterRequestDto.getCurrency());
       materialMaster.setIndigenousOrImported(materialMasterRequestDto.getIndigenousOrImported());
       materialMaster.setEstimatedPriceWithCcy(materialMasterRequestDto.getEstimatedPriceWithCcy());
       materialMaster.setUpdatedBy(materialMasterRequestDto.getUpdatedBy());
       materialMaster.setStatusOfMaterialActiveOrDeactive(materialMasterRequestDto.getMaterialStatus());

       materialMaster.setReasonForDeactive(materialMasterRequestDto.getReasonForDeactive());
       String existingFiles = materialMaster.getUploadImageName();


       List<String> newFilesList = materialMasterRequestDto.getUploadImageFileName();

       String uploadedFileNames = null;


       if (newFilesList != null && !newFilesList.isEmpty()) {
           uploadedFileNames = saveBase64Files(newFilesList, basePath); // returns comma-separated file names
           materialMaster.setUploadImageName(uploadedFileNames);
       }else{
           materialMaster.setUploadImageName(existingFiles);
       }



       materialMasterRepository.save(materialMaster);

       return mapToResponseDTO(materialMaster);
   }*/
   @Override
   public MaterialMasterResponseDto updateMaterialMaster(
           String materialCode, MaterialMasterRequestDto materialMasterRequestDto) {


       Optional<MaterialMaster> optMain = materialMasterRepository.findById(materialCode);

       if (optMain.isPresent()) {
           MaterialMaster materialMaster = optMain.get();

           materialMaster.setCategory(materialMasterRequestDto.getCategory());
           materialMaster.setSubCategory(materialMasterRequestDto.getSubCategory());
           materialMaster.setDescription(materialMasterRequestDto.getDescription());
           materialMaster.setUom(materialMasterRequestDto.getUom());
           materialMaster.setUnitPrice(materialMasterRequestDto.getUnitPrice());
           materialMaster.setCurrency(materialMasterRequestDto.getCurrency());
           materialMaster.setIndigenousOrImported(materialMasterRequestDto.getIndigenousOrImported());
           materialMaster.setEstimatedPriceWithCcy(materialMasterRequestDto.getEstimatedPriceWithCcy());
           materialMaster.setUpdatedBy(materialMasterRequestDto.getUpdatedBy());
           materialMaster.setStatusOfMaterialActiveOrDeactive(materialMasterRequestDto.getMaterialStatus());
           materialMaster.setReasonForDeactive(materialMasterRequestDto.getReasonForDeactive());

           String existingFiles = materialMaster.getUploadImageName();
           List<String> newFilesList = materialMasterRequestDto.getUploadImageFileName();

           if (newFilesList != null && !newFilesList.isEmpty()) {
               String uploadedFileNames = saveBase64Files(newFilesList, basePath);
               materialMaster.setUploadImageName(uploadedFileNames);
           } else {
               materialMaster.setUploadImageName(existingFiles);
           }

           materialMasterRepository.save(materialMaster);
           return mapToResponseDTO(materialMaster);
       }


       MaterialMasterUtil util = materialMasterUtilRepository.findById(materialCode)
               .orElseThrow(() -> new BusinessException(
                       new ErrorDetails(
                               AppConstant.ERROR_CODE_RESOURCE,
                               AppConstant.ERROR_TYPE_CODE_RESOURCE,
                               AppConstant.ERROR_TYPE_VALIDATION,
                               "Material not found in both main and util tables."
                       )
               ));

       util.setCategory(materialMasterRequestDto.getCategory());
       util.setSubCategory(materialMasterRequestDto.getSubCategory());
       util.setDescription(materialMasterRequestDto.getDescription());
       util.setUom(materialMasterRequestDto.getUom());
       util.setUnitPrice(materialMasterRequestDto.getUnitPrice());
       util.setCurrency(materialMasterRequestDto.getCurrency());
       util.setIndigenousOrImported(materialMasterRequestDto.getIndigenousOrImported());
       util.setEstimatedPriceWithCcy(materialMasterRequestDto.getEstimatedPriceWithCcy());
       util.setUpdatedBy(materialMasterRequestDto.getUpdatedBy());
      // util.setComments(materialMasterRequestDto.getComments()); // optional

       util.setApprovalStatus(MaterialMasterUtil.ApprovalStatus.AWAITING_APPROVAL);
       List<String> newFilesList2 = materialMasterRequestDto.getUploadImageFileName();
       String existingUtilFiles = util.getUploadImageName();

       if (newFilesList2 != null && !newFilesList2.isEmpty()) {
           String uploadedFileNames = saveBase64Files(newFilesList2, basePath);
           util.setUploadImageName(uploadedFileNames);
       } else {
           util.setUploadImageName(existingUtilFiles);
       }

       util.setUpdatedDate(LocalDateTime.now());

       materialMasterUtilRepository.save(util);


       return mapUtilToResponseDTO(util);
   }

    private MaterialMasterResponseDto mapUtilToResponseDTO(MaterialMasterUtil util) {
        MaterialMasterResponseDto dto = new MaterialMasterResponseDto();

        dto.setMaterialCode(util.getMaterialCode());
        dto.setCategory(util.getCategory());
        dto.setSubCategory(util.getSubCategory());
        dto.setDescription(util.getDescription());
        dto.setUom(util.getUom());
        dto.setUnitPrice(util.getUnitPrice());
        dto.setCurrency(util.getCurrency());
        dto.setEstimatedPriceWithCcy(util.getEstimatedPriceWithCcy());
        dto.setIndigenousOrImported(util.getIndigenousOrImported());
      //  dto.setUploadImageName(util.getUploadImageName());
      //  dto.setComments(util.getComments());
        dto.setMaterialStatus(util.getApprovalStatus().name());
        dto.setUpdatedBy(util.getUpdatedBy());

        return dto;
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
    public List<MaterialMasterResponseDto> getAllMaterialMasters() {
        List<MaterialMaster> materialMasters= materialMasterRepository.findActiveMaterials();
        return materialMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public MaterialMasterResponseDto getMaterialMasterById(String materialCode) {
        MaterialMaster materialMaster= materialMasterRepository.findById(materialCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "material master not found for the provided materialcode.")
                ));
        return mapToResponseDTO(materialMaster);
    }
    /*
    @Override
    public MaterialMasterResponseDto getMaterialMasterByIdBase64(String materialCode) throws IOException {
        MaterialMaster materialMaster= materialMasterRepository.findById(materialCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "material master not found for the provided materialcode.")
                ));
        MaterialMasterResponseDto materialMasterResponseDto = new MaterialMasterResponseDto();
        materialMasterResponseDto.setMaterialCode(materialMaster.getMaterialCode());
        materialMasterResponseDto.setCategory(materialMaster.getCategory());
        materialMasterResponseDto.setSubCategory(materialMaster.getSubCategory());
        materialMasterResponseDto.setDescription(materialMaster.getDescription());
        materialMasterResponseDto.setUom(materialMaster.getUom());
        //  materialMasterResponseDto.setEndOfLife(materialMaster.getEndOfLife());
        //   materialMasterResponseDto.setModeOfProcurement(materialMaster.getModeOfProcurement());
        materialMasterResponseDto.setCurrency(materialMaster.getCurrency());
        materialMasterResponseDto.setUnitPrice(materialMaster.getUnitPrice());
        //  materialMasterResponseDto.setDepreciationRate(materialMaster.getDepreciationRate());
        //  materialMasterResponseDto.setStockLevels(materialMaster.getStockLevels());
        //  materialMasterResponseDto.setConditionOfGoods(materialMaster.getConditionOfGoods());
        // materialMasterResponseDto.setShelfLife(materialMaster.getShelfLife());
        materialMasterResponseDto.setUploadImageFileName(materialMaster.getUploadImageName());
        materialMasterResponseDto.setIndigenousOrImported(materialMaster.getIndigenousOrImported());
        materialMasterResponseDto.setEstimatedPriceWithCcy(materialMaster.getEstimatedPriceWithCcy());
        materialMasterResponseDto.setCreatedBy(materialMaster.getCreatedBy());
        materialMasterResponseDto.setUpdatedBy(materialMaster.getUpdatedBy());
        materialMasterResponseDto.setCreatedDate(materialMaster.getCreatedDate());
        materialMasterResponseDto.setUpdatedDate(materialMaster.getUpdatedDate());
        if (materialMaster.getUploadImageName() == null || materialMaster.getUploadImageName().isEmpty()) {
            materialMasterResponseDto.setMaterialFile(null);
        } else {
            materialMasterResponseDto.setMaterialFile(
                    convertFilesToBase64(materialMaster.getUploadImageName(), basePath));
        }
        materialMasterResponseDto.setStatus(materialMaster.getStatus());
/*
        List<String> vendorNames= vendorNameRepository.findByMaterialCode(materialMaster.getMaterialCode())
                .stream()
                .map(VendorNamesForJobWorkMaterial::getVendorName)
                .collect(Collectors.toList());
        materialMasterResponseDto.setVendorNames(vendorNames);

 */
     /*   return materialMasterResponseDto;

    }*/
    @Override
    public MaterialMasterResponseDto getMaterialMasterByIdBase64(String materialCode) throws IOException {
        // Try fetching from main material master first
        Optional<MaterialMaster> materialOpt = materialMasterRepository.findById(materialCode);

        if (materialOpt.isPresent()) {
            MaterialMaster materialMaster = materialOpt.get();
            return buildMaterialResponseFromMaster(materialMaster);
        }
        Optional<MaterialMasterUtil> materialUtilOpt = materialMasterUtilRepository.findById(materialCode);

        if (materialUtilOpt.isPresent()) {
            MaterialMasterUtil util = materialUtilOpt.get();
            MaterialMasterResponseDto dto = new MaterialMasterResponseDto();

            dto.setMaterialCode(util.getMaterialCode());
            dto.setCategory(util.getCategory());
            dto.setSubCategory(util.getSubCategory());
            dto.setDescription(util.getDescription());
            dto.setUom(util.getUom());
            dto.setUnitPrice(util.getUnitPrice());
            dto.setCurrency(util.getCurrency());
            dto.setEstimatedPriceWithCcy(util.getEstimatedPriceWithCcy());
            dto.setUploadImageFileName(util.getUploadImageName());
            dto.setIndigenousOrImported(util.getIndigenousOrImported());
            dto.setCreatedBy(util.getCreatedBy());
            dto.setUpdatedBy(util.getUpdatedBy());
            dto.setCreatedDate(util.getCreatedDate());
            dto.setUpdatedDate(util.getUpdatedDate());
            dto.setBriefDescription(util.getBriefDescription());
            // Added byb Aman 
            dto.setAsset_Flag(util.getAssetFlag());
            // End

            dto.setStatus(util.getApprovalStatus() != null ? util.getApprovalStatus().name() : null);

            // Handle file conversion to base64 (if available)
            if (util.getUploadImageName() == null || util.getUploadImageName().isEmpty()) {
                dto.setMaterialFile(null);
            } else {
                dto.setMaterialFile(convertFilesToBase64(util.getUploadImageName(), basePath));
            }

            return dto;
        }


        throw new BusinessException(
                new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Material not found in MaterialMaster or MaterialMasterUtil for code: " + materialCode
                )
        );
    }
    private MaterialMasterResponseDto buildMaterialResponseFromMaster(MaterialMaster materialMaster) throws IOException {
        MaterialMasterResponseDto dto = new MaterialMasterResponseDto();

        dto.setMaterialCode(materialMaster.getMaterialCode());
        dto.setCategory(materialMaster.getCategory());
        dto.setSubCategory(materialMaster.getSubCategory());
        dto.setDescription(materialMaster.getDescription());
        dto.setUom(materialMaster.getUom());
        dto.setUnitPrice(materialMaster.getUnitPrice());
        dto.setCurrency(materialMaster.getCurrency());
        dto.setEstimatedPriceWithCcy(materialMaster.getEstimatedPriceWithCcy());
        dto.setUploadImageFileName(materialMaster.getUploadImageName());
        dto.setIndigenousOrImported(materialMaster.getIndigenousOrImported());
        dto.setCreatedBy(materialMaster.getCreatedBy());
        dto.setUpdatedBy(materialMaster.getUpdatedBy());
        dto.setCreatedDate(materialMaster.getCreatedDate());
        dto.setUpdatedDate(materialMaster.getUpdatedDate());
        dto.setStatus(materialMaster.getStatus());
        dto.setMaterialStatus(materialMaster.getStatusOfMaterialActiveOrDeactive());
        dto.setReasonForDeactive(materialMaster.getReasonForDeactive());
        dto.setBriefDescription(materialMaster.getBriefDescription());
        // Added by Aman
        dto.setAsset_Flag(materialMaster.getAssetFlag());
        // End

        if (materialMaster.getUploadImageName() == null || materialMaster.getUploadImageName().isEmpty()) {
            dto.setMaterialFile(null);
        } else {
            dto.setMaterialFile(convertFilesToBase64(materialMaster.getUploadImageName(), basePath));
        }

        return dto;
    }


    @Override
    public void deleteMaterialMaster(String materialCode) {


        MaterialMaster materialMaster=materialMasterRepository.findById(materialCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Material master not found for the provided material code."
                        )
                ));
        try {
            materialMasterRepository.delete(materialMaster);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the material master."
                    ),
                    ex
            );
        }


    }
    private MaterialMasterResponseDto mapToResponseDTO(MaterialMaster materialMaster) {

        MaterialMasterResponseDto materialMasterResponseDto = new MaterialMasterResponseDto();
        materialMasterResponseDto.setMaterialCode(materialMaster.getMaterialCode());
        materialMasterResponseDto.setCategory(materialMaster.getCategory());
        materialMasterResponseDto.setSubCategory(materialMaster.getSubCategory());
        materialMasterResponseDto.setDescription(materialMaster.getDescription());
        materialMasterResponseDto.setUom(materialMaster.getUom());
      //  materialMasterResponseDto.setEndOfLife(materialMaster.getEndOfLife());
     //   materialMasterResponseDto.setModeOfProcurement(materialMaster.getModeOfProcurement());
        materialMasterResponseDto.setCurrency(materialMaster.getCurrency());
        materialMasterResponseDto.setUnitPrice(materialMaster.getUnitPrice());
      //  materialMasterResponseDto.setDepreciationRate(materialMaster.getDepreciationRate());
      //  materialMasterResponseDto.setStockLevels(materialMaster.getStockLevels());
      //  materialMasterResponseDto.setConditionOfGoods(materialMaster.getConditionOfGoods());
       // materialMasterResponseDto.setShelfLife(materialMaster.getShelfLife());
        materialMasterResponseDto.setUploadImageFileName(materialMaster.getUploadImageName());
        materialMasterResponseDto.setIndigenousOrImported(materialMaster.getIndigenousOrImported());
        materialMasterResponseDto.setEstimatedPriceWithCcy(materialMaster.getEstimatedPriceWithCcy());
        materialMasterResponseDto.setCreatedBy(materialMaster.getCreatedBy());
        materialMasterResponseDto.setUpdatedBy(materialMaster.getUpdatedBy());
        materialMasterResponseDto.setCreatedDate(materialMaster.getCreatedDate());
        materialMasterResponseDto.setUpdatedDate(materialMaster.getUpdatedDate());
        // Added by Aman
        materialMasterResponseDto.setAsset_Flag(materialMaster.getAssetFlag());
        // End

/*
        List<String> vendorNames= vendorNameRepository.findByMaterialCode(materialMaster.getMaterialCode())
                .stream()
                .map(VendorNamesForJobWorkMaterial::getVendorName)
                .collect(Collectors.toList());
        materialMasterResponseDto.setVendorNames(vendorNames);

 */
        return materialMasterResponseDto;



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
    public List<MaterialSearchResponseDto> searchMaterials(String keyword) {
        List<Object[]> results = materialMasterRepository.searchMaterialsForDropdown(keyword);
        return mapSearchResults(results);
    }

    @Override
    public List<MaterialSearchResponseDto> searchMaterialsByCategory(String keyword, String materialCategoryType) {
        List<Object[]> results;

        if ("computer".equalsIgnoreCase(materialCategoryType)) {
            // Matches subCategory containing "computer" (e.g., "COMPUTER", "Computer & Peripherals")
            results = materialMasterRepository.searchApprovedMaterialsComputer(keyword);
        } else if ("non-computer".equalsIgnoreCase(materialCategoryType)) {
            // Matches subCategory NOT containing "computer" (e.g., "CEMENT", "STEEL", "Electrical")
            results = materialMasterRepository.searchApprovedMaterialsNonComputer(keyword);
        } else {
            // "all" or null - return all approved active materials
            results = materialMasterRepository.searchMaterialsForDropdown(keyword);
        }

        return mapSearchResults(results);
    }

    private List<MaterialSearchResponseDto> mapSearchResults(List<Object[]> results) {
        return results.stream()
                .map(obj -> new MaterialSearchResponseDto(
                        (String) obj[0],                                          // materialCode
                        (String) obj[1],                                          // description
                        (String) obj[2],                                          // category
                        (String) obj[3],                                          // subCategory
                        (String) obj[4],                                          // uom
                        obj[5] != null ? new java.math.BigDecimal(obj[5].toString()) : null, // unitPrice
                        (String) obj[6]                                           // currency
                ))
                .collect(Collectors.toList());
    }


}

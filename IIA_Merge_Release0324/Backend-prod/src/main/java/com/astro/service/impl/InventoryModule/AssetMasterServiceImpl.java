package com.astro.service.impl.InventoryModule;

import com.astro.dto.workflow.AssetDataForGtDto;
import com.astro.dto.workflow.AssetSearchResponseDto;
import com.astro.dto.workflow.InventoryModule.*;
import com.astro.dto.workflow.InventoryModule.asset.AssetMasterReportDto;
import com.astro.dto.workflow.InventoryModule.asset.AssetOhqDisposalDto;
import com.astro.entity.InventoryModule.*;
import com.astro.entity.UserMaster;
import com.astro.repository.InventoryModule.*;
import com.astro.repository.UserMasterRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.validator.internal.metadata.aggregated.rule.OverridingMethodMustNotAlterParameterConstraints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.astro.util.CommonUtils;

import javax.transaction.Transactional;

import com.astro.service.InventoryModule.AssetMasterService;
import com.astro.service.impl.AssetServiceImpl;
import com.astro.util.CommonUtils;
import com.astro.repository.ohq.OhqMasterRepository;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.constant.AppConstant;

@Service
public class AssetMasterServiceImpl implements AssetMasterService {

    @Value("${filePath}")
    private String bp;

    @Autowired
    private AssetMasterRepository assetMasterRepository;    
    
    @Autowired
    private AssetDisposalMasterRepository disposalMasterRepository;
    
    @Autowired
    private AssetDisposalDetailRepository disposalDetailRepository;
    
    @Autowired
    private OhqMasterRepository ohqMasterRepository;
    @Autowired
    private UserMasterRepository userMasterRepository;

    private final String basePath;

    @Autowired
    private OhqMasterConsumableRepository ohqMasterConsumableRepository;
    @Autowired
    private OhqConsumableStoreStockRepository ohqStoreStockRepo;
    @Autowired
    private AssetDisposalAuctionEntityRepository assetDisposalAuctionEntityRepository;
    @Autowired
    private AssetDisposalAuctionDetailEntityRepository assetDisposalAuctionDetailEntityRepository;
    @Autowired
    private AssetSerialEntityRepository assetSerialEntityRepository;

    public AssetMasterServiceImpl(@Value("${filePath}") String bp) {
        this.basePath = bp + "/INV";
    }

    @Override
    @Transactional
    public String saveAssetMaster(AssetMasterDto request) {
        // Validate duplicate asset
        if (assetMasterRepository.existsByMaterialCodeAndMaterialDescAndMakeNoAndModelNoAndSerialNoAndUomId(
                request.getMaterialCode(),
                request.getMaterialDesc(),
                request.getMakeNo(),
                request.getModelNo(),
                request.getSerialNo(),
                request.getUomId())) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Asset already exists with the provided details"));
        }

        ModelMapper mapper = new ModelMapper();
        AssetMasterEntity asset = mapper.map(request, AssetMasterEntity.class);
        
        // Convert end of life string to LocalDate
        if (request.getEndOfLife() != null && !request.getEndOfLife().trim().isEmpty()) {
            asset.setEndOfLife(CommonUtils.convertStringToDateObject(request.getEndOfLife()));
        }
        
        asset.setCreateDate(LocalDateTime.now());
        asset.setUpdatedDate(LocalDateTime.now());

        asset = assetMasterRepository.save(asset);
        return asset.getAssetId().toString();
    }

    @Override
    @Transactional
    public String updateAssetMaster(AssetMasterDto request) {
        if (request.getAssetId() == null) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Asset ID is required for update"));
        }

        AssetMasterEntity existingAsset = assetMasterRepository.findById(request.getAssetId())
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Asset not found with ID: " + request.getAssetId())));

        ModelMapper mapper = new ModelMapper();
        mapper.map(request, existingAsset);
        
        // Convert end of life string to LocalDate
        if (request.getEndOfLife() != null && !request.getEndOfLife().trim().isEmpty()) {
            existingAsset.setEndOfLife(CommonUtils.convertIsoDateStringToDateObject(request.getEndOfLife()));
        }else{
            existingAsset.setEndOfLife(null);
        }
        
        existingAsset.setUpdatedDate(LocalDateTime.now());
        existingAsset.setDepriciationRate(request.getDepriciationRate());
        
        assetMasterRepository.save(existingAsset);
        return existingAsset.getAssetId().toString();
    }

    @Override
    @Transactional
    public String saveAssetDisposal(AssetDisposalDto request) {
        AssetDisposalMasterEntity disposalMaster = new AssetDisposalMasterEntity();
        
        // Convert string date to LocalDate
        if (request.getDisposalDate() != null && !request.getDisposalDate().trim().isEmpty()) {
            disposalMaster.setDisposalDate(CommonUtils.convertStringToDateObject(request.getDisposalDate()));
        }
        
        disposalMaster.setCreatedBy(request.getCreatedBy());
        disposalMaster.setCreateDate(LocalDateTime.now());
        disposalMaster.setLocationId(request.getLocationId());
      //  disposalMaster.setVendorId(request.getVendorId());
        disposalMaster.setCustodianId(request.getCustodianId());
        disposalMaster.setStatus("For Disposal");
        disposalMaster.setAction("Awaiting For Approval");
        disposalMaster = disposalMasterRepository.save(disposalMaster);
        
        List<AssetDisposalDetailEntity> disposalDetails = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();
        boolean errorFound = false;

        // Process each disposal detail
        for (AssetDisposalDetailDto detailDto : request.getMaterialDtlList()) {
            // Validate OHQ stock
            OhqMasterEntity ohq = ohqMasterRepository
                    .findByAssetIdAndLocatorIdAndCustodianId(detailDto.getAssetId(), detailDto.getLocatorId(), detailDto.getCustodianId())
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "No stock found for asset ID: " + detailDto.getAssetId() + 
                            " at locator: " + detailDto.getLocatorId())));

            BigDecimal remainingQuantity = ohq.getQuantity().subtract(detailDto.getQuantity());
            
            if (remainingQuantity.compareTo(BigDecimal.ZERO) < 0) {
                errorMessage.append("Insufficient quantity for asset ID: ")
                        .append(detailDto.getAssetId())
                        .append(". Available: ")
                        .append(ohq.getQuantity())
                        .append(", Requested: ")
                        .append(detailDto.getQuantity())
                        .append(". ");
                errorFound = true;
                continue;
            }

            // Create disposal detail
            AssetDisposalDetailEntity detail = new AssetDisposalDetailEntity();
            detail.setDisposalId(disposalMaster.getDisposalId());
            detail.setAssetId(detailDto.getAssetId());
            detail.setAssetCode(detailDto.getAssetCode());
            detail.setAssetDesc(detailDto.getAssetDesc());
            detail.setDisposalQuantity(detailDto.getQuantity());
            detail.setDisposalCategory(detailDto.getDisposalCategory());
            detail.setDisposalMode(detailDto.getDisposalMode());

            detail.setOhqId(detailDto.getOhqId());
            detail.setLocatorId(detailDto.getLocatorId());
            detail.setBookValue(detailDto.getBookValue());
            detail.setDepriciationRate(detailDto.getDepriciationRate());
            detail.setUnitPrice(detailDto.getUnitPrice());
            detail.setCustodianId(detailDto.getCustodianId());
            detail.setPoValue(detailDto.getPoValue());
            detail.setReasonForDisposal(detailDto.getReasonForDisposal());
            detail.setPoId(detailDto.getPoId());
            detail.setSerialNo(detailDto.getSerialNo());
            detail.setModelNo(detailDto.getModelNo());
            detail.setPoDate(CommonUtils.convertStringToDateObject(detailDto.getPoDate()));
            if(Objects.nonNull(detailDto.getSalesNoteFilename())){

                try {
                    String  file = CommonUtils.saveBase64Image(detailDto.getSalesNoteFilename(), basePath);
                    detailDto.setSalesNoteFilename(file);
                } catch (Exception e) {
                    throw new BusinessException(new ErrorDetails(
                        AppConstant.FILE_UPLOAD_ERROR,
                        AppConstant.USER_INVALID_INPUT,
                        AppConstant.ERROR_TYPE_CORRUPTED,
                        "Error while uploading image."));
                }

                 // Update the DTO with the file path
            }
            
            disposalDetails.add(detail);

            // Update OHQ
            ohq.setQuantity(remainingQuantity);
            ohqMasterRepository.save(ohq);

            // Mark serial number as disposed
            if (detailDto.getSerialNo() != null && !detailDto.getSerialNo().isEmpty()) {
                AssetSerialEntity serial = assetSerialEntityRepository
                        .findByAssetIdAndCustodianIdAndLocatorIdAndSerialNo(
                                detailDto.getAssetId(),
                                detailDto.getCustodianId(),
                                detailDto.getLocatorId(),
                                detailDto.getSerialNo()
                        )
                        .orElse(null);

                if (serial != null) {
                    serial.setStatus("Disposed");
                    assetSerialEntityRepository.save(serial);
                }
            }

        }

        if (errorFound) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    errorMessage.toString()));
        }

        disposalDetailRepository.saveAll(disposalDetails);
        
        return "INV/" + disposalMaster.getDisposalId().toString();
    }
    @Override
    public List<AssetDisposalDto> getAllAssetDisposalAwaitingForApproval() {
        List<AssetDisposalMasterEntity> masters = disposalMasterRepository.findAllAwaitingForApproval();
        List<AssetDisposalDto> result = new ArrayList<>();

        for (AssetDisposalMasterEntity master : masters) {
            AssetDisposalDto dto = new AssetDisposalDto();
            dto.setDisposalId(master.getDisposalId());
            dto.setDisposalDate(master.getDisposalDate() != null ? master.getDisposalDate().toString() : null);
            dto.setCreatedBy(master.getCreatedBy());
            dto.setLocationId(master.getLocationId());
            String userName = userMasterRepository.findUserNameByUserId(Integer.valueOf(master.getCustodianId()));
            dto.setCustodianName(userName);

            dto.setCustodianId(master.getCustodianId());

            List<AssetDisposalDetailEntity> details = disposalDetailRepository.findByDisposalId(master.getDisposalId());
            List<AssetDisposalDetailDto> detailDtos = new ArrayList<>();

            for (AssetDisposalDetailEntity detail : details) {
                AssetDisposalDetailDto dDto = new AssetDisposalDetailDto();
                dDto.setAssetId(detail.getAssetId());
                dDto.setAssetCode(detail.getAssetCode());
                dDto.setAssetDesc(detail.getAssetDesc());
                dDto.setQuantity(detail.getDisposalQuantity());
                dDto.setDisposalCategory(detail.getDisposalCategory());
                dDto.setDisposalMode(detail.getDisposalMode());
                dDto.setSalesNoteFilename(detail.getSalesNoteFilename());
                dDto.setLocatorId(detail.getLocatorId());
                dDto.setOhqId(detail.getOhqId());
                dDto.setBookValue(detail.getBookValue());
                dDto.setDepriciationRate(detail.getDepriciationRate());
                dDto.setUnitPrice(detail.getUnitPrice());
                dDto.setCustodianId(detail.getCustodianId());
                dDto.setPoValue(detail.getPoValue());
                dDto.setReasonForDisposal(detail.getReasonForDisposal());
                dDto.setPoDate(CommonUtils.convertDateToString(detail.getPoDate()));
                dDto.setPoId(detail.getPoId());
                dDto.setModelNo(detail.getModelNo());
                dDto.setSerialNo(detail.getSerialNo());
                detailDtos.add(dDto);
            }

            dto.setMaterialDtlList(detailDtos);
            result.add(dto);
        }

        return result;
    }
    @Override
    public List<AssetDisposalDto> getAllApprovedAssetDisposalReport() {
        List<AssetDisposalMasterEntity> masters = disposalMasterRepository.findAllApprovedAssetDisposals();
        List<AssetDisposalDto> result = new ArrayList<>();

        for (AssetDisposalMasterEntity master : masters) {
            AssetDisposalDto dto = new AssetDisposalDto();
            dto.setDisposalId(master.getDisposalId());
            dto.setDisposalDate(master.getDisposalDate() != null ? master.getDisposalDate().toString() : null);
            dto.setCreatedBy(master.getCreatedBy());
            dto.setLocationId(master.getLocationId());
            dto.setCustodianId(master.getCustodianId());

            dto.setStatus(master.getStatus());
            dto.setAction(master.getAction());
            List<AssetDisposalDetailEntity> details = disposalDetailRepository.findByDisposalId(master.getDisposalId());
            List<AssetDisposalDetailDto> detailDtos = new ArrayList<>();

            for (AssetDisposalDetailEntity detail : details) {
                AssetDisposalDetailDto dDto = new AssetDisposalDetailDto();
                dDto.setAssetId(detail.getAssetId());
                dDto.setAssetCode(detail.getAssetCode());
                dDto.setAssetDesc(detail.getAssetDesc());
                dDto.setQuantity(detail.getDisposalQuantity());
                dDto.setDisposalCategory(detail.getDisposalCategory());
                dDto.setDisposalMode(detail.getDisposalMode());
                dDto.setSalesNoteFilename(detail.getSalesNoteFilename());
                dDto.setLocatorId(detail.getLocatorId());
                dDto.setOhqId(detail.getOhqId());
                dDto.setBookValue(detail.getBookValue());
                dDto.setSerialNo(detail.getSerialNo());
                dDto.setDepriciationRate(detail.getDepriciationRate());
                dDto.setUnitPrice(detail.getUnitPrice());
                dDto.setCustodianId(detail.getCustodianId());
                dDto.setPoValue(detail.getPoValue());
                dDto.setReasonForDisposal(detail.getReasonForDisposal());
                detailDtos.add(dDto);
            }

            dto.setMaterialDtlList(detailDtos);
            result.add(dto);
        }

        return result;
    }
    @Transactional
    public void approveDisposal(String disposalIdStr) {
      //  Integer disposalId = Integer.valueOf(disposalIdStr.split("/")[1]); // Extract ID from string if needed
        Integer disposalId = Integer.valueOf(disposalIdStr);
        AssetDisposalMasterEntity disposalMaster = disposalMasterRepository.findById(disposalId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Asset Disposal not found for ID: " + disposalId)));
        disposalMaster.setAction("Approved");
        disposalMasterRepository.save(disposalMaster);
    }

    @Transactional
    public void rejectDisposal(String disposalIdStr) {
      //  Integer disposalId = Integer.valueOf(disposalIdStr.split("/")[1]);
        Integer disposalId = Integer.valueOf(disposalIdStr);
        AssetDisposalMasterEntity disposalMaster = disposalMasterRepository.findById(disposalId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Asset Disposal not found for ID: " + disposalId)));

        List<AssetDisposalDetailEntity> disposalDetails = disposalDetailRepository.findByDisposalId(disposalId);

        for (AssetDisposalDetailEntity detail : disposalDetails) {
            // Find OHQ by ohqId, locatorId, and custodianId
            OhqMasterEntity ohq = ohqMasterRepository
                    .findByAssetIdAndLocatorIdAndCustodianId(detail.getAssetId(), detail.getLocatorId(), detail.getCustodianId())
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "No stock found for asset ID: " + detail.getAssetId() +
                                    " at locator: " + detail.getLocatorId())));


            // Add back quantity
            ohq.setQuantity(ohq.getQuantity().add(detail.getDisposalQuantity()));
            ohqMasterRepository.save(ohq);
        }
        disposalMaster.setAction("Rejected");
        disposalMasterRepository.save(disposalMaster);
    }

    public AssetDisposalDto getAssetDisposalById(String disposalIdStr) {
        // Extract numeric ID from "INV12"
        Integer disposalId = Integer.valueOf(disposalIdStr.split("/")[1]);

        AssetDisposalMasterEntity master = disposalMasterRepository.findById(disposalId)
                .orElseThrow(() -> new RuntimeException("Asset Disposal not found: " + disposalIdStr));

        List<AssetDisposalDetailEntity> details = disposalDetailRepository.findByDisposalId(disposalId);

        AssetDisposalDto dto = new AssetDisposalDto();
        dto.setDisposalId(master.getDisposalId());
       // dto.setDisposalDate(master.getDisposalDate() != null ? master.getDisposalDate().toString() : null);
        dto.setDisposalDate(CommonUtils.convertDateToString(master.getDisposalDate()));
        dto.setLocationId(master.getLocationId());
        dto.setCustodianId(master.getCustodianId());
        dto.setCreatedBy(master.getCreatedBy());
        dto.setAction(master.getAction());
        dto.setStatus(master.getStatus());
        dto.setAuctionId(master.getAuctionId());

        dto.setAuctionDate(CommonUtils.convertDateToString(master.getAuctionDate()));
        dto.setAuctionPrice(master.getAuctionPrice());
        dto.setReservePrice(master.getReservePrice());
        dto.setVendorName(master.getVendorName());


        dto.setMaterialDtlList(details.stream().map(this::mapToDetailDto).collect(Collectors.toList()));

        return dto;
    }

    private AssetDisposalDetailDto mapToDetailDto(AssetDisposalDetailEntity detail) {
        AssetDisposalDetailDto dto = new AssetDisposalDetailDto();
        dto.setAssetId(detail.getAssetId());
        dto.setAssetDesc(detail.getAssetDesc());
        dto.setQuantity(detail.getDisposalQuantity());
        dto.setDisposalCategory(detail.getDisposalCategory());
        dto.setDisposalMode(detail.getDisposalMode());
        dto.setSalesNoteFilename(detail.getSalesNoteFilename());
        dto.setLocatorId(detail.getLocatorId());
        dto.setOhqId(detail.getOhqId());
        dto.setBookValue(detail.getBookValue());
        dto.setDepriciationRate(detail.getDepriciationRate());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setCustodianId(detail.getCustodianId());
        dto.setPoValue(detail.getPoValue());
        dto.setReasonForDisposal(detail.getReasonForDisposal());
        return dto;
    }

    @Override
    @Transactional
    public String updateAssetDisposal(AssetDisposalDto request) {
      //  Integer disposalId = Integer.valueOf(request.getDisposalId().split("/")[1]);
        AssetDisposalMasterEntity disposalMaster = disposalMasterRepository
                .findById(Integer.valueOf(request.getDisposalId()))
                .orElseThrow(() -> new RuntimeException("Disposal not found: " + request.getDisposalId()));

        String status = request.getStatus();

        if ("Disposed".equalsIgnoreCase(status)) {
            // Update disposal master fields
            disposalMaster.setStatus("Disposed");
            disposalMaster.setAuctionId(request.getAuctionId());
            if (request.getAuctionDate() != null && !request.getAuctionDate().trim().isEmpty()) {
                disposalMaster.setAuctionDate(CommonUtils.convertStringToDateObject(request.getAuctionDate()));
            }
          //  disposalMaster.setAuctionDate(request.getAuctionDate());
            disposalMaster.setReservePrice(request.getReservePrice());
            disposalMaster.setAuctionPrice(request.getAuctionPrice());
            disposalMaster.setVendorName(request.getVendorName());

            disposalMasterRepository.save(disposalMaster);

            // No OHQ update needed for Disposed
        }
        else if ("Removal of Disposal".equalsIgnoreCase(status)) {
            // Revert stock in OHQ
            List<AssetDisposalDetailEntity> disposalDetails = disposalDetailRepository
                    .findByDisposalId(disposalMaster.getDisposalId());

            for (AssetDisposalDetailEntity detail : disposalDetails) {
                // Find OHQ by ohqId, locatorId, and custodianId
                OhqMasterEntity ohq = ohqMasterRepository
                        .findByAssetIdAndLocatorIdAndCustodianId(detail.getAssetId(), detail.getLocatorId(), detail.getCustodianId())
                        .orElseThrow(() -> new BusinessException(new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "No stock found for asset ID: " + detail.getAssetId() +
                                        " at locator: " + detail.getLocatorId())));


                // Add back quantity
                ohq.setQuantity(ohq.getQuantity().add(detail.getDisposalQuantity()));
                ohqMasterRepository.save(ohq);

                assetSerialEntityRepository
                        .findByAssetIdAndLocatorIdAndCustodianIdAndSerialNoAndStatus(
                                detail.getAssetId(),
                                detail.getLocatorId(),
                                detail.getCustodianId(),
                                detail.getSerialNo(),
                                "Disposed")
                        .ifPresent(serial -> {
                            serial.setStatus(null);
                            assetSerialEntityRepository.save(serial);
                        });

            }

            // Update disposal status
            disposalMaster.setStatus("Removed");
            disposalMasterRepository.save(disposalMaster);
        }
        else {
            throw new RuntimeException("Invalid status: " + status);
        }

        return "INV/" + disposalMaster.getDisposalId();
    }



    @Override
    public AssetMasterDto getAssetDetails(Integer assetId) {
        System.out.println("CALLED");
        AssetMasterEntity asset = assetMasterRepository.findById(assetId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Asset not found with ID: " + assetId)));

        // System.out.println("ASEET" + asset);

        AssetMasterDto response = new AssetMasterDto();
        response.setAssetId(asset.getAssetId());
        response.setMaterialCode(asset.getMaterialCode());
        response.setMaterialDesc(asset.getMaterialDesc());
        response.setAssetDesc(asset.getAssetDesc());
        response.setMakeNo(asset.getMakeNo());
        response.setModelNo(asset.getModelNo());
        response.setSerialNo(asset.getSerialNo());
        response.setUomId(asset.getUomId());
        response.setComponentName(asset.getComponentName());
        response.setComponentId(asset.getComponentId());
        response.setInitQuantity(asset.getInitQuantity());
        response.setUnitPrice(asset.getUnitPrice());
        response.setStockLevels(asset.getStockLevels());
        response.setConditionOfGoods(asset.getConditionOfGoods());
        response.setShelfLife(asset.getShelfLife());
        response.setLocatorId(asset.getLocatorId());
        
        if (asset.getEndOfLife() != null) {
            response.setEndOfLife(asset.getEndOfLife().toString());
        }
        
        return response;
    }

    @Override
public List<AssetMasterReportDto> getAssetReport() {
    List<Object[]> results = assetMasterRepository.getAssetReport();
    
    return results.stream().map(row -> {
        AssetMasterReportDto dto = new AssetMasterReportDto();
        dto.setAssetId((Integer) row[0]);
        dto.setMaterialCode((String) row[1]);
        dto.setMaterialDesc((String) row[2]);
        dto.setAssetDesc((String) row[3]);
        dto.setMakeNo((String) row[4]);
        dto.setSerialNo((String) row[5]);
        dto.setModelNo((String) row[6]);
        dto.setInitQuantity((BigDecimal) row[7]);
        dto.setUnitPrice((BigDecimal) row[8]);
        dto.setUomId((String) row[9]);
        dto.setDepriciationRate((BigDecimal) row[10]);
        CommonUtils.convertSqlDateToString((Date) row[11]);
        // dto.setEndOfLife(row[11] != null ? ((Date) row[11]).toLocalDate() : null);
        dto.setStockLevels((BigDecimal) row[12]);
        dto.setConditionOfGoods((String) row[13]);
        dto.setShelfLife((String) row[14]);
        dto.setComponentName((String) row[15]);
        dto.setComponentId((Integer) row[16]);
        // dto.setCreateDate(((Timestamp) row[17]).toLocalDateTime());
        dto.setCreatedBy((Integer) row[18]);
        // dto.setUpdatedDate(((Timestamp) row[19]).toLocalDateTime());
        dto.setUpdatedBy((Integer) row[20]);
        dto.setPoId((String) row[21]); // po_id
        dto.setPoValue((BigDecimal) row[22]); // total_value_of_po
        dto.setVendorId((String) row[23]);
        return dto;
    }).collect(Collectors.toList());
}

@Override
public List<Integer> getAllAssetIds() {
    return assetMasterRepository.findAllAssetIds();
}

@Override
public List<OhqMasterEntity> getAssetOhqList() {
    return ohqMasterRepository.findAll();
}
@Override
public List<AssetOhqDetailsDto> getAssetOhqDetails() {
        return ohqMasterRepository.fetchAssetOhqDetails();
    }

@Override
public List<OhqMasterConsumableEntity> getAssetOhqConsumableList() {
    return ohqMasterConsumableRepository.findAll();
}
@Override
public List<OhqConsumableStoreStockEntity> getStoreStockOhqConsumableList(){
        return ohqStoreStockRepo.findAll();
}
   @Override
   public List<AssetOhqDisposalDto> getAllAssetsForDisposal() {
       List<Object[]> rows = ohqMasterRepository.getAllAssetOhqDisposalsNative();
       List<AssetOhqDisposalDto> dtos = new ArrayList<>();

       for (Object[] r : rows) {
           AssetOhqDisposalDto dto = new AssetOhqDisposalDto();
           dto.setOhqId((Integer) r[0]);
           dto.setAssetId((Integer) r[1]);
           dto.setAseetDescription((String) r[2]);
           dto.setLocatorId((Integer) r[3]);
           dto.setBookValue((BigDecimal) r[4]);
           dto.setDepriciationRate((BigDecimal) r[5]);
           dto.setUnitPrice((BigDecimal) r[6]);
           dto.setQuantity((BigDecimal) r[7]);
           dto.setCustodianId((String) r[8]);
           dto.setPoValue((BigDecimal) r[9]);
           dto.setPoId((String) r[10]);
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

           if (r[11] != null) {
               java.sql.Date sqlDate = (java.sql.Date) r[11];     // cast to java.sql.Date first
               LocalDate localDate = sqlDate.toLocalDate();       // convert to LocalDate
               dto.setGprnDate(localDate.format(formatter));     // format as dd/MM/yyyy
           } else {
               dto.setGprnDate(null);
           }

           dto.setSerialNo((String) r[12]);
           dto.setModelNo((String) r[13]);

           dto.setAssetCode((String) r[14]);
           List<String> serials = assetSerialEntityRepository.findSerialNumbers(
                   dto.getAssetId(),
                   dto.getAssetCode(),
                   dto.getLocatorId(),
                   dto.getCustodianId()
           );
           dto.setSerialNumbers(serials);
           dtos.add(dto);

       }


       return dtos;
   }


    @Override
    public List<AssetDisposalReportDto> getAssetDisposalReport(String startDate, String endDate) {
        List<LocalDateTime> range = CommonUtils.getDateRenge(startDate, endDate);
        LocalDateTime start = range.get(0);
        LocalDateTime end = range.get(1);

        // Step 1: Fetch all auctions in range
        List<AssetDisposalAuctionEntity> auctions = assetDisposalAuctionEntityRepository.findByCreatedDateBetween(start, end);
        List<AssetDisposalReportDto> reports = new ArrayList<>();

        for (AssetDisposalAuctionEntity auction : auctions) {
            AssetDisposalReportDto dto = new AssetDisposalReportDto();
            dto.setAuctionId(auction.getAuctionId().toString());
            dto.setAuctionCode(auction.getAuctionCode());
            dto.setAuctionDate(auction.getAuctionDate().toString());
            dto.setReservePrice(auction.getReservePrice());
            dto.setAuctionPrice(auction.getAuctionPrice());
            dto.setVendorName(auction.getVendorName());

            // Step 2: Fetch disposal IDs for this auction
            List<Integer> disposalIds = auction.getAuctionDetails()
                    .stream()
                    .map(AssetDisposalAuctionDetailEntity::getDisposalId)
                    .toList();

            List<AutionAssetDisposalReportDto> disposals = new ArrayList<>();

            if (!disposalIds.isEmpty()) {
                // Step 3: Fetch disposals
                List<AssetDisposalMasterEntity> disposalEntities = disposalMasterRepository.findByDisposalIdIn(disposalIds);

                for (AssetDisposalMasterEntity disposal : disposalEntities) {
                    AutionAssetDisposalReportDto disposalDto = new AutionAssetDisposalReportDto();
                    disposalDto.setDisposalId(disposal.getDisposalId());
                    disposalDto.setDisposalDate(disposal.getDisposalDate() != null ? disposal.getDisposalDate().toString() : null);
                    disposalDto.setLocationId(disposal.getLocationId());
                    disposalDto.setStatus(disposal.getStatus());
                    disposalDto.setCustodianId(disposal.getCustodianId());
                    disposalDto.setCreatedBy(disposal.getCreatedBy());
                    disposalDto.setCreateDate(disposal.getCreateDate() != null ? disposal.getCreateDate().toString() : null);
                    disposalDto.setAction(disposal.getAction());

                    // Step 4: Fetch assets for this disposal
                    List<AssetDisposalDetailEntity> assetDetails = disposalDetailRepository.findByDisposalId(disposal.getDisposalId());
                    List<AssetDisposalMaterialDto> assetDtos = assetDetails.stream().map(dd -> {
                        AssetDisposalMaterialDto ad = new AssetDisposalMaterialDto();
                        ad.setDisposalDetailId(dd.getDisposalDetailId());
                        ad.setDisposalId(dd.getDisposalId());
                        ad.setAssetId(dd.getAssetId());
                        ad.setAssetDesc(dd.getAssetDesc());
                        ad.setDisposalQuantity(dd.getDisposalQuantity());
                        ad.setLocatorId(dd.getLocatorId());
                        ad.setBookValue(dd.getBookValue());
                        ad.setDepriciationRate(dd.getDepriciationRate());
                        ad.setUnitPrice(dd.getUnitPrice());
                        ad.setCustodianId(dd.getCustodianId());
                        ad.setPoValue(dd.getPoValue());
                        ad.setReasonForDisposal(dd.getReasonForDisposal());
                        return ad;
                    }).toList();

                    disposalDto.setAssets(assetDtos);
                    disposals.add(disposalDto);
                }
            }

            dto.setDisposals(disposals);
            reports.add(dto);
        }

        return reports;
    }


    @Transactional
    public String disposeMultipleAssets(DisposeAssetRequest request) {
        //Create Auction record
        AssetDisposalAuctionEntity auction = new AssetDisposalAuctionEntity();
        auction.setAuctionCode(request.getAuctionCode());
        auction.setAuctionDate(CommonUtils.convertStringToDateObject(request.getAuctionDate()));
        auction.setReservePrice(request.getReservePrice());
        auction.setAuctionPrice(request.getAuctionPrice());
        auction.setVendorName(request.getVendorName());
        auction.setCreatedBy(request.getUpdatedBy());
        auction.setCreatedDate(LocalDateTime.now());
        auction = assetDisposalAuctionEntityRepository.save(auction);

        for (Integer disposalId : request.getDisposalIds()) {
            AssetDisposalAuctionDetailEntity detail = new AssetDisposalAuctionDetailEntity();
            detail.setAuction(auction);
            detail.setDisposalId(disposalId);
            assetDisposalAuctionDetailEntityRepository.save(detail);
        }

        //  Update each disposal
        List<AssetDisposalMasterEntity> disposals = disposalMasterRepository.findAllById(request.getDisposalIds());
        for (AssetDisposalMasterEntity disposal : disposals) {
            disposal.setStatus("Disposed");
            disposal.setAuctionId(auction.getAuctionCode());
            disposalMasterRepository.save(disposal);
        }
        return "INV/" +  auction.getAuctionId();
    }
    @Override
    public List<Integer> getPendingAuctionIds() {
        return  disposalMasterRepository.findPendingAuctionIdsWithoutOgp();
    }

    @Override
    public AssetsAuctionDto searchByAuctionId(String auctionId) {
        Integer id = Integer.valueOf(auctionId.split("/")[1]);
        // 1. Run query and fetch rows
        List<Object[]> rows = assetDisposalAuctionEntityRepository.findAuctionWithFullDetails(id);

        if (rows.isEmpty()) {
            return null; // or throw custom exception
        }

        // 2. Extract Auction entity from first row
        AssetDisposalAuctionEntity auction = (AssetDisposalAuctionEntity) rows.get(0)[0];

        // 3. Prepare Auction DTO (top-level info)
        AssetsAuctionDto auctionDto = new AssetsAuctionDto();
        auctionDto.setAuctionId("INV/"+auction.getAuctionId());
        auctionDto.setAuctionCode(auction.getAuctionCode());
        auctionDto.setAuctionDate(CommonUtils.convertDateToString(auction.getAuctionDate()));
        auctionDto.setReservePrice(auction.getReservePrice());
        auctionDto.setAuctionPrice(auction.getAuctionPrice());
        auctionDto.setVendorName(auction.getVendorName());

        // 4. Map assets from each row
        List<AutionAssetsDisposalsDto> assetList = rows.stream().map(row -> {
            AssetDisposalMasterEntity master = (AssetDisposalMasterEntity) row[2];
            AssetDisposalDetailEntity detail = (AssetDisposalDetailEntity) row[3];

            AutionAssetsDisposalsDto dto = new AutionAssetsDisposalsDto();
            dto.setDisposalDetailId(detail.getDisposalDetailId());
            dto.setDisposalId(master.getDisposalId());
            dto.setAssetId(detail.getAssetId());
            dto.setAssetCode(detail.getAssetCode());
            dto.setAssetDesc(detail.getAssetDesc());
            dto.setDisposalQuantity(detail.getDisposalQuantity());
            dto.setLocatorId(detail.getLocatorId());
            dto.setBookValue(detail.getBookValue());
            dto.setSerialNo(detail.getSerialNo());
            dto.setDepriciationRate(detail.getDepriciationRate());
            dto.setUnitPrice(detail.getUnitPrice());
            dto.setCustodianId(detail.getCustodianId());
            dto.setPoValue(detail.getPoValue());
            dto.setReasonForDisposal(detail.getReasonForDisposal());
            dto.setDisposalDate(CommonUtils.convertDateToString(master.getDisposalDate()));
            dto.setLocationId(master.getLocationId());
            dto.setStatus(master.getStatus());
            return dto;
        }).collect(Collectors.toList());

        // 5. Attach assets to auction DTO
        auctionDto.setAssets(assetList);

        return auctionDto;
    }

    @Override
    public List<AssetSearchResponseDto> searchAssetsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a search keyword.");
        }

        return ohqMasterRepository.searchAssetsByKeyword(keyword.trim());
    }

    @Override
    public List<AssetFullResponseDto> getFullAssetDetails(
            Integer assetId, String assetCode, String custodianId, Integer locatorId) {

        if ((assetId == null || assetId <= 0) &&
                (assetCode == null || assetCode.isEmpty()) &&
                (custodianId == null || custodianId.isEmpty()) &&
                (locatorId == null)) {
            throw new IllegalArgumentException("Please provide at least one search parameter.");
        }

        return assetMasterRepository.findAssetFullDetails(assetId, assetCode, custodianId, locatorId);
    }

    @Transactional
    public String updateAssetSerials(AssetSerialUpdateRequestDto req) {

        AssetMasterEntity asset = assetMasterRepository.findById(req.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found for ID: " + req.getAssetId()));

        // Basic validations
        if (req.getSerialNumbers() == null || req.getSerialNumbers().isEmpty()) {
            throw new RuntimeException("Serial number list is empty");
        }

        if (req.getQuantity() == null || req.getQuantity().intValue() != req.getSerialNumbers().size()) {
            throw new RuntimeException("Quantity and serial number count do not match");
        }

        // Update fields in asset master
        asset.setMaterialCode(req.getMaterialCode());
        asset.setMaterialDesc(req.getMaterialDesc());
        asset.setAssetDesc(req.getAssetDesc());
        asset.setMakeNo(req.getMakeNo());
        asset.setModelNo(req.getModelNo());
        asset.setUomId(req.getUomId());
        asset.setUnitPrice(req.getUnitPrice());
        asset.setStockLevels(req.getQuantity());
        asset.setUpdatedDate(LocalDateTime.now());

      AssetMasterEntity as =  assetMasterRepository.save(asset);

        // Save to a separate table (if you track per-unit serials)
        for (String serial : req.getSerialNumbers()) {
            AssetSerialEntity serialEntity = new AssetSerialEntity();
            serialEntity.setAssetId(req.getAssetId());
            serialEntity.setAssetCode(req.getAssetCode());
            serialEntity.setSerialNo(serial);
            serialEntity.setCustodianId(req.getCustodianId());
            serialEntity.setLocatorId(req.getLocatorId());
            serialEntity.setPoId(req.getPoId());
            serialEntity.setCreatedDate(LocalDateTime.now());
             assetSerialEntityRepository.save(serialEntity);
        }
        return as.getAssetCode();
    }

    public List<AssetDataForGtDto> getAllFullAssets() {
        List<AssetDataForGtDto> assets = assetMasterRepository.findAllAssetFullDetails();

        // For each asset, fetch serial numbers
        for (AssetDataForGtDto dto : assets) {
            List<String> serials = assetSerialEntityRepository.findSerialNumbers(
                    dto.getAssetId(),
                    dto.getAssetCode(),
                    dto.getLocatorId(),
                    dto.getCustodianId()
            );
            dto.setSerialNumbers(serials);
        }

        return assets;
    }
// Add this method after getAllFullAssets() at line 923
@Override
public List<AssetDataForGtDto> getAllFullAssetsForStorePerson() {
    List<AssetDataForGtDto> assets = assetMasterRepository.findAllAssetFullDetails();

    List<AssetDataForGtDto> result = new ArrayList<>();
    for (AssetDataForGtDto dto : assets) {
        // skip zero or null quantity items
        if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            continue;
        }
        List<String> serials = assetSerialEntityRepository.findSerialNumbers(
                dto.getAssetId(),
                dto.getAssetCode(),
                dto.getLocatorId(),
                dto.getCustodianId()
        );
        dto.setSerialNumbers(serials);
        result.add(dto);
    }
    return result;
}

    @Override
    public SerialCheckResponseDto checkSerials(String assetCode, Integer assetId, String custodianId, Integer locatorId, Integer quantity) {


        List<String> existingSerials = assetSerialEntityRepository
                .findSerialNosByAssetIdAndLocatorIdAndCustodianId(assetId, locatorId, custodianId);

        int existingCount = existingSerials.size();
        int remainingToEnter = quantity - existingCount;

        SerialCheckResponseDto response = new SerialCheckResponseDto();
        response.setAssetId(assetId);
        response.setAssetCode(assetCode);
        response.setCustodianId(custodianId);
        response.setLocatorId(locatorId);
        response.setExistingSerials(existingSerials);
        response.setExistingCount(existingCount);
        response.setRemainingToEnter(Math.max(remainingToEnter, 0));
        return response;
    }

    @Transactional
    public String addRemainingSerials(AssetSerialUpdateRequestDto req) {
        AssetMasterEntity asset = assetMasterRepository.findById(req.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        List<String> existingSerials = assetSerialEntityRepository
                .findSerialNosByAssetIdAndLocatorIdAndCustodianId(req.getAssetId(), req.getLocatorId(), req.getCustodianId());


        //  Validate duplicates
        for (String serial : req.getSerialNumbers()) {
            if (existingSerials.contains(serial)) {
                throw new RuntimeException("Duplicate serial: " + serial);
            }
        }

        //  Save new serials
        for (String serial : req.getSerialNumbers()) {
            AssetSerialEntity s = new AssetSerialEntity();
            s.setAssetId(req.getAssetId());
            s.setAssetCode(req.getAssetCode());
            s.setSerialNo(serial);
            s.setCustodianId(req.getCustodianId());
            s.setLocatorId(req.getLocatorId());
            s.setPoId(req.getPoId());
            s.setCreatedDate(LocalDateTime.now());
            assetSerialEntityRepository.save(s);
        }

        return "Successfully added " + req.getSerialNumbers().size() + " serials.";
    }



}
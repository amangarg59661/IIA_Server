package com.astro.service.impl.InventoryModule;

import com.astro.repository.UserMasterRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.astro.service.InventoryModule.IgpService;
import com.astro.repository.MaterialMasterRepository;
import com.astro.repository.InventoryModule.AssetMasterRepository;
import com.astro.repository.InventoryModule.igp.*;
import com.astro.repository.InventoryModule.isn.IssueNoteMasterRepository;
import com.astro.repository.InventoryModule.ogp.IgpPoDtlRepository;
import com.astro.repository.InventoryModule.ogp.OgpDetailRepository;
import com.astro.repository.InventoryModule.ogp.OgpMasterRepository;
import com.astro.entity.MaterialMaster;
import com.astro.entity.InventoryModule.*;
import com.astro.dto.workflow.InventoryModule.GprnDto.MaterialDtlDto;
import com.astro.dto.workflow.InventoryModule.igp.*;
import com.astro.dto.workflow.InventoryModule.ogp.IgpMaterialDetailDto;
import com.astro.dto.workflow.InventoryModule.ogp.MaterialIgpDto;
import com.astro.exception.*;
import com.astro.constant.AppConstant;
import com.astro.util.CommonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.modelmapper.ModelMapper;

@Service
public class IgpServiceImpl implements IgpService {
    
    @Autowired
    private IgpMasterRepository igpMasterRepository;
    
    @Autowired
    private IgpDetailRepository igpDetailRepository;
    
    @Autowired
    private IssueNoteMasterRepository isnMasterRepository;

    @Autowired
    private OgpMasterRepository ogpMasterRepository;

    @Autowired
    private OgpDetailRepository ogpDetailRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AssetMasterRepository amr;

    @Autowired
    private IgpPoDtlRepository igpPoDtlRepository;

    @Autowired
    private IgpMaterialMasterRepository immr;

    @Autowired
    private IgpMaterialDtlRepository imdr;

    @Autowired
    private MaterialMasterRepository mmr;
    @Autowired
    private UserMasterRepository userMasterRepository;

    @Override
    @Transactional
    public String saveIgp(IgpDto req) {
        // 1. Validate OGP exists
        validateOgp(req.getOgpId());
        Integer ogpSubProcessId = Integer.parseInt(req.getOgpId().split("/")[1]);

        OgpMasterEntity ogpMaster = ogpMasterRepository.findById(ogpSubProcessId)
            .orElseThrow(() -> new BusinessException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "OGP not found")));

        // 2. Check OGP Type
        if ("Non Returnable".equals(req.getOgpType())) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Cannot create IGP for Non-Returnable OGP"));
        }

        // 3. Handle PO type IGP
        if ("PO".equals(req.getIgpType())) {
            // Check existing IGP PO details
            List<IgpPoDtlEntity> existingPoDetails = igpPoDtlRepository.findByIgpSubProcessId(ogpSubProcessId);
            
            // Validate quantities and duplicates for PO
            validatePoDetails(req.getMaterialDtlList(), existingPoDetails);

            // Create and save IGP master
            final IgpMasterEntity igpMaster = new IgpMasterEntity();
            igpMaster.setIgpDate(CommonUtils.convertStringToDateObject(req.getIgpDate()));
            igpMaster.setLocationId(req.getLocationId());
            igpMaster.setCreatedBy(req.getCreatedBy());
            igpMaster.setCreateDate(LocalDateTime.now());
            igpMaster.setIgpProcessId("PO" + ogpSubProcessId);
            igpMaster.setOgpSubProcessId(ogpSubProcessId);

            final IgpMasterEntity savedIgpMaster = igpMasterRepository.save(igpMaster);

            // Save PO details
            List<IgpPoDtlEntity> igpPoDetails = req.getMaterialDtlList().stream()
                .map(dtl -> {
                    IgpPoDtlEntity detail = new IgpPoDtlEntity();
                    detail.setIgpSubProcessId(savedIgpMaster.getIgpSubProcessId());
                    detail.setMaterialCode(dtl.getMaterialCode());
                    detail.setMaterialDescription(dtl.getMaterialDescription());
                    detail.setUomId(dtl.getUomId());
                    detail.setQuantity(dtl.getQuantity());
                    return detail;
                })
                .collect(Collectors.toList());
            
            igpPoDtlRepository.saveAll(igpPoDetails);
            return "INV" + "/" + savedIgpMaster.getIgpSubProcessId();
        }
        
        // 4. Handle non-PO type IGP
        else {
            // Validate issue note type
           /* IsnMasterEntity issueNote = isnMasterRepository.findById(ogpMaster.getIssueNoteId())
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Issue Note not found")));

            if ("Non Returnable".equals(issueNote.getIssueNoteType().toString())) {
                throw new BusinessException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot create IGP for Non-Returnable Issue Note"));
            }*/
            if ("Non Returnable".equalsIgnoreCase(ogpMaster.getOgpType())) {
                throw new BusinessException(new ErrorDetails(
                        AppConstant.USER_INVALID_INPUT,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Cannot create IGP for Non-Returnable OGP"));
            }


            // Check existing IGP details
            List<IgpMasterEntity> existingIgpMasters = igpMasterRepository.findByOgpSubProcessId(ogpSubProcessId);
            validateNonPoDetails(req.getMaterialDtlList(), existingIgpMasters);

            // Create and save IGP master
            final IgpMasterEntity igpMaster = new IgpMasterEntity();
            igpMaster.setIgpDate(CommonUtils.convertStringToDateObject(req.getIgpDate()));
            igpMaster.setLocationId(req.getLocationId());
            igpMaster.setCreatedBy(req.getCreatedBy());
            igpMaster.setCreateDate(LocalDateTime.now());
            igpMaster.setIgpProcessId("INV" + ogpSubProcessId);
            igpMaster.setOgpSubProcessId(ogpSubProcessId);

            final IgpMasterEntity savedIgpMaster = igpMasterRepository.save(igpMaster);

            // Save non-PO details
            List<IgpDetailEntity> igpDetails = req.getMaterialDtlList().stream()
                .map(dtl -> {
                    IgpDetailEntity detail = new IgpDetailEntity();
                    detail.setIgpProcessId(savedIgpMaster.getIgpProcessId());
                    detail.setIgpSubProcessId(savedIgpMaster.getIgpSubProcessId());
                    detail.setAssetId(dtl.getAssetId());
                    detail.setLocatorId(dtl.getLocatorId());
                    detail.setQuantity(dtl.getQuantity());
                    detail.setOgpSubProcessId(ogpSubProcessId);
                    return detail;
                })
                .collect(Collectors.toList());
            
            igpDetailRepository.saveAll(igpDetails);
            return "INV" + "/" + savedIgpMaster.getIgpSubProcessId();
        }
    }

    private void validatePoDetails(List<IgpMaterialDtlDto> newDetails, List<IgpPoDtlEntity> existingDetails) {
        StringBuilder errorMessage = new StringBuilder();
        
        // Add your PO-specific validation logic here
        // Example: Check for duplicates, validate quantities, etc.
        
        if (errorMessage.length() > 0) {
            throw new BusinessException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                errorMessage.toString()));
        }
    }

    private void validateNonPoDetails(List<IgpMaterialDtlDto> newDetails, List<IgpMasterEntity> existingIgpMasters) {
        Map<String, BigDecimal> totalIgpQuantities = new HashMap<>();
        StringBuilder errorMessage = new StringBuilder();

        // Calculate existing quantities
        for (IgpMasterEntity existingIgp : existingIgpMasters) {
            List<IgpDetailEntity> existingDetails = igpDetailRepository.findByIgpSubProcessId(existingIgp.getIgpSubProcessId());
            for (IgpDetailEntity detail : existingDetails) {
                String key = detail.getAssetId() + "_" + detail.getLocatorId();
                totalIgpQuantities.merge(key, detail.getQuantity(), BigDecimal::add);
            }
        }

        // Validate new quantities
        for (IgpMaterialDtlDto dtl : newDetails) {
            String key = dtl.getAssetId() + "_" + dtl.getLocatorId();
            BigDecimal existingQty = totalIgpQuantities.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal newTotalQty = existingQty.add(dtl.getQuantity());
            
            // Add your non-PO specific validation logic here
            // Example: Check against OGP quantity, validate duplicates, etc.
        }

        if (errorMessage.length() > 0) {
            throw new BusinessException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                errorMessage.toString()));
        }
    }

    @Override
    public IgpDto getIgpDtls(String processNo) {
        String[] processNoSplit = processNo.split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process number format"));
        }

        Integer igpSubProcessId = Integer.parseInt(processNoSplit[1]);
        
        IgpMasterEntity igpMaster = igpMasterRepository.findById(igpSubProcessId)
            .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "IGP not found")));

        List<IgpDetailEntity> igpDetails = igpDetailRepository.findByIgpSubProcessId(igpSubProcessId);

        IgpDto response = modelMapper.map(igpMaster, IgpDto.class);
        response.setIgpDate(CommonUtils.convertDateToString(igpMaster.getIgpDate()));
        response.setIgpId(igpMaster.getIgpProcessId() + "/" + igpMaster.getIgpSubProcessId());

        List<IgpMaterialDtlDto> materialDtls = igpDetails.stream()
            .map(detail -> {
                IgpMaterialDtlDto dto = modelMapper.map(detail, IgpMaterialDtlDto.class);
                amr.findById(detail.getAssetId())
                    .ifPresent(asset -> {
                        dto.setAssetDesc(asset.getAssetDesc());
                        dto.setUomId(asset.getUomId());
                    });
                return dto;
            })
            .collect(Collectors.toList());

        response.setMaterialDtlList(materialDtls);
        return response;
    }

    private void validateOgp(String processNo) {
        String[] processNoSplit = processNo.split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid OGP No."));
        }

        try {
            Integer subProcessId = Integer.parseInt(processNoSplit[1]);
            
            OgpMasterEntity ogpMaster = ogpMasterRepository.findById(subProcessId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Provided OGP No. is not valid.")));

            if (!"APPROVED".equals(ogpMaster.getStatus())) {
                throw new BusinessException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "OGP must be approved before creating IGP."));
            }
            
        } catch (NumberFormatException e) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid OGP number format"));
        }
    }

    private BigDecimal getOgpQuantity(Integer ogpSubProcessId, Integer assetId, Integer locatorId) {
        return ogpDetailRepository.findQuantityByOgpSubProcessIdAndAssetIdAndLocatorId(
            ogpSubProcessId, assetId, locatorId)
            .orElse(BigDecimal.ZERO);
    }
    
    @Override
    public List<IgpReportDto> getIgpReport(String startDate, String endDate) {
        List<LocalDateTime> dateRange = CommonUtils.getDateRenge(startDate, endDate);
        List<Object[]> results = igpMasterRepository.getIgpReport(dateRange.get(0), dateRange.get(1));
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return results.stream().map(row -> {
            IgpReportDto dto = new IgpReportDto();
            dto.setIgpProcessId((String) row[0]);
            dto.setIgpSubProcessId((Integer) row[1]);
            dto.setOgpSubProcessId((Integer) row[2]);
            dto.setIgpDate(CommonUtils.convertSqlDateToString((Date) row[3]));
            // dto.setIgpDate(row[3] != null ? ((java.sql.Timestamp) row[3]).toLocalDateTime() : null);
            dto.setLocationId((String) row[4]);
            dto.setCreatedBy(row[5] != null ? String.valueOf(row[5]) : null);
            // dto.setCreateDate(row[6] != null ? ((java.sql.Timestamp) row[6]).toLocalDateTime() : null);
            
            try {
                String detailsJson = (String) row[7];
                if (detailsJson != null && !detailsJson.isEmpty()) {
                    List<IgpDetailReportDto> details = mapper.readValue(
                        detailsJson, 
                        mapper.getTypeFactory().constructCollectionType(List.class, IgpDetailReportDto.class)
                    );
                    dto.setIgpDetails(details);
                } else {
                    dto.setIgpDetails(new ArrayList<>());
                }
            } catch (Exception e) {
                dto.setIgpDetails(new ArrayList<>());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
/*
    @Override
    public List<IgpMaterialInReportDto> getIgpMaterialInReport(String startDate, String endDate) {
     //  List<LocalDateTime> dateRange = CommonUtils.getDateRenge(startDate, endDate);
        List<LocalDate> dateRange = CommonUtils.getDateRengeAsLocalDate(startDate, endDate);

        List<Object[]> results = igpMasterRepository.getIgpMaterailInReport(dateRange.get(0), dateRange.get(1));

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return results.stream().map(row -> {
            IgpMaterialInReportDto dto = new IgpMaterialInReportDto();
            dto.setId(((Number) row[0]).longValue());
            dto.setStatus((String) row[1]);
            dto.setOgpId((String) row[2]);
           // dto.setIgpDate((String) row[3]);
            dto.setIgpDate((String) row[3]);
            dto.setIgpType((String) row[4]);
            dto.setIndentId(row[5] != null ? ((Number) row[5]).intValue() : null);
            dto.setCreatedBy(row[6] != null ? String.valueOf(((Number) row[6]).intValue()) : null);
            dto.setCreateDate(row[7] != null ? ((java.sql.Timestamp) row[7]).toLocalDateTime() : null);
            dto.setLocationId((String) row[8]);

            try {
                String detailsJson = (String) row[9];
                if (detailsJson != null && !detailsJson.isEmpty()) {
                    List<IgpMaterialInDetailReportDto> details = mapper.readValue(
                            detailsJson,
                            mapper.getTypeFactory().constructCollectionType(List.class, IgpDetailReportDto.class)
                    );
                    dto.setIgpDetails(details);
                } else {
                    dto.setIgpDetails(new ArrayList<>());
                }
            } catch (Exception e) {
                dto.setIgpDetails(new ArrayList<>());
            }

            return dto;
        }).collect(Collectors.toList());
    }*/
@Override
public List<IgpMaterialInReportDto> getIgpMaterialInReport(String startDate, String endDate) {
    List<LocalDate> dateRange = CommonUtils.getDateRengeAsLocalDate(startDate, endDate);

    List<Object[]> results = igpMasterRepository.getIgpMaterailInReport(dateRange.get(0), dateRange.get(1));

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return results.stream().map(row -> {
        IgpMaterialInReportDto dto = new IgpMaterialInReportDto();
        dto.setId(((Number) row[0]).longValue());
        dto.setStatus((String) row[1]);
        dto.setOgpId((String) row[2]);
        dto.setIgpDate((String) row[3]);
        dto.setIgpType((String) row[4]);
        dto.setIndentId(row[5] != null ? ((Number) row[5]).intValue() : null);
        dto.setCreatedBy(row[6] != null ? String.valueOf(((Number) row[6]).intValue()) : null);
        dto.setCreateDate(row[7] != null ? ((java.sql.Timestamp) row[7]).toLocalDateTime() : null);
        dto.setLocationId((String) row[8]);

        try {
            String detailsJson = (String) row[9];
            if (detailsJson != null && !detailsJson.isEmpty()) {
                List<IgpMaterialInDetailReportDto> details = mapper.readValue(
                        detailsJson,
                        mapper.getTypeFactory().constructCollectionType(List.class, IgpMaterialInDetailReportDto.class)
                );
                dto.setIgpDetails(details);
            } else {
                dto.setIgpDetails(new ArrayList<>());
            }
        } catch (Exception e) {
            dto.setIgpDetails(new ArrayList<>());
        }

        return dto;
    }).collect(Collectors.toList());
}


    @Override
    public List<IgpCombinedDetailDto> getIgpDetails() {
        List<Object[]> results = igpDetailRepository.findAllIgpDetails();
        ObjectMapper mapper = new ObjectMapper();
        
        return results.stream().map(row -> {
            IgpCombinedDetailDto dto = new IgpCombinedDetailDto();
            
            dto.setIssueNoteId(row[0] != null ? Integer.valueOf(row[0].toString()) : null);
            dto.setOgpSubProcessId(row[1] != null ? Integer.valueOf(row[1].toString()) : null);
            dto.setIgpSubProcessId(row[2] != null ? Integer.valueOf(row[2].toString()) : null);
            dto.setPoId((String) row[3]);
            dto.setStatus((String) row[5]);
            dto.setSenderName((String) row[6]);
            dto.setReceiverName((String) row[7]);
            dto.setOgpType((String) row[8]);
            
            try {
                String detailsJson = (String) row[4];
                if (detailsJson != null && !detailsJson.isEmpty()) {
                    List<IgpItemDetailDto> details = mapper.readValue(
                        detailsJson,
                        mapper.getTypeFactory().constructCollectionType(List.class, IgpItemDetailDto.class)
                    );
                    dto.setDetails(details);
                }
            } catch (Exception e) {
                dto.setDetails(new ArrayList<>());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String saveMaterialIgp(MaterialIgpDto req) {
        IgpMaterialMasterEntity igpMaterialMasterEntity = new IgpMaterialMasterEntity();
        igpMaterialMasterEntity.setStatus("AWAITING APPROVAL");
        igpMaterialMasterEntity.setOgpId(req.getOgpId());
        igpMaterialMasterEntity.setIgpDate(req.getIgpDate());
        igpMaterialMasterEntity.setCreatedBy(req.getCreatedBy());
        igpMaterialMasterEntity.setIgpType(req.getIgpType());
        igpMaterialMasterEntity.setIndentId(req.getIndentId());
        igpMaterialMasterEntity.setLocationId(req.getLocationId());
        igpMaterialMasterEntity = immr.save(igpMaterialMasterEntity);

        for (IgpMaterialDetailDto materialIgpDetailDto : req.getMaterialDtlList()) {
            IgpMaterialDtlEntity igpMaterialDtlEntity = new IgpMaterialDtlEntity();
            igpMaterialDtlEntity.setIgpId(igpMaterialMasterEntity.getId());
            igpMaterialDtlEntity.setMaterialCode(materialIgpDetailDto.getMaterialCode());
            igpMaterialDtlEntity.setQuantity(materialIgpDetailDto.getQuantity());
            igpMaterialDtlEntity.setCategory(materialIgpDetailDto.getCategory());
            igpMaterialDtlEntity.setSubCategory(materialIgpDetailDto.getSubCategory());
            igpMaterialDtlEntity.setDescription(materialIgpDetailDto.getDescription());
            igpMaterialDtlEntity.setUom(materialIgpDetailDto.getUom());
           // igpMaterialDtlEntity.setEstimatedPriceWithCcy(materialIgpDetailDto.getEstimatedPriceWithCcy());
            igpMaterialDtlEntity.setEstimatedPriceWithCcy(
                    materialIgpDetailDto.getUnitPrice() != null
                            ? materialIgpDetailDto.getUnitPrice().doubleValue()
                            : 0.0
            );

            igpMaterialDtlEntity.setIndigenousOrImported(materialIgpDetailDto.getIndigenousOrImported());
            imdr.save(igpMaterialDtlEntity);
        }

        return "INV/" + igpMaterialMasterEntity.getId();
    }

    @Override
    @Transactional
    public void approveMaterialIgp(String igpId) {
        System.out.println("APprove material igp called");
        Long id = Long.parseLong(igpId.split("/")[1]);
        IgpMaterialMasterEntity igpMaterialMasterEntity = immr.findById(id).orElseThrow(() -> new RuntimeException("IGP not found"));
        igpMaterialMasterEntity.setStatus("APPROVED");
        immr.save(igpMaterialMasterEntity);
        
        createNewAsset(igpId, igpMaterialMasterEntity.getCreatedBy());
    }

    @Override
    @Transactional
    public void rejectMaterialIgp(String igpId) {
        Long id = Long.parseLong(igpId.split("/")[1]);
        IgpMaterialMasterEntity igpMaterialMasterEntity = immr.findById(id).orElseThrow(() -> new RuntimeException("IGP not found"));
        igpMaterialMasterEntity.setStatus("REJECTED");
        immr.save(igpMaterialMasterEntity);
    }

    public String updateMaterialIgp(MaterialIgpDto req) {
        Long id = Long.parseLong(req.getIgpId().split("/")[1]);
        IgpMaterialMasterEntity igpMaterialMasterEntity = immr.findById(id).orElseThrow(() -> new RuntimeException("IGP not found"));
        igpMaterialMasterEntity.setStatus("AWAITING APPROVAL");
        igpMaterialMasterEntity.setOgpId(req.getOgpId());
        igpMaterialMasterEntity.setIgpDate(req.getIgpDate());
        igpMaterialMasterEntity.setIgpType(req.getIgpType());
        igpMaterialMasterEntity.setIndentId(req.getIndentId());
        immr.save(igpMaterialMasterEntity);

        for (IgpMaterialDetailDto materialIgpDetailDto : req.getMaterialDtlList()) {
            IgpMaterialDtlEntity igpMaterialDtlEntity = new IgpMaterialDtlEntity();
            igpMaterialDtlEntity.setIgpId(igpMaterialMasterEntity.getId());
            igpMaterialDtlEntity.setMaterialCode(materialIgpDetailDto.getMaterialCode());
            igpMaterialDtlEntity.setQuantity(materialIgpDetailDto.getQuantity());
            igpMaterialDtlEntity.setCategory(materialIgpDetailDto.getCategory());
            igpMaterialDtlEntity.setSubCategory(materialIgpDetailDto.getSubCategory());
            igpMaterialDtlEntity.setDescription(materialIgpDetailDto.getDescription());
            igpMaterialDtlEntity.setUom(materialIgpDetailDto.getUom());
           // igpMaterialDtlEntity.setEstimatedPriceWithCcy(materialIgpDetailDto.getEstimatedPriceWithCcy());
            igpMaterialDtlEntity.setEstimatedPriceWithCcy(
                    materialIgpDetailDto.getUnitPrice() != null
                            ? materialIgpDetailDto.getUnitPrice().doubleValue()
                            : 0.0
            );

            igpMaterialDtlEntity.setIndigenousOrImported(materialIgpDetailDto.getIndigenousOrImported());
            imdr.save(igpMaterialDtlEntity);
        }

        return "INV/" + igpMaterialMasterEntity.getId();
    }
    
    // @Override
    // public List<IgpDetailReportDto> getIgpDetails() {
    //     List<Object[]> results = igpDetailRepository.findAllIgpDetails();
        
    //     return results.stream()
    //         .map(row -> {
    //             IgpDetailReportDto dto = new IgpDetailReportDto();
    //             dto.setDetailId((Integer) row[0]);
    //             dto.setIgpSubProcessId((Integer) row[1]);
    //             dto.setMaterialCode((String) row[2]);
    //             dto.setMaterialDesc((String) row[3]);
    //             dto.setAssetId((Integer) row[4]);
    //             dto.setLocatorId((Integer) row[5]);
    //             dto.setUomId((String) row[6]);
    //             dto.setQuantity((BigDecimal) row[7]);
    //             dto.setType((String) row[8]);
    //             return dto;
    //         })
    //         .collect(Collectors.toList());
    // }

    @Override
    public MaterialIgpDto getIgpMaterialDtls(String igpId){
        Long id = Long.parseLong(igpId.split("/")[1]);
        IgpMaterialMasterEntity igpMaterialMasterEntity = immr.findById(id).orElseThrow(() -> new RuntimeException("IGP not found"));
        MaterialIgpDto materialIgpDto = new MaterialIgpDto();
        materialIgpDto.setIgpId(igpId);
        materialIgpDto.setOgpId(igpMaterialMasterEntity.getOgpId());
        materialIgpDto.setIgpDate(igpMaterialMasterEntity.getIgpDate());
        materialIgpDto.setIgpType(igpMaterialMasterEntity.getIgpType());
        materialIgpDto.setIndentId(igpMaterialMasterEntity.getIndentId());
        materialIgpDto.setStatus(igpMaterialMasterEntity.getStatus());
        materialIgpDto.setLocationId(igpMaterialMasterEntity.getLocationId());

       String custodianName = userMasterRepository.findUserNameByUserId(igpMaterialMasterEntity.getIndentId());

       materialIgpDto.setCustodianName(custodianName);
        List<IgpMaterialDtlEntity> igpMaterialDtlEntityList = imdr.findByIgpId(id);
        List<IgpMaterialDetailDto> igpMaterialDetailDtoList = new ArrayList<>();
        for (IgpMaterialDtlEntity igpMaterialDtlEntity : igpMaterialDtlEntityList) {
            IgpMaterialDetailDto igpMaterialDetailDto = new IgpMaterialDetailDto();
            igpMaterialDetailDto.setId(igpMaterialDtlEntity.getId());
            igpMaterialDetailDto.setAssetId(igpMaterialDtlEntity.getAssetId());
            igpMaterialDetailDto.setMaterialCode(igpMaterialDtlEntity.getMaterialCode());
            igpMaterialDetailDto.setQuantity(igpMaterialDtlEntity.getQuantity());
            igpMaterialDetailDto.setCategory(igpMaterialDtlEntity.getCategory());
            igpMaterialDetailDto.setSubCategory(igpMaterialDtlEntity.getSubCategory());
            igpMaterialDetailDto.setDescription(igpMaterialDtlEntity.getDescription());
            igpMaterialDetailDto.setUom(igpMaterialDtlEntity.getUom());
          //  igpMaterialDetailDto.setEstimatedPriceWithCcy(igpMaterialDtlEntity.getEstimatedPriceWithCcy());
           // igpMaterialDetailDto.setUnitPrice(BigDecimal.valueOf(igpMaterialDtlEntity.getEstimatedPriceWithCcy()));
            Double estimatedPrice = igpMaterialDtlEntity.getEstimatedPriceWithCcy();
            igpMaterialDetailDto.setUnitPrice(
                    estimatedPrice != null ? BigDecimal.valueOf(estimatedPrice) : BigDecimal.ZERO
            );

            igpMaterialDetailDto.setIndigenousOrImported(igpMaterialDtlEntity.getIndigenousOrImported());
            igpMaterialDetailDtoList.add(igpMaterialDetailDto);
        }
        materialIgpDto.setMaterialDtlList(igpMaterialDetailDtoList);


        return materialIgpDto;
    }

    private void createNewAsset(String igpId, String createdBy){
        System.out.println("CREATE NEW ASSET");
        Long id = Long.parseLong(igpId.split("/")[1]);
        IgpMaterialMasterEntity igpMaterialMasterEntity = immr.findById(id).orElseThrow(() -> new RuntimeException("IGP not found"));
        List<IgpMaterialDtlEntity> igpMaterialDtlEntityList = imdr.findByIgpId(id);
        for (IgpMaterialDtlEntity igpMaterialDtlEntity : igpMaterialDtlEntityList) {
            // AssetEntity assetEntity = new AssetEntity();
                if(!igpMaterialDtlEntity.getCategory().equalsIgnoreCase("consumable")){
                    System.out.println("NOT CONSUMABLE");
                    continue;
                }
                MaterialMaster mme = mmr.findById(igpMaterialDtlEntity.getMaterialCode())
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Material not found for the provided material code.")));

        Optional<AssetMasterEntity> ameOpt = amr.findByMaterialCodeAndMaterialDescAndUomIdAndIgpId(
            mme.getMaterialCode(),
            mme.getDescription(),
            mme.getUom(),
            id
        );

        if(ameOpt.isEmpty()){
            System.out.println("INSIDEEE");
            AssetMasterEntity assetMasterEntity = new AssetMasterEntity();
            assetMasterEntity.setAssetDesc(mme.getDescription());
            assetMasterEntity.setMaterialCode(mme.getMaterialCode());
            assetMasterEntity.setMaterialDesc(mme.getDescription());
            assetMasterEntity.setUomId(mme.getUom());
            assetMasterEntity.setIgpId(id);
            assetMasterEntity.setCreateDate(LocalDateTime.now());
            System.out.println("ABOVEEE");
            assetMasterEntity.setCreatedBy(createdBy);
            assetMasterEntity.setUpdatedDate(LocalDateTime.now());
            System.out.println("CREATED BY: " + createdBy);
            assetMasterEntity = amr.save(assetMasterEntity);
            igpMaterialDtlEntity.setAssetId(assetMasterEntity.getAssetId());
        }

            
    }
}

@Override
public void validateMaterialIgp(String igpId){
    Long id = Long.parseLong(igpId.split("/")[1]);
    IgpMaterialMasterEntity igpMaterialMasterEntity = immr.findById(id).orElseThrow(() -> new RuntimeException("IGP not found"));
    if(!igpMaterialMasterEntity.getStatus().equalsIgnoreCase("APPROVED")){
        throw new InvalidInputException(new ErrorDetails(
            AppConstant.ERROR_CODE_RESOURCE,
            AppConstant.ERROR_TYPE_CODE_RESOURCE,
            AppConstant.ERROR_TYPE_RESOURCE,
            "IGP is not approved."));
    }
}

@Override
public List<MaterialIgpDto> getAwaitingApprovalIgp(){
    List<IgpMaterialMasterEntity> igpMaterialMasterEntityList = immr.findByStatus("AWAITING APPROVAL");
    List<MaterialIgpDto> materialIgpDtoList = new ArrayList<>();

    for (IgpMaterialMasterEntity igpMaterialMasterEntity : igpMaterialMasterEntityList) {
        MaterialIgpDto materialIgpDto = getIgpMaterialDtls("INV/" + igpMaterialMasterEntity.getId());
        materialIgpDtoList.add(materialIgpDto);
    }
    return materialIgpDtoList;
}

}
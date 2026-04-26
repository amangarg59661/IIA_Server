package com.astro.service.impl.InventoryModule;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.astro.service.InventoryModule.IsnService;
import com.astro.repository.UserMasterRepository;
import com.astro.repository.InventoryModule.AssetMasterRepository;
import com.astro.repository.InventoryModule.isn.*;
import com.astro.repository.ohq.OhqMasterRepository;
import com.astro.entity.UserMaster;
import com.astro.entity.InventoryModule.*;
import com.astro.dto.workflow.InventoryModule.isn.*;
import com.astro.exception.*;
import com.astro.constant.AppConstant;
import com.astro.util.CommonUtils;
import org.modelmapper.ModelMapper;

@Service
public class IsnServiceImpl implements IsnService {
    @Autowired
    private IssueNoteMasterRepository isnmr;
    
    @Autowired
    private IssueNoteMaterialDtlRepository isnmdr;
    
    @Autowired
    private OhqMasterRepository ohqmr;

    @Autowired
    private AssetMasterRepository amr;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Override
    @Transactional
    public String saveIsn(IsnDto req) {
        System.out.println("SAVE ISN CALLED");
        ModelMapper mapper = new ModelMapper();

        IsnMasterEntity isnMaster = new IsnMasterEntity();
        isnMaster.setIssueDate(CommonUtils.convertStringToDateObject(req.getIssueDate()));
        isnMaster.setCreatedBy(req.getCreatedBy());
        isnMaster.setCreateDate(LocalDateTime.now());
        isnMaster.setLocationId(req.getLocationId());
        isnMaster.setIndentorName(req.getIndentorName());
        isnMaster.setConsigneeDetail(req.getConsigneeDetail());
        isnMaster.setFieldStation(req.getFieldStation());
        isnMaster.setIssueNoteType(req.getIssueNoteType());  

        isnMaster = isnmr.save(isnMaster);

        List<IsnMaterialDtlEntity> isnMaterialDtlList = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();
        Boolean errorFound = false;

        for (IsnMaterialDtlDto materialDtl : req.getMaterialDtlList()) {
            Optional<OhqMasterEntity> ohq = ohqmr.findByAssetIdAndLocatorId(
                    materialDtl.getAssetId(), 
                    materialDtl.getLocatorId());

            if (ohq.isEmpty()) {
                errorMessage.append("Asset ID " + materialDtl.getAssetId() + 
                        " not found in location. ");
                errorFound = true;
                continue;
            }

            if (ohq.get().getQuantity().compareTo(materialDtl.getQuantity()) < 0) {
                errorMessage.append("Issue quantity exceeds available quantity for Asset ID " + 
                        materialDtl.getAssetId() + ". ");
                errorFound = true;
                continue;
            }

            IsnMaterialDtlEntity isnMaterialDtl = new IsnMaterialDtlEntity();
            mapper.map(materialDtl, isnMaterialDtl);
            isnMaterialDtl.setIssueNoteId(isnMaster.getIssueNoteId());

            isnMaterialDtlList.add(isnMaterialDtl);

            // Update OHQ entry
            ohq.get().setQuantity(ohq.get().getQuantity().subtract(materialDtl.getQuantity()));
            ohqmr.save(ohq.get());
        }

        if (errorFound) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    errorMessage.toString()));
        }

        isnmdr.saveAll(isnMaterialDtlList);

        return "INV" + "/" + isnMaster.getIssueNoteId();
    }

    @Override
    public IsnDto getIsnDtls(String processNo) {
        ModelMapper mapper = new ModelMapper();
        String[] processNoSplit = processNo.split("/");
        
        // if (processNoSplit.length != 2) {
        //     throw new InvalidInputException(new ErrorDetails(
        //             AppConstant.USER_INVALID_INPUT,
        //             AppConstant.ERROR_TYPE_CODE_VALIDATION,
        //             AppConstant.ERROR_TYPE_VALIDATION,
        //             "Invalid process ID"));
        // }
Integer isnSubProcessId;
        if (3 == processNoSplit.length ){
             isnSubProcessId = Integer.parseInt(processNoSplit[2]);
        }else if(2 == processNoSplit.length) {
             isnSubProcessId = Integer.parseInt(processNoSplit[1]);
        }else {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process ID"));
        }

        // Integer isnSubProcessId = Integer.parseInt(processNoSplit[1]);

        IsnMasterEntity isnMaster = isnmr.findById(isnSubProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "ISN not found for the provided process ID.")));

        List<IsnMaterialDtlEntity> isnMaterialList = isnmdr.findByIssueNoteId(isnMaster.getIssueNoteId());

        System.out.println("ISN CREATED BY: " + isnMaster.getCreatedBy());
        UserMaster um = userMasterRepository.findByUserId(isnMaster.getCreatedBy());

        List<IsnMaterialDtlDto> materialDtlListRes = isnMaterialList.stream()
                .map(material -> {
                    IsnMaterialDtlDto dto = mapper.map(material, IsnMaterialDtlDto.class);
                    amr.findById(material.getAssetId())
                        .ifPresent(asset -> dto.setAssetDesc(asset.getAssetDesc()));
                    return dto;
                })
                .collect(Collectors.toList());

        IsnDto isnRes = new IsnDto();
        isnRes.setIssueNoteNo(processNo);
        isnRes.setConsigneeDetail(isnMaster.getConsigneeDetail());
        isnRes.setFieldStation(isnMaster.getFieldStation());
        isnRes.setIndentorName(isnMaster.getIndentorName());
        isnRes.setIssueDate(CommonUtils.convertDateToString(isnMaster.getIssueDate()));
        isnRes.setCreatedBy(isnMaster.getCreatedBy());
        isnRes.setLocationId(isnMaster.getLocationId());
        isnRes.setMaterialDtlList(materialDtlListRes);
        isnRes.setSenderName(um.getUserName());


        return isnRes;
    }

    @Override
    public List<IsnAssetOhqDtlsDto> getAssetMasterAndOhqDtls() {
        List<IsnAssetOhqDtlsDto> response = new ArrayList<>();

        List<AssetMasterEntity> assets = amr.findAll();

        for (AssetMasterEntity asset : assets) {
            List<OhqMasterEntity> ohqList = ohqmr.findByAssetId(asset.getAssetId());

            if (ohqList.isEmpty() || ohqList.stream()
                    .allMatch(ohq -> ohq.getQuantity().compareTo(BigDecimal.ZERO) == 0)) {
                continue;
            }

            IsnAssetOhqDtlsDto assetDto = new IsnAssetOhqDtlsDto();
            assetDto.setAssetId(asset.getAssetId());
            assetDto.setAssetDesc(asset.getAssetDesc());
            assetDto.setUomId(asset.getUomId());
            assetDto.setUnitPrice(asset.getUnitPrice());
            assetDto.setPoId(asset.getPoId()); // Added poId field
            assetDto.setDepriciationRate(asset.getDepriciationRate());
            assetDto.setMakeNo(asset.getMakeNo());
            assetDto.setModelNo(asset.getModelNo());
            assetDto.setSerialNo(asset.getSerialNo());
            assetDto.setDepriciationRate(asset.getDepriciationRate());

            List<IsnOhqDtlsDto> ohqDtoList = new ArrayList<>();
            for (OhqMasterEntity ohq : ohqList) {
                if (ohq.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    IsnOhqDtlsDto ohqDto = new IsnOhqDtlsDto();
                    ohqDto.setLocatorId(ohq.getLocatorId());
                    ohqDto.setQuantity(ohq.getQuantity());
                    try {
                        ohqDto.setCustodianId(ohq.getCustodianId() != null
                                ? Integer.valueOf(ohq.getCustodianId())
                                : null
                        );
                    } catch (NumberFormatException e) {
                        // ignore non-integer custodianIds
                        ohqDto.setCustodianId(null);
                    }
                    ohqDto.setBookValue(ohq.getBookValue());
                    ohqDtoList.add(ohqDto);

                }
            }

            if (!ohqDtoList.isEmpty()) {
                assetDto.setQtyList(ohqDtoList);
                response.add(assetDto);
            }
        }

        return response;
    }

    @Override
    public List<IsnReportDto> getIsnReport(String startDate, String endDate) {
        List<LocalDateTime> dateRange = CommonUtils.getDateRenge(startDate, endDate);
        
        List<Object[]> results = isnmr.getIssueNoteDetails(dateRange.get(0), dateRange.get(1));
        
        Map<Integer, IsnReportDto> reportMap = new HashMap<>();
        
        for (Object[] row : results) {
            Integer issueNoteId = (Integer) row[0];
            
            // Create or get master record
            IsnReportDto masterDto = reportMap.computeIfAbsent(issueNoteId, k -> {
                IsnReportDto dto = new IsnReportDto();
                dto.setIssueNoteId(issueNoteId);
                dto.setIssueNoteType((String) row[1]);
                dto.setIssueDate(CommonUtils.convertSqlDateToString((java.sql.Date) row[2]));
                dto.setConsigneeDetail((String) row[3]);
                dto.setIndentorName((String) row[4]);
                dto.setFieldStation((String) row[5]);
                dto.setCreatedBy((Integer) row[6]);
                // dto.setCreateDate(CommonUtils.convertSqlDateToString((java.sql.Date) row[7]));
                // dto.setCreateDate((LocalDateTime) row[7]);
                dto.setLocationId((String) row[8]);
                dto.setDetails(new ArrayList<>());
                return dto;
            });
            
            // Create detail record
            IsnReportDetailDto detailDto = new IsnReportDetailDto();
            detailDto.setDetailId((Integer) row[9]);
            detailDto.setAssetId((Integer) row[10]);
            detailDto.setLocatorId((Integer) row[11]);
            detailDto.setQuantity((BigDecimal) row[12]);
            detailDto.setMaterialDesc((String) row[13]);
            detailDto.setAssetDesc((String) row[14]);
            detailDto.setUomId((String) row[15]);
            
            masterDto.getDetails().add(detailDto);
        }
        
        return new ArrayList<>(reportMap.values());
    }
}
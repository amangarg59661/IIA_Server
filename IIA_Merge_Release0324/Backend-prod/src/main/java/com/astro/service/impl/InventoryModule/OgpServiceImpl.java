package com.astro.service.impl.InventoryModule;

import com.astro.dto.workflow.InventoryModule.ogp.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.astro.service.InventoryModule.GtService;
import com.astro.service.InventoryModule.OgpService;
import com.astro.repository.UserMasterRepository;
import com.astro.repository.InventoryModule.AssetMasterRepository;
import com.astro.repository.InventoryModule.isn.IssueNoteMasterRepository;
import com.astro.repository.InventoryModule.ogp.OgpDetailRejectedGiRepository;
import com.astro.repository.InventoryModule.ogp.OgpDetailRepository;
import com.astro.repository.InventoryModule.ogp.OgpGtDtlRepository;
import com.astro.repository.InventoryModule.ogp.OgpGtMasterRepository;
import com.astro.repository.InventoryModule.ogp.OgpMasterPoRepository;
import com.astro.repository.InventoryModule.ogp.OgpMasterRejectedGiRepository;
import com.astro.repository.InventoryModule.ogp.OgpPoDetailRepository;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderAttributesRepository;
import com.astro.repository.InventoryModule.ogp.OgpMasterRepository;
import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtDtl;
import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtMasterDto;
import com.astro.dto.workflow.InventoryModule.ogp.GprApprovalDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpDetailReportDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpMasterRejectedGiDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpMaterialDtlDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoDtlDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoMaterialDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoResponseDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpRejectedGiDtlDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpReportDto;
import com.astro.entity.UserMaster;
import com.astro.entity.InventoryModule.OgpDetailEntity;
import com.astro.entity.InventoryModule.OgpDetailRejectedGiEntity;
import com.astro.entity.InventoryModule.OgpGtDtlEntity;
import com.astro.entity.InventoryModule.OgpGtMasterEntity;
import com.astro.entity.InventoryModule.OgpMasterEntity;
import com.astro.entity.InventoryModule.OgpMasterPoEntity;
import com.astro.entity.InventoryModule.OgpMasterRejectedGiEntity;
import com.astro.entity.InventoryModule.OgpPoDetailEntity;
import com.astro.entity.ProcurementModule.PurchaseOrder;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.util.CommonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.modelmapper.ModelMapper;

@Service
public class OgpServiceImpl implements OgpService {
    
    @Autowired
    private OgpMasterPoRepository ogpMasterPoRepository;
    
    @Autowired
    private OgpPoDetailRepository ogpPoDetailRepository;
    
    @Autowired
    private OgpMasterRepository ogpMasterRepository;
    
    @Autowired
    private OgpDetailRepository ogpDetailRepository;
    
    @Autowired
    private IssueNoteMasterRepository isnMasterRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AssetMasterRepository amr;

    @Autowired
    private PurchaseOrderAttributesRepository poMasterRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Autowired
    private OgpMasterRejectedGiRepository omrgr;

    @Autowired
    private OgpDetailRejectedGiRepository odrgr;

    @Autowired
    private OgpGtMasterRepository ogmr;

    @Autowired
    private OgpGtDtlRepository ogdr;

    @Autowired
    private GtService gtService;

    @Override
    @Transactional
    public String saveOgp(OgpDto req) {
        validateIsn(req.getIssueNoteId());
        Integer issueNoteId = Integer.parseInt(req.getIssueNoteId().split("/")[1]);

        // Validate if OGP already exists for these specific items
        StringBuilder errorMessage = new StringBuilder();
        Boolean errorFound = false;

        for (OgpMaterialDtlDto material : req.getMaterialDtlList()) {
            if (ogpDetailRepository.existsByIssueNoteIdAndAssetIdAndLocatorId(
                    issueNoteId, 
                    material.getAssetId(), 
                    material.getLocatorId())) {
                errorMessage.append("OGP already exists for Asset ID: ")
                    .append(material.getAssetId())
                    .append(" at Locator: ")
                    .append(material.getLocatorId())
                    .append(". ");
                errorFound = true;
            }
        }

        if (errorFound) {
            throw new BusinessException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                errorMessage.toString()));
        }

        // Create OGP master
        final OgpMasterEntity ogpMaster = new OgpMasterEntity();  // Manual mapping instead of ModelMapper
        ogpMaster.setOgpDate(CommonUtils.convertStringToDateObject(req.getOgpDate()));
        ogpMaster.setIssueNoteId(issueNoteId);
        ogpMaster.setCreateDate(LocalDateTime.now());
        ogpMaster.setOgpProcessId("INV" + issueNoteId);
        ogpMaster.setCreatedBy(req.getCreatedBy());
        ogpMaster.setLocationId(req.getLocationId());
        ogpMaster.setOgpType(req.getOgpType());
        ogpMaster.setSenderName(req.getSenderName());
        ogpMaster.setStatus("AWAITING APPROVAL");
        if(Objects.nonNull(req.getDateOfReturn())){
            ogpMaster.setDateOfReturn(CommonUtils.convertStringToDateObject(req.getDateOfReturn()));
        }
        ogpMaster.setReceiverLocation(req.getReceiverLocation());
        ogpMaster.setReceiverName(req.getReceiverName());

        final OgpMasterEntity savedOgpMaster = ogpMasterRepository.save(ogpMaster);

        // Save OGP details
        List<OgpDetailEntity> ogpDetails = req.getMaterialDtlList().stream()
            .map(dtl -> {
                OgpDetailEntity detail = new OgpDetailEntity();  // Manual mapping instead of ModelMapper
                detail.setOgpProcessId(savedOgpMaster.getOgpProcessId()); 
                detail.setOgpSubProcessId(savedOgpMaster.getOgpSubProcessId());
                detail.setIssueNoteId(issueNoteId);
                detail.setAssetId(dtl.getAssetId());
                detail.setLocatorId(dtl.getLocatorId());
                detail.setQuantity(dtl.getQuantity());
                return detail;
            })
            .collect(Collectors.toList());
        
        ogpDetailRepository.saveAll(ogpDetails);

        return ogpMaster.getOgpProcessId() + "/" + ogpMaster.getOgpSubProcessId();
    }

    @Override
    public OgpDto getOgpDtls(String processNo) {
        String[] processNoSplit = processNo.split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process number format"));
        }

        Integer ogpSubProcessId = Integer.parseInt(processNoSplit[1]);
        
        OgpMasterEntity ogpMaster = ogpMasterRepository.findById(ogpSubProcessId)
            .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "OGP not found")));

        List<OgpDetailEntity> ogpDetails = ogpDetailRepository.findByOgpSubProcessId(ogpSubProcessId);


        OgpDto response = modelMapper.map(ogpMaster, OgpDto.class);
        response.setOgpDate(CommonUtils.convertDateToString(ogpMaster.getOgpDate()));
        response.setIssueNoteId(processNo);
        response.setOgpId(ogpMaster.getOgpProcessId() + "/" + ogpMaster.getOgpSubProcessId());

        List<OgpMaterialDtlDto> materialDtls = ogpDetails.stream()
            .map(detail -> {
                OgpMaterialDtlDto dto = modelMapper.map(detail, OgpMaterialDtlDto.class);
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

    public void validateIsn(String processNo) {
        String[] processNoSplit = processNo.split("/");
        if (processNoSplit.length != 2 || !processNoSplit[0].equals("INV")) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid ISN No."));
        }

        try {
            Integer subProcessId = Integer.parseInt(processNoSplit[1]);
            
            if (!isnMasterRepository.existsById(subProcessId)) {
                throw new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Provided ISN No. is not valid."));
            }
        } catch (NumberFormatException e) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid ISN number format"));
        }
    }

    @Override
    public List<OgpReportDto> getOgpReport(String startDate, String endDate) {
        List<LocalDateTime> dateRange = CommonUtils.getDateRenge(startDate, endDate);
        List<Object[]> results = ogpMasterRepository.getOgpReport(dateRange.get(0), dateRange.get(1));
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return results.stream().map(row -> {
            OgpReportDto dto = new OgpReportDto();
            dto.setOgpProcessId((String) row[0]);
            dto.setOgpSubProcessId((Integer) row[1]);
            dto.setIssueNoteId((Integer) row[2]);
            dto.setOgpDate(CommonUtils.convertSqlDateToString((java.sql.Date) row[3]));
            // dto.setOgpDate( row[3] != null ? ((java.sql.Timestamp) row[3]).toLocalDateTime() : null);
            dto.setLocationId((String) row[4]);
            dto.setCreatedBy(row[5] != null ? String.valueOf(row[5]) : null);
            
            try {
                String detailsJson = (String) row[7];
                List<OgpDetailReportDto> details = mapper.readValue(
                    detailsJson, 
                    new TypeReference<List<OgpDetailReportDto>>() {}
                );
                dto.setOgpDetails(details);
            } catch (Exception e) {
                dto.setOgpDetails(new ArrayList<>());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OgpRejectedGiReportDto> getOgpRejectedGiReport(String startDate, String endDate) {
        List<LocalDateTime> dateRange = CommonUtils.getDateRenge(startDate, endDate);
        List<OgpRejectedGiReportDto> reports = new ArrayList<>();

        List<Object[]> results =  ogpMasterRepository.getOgpRejectedGiReport(dateRange.get(0), dateRange.get(1));

        ObjectMapper mapper = new ObjectMapper();

        for (Object[] row : results) {
            OgpRejectedGiReportDto dto = new OgpRejectedGiReportDto();
            dto.setOgpSubProcessId((Integer) row[0]);
            dto.setOgpType((String) row[1]);
            dto.setStatus((String) row[2]);
            dto.setGiId((String) row[3]);
            dto.setLocationId((String) row[4]);
            dto.setCreatedBy((String) row[5]);
            dto.setSenderName((String) row[6]);
            dto.setReceiverName((String) row[7]);
            dto.setReceiverLocation((String) row[8]);
            dto.setOgpDate(row[9] != null ? row[9].toString() : null);
            dto.setReturnDate(row[10] != null ? row[10].toString() : null);

            try {
                String detailsJson = (String) row[11];
                List<OgpDetailRejectedGiReportDto> details = mapper.readValue(
                        detailsJson,
                        new TypeReference<List<OgpDetailRejectedGiReportDto>>() {}
                );
                dto.setRejectedDetails(details);
            } catch (Exception e) {
                dto.setRejectedDetails(new ArrayList<>());
            }

            reports.add(dto);
        }

        return reports;
    }


    @Override
    @Transactional
    public String savePoOgp(OgpPoDto request) {
        // Get existing OGP quantities for this PO
        List<OgpMasterPoEntity> existingOgps = ogpMasterPoRepository.findByPoId(request.getPoId());
        
        // Validate quantities against PO
        StringBuilder errorMessage = new StringBuilder();
        Boolean errorFound = false;
        
        for (OgpPoDtlDto dtl : request.getMaterialDtlList()) {
            // Get sum of existing OGP quantities for this material
            BigDecimal existingQuantity = existingOgps.stream()
                .flatMap(ogp -> ogpPoDetailRepository.findByOgpSubProcessIdAndMaterialCode(
                    ogp.getOgpSubProcessId(), 
                    dtl.getMaterialCode()
                ).stream())
                .map(OgpPoDetailEntity::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Add current quantity
            BigDecimal totalQuantity = existingQuantity.add(dtl.getQuantity());
            
            // Get PO quantity from PO master
            BigDecimal poQuantity = poMasterRepository.findQuantityByPoIdAndMaterialCode(
                request.getPoId(), 
                dtl.getMaterialCode()
            ).orElse(BigDecimal.ZERO);
            
            if (totalQuantity.compareTo(poQuantity) > 0) {
                errorMessage.append("Total OGP quantity for material ")
                    .append(dtl.getMaterialCode())
                    .append(" exceeds PO quantity. PO quantity: ")
                    .append(poQuantity)
                    .append(", Total OGP quantity: ")
                    .append(totalQuantity)
                    .append(". ");
                errorFound = true;
            }
        }

        if (errorFound) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                errorMessage.toString()));
        }

        // Create and save OGP Master PO
        OgpMasterPoEntity ogpMasterPo = new OgpMasterPoEntity();
        ogpMasterPo.setPoId(request.getPoId());
        ogpMasterPo.setStatus("AWAITING APPROVAL");
        ogpMasterPo.setOgpDate(CommonUtils.convertStringToDateObject(request.getOgpDate()));
        ogpMasterPo.setLocationId(request.getLocationId());
        ogpMasterPo.setCreatedBy(request.getCreatedBy());
        ogpMasterPo.setCreateDate(LocalDateTime.now());
        ogpMasterPo.setOgpType(request.getOgpType());
        if(Objects.nonNull(request.getDateOfReturn())){
            ogpMasterPo.setDateOfReturn(CommonUtils.convertStringToDateObject(request.getDateOfReturn()));
        }
        ogpMasterPo.setSenderName(request.getSenderName());
        ogpMasterPo.setReceiverLocation(request.getReceiverLocation());
        ogpMasterPo.setReceiverName(request.getReceiverName());

        OgpMasterPoEntity savedMaster = ogpMasterPoRepository.save(ogpMasterPo);

        // Save OGP PO Details
        List<OgpPoDetailEntity> details = request.getMaterialDtlList().stream()
            .map(dtl -> {
                OgpPoDetailEntity detail = new OgpPoDetailEntity();
                detail.setOgpSubProcessId(savedMaster.getOgpSubProcessId());
                detail.setMaterialCode(dtl.getMaterialCode());
                detail.setMaterialDescription(dtl.getMaterialDescription());
                detail.setUomId(dtl.getUom());
                detail.setQuantity(dtl.getQuantity());
                return detail;
            })
            .collect(Collectors.toList());

        ogpPoDetailRepository.saveAll(details);

        return "INV/" + savedMaster.getOgpSubProcessId().toString();
    }
    
    @Override
    public OgpPoResponseDto getPoOgp(String processNo) {
        String[] processNoSplit = processNo.split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process number format"));
        }

        Integer ogpSubProcessId = Integer.parseInt(processNoSplit[1]);
        
        // Get OGP PO master
        OgpMasterPoEntity ogpMasterPo = ogpMasterPoRepository.findById(ogpSubProcessId)
            .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "OGP PO not found")));

        // Get OGP PO details
        List<OgpPoDetailEntity> ogpPoDetails = ogpPoDetailRepository.findByOgpSubProcessId(ogpSubProcessId);

        // Map to response DTO
        OgpPoResponseDto response = new OgpPoResponseDto();
        response.setOgpId("INV/" + ogpMasterPo.getOgpSubProcessId());
        response.setPoId(ogpMasterPo.getPoId());
        response.setOgpDate(CommonUtils.convertDateToString(ogpMasterPo.getOgpDate()));
        response.setLocationId(ogpMasterPo.getLocationId());
        response.setCreatedBy(ogpMasterPo.getCreatedBy());
        response.setOgpType(ogpMasterPo.getOgpType());

        // Map details
        List<OgpPoMaterialDto> materialDtls = ogpPoDetails.stream()
            .map(detail -> {
                OgpPoMaterialDto dto = new OgpPoMaterialDto();
                dto.setMaterialCode(detail.getMaterialCode());
                dto.setMaterialDescription(detail.getMaterialDescription());
                dto.setUomId(detail.getUomId());
                dto.setQuantity(detail.getQuantity());
                return dto;
            })
            .collect(Collectors.toList());

        response.setMaterialDtlList(materialDtls);
        return response;
    }

    @Override
    @Transactional
    public void approveOgp(GprApprovalDto req) {
        String processNo = req.getProcessNo();
        String type = req.getType();
        String[] processNoSplit = processNo.split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process number format"));
        }

        Integer ogpSubProcessId = Integer.parseInt(processNoSplit[1]);
        
        if ("ISN".equals(type)) {
            // Handle ISN type OGP
            OgpMasterEntity ogpMaster = ogpMasterRepository.findById(ogpSubProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "OGP not found")));

            ogpMaster.setStatus("APPROVED");
            ogpMasterRepository.save(ogpMaster);
        } else {
            // Handle PO type OGP
            OgpMasterPoEntity poOgp = ogpMasterPoRepository.findById(ogpSubProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "OGP PO not found")));

            poOgp.setStatus("APPROVED");
            ogpMasterPoRepository.save(poOgp);
        }
    }

    @Override
    @Transactional
    public void rejectOgp(GprApprovalDto req) {
        String processNo = req.getProcessNo();
        String type = req.getType();
        String[] processNoSplit = processNo.split("/");
        if (processNoSplit.length != 2) {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process number format"));
        }

        Integer ogpSubProcessId = Integer.parseInt(processNoSplit[1]);
        
        if ("ISN".equals(type)) {
            // Handle ISN type OGP
            OgpMasterEntity ogpMaster = ogpMasterRepository.findById(ogpSubProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "OGP not found")));

            ogpMaster.setStatus("REJECTED");
            ogpMasterRepository.save(ogpMaster);
        } else {
            // Handle PO type OGP
            OgpMasterPoEntity poOgp = ogpMasterPoRepository.findById(ogpSubProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "OGP PO not found")));

            poOgp.setStatus("REJECTED");
            ogpMasterPoRepository.save(poOgp);
        }
    }

    @Override
    @Transactional
    public String saveOgpRejectedGi(OgpMasterRejectedGiDto req){
        OgpMasterRejectedGiEntity omrge = new OgpMasterRejectedGiEntity();
        omrge.setGiId(req.getGiId());
        omrge.setLocationId(req.getLocationId());
        omrge.setOgpDate(CommonUtils.convertStringToDateObject(req.getOgpDate()));
        omrge.setOgpType(req.getOgpType());
        omrge.setCreatedBy(req.getCreatedBy());
        omrge.setReceiverLocation(req.getReceiverLocation());
        omrge.setSenderName(req.getSenderName());
        omrge.setReceiverName(req.getReceiverName());
        omrge.setStatus("AWAITING APPROVAL");
        if(Objects.nonNull(req.getDateOfReturn())){
            omrge.setReturnDate(CommonUtils.convertStringToDateObject(req.getDateOfReturn()));
        }

        omrge = omrgr.save(omrge);

        List<OgpDetailRejectedGiEntity> odrgeList = new ArrayList<>();

        for(OgpRejectedGiDtlDto dto : req.getMaterialDtlList()){
            OgpDetailRejectedGiEntity odrge = new OgpDetailRejectedGiEntity();
            odrge.setAssetDesc(dto.getAssetDesc());
            odrge.setAssetId(dto.getAssetId());
            odrge.setAssetCode(dto.getAssetCode());
            odrge.setMaterialCode(dto.getMaterialCode());
            odrge.setMaterialDesc(dto.getMaterialDesc());
            odrge.setRejectedQuantity(dto.getRejectedQuantity());
            odrge.setRejectionType(dto.getRejectionType());
            odrge.setOgpSubprocessId(omrge.getOgpSubProcessId());
            odrgeList.add(odrge);
        }

        odrgr.saveAll(odrgeList);

        return "INV/" + omrge.getOgpSubProcessId();
    }

    // public List<OgpMasterRejectedGiEntity> getAwaitingRejectedGi(){

    //     List<OgpMasterRejectedGiEntity> omrgeList = omrgr.findByStatus("AWAITING APPROVAL");

    //     for(OgpMasterRejectedGiEntity omrge : omrgeList){
    //         List<OgpDetailRejectedGiEntity> odrgeList = odrgr.findByOgpSubprocessId(omrge.getOgpSubProcessId());

    //     }

    // }

@Override
    public List<OgpMasterRejectedGiDto> getAwaitingRejectedGi() {
    List<OgpMasterRejectedGiEntity> omrgeList = omrgr.findByStatus("AWAITING APPROVAL");
    List<OgpMasterRejectedGiDto> dtoList = new ArrayList<>();

    for (OgpMasterRejectedGiEntity omrge : omrgeList) {
        OgpMasterRejectedGiDto dto = new OgpMasterRejectedGiDto();

        dto.setOgpDate(CommonUtils.convertDateToString(omrge.getOgpDate()));
        dto.setGiId(omrge.getGiId());
        dto.setLocationId(omrge.getLocationId());
        dto.setCreatedBy(omrge.getCreatedBy() != null ? omrge.getCreatedBy().toString() : null);
        dto.setSenderName(omrge.getSenderName());
        dto.setReceiverName(omrge.getReceiverName());
        dto.setReceiverLocation(omrge.getReceiverLocation());
        dto.setDateOfReturn(CommonUtils.convertDateToString(omrge.getReturnDate()));
        dto.setOgpId("INV/" + omrge.getOgpSubProcessId());
        dto.setOgpType(omrge.getOgpType());

        List<OgpDetailRejectedGiEntity> odrgeList = odrgr.findByOgpSubprocessId(omrge.getOgpSubProcessId());
        List<OgpRejectedGiDtlDto> materialDtlList = odrgeList.stream().map(detail -> {
            OgpRejectedGiDtlDto mdto = new OgpRejectedGiDtlDto();
            mdto.setMaterialCode(detail.getMaterialCode());
            mdto.setMaterialDesc(detail.getMaterialDesc());
            mdto.setAssetId(detail.getAssetId());
            mdto.setAssetCode(detail.getAssetCode());
            mdto.setAssetDesc(detail.getAssetDesc());
            mdto.setRejectedQuantity(detail.getRejectedQuantity());
            mdto.setRejectionType(detail.getRejectionType());
            return mdto;
        }).collect(Collectors.toList());

        dto.setMaterialDtlList(materialDtlList);
        dtoList.add(dto);
    }

    return dtoList;
}

    @Override
    public void approveGiOgp(String ogpId){
        Integer ogpSubprocessId = Integer.parseInt(ogpId.split("/")[1]);
        OgpMasterRejectedGiEntity omrge = omrgr.findById(ogpSubprocessId)
                                    .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                                        AppConstant.ERROR_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_RESOURCE,
                                        "OGP not found")));
        omrge.setStatus("APPROVED");
        omrgr.save(omrge);
    }
    @Override
    public void rejectGiOgp(String ogpId){
        Integer ogpSubprocessId = Integer.parseInt(ogpId.split("/")[1]);
        OgpMasterRejectedGiEntity omrge = omrgr.findById(ogpSubprocessId)
                                    .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                                        AppConstant.ERROR_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_RESOURCE,
                                        "OGP not found")));
        omrge.setStatus("REJECTED");
        omrgr.save(omrge);
    }

    @Override
    @Transactional
    public String saveGtOgp(GtMasterDto gtMasterDto){
        OgpGtMasterEntity gtMasterEntity = new OgpGtMasterEntity();
        gtMasterEntity.setGtId(Long.parseLong(gtMasterDto.getId().split("/")[1]));
        gtMasterEntity.setSenderLocationId(gtMasterDto.getSenderLocationId());
        
        gtMasterEntity.setReceiverLocationId(gtMasterDto.getReceiverLocationId());
        gtMasterEntity.setReceiverCustodianId(gtMasterDto.getReceiverCustodianId());
        gtMasterEntity.setSenderCustodianId(gtMasterDto.getSenderCustodianId());
        gtMasterEntity.setGtDate(CommonUtils.convertStringToDateObject(gtMasterDto.getGtDate()));
        gtMasterEntity.setCreatedBy(gtMasterDto.getCreatedBy());
        gtMasterEntity.setCreateDate(LocalDateTime.now());
       // gtMasterEntity.setStatus("AWAITING APPROVAL");
        gtMasterEntity.setStatus("PENDING RECEIVER APPROVAL");
        gtMasterEntity = ogmr.save(gtMasterEntity);

        for (GtDtl gtDtl : gtMasterDto.getMaterialDtlList()) {
            OgpGtDtlEntity gtDtlEntity = new OgpGtDtlEntity();
            gtDtlEntity.setGtId(gtMasterEntity.getId());
            gtDtlEntity.setAssetId(gtDtl.getAssetId());
            gtDtlEntity.setAssetCode(gtDtl.getAssetCode());
            gtDtlEntity.setSerialNo(gtDtlEntity.getSerialNo());
            gtDtlEntity.setAssetDesc(gtDtl.getAssetDesc());
            gtDtlEntity.setMaterialCode(gtDtl.getMaterialCode());
            gtDtlEntity.setUnitPrice(gtDtl.getUnitPrice());
            gtDtlEntity.setDepriciationRate(gtDtl.getDepriciationRate());
            gtDtlEntity.setBookValue(gtDtl.getBookValue());
            gtDtlEntity.setMaterialDesc(gtDtl.getMaterialDesc());
            gtDtlEntity.setQuantity(gtDtl.getQuantity());
            gtDtlEntity.setReceiverLocatorId(gtDtl.getReceiverLocatorId());
            gtDtlEntity.setSenderLocatorId(gtDtl.getSenderLocatorId());
            ogdr.save(gtDtlEntity);
        }

        return "INV/" + gtMasterEntity.getId();
    }

    @Override
    public List<GtMasterDto> getPendingGtOgp(){
       // List<OgpGtMasterEntity> gtMasterEntityList = ogmr.findByStatus("AWAITING APPROVAL");
        List<OgpGtMasterEntity> gtMasterEntityList = ogmr.findByStatus("RECEIVER APPROVED");
        List<GtMasterDto> gtMasterDtoList = new ArrayList<>();
        for (OgpGtMasterEntity gtMasterEntity : gtMasterEntityList) {
            GtMasterDto gtMasterDto = new GtMasterDto();
            gtMasterDto.setGtId("INV/" + gtMasterEntity.getGtId());
            gtMasterDto.setId("INV/" + gtMasterEntity.getId());
            gtMasterDto.setSenderLocationId(gtMasterEntity.getSenderLocationId());
            gtMasterDto.setReceiverLocationId(gtMasterEntity.getReceiverLocationId());
            gtMasterDto.setReceiverCustodianId(gtMasterEntity.getReceiverCustodianId());
            gtMasterDto.setSenderCustodianId(gtMasterEntity.getSenderCustodianId());
            gtMasterDto.setGtDate(CommonUtils.convertDateToString(gtMasterEntity.getGtDate()));
            gtMasterDto.setStatus(gtMasterEntity.getStatus());
            gtMasterDto.setCreatedBy(gtMasterEntity.getCreatedBy());

            List<OgpGtDtlEntity> gtDtlEntityList = ogdr.findByGtId(gtMasterEntity.getId());
            List<GtDtl> gtDtlList = new ArrayList<>();
            for (OgpGtDtlEntity gtDtlEntity : gtDtlEntityList) {
                GtDtl gtDtl = new GtDtl();
                gtDtl.setAssetId(gtDtlEntity.getAssetId());
                gtDtl.setAssetCode(gtDtlEntity.getAssetCode());
                gtDtl.setSerialNo(gtDtlEntity.getSerialNo());
                gtDtl.setAssetDesc(gtDtlEntity.getAssetDesc());
                gtDtl.setMaterialCode(gtDtlEntity.getMaterialCode());
                gtDtl.setMaterialDesc(gtDtlEntity.getMaterialDesc());
                gtDtl.setQuantity(gtDtlEntity.getQuantity());
                gtDtl.setUnitPrice(gtDtlEntity.getUnitPrice());
                gtDtl.setDepriciationRate(gtDtlEntity.getDepriciationRate());
                gtDtl.setBookValue(gtDtlEntity.getBookValue());
                gtDtl.setReceiverLocatorId(gtDtlEntity.getReceiverLocatorId());
                gtDtl.setSenderLocatorId(gtDtlEntity.getSenderLocatorId());
                gtDtlList.add(gtDtl);
            }
            gtMasterDto.setMaterialDtlList(gtDtlList);
            gtMasterDtoList.add(gtMasterDto);
        }
        return gtMasterDtoList;
    }


    @Override
    public List<GtMasterDto> getReciverPendingGtOgp(Integer userId) {
        // Fetch only records with status = PENDING RECEIVER APPROVAL
        List<OgpGtMasterEntity> gtMasterEntityList = ogmr.findByStatus("PENDING RECEIVER APPROVAL");
        List<GtMasterDto> gtMasterDtoList = new ArrayList<>();

        for (OgpGtMasterEntity gtMasterEntity : gtMasterEntityList) {
            // Filter by userId = receiverCustodianId
            if (gtMasterEntity.getReceiverCustodianId() != null &&
                    gtMasterEntity.getReceiverCustodianId().equals(userId)) {

                GtMasterDto gtMasterDto = new GtMasterDto();
                gtMasterDto.setGtId("INV/" + gtMasterEntity.getGtId());
                gtMasterDto.setId("INV/" + gtMasterEntity.getId());
                gtMasterDto.setSenderLocationId(gtMasterEntity.getSenderLocationId());
                gtMasterDto.setReceiverLocationId(gtMasterEntity.getReceiverLocationId());
                gtMasterDto.setReceiverCustodianId(gtMasterEntity.getReceiverCustodianId());
                gtMasterDto.setSenderCustodianId(gtMasterEntity.getSenderCustodianId());
                gtMasterDto.setGtDate(CommonUtils.convertDateToString(gtMasterEntity.getGtDate()));
                gtMasterDto.setStatus(gtMasterEntity.getStatus());
                gtMasterDto.setCreatedBy(gtMasterEntity.getCreatedBy());

                // Fetch material details
                List<OgpGtDtlEntity> gtDtlEntityList = ogdr.findByGtId(gtMasterEntity.getId());
                List<GtDtl> gtDtlList = new ArrayList<>();
                for (OgpGtDtlEntity gtDtlEntity : gtDtlEntityList) {
                    GtDtl gtDtl = new GtDtl();
                    gtDtl.setAssetId(gtDtlEntity.getAssetId());
                    gtDtl.setAssetCode(gtDtlEntity.getAssetCode());
                    gtDtl.setSerialNo(gtDtlEntity.getSerialNo());
                    gtDtl.setAssetDesc(gtDtlEntity.getAssetDesc());
                    gtDtl.setMaterialCode(gtDtlEntity.getMaterialCode());
                    gtDtl.setMaterialDesc(gtDtlEntity.getMaterialDesc());
                    gtDtl.setQuantity(gtDtlEntity.getQuantity());
                    gtDtl.setUnitPrice(gtDtlEntity.getUnitPrice());
                    gtDtl.setDepriciationRate(gtDtlEntity.getDepriciationRate());
                    gtDtl.setBookValue(gtDtlEntity.getBookValue());
                    gtDtl.setReceiverLocatorId(gtDtlEntity.getReceiverLocatorId());
                    gtDtl.setSenderLocatorId(gtDtlEntity.getSenderLocatorId());
                    gtDtlList.add(gtDtl);
                }
                gtMasterDto.setMaterialDtlList(gtDtlList);
                gtMasterDtoList.add(gtMasterDto);
            }
        }
        return gtMasterDtoList;
    }
    @Override
    @Transactional
    public void approveReceiverGtOgp(String ogpId) {
        OgpGtMasterEntity gtMasterEntity = ogmr.findById(Long.parseLong(ogpId.split("/")[1]))
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "OGP not found")));

        gtMasterEntity.setStatus("RECEIVER APPROVED");
        ogmr.save(gtMasterEntity);
    }

    @Override
    @Transactional
    public void approveGtOgp(String ogpId){
        OgpGtMasterEntity gtMasterEntity = ogmr.findById(Long.parseLong(ogpId.split("/")[1]))
                                    .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                                        AppConstant.ERROR_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_RESOURCE,
                                        "OGP not found")));
        if (!"RECEIVER APPROVED".equals(gtMasterEntity.getStatus())) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Receiver approval pending. Cannot approve GT."
            ));
        }
        gtMasterEntity.setStatus("APPROVED");
        ogmr.save(gtMasterEntity);
        gtService.approveGtFromOgp("INV/" + gtMasterEntity.getGtId());
    }

    @Override
    @Transactional
    public void rejectGtOgp(String ogpId){
        OgpGtMasterEntity gtMasterEntity = ogmr.findById(Long.parseLong(ogpId.split("/")[1]))
                                    .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                                        AppConstant.ERROR_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                        AppConstant.ERROR_TYPE_RESOURCE,
                                        "OGP not found")));
        gtMasterEntity.setStatus("REJECTED");
        ogmr.save(gtMasterEntity);
    }




    
    // @Override
    // @Transactional
//     public void updateOgp(OgpDto updateRequest) {
//         String processNo = updateRequest.getOgpId();
//         String[] processNoSplit = processNo.split("/");
//         if (processNoSplit.length != 2) {
//             throw new InvalidInputException(new ErrorDetails(
//                 AppConstant.USER_INVALID_INPUT,
//                 AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                 AppConstant.ERROR_TYPE_VALIDATION,
//                 "Invalid process number format"));
//         }
    
//         Integer ogpSubProcessId = Integer.parseInt(processNoSplit[1]);
        
//         OgpMasterEntity ogpMaster = ogpMasterRepository.findById(ogpSubProcessId)
//             .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
//                 AppConstant.ERROR_CODE_RESOURCE,
//                 AppConstant.ERROR_TYPE_CODE_RESOURCE,
//                 AppConstant.ERROR_TYPE_RESOURCE,
//                 "OGP not found")));
        
//         // Only allow updates for OGPs in AWAITING APPROVAL or CHANGE REQUEST status
//         if (!ogpMaster.getStatus().equals("AWAITING APPROVAL") && !ogpMaster.getStatus().equals("CHANGE REQUEST")) {
//             throw new InvalidInputException(new ErrorDetails(
//                 AppConstant.USER_INVALID_INPUT,
//                 AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                 AppConstant.ERROR_TYPE_VALIDATION,
//                 "Cannot update OGP in " + ogpMaster.getStatus() + " status"));
//         }
        
//         // Update master fields
//         ogpMaster.setOgpDate(CommonUtils.convertStringToDateObject(updateRequest.getOgpDate()));
//         ogpMaster.setDateOfReturn(CommonUtils.convertStringToDateObject(updateRequest.getDateOfReturn()));
//         ogpMaster.setReceiverLocation(updateRequest.getReceiverLocation());
//         ogpMaster.setReceiverName(updateRequest.getReceiverName());
//         ogpMaster.setOgpType(updateRequest.getOgpType());
//         ogpMaster.setUpdateDate(LocalDateTime.now());
//         ogpMaster.setStatus("AWAITING APPROVAL"); // Reset status to awaiting approval
        
//         ogpMasterRepository.save(ogpMaster);
        
//         // Update material details if provided
//         if (updateRequest.getMaterialDtlList() != null && !updateRequest.getMaterialDtlList().isEmpty()) {
//             List<OgpDetailEntity> existingDetails = ogpDetailRepository.findByOgpSubProcessId(ogpSubProcessId);
            
//             // Create a map of existing details for easy lookup
//             Map<String, OgpDetailEntity> existingDetailsMap = existingDetails.stream()
//                 .collect(Collectors.toMap(
//                     detail -> detail.getAssetId() + "-" + detail.getLocatorId(),
//                     detail -> detail
//                 ));
            
//             StringBuilder errorMessage = new StringBuilder();
//             Boolean errorFound = false;
            
//             // Process each material in the update request
//             for (OgpMaterialDtlDto materialDto : updateRequest.getMaterialDtlList()) {
//                 String key = materialDto.getAssetId() + "-" + materialDto.getLocatorId();
//                 OgpDetailEntity detail = existingDetailsMap.get(key);
                
//                 if (detail == null) {
//                     errorMessage.append("Material with Asset ID ")
//                         .append(materialDto.getAssetId())
//                         .append(" and Locator ID ")
//                         .append(materialDto.getLocatorId())
//                         .append(" not found in OGP. ");
//                     errorFound = true;
//                     continue;
//                 }
                
//                 // Calculate quantity difference
//                 BigDecimal existingQuantity = detail.getQuantity();
//                 BigDecimal newQuantity = materialDto.getQuantity();
//                 BigDecimal quantityDifference = newQuantity.subtract(existingQuantity);
                
//                 // If quantity is increased, check OHQ availability
//                 if (quantityDifference.compareTo(BigDecimal.ZERO) > 0) {
//                     // Check OHQ availability (assuming you have a repository method for this)
//                     BigDecimal availableQuantity = amr.findAvailableQuantityByAssetIdAndLocatorId(
//                         materialDto.getAssetId(), 
//                         materialDto.getLocatorId()
//                     ).orElse(BigDecimal.ZERO);
                    
//                     if (availableQuantity.compareTo(quantityDifference) < 0) {
//                         errorMessage.append("Insufficient quantity available for Asset ID ")
//                             .append(materialDto.getAssetId())
//                             .append(" at Locator ID ")
//                             .append(materialDto.getLocatorId())
//                             .append(". Available: ")
//                             .append(availableQuantity)
//                             .append(", Additional needed: ")
//                             .append(quantityDifference)
//                             .append(". ");
//                         errorFound = true;
//                         continue;
//                     }
//                 }
                
//                 // Update quantity
//                 detail.setQuantity(newQuantity);
//                 ogpDetailRepository.save(detail);
//             }
            
//             if (errorFound) {
//                 throw new InvalidInputException(new ErrorDetails(
//                     AppConstant.USER_INVALID_INPUT,
//                     AppConstant.ERROR_TYPE_CODE_VALIDATION,
//                     AppConstant.ERROR_TYPE_VALIDATION,
//                     errorMessage.toString()));
//             }
//         }
//     }


}

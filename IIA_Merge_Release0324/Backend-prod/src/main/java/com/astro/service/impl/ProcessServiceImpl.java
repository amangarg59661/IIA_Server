package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.GprnDto.SaveGprnDto;
import com.astro.dto.workflow.InventoryModule.PendingGprnPoDto;
import com.astro.dto.workflow.InventoryModule.grn.GrnDto;
import com.astro.dto.workflow.InventoryModule.grv.GrvDto;
import com.astro.dto.workflow.InventoryModule.igp.IgpDto;
import com.astro.dto.workflow.InventoryModule.isn.IsnDto;
import com.astro.dto.workflow.InventoryModule.ogp.GprApprovalDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpMasterRejectedGiDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoResponseDto;
import com.astro.entity.InventoryModule.IsnAssetOhqDtlsDto;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;

import com.astro.service.ProcessService;
import com.astro.service.InventoryModule.GiService;
import com.astro.service.InventoryModule.GprnService;
import com.astro.service.InventoryModule.GrnService;
import com.astro.service.InventoryModule.GrvService;
import com.astro.service.InventoryModule.GtService;
import com.astro.service.InventoryModule.IgpService;
import com.astro.service.InventoryModule.IsnService;
import com.astro.service.InventoryModule.OgpService;
import com.astro.exception.InvalidInputException;
import com.astro.repository.InventoryModule.OhqMasterConsumableRepository;
import com.astro.repository.ohq.OhqMasterRepository;
import com.astro.dto.workflow.InventoryModule.ohq.OhqReportDto;
import com.astro.dto.workflow.InventoryModule.ohq.OhqLocatorDetailDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import com.astro.service.InventoryModule.ServiceInspectionService;      

import com.astro.dto.workflow.InventoryModule.GiDto.SaveGiDto;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProcessServiceImpl implements ProcessService {
    @Autowired
    private GprnService gprnService;
    
    @Autowired
    private GiService giService;
    
    @Autowired
    private GrvService grvService;

    @Autowired
    private ServiceInspectionService serviceInspectionService;  
    
    @Autowired
    private GrnService grnService;
    
    @Autowired
    private IsnService isnService;

    @Autowired
    private OgpService ogpService;

    @Autowired
    private IgpService igpService;

    @Autowired
    private OhqMasterRepository ohqMasterRepository;

    @Autowired
    private OhqMasterConsumableRepository omcr;

    @Autowired
    private GtService gtService;

    @Override
    public String saveGprn(SaveGprnDto req) {
        return gprnService.saveGprn(req);
    }

    @Override
    public String saveGi(SaveGiDto req) {
        return giService.saveGi(req);
    }

    @Override
    public String saveGrv(GrvDto req) {
        return grvService.saveGrv(req);
    }

    @Override
    public String saveGrn(GrnDto req) {
        return grnService.saveGrn(req);
    }

    @Override
    public String saveIsn(IsnDto req) {
        return isnService.saveIsn(req);
    }

    @Override
    public List<IsnAssetOhqDtlsDto> getIsnAssetOhqDtls() {
        return isnService.getAssetMasterAndOhqDtls();
    }

    @Override
    public String saveOgp(OgpDto req) {
        return ogpService.saveOgp(req);
    }

    @Override
    public String saveIgp(IgpDto req) {
        return igpService.saveIgp(req);
    }

    @Override
    public Object getSubProcessDtls(String processStage, String processNo) {
        if (Objects.isNull(processNo) || processNo.isEmpty() || Objects.isNull(processStage)
                || processStage.isEmpty()) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Process Id or Process Stage is empty."));
        }

        switch (processStage) {
            case "GPRN":
                return gprnService.getGprnDtls(processNo);
            case "GI":
                return giService.getGiDtls(processNo);
            case "GRV":
                return grvService.getGrvDtls(processNo);
            case "GRN":
                return grnService.getGrnDtls(processNo);
            case "ISN":
                return isnService.getIsnDtls(processNo);
            case "IGP":
                return igpService.getIgpDtls(processNo);
            case "OGP":
                return ogpService.getOgpDtls(processNo);
            case "GT":
                return gtService.getGtDtls(processNo);
            case "SI":
                return serviceInspectionService.getServiceInspectionDtls(processNo);
            default:
                throw new BusinessException(
                    new ErrorDetails(AppConstant.ERROR_TYPE_CODE_DB,
                        AppConstant.ERROR_TYPE_CODE_DB,
                        AppConstant.ERROR_TYPE_ERROR,
                        "Invalid process stage."));
        }
    }
    @Override
    public List<String> getPendingGprn() {

        return gprnService.getPendingGprn();
    }

    @Override
    public List<PendingGprnPoDto> getPendingGprnDetails(){
        return gprnService.getPendingGprnDetails();
    }

    // @Override
    // public List<OhqReportDto> getOhqReport() {
    //     List<Object[]> results = ohqMasterRepository.getOhqReport();
    //     List<Object[]> resultConsumable = omcr.getOhqConsumableReport();
    //     ObjectMapper mapper = new ObjectMapper();
        
    //     return results.stream().map(row -> {
    //         OhqReportDto dto = new OhqReportDto();
    //         dto.setAssetId((Integer) row[0]);
    //         dto.setAssetDesc((String) row[1]);
    //         dto.setMaterialDesc((String) row[2]);
    //         dto.setUomId((String) row[3]);
    //         dto.setTotalQuantity((BigDecimal) row[4]);
    //         dto.setBookValue((BigDecimal) row[5]);
    //         dto.setDepriciationRate((BigDecimal) row[6]);
    //         dto.setUnitPrice((BigDecimal) row[7]);
            
    //         try {
    //             String locatorDetailsJson = (String) row[8];
    //             List<OhqLocatorDetailDto> locatorDetails = mapper.readValue(
    //                 locatorDetailsJson, 
    //                 new TypeReference<List<OhqLocatorDetailDto>>() {}
    //             );
    //             dto.setLocatorDetails(locatorDetails);
    //         } catch (Exception e) {
    //             dto.setLocatorDetails(new ArrayList<>());
    //         }
    //         dto.setCustodianId((Integer) row[9]);
    //         return dto;
    //     }).collect(Collectors.toList());
    // }


    @Override
public List<OhqReportDto> getOhqReport() {
    List<Object[]> results = ohqMasterRepository.getOhqReport();
    List<Object[]> resultConsumable = omcr.getOhqConsumableReport();
    ObjectMapper mapper = new ObjectMapper();

    List<OhqReportDto> combined = new ArrayList<>();

    // // Map asset-based OHQ
    for (Object[] row : results) {
        OhqReportDto dto = new OhqReportDto();
        dto.setAssetId((Integer) row[0]);
        dto.setAssetDesc((String) row[1]);
        dto.setMaterialDesc((String) row[2]);
        dto.setUomId((String) row[3]);
        dto.setTotalQuantity((BigDecimal) row[4]);
        dto.setBookValue((BigDecimal) row[5]);
        dto.setDepriciationRate((BigDecimal) row[6]);
        dto.setUnitPrice((BigDecimal) row[7]);

        try {
            String locatorDetailsJson = (String) row[8];
            List<OhqLocatorDetailDto> locatorDetails = mapper.readValue(
                locatorDetailsJson,
                new TypeReference<List<OhqLocatorDetailDto>>() {}
            );
            dto.setLocatorDetails(locatorDetails);
        } catch (Exception e) {
            dto.setLocatorDetails(new ArrayList<>());
        }

        dto.setCustodianId(row[9] != null ? (String) row[9] : null);
        dto.setAssetCode(row[10] != null ? (String) row[10] : null);
        combined.add(dto);
    }

    // Map consumable-based OHQ
    for (Object[] row : resultConsumable) {
        OhqReportDto dto = new OhqReportDto();
        // Consumables don't have assetId, so we can set it null and use materialCode instead
        dto.setMaterialCode((String) row[0]); // assuming OhqReportDto has materialCode
        dto.setMaterialDesc((String) row[1]);
        dto.setUomId((String) row[2]);
        dto.setTotalQuantity((BigDecimal) row[3]);
        dto.setBookValue((BigDecimal) row[4]);
        dto.setDepriciationRate((BigDecimal) row[5]);
        dto.setUnitPrice((BigDecimal) row[6]);

        try {
            String locatorDetailsJson = (String) row[7];
            List<OhqLocatorDetailDto> locatorDetails = mapper.readValue(
                locatorDetailsJson,
                new TypeReference<List<OhqLocatorDetailDto>>() {}
            );
            dto.setLocatorDetails(locatorDetails);
        } catch (Exception e) {
            dto.setLocatorDetails(new ArrayList<>());
        }

        dto.setCustodianId(row[8] != null ? (String) row[8] : null);
        combined.add(dto);
    }

    return combined;
}




    @Override
    public String savePoOgp(OgpPoDto request) {
        return ogpService.savePoOgp(request);
    }

    @Override
    public OgpPoResponseDto getPoOgp(String processNo){
        return ogpService.getPoOgp(processNo);
    }

    @Override
    public void approveOgp(GprApprovalDto req) {
    ogpService.approveOgp(req);
    }
    @Override
    public void rejectOgp(GprApprovalDto req) {
        ogpService.rejectOgp(req);
    }
    @Override
    public void approveGprn(String processNo) {
        gprnService.approveGprn(processNo);
    }
    @Override
    public void rejectGprn(String processNo) {
        gprnService.rejectGprn(processNo);
    }

    @Override
    public void updateGprn(SaveGprnDto req) {
        gprnService.updateGprn(req);
    }

    @Override
    public void changeReqGprn(String processNo) {
        gprnService.changeReqGprn(processNo);
    }

    @Override
    public String saveOgpRejectedGi(OgpMasterRejectedGiDto req){
        String id = ogpService.saveOgpRejectedGi(req);
        return id;
    }

    @Override
    public List<OgpMasterRejectedGiDto> getAwaitingRejectedGi(){
        return ogpService.getAwaitingRejectedGi();
    }

    @Override
    public void approveGiOgp(String ogpId){
        ogpService.approveGiOgp(ogpId);
    }

    @Override
    public void rejectGiOgp(String ogpId){
        ogpService.rejectGiOgp(ogpId);
    }
}
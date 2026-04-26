package com.astro.service.impl.InventoryModule;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.astro.service.InventoryModule.GrvService;
import com.astro.service.InventoryModule.GiService;
import com.astro.repository.InventoryModule.grv.*;
import com.astro.entity.InventoryModule.*;
import com.astro.dto.workflow.InventoryModule.grv.*;
import com.astro.exception.*;
import com.astro.constant.AppConstant;
import com.astro.util.CommonUtils;
import org.modelmapper.ModelMapper;

@Service
public class GrvServiceImpl implements GrvService {
    @Autowired
    private GrvMasterRepository grvMasterRepository;
    
    @Autowired
    private GrvMaterialDtlRepository grvMaterialDtlRepository;
    
    @Autowired
    private GiService giService;

    @Override
    @Transactional
    public String saveGrv(GrvDto req) {
      //  giService.validateGiSubProcessId(req.getGiNo());
        giService.validateGiIsApproved(req.getGiNo());

        ModelMapper mapper = new ModelMapper();

        System.out.println("GI SUB PROC:"+ Integer.parseInt(req.getGiNo().split("/")[1]));

        GrvMasterEntity grvMaster = new GrvMasterEntity();
        grvMaster.setDate(CommonUtils.convertStringToDateObject(req.getDate()));
        grvMaster.setCreatedBy(req.getCreatedBy());
        grvMaster.setCreateDate(LocalDateTime.now());
        grvMaster.setGiProcessId(req.getGiNo().split("/")[0].substring(3));
        grvMaster.setGiSubProcessId(Integer.parseInt(req.getGiNo().split("/")[1]));
        grvMaster.setGrvProcessId(req.getGiNo().split("/")[0].substring(3));
        grvMaster.setLocationId(req.getLocationId());

        grvMaster = grvMasterRepository.save(grvMaster);

        List<GrvMaterialDtlEntity> grvMaterialDtlList = new ArrayList<>();
        StringBuilder errorMessage = new StringBuilder();
        Boolean errorFound = false;

        for (GrvMaterialDtlDto materialDtl : req.getMaterialDtlList()) {
            Optional<GrvMaterialDtlEntity> existingGrv = grvMaterialDtlRepository
                    .findByGiSubProcessIdAndMaterialCode(
                            Integer.parseInt(req.getGiNo().split("/")[1]),
                            materialDtl.getMaterialCode());

            if (existingGrv.isPresent()) {
                errorMessage.append("GRV already exists for GI No. " + req.getGiNo() +
                        " and Material Code " + materialDtl.getMaterialCode() + ". ");
                errorFound = true;
                continue;
            }

            if (materialDtl.getRejectedQuantity().compareTo(materialDtl.getReturnQuantity()) < 0) {
                errorMessage.append("Return quantity cannot be greater than rejected quantity for material " +
                        materialDtl.getMaterialCode() + ". ");
                errorFound = true;
                continue;
            }

            GrvMaterialDtlEntity grvMaterialDtl = new GrvMaterialDtlEntity();
            mapper.map(materialDtl, grvMaterialDtl);
            grvMaterialDtl.setGrvProcessId(grvMaster.getGrvProcessId());
            grvMaterialDtl.setGrvSubProcessId(grvMaster.getGrvSubProcessId());
            grvMaterialDtl.setGiSubProcessId(Integer.parseInt(req.getGiNo().split("/")[1]));

            grvMaterialDtlList.add(grvMaterialDtl);
        }

        if (errorFound) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    errorMessage.toString()));
        }

        grvMaterialDtlRepository.saveAll(grvMaterialDtlList);

        return "INV" + grvMaster.getGrvProcessId() + "/" + grvMaster.getGrvSubProcessId();
    }

    @Override
    public Map<String, Object> getGrvDtls(String processNo) {
        ModelMapper mapper = new ModelMapper();
        String[] processNoSplit = processNo.split("/");
        
        // if (processNoSplit.length != 2) {
        //     throw new InvalidInputException(new ErrorDetails(
        //             AppConstant.USER_INVALID_INPUT,
        //             AppConstant.ERROR_TYPE_CODE_VALIDATION,
        //             AppConstant.ERROR_TYPE_VALIDATION,
        //             "Invalid process ID"));
        // }

                Integer grvSubProcessId ;

        if (3 == processNoSplit.length ){
             grvSubProcessId = Integer.parseInt(processNoSplit[2]);
        }else if(2 == processNoSplit.length) {
             grvSubProcessId = Integer.parseInt(processNoSplit[1]);
        }else {
            throw new InvalidInputException(new ErrorDetails(
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Invalid process ID"));
        }

        // Integer grvSubProcessId = Integer.parseInt(processNoSplit[1]);

        GrvMasterEntity grvMaster = grvMasterRepository.findById(grvSubProcessId)
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "GRV not found for the provided process ID.")));

        List<GrvMaterialDtlEntity> grvMaterialList = grvMaterialDtlRepository
                .findByGrvSubProcessId(grvMaster.getGrvSubProcessId());

        List<GrvMaterialDtlDto> materialDtlListRes = grvMaterialList.stream()
                .map(material -> mapper.map(material, GrvMaterialDtlDto.class))
                .collect(Collectors.toList());

        GrvDto grvRes = new GrvDto();
        grvRes.setGrvNo(processNo);
        grvRes.setGiNo("INV" + grvMaster.getGiProcessId() + "/" + grvMaster.getGiSubProcessId());
        grvRes.setDate(CommonUtils.convertDateToString(grvMaster.getDate()));
        grvRes.setCreatedBy(grvMaster.getCreatedBy());
        grvRes.setMaterialDtlList(materialDtlListRes);

        Map<String, Object> giDetails = giService.getGiDtls("INV" + grvMaster.getGiProcessId() + "/" + 
                grvMaster.getGiSubProcessId());
        
        Map<String, Object> combinedRes = new HashMap<>();
        combinedRes.put("grvDtls", grvRes);
        combinedRes.put("giDtls", giDetails.get("giDtls"));
        combinedRes.put("gprnDtls", giDetails.get("gprnDtls"));

        return combinedRes;
    }
}
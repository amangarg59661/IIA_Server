package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.GatepassRequestDTO;
import com.astro.dto.workflow.InventoryModule.GatepassResponseDTO;
import com.astro.entity.InventoryModule.GatepassOutAndIn;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.InventoryModule.GatePassRepository;
import com.astro.service.GatepassService;
import com.astro.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GatepassServiceImpl implements GatepassService {
    @Autowired
    private GatePassRepository gatepassRepository;
    @Override
    public GatepassResponseDTO createGatepass(GatepassRequestDTO requestDTO) {
        // Check if the indentorId already exists
        if (gatepassRepository.existsById(requestDTO.getGatePassId())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate gate pass ID", "Gatepass ID " + requestDTO.getGatePassId() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }
        GatepassOutAndIn gatepass = new GatepassOutAndIn();
        gatepass.setGatePassId(requestDTO.getGatePassId());
        gatepass.setGatePassType(requestDTO.getGatePassType());
        gatepass.setMaterialDetails(requestDTO.getMaterialDetails());
        String EDR = requestDTO.getExpectedDateOfReturn();
        gatepass.setExpectedDateOfReturn(CommonUtils.convertStringToDateObject(EDR));
        gatepass.setExtendEDR(requestDTO.getExtendEDR());

        GatepassOutAndIn savedGatepass = gatepassRepository.save(gatepass);

        return mapToResponseDTO(savedGatepass);
    }

    private GatepassResponseDTO mapToResponseDTO(GatepassOutAndIn gatepass) {

        GatepassResponseDTO responseDTO = new GatepassResponseDTO();
        responseDTO.setGatePassId(gatepass.getGatePassId());
        responseDTO.setGatePassType(gatepass.getGatePassType());
        responseDTO.setMaterialDetails(gatepass.getMaterialDetails());
        LocalDate EDR = gatepass.getExpectedDateOfReturn();
        responseDTO.setExpectedDateOfReturn(CommonUtils.convertDateToString(EDR));
        responseDTO.setExtendEDR(gatepass.getExtendEDR());
        responseDTO.setCreatedBy(gatepass.getCreatedBy());
        responseDTO.setUpdatedBy(gatepass.getUpdatedBy());
        responseDTO.setCreatedDate(gatepass.getCreatedDate());
        responseDTO.setUpdatedDate(gatepass.getUpdatedDate());

        return responseDTO;
    }

    @Override
    public List<GatepassResponseDTO> getAllGatepasses() {
        return gatepassRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public GatepassResponseDTO getGatepassById(String gatePassId) {

       GatepassOutAndIn gatepass=gatepassRepository.findById(gatePassId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "gatepass not found for the provided gate pass ID.")
                ));
        return mapToResponseDTO(gatepass);
    }

    @Override
    public GatepassResponseDTO updateGatepass(String gatePassId, GatepassRequestDTO requestDTO) {
       GatepassOutAndIn gatepass = gatepassRepository.findById(gatePassId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Gate Pass not found for the provided Gate Pass Id.")
                ));
       gatepass.setGatePassId(requestDTO.getGatePassId());
        gatepass.setGatePassType(requestDTO.getGatePassType());
        gatepass.setMaterialDetails(requestDTO.getMaterialDetails());
        String EDR = requestDTO.getExpectedDateOfReturn();
        gatepass.setExpectedDateOfReturn(CommonUtils.convertStringToDateObject(EDR));
        gatepass.setExtendEDR(requestDTO.getExtendEDR());

        GatepassOutAndIn savedGatepass = gatepassRepository.save(gatepass);

        return mapToResponseDTO(savedGatepass);


    }

    @Override
    public void deleteGatepass(String gatePassId) {

       GatepassOutAndIn gatepass=gatepassRepository.findById(gatePassId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Gatepass not found for the provided gate pass Id."
                        )
                ));
        try {
           gatepassRepository.delete(gatepass);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the gatepass ID."
                    ),
                    ex
            );
        }

    }
}

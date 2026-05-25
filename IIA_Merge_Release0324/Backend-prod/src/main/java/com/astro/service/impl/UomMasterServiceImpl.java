package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.UomMasterRequestDto;
import com.astro.dto.workflow.UomMasterResponseDto;
import com.astro.entity.UomMaster;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.UomMasterRepository;
import com.astro.service.UomMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// Added by aman
import com.astro.entity.AdminPanel.LOVMaster;
import java.util.Optional;
// End

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UomMasterServiceImpl implements UomMasterService {

    @Autowired
    private UomMasterRepository uomMasterRepository;

    @Override
    public UomMasterResponseDto createUomMaster(UomMasterRequestDto uomMasterRequestDto) {

        // Check if the indentorId already exists
        if (uomMasterRepository.existsById(uomMasterRequestDto.getUomCode())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate UOM code", "Uom code " + uomMasterRequestDto.getUomCode() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }
        UomMaster uomMaster = new UomMaster();
        uomMaster.setUomCode(uomMasterRequestDto.getUomCode());
        uomMaster.setUomName(uomMasterRequestDto.getUomName());
        uomMaster.setUpdatedBy(uomMasterRequestDto.getUpdatedBy());
        uomMaster.setCreatedBy(uomMasterRequestDto.getCreatedBy());

        uomMasterRepository.save(uomMaster);



        return mapToResponseDTO(uomMaster);
    }

    private UomMasterResponseDto mapToResponseDTO(UomMaster uomMaster) {

        UomMasterResponseDto responseDto = new UomMasterResponseDto();
        responseDto.setUomCode(uomMaster.getUomCode());
        responseDto.setUomName(uomMaster.getUomName());
        responseDto.setCreatedBy(uomMaster.getCreatedBy());
        responseDto.setUpdatedBy(uomMaster.getUpdatedBy());
        responseDto.setCreatedDate(uomMaster.getCreatedDate());
        responseDto.setUpdatedDate(uomMaster.getUpdatedDate());
        // Return the response DTO
        return responseDto;


    }
    // Added by Aman
    // Add these methods inside UomMasterServiceImpl class

// ========== LOV-TRIGGERED METHODS ==========

/**
 * Called by LOVServiceImpl when a new LOV is saved for designatorId = 22 (UOM).
 * Creates a corresponding UomMaster record.
 */
@Override
public void createFromLOV(LOVMaster saved) {
    UomMaster uomMaster = new UomMaster();
    uomMaster.setUomCode(saved.getLovValue());
    uomMaster.setUomName(saved.getLovDisplayValue());
    uomMaster.setCreatedBy(saved.getCreatedBy());
    uomMaster.setUpdatedBy(saved.getCreatedBy());
    uomMasterRepository.save(uomMaster);
}

/**
 * Called by LOVServiceImpl when an existing LOV is updated for designatorId = 22 (UOM).
 * Updates the corresponding UomMaster record, or creates it if missing.
 */
@Override
public void updateFromLOV(LOVMaster updated) {
    Optional<UomMaster> existing = uomMasterRepository.findById(updated.getLovValue());

    UomMaster uomMaster;
    if (existing.isPresent()) {
        uomMaster = existing.get();
    } else {
        uomMaster = new UomMaster();
        uomMaster.setUomCode(updated.getLovValue());
        uomMaster.setCreatedBy(updated.getUpdatedBy() != null ? updated.getUpdatedBy() : updated.getCreatedBy());
    }

    uomMaster.setUomName(updated.getLovDisplayValue());
    uomMaster.setUpdatedBy(updated.getUpdatedBy() != null ? updated.getUpdatedBy() : updated.getCreatedBy());
    uomMasterRepository.save(uomMaster);
}
// End

    @Override
    public UomMasterResponseDto updateUomMaster(String uomCode, UomMasterRequestDto uomMasterRequestDto) {

        UomMaster uomMaster = uomMasterRepository.findById(uomCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "UOM master not found for the provided UOm code.")
                ));

        //uomMaster.setUomCode(uomMasterRequestDto.getUomCode());
        uomMaster.setUomName(uomMasterRequestDto.getUomName());
        uomMaster.setUpdatedBy(uomMasterRequestDto.getUpdatedBy());
        uomMaster.setCreatedBy(uomMasterRequestDto.getCreatedBy());

        uomMasterRepository.save(uomMaster);

        return mapToResponseDTO(uomMaster);
    }

    @Override
    public UomMasterResponseDto getUomMasterById(String uomCode) {
        UomMaster uomMaster= uomMasterRepository.findById(uomCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "UOM master not found for the provided UOM code.")
                ));
        return mapToResponseDTO(uomMaster);

    }

    @Override
    public List<UomMasterResponseDto> getAllUomMasters() {
        List<UomMaster> uomMasters= uomMasterRepository.findAll();
        return uomMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());

    }

    @Override
    public void deleteUomMaster(String uomCode) {

       UomMaster uomMaster=uomMasterRepository.findById(uomCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "UOM master not found for the provided UOM code."
                        )
                ));
        try {
            uomMasterRepository.delete(uomMaster);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the UOM master."
                    ),
                    ex
            );
        }

    }
}

package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.LocationMasterRequestDto;
import com.astro.dto.workflow.LocationMasterResponseDto;
import com.astro.entity.LocationMaster;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.LocationMasterRepository;
import com.astro.service.LocationMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// Added by Aman 
import com.astro.entity.AdminPanel.LOVMaster;
import java.util.Optional;
// End

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationMasterServiceImpl implements LocationMasterService {

    @Autowired
    private LocationMasterRepository locationMasterRepository;
    @Override
    public LocationMasterResponseDto createLocationMaster(LocationMasterRequestDto locationMasterRequestDto) {

        // Check if the indentorId already exists
        if (locationMasterRepository.existsById(locationMasterRequestDto.getLocationCode())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate location code", "location code " + locationMasterRequestDto.getLocationCode() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }
        LocationMaster locationMaster = new LocationMaster();
        locationMaster.setLocationCode(locationMasterRequestDto.getLocationCode());
        locationMaster.setLocationName(locationMasterRequestDto.getLocationName());
        locationMaster.setAddress(locationMasterRequestDto.getAddress());

        locationMasterRepository.save(locationMaster);
        return  mapToResponseDTO(locationMaster);
    }


    @Override
    public LocationMasterResponseDto updateLocationMaster(String locationCode, LocationMasterRequestDto locationMasterRequestDto) {

        LocationMaster locationMaster= locationMasterRepository.findById(locationCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "location Code not found for the provided location code.")
                ));


       // locationMaster.setLocationCode(locationMasterRequestDto.getLocationCode());
        locationMaster.setLocationName(locationMasterRequestDto.getLocationName());
        locationMaster.setAddress(locationMasterRequestDto.getAddress());
        locationMasterRepository.save(locationMaster);
        return mapToResponseDTO(locationMaster);


    }

    @Override
    public List<LocationMasterResponseDto> getAllLocationMasters() {
        List<LocationMaster> locationMasters= locationMasterRepository.findAll();
        return locationMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public LocationMasterResponseDto getLocationMasterById(String locationCode) {
        LocationMaster locationMaster= locationMasterRepository.findById(locationCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Location master not found for the provided location code.")
                ));
        return mapToResponseDTO(locationMaster);
    }

    @Override
    public void deleteLocationMaster(String locationCode) {

        LocationMaster locationMaster=locationMasterRepository.findById(locationCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Location Master not found for the provided location code."
                        )
                ));
        try {
            locationMasterRepository.delete(locationMaster);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the Location master."
                    ),
                    ex
            );
        }

    }
    // Added by Aman
     // ========== LOV-TRIGGERED METHODS ==========

    /**
     * Called by LOVServiceImpl when a new LOV is saved for designatorId = 1 (Location).
     * Creates a corresponding LocationMaster record.
     */
    public void createFromLOV(LOVMaster saved) {
        LocationMaster location = new LocationMaster();
        location.setLocationCode(saved.getLovValue());
        location.setLocationName(saved.getLovDisplayValue());
        location.setAddress(saved.getLovDescription());
        location.setCreatedBy(saved.getCreatedBy());
        location.setUpdatedBy(saved.getCreatedBy());
        locationMasterRepository.save(location);
    }

    /**
     * Called by LOVServiceImpl when an existing LOV is updated for designatorId = 1 (Location).
     * Updates the corresponding LocationMaster record, or creates it if missing.
     */
    public void updateFromLOV(LOVMaster updated) {
        Optional<LocationMaster> existing = locationMasterRepository.findById(updated.getLovValue());

        LocationMaster location;
        if (existing.isPresent()) {
            location = existing.get();
        } else {
            location = new LocationMaster();
            location.setLocationCode(updated.getLovValue());
            location.setCreatedBy(updated.getUpdatedBy() != null ? updated.getUpdatedBy() : updated.getCreatedBy());
        }

        location.setLocationName(updated.getLovDisplayValue());
        location.setAddress(updated.getLovDescription());
        location.setIsActive(updated.getIsActive());
        location.setUpdatedBy(updated.getUpdatedBy() != null ? updated.getUpdatedBy() : updated.getCreatedBy());
        locationMasterRepository.save(location);
    }
    // End

    private LocationMasterResponseDto mapToResponseDTO(LocationMaster locationMaster) {

        LocationMasterResponseDto responseDto = new LocationMasterResponseDto();
        responseDto.setLocationCode(locationMaster.getLocationCode());
        responseDto.setLocationName(locationMaster.getLocationName());
        responseDto.setAddress(locationMaster.getAddress());
        responseDto.setUpdatedBy(locationMaster.getUpdatedBy());
        responseDto.setCreatedBy(locationMaster.getCreatedBy());
        responseDto.setCreatedDate(locationMaster.getCreatedDate());
        responseDto.setUpdatedDate(locationMaster.getUpdatedDate());


        return responseDto;

    }

}

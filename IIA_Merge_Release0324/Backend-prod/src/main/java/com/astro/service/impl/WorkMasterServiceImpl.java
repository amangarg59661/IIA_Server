package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.WorkMasterRequestDto;
import com.astro.dto.workflow.WorkMasterResponseDto;
import com.astro.entity.VendorNamesForJobWorkMaterial;
import com.astro.entity.WorkMaster;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.VendorNamesForJobWorkMaterialRepository;
import com.astro.repository.WorkMasterRepository;
import com.astro.service.WorkMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkMasterServiceImpl implements WorkMasterService {

    @Autowired
    private WorkMasterRepository workMasterRepository;
    @Autowired
    private VendorNamesForJobWorkMaterialRepository vendorNameRepository;
    @Override
    public WorkMasterResponseDto createWorkMaster(WorkMasterRequestDto workMasterRequestDto) {

        String workCode = "W" + System.currentTimeMillis();

        WorkMaster workMaster = new WorkMaster();
        workMaster.setWorkCode(workCode);
        workMaster.setWorkSubCategory(workMasterRequestDto.getWorkSubCategory());
    //    workMaster.setModeOfProcurement(workMasterRequestDto.getModeOfProcurement());
        workMaster.setWorkDescription(workMasterRequestDto.getWorkDescription());

        workMasterRepository.save(workMaster);

        return mapToResponseDTO(workMaster);
    }

    private WorkMasterResponseDto mapToResponseDTO(WorkMaster workMaster) {

        WorkMasterResponseDto responseDto = new WorkMasterResponseDto();
        responseDto.setWorkCode(workMaster.getWorkCode());
        responseDto.setWorkDescription(workMaster.getWorkDescription());
        responseDto.setWorkSubCategory(workMaster.getWorkSubCategory());
     //   responseDto.setModeOfProcurement(workMaster.getModeOfProcurement());
        responseDto.setCreatedBy(workMaster.getCreatedBy());
        responseDto.setUpdatedBy(workMaster.getUpdatedBy());
        responseDto.setCreatedDate(workMaster.getCreatedDate());
        responseDto.setUpdatedDate(workMaster.getUpdatedDate());
        return responseDto;

    }

    @Override
    public WorkMasterResponseDto updateWorkMaster(String workCode, WorkMasterRequestDto workMasterRequestDto) {

        WorkMaster workMaster=  workMasterRepository.findById(workCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Work Master not found for the provided work code.")
                ));

        workMaster.setWorkSubCategory(workMasterRequestDto.getWorkSubCategory());
      //  workMaster.setModeOfProcurement(workMasterRequestDto.getModeOfProcurement());
        workMaster.setWorkDescription(workMasterRequestDto.getWorkDescription());

        workMasterRepository.save(workMaster);

        return mapToResponseDTO(workMaster);

    }

    @Override
    public List<WorkMasterResponseDto> getAllWorkMasters() {
        List<WorkMaster> workMasters= workMasterRepository.findAll();
        return workMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());

    }

    @Override
    public WorkMasterResponseDto getWorkMasterById(String workCode) {
        WorkMaster workMaster= workMasterRepository.findById(workCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Work Master not found for the provided Work code.")
                ));
        return mapToResponseDTO(workMaster);
    }

    @Override
    public void deleteWorkMaster(String workCode) {

        WorkMaster workMaster=workMasterRepository.findById(workCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Work Master not found for the provided Work code."
                        )
                ));
        try {
            workMasterRepository.delete(workMaster);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the Work master."
                    ),
                    ex
            );
        }

    }
}

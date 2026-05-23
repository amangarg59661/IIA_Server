package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.MaterialCreationRequestDto;
import com.astro.dto.workflow.MaterialCreationResponseDto;

import com.astro.entity.MaterialCreation;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.MaterialCreationRepository;
import com.astro.service.MaterialCreationService;
import com.astro.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialCreationServiceImpl implements MaterialCreationService {

    @Autowired
    private MaterialCreationRepository materialCreationRepository;


    @Override
    public MaterialCreationResponseDto createMaterial(MaterialCreationRequestDto materialCreationRequestDto) {

        MaterialCreation materialCreation = new MaterialCreation();
        materialCreation.setMaterialCode(materialCreationRequestDto.getMaterialCode());
        materialCreation.setMaterialName(materialCreationRequestDto.getMaterialName());
        materialCreation.setMaterialDescription(materialCreationRequestDto.getMaterialDescription());
        materialCreation.setModeOfProcurement(materialCreationRequestDto.getModeOfProcurement());
        materialCreation.setMaterialCategory(materialCreationRequestDto.getMaterialCategory());
        materialCreation.setMaterialSubCategory(materialCreationRequestDto.getMaterialSubCategory());
        materialCreation.setUom(materialCreationRequestDto.getUom());
        materialCreation.setEndOfLife(materialCreationRequestDto.getEndOfLife());
        materialCreation.setBookValue(materialCreationRequestDto.getBookValue());
        materialCreation.setDepricationRate(materialCreationRequestDto.getDepricationRate());
        materialCreation.setIndegeniousOrImported(materialCreationRequestDto.getIndegeniousOrImported());
        materialCreation.setMinLevel(materialCreationRequestDto.getMinLevel());
        materialCreation.setMaxLevel(materialCreationRequestDto.getMaxLevel());
        materialCreation.setReOrderLevel(materialCreationRequestDto.getReOrderLevel());
        materialCreation.setConditionOfMaterial(materialCreationRequestDto.getConditionOfMaterial());
        materialCreation.setLocator(materialCreationRequestDto.getLocator());
        materialCreation.setShelf(materialCreationRequestDto.getShelf());
        materialCreation.setRank(materialCreationRequestDto.getRank());
        materialCreation.setZone(materialCreationRequestDto.getZone());
        materialCreation.setBuilding(materialCreationRequestDto.getBuilding());
        materialCreation.setUploadImage(materialCreationRequestDto.getUploadImage());
        materialCreation.setShelfLife(materialCreationRequestDto.getShelfLife());
        materialCreationRepository.save(materialCreation);
        return mapToResponseDTO(materialCreation);
    }


    @Override
    public MaterialCreationResponseDto updateMaterial(String materialCode, MaterialCreationRequestDto materialCreationRequestDto) {

      MaterialCreation materialCreation = materialCreationRepository.findById(materialCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Materails not found for the provided MaterialCreation ID.")
                ));
        materialCreation.setMaterialName(materialCreationRequestDto.getMaterialName());
        materialCreation.setMaterialDescription(materialCreationRequestDto.getMaterialDescription());
        materialCreation.setModeOfProcurement(materialCreationRequestDto.getModeOfProcurement());
        materialCreation.setMaterialCategory(materialCreationRequestDto.getMaterialCategory());
        materialCreation.setMaterialSubCategory(materialCreationRequestDto.getMaterialSubCategory());
        materialCreation.setUom(materialCreationRequestDto.getUom());
        materialCreation.setEndOfLife(materialCreationRequestDto.getEndOfLife());
        materialCreation.setBookValue(materialCreationRequestDto.getBookValue());
        materialCreation.setDepricationRate(materialCreationRequestDto.getDepricationRate());
        materialCreation.setIndegeniousOrImported(materialCreationRequestDto.getIndegeniousOrImported());
        materialCreation.setMinLevel(materialCreationRequestDto.getMinLevel());
        materialCreation.setMaxLevel(materialCreationRequestDto.getMaxLevel());
        materialCreation.setReOrderLevel(materialCreationRequestDto.getReOrderLevel());
        materialCreation.setConditionOfMaterial(materialCreationRequestDto.getConditionOfMaterial());
        materialCreation.setLocator(materialCreationRequestDto.getLocator());
        materialCreation.setShelf(materialCreationRequestDto.getShelf());
        materialCreation.setRank(materialCreationRequestDto.getRank());
        materialCreation.setZone(materialCreationRequestDto.getZone());
        materialCreation.setBuilding(materialCreationRequestDto.getBuilding());
        materialCreation.setUploadImage(materialCreationRequestDto.getUploadImage());
        materialCreation.setShelfLife(materialCreationRequestDto.getShelfLife());

        materialCreationRepository.save(materialCreation);


        return mapToResponseDTO(materialCreation);
    }

    @Override
    public List<MaterialCreationResponseDto> getAllMaterials() {
        List<MaterialCreation> materialCreations = materialCreationRepository.findAll();
        return materialCreations.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public MaterialCreationResponseDto getMaterialById(String materialCode) {
       MaterialCreation materialCreation= materialCreationRepository.findById(materialCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "material not found for the provided material ID.")
                ));
        return mapToResponseDTO(materialCreation);
    }

    @Override
    public void deleteMaterial(String materialCode) {

        MaterialCreation materialCreation=materialCreationRepository.findById(materialCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Material not found for the provided ID."
                        )
                ));
        try {
            materialCreationRepository.delete(materialCreation);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the material."
                    ),
                    ex
            );
        }


    }
    private MaterialCreationResponseDto mapToResponseDTO(MaterialCreation materialCreation) {
        MaterialCreationResponseDto materialCreationResponseDto = new MaterialCreationResponseDto();
        materialCreationResponseDto.setMaterialCode(materialCreation.getMaterialCode());
        materialCreationResponseDto.setMaterialName(materialCreation.getMaterialName());
        materialCreationResponseDto.setMaterialDescription(materialCreation.getMaterialDescription());
        materialCreationResponseDto.setModeOfProcurement(materialCreation.getModeOfProcurement());
        materialCreationResponseDto.setMaterialCategory(materialCreation.getMaterialCategory());
        materialCreationResponseDto.setMaterialSubCategory(materialCreation.getMaterialSubCategory());
        materialCreationResponseDto.setUom(materialCreation.getUom());
        materialCreationResponseDto.setEndOfLife(materialCreation.getEndOfLife());
        materialCreationResponseDto.setBookValue(materialCreation.getBookValue());
        materialCreationResponseDto.setDepricationRate(materialCreation.getDepricationRate());
        materialCreationResponseDto.setIndegeniousOrImported(materialCreation.getIndegeniousOrImported());
        materialCreationResponseDto.setMinLevel(materialCreation.getMinLevel());
        materialCreationResponseDto.setMaxLevel(materialCreation.getMaxLevel());
        materialCreationResponseDto.setReOrderLevel(materialCreation.getReOrderLevel());
        materialCreationResponseDto.setConditionOfMaterial(materialCreation.getConditionOfMaterial());
        materialCreationResponseDto.setLocator(materialCreation.getLocator());
        materialCreationResponseDto.setShelf(materialCreation.getShelf());
        materialCreationResponseDto.setRank(materialCreation.getRank());
        materialCreationResponseDto.setZone(materialCreation.getZone());
        materialCreationResponseDto.setBuilding(materialCreation.getBuilding());
        materialCreationResponseDto.setUploadImage(materialCreation.getUploadImage());
        materialCreationResponseDto.setShelfLife(materialCreation.getShelfLife());
        materialCreationResponseDto.setCreatedBy(materialCreation.getCreatedBy());
        materialCreationResponseDto.setUpdatedBy(materialCreation.getUpdatedBy());
        materialCreationResponseDto.setCreatedDate(materialCreation.getCreatedDate());
        materialCreationResponseDto.setUpdatedDate(materialCreation.getUpdatedDate());
        return materialCreationResponseDto;
    }

}

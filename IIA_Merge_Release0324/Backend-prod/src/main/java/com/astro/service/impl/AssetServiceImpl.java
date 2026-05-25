package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.AssetMasterDto;
import com.astro.dto.workflow.InventoryModule.AssetRequestDTO;
import com.astro.dto.workflow.InventoryModule.AssetResponseDto;
import com.astro.entity.InventoryModule.Asset;
import com.astro.entity.ProcurementModule.IndentCreation;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.InventoryModule.AssetRepository;
import com.astro.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public AssetResponseDto createAsset(AssetRequestDTO assetDTO) {

        // Check if the indentorId already exists
        if (assetRepository.existsById(assetDTO.getAssetCode())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Asset code", "Asset code " + assetDTO.getAssetCode() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }
        Asset asset = new Asset();
        asset.setAssetCode(assetDTO.getAssetCode());
        asset.setMaterialCode(assetDTO.getMaterialCode());
        asset.setDescription(assetDTO.getDescription());
        asset.setUom(assetDTO.getUom());
        asset.setMakeNo(assetDTO.getMakeNo());
        asset.setModelNo(assetDTO.getModelNo());
        asset.setSerialNo(assetDTO.getSerialNo());
        asset.setComponentName(assetDTO.getComponentName());
        asset.setComponentCode(assetDTO.getComponentCode());
        asset.setQuantity(assetDTO.getQuantity());
        asset.setLocator(assetDTO.getLocator());
        asset.setTransactionHistory(assetDTO.getTransactionHistory());
        asset.setCurrentCondition(assetDTO.getCurrentCondition());
        assetRepository.save(asset);
        return mapToResponseDTO(asset);
    }



    public AssetResponseDto updateAsset(String assetCode, AssetRequestDTO assetDTO) {
        Asset asset = assetRepository.findById(assetCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Contingency Purchase not found for the provided asset ID.")
                ));

        asset.setAssetCode(assetDTO.getAssetCode());
        asset.setMaterialCode(assetDTO.getMaterialCode());
        asset.setDescription(assetDTO.getDescription());
        asset.setUom(assetDTO.getUom());
        asset.setMakeNo(assetDTO.getMakeNo());
        asset.setModelNo(assetDTO.getModelNo());
        asset.setSerialNo(assetDTO.getSerialNo());
        asset.setComponentName(assetDTO.getComponentName());
        asset.setComponentCode(assetDTO.getComponentCode());
        asset.setQuantity(assetDTO.getQuantity());
        asset.setLocator(assetDTO.getLocator());
        asset.setTransactionHistory(assetDTO.getTransactionHistory());
        asset.setCurrentCondition(assetDTO.getCurrentCondition());
        assetRepository.save(asset);
        return mapToResponseDTO(asset);
    }

    public List<AssetResponseDto> getAllAssets() {

        List<Asset> asset = assetRepository.findAll();
        return asset.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public AssetResponseDto getAssetById(String assetCode) {
        Asset asset = assetRepository.findById(assetCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Contingency Purchase not found for the provided asset ID.")
                ));
        return mapToResponseDTO(asset);
    }

    public void deleteAsset(String assetCode) {

        Asset asset = assetRepository.findById(assetCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Asset not found for the provided ID."
                        )
                ));
        try {
            assetRepository.delete(asset);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the asset."
                    ),
                    ex
            );
        }

    }

    private AssetResponseDto mapToResponseDTO(Asset asset) {
        AssetResponseDto assetResponseDto =new  AssetResponseDto();
        assetResponseDto.setAssetCode(asset.getAssetCode());
        assetResponseDto.setMaterialCode(asset.getMaterialCode());
        assetResponseDto.setDescription(asset.getDescription());
        assetResponseDto.setUom(asset.getUom());
        assetResponseDto.setMakeNo(asset.getMakeNo());
        assetResponseDto.setModelNo(asset.getModelNo());
        assetResponseDto.setSerialNo(asset.getSerialNo());
        assetResponseDto.setComponentName(asset.getComponentName());
        assetResponseDto.setComponentCode(asset.getComponentCode());
        assetResponseDto.setQuantity(asset.getQuantity());
        assetResponseDto.setLocator(asset.getLocator());
        assetResponseDto.setTransactionHistory(asset.getTransactionHistory());
        assetResponseDto.setCurrentCondition(asset.getCurrentCondition());
        assetResponseDto.setUpdatedBy(asset.getUpdatedBy());
        assetResponseDto.setCreatedBy(asset.getCreatedBy());
        assetResponseDto.setCreatedDate(asset.getCreatedDate());
        assetResponseDto.setUpdatedDate(asset.getUpdatedDate());
        return assetResponseDto;


    }

}

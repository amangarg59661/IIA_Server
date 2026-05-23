package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.MaterialDisposalRequestDTO;
import com.astro.dto.workflow.InventoryModule.MaterialDisposalResponseDTO;

import com.astro.entity.InventoryModule.MaterialDisposal;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.InventoryModule.MaterialDisposalRepository;
import com.astro.service.MaterialDisposalService;
import com.astro.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class MaterialDisposalServiceImpl implements MaterialDisposalService {
    @Autowired
    private MaterialDisposalRepository materialDisposalRepository;


    @Override
    public MaterialDisposalResponseDTO createMaterialDisposal(MaterialDisposalRequestDTO materialDisposalRequestDTO, String saleNoteFileName) {

        // Check if the indentorId already exists
        if (materialDisposalRepository.existsById(materialDisposalRequestDTO.getMaterialDisposalCode())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Material Disposal Code", "MaterialDisposal Code " + materialDisposalRequestDTO.getMaterialDisposalCode() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }

        MaterialDisposal materialDisposal = new MaterialDisposal();
        materialDisposal.setMaterialDisposalCode(materialDisposalRequestDTO.getMaterialDisposalCode());
        materialDisposal.setDisposalCategory(materialDisposalRequestDTO.getDisposalCategory());
        materialDisposal.setDisposalMode(materialDisposalRequestDTO.getDisposalMode());
        materialDisposal.setVendorDetails(materialDisposalRequestDTO.getVendorDetails());
        String DisposalDate = materialDisposalRequestDTO.getDisposalDate();
        materialDisposal.setDisposalDate(CommonUtils.convertStringToDateObject(DisposalDate));
        materialDisposal.setCurrentBookValue(materialDisposalRequestDTO.getCurrentBookValue());
        materialDisposal.setEditReserveValue(materialDisposalRequestDTO.getEditReserveValue());
        materialDisposal.setFinalBidValue(materialDisposalRequestDTO.getFinalBidValue());

        handleFileUpload(materialDisposal, materialDisposalRequestDTO.getSaleNote(),
                materialDisposal::setSaleNote);
        materialDisposal.setSaleNoteFileName(saleNoteFileName);
        materialDisposal.setEditQuantity(materialDisposalRequestDTO.getEditQuantity());
        materialDisposal.setEditValueMaterials(materialDisposalRequestDTO.getEditValueMaterials());
        materialDisposal = materialDisposalRepository.save(materialDisposal);

        return mapToResponseDTO(materialDisposal);
    }

    private MaterialDisposalResponseDTO mapToResponseDTO(MaterialDisposal materialDisposal) {
        MaterialDisposalResponseDTO dto = new MaterialDisposalResponseDTO();
        dto.setMaterialDisposalCode(materialDisposal.getMaterialDisposalCode());
        dto.setDisposalCategory(materialDisposal.getDisposalCategory());
        dto.setDisposalMode(materialDisposal.getDisposalMode());
        dto.setVendorDetails(materialDisposal.getVendorDetails());
        LocalDate DisposalDate = materialDisposal.getDisposalDate();
        dto.setDisposalDate(CommonUtils.convertDateToString(DisposalDate));
        dto.setCurrentBookValue(materialDisposal.getCurrentBookValue());
        dto.setEditReserveValue(materialDisposal.getEditReserveValue());
        dto.setFinalBidValue(materialDisposal.getFinalBidValue());
        dto.setSaleNoteFileName(materialDisposal.getSaleNoteFileName());
        dto.setEditQuantity(materialDisposal.getEditQuantity());
        dto.setEditValueMaterials(materialDisposal.getEditValueMaterials());
        dto.setCreatedBy(materialDisposal.getCreatedBy());
        dto.setUpdatedBy(materialDisposal.getUpdatedBy());
        dto.setCreatedDate(materialDisposal.getCreatedDate());
        dto.setUpdatedDate(materialDisposal.getUpdatedDate());
        return dto;
    }

    @Override
    public MaterialDisposalResponseDTO updateMaterialDisposal(String materialDisposalCode, MaterialDisposalRequestDTO materialDisposalRequestDTO,String saleNoteFileName) {
       MaterialDisposal materialDisposal = materialDisposalRepository.findById(materialDisposalCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Materail Disposal not found for the provided Material Disposal Code.")
                ));
        materialDisposal.setMaterialDisposalCode(materialDisposalRequestDTO.getMaterialDisposalCode());
        materialDisposal.setDisposalCategory(materialDisposalRequestDTO.getDisposalCategory());
        materialDisposal.setDisposalMode(materialDisposalRequestDTO.getDisposalMode());
        materialDisposal.setVendorDetails(materialDisposalRequestDTO.getVendorDetails());
        String DisposalDate = materialDisposalRequestDTO.getDisposalDate();
        materialDisposal.setDisposalDate(CommonUtils.convertStringToDateObject(DisposalDate));
        materialDisposal.setCurrentBookValue(materialDisposalRequestDTO.getCurrentBookValue());
        materialDisposal.setEditReserveValue(materialDisposalRequestDTO.getEditReserveValue());
        materialDisposal.setFinalBidValue(materialDisposalRequestDTO.getFinalBidValue());

        handleFileUpload(materialDisposal, materialDisposalRequestDTO.getSaleNote(),
                materialDisposal::setSaleNote);
        //   materialDisposal.setSaleNoteFileName(materialDisposalRequestDTO.getSaleNoteFileName());
        materialDisposal.setEditQuantity(materialDisposalRequestDTO.getEditQuantity());
        materialDisposal.setEditValueMaterials(materialDisposalRequestDTO.getEditValueMaterials());
        materialDisposal.setSaleNoteFileName(saleNoteFileName);
        materialDisposal = materialDisposalRepository.save(materialDisposal);

        return mapToResponseDTO(materialDisposal);

    }

    @Override
    public List<MaterialDisposalResponseDTO> getAllMaterialDisposals() {
        return materialDisposalRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaterialDisposalResponseDTO getMaterialDisposalById(String materialDisposalCode) {
       MaterialDisposal materialDisposal=materialDisposalRepository.findById(materialDisposalCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "material Disposal not found for the provided Material Disposal Code.")
                ));
        return mapToResponseDTO(materialDisposal);
    }

    @Override
    public void deleteMaterialDisposal(String materialDisposalCode) {
        MaterialDisposal materialDisposal=materialDisposalRepository.findById(materialDisposalCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Material Disposal not found for the provided material disposal code."
                        )
                ));
        try {
            materialDisposalRepository.delete(materialDisposal);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the material Disposal."
                    ),
                    ex
            );
        }
    }

    public void handleFileUpload(MaterialDisposal materialDisposal, MultipartFile file, Consumer<byte[]> fileSetter) {
        if (file != null) {
            try (InputStream inputStream = file.getInputStream()) {
                byte[] fileBytes = inputStream.readAllBytes();
                fileSetter.accept(fileBytes); // Set file content (byte[])

            } catch (IOException e) {
                throw new InvalidInputException(new ErrorDetails(500, 3, "File Processing Error",
                        "Error while processing the uploaded file. Please try again."));
            }
        } else {
            fileSetter.accept(null);  // Handle gracefully if no file is uploaded
        }
    }
}

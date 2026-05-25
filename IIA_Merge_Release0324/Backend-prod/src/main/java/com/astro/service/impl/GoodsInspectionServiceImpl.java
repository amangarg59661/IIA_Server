package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.GoodsInspectionRequestDto;
import com.astro.dto.workflow.InventoryModule.GoodsInspectionResponseDto;
import com.astro.entity.InventoryModule.Asset;
import com.astro.entity.InventoryModule.GoodsInspection;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.InventoryModule.GoodsInspectionRepository;
import com.astro.service.GoodsInspectionService;
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
public class GoodsInspectionServiceImpl implements GoodsInspectionService {

    @Autowired
    private GoodsInspectionRepository repository;



    @Override
    public GoodsInspectionResponseDto createGoodsInspection(GoodsInspectionRequestDto goodsInspectionDTO, String uploadInstallationReportFileName) {
        // Check if the indentorId already exists
        if (repository.existsById(goodsInspectionDTO.getGoodsInspectionNo())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Goods Inspection ID", "goods Inspection ID " + goodsInspectionDTO.getGoodsInspectionNo() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }

         GoodsInspection goodsInspection = new GoodsInspection();
         goodsInspection.setGoodsInspectionNo(goodsInspectionDTO.getGoodsInspectionNo());
         goodsInspection.setGriId(goodsInspectionDTO.getGriId());
        String InstallationDate=goodsInspectionDTO.getInstallationDate();
        goodsInspection.setInstallationDate(CommonUtils.convertStringToDateObject(InstallationDate));
        String CommissioningDate= goodsInspectionDTO.getCommissioningDate();
        goodsInspection.setCommissioningDate(CommonUtils.convertStringToDateObject(CommissioningDate));
        //goodsInspection.setUploadInstallationReport(goodsInspectionDTO.getUploadInstallationReport());
        handleFileUpload(goodsInspection, goodsInspectionDTO.getUploadInstallationReport(),
                goodsInspection::setUploadInstallationReport);
        goodsInspection.setAcceptedQuantity(goodsInspectionDTO.getAcceptedQuantity());
        goodsInspection.setRejectedQuantity(goodsInspectionDTO.getRejectedQuantity());
        goodsInspection.setGoodsReturnPermamentOrReplacement(goodsInspectionDTO.getGoodsReturnPermamentOrReplacement());
        goodsInspection.setGoodsReturnFullOrPartial(goodsInspectionDTO.getGoodsReturnFullOrPartial());
        goodsInspection.setGoodsReturnReason(goodsInspectionDTO.getGoodsReturnReason());
        goodsInspection.setMaterialRejectionAdviceSent(goodsInspectionDTO.getMaterialRejectionAdviceSent());
        goodsInspection.setPoAmendmentNotified(goodsInspectionDTO.getPoAmendmentNotified());
        goodsInspection.setUploadInstallationReportFileName(uploadInstallationReportFileName);
        GoodsInspection saved = repository.save(goodsInspection);
        return mapToResponseDTO(saved);
    }

    private GoodsInspectionResponseDto mapToResponseDTO(GoodsInspection saved) {
        GoodsInspectionResponseDto  goodsInspectionResponseDto = new GoodsInspectionResponseDto();

        goodsInspectionResponseDto.setGoodsInspectionNo(saved.getGoodsInspectionNo());
        goodsInspectionResponseDto.setGriId(saved.getGriId());
      LocalDate InstallationDate=saved.getInstallationDate();
        goodsInspectionResponseDto.setInstallationDate(CommonUtils.convertDateToString(InstallationDate));
       LocalDate CommissioningDate= saved.getCommissioningDate();
        goodsInspectionResponseDto.setCommissioningDate(CommonUtils.convertDateToString(CommissioningDate));
       //goodsInspectionResponseDto.setUploadInstallationReport(saved.getUploadInstallationReport());
        goodsInspectionResponseDto.setAcceptedQuantity(saved.getAcceptedQuantity());
        goodsInspectionResponseDto.setRejectedQuantity(saved.getRejectedQuantity());
        goodsInspectionResponseDto.setGoodsReturnPermamentOrReplacement(saved.getGoodsReturnPermamentOrReplacement());
        goodsInspectionResponseDto.setGoodsReturnFullOrPartial(saved.getGoodsReturnFullOrPartial());
        goodsInspectionResponseDto.setGoodsReturnReason(saved.getGoodsReturnReason());
        goodsInspectionResponseDto.setMaterialRejectionAdviceSent(saved.getMaterialRejectionAdviceSent());
        goodsInspectionResponseDto.setPoAmendmentNotified(saved.getPoAmendmentNotified());
        goodsInspectionResponseDto.setUpdatedBy(saved.getUpdatedBy());
        goodsInspectionResponseDto.setCreatedBy(saved.getCreatedBy());
        goodsInspectionResponseDto.setCreatedDate(saved.getCreatedDate());
        goodsInspectionResponseDto.setUpdatedDate(saved.getUpdatedDate());
        goodsInspectionResponseDto.setUploadInstallationReportFileName(saved.getUploadInstallationReportFileName());
        return goodsInspectionResponseDto;
    }

    @Override
    public GoodsInspectionResponseDto updateGoodsInspection(String goodsInspectionNo, GoodsInspectionRequestDto goodsInspectionDTO, String uploadInstallationReportFileName) {
        GoodsInspection existing = repository.findById(goodsInspectionNo)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Goods Inspection not found for the provided goodsInspectionNo.")
                ));

        String InstallationDate=goodsInspectionDTO.getInstallationDate();
        existing.setInstallationDate(CommonUtils.convertStringToDateObject(InstallationDate));
        existing.setGriId(goodsInspectionDTO.getGriId());
        String CommissioningDate= goodsInspectionDTO.getCommissioningDate();
        existing.setCommissioningDate(CommonUtils.convertStringToDateObject(CommissioningDate));
       // existing.setUploadInstallationReport(goodsInspectionDTO.getUploadInstallationReport());
        handleFileUpload(existing, goodsInspectionDTO.getUploadInstallationReport(),
                existing::setUploadInstallationReport);
        existing.setAcceptedQuantity(goodsInspectionDTO.getAcceptedQuantity());
        existing.setRejectedQuantity(goodsInspectionDTO.getRejectedQuantity());
        existing.setGoodsReturnPermamentOrReplacement(goodsInspectionDTO.getGoodsReturnPermamentOrReplacement());
        existing.setGoodsReturnFullOrPartial(goodsInspectionDTO.getGoodsReturnFullOrPartial());
        existing.setGoodsReturnReason(goodsInspectionDTO.getGoodsReturnReason());
        existing.setPoAmendmentNotified(goodsInspectionDTO.getPoAmendmentNotified());
        existing.setMaterialRejectionAdviceSent(goodsInspectionDTO.getMaterialRejectionAdviceSent());
        existing.setUploadInstallationReportFileName(uploadInstallationReportFileName);
        GoodsInspection updated = repository.save(existing);
        return mapToResponseDTO(updated);
    }

    @Override
    public List<GoodsInspectionResponseDto> getAllGoodsInspections() {

        List<GoodsInspection> goodsInspections = repository.findAll();
        return goodsInspections.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public GoodsInspectionResponseDto getGoodsInspectionById(String goodsInspectionNo) {
       GoodsInspection inspection = repository.findById(goodsInspectionNo)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Goods Inspection not found for the provided goodsInspectionNo.")
                ));
        return mapToResponseDTO(inspection);
    }
    @Override
    public void deleteGoodsInspection(String goodsInspectionNo) {

     GoodsInspection inspection =repository.findById(goodsInspectionNo)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                " Goods Inspection not found for the provided ID."
                        )
                ));
        try {
           repository.delete(inspection);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the  Goods Inspection."
                    ),
                    ex
            );
        }
    }

    public void handleFileUpload(GoodsInspection goodsInspection, MultipartFile file, Consumer<byte[]> fileSetter) {
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

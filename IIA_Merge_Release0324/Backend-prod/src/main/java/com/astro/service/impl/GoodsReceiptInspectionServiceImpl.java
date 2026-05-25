package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.GoodsInspectionResponseDto;
import com.astro.dto.workflow.InventoryModule.GoodsReceiptInspectionRequestDto;
import com.astro.dto.workflow.InventoryModule.GoodsReceiptInspectionResponseDto;
import com.astro.entity.InventoryModule.GoodsReceiptInspection;
import com.astro.entity.InventoryModule.GoodsReturn;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.InventoryModule.GoodsReceiptInspectionRepository;
import com.astro.service.GoodsReceiptInspectionService;
import com.astro.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsReceiptInspectionServiceImpl implements GoodsReceiptInspectionService {

    @Autowired
    private GoodsReceiptInspectionRepository GRIrepository;

    @Override
    public GoodsReceiptInspectionResponseDto createGoodsReceiptInspection(GoodsReceiptInspectionRequestDto dto) {

        // Check if the indentorId already exists
        if (GRIrepository.existsById(dto.getGprId())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate GRI ID", "GRI ID " + dto.getGprId() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }

        GoodsReceiptInspection entity = new GoodsReceiptInspection();

        entity.setGriId(dto.getGprId());
        String InstallationDate = dto.getInstallationDate();
        entity.setInstallationDate(CommonUtils.convertStringToDateObject(InstallationDate));
        String CommissioningDate = dto.getCommissioningDate();
        entity.setCommissioningDate(CommonUtils.convertStringToDateObject(CommissioningDate));
        entity.setAssetCode(dto.getAssetCode());
        entity.setAdditionalMaterialDescription(dto.getAdditionalMaterialDescription());
        entity.setLocator(dto.getLocator());
        entity.setPrintLabelOption(dto.isPrintLabelOption());
        entity.setDepreciationRate(dto.getDepreciationRate());
        entity.setAttachComponentPopup(dto.getAttachComponentPopup());
        entity.setBookValue(dto.getBookValue());
         GRIrepository.save(entity);
         return mapToResponseDTO(entity);
    }



    @Override
    public GoodsReceiptInspectionResponseDto updateGoodsReceiptInspection(String receiptInspectionNo, GoodsReceiptInspectionRequestDto dto) {
        GoodsReceiptInspection entity = GRIrepository.findById(receiptInspectionNo)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Goods Receipt not found for the provided asset ID.")
                ));
        //entity.setReceiptInspectionNo(dto.getReceiptInspectionNo());
        entity.setGriId(dto.getGprId());
        String InstallationDate = dto.getInstallationDate();
        entity.setInstallationDate(CommonUtils.convertStringToDateObject(InstallationDate));
        String CommissioningDate = dto.getCommissioningDate();
        entity.setCommissioningDate(CommonUtils.convertStringToDateObject(CommissioningDate));
        entity.setAssetCode(dto.getAssetCode());
        entity.setAdditionalMaterialDescription(dto.getAdditionalMaterialDescription());
        entity.setLocator(dto.getLocator());
        entity.setPrintLabelOption(dto.isPrintLabelOption());
        entity.setDepreciationRate(dto.getDepreciationRate());
        entity.setBookValue(dto.getBookValue());
        entity.setAttachComponentPopup(dto.getAttachComponentPopup());
        GRIrepository.save(entity);
        return mapToResponseDTO(entity);
    }

    @Override
    public List<GoodsReceiptInspectionResponseDto> getAllGoodsReceiptInspections() {

        List<GoodsReceiptInspection> goods= GRIrepository.findAll();
        return goods.stream().map(this::mapToResponseDTO).collect(Collectors.toList());

    }

    @Override
    public GoodsReceiptInspectionResponseDto getGoodsReceiptInspectionById(String receiptInspectionNo) {
       GoodsReceiptInspection goods= GRIrepository.findById(receiptInspectionNo)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Goods Receipt inspection not found for the provided asset ID.")
                ));
        return mapToResponseDTO(goods);
    }

    @Override
    public void deleteGoodsReceiptInspection(String receiptInspectionNo) {
       GoodsReceiptInspection  goods=GRIrepository.findById(receiptInspectionNo)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                " Goods Receipt Inspection not found for the provided ID."
                        )
                ));
        try {
            GRIrepository.delete(goods);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the  Goods ReceiptInspection."
                    ),
                    ex
            );
        }
    }

    private GoodsReceiptInspectionResponseDto mapToResponseDTO(GoodsReceiptInspection entity) {
        GoodsReceiptInspectionResponseDto goodsReceiptInspectionResponseDto = new GoodsReceiptInspectionResponseDto();

        goodsReceiptInspectionResponseDto.setGriId(entity.getGriId());
        LocalDate InstallationDate = entity.getInstallationDate();
        goodsReceiptInspectionResponseDto.setInstallationDate(CommonUtils.convertDateToString(InstallationDate));
        LocalDate CommissioningDate = entity.getCommissioningDate();
        goodsReceiptInspectionResponseDto.setCommissioningDate(CommonUtils.convertDateToString(CommissioningDate));
        goodsReceiptInspectionResponseDto.setAssetCode(entity.getAssetCode());
        goodsReceiptInspectionResponseDto.setAdditionalMaterialDescription(entity.getAdditionalMaterialDescription());
        goodsReceiptInspectionResponseDto.setLocator(entity.getLocator());
        goodsReceiptInspectionResponseDto.setPrintLabelOption(entity.isPrintLabelOption());
        goodsReceiptInspectionResponseDto.setDepreciationRate(entity.getDepreciationRate());
        goodsReceiptInspectionResponseDto.setAttachComponentPopup(entity.getAttachComponentPopup());
        goodsReceiptInspectionResponseDto.setUpdatedBy(entity.getUpdatedBy());
        goodsReceiptInspectionResponseDto.setCreatedBy(entity.getCreatedBy());
        goodsReceiptInspectionResponseDto.setBookValue(entity.getBookValue());
        goodsReceiptInspectionResponseDto.setCreatedDate(entity.getCreatedDate());
        goodsReceiptInspectionResponseDto.setUpdatedDate(entity.getUpdatedDate());
        return goodsReceiptInspectionResponseDto;
    }

}

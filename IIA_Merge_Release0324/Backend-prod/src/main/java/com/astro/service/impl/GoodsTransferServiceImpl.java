package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.GoodsTransferRequestDto;
import com.astro.dto.workflow.InventoryModule.GoodsTransferResponseDto;
import com.astro.entity.InventoryModule.GoodsReturn;
import com.astro.entity.InventoryModule.GoodsTransfer;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.InventoryModule.GoodsTransferRepository;
import com.astro.service.GoodsTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsTransferServiceImpl implements GoodsTransferService {
    @Autowired
    private GoodsTransferRepository goodsTransferRepository;
    @Override
    public GoodsTransferResponseDto createGoodsTransfer(GoodsTransferRequestDto goodsTransferRequestDto) {
        // Check if the indentorId already exists
        if (goodsTransferRepository.existsById(goodsTransferRequestDto.getGoodsTransferID())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Goods Transfer ID", "Goods Transfer ID " + goodsTransferRequestDto.getGoodsTransferID() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }

        GoodsTransfer goodsTransfer = new GoodsTransfer();

        goodsTransfer.setGoodsTransferID(goodsTransferRequestDto.getGoodsTransferID());
        goodsTransfer.setConsignorDetails(goodsTransferRequestDto.getConsignorDetails());
        goodsTransfer.setConsigneeDetails(goodsTransferRequestDto.getConsigneeDetails());
        goodsTransfer.setFieldStationName(goodsTransferRequestDto.getFieldStationName());
        goodsTransfer.setMaterialCode(goodsTransferRequestDto.getMaterialCode());
        goodsTransfer.setUom(goodsTransferRequestDto.getUom());
        goodsTransfer.setQuantity(goodsTransferRequestDto.getQuantity());
        goodsTransfer.setLocator(goodsTransferRequestDto.getLocator());
        goodsTransfer.setNote(goodsTransferRequestDto.getNote());
        goodsTransferRepository.save(goodsTransfer);
        return mapToResponseDTO(goodsTransfer);
    }



    @Override
    public GoodsTransferResponseDto updateGoodsTransfer(String goodsTransferID, GoodsTransferRequestDto goodsTransferRequestDto) {
        GoodsTransfer existing = goodsTransferRepository.findById(goodsTransferID)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Goods transfer not found for the provided goods transfer ID.")
                ));
        existing.setGoodsTransferID(goodsTransferRequestDto.getGoodsTransferID());
        existing.setConsignorDetails(goodsTransferRequestDto.getConsignorDetails());
        existing.setConsigneeDetails(goodsTransferRequestDto.getConsigneeDetails());
        existing.setFieldStationName(goodsTransferRequestDto.getFieldStationName());
        existing.setMaterialCode(goodsTransferRequestDto.getMaterialCode());
        existing.setUom(goodsTransferRequestDto.getUom());
        existing.setQuantity(goodsTransferRequestDto.getQuantity());
        existing.setLocator(goodsTransferRequestDto.getLocator());
        existing.setNote(goodsTransferRequestDto.getNote());
        goodsTransferRepository.save(existing);
        return mapToResponseDTO(existing);
    }

    @Override
    public List<GoodsTransferResponseDto> getAllGoodsTransfer() {
        List<GoodsTransfer> goodsTransfers = goodsTransferRepository.findAll();
        return goodsTransfers.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public GoodsTransferResponseDto getGoodsTransferById(String goodsTransferID) {
        GoodsTransfer goodsTransfer= goodsTransferRepository.findById(goodsTransferID)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Goods transfer not found for the provided  ID.")
                ));
        return mapToResponseDTO(goodsTransfer);
    }

    @Override
    public void deleteGoodsTransfer(String goodsTransferID) {
       GoodsTransfer goodsTransfer=goodsTransferRepository.findById(goodsTransferID)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                " Goods transfer not found for the provided ID."
                        )
                ));
        try {
            goodsTransferRepository.delete(goodsTransfer);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the  Goods transfer."
                    ),
                    ex
            );
        }

    }

    private GoodsTransferResponseDto mapToResponseDTO(GoodsTransfer goodsTransfer) {
        GoodsTransferResponseDto goodsTransferResponseDto = new GoodsTransferResponseDto();
        goodsTransferResponseDto.setGoodsTransferID(goodsTransfer.getGoodsTransferID());
        goodsTransferResponseDto.setConsignorDetails(goodsTransfer.getConsignorDetails());
        goodsTransferResponseDto.setConsigneeDetails(goodsTransfer.getConsigneeDetails());
        goodsTransferResponseDto.setFieldStationName(goodsTransfer.getFieldStationName());
        goodsTransferResponseDto.setMaterialCode(goodsTransfer.getMaterialCode());
        goodsTransferResponseDto.setUom(goodsTransfer.getUom());
        goodsTransferResponseDto.setQuantity(goodsTransfer.getQuantity());
        goodsTransferResponseDto.setLocator(goodsTransfer.getLocator());
        goodsTransferResponseDto.setNote(goodsTransfer.getNote());
        goodsTransferResponseDto.setUpdatedBy(goodsTransfer.getUpdatedBy());
        goodsTransferResponseDto.setCreatedBy(goodsTransfer.getCreatedBy());
        goodsTransferResponseDto.setCreatedDate(goodsTransfer.getCreatedDate());
        goodsTransferResponseDto.setUpdatedDate(goodsTransfer.getUpdatedDate());
        return goodsTransferResponseDto;

    }
}

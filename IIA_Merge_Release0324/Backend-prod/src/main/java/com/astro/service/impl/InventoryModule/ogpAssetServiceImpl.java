package com.astro.service.impl.InventoryModule;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.InventoryModule.AssetDisposalDetailDto;
import com.astro.dto.workflow.InventoryModule.AssetDisposalDto;
import com.astro.dto.workflow.InventoryModule.AssetsAuctionDto;
import com.astro.dto.workflow.InventoryModule.AutionAssetsDisposalsDto;
import com.astro.entity.InventoryModule.OgpAssetDisposal;
import com.astro.entity.InventoryModule.OgpAssetDisposalDetail;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.InventoryModule.OgpAssetDisposalDetailRepository;
import com.astro.repository.InventoryModule.OgpAssetDisposalRepository;
import com.astro.service.InventoryModule.ogpAssetService;
import com.astro.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.IdentifierSequences;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ogpAssetServiceImpl implements ogpAssetService {
    @Autowired
    private OgpAssetDisposalRepository ogpAssetDisposalRepository;
    @Autowired
    private OgpAssetDisposalDetailRepository ogpAssetDisposalDetailRepository;


    @Override
    @Transactional
    public String saveAssetDisposalOgp(AssetsAuctionDto request) {

        Integer id = Integer.valueOf(request.getAuctionId().split("/")[1]);
        OgpAssetDisposal ogpMaster = new OgpAssetDisposal();
        ogpMaster.setAuctionId(id);
        ogpMaster.setAuctionCode(request.getAuctionCode());
        ogpMaster.setAuctionDate(CommonUtils.convertStringToDateObject(request.getAuctionDate()));
        ogpMaster.setReservePrice(request.getReservePrice());
        ogpMaster.setAuctionPrice(request.getAuctionPrice());
        ogpMaster.setVendorName(request.getVendorName());
        ogpMaster.setStatus("Awaiting For Approval");

        ogpMaster.setCreatedBy(request.getCreatedBy());
        ogpMaster.setCreateDate(LocalDateTime.now());

        ogpMaster = ogpAssetDisposalRepository.save(ogpMaster);

        // 2. Save details
        List<OgpAssetDisposalDetail> detailList = new ArrayList<>();
        if (request.getAssets() != null) {
            for (AutionAssetsDisposalsDto assetDto : request.getAssets()) {
                OgpAssetDisposalDetail detail = new OgpAssetDisposalDetail();
                detail.setDisposal(ogpMaster); // foreign key to parent table
                detail.setDisposalId(assetDto.getDisposalId());
                detail.setAssetId(assetDto.getAssetId());
                detail.setAssetCode(assetDto.getAssetCode());
                detail.setSerialNo(assetDto.getSerialNo());
                detail.setAssetDesc(assetDto.getAssetDesc());
                detail.setDisposalQuantity(assetDto.getDisposalQuantity());
                detail.setLocatorId(assetDto.getLocatorId());
                detail.setBookValue(assetDto.getBookValue());
                detail.setDepriciationRate(assetDto.getDepriciationRate());
                detail.setUnitPrice(assetDto.getUnitPrice());
                detail.setCustodianId(assetDto.getCustodianId());
                detail.setPoValue(assetDto.getPoValue());
                detail.setReasonForDisposal(assetDto.getReasonForDisposal());
                detail.setDisposalDate(CommonUtils.convertStringToDateObject(assetDto.getDisposalDate()));
                detail.setLocationId(assetDto.getLocationId());
                detail.setStatus(assetDto.getStatus());

                detailList.add(detail);
            }
            ogpAssetDisposalDetailRepository.saveAll(detailList);
        }

        return "INV/" + ogpMaster.getDisposalOgpId();
    }



    public List<AssetsAuctionDto> getPendingApprovals() {
        List<OgpAssetDisposal> pendingList = ogpAssetDisposalRepository.findByStatus("Awaiting For Approval");

        return pendingList.stream().map(disposal -> {

            AssetsAuctionDto auctionDto = new AssetsAuctionDto();
            auctionDto.setDisposalOgpId(disposal.getDisposalOgpId());
            auctionDto.setAuctionId(String.valueOf(disposal.getAuctionId()));
            auctionDto.setAuctionCode(disposal.getAuctionCode());
            auctionDto.setAuctionDate(CommonUtils.convertDateToString(disposal.getAuctionDate()));
            auctionDto.setVendorName(disposal.getVendorName());
            auctionDto.setReservePrice(disposal.getReservePrice());
            auctionDto.setAuctionPrice(disposal.getAuctionPrice());


            List<OgpAssetDisposalDetail> details = ogpAssetDisposalDetailRepository.findByDisposalOgpId(disposal.getDisposalOgpId());
            List<AutionAssetsDisposalsDto> assetDtos = details.stream().map(detail -> {
                AutionAssetsDisposalsDto dto = new AutionAssetsDisposalsDto();
                dto.setDisposalDetailId(detail.getOgpDisposalDetailId());
                dto.setDisposalId(detail.getDisposalId());
                dto.setAssetId(detail.getAssetId());
                dto.setSerialNo(detail.getSerialNo());
                dto.setAssetCode(detail.getAssetCode());
                dto.setAssetDesc(detail.getAssetDesc());
                dto.setDisposalQuantity(detail.getDisposalQuantity());
                dto.setLocatorId(detail.getLocatorId());
                dto.setBookValue(detail.getBookValue());
                dto.setDepriciationRate(detail.getDepriciationRate());
                dto.setUnitPrice(detail.getUnitPrice());
                dto.setCustodianId(detail.getCustodianId());
                dto.setPoValue(detail.getPoValue());
                dto.setReasonForDisposal(detail.getReasonForDisposal());
                dto.setDisposalDate(CommonUtils.convertDateToString(detail.getDisposalDate()));
                dto.setLocationId(detail.getLocationId());
                dto.setStatus(disposal.getStatus());
                return dto;
            }).collect(Collectors.toList());

            auctionDto.setAssets(assetDtos);

            return auctionDto;
        }).collect(Collectors.toList());
    }

    public String approveOgpAssetDisposal(Integer disposalOgpId) {
        OgpAssetDisposal disposal = ogpAssetDisposalRepository.findById(disposalOgpId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Asset disposal not found for the provided process number.")));

        disposal.setStatus("APPROVED");
        ogpAssetDisposalRepository.save(disposal);
        return "Asset Disposal OGP approved successfully.";
    }

    public String rejectOgpAssetDisposal(Integer disposalOgpId) {
        OgpAssetDisposal disposal = ogpAssetDisposalRepository.findById(disposalOgpId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Asset disposal not found for the provided process number.")));

        disposal.setStatus("REJECTED");

        ogpAssetDisposalRepository.save(disposal);
        return "Asset Disposal OGP rejected successfully.";
    }
}

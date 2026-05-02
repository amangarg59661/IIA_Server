package com.astro.service.InventoryModule;

import java.util.List;

import com.astro.dto.workflow.AssetDataForGtDto;
import com.astro.dto.workflow.AssetSearchResponseDto;
import com.astro.dto.workflow.InventoryModule.*;
import com.astro.dto.workflow.InventoryModule.asset.AssetMasterReportDto;
import com.astro.dto.workflow.InventoryModule.asset.AssetOhqDisposalDto;
import com.astro.entity.InventoryModule.AssetDisposalDetailEntity;
import com.astro.entity.InventoryModule.OhqConsumableStoreStockEntity;
import com.astro.entity.InventoryModule.OhqMasterConsumableEntity;
import com.astro.entity.InventoryModule.OhqMasterEntity;

public interface AssetMasterService {
    String saveAssetMaster(AssetMasterDto request);
    String updateAssetMaster(AssetMasterDto request);
    public String saveAssetDisposal(AssetDisposalDto request);
    AssetMasterDto getAssetDetails(Integer assetId);
    public List<AssetMasterReportDto> getAssetReport();
    List<Integer> getAllAssetIds();
    public List<OhqMasterEntity> getAssetOhqList();
    public List<AssetOhqDetailsDto> getAssetOhqDetails();
    public List<OhqMasterConsumableEntity> getAssetOhqConsumableList();
    public List<OhqConsumableStoreStockEntity> getStoreStockOhqConsumableList();
    public List<AssetOhqDisposalDto> getAllAssetsForDisposal();
    public List<AssetDisposalDto> getAllAssetDisposalAwaitingForApproval();
    public void approveDisposal(String disposalIdStr);
    public void rejectDisposal(String disposalIdStr);
    public AssetDisposalDto getAssetDisposalById(String disposalIdStr);
    public String updateAssetDisposal(AssetDisposalDto request);
    public List<AssetDisposalDto> getAllApprovedAssetDisposalReport();
    public List<AssetDisposalReportDto> getAssetDisposalReport(String startDate, String endDate);
    public String disposeMultipleAssets(DisposeAssetRequest request);
    public AssetsAuctionDto searchByAuctionId(String auctionId);
    public List<Integer> getPendingAuctionIds();
    public List<AssetDataForGtDto> getAllFullAssetsForStorePerson();
    List<AssetSearchResponseDto> searchAssetsByKeyword(String keyword);
    public List<AssetFullResponseDto> getFullAssetDetails( Integer assetId, String assetCode, String custodianId, Integer locatorId);
    public String updateAssetSerials(AssetSerialUpdateRequestDto req);

    public List<AssetDataForGtDto> getAllFullAssets();

    public SerialCheckResponseDto checkSerials(String assetCode, Integer assetId, String custodianId, Integer locatorId, Integer quantity);

    public String addRemainingSerials(AssetSerialUpdateRequestDto req) ;
    }
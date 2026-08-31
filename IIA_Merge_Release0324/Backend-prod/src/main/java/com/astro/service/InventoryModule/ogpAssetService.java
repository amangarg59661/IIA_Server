package com.astro.service.InventoryModule;

import com.astro.dto.workflow.InventoryModule.AssetDisposalDto;
import com.astro.dto.workflow.InventoryModule.AssetMasterDto;
import com.astro.dto.workflow.InventoryModule.AssetsAuctionDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ogpAssetService {
    String saveAssetDisposalOgp(AssetsAuctionDto request);
    public List<AssetsAuctionDto> getPendingApprovals();
    public String approveOgpAssetDisposal(Integer disposalId, Integer actionBy);
    public String rejectOgpAssetDisposal(Integer disposalId, Integer actionBy);
}

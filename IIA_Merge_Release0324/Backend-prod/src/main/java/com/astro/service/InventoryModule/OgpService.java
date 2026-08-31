package com.astro.service.InventoryModule;

import java.time.LocalDateTime;
import java.util.List;

import com.astro.dto.workflow.InventoryModule.ogp.*;
import com.astro.util.CommonUtils;

import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtMasterDto;

public interface OgpService {
    public String saveOgp(OgpDto req);

    public OgpDto getOgpDtls(String processNo);

    public List<OgpReportDto> getOgpReport(String startDate, String endDate);

    public String savePoOgp(OgpPoDto request);

    public OgpPoResponseDto getPoOgp(String processNo);

    void approveOgp(GprApprovalDto req, Integer actionBy);

    void rejectOgp(GprApprovalDto req, Integer actionBy);

    public String saveOgpRejectedGi(OgpMasterRejectedGiDto req);
    public List<OgpMasterRejectedGiDto> getAwaitingRejectedGi();
    public void approveGiOgp(String ogpId, Integer actionBy);
    public List<OgpRejectedGiReportDto> getOgpRejectedGiReport(String startDate, String endDate);

    public void rejectGiOgp(String ogpId, Integer actionBy);
    public String saveGtOgp(GtMasterDto gtMasterDto);
    public List<GtMasterDto> getReciverPendingGtOgp(Integer userId);
    public List<GtMasterDto> getPendingGtOgp();
    public void rejectGtOgp(String ogpId, Integer actionBy);
    public void approveGtOgp(String ogpId, Integer actionBy);
    public void approveReceiverGtOgp(String ogpId, Integer actionBy);
}

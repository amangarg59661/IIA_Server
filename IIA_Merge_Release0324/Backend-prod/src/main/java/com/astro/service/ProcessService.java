package com.astro.service;

import com.astro.dto.workflow.InventoryModule.GprnDto.SaveGprnDto;
import com.astro.dto.workflow.InventoryModule.PendingGprnPoDto;
import com.astro.dto.workflow.InventoryModule.grn.GrnDto;
import com.astro.dto.workflow.InventoryModule.grv.GrvDto;
import com.astro.dto.workflow.InventoryModule.igp.IgpDto;
import com.astro.dto.workflow.InventoryModule.isn.IsnDto;
import com.astro.dto.workflow.InventoryModule.ogp.GprApprovalDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpMasterRejectedGiDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoDto;
import com.astro.dto.workflow.InventoryModule.ogp.OgpPoResponseDto;
import com.astro.dto.workflow.InventoryModule.ohq.OhqReportDto;
import com.astro.entity.InventoryModule.IsnAssetOhqDtlsDto;

import java.util.List;

import com.astro.dto.workflow.InventoryModule.GiDto.SaveGiDto;

public interface ProcessService {
    public String saveGprn(SaveGprnDto saveGprnDto);

    public String saveGi(SaveGiDto saveGiDto);

    public String saveGrv(GrvDto req);

    public String saveGrn(GrnDto req);

    public String saveIsn(IsnDto req);

    public String saveOgp(OgpDto req);

    public String saveIgp(IgpDto req);

    public String savePoOgp(OgpPoDto request);

    public List<IsnAssetOhqDtlsDto> getIsnAssetOhqDtls();

    public Object getSubProcessDtls(String processStage, String processId);

    public List<OhqReportDto> getOhqReport();
    public List<String> getPendingGprn();
    public List<PendingGprnPoDto> getPendingGprnDetails();

    public OgpPoResponseDto getPoOgp(String processNo);

    public void approveOgp(GprApprovalDto req, Integer actionBy);
    public void rejectOgp(GprApprovalDto req, Integer actionBy);
    public void rejectGprn(String processNo,Integer actionBy);
    public void approveGprn(String processNo, Integer actionBy);
    public void changeReqGprn(String processNo,Integer actionBy);
    public void updateGprn(SaveGprnDto req);
    public String saveOgpRejectedGi(OgpMasterRejectedGiDto req);
    public List<OgpMasterRejectedGiDto> getAwaitingRejectedGi();
public void approveGiOgp(String ogpId, Integer actionBy);
public void rejectGiOgp(String ogpId, Integer actionBy);
}

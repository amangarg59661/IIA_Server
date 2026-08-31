package com.astro.service.InventoryModule;

import java.util.List;

import com.astro.dto.workflow.InventoryModule.igp.*;
import com.astro.dto.workflow.InventoryModule.ogp.MaterialIgpDto;

public interface IgpService {
    String saveIgp(IgpDto req);
    IgpDto getIgpDtls(String processNo);
    List<IgpReportDto> getIgpReport(String startDate, String endDate);

    public List<IgpCombinedDetailDto> getIgpDetails();
    public String saveMaterialIgp(MaterialIgpDto req);
    public void approveMaterialIgp(String igpId, Integer actionBy);
    public void rejectMaterialIgp(String igpId, Integer actionBy);
    public MaterialIgpDto getIgpMaterialDtls(String igpId);
    public void validateMaterialIgp(String igpId);
    public List<MaterialIgpDto> getAwaitingApprovalIgp();
    public List<IgpMaterialInReportDto> getIgpMaterialInReport(String startDate, String endDate);
    }
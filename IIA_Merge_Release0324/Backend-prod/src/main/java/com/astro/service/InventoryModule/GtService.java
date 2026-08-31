package com.astro.service.InventoryModule;

import java.time.LocalDate;
import java.util.List;

import com.astro.dto.workflow.InventoryModule.GoodsTransfer.GtMasterDto;
import com.astro.dto.workflow.InventoryModule.GtMasterResponseDto;
import com.astro.dto.workflow.InventoryModule.withinFieldStationGtDto;

public interface GtService {
    public String createGt(GtMasterDto gtMasterDto);
    public List<GtMasterDto> getPendingGt();
    public List<GtMasterDto> getRecevierPendingGt();
    public void approveGt(String gtId, Integer actionBy);
    public void rejectGt(String gtId, Integer actionBy);
    public GtMasterResponseDto getGtById(String gtId);
    public GtMasterDto getGtDtls(String gtId);
    public void approveGtFromOgp(String gtId);
    public void receiverApproveGt(String gtId, Integer actionBy);
    public List<withinFieldStationGtDto> getGtReport(String  startDate, String  endDate) ;

    public List<Long> getPendingInterFiledGtIdsOgp();
    }

package com.astro.service.InventoryModule;

import com.astro.dto.workflow.InventoryModule.GprnDto.SaveGprnDto;
import com.astro.dto.workflow.InventoryModule.PendingGprnPoDto;

import java.util.List;

public interface GprnService {
    String saveGprn(SaveGprnDto req);
    SaveGprnDto getGprnDtls(String processNo);
    void validateGprnSubProcessId(String processNo);

    public List<String> getPendingGprn();
    public List<PendingGprnPoDto> getPendingGprnDetails();
    public void rejectGprn(String processNo, Integer actionBy);
    public void approveGprn(String processNo, Integer actionBy);
    public void changeReqGprn(String processNo, Integer actionBy);
    public void updateGprn(SaveGprnDto updateRequest);
}
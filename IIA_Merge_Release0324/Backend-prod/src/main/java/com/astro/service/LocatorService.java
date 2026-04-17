package com.astro.service;

import java.util.List;
import com.astro.entity.AdminPanel.LOVMaster;
import com.astro.dto.AdminPanel.LOVRequestDto;
import com.astro.dto.workflow.LocatorMasterResDto;

public interface LocatorService {
    public List<LocatorMasterResDto> getLocatorMaster();
        // Added by Aman 
    void updateFromLOV(LOVMaster updated,LOVRequestDto request);
    void createFromLOV(LOVMaster saved , LOVRequestDto request);
    // End
}

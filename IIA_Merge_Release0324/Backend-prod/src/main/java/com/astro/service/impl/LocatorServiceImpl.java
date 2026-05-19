package com.astro.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.astro.dto.workflow.LocatorMasterResDto;
import com.astro.entity.LocatorMasterEntity;
import com.astro.repository.LocatorMasterRepository;
import com.astro.service.LocatorService;

import com.astro.entity.AdminPanel.LOVMaster;
import com.astro.entity.LocatorMasterEntity; // adjust package as needed
  import java.util.Optional;
  import com.astro.dto.AdminPanel.LOVRequestDto;

@Service
public class LocatorServiceImpl implements LocatorService{

    @Autowired
    private LocatorMasterRepository lmr;

    @Override
    public List<LocatorMasterResDto> getLocatorMaster() {
        List<LocatorMasterEntity> lmeList = lmr.findAll();

        return lmeList.stream().map(lme -> {
            LocatorMasterResDto lmrDto = new LocatorMasterResDto();
            lmrDto.setValue(lme.getLocatorId());
            lmrDto.setLabel(lme.getLocatorDesc());
            lmrDto.setLocationId(lme.getLocationId());
            return lmrDto;
        }).collect(Collectors.toList());
    }
    // Added by Aman
     // ========== LOV-TRIGGERED METHODS ==========

    /**
     * Called by LOVServiceImpl when a new LOV is saved for designatorId = 1 (Location).
     * Creates a corresponding LocationMaster record.
     */
    public void createFromLOV(LOVMaster saved , LOVRequestDto request) {
        LocatorMasterEntity locator = new LocatorMasterEntity();
        locator.setLocationId(request.getLocationCode());
        locator.setLocatorId(Integer.parseInt(saved.getLovValue()));
        locator.setLocatorDesc(saved.getLovDisplayValue());
        locator.setCreatedBy("SYSTEM");
        locator.setUpdatedBy("SYSTEM");
        lmr.save(locator);
    }

    /**
     * Called by LOVServiceImpl when an existing LOV is updated for designatorId = 1 (Location).
     * Updates the corresponding LocationMaster record, or creates it if missing.
     */
    public void updateFromLOV(LOVMaster updated ,LOVRequestDto request) {

        // Add a null/format check
if (updated.getLovValue() == null || updated.getLovValue().isBlank()) {
    throw new RuntimeException("LOV value is null or empty");
}
        Optional<LocatorMasterEntity> existing = lmr.findById(Integer.parseInt(updated.getLovValue()));

       LocatorMasterEntity locator;
        if (existing.isPresent()) {
            locator = existing.get();
        } else {
            locator = new LocatorMasterEntity();
            locator.setLocatorId(Integer.parseInt(updated.getLovValue()));
            locator.setCreatedBy("SYSTEM");
        }
        
        locator.setLocatorDesc(updated.getLovDisplayValue());
        // locator.setAddress(updated.getLovDescription());
        // locator.setIsActive(updated.getIsActive());
        locator.setUpdatedBy("SYSTEM");
        lmr.save(locator);
    }
    // End
}

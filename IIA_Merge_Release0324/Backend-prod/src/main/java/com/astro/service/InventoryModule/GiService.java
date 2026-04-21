package com.astro.service.InventoryModule;

import com.astro.dto.workflow.InventoryModule.GiDto.GiApprovalDto;
import com.astro.dto.workflow.InventoryModule.GiDto.GiWorkflowStatusDto;
import com.astro.dto.workflow.InventoryModule.GiDto.SaveGiDto;
import com.astro.dto.workflow.InventoryModule.GprnDropdownDto;
import com.astro.dto.workflow.InventoryModule.gprn.GprnPendingInspectionDto;
import com.astro.entity.InventoryModule.GiMasterEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GiService {
    String saveGi(SaveGiDto req);
    Map<String, Object> getGiDtls(String processNo);
    void validateGiSubProcessId(String processNo);
    public List<GprnPendingInspectionDto> getGiStatusWise(String status, Optional<String> createdBy);


    public void changeReqGi(GiApprovalDto req);
    public void rejectGi(GiApprovalDto req);
    public void approveGi(GiApprovalDto req);
    public List<GiMasterEntity> getGiByStatuses();
    public List<GiMasterEntity> getGiByIndentorStatuses();
    public String updateGi(SaveGiDto req);
    public void validateGiIsApproved(String processNo);
    public List<GiWorkflowStatusDto> getGiHistoryByProcessId(String processId, Integer subProcessId);

    public List<GprnDropdownDto> getPendingGprnsForGI(Integer userId, String role);
    public List<GprnDropdownDto> getPendingRejectedGis();

}
package com.astro.service.InventoryModule;

import com.astro.dto.workflow.InventoryModule.GiDto.GiApprovalDto;
import com.astro.dto.workflow.InventoryModule.GiDto.GiWorkflowStatusDto;
import com.astro.dto.workflow.InventoryModule.GprnDropdownDto;
import com.astro.dto.workflow.InventoryModule.GrnDropdownDto;
import com.astro.dto.workflow.InventoryModule.PoGrnInfoDto;
import com.astro.dto.workflow.InventoryModule.grn.GrnDto;
import com.astro.dto.workflow.InventoryModule.grn.GrnMaterialMasterDto;
import com.astro.dto.workflow.InventoryModule.grn.UpdateGrnDto;
import com.astro.dto.workflow.InventoryModule.paymentVoucherDto;
import com.astro.entity.InventoryModule.GiMasterEntity;
import com.astro.entity.InventoryModule.GrnMasterEntity;

import java.util.List;
import java.util.Map;

public interface GrnService {
    String saveGrn(GrnDto req);
    Map<String, Object> getGrnDtls(String processNo);

    public void changeReqGrn(GiApprovalDto req);
    public void rejectGrn(GiApprovalDto req);
    public void approveGrn(GiApprovalDto req);
    public List<GrnMasterEntity> getGrnByStatuses();
    public List<GrnMasterEntity> getGrnByStorePresonStatuses();
    public String updateGrn(GrnDto req);
    public List<GiWorkflowStatusDto> getGrnHistoryByProcessId(String processId, Integer subProcessId);
    public String saveMaterialGrn(GrnMaterialMasterDto req);

    public List<PoGrnInfoDto> getDistinctGrnProcessIdsForGIAndApproved();


    public List<String> getApprovedSoIds();
    public List<String> getGrnDetailsByProcessId(String grnProcessId);
    public paymentVoucherDto getPaymentVoucherData(String grnProcessId);

    public paymentVoucherDto getPaymentVoucherDtoBySoId(String soId);
    public List<String> getApprovedCpIds();
    public paymentVoucherDto getPaymentVoucherDtoByCpId(String cpId);
    public List<GrnDropdownDto> getPendingGrns();

}
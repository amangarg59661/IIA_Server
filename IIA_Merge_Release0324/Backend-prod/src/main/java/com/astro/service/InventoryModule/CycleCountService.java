package com.astro.service.InventoryModule;

import java.util.List;

import com.astro.dto.workflow.InventoryModule.cyclecount.CycleCountDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.InitiateCycleCountDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.PendingCycleCountDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.SubmitCycleCountDto;
import com.astro.dto.workflow.InventoryModule.cyclecount.CycleCountReportDto;

public interface CycleCountService {

    // Creates the count header + lines (system-qty snapshot taken here). Returns "CC/<id>".
    String initiateCycleCount(InitiateCycleCountDto req);

    List<CycleCountReportDto> getCycleCountReport();

    List<PendingCycleCountDto> searchCycleCounts(String value);

    // Records counted quantities, computes variance, flips status to AWAITING APPROVAL.
    // Does NOT initiate the workflow -- that is done by the caller (ProcessController),
    // matching how Payment Voucher's controller calls workflowService.initiateWorkflow
    // right after its own service call.
    void submitCycleCount(SubmitCycleCountDto req);

    // Applies the signed variance to store stock via StoreStockService and marks APPROVED.
    // Not exposed as a direct REST endpoint -- called only by WorkflowServiceImpl when the
    // generic approval chain reaches its final step for a "CC" request.
    void approveCycleCount(String cycleCountId);

    // Marks REJECTED. Not exposed as a direct REST endpoint -- called only by
    // WorkflowServiceImpl's rejection dispatch.
    void rejectCycleCount(String cycleCountId);

    CycleCountDto getCycleCountDtls(String cycleCountId);

    List<PendingCycleCountDto> getPendingCycleCounts();
}

package com.astro.service;

import com.astro.entity.ProcurementModule.JobDetails;
import com.astro.entity.ProcurementModule.MaterialDetails;
import com.astro.entity.ProcurementModule.PurchaseOrderAttributes;
import java.util.List;

public interface BudgetService {

    void holdBudgetForIndent(String indentId,
                              List<MaterialDetails> materials,
                              List<JobDetails> jobs);

    void reHoldBudgetForUpdatedIndent(String oldIndentId,
                                       String newIndentId,
                                       List<MaterialDetails> newMaterials,
                                       List<JobDetails> newJobs);

    void releaseBudgetHoldForIndent(String indentId);
    // Check only — no hold changes. Called on PO create/update.
void checkBudgetForPo(String poId, String tenderId, List<PurchaseOrderAttributes> poAttributes);

// Called on PO final approval — release indent holds, place PO holds.
void finalizePoHold(String poId, String tenderId, List<PurchaseOrderAttributes> poAttributes);

// Called on PO cancel — release PO hold, restore indent holds.
void cancelPoHold(String poId, String tenderId);

    void releaseHoldIfRejected(String referenceId, String referenceType);

    // Called on GRN approval — move PO hold → spent for received materials
void convertHoldToSpentOnGrn(Integer grnSubProcessId);

// Called on GI approval — release hold for permanently rejected materials
void releaseHoldForPermanentRejection(Integer inspectionSubProcessId, String poId);
}
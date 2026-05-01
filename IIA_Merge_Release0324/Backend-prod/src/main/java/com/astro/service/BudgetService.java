package com.astro.service;

import com.astro.entity.ProcurementModule.JobDetails;
import com.astro.entity.ProcurementModule.MaterialDetails;

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
}
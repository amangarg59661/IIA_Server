package com.astro.service;

import com.astro.entity.ProcurementModule.GemTenderEvaluation;

import java.util.List;
import java.util.Map;

public interface GemTenderEvaluationService {

    /** Add a vendor entry manually (Purchase Personnel, GeM/Open/Global flow). */
    GemTenderEvaluation addVendor(String tenderId, String vendorName,
                                   String technicalDocFileName, String financialDocFileName,
                                   Integer addedByUserId);

    /** Upload / update technical doc for an existing GeM vendor entry. */
    GemTenderEvaluation uploadTechnicalDoc(Long id, String technicalDocFileName, Integer userId);

    /** Upload / update financial doc for an existing GeM vendor entry. */
    GemTenderEvaluation uploadFinancialDoc(Long id, String financialDocFileName, Integer userId);

    /** Get all entries for a tender. */
    List<GemTenderEvaluation> getByTenderId(String tenderId);

    /**
     * "Send Quotation for Evaluation" action.
     * Promotes all un-sent GeM vendor entries into VendorQuotationAgainstTender
     * so they appear in the Tender Evaluation table.
     * Returns count of promoted entries.
     */
    Map<String, Object> sendForEvaluation(String tenderId, Integer actionByUserId);
}

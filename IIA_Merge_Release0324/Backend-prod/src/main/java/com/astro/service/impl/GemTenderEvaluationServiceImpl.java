package com.astro.service.impl;

import com.astro.entity.GemVendorIdTracker;
import com.astro.entity.ProcurementModule.GemTenderEvaluation;
import com.astro.entity.VendorQuotationAgainstTender;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.GemVendorIdTrackerRepository;
import com.astro.repository.ProcurementModule.GemTenderEvaluationRepository;
import com.astro.repository.VendorQuotationAgainstTenderRepository;
import com.astro.service.GemTenderEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GemTenderEvaluationServiceImpl implements GemTenderEvaluationService {

    @Autowired private GemTenderEvaluationRepository gemRepo;
    @Autowired private VendorQuotationAgainstTenderRepository quotationRepository;
    @Autowired private GemVendorIdTrackerRepository gemVendorIdTrackerRepository;

    @Override
    public GemTenderEvaluation addVendor(String tenderId, String vendorName,
                                          String technicalDocFileName, String financialDocFileName,
                                          Integer addedByUserId) {
        GemTenderEvaluation entry = new GemTenderEvaluation();
        entry.setTenderId(tenderId);
        entry.setVendorName(vendorName);
        entry.setTechnicalDocFileName(technicalDocFileName);
        entry.setFinancialDocFileName(financialDocFileName);
        entry.setAddedByUserId(addedByUserId);
        entry.setStatus("PENDING");
        entry.setSentForEvaluation(false);
        return gemRepo.save(entry);
    }

    @Override
    public GemTenderEvaluation uploadTechnicalDoc(Long id, String technicalDocFileName, Integer userId) {
        GemTenderEvaluation entry = requireEntry(id);
        entry.setTechnicalDocFileName(technicalDocFileName);
        entry.setUpdatedDate(LocalDateTime.now());
        return gemRepo.save(entry);
    }

    @Override
    public GemTenderEvaluation uploadFinancialDoc(Long id, String financialDocFileName, Integer userId) {
        GemTenderEvaluation entry = requireEntry(id);
        entry.setFinancialDocFileName(financialDocFileName);
        entry.setUpdatedDate(LocalDateTime.now());
        return gemRepo.save(entry);
    }

    @Override
    public List<GemTenderEvaluation> getByTenderId(String tenderId) {
        return gemRepo.findByTenderId(tenderId);
    }

    @Override
    public Map<String, Object> sendForEvaluation(String tenderId, Integer actionByUserId) {
        List<GemTenderEvaluation> pending = gemRepo.findByTenderIdAndSentForEvaluationFalse(tenderId);

        if (pending.isEmpty()) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "No pending GeM vendor entries found for tender: " + tenderId
                    + ". All entries may already have been sent for evaluation."));
        }

        int promoted = 0;
        for (GemTenderEvaluation gem : pending) {
            // Auto-assign a GemVendorId if not yet tracked
            String gemVendorId = resolveGemVendorId(gem.getVendorName());
            gem.setVendorId(gemVendorId);

            // Promote into VendorQuotationAgainstTender
            boolean alreadyExists = quotationRepository
                    .findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, gemVendorId)
                    .isPresent();

            if (!alreadyExists) {
                VendorQuotationAgainstTender quotation = new VendorQuotationAgainstTender();
                quotation.setTenderId(tenderId);
                quotation.setVendorId(gemVendorId);
                quotation.setQuotationFileName(gem.getTechnicalDocFileName());
                quotation.setPriceBidFileName(gem.getFinancialDocFileName());
                quotation.setStatus("SUBMITTED");
                quotation.setIsLatest(true);
                quotation.setVersion(1);
                quotation.setCreatedBy(actionByUserId);
                quotation.setTechnicalStatus("PENDING");
                quotation.setFinancialBidVisible(false);
                quotation.setCreatedDate(LocalDateTime.now());
                quotation.setUpdatedDate(LocalDateTime.now());
                quotationRepository.save(quotation);
            }

            gem.setSentForEvaluation(true);
            gem.setSentAt(LocalDateTime.now());
            gem.setStatus("SENT_FOR_EVALUATION");
            gemRepo.save(gem);
            promoted++;
        }

        return Map.of(
                "tenderId", tenderId,
                "promotedCount", promoted,
                "message", promoted + " GeM vendor(s) sent for Tender Evaluation."
        );
    }

    private GemTenderEvaluation requireEntry(Long id) {
        return gemRepo.findById(id)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "GeM Tender Evaluation entry not found: " + id)));
    }

    private String resolveGemVendorId(String vendorName) {
        // Reuse existing gem vendor ID if same name was used before
        // Otherwise mint a new one using GemVendorIdTracker sequence
        return gemVendorIdTrackerRepository.findAll().stream()
                .filter(g -> vendorName.equalsIgnoreCase(g.getVendorName()))
                .map(GemVendorIdTracker::getGemVendorId)
                .findFirst()
                .orElseGet(() -> {
                    long nextSeq = gemVendorIdTrackerRepository.findTopByOrderByVendorIdDesc()
                            .map(g -> g.getVendorId() + 1L)
                            .orElse(1L);
                    String newGemId = "Gem" + String.format("%04d", nextSeq);
                    GemVendorIdTracker tracker = new GemVendorIdTracker();
                    tracker.setVendorId(nextSeq);
                    tracker.setGemVendorId(newGemId);
                    tracker.setVendorName(vendorName);
                    gemVendorIdTrackerRepository.save(tracker);
                    return newGemId;
                });
    }
}

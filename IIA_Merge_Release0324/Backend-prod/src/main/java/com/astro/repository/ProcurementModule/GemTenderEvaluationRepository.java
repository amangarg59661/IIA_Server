package com.astro.repository.ProcurementModule;

import com.astro.entity.ProcurementModule.GemTenderEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GemTenderEvaluationRepository extends JpaRepository<GemTenderEvaluation, Long> {

    List<GemTenderEvaluation> findByTenderId(String tenderId);

    List<GemTenderEvaluation> findByTenderIdAndSentForEvaluationFalse(String tenderId);

    boolean existsByTenderIdAndVendorName(String tenderId, String vendorName);
}

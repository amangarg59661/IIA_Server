package com.astro.repository.ProcurementModule;

import com.astro.entity.ProcurementModule.TenderEvaluation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenderEvaluationRepository extends JpaRepository<TenderEvaluation, String> {

    TenderEvaluation findByTenderId(String tenderId);

    boolean existsByTenderIdAndEvaluationStatus(String tenderId, String evaluationStatus);

    Optional<TenderEvaluation> findByTenderIdAndEvaluationStatus(String tenderId, String evaluationStatus);

    /** Returns all tender IDs where evaluation is fully approved (final status). */
    @Query("SELECT te.tenderId FROM TenderEvaluation te WHERE te.evaluationStatus = 'APPROVED'")
    List<String> findAllApprovedEvaluationTenderIds();

    /** Returns approved vendor IDs for a tender (SPO-accepted, for PO vendor dropdown). */
    @Query("""
        SELECT q.vendorId FROM VendorQuotationAgainstTender q
        WHERE q.tenderId = :tenderId
          AND q.isLatest = true
          AND q.spoStatus = 'ACCEPTED'
    """)
    List<String> findSpoApprovedVendorIds(@Param("tenderId") String tenderId);
}

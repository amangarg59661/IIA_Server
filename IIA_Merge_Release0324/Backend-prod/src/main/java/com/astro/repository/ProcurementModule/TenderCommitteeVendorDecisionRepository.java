package com.astro.repository.ProcurementModule;

import com.astro.entity.TenderCommitteeVendorDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenderCommitteeVendorDecisionRepository
        extends JpaRepository<TenderCommitteeVendorDecision, Long> {

    List<TenderCommitteeVendorDecision> findByTenderIdAndVendorIdAndPhase(
            String tenderId, String vendorId, String phase);

    List<TenderCommitteeVendorDecision> findByTenderIdAndCommitteeUserIdAndPhase(
            String tenderId, Integer committeeUserId, String phase);

    List<TenderCommitteeVendorDecision> findByTenderIdAndPhase(
            String tenderId, String phase);

    Optional<TenderCommitteeVendorDecision> findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(
            String tenderId, String vendorId, Integer committeeUserId, String phase);

    void deleteByTenderIdAndPhase(String tenderId, String phase);

    List<TenderCommitteeVendorDecision> findByTenderIdAndPhaseAndVoterRole(
            String tenderId, String phase, String voterRole);
}

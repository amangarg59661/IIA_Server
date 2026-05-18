
package com.astro.repository;

import com.astro.entity.TenderCommitteeDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenderCommitteeDecisionRepository extends JpaRepository<TenderCommitteeDecision, Long> {

    List<TenderCommitteeDecision> findByTenderId(String tenderId);

    Optional<TenderCommitteeDecision> findByTenderIdAndCommitteeUserId(String tenderId, Integer committeeUserId);

    // Find the single committee-level row (chairmanDecision is set here; it may not be per-member)
    Optional<TenderCommitteeDecision> findFirstByTenderIdOrderByCreatedDateDesc(String tenderId);

    List<TenderCommitteeDecision> findByTenderIdAndVoteIsNotNull(String tenderId);

    boolean existsByTenderId(String tenderId);
}
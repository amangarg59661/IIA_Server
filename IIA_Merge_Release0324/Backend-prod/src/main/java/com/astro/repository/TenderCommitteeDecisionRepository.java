
package com.astro.repository;

import com.astro.entity.TenderCommitteeDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT COUNT(*) FROM tender_committee_decision d " +
                   "JOIN tender_request t ON d.tender_id = t.tender_id " +
                   "WHERE d.committee_user_id = :userId " +
                   "AND d.tender_id != :excludeTenderId " +
                   "AND t.locked_for_po IS NULL",
           nativeQuery = true)
    int countActiveAssignmentsExcludingTender(
            @Param("userId") Integer userId,
            @Param("excludeTenderId") String excludeTenderId);
}
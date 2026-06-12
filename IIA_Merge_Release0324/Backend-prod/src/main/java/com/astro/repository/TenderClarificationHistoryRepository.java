package com.astro.repository;

import com.astro.entity.TenderClarificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenderClarificationHistoryRepository extends JpaRepository<TenderClarificationHistory, Long> {

    List<TenderClarificationHistory> findByTenderIdOrderByRequestedAtDesc(String tenderId);

    @Query("SELECT COALESCE(MAX(h.roundNumber), 0) FROM TenderClarificationHistory h WHERE h.tenderId = :tenderId")
    int findMaxRoundByTenderId(@Param("tenderId") String tenderId);

    List<TenderClarificationHistory> findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(
            String tenderId, String targetVendorId);

    long countByTenderIdAndRespondedAtIsNull(String tenderId);

    List<TenderClarificationHistory> findByTenderIdAndClarificationTargetAndRespondedAtIsNull(
            String tenderId, String clarificationTarget);

    List<TenderClarificationHistory> findByTenderIdAndRequestedByRoleAndClarificationTargetAndRespondedAtIsNull(
            String tenderId, String requestedByRole, String clarificationTarget);
}
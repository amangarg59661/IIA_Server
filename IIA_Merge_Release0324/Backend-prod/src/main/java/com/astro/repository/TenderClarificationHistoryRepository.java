package com.astro.repository;

import com.astro.entity.TenderClarificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TenderClarificationHistoryRepository extends JpaRepository<TenderClarificationHistory, Long> {

    List<TenderClarificationHistory> findByTenderIdOrderByRequestedAtDesc(String tenderId);

    @Query("SELECT COALESCE(MAX(h.roundNumber), 0) FROM TenderClarificationHistory h WHERE h.tenderId = :tenderId")
    int findMaxRoundByTenderId(String tenderId);
}
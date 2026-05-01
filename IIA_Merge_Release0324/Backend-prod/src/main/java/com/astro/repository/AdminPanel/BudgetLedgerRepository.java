package com.astro.repository.AdminPanel;

import com.astro.entity.AdminPanel.BudgetLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetLedgerRepository extends JpaRepository<BudgetLedger, Long> {

    @Query("SELECT l FROM BudgetLedger l WHERE l.referenceId = :referenceId AND l.referenceType = :referenceType AND l.status = :status")
    List<BudgetLedger> findByReferenceIdAndTypeAndStatus(
            @Param("referenceId") String referenceId,
            @Param("referenceType") String referenceType,
            @Param("status") String status);

    List<BudgetLedger> findByBudgetCode(String budgetCode);
}
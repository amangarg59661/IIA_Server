package com.astro.service.impl;

import com.astro.entity.AdminPanel.BudgetLedger;
import com.astro.entity.AdminPanel.BudgetMaster;
import com.astro.entity.ProcurementModule.JobDetails;
import com.astro.entity.ProcurementModule.MaterialDetails;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.AdminPanel.BudgetLedgerRepository;
import com.astro.repository.AdminPanel.BudgetMasterRepository;
import com.astro.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetMasterRepository budgetMasterRepository;

    @Autowired
    private BudgetLedgerRepository budgetLedgerRepository;

    // ─── 1. HOLD on new indent submit ────────────────────────────────────────

    @Override
    @Transactional
    public void holdBudgetForIndent(String indentId,
                                     List<MaterialDetails> materials,
                                     List<JobDetails> jobs) {

        Map<String, BigDecimal> budgetTotals = computeBudgetTotals(materials, jobs);

        for (Map.Entry<String, BigDecimal> entry : budgetTotals.entrySet()) {
            String budgetCode = entry.getKey();
            BigDecimal amount = entry.getValue();

            BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            400, 4, "Budget Not Found",
                            "Budget not found for code: " + budgetCode)));

            BigDecimal available = budget.getRemainingAmount();
            if (available.compareTo(amount) < 0) {
                throw new BusinessException(new ErrorDetails(
                        400, 4, "Insufficient Budget",
                        "Insufficient budget for code: " + budgetCode
                        + ". Required: " + amount + ", Available: " + available));
            }

            budget.setOnHoldAmount(budget.getOnHoldAmount().add(amount));
            budgetMasterRepository.save(budget);

            BudgetLedger ledger = new BudgetLedger();
            ledger.setBudgetCode(budgetCode);
            ledger.setReferenceId(indentId);
            ledger.setReferenceType("INDENT");
            ledger.setHoldAmount(amount);
            ledger.setStatus("ACTIVE_HOLD");
            budgetLedgerRepository.save(ledger);
        }
    }

    // ─── 2. RE-HOLD on indent version update (V2, V3...) ────────────────────

    @Override
    @Transactional
    public void reHoldBudgetForUpdatedIndent(String oldIndentId,
                                              String newIndentId,
                                              List<MaterialDetails> newMaterials,
                                              List<JobDetails> newJobs) {

        Map<String, BigDecimal> newTotals = computeBudgetTotals(newMaterials, newJobs);

        List<BudgetLedger> oldLedgers = budgetLedgerRepository
                .findByReferenceIdAndTypeAndStatus(oldIndentId, "INDENT", "ACTIVE_HOLD");

        Map<String, BigDecimal> oldTotals = oldLedgers.stream()
                .collect(Collectors.toMap(BudgetLedger::getBudgetCode, BudgetLedger::getHoldAmount));

        Set<String> allBudgetCodes = new HashSet<>();
        allBudgetCodes.addAll(oldTotals.keySet());
        allBudgetCodes.addAll(newTotals.keySet());

        // --- VALIDATION PASS (check all before making any changes) ---
        for (String budgetCode : allBudgetCodes) {
            BigDecimal oldAmount = oldTotals.getOrDefault(budgetCode, BigDecimal.ZERO);
            BigDecimal newAmount = newTotals.getOrDefault(budgetCode, BigDecimal.ZERO);
            BigDecimal diff = newAmount.subtract(oldAmount);

            if (diff.compareTo(BigDecimal.ZERO) > 0) {
                BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode)
                        .orElseThrow(() -> new BusinessException(new ErrorDetails(
                                400, 4, "Budget Not Found",
                                "Budget not found for code: " + budgetCode)));

                // Effective available = current available + old hold (since we'll release it)
                BigDecimal effectiveAvailable = budget.getRemainingAmount().add(oldAmount);
                if (effectiveAvailable.compareTo(newAmount) < 0) {
                    throw new BusinessException(new ErrorDetails(
                            400, 4, "Insufficient Budget",
                            "Insufficient budget for updated indent on budget code: " + budgetCode
                            + ". Required: " + newAmount
                            + ", Effectively available: " + effectiveAvailable));
                }
            }
        }

        // --- APPLY CHANGES (only after all validations pass) ---
        for (String budgetCode : allBudgetCodes) {
            BigDecimal oldAmount = oldTotals.getOrDefault(budgetCode, BigDecimal.ZERO);
            BigDecimal newAmount = newTotals.getOrDefault(budgetCode, BigDecimal.ZERO);

            BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            400, 4, "Budget Not Found",
                            "Budget not found for code: " + budgetCode)));

            // Release old hold, place new hold in one operation
            budget.setOnHoldAmount(
                    budget.getOnHoldAmount()
                            .subtract(oldAmount)
                            .add(newAmount)
            );
            budgetMasterRepository.save(budget);

            // Mark old ledger entries as RELEASED
            oldLedgers.stream()
                    .filter(l -> l.getBudgetCode().equals(budgetCode))
                    .forEach(l -> {
                        l.setStatus("RELEASED");
                        budgetLedgerRepository.save(l);
                    });

            // Create new ledger entry for new version
            if (newAmount.compareTo(BigDecimal.ZERO) > 0) {
                BudgetLedger newLedger = new BudgetLedger();
                newLedger.setBudgetCode(budgetCode);
                newLedger.setReferenceId(newIndentId);
                newLedger.setReferenceType("INDENT");
                newLedger.setHoldAmount(newAmount);
                newLedger.setStatus("ACTIVE_HOLD");
                budgetLedgerRepository.save(newLedger);
            }
        }
    }

    // ─── 3. RELEASE hold on indent cancellation ──────────────────────────────

    @Override
    @Transactional
    public void releaseBudgetHoldForIndent(String indentId) {
        List<BudgetLedger> activeLedgers = budgetLedgerRepository
                .findByReferenceIdAndTypeAndStatus(indentId, "INDENT", "ACTIVE_HOLD");

        for (BudgetLedger ledger : activeLedgers) {
            BudgetMaster budget = budgetMasterRepository
                    .findByBudgetCode(ledger.getBudgetCode()).orElse(null);
            if (budget != null) {
                budget.setOnHoldAmount(
                        budget.getOnHoldAmount().subtract(ledger.getHoldAmount()));
                budgetMasterRepository.save(budget);
            }
            ledger.setStatus("RELEASED");
            budgetLedgerRepository.save(ledger);
        }
    }

    // ─── HELPER ──────────────────────────────────────────────────────────────

    private Map<String, BigDecimal> computeBudgetTotals(List<MaterialDetails> materials,
                                                         List<JobDetails> jobs) {
        Map<String, BigDecimal> totals = new HashMap<>();

        if (materials != null) {
            for (MaterialDetails m : materials) {
                if (m.getBudgetCode() == null) continue;
                totals.merge(m.getBudgetCode(),
                        m.getTotalPrice() != null ? m.getTotalPrice() : BigDecimal.ZERO,
                        BigDecimal::add);
            }
        }
        if (jobs != null) {
            for (JobDetails j : jobs) {
                if (j.getBudgetCode() == null) continue;
                totals.merge(j.getBudgetCode(),
                        j.getTotalPrice() != null ? j.getTotalPrice() : BigDecimal.ZERO,
                        BigDecimal::add);
            }
        }
        return totals;
    }
}
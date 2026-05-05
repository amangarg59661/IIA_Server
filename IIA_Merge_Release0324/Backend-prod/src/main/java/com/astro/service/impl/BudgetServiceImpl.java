package com.astro.service.impl;

import com.astro.entity.AdminPanel.BudgetLedger;
import com.astro.entity.AdminPanel.BudgetMaster;
import com.astro.entity.InventoryModule.AssetMasterEntity;
import com.astro.entity.InventoryModule.GrnConsumableDtlEntity;
import com.astro.repository.InventoryModule.grn.GrnConsumableDtlRepository;
import com.astro.entity.ProcurementModule.JobDetails;
import com.astro.entity.ProcurementModule.MaterialDetails;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.AdminPanel.BudgetLedgerRepository;
import com.astro.repository.AdminPanel.BudgetMasterRepository;
import com.astro.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.astro.entity.ProcurementModule.PurchaseOrderAttributes;
import com.astro.repository.ProcurementModule.IndentIdRepository;
import com.astro.entity.InventoryModule.GrnMasterEntity;
import com.astro.entity.InventoryModule.GrnMaterialDtlEntity;
import com.astro.entity.InventoryModule.GiMaterialDtlEntity;
import com.astro.entity.InventoryModule.GoodsInspectionConsumableDetailEntity;
import com.astro.entity.InventoryModule.GprnMasterEntity;
import com.astro.repository.InventoryModule.grn.GrnMasterRepository;
import com.astro.repository.InventoryModule.grn.GrnMaterialDtlRepository;
import com.astro.repository.InventoryModule.GiRepository.GiMaterialDtlRepository;
import com.astro.repository.InventoryModule.GoodsInspectionConsumableDetailRepository;
import com.astro.repository.InventoryModule.GprnRepository.GprnMasterRepository;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {
    @Autowired
private com.astro.repository.InventoryModule.AssetMasterRepository assetMasterRepository;

@Autowired
private com.astro.repository.InventoryModule.grn.GrnConsumableDtlRepository grnConsumableDtlRepository;
@Autowired
private com.astro.repository.InventoryModule.grn.GrnMasterRepository grnMasterRepository;

@Autowired
private com.astro.repository.InventoryModule.grn.GrnMaterialDtlRepository grnMaterialDtlRepository;

@Autowired
private com.astro.repository.InventoryModule.GiRepository.GiMaterialDtlRepository giMaterialDtlRepository;

@Autowired
private com.astro.repository.InventoryModule.GoodsInspectionConsumableDetailRepository goodsInspectionConsumableDetailRepository;

@Autowired
private com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderAttributesRepository purchaseOrderAttributesRepository;

@Autowired
private com.astro.repository.InventoryModule.GprnRepository.GprnMasterRepository gprnMasterRepository;

    @Autowired
    private BudgetMasterRepository budgetMasterRepository;
@Autowired
private IndentIdRepository indentIdRepository;
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

    @Override
@Transactional
public void releaseHoldIfRejected(String referenceId, String referenceType) {
    List<BudgetLedger> activeLedgers = budgetLedgerRepository
            .findByReferenceIdAndTypeAndStatus(referenceId, referenceType, "ACTIVE_HOLD");

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
// ─── PO: CHECK ONLY on create/update ─────────────────────────────────────────

@Override
@Transactional
public void checkBudgetForPo(String poId, String tenderId,
                              List<PurchaseOrderAttributes> poAttributes) {

    // PO totals per budgetCode
    Map<String, BigDecimal> poTotals = computePoTotals(poAttributes);

    // Indent holds per budgetCode (sum across all indents in this tender)
    Map<String, BigDecimal> indentHolds = getIndentHoldsForTender(tenderId);

    // Union of all budget codes
    Set<String> allCodes = new HashSet<>();
    allCodes.addAll(poTotals.keySet());
    allCodes.addAll(indentHolds.keySet());

    for (String budgetCode : allCodes) {
        BigDecimal poAmount    = poTotals.getOrDefault(budgetCode, BigDecimal.ZERO);
        BigDecimal indentHold  = indentHolds.getOrDefault(budgetCode, BigDecimal.ZERO);
        BigDecimal diff        = poAmount.subtract(indentHold); // positive = PO needs more

        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            // PO > indent hold → check available
            BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            400, 4, "Budget Not Found",
                            "Budget not found for code: " + budgetCode)));

            if (budget.getRemainingAmount().compareTo(diff) < 0) {
                throw new BusinessException(new ErrorDetails(
                        400, 4, "Insufficient Budget",
                        "Insufficient budget for PO on budget code: " + budgetCode
                        + ". PO requires: " + poAmount
                        + ", Indent hold: " + indentHold
                        + ", Additional needed: " + diff
                        + ", Available: " + budget.getRemainingAmount()));
            }
        }
        // PO <= indent hold → always ok, budget will be freed at final approval
    }
}

// ─── PO: FINALIZE HOLD on final approval ─────────────────────────────────────

@Override
@Transactional
public void finalizePoHold(String poId, String tenderId,
                            List<PurchaseOrderAttributes> poAttributes) {

    Map<String, BigDecimal> poTotals    = computePoTotals(poAttributes);
    Map<String, BigDecimal> indentHolds = getIndentHoldsForTender(tenderId);

    Set<String> allCodes = new HashSet<>();
    allCodes.addAll(poTotals.keySet());
    allCodes.addAll(indentHolds.keySet());

    // ── VALIDATION PASS ──
    for (String budgetCode : allCodes) {
        BigDecimal poAmount   = poTotals.getOrDefault(budgetCode, BigDecimal.ZERO);
        BigDecimal indentHold = indentHolds.getOrDefault(budgetCode, BigDecimal.ZERO);
        BigDecimal diff       = poAmount.subtract(indentHold);

        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            400, 4, "Budget Not Found",
                            "Budget not found for code: " + budgetCode)));

            // Available + indentHold = effective available (we're releasing indent hold)
            BigDecimal effectiveAvailable = budget.getRemainingAmount().add(indentHold);
            if (effectiveAvailable.compareTo(poAmount) < 0) {
                throw new BusinessException(new ErrorDetails(
                        400, 4, "Insufficient Budget",
                        "Cannot finalize PO approval. Insufficient budget for code: " + budgetCode
                        + ". PO requires: " + poAmount
                        + ", Effective available: " + effectiveAvailable));
            }
        }
    }

    // ── APPLY PASS ──
    // 1. Release all indent holds for this tender
    releaseIndentHoldsForTender(tenderId);

    // 2. Place PO hold per budgetCode
    for (Map.Entry<String, BigDecimal> entry : poTotals.entrySet()) {
        String budgetCode = entry.getKey();
        BigDecimal amount = entry.getValue();
        if (amount.compareTo(BigDecimal.ZERO) <= 0) continue;

        BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        400, 4, "Budget Not Found",
                        "Budget not found for code: " + budgetCode)));

        budget.setOnHoldAmount(budget.getOnHoldAmount().add(amount));
        budgetMasterRepository.save(budget);

        BudgetLedger ledger = new BudgetLedger();
        ledger.setBudgetCode(budgetCode);
        ledger.setReferenceId(poId);
        ledger.setReferenceType("PO");
        ledger.setHoldAmount(amount);
        ledger.setStatus("ACTIVE_HOLD");
        budgetLedgerRepository.save(ledger);
    }
}

// ─── PO: CANCEL — release PO hold, restore indent holds ──────────────────────

@Override
@Transactional
public void cancelPoHold(String poId, String tenderId) {

    // Get current PO hold entries
    List<BudgetLedger> poLedgers = budgetLedgerRepository
            .findByReferenceIdAndTypeAndStatus(poId, "PO", "ACTIVE_HOLD");

    // Get indent holds that were released at PO final approval (now RELEASED)
    // We need to know how much indent originally held per budgetCode
    Map<String, BigDecimal> indentOriginalHolds = getReleasedIndentHoldsForTender(tenderId);

    // ── VALIDATION: can we restore indent holds? ──
    for (Map.Entry<String, BigDecimal> entry : indentOriginalHolds.entrySet()) {
        String budgetCode    = entry.getKey();
        BigDecimal indentAmt = entry.getValue();

        // PO hold for this budgetCode (what we're releasing)
        BigDecimal poHold = poLedgers.stream()
                .filter(l -> l.getBudgetCode().equals(budgetCode))
                .map(BudgetLedger::getHoldAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal diff = indentAmt.subtract(poHold); // positive = indent needed more than PO

        if (diff.compareTo(BigDecimal.ZERO) > 0) {
            // Indent originally held MORE than PO → releasing PO frees budget,
            // but we need 'diff' more to restore indent hold
            BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(
                            400, 4, "Budget Not Found",
                            "Budget not found for code: " + budgetCode)));

            // After releasing PO hold, available = getRemainingAmount() + poHold
            BigDecimal availableAfterPoRelease = budget.getRemainingAmount().add(poHold);
            if (availableAfterPoRelease.compareTo(indentAmt) < 0) {
                throw new BusinessException(new ErrorDetails(
                        400, 4, "Cannot Cancel PO",
                        "Cannot cancel PO. Insufficient budget to restore indent hold for code: "
                        + budgetCode
                        + ". Required to restore: " + indentAmt
                        + ", Available after PO release: " + availableAfterPoRelease));
            }
        }
    }

    // ── APPLY: release PO hold ──
    for (BudgetLedger ledger : poLedgers) {
        BudgetMaster budget = budgetMasterRepository
                .findByBudgetCode(ledger.getBudgetCode()).orElse(null);
        if (budget != null) {
            budget.setOnHoldAmount(budget.getOnHoldAmount().subtract(ledger.getHoldAmount()));
            budgetMasterRepository.save(budget);
        }
        ledger.setStatus("RELEASED");
        budgetLedgerRepository.save(ledger);
    }

    // ── APPLY: restore indent holds ──
    for (Map.Entry<String, BigDecimal> entry : indentOriginalHolds.entrySet()) {
        String budgetCode    = entry.getKey();
        BigDecimal indentAmt = entry.getValue();
        if (indentAmt.compareTo(BigDecimal.ZERO) <= 0) continue;

        BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode)
                .orElse(null);
        if (budget != null) {
            budget.setOnHoldAmount(budget.getOnHoldAmount().add(indentAmt));
            budgetMasterRepository.save(budget);
        }

        // Create a fresh INDENT ledger entry to track restored hold
        // Use tenderId as referenceId since it links back to all indents
        BudgetLedger restored = new BudgetLedger();
        restored.setBudgetCode(budgetCode);
        restored.setReferenceId(tenderId + "_RESTORED");
        restored.setReferenceType("INDENT");
        restored.setHoldAmount(indentAmt);
        restored.setStatus("ACTIVE_HOLD");
        budgetLedgerRepository.save(restored);
    }
}

// ─── HELPERS ──────────────────────────────────────────────────────────────────

private Map<String, BigDecimal> computePoTotals(List<PurchaseOrderAttributes> attrs) {
    Map<String, BigDecimal> totals = new HashMap<>();
    if (attrs == null) return totals;
    for (PurchaseOrderAttributes a : attrs) {
        if (a.getBudgetCode() == null) continue;
        totals.merge(a.getBudgetCode(),
                a.getTotalPoMaterialPriceInInr() != null
                        ? a.getTotalPoMaterialPriceInInr() : BigDecimal.ZERO,
                BigDecimal::add);
    }
    return totals;
}

private Map<String, BigDecimal> getIndentHoldsForTender(String tenderId) {
    // Find all indent IDs linked to this tender via BudgetLedger ACTIVE_HOLD entries
    // IndentIds are stored in IndentId table; we look up active ledger entries for each
    List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
    Map<String, BigDecimal> holds = new HashMap<>();
    for (String indentId : indentIds) {
        List<BudgetLedger> ledgers = budgetLedgerRepository
                .findByReferenceIdAndTypeAndStatus(indentId, "INDENT", "ACTIVE_HOLD");
        for (BudgetLedger l : ledgers) {
            holds.merge(l.getBudgetCode(), l.getHoldAmount(), BigDecimal::add);
        }
    }
    return holds;
}

private Map<String, BigDecimal> getReleasedIndentHoldsForTender(String tenderId) {
    // Get the most recently RELEASED indent ledger entries for indents in this tender
    // These were released when PO was finally approved
    List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
    Map<String, BigDecimal> holds = new HashMap<>();
    for (String indentId : indentIds) {
        List<BudgetLedger> ledgers = budgetLedgerRepository
                .findByReferenceIdAndTypeAndStatus(indentId, "INDENT", "RELEASED");
        for (BudgetLedger l : ledgers) {
            holds.merge(l.getBudgetCode(), l.getHoldAmount(), BigDecimal::add);
        }
    }
    return holds;
}

private void releaseIndentHoldsForTender(String tenderId) {
    List<String> indentIds = indentIdRepository.findTenderWithIndent(tenderId);
    for (String indentId : indentIds) {
        List<BudgetLedger> ledgers = budgetLedgerRepository
                .findByReferenceIdAndTypeAndStatus(indentId, "INDENT", "ACTIVE_HOLD");
        for (BudgetLedger l : ledgers) {
            BudgetMaster budget = budgetMasterRepository
                    .findByBudgetCode(l.getBudgetCode()).orElse(null);
            if (budget != null) {
                budget.setOnHoldAmount(budget.getOnHoldAmount().subtract(l.getHoldAmount()));
                budgetMasterRepository.save(budget);
            }
            l.setStatus("RELEASED");
            budgetLedgerRepository.save(l);
        }
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
    // ─── GRN APPROVAL: Hold → Spent ──────────────────────────────────────────────

// @Override
// @Transactional
// public void convertHoldToSpentOnGrn(Integer grnSubProcessId) {
//     try {
//         // 1. Get GRN master to find GI subProcessId and then GPRN → PO
//         GrnMasterEntity grnMaster = grnMasterRepository.findById(grnSubProcessId)
//                 .orElse(null);
//         if (grnMaster == null) {
//             System.err.println("⚠️ [GRN BUDGET] GRN not found for subProcessId: " + grnSubProcessId);
//             return;
//         }

//         // 2. Get GPRN to find poId
//         GprnMasterEntity gprnMaster = gprnMasterRepository
//                 .findBySubProcessId(grnMaster.getGiSubProcessId() != null
//                         ? grnMasterRepository.findGprnSubProcessIdByGiSubProcessId(grnMaster.getGiSubProcessId())
//                         : null);

//         if (gprnMaster == null || gprnMaster.getPoId() == null) {
//             System.err.println("⚠️ [GRN BUDGET] Cannot resolve GPRN/PO for GRN: " + grnSubProcessId);
//             return;
//         }
//         String poId = gprnMaster.getPoId();

//         // 3. Get GRN material lines — compute spend per budgetCode
//         List<GrnMaterialDtlEntity> grnMaterials = grnMaterialDtlRepository
//                 .findByGrnSubProcessId(grnSubProcessId);

//         // Group by budgetCode → sum (acceptedQty × PO rate)
//         Map<String, BigDecimal> spendPerBudget = new HashMap<>();

//         for (GrnMaterialDtlEntity line : grnMaterials) {
//             String materialCode = line.getMaterialCode();
//             BigDecimal qty = line.getQuantity() != null ? line.getQuantity() : BigDecimal.ZERO;

//             // Get PO attributes for rate and budgetCode
//             PurchaseOrderAttributes poa = purchaseOrderAttributesRepository
//                     .findByPurchaseOrder_PoIdAndMaterialCode(poId, materialCode)
//                     .orElse(null);
//             if (poa == null || poa.getBudgetCode() == null) continue;

//             BigDecimal rate = poa.getRate() != null ? poa.getRate() : BigDecimal.ZERO;
//             BigDecimal exchangeRate = (poa.getExchangeRate() != null
//                     && poa.getCurrency() != null
//                     && !"INR".equalsIgnoreCase(poa.getCurrency()))
//                     ? poa.getExchangeRate() : BigDecimal.ONE;

//             BigDecimal lineAmount = qty.multiply(rate).multiply(exchangeRate);
//             spendPerBudget.merge(poa.getBudgetCode(), lineAmount, BigDecimal::add);
//         }

//         // 4. For each budgetCode: reduce PO hold, increase spent
//         for (Map.Entry<String, BigDecimal> entry : spendPerBudget.entrySet()) {
//             String budgetCode = entry.getKey();
//             BigDecimal spendAmount = entry.getValue();

//             BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode).orElse(null);
//             if (budget == null) continue;

//             // Reduce hold, increase spent
//             budget.setOnHoldAmount(
//                     budget.getOnHoldAmount().subtract(spendAmount).max(BigDecimal.ZERO));
//             budget.setSpentAmount(
//                     (budget.getSpentAmount() != null ? budget.getSpentAmount() : BigDecimal.ZERO)
//                     .add(spendAmount));
//             budgetMasterRepository.save(budget);

//             // Update PO ledger entry — reduce holdAmount, increase spentAmount
//             List<BudgetLedger> poLedgers = budgetLedgerRepository
//                     .findByReferenceIdAndTypeAndStatus(poId, "PO", "ACTIVE_HOLD");
//             poLedgers.stream()
//                     .filter(l -> l.getBudgetCode().equals(budgetCode))
//                     .forEach(l -> {
//                         l.setHoldAmount(l.getHoldAmount().subtract(spendAmount).max(BigDecimal.ZERO));
//                         l.setSpentAmount(
//                                 (l.getSpentAmount() != null ? l.getSpentAmount() : BigDecimal.ZERO)
//                                 .add(spendAmount));
//                         // Keep status ACTIVE_HOLD until all qty received (partial GRN scenario)
//                         budgetLedgerRepository.save(l);
//                     });

//             System.out.println("✅ [GRN BUDGET] Budget " + budgetCode
//                     + " → Hold↓ Spent↑ by " + spendAmount + " (GRN subProcessId: " + grnSubProcessId + ")");
//         }
//     } catch (Exception e) {
//         System.err.println("❌ [GRN BUDGET] Failed to convert hold to spent: " + e.getMessage());
//         // Log but don't rethrow — GRN approval must not fail due to budget update
//     }
// }
private BigDecimal calculateLineAmount(BigDecimal qty, PurchaseOrderAttributes poa) {
    BigDecimal rate         = poa.getRate()          != null ? poa.getRate()          : BigDecimal.ZERO;
    BigDecimal exchangeRate = poa.getExchangeRate()  != null ? poa.getExchangeRate()  : BigDecimal.ONE;
    BigDecimal gst          = poa.getGst()           != null ? poa.getGst()           : BigDecimal.ZERO;
    BigDecimal duties       = poa.getDuties()        != null ? poa.getDuties()        : BigDecimal.ZERO;
    BigDecimal freight      = poa.getFreightCharge() != null ? poa.getFreightCharge() : BigDecimal.ZERO;

    boolean isForeign = poa.getCurrency() != null
            && !"INR".equalsIgnoreCase(poa.getCurrency());
    BigDecimal effectiveExchangeRate = isForeign ? exchangeRate : BigDecimal.ONE;

    BigDecimal baseAmount   = qty.multiply(rate).multiply(effectiveExchangeRate);
    BigDecimal gstAmount    = baseAmount.multiply(gst)
            .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    BigDecimal dutiesAmount = baseAmount.multiply(duties)
            .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

    return baseAmount.add(gstAmount).add(dutiesAmount).add(freight);
}

private BigDecimal calculateRejectedLineAmount(BigDecimal rejectedQty, PurchaseOrderAttributes poa) {
    BigDecimal rate         = poa.getRate()          != null ? poa.getRate()          : BigDecimal.ZERO;
    BigDecimal exchangeRate = poa.getExchangeRate()  != null ? poa.getExchangeRate()  : BigDecimal.ONE;
    BigDecimal gst          = poa.getGst()           != null ? poa.getGst()           : BigDecimal.ZERO;
    BigDecimal duties       = poa.getDuties()        != null ? poa.getDuties()        : BigDecimal.ZERO;
    BigDecimal freight      = poa.getFreightCharge() != null ? poa.getFreightCharge() : BigDecimal.ZERO;
    BigDecimal totalQty     = poa.getQuantity()      != null ? poa.getQuantity()      : BigDecimal.ONE;

    boolean isForeign = poa.getCurrency() != null && !"INR".equalsIgnoreCase(poa.getCurrency());
    BigDecimal effectiveExchangeRate = isForeign ? exchangeRate : BigDecimal.ONE;

    BigDecimal baseAmount   = rejectedQty.multiply(rate).multiply(effectiveExchangeRate);
    BigDecimal gstAmount    = baseAmount.multiply(gst)
            .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
    BigDecimal dutiesAmount = baseAmount.multiply(duties)
            .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);

    BigDecimal freightPortion = BigDecimal.ZERO;
    if (totalQty.compareTo(BigDecimal.ZERO) > 0) {
        freightPortion = freight
                .divide(totalQty, 10, java.math.RoundingMode.HALF_UP)
                .multiply(rejectedQty)
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }

    return baseAmount.add(gstAmount).add(dutiesAmount).add(freightPortion);
}
@Override
@Transactional
public void convertHoldToSpentOnGrn(Integer grnSubProcessId) {
    try {
        // 1. Get GRN master → GI subProcessId → GPRN → PO
        GrnMasterEntity grnMaster = grnMasterRepository.findById(grnSubProcessId).orElse(null);
        if (grnMaster == null) {
            System.err.println("⚠️ [GRN BUDGET] GRN not found: " + grnSubProcessId);
            return;
        }

        Integer gprnSubProcessId = grnMasterRepository
                .findGprnSubProcessIdByGiSubProcessId(grnMaster.getGiSubProcessId());
        if (gprnSubProcessId == null) {
            System.err.println("⚠️ [GRN BUDGET] GPRN not found for GI: " + grnMaster.getGiSubProcessId());
            return;
        }

        GprnMasterEntity gprnMaster = gprnMasterRepository.findBySubProcessId(gprnSubProcessId);
        if (gprnMaster == null || gprnMaster.getPoId() == null) {
            System.err.println("⚠️ [GRN BUDGET] PO not found for GPRN: " + gprnSubProcessId);
            return;
        }
        String poId = gprnMaster.getPoId();

        Map<String, BigDecimal> spendPerBudget = new HashMap<>();

        // 2a. Non-consumables — resolve materialCode via AssetMaster
        List<GrnMaterialDtlEntity> grnMaterials = grnMaterialDtlRepository
                .findByGrnSubProcessId(grnSubProcessId);

        for (GrnMaterialDtlEntity line : grnMaterials) {
            BigDecimal acceptedQty = line.getQuantity() != null
                    ? line.getQuantity() : BigDecimal.ZERO;
            if (acceptedQty.compareTo(BigDecimal.ZERO) == 0) continue;
            if (line.getAssetId() == null) continue;

            // Resolve materialCode from AssetMaster
            AssetMasterEntity asset = assetMasterRepository.findById(line.getAssetId()).orElse(null);
            if (asset == null || asset.getMaterialCode() == null) continue;

            PurchaseOrderAttributes poa = purchaseOrderAttributesRepository
                    .findByPurchaseOrder_PoIdAndMaterialCode(poId, asset.getMaterialCode())
                    .orElse(null);
            if (poa == null || poa.getBudgetCode() == null) continue;

            spendPerBudget.merge(poa.getBudgetCode(),
                    calculateLineAmount(acceptedQty, poa), BigDecimal::add);
        }

        // 2b. Consumables — materialCode is directly on the entity
        List<GrnConsumableDtlEntity> grnConsumables = grnConsumableDtlRepository
                .findByGrnSubProcessId(grnSubProcessId);

        for (GrnConsumableDtlEntity line : grnConsumables) {
            BigDecimal acceptedQty = line.getQuantity() != null
                    ? line.getQuantity() : BigDecimal.ZERO;
            if (acceptedQty.compareTo(BigDecimal.ZERO) == 0) continue;
            if (line.getMaterialCode() == null) continue;

            PurchaseOrderAttributes poa = purchaseOrderAttributesRepository
                    .findByPurchaseOrder_PoIdAndMaterialCode(poId, line.getMaterialCode())
                    .orElse(null);
            if (poa == null || poa.getBudgetCode() == null) continue;

            spendPerBudget.merge(poa.getBudgetCode(),
                    calculateLineAmount(acceptedQty, poa), BigDecimal::add);
        }

        // 3. For each budgetCode → reduce PO hold, increase spent
        for (Map.Entry<String, BigDecimal> entry : spendPerBudget.entrySet()) {
            String budgetCode   = entry.getKey();
            BigDecimal spendAmt = entry.getValue();

            BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode).orElse(null);
            if (budget == null) continue;

            budget.setOnHoldAmount(
                    budget.getOnHoldAmount().subtract(spendAmt).max(BigDecimal.ZERO));
            budget.setSpentAmount(
                    (budget.getSpentAmount() != null ? budget.getSpentAmount() : BigDecimal.ZERO)
                    .add(spendAmt));
            budgetMasterRepository.save(budget);

            // Update PO ledger — reduce hold, increase spent
            budgetLedgerRepository
                    .findByReferenceIdAndTypeAndStatus(poId, "PO", "ACTIVE_HOLD")
                    .stream()
                    .filter(l -> l.getBudgetCode().equals(budgetCode))
                    .forEach(l -> {
                        l.setHoldAmount(l.getHoldAmount().subtract(spendAmt).max(BigDecimal.ZERO));
                        l.setSpentAmount(
                                (l.getSpentAmount() != null ? l.getSpentAmount() : BigDecimal.ZERO)
                                .add(spendAmt));
                        budgetLedgerRepository.save(l);
                    });

            System.out.println("✅ [GRN BUDGET] " + budgetCode
                    + " Hold↓ Spent↑ by " + spendAmt
                    + " (GRN: " + grnSubProcessId + ", PO: " + poId + ")");
        }

    } catch (Exception e) {
        System.err.println("❌ [GRN BUDGET] convertHoldToSpentOnGrn failed: " + e.getMessage());
        e.printStackTrace();
    }
}
// ─── GI APPROVAL: Permanent Rejection → Release Hold ─────────────────────────

@Override
@Transactional
public void releaseHoldForPermanentRejection(Integer inspectionSubProcessId, String poId) {
    try {
        List<GiMaterialDtlEntity> giMaterials = giMaterialDtlRepository
                .findByInspectionSubProcessId(inspectionSubProcessId);
        List<GoodsInspectionConsumableDetailEntity> giConsumables = goodsInspectionConsumableDetailRepository
                .findByInspectionSubProcessId(inspectionSubProcessId);

        Map<String, BigDecimal> releasePerBudget = new HashMap<>();

        // Non-consumables
        for (GiMaterialDtlEntity line : giMaterials) {
            if (!"permanent".equalsIgnoreCase(line.getRejectionType())) continue;

            BigDecimal rejectedQty = line.getRejectedQuantity() != null
                    ? line.getRejectedQuantity() : BigDecimal.ZERO;
            if (rejectedQty.compareTo(BigDecimal.ZERO) == 0) continue;

            PurchaseOrderAttributes poa = purchaseOrderAttributesRepository
                    .findByPurchaseOrder_PoIdAndMaterialCode(poId, line.getMaterialCode())
                    .orElse(null);
            if (poa == null || poa.getBudgetCode() == null) continue;

            // BigDecimal rate = poa.getRate() != null ? poa.getRate() : BigDecimal.ZERO;
            // BigDecimal exchangeRate = (poa.getExchangeRate() != null
            //         && poa.getCurrency() != null
            //         && !"INR".equalsIgnoreCase(poa.getCurrency()))
            //         ? poa.getExchangeRate() : BigDecimal.ONE;

            // BigDecimal releaseAmount = rejectedQty.multiply(rate).multiply(exchangeRate);
            BigDecimal releaseAmount = calculateRejectedLineAmount(rejectedQty, poa);
            releasePerBudget.merge(poa.getBudgetCode(), releaseAmount, BigDecimal::add);
        }

        // Consumables
        for (GoodsInspectionConsumableDetailEntity line : giConsumables) {
            if (!"permanent".equalsIgnoreCase(line.getRejectionType())) continue;

            BigDecimal rejectedQty = line.getRejectedQuantity() != null
                    ? line.getRejectedQuantity() : BigDecimal.ZERO;
            if (rejectedQty.compareTo(BigDecimal.ZERO) == 0) continue;

            PurchaseOrderAttributes poa = purchaseOrderAttributesRepository
                    .findByPurchaseOrder_PoIdAndMaterialCode(poId, line.getMaterialCode())
                    .orElse(null);
            if (poa == null || poa.getBudgetCode() == null) continue;

            // BigDecimal rate = poa.getRate() != null ? poa.getRate() : BigDecimal.ZERO;
            // BigDecimal exchangeRate = (poa.getExchangeRate() != null
            //         && poa.getCurrency() != null
            //         && !"INR".equalsIgnoreCase(poa.getCurrency()))
            //         ? poa.getExchangeRate() : BigDecimal.ONE;

            // BigDecimal releaseAmount = rejectedQty.multiply(rate).multiply(exchangeRate);
            BigDecimal releaseAmount = calculateRejectedLineAmount(rejectedQty, poa);
            releasePerBudget.merge(poa.getBudgetCode(), releaseAmount, BigDecimal::add);
        }

        // Release from PO hold → Available
        for (Map.Entry<String, BigDecimal> entry : releasePerBudget.entrySet()) {
            String budgetCode = entry.getKey();
            BigDecimal releaseAmount = entry.getValue();

            BudgetMaster budget = budgetMasterRepository.findByBudgetCode(budgetCode).orElse(null);
            if (budget == null) continue;

            budget.setOnHoldAmount(
                    budget.getOnHoldAmount().subtract(releaseAmount).max(BigDecimal.ZERO));
            budgetMasterRepository.save(budget);

            // Reduce PO ledger hold
            List<BudgetLedger> poLedgers = budgetLedgerRepository
                    .findByReferenceIdAndTypeAndStatus(poId, "PO", "ACTIVE_HOLD");
            poLedgers.stream()
                    .filter(l -> l.getBudgetCode().equals(budgetCode))
                    .forEach(l -> {
                        l.setHoldAmount(l.getHoldAmount().subtract(releaseAmount).max(BigDecimal.ZERO));
                        budgetLedgerRepository.save(l);
                    });

            System.out.println("✅ [GI PERMANENT REJECTION] Budget " + budgetCode
                    + " → Hold released by " + releaseAmount
                    + " (GI subProcessId: " + inspectionSubProcessId + ")");
        }
    } catch (Exception e) {
        System.err.println("❌ [GI BUDGET] Failed to release hold for permanent rejection: " + e.getMessage());
    }
}
}
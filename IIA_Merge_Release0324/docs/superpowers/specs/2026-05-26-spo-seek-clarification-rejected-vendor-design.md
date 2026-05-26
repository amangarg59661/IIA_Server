# SPO Seek Clarification from Indentor on Rejected Vendors (Under 10 Lakh)

## Problem

When an indentor rejects a vendor during tender evaluation (under 10 lakh), the SPO cannot seek clarification or revision from the indentor for that specific vendor. The per-row "Seek Clarification" button in SPO actions only targets VENDOR, not INDENTOR. The system should allow SPO to ask the indentor to reconsider or explain their rejection.

## Solution: Frontend + Backend (Approach B)

### Frontend Change

**File:** `Frontend-test/src/pages/dashboard/tenderEvaluation/TenderEvaluationPage.jsx`

**Per-row SPO "Seek Clarification" button (~line 807-821):**

- Change `setClarTarget("VENDOR")` to `setClarTarget(null)` so SPO must pick a target from the dropdown (VENDOR or INDENTOR).
- `clarVendorId` is already pre-filled from the row.
- The clarification dialog already renders VENDOR and INDENTOR options for SPO role (lines 1397-1398).
- Add validation in `handleSeekClarification`: block submit if `clarTarget` is null.

### Backend Change

**File:** `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java`

**In `seekClarification()` method (~line 737), add vendor indentorStatus reset logic:**

When all of the following are true:
- `requestedByRole` is SPO
- `clarificationTarget` resolves to INDENTOR
- `targetVendorId` is provided (not null/blank)

Then:
1. Find vendor quotation via `findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, targetVendorId)`
2. If `eval.financialBidPhase` is true: reset `financialIndentorStatus` and `financialIndentorRemarks` to `null`
3. Else: reset `indentorStatus` and `indentorRemarks` to `null`
4. Set quotation `status` to `"CHANGE_REQUESTED"`
5. Save quotation

Existing logic already handles:
- Setting `eval.evaluationStatus` to `"PENDING_INDENTOR_CLARIFICATION"` (switch-case for INDENTOR target)
- Saving `previousEvaluationStatus` (preserves `PENDING_SPO_APPROVAL` for auto-return)
- Recording clarification history with `targetVendorId`

**No changes to `respondToClarification()`** — it already restores `previousEvaluationStatus` when indentor responds.

### Workflow

```
SPO sees vendor rejected by indentor
  -> clicks per-row "Seek Clarification"
  -> picks "To Indentor" from dropdown (vendor pre-filled)
  -> submits

Backend:
  -> resets vendor indentorStatus to null
  -> sets quotation status = CHANGE_REQUESTED
  -> sets eval status = PENDING_INDENTOR_CLARIFICATION
  -> saves previousEvaluationStatus = PENDING_SPO_APPROVAL
  -> saves clarification history (with targetVendorId)

Indentor:
  -> sees clarification request
  -> responds via respondToClarification endpoint
  -> eval auto-returns to PENDING_SPO_APPROVAL
  -> re-decides on vendor via saveVendorIndentorDecision (accept or reject again)
```

### Edge Cases

| Scenario | Behavior |
|---|---|
| SPO targets INDENTOR without vendorId | General clarification, no indentorStatus reset |
| SPO targets INDENTOR for already-accepted vendor | Sends clarification, resets indentorStatus to null, indentor must re-decide |
| Multiple rejected vendors | Each per-row clarification is independent |
| SPO targets VENDOR (existing flow) | No change, works as before |
| Financial bid phase | Same behavior applies to financialIndentorStatus if in financial phase |

### Files Modified

1. `Frontend-test/src/pages/dashboard/tenderEvaluation/TenderEvaluationPage.jsx` — per-row button target + validation
2. `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java` — indentorStatus reset in seekClarification()

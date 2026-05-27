# Clarification History Response Mapping Fix

**Date:** 2026-05-27
**Status:** Approved

## Problem

`TenderClarificationHistory` stores one row per clarification round. Multiple open rows (`respondedAt=null`) can exist for the same `tenderId + targetVendorId` — e.g., an active ALL_VENDORS clarification AND a specific VENDOR clarification simultaneously.

`respondToClarification()` uses `findFirst()` on a stream filtered only by `respondedAt == null`. This hits an arbitrary row (newest by `requestedAtDesc`) regardless of clarification target type. Result: wrong history row gets marked as responded.

Additionally, the "else" branch (indentor/member responses) restores evaluation status immediately without checking whether other clarification rows are still open — causing premature status restoration when multiple concurrent clarifications exist.

## Decisions

1. **ALL_VENDORS rows excluded from GET endpoint** — they have `targetVendorId=null`, stay audit-only. Vendors see ALL_VENDORS clarifications via their quotation `CHANGE_REQUESTED` status.
2. **Eval status restore gated on ALL open rows** — not just vendor flows. Indentor/member responses no longer restore immediately if other rows are still open.
3. **Approach B selected** — user's proposal + safer target-type-filtered fallback + DB-level repository queries.

## Scope

### Files Changed

| File | Change |
|------|--------|
| `RespondClarificationDto.java` | Add `clarificationHistoryId` field (Long, nullable) |
| `TenderClarificationHistoryRepository.java` | Add 2 query methods |
| `TenderEvaluationApprovalService.java` | Add `getOpenClarifications()` method signature |
| `TenderEvaluationApprovalServiceImpl.java` | Implement `getOpenClarifications()`, modify `respondToClarification()` |
| `TenderEvaluationController.java` | Add GET `/open-clarifications` endpoint |

### Files NOT Changed

- `seekClarification()` method
- `TenderClarificationHistory` entity
- `getClarificationHistory()` method
- `SeekClarificationDto`
- Round numbering logic
- Any other method in `TenderEvaluationApprovalServiceImpl`

## Design

### 1. DTO Change — RespondClarificationDto

Add one field:

```java
private Long clarificationHistoryId;
```

Type is `Long` to match entity `id` field. Nullable for backward compatibility — when null, falls back to filtered lookup.

### 2. Repository Changes — TenderClarificationHistoryRepository

Add 2 Spring Data derived query methods:

```java
List<TenderClarificationHistory> findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(
    String tenderId, String targetVendorId);

long countByTenderIdAndRespondedAtIsNull(String tenderId);
```

No `@Query` annotation needed — Spring Data derives from method names.

### 3. GET Endpoint — Open Clarifications Per Vendor

**Endpoint:** `GET /api/v1/tenders/evaluation/open-clarifications?tenderId=X&vendorId=Y`

**Logic:**
1. Call `findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(tenderId, vendorId)`
2. Return list of objects with: `clarificationHistoryId`, `roundNumber`, `questionRemarks`, `requestedByRole`, `requestedAt`, `clarificationTarget`

**Response shape:**
```json
[
  {
    "clarificationHistoryId": 42,
    "roundNumber": 3,
    "questionRemarks": "Please clarify delivery timeline",
    "requestedByRole": "SPO",
    "requestedAt": "2026-05-27T10:30:00",
    "clarificationTarget": "VENDOR"
  }
]
```

ALL_VENDORS rows (targetVendorId=null) are naturally excluded by the query filter.

### 4. respondToClarification() Changes

Three branches (VENDOR, PP-per-vendor, else) all follow the same pattern:

**Step 1 — Resolve history row:**

```
IF dto.clarificationHistoryId != null:
    row = repo.findById(clarificationHistoryId)
    validate: row exists, belongs to this tenderId, respondedAt is null
ELSE (fallback with target-type filtering):
    VENDOR response    → filter target IN (VENDOR, ALL_VENDORS)
                         AND (vendorId matches OR targetVendorId is null)
    PP per-vendor      → filter target = PURCHASE_PERSONNEL
                         AND vendorId matches
    Else (indentor/member) → filter target IN (INDENTOR, CHAIRMAN,
                         PURCHASE_PERSONNEL, SPECIFIC_MEMBER, ALL_MEMBERS)
    Then .findFirst()
```

**Step 2 — Update row** (unchanged from current):
Set `responseText`, `responseFileName`, `respondedByRole`, `respondedById`, `respondedAt`.

**Step 3 — Unified restore gate** (replaces per-branch restore logic):

```
quotationsResolved = no quotations with status CHANGE_REQUESTED
openHistoryRows = countByTenderIdAndRespondedAtIsNull(tenderId)

if (!quotationsResolved || openHistoryRows > 0):
    save eval, return early (still pending)

// All resolved — restore previousEvaluationStatus
```

Key behavioral change: the else branch (indentor/member) no longer restores immediately. It goes through the same gate as vendor flows.

### 5. Edge Case: Concurrent ALL_VENDORS + Specific VENDOR

Scenario:
1. SPO seeks ALL_VENDORS clarification → 1 history row (target=ALL_VENDORS, targetVendorId=null), all quotations marked CHANGE_REQUESTED
2. SPO seeks VENDOR V1 clarification → 1 history row (target=VENDOR, targetVendorId=V1)
3. Both rows open simultaneously

Resolution:
- GET endpoint for V1 returns only the VENDOR-specific row (targetVendorId=V1)
- V1 responds to VENDOR row by `clarificationHistoryId` → that row gets respondedAt
- V1 responds to ALL_VENDORS via quotation update → quotation status changes to CHANGE_RESPONDED
- ALL_VENDORS history row: existing `findFirst()` fallback (no clarificationHistoryId) matches it when a vendor responds without ID
- Restore gate: checks both quotation statuses AND history respondedAt=null count — only restores when ALL are done

### 6. Backward Compatibility

- `clarificationHistoryId = null` in DTO → fallback to filtered findFirst() (existing behavior, but now with target-type filtering)
- Old frontend versions that don't send clarificationHistoryId continue to work
- No entity schema changes — no DB migration needed
- GET endpoint is additive — no existing endpoint modified

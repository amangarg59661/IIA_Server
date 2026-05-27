# Above-10L Double Bid: Dual Table + Per-Vendor Committee Voting

**Date:** 2026-05-27  
**Scope:** Backend + Frontend — new entity, new endpoints, dual-table extension, expert endpoint migration

## Problem

1. **Double bid dual tables only render for under-10L.** The `showTechActionButtons` and `showFinActionButtons` flags gate on `isBelow10L`. Above-10L double bid tenders don't show separate tech/financial tables.

2. **Committee voting is evaluation-level, not vendor-level.** `TenderCommitteeDecision` stores one vote per member per tender. For above-10L double bid, each committee member should independently Accept/Reject each vendor's bid, with chairman resolving conflicts.

3. **Expert endpoint duplicates nominate logic.** `POST /committee/expert` does a subset of what `POST /nominate` does, with weaker validation. Expert endpoint is now commented out; frontend needs to call nominate with `expert=true`.

## Solution

### Part 1: Frontend Expert → Nominate Migration

Update all frontend calls from `POST /api/tender-evaluation/committee/expert` to `POST /api/admin/techno-financial-committee/nominate` with `expert: true`.

**File:** `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluator.jsx`

Find the expert assignment handler (calls `/api/tender-evaluation/committee/expert`) and replace with:
```js
await axios.post('/api/admin/techno-financial-committee/nominate', {
  tenderId,
  userId: expertUserId,
  nominatedBy: chairmanUserId,
  expert: true,
});
```

### Part 2: New Entity — TenderCommitteeVendorDecision

```sql
CREATE TABLE tender_committee_vendor_decision (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    tender_id       VARCHAR(50) NOT NULL,
    vendor_id       VARCHAR(50) NOT NULL,
    committee_user_id INTEGER NOT NULL,
    member_name     VARCHAR(255),
    decision        VARCHAR(20),          -- ACCEPTED / REJECTED / null (pending)
    remarks         VARCHAR(1000),
    phase           VARCHAR(20) NOT NULL,  -- TECHNICAL / FINANCIAL
    decision_date   DATETIME,
    created_date    DATETIME NOT NULL,
    updated_date    DATETIME,
    UNIQUE KEY uk_tender_vendor_member_phase (tender_id, vendor_id, committee_user_id, phase)
);
```

**JPA Entity:** `com.astro.entity.TenderCommitteeVendorDecision`
- Standard Lombok `@Data`, `@Entity`, `@Table`
- Unique constraint on (tenderId, vendorId, committeeUserId, phase)

**Repository:** `com.astro.repository.ProcurementModule.TenderCommitteeVendorDecisionRepository`
- `findByTenderIdAndVendorIdAndPhase(String tenderId, String vendorId, String phase)` — all member votes for a vendor in a phase
- `findByTenderIdAndCommitteeUserIdAndPhase(String tenderId, Integer userId, String phase)` — all vendor decisions by a member
- `findByTenderIdAndPhase(String tenderId, String phase)` — all votes for a tender in a phase
- `deleteByTenderIdAndPhase(String tenderId, String phase)` — reset votes (used when director resets financial phase)

### Part 3: New Backend Endpoints

All under `TenderEvaluationController` at `/api/tender-evaluation`.

#### 3a. Committee Member Per-Vendor Decision

```
POST /committee/vendor-decision?tenderId={id}
Body: { vendorId, decision, remarks, committeeUserId }
```

**Logic:**
1. Validate tender eval exists, status is `PENDING_TECHNICAL` or `PENDING_FINANCIAL`
2. Validate caller is STEC member for this tender (has `TenderCommitteeDecision` row)
3. Determine phase from `eval.financialBidPhase` (false → TECHNICAL, true → FINANCIAL)
4. Upsert `TenderCommitteeVendorDecision` row (member can change their vote until chairman resolves)
5. Return updated status DTO

**Locked statuses:** Same as `saveVendorIndentorDecision` — PENDING_SPO_APPROVAL, APPROVED, REJECTED, PENDING_DIRECTOR_APPROVAL, PENDING_COMMITTEE_FORMATION

#### 3b. Get Vendor Vote Grid

```
GET /committee/vendor-votes?tenderId={id}&phase={TECHNICAL|FINANCIAL}
```

**Returns:** Map of vendorId → list of { userId, memberName, decision, remarks, decisionDate }

Used by chairman UI to see per-vendor vote breakdown.

#### 3c. Chairman Per-Vendor Resolution

```
POST /committee/chairman-vendor-resolve?tenderId={id}
Body: { vendorId, decision, remarks, chairmanUserId }
```

**Logic:**
1. Validate caller is STEC-I/STEC-II Chairman (same validation as `chairmanDecide`)
2. Determine phase from `eval.financialBidPhase`
3. Write to `VendorQuotationAgainstTender`:
   - TECHNICAL phase: sets `indentorStatus` and `indentorRemarks`
   - FINANCIAL phase: sets `financialIndentorStatus` and `financialIndentorRemarks`
4. Record chairman's resolution in `TenderCommitteeVendorDecision` too (for audit)
5. If ALL vendors resolved in current phase:
   - TECHNICAL phase: status → `PENDING_DIRECTOR_APPROVAL`
   - FINANCIAL phase: status → `PENDING_DIRECTOR_APPROVAL`
6. Return updated status DTO

### Part 4: Modified Initiation (Above-10L Double Bid)

In `initiateTenderEvaluation`, after creating STEC vote rows, also **pre-create** `TenderCommitteeVendorDecision` rows for each (member × vendor × phase=TECHNICAL). This gives the frontend a complete grid from the start.

Financial phase rows are created later when director unlocks financial bids (or when chairman resolves all technical bids and status moves to PENDING_FINANCIAL).

### Part 5: Director Financial Phase Reset

In `directorApprove` for double bid (when director approves technical phase and unlocks financial bids), add:
- Clear existing `TenderCommitteeVendorDecision` rows for FINANCIAL phase (if any stale ones)
- Pre-create FINANCIAL phase rows for each (member × technically-approved vendor)

### Part 6: Frontend Dual Table Extension

**File:** `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluator.jsx`

#### 6a. Render dual tables for above-10L

Remove `isBelow10L` gate from dual table render block. The `{isDoubleBidEval && (...)}` wrapper already handles single vs double bid. Just need the ACTION buttons to also show for committee members.

#### 6b. New control flags

```js
// Committee member can vote on vendors (above-10L double bid)
const showCommitteeTechActions = isDoubleBidEval && !isFinancialPhase &&
  isAbove10L && isCommitteeMember && isVotingMember &&
  (evalStatus?.evaluationStatus === 'PENDING_TECHNICAL');

const showCommitteeFinActions = isDoubleBidEval && isFinancialPhase &&
  isAbove10L && isCommitteeMember && isVotingMember &&
  (evalStatus?.evaluationStatus === 'PENDING_FINANCIAL');

// Chairman can resolve vendor decisions (above-10L double bid)
const showChairmanTechResolve = isDoubleBidEval && !isFinancialPhase &&
  isAbove10L && isChairman &&
  (evalStatus?.evaluationStatus === 'PENDING_TECHNICAL');

const showChairmanFinResolve = isDoubleBidEval && isFinancialPhase &&
  isAbove10L && isChairman &&
  (evalStatus?.evaluationStatus === 'PENDING_FINANCIAL');
```

#### 6c. Column definitions for above-10L

**Committee member tech columns:** Same as `doubleBidTechColumns` but:
- Action column calls `POST /committee/vendor-decision` instead of `/vendor/indentor-decision`
- Shows member's own current vote as Tag

**Chairman tech columns:** Same base columns plus:
- Vote summary column: "3 Accept / 1 Reject" badge per vendor
- Expandable row or tooltip showing each member's vote
- Resolve button (Accept/Reject) per vendor

#### 6d. Status DTO extension

`TenderEvaluationStatusDto` needs:
- `committeeVendorVotes: Map<String, List<CommitteeVendorVoteDto>>` — keyed by vendorId
- Each `CommitteeVendorVoteDto`: userId, memberName, decision, remarks

This is populated in `buildStatusDto` for above-10L tenders, by querying `TenderCommitteeVendorDecisionRepository`.

### Visibility Matrix (Above-10L Double Bid)

| Status                              | Tech Table                  | Financial Table              |
|-------------------------------------|-----------------------------|------------------------------|
| PENDING_TECHNICAL                   | Committee: vote buttons     | Hidden                       |
|                                     | Chairman: resolve buttons   |                              |
| PENDING_DIRECTOR_APPROVAL (tech)    | Read-only (resolved)        | Hidden                       |
| PENDING_FINANCIAL                   | Read-only (resolved)        | Committee: vote buttons      |
|                                     |                             | Chairman: resolve buttons    |
| PENDING_DIRECTOR_APPROVAL (fin)     | Read-only                   | Read-only (resolved)         |
| APPROVED                            | Read-only                   | Read-only                    |
| REJECTED                            | Read-only                   | Read-only                    |

### Flow Summary (Above-10L Double Bid)

```
Initiate (PP)
  → PENDING_TECHNICAL
  → Committee members: Accept/Reject each vendor's tech bid
  → Chairman: sees vote grid, resolves each vendor
  → All tech vendors resolved → PENDING_DIRECTOR_APPROVAL
  → Director approves technical phase → PENDING_FINANCIAL (unlocks financial bids, resets committee vendor votes)
  → Committee members: Accept/Reject each vendor's financial bid
  → Chairman: resolves each vendor
  → All financial vendors resolved → PENDING_DIRECTOR_APPROVAL
  → Director approves financial phase → APPROVED
```

Director approval happens TWICE for double bid: once after tech phase, once after financial phase. The existing `directorApprove` method already handles this two-pass pattern — it checks `financialBidPhase` to decide whether to unlock financials or finalize.

### Single Bid Above-10L — No Change

Single bid above-10L keeps current evaluation-level committee vote (`castCommitteeVote` → `chairmanDecide` → `directorApprove`). Per-vendor voting applies only to double bid.

## Files Changed

| File | Change |
|------|--------|
| `CommitteeNominationDto.java` | Already done — `expert` field added |
| `TechnoFinancialCommitteeServiceImpl.java` | Already done — expert audit trail in nominate |
| `TenderEvaluationController.java` | Already done (expert commented out) + 3 new endpoints |
| `TenderEvaluationApprovalService.java` | Already done (expert commented out) + 3 new method signatures |
| `TenderEvaluationApprovalServiceImpl.java` | Already done (expert commented out) + 3 new methods + initiation change + director reset change |
| `TenderCommitteeVendorDecision.java` | **New** — JPA entity |
| `TenderCommitteeVendorDecisionRepository.java` | **New** — Spring Data repository |
| `CommitteeVendorVoteDto.java` | **New** — DTO for vote grid |
| `TenderEvaluationStatusDto.java` | Add `committeeVendorVotes` field |
| `TenderEvaluator.jsx` | Expert → nominate migration + dual table for above-10L + committee vote columns + chairman resolve columns |
| `DDL migration SQL` | **New** — CREATE TABLE tender_committee_vendor_decision |

## Testing Checklist

- [ ] Under-10L single bid: no behavior change
- [ ] Under-10L double bid: dual tables still work as before
- [ ] Above-10L single bid: evaluation-level committee vote still works
- [ ] Above-10L double bid tech phase: committee members see Accept/Reject per vendor
- [ ] Above-10L double bid tech phase: chairman sees vote grid per vendor
- [ ] Above-10L double bid tech phase: chairman resolves per vendor → writes to indentorStatus
- [ ] Above-10L double bid: all tech vendors resolved → PENDING_FINANCIAL
- [ ] Above-10L double bid financial phase: committee votes on financial bids
- [ ] Above-10L double bid financial phase: chairman resolves → PENDING_DIRECTOR_APPROVAL
- [ ] Director approves → APPROVED
- [ ] Expert nomination via nominate endpoint with expert=true works
- [ ] Old expert endpoint returns 404 (commented out)
- [ ] Clarification flow still works for above-10L double bid
- [ ] Committee vote reset on director financial phase unlock

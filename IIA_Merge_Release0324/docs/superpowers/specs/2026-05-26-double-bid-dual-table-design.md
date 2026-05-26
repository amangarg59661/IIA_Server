# Double Bid Dual Table Design

**Date:** 2026-05-26  
**Scope:** Frontend-test — `TenderEvaluator.jsx` only  
**Constraint:** Single bid path is untouched. All changes gated by `isDoubleBidEval === true`.

## Problem

Double bid evaluation uses a single vendor table with `isFinancialPhase` ternaries throughout columns, action handlers, and disabled conditions. This causes:
1. Seek clarification during financial phase modifies technical round status
2. Accept/Reject disabled conditions mix technical and financial state
3. Financial document column was missing entirely for double bid
4. `handleSpoReview` for `CHANGE_REQUEST_TO_INTENTOR` didn't pass `targetVendorId`

Root cause: every column render, every disabled check, and every handler must remember to branch on `isFinancialPhase`. One missed ternary = wrong-phase data mutation.

## Solution

Split the vendor table into two independent tables for double bid only:
- **Technical Bid Table** — all vendors, technical status fields, technical actions
- **Financial Bid Table** — only technically approved vendors, financial status fields, financial actions

Single bid continues using the existing single-table flow unchanged.

## Architecture

### Data Flow

```
quotationData (all vendors)
│
├── [Single Bid] → existing single table (NO CHANGES)
│
├── [Double Bid] Technical Bid Table
│   ├── Data: quotationData (all vendors)
│   ├── Columns: vendorId, name, techDoc, vendorResponse, clarificationFile,
│   │            indentorStatus, sopStatus
│   ├── Actions: Accept, Reject, Seek Clarification (technical phase only)
│   └── Read-only when isFinancialPhase === true
│
└── [Double Bid] Financial Bid Table
    ├── Data: quotationData.filter(q =>
    │     q.indentorStatus === 'ACCEPTED' && q.sopStatus === 'ACCEPTED')
    ├── Columns: vendorId, name, financialDoc, financialIndentorStatus, financialSpoStatus
    ├── Actions: Accept, Reject, Seek Clarification (financial phase only)
    └── Hidden until isFinancialPhase === true
```

### Visibility Matrix

| Eval Status                      | Technical Table       | Financial Table        |
|----------------------------------|-----------------------|------------------------|
| PENDING_TECHNICAL                | Actions enabled       | Hidden                 |
| PENDING_SPO_APPROVAL (tech)      | SPO actions           | Hidden                 |
| PENDING_FINANCIAL_SHEET_UPLOAD   | Read-only             | Hidden                 |
| PENDING_FINANCIAL                | Read-only             | Actions enabled        |
| PENDING_SPO_APPROVAL (fin)       | Read-only             | SPO actions            |
| PENDING_VENDOR_CLARIFICATION     | Context-dependent     | Context-dependent      |
| PENDING_INDENTOR_CLARIFICATION   | Context-dependent     | Context-dependent      |
| APPROVED                         | Read-only             | Read-only              |
| REJECTED                         | Read-only             | Read-only              |

### Column Definitions (Double Bid Only)

**`doubleBidTechColumns`** — independent array, no `isFinancialPhase` ternary:
- Vendor ID, Vendor Name, Technical Document
- Vendor Response, Clarification File (conditional)
- Indentor Status (`indentorStatus`)
- SPO Status (`sopStatus`)
- Qualification Status (derived from `sopStatus`)
- Accept/Reject/Seek Clarification (only when `showTechActionButtons`)
- SPO Accept/Reject/Seek Revision (only when `showSpoTechActions`)

**`doubleBidFinColumns`** — independent array, no `isFinancialPhase` ternary:
- Vendor ID, Vendor Name, Financial Document (`priceBidFileName`)
- Financial Indentor Status (`financialIndentorStatus`)
- Financial SPO Status (`financialSpoStatus`)
- Accept/Reject/Seek Clarification (only when `showFinActionButtons`)
- SPO Accept/Reject/Seek Revision (only when `showSpoFinActions`)

### Shared Base Columns

To avoid duplication, define a helper:
```js
const vendorInfoColumns = [
  { title: 'Vendor ID', dataIndex: 'vendorId', ... },
  { title: 'Vendor Name', dataIndex: 'vendorName', ... },
];
```

Each table spreads `vendorInfoColumns` then adds its own phase-specific columns.

### Control Flags (Double Bid Only)

```js
// Technical phase: actions allowed
const showTechActionButtons = isDoubleBidEval && !isFinancialPhase &&
  ((isIndentCreatorRole && isBelow10L && !isMultipleIndentEval) ||
   (isPurchasePersonnelRole && isBelow10L && isMultipleIndentEval)) &&
  evalStatus !== null;

// Financial phase: actions allowed
const showFinActionButtons = isDoubleBidEval && isFinancialPhase &&
  evalStatus?.evaluationStatus === 'PENDING_FINANCIAL' &&
  (isIndentCreatorRole || isPurchasePersonnelRole);

// SPO technical
const showSpoTechActions = isDoubleBidEval && !isFinancialPhase &&
  isSpoRole && evalStatus?.evaluationStatus === 'PENDING_SPO_APPROVAL';

// SPO financial
const showSpoFinActions = isDoubleBidEval && isFinancialPhase &&
  isSpoRole && evalStatus?.evaluationStatus === 'PENDING_SPO_APPROVAL';
```

### Handler Changes

Current handlers use `isFinancialPhase` internally. For double bid, each table passes an explicit phase:

```js
// Accept — called from table onClick
const handleAcceptDoubleBid = async (record, phase) => {
  // phase = 'TECHNICAL' or 'FINANCIAL'
  // Calls same endpoint — backend uses eval.financialBidPhase to route
  // No isFinancialPhase ternary needed in handler
  await axios.post('/api/tender-evaluation/vendor/indentor-decision',
    { decision: "ACCEPTED", remarks: "Accepted", userId },
    { params: { tenderId, vendorId: record.vendorId } }
  );
};
```

**Note:** Backend already routes via `eval.financialBidPhase`. Frontend handlers don't need to pass phase to backend — they just need to avoid mixing UI state between tables. The explicit phase param is for clarity and future-proofing, not a backend change.

However, `handleReject` and seek clarification handlers DO use `rejectComment` state which is shared. Each table's popover uses the same `rejectComment`/`rejectedVendorId` state — this is fine since only one popover is open at a time.

### Computed State (Double Bid Only)

```js
// Technical: all vendors decided
const allVendorsTechDecided = quotationData.length > 0 &&
  quotationData.every(q =>
    q.indentorStatus === 'ACCEPTED' || q.indentorStatus === 'REJECTED'
  );

// Financial: all tech-approved vendors decided
const financialVendors = quotationData.filter(q =>
  q.indentorStatus === 'ACCEPTED' && q.sopStatus === 'ACCEPTED'
);
const allVendorsFinDecided = financialVendors.length > 0 &&
  financialVendors.every(q =>
    q.financialIndentorStatus === 'ACCEPTED' || q.financialIndentorStatus === 'REJECTED'
  );

// SPO: all tech-approved vendors with SPO tech decision
const allVendorsSpoTechDecided = quotationData.length > 0 &&
  quotationData
    .filter(q => q.indentorStatus === 'ACCEPTED')
    .every(q => q.sopStatus === 'ACCEPTED' || q.sopStatus === 'REJECTED');

// SPO: all fin-approved vendors with SPO fin decision
const allVendorsSpoFinDecided = financialVendors.length > 0 &&
  financialVendors
    .filter(q => q.financialIndentorStatus === 'ACCEPTED')
    .every(q => q.financialSpoStatus === 'ACCEPTED' || q.financialSpoStatus === 'REJECTED');
```

### Render Structure (Double Bid)

```jsx
{isDoubleBidEval && (
  <>
    {/* Technical Bid Table — always visible for double bid */}
    <Card
      title={
        <span>
          Technical Bid Evaluation
          {isFinancialPhase && <Tag color="green" style={{ marginLeft: 8 }}>Completed</Tag>}
        </span>
      }
      size="small"
      style={{ marginTop: 16 }}
    >
      <Table
        dataSource={quotationData}
        columns={isSpoRole ? spoTechColumns : doubleBidTechColumns}
        rowKey="vendorId"
        size="small"
        bordered
        pagination={false}
      />
    </Card>

    {/* Financial Bid Table — only when financial phase active */}
    {isFinancialPhase && (
      <Card title="Financial Bid Evaluation" size="small" style={{ marginTop: 16 }}>
        <Table
          dataSource={financialVendors}
          columns={isSpoRole ? spoFinColumns : doubleBidFinColumns}
          rowKey="vendorId"
          size="small"
          bordered
          pagination={false}
        />
      </Card>
    )}
  </>
)}
```

### Single Bid — NO CHANGES

The existing table render, column definitions, and handlers for single bid remain completely untouched. The dual-table code is wrapped in `{isDoubleBidEval && (...)}` and the existing single-table code gets wrapped in `{!isDoubleBidEval && (...)}`.

### Confirm Button Logic

For double bid, the confirm buttons also need phase-aware computed state:

- **Indentor/PP Confirm (tech phase):** enabled when `allVendorsTechDecided && !anyVendorPendingClarif`
- **Indentor/PP Confirm (fin phase):** enabled when `allVendorsFinDecided && !anyVendorPendingClarif`
- **SPO Confirm (tech phase):** enabled when `allVendorsSpoTechDecided`
- **SPO Confirm (fin phase):** enabled when `allVendorsSpoFinDecided`

Existing confirm buttons for single bid stay unchanged.

## Files Changed

| File | Change |
|------|--------|
| `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluator.jsx` | Add dual table columns, control flags, render split, computed state |

## No Backend Changes

Backend already handles financial phase via `eval.getFinancialBidPhase()` in:
- `saveVendorIndentorDecision` — routes to `financialIndentorStatus` when financial phase
- `saveVendorSpoDecision` — routes to `financialSpoStatus` when financial phase
- `seekClarification` — resets correct phase status when `targetVendorId` is provided

## Testing Checklist

- [ ] Single bid: verify no behavior change (accept, reject, seek clarification, confirm)
- [ ] Double bid technical phase: technical table shows actions, financial table hidden
- [ ] Double bid financial phase: technical table read-only, financial table shows only tech-approved vendors
- [ ] Double bid financial table: accept/reject updates `financialIndentorStatus` not `indentorStatus`
- [ ] SPO seek revision from technical table: targets technical status
- [ ] SPO seek revision from financial table: targets financial status
- [ ] Seek clarification during financial phase: does NOT modify technical data
- [ ] Confirm evaluation: uses correct phase-specific decided checks
- [ ] Financial document visible in financial table during financial phase
- [ ] Financial document hidden in technical table (or shows "Hidden (Technical phase)")

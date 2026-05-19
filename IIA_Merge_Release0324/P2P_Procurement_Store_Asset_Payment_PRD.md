# Product Requirements Document
## Procurement, Store, Asset & Payment Management System

## 1. Purpose

This system is an internal P2P procurement and inventory management platform for managing the complete lifecycle of:

- Vendor registration and tender participation
- Material/job master creation
- Indent creation and approval
- Tender creation and approval
- Tender evaluation
- PO/SO generation and approval
- GPRN, inspection, goods return and GRN
- Asset generation and assignment
- Stock management
- Goods transfer
- Demand and issue
- Asset disposal
- Contingency purchase
- Payment voucher generation
- Tally integration

The system will run on a **private internal network**. Both frontend and backend will be deployed inside the closed network, with no external public access.

The system must be **modular, production-grade, maintainable, role-based, auditable, and fully manageable from frontend forms and admin panels**. End users should not need to modify code to configure business rules, approval workflows, limits, roles, masters, or operational settings.

---

## 2. System Architecture

### 2.1 Application Structure

The system consists of:

| Component | Technology | Purpose |
|---|---|---|
| Frontend 1 | React | Vendor Portal |
| Frontend 2 | React | Internal Procurement / Store / Asset / Payment Portal |
| Backend | Spring Boot | Core business APIs, workflow, validation, persistence |
| Database | MySQL | Transactional and master data storage |

---

## 3. Key Design Principles

### 3.1 Internal Network Deployment

The system is designed for private network usage. Therefore:

- No public-facing assumptions should be made.
- No external dependency should be required for core operations.
- Strict public-facing CORS controls are not required unless needed for deployment compatibility.
- Authentication and authorization are still required because internal users have different roles and responsibilities.
- Audit logs are mandatory for important business events.

### 3.2 Fully Frontend-Moderated System

All operational and administrative actions must be available through frontend screens/forms.

Users should not need to modify backend/frontend code for:

- Approval workflow configuration
- Role assignment
- Purchase limits
- Contingency purchase limit
- Material/job master data
- Vendor data
- Tender creation
- PO/SO generation
- GRN/GPRN/GI transactions
- Asset disposal
- Demand and issue
- Payment voucher creation
- System configuration

### 3.3 Modular Backend

The backend should be divided into functional modules, for example:

- User & Role Management
- Vendor Management
- Material/Job Master
- Approval Workflow
- Indent Management
- Tender Management
- Tender Evaluation
- PO/SO Management
- GPRN Management
- Goods Inspection
- Goods Return
- GRN & Stock Posting
- Asset Management
- Goods Transfer
- Demand & Issue
- Asset Disposal
- Contingency Purchase
- Payment Voucher
- Tally Integration
- Reports & Audit Logs
- Admin Configuration

Each module should have clear responsibilities and should not be tightly coupled to unrelated modules.

---

## 4. Roles

The system should support role-based access control.

### 4.1 Core Roles

| Role | Responsibility |
|---|---|
| Vendor | Register/login, submit tender response, answer clarifications, view shared PO |
| Indent Creator / User | Create material/job request, create indent, respond to clarification, raise demand, receive auto-issued items/assets |
| Approver | Approve, reject, or seek clarification based on workflow |
| Store Purchase Officer | Approve material/job master, approve inspection, approve demand, approve asset disposal, final approval for goods transfer |
| Purchase Head | Assign approved indent/tender requirement to purchase department member |
| Purchase Department Member | Create tender, create PO/SO based on tender |
| Store Person | Perform GPRN, GRN, issue stock, handle store operations, receive stock/assets when applicable |
| Admin | Configure workflows, users, roles, limits, masters, system parameters |

---

## 5. Vendor Portal Requirements

### 5.1 Vendor Registration

Vendors should be able to:

- Register
- Login
- Maintain basic profile details
- Participate in tenders
- Submit tender responses
- Respond to clarification requests
- Receive/view PO shared with them

### 5.2 Vendor Tender Submission

Vendor should be able to:

- View eligible tenders
- Submit tender quotation
- Attach required documents
- Respond to clarification
- Track tender status where applicable

---

## 6. Internal Portal Requirements

### 6.1 Material / Job Master

#### 6.1.1 Creation

Internal users should be able to create a material or job if it does not already exist in the system.

Each item should have a type/category indicating whether it is:

- Material
- Job / Service

#### 6.1.2 Asset Flag

For material items, the system should maintain an **asset flag**.

| Asset Flag | Meaning |
|---|---|
| True | Non-consumable asset item |
| False | Consumable / non-asset item |

If `assetFlag = true`, the item is treated as an asset and asset code generation is required during GRN.

If `assetFlag = false`, the item is treated as non-asset/consumable and may update stock depending on store-stock handling.

#### 6.1.3 Approval

Material/job creation must be approved by the **Store Purchase Officer** before it can be used in indent creation.

---

## 7. Indent Management

### 7.1 Indent Creation

Once a material/job is approved, users can create indents.

An indent may contain:

- Material items
- Job/service items

### 7.2 Indent Approval Workflow

Indent approval should follow a **dynamic user-defined approval workflow** configured from the admin panel.

Approvers should have three actions:

| Action | Behavior |
|---|---|
| Accept | Moves request to next approver |
| Reject | Rejects the request with mandatory reason/remarks |
| Change Request / Seek Clarification | Sends request back to a lower hierarchy user for clarification |

### 7.3 Clarification Rules

If clarification is sent back to the indentor:

- It should only be visible to the original indent creator.
- Other users at the same lower hierarchy should not see it unless explicitly part of the workflow.

### 7.4 Final Indent Approval

Once the indent is fully approved, it should go to the **Purchase Head**.

The Purchase Head will assign the approved requirement to a specific purchase department member for tender creation.

---

## 8. Dynamic Approval Workflow

### 8.1 Workflow Configuration

The system must support workflow configuration from the admin panel.

Configurable workflow should support:

- Module-wise workflows
- Role/user-based approvers
- Sequential approvals
- Approval hierarchy
- Rejection remarks
- Clarification routing
- Status tracking
- Audit trail

Modules using the same approval logic include:

- Indent
- Tender
- PO
- SO
- Goods Inspection
- Demand
- Goods Transfer
- Asset Disposal
- Contingency Purchase, if approval is required
- Payment Voucher, if approval is required

### 8.2 Common Approval Actions

The common approval engine should support:

| Action | Required Behavior |
|---|---|
| Accept | Move to next approver or mark final approved |
| Reject | Mark rejected with reason |
| Seek Clarification | Move back to selected lower hierarchy user |
| Resubmit | Original responsible user can respond and resubmit |

### 8.3 Approval Audit

Every approval action must record:

- Module name
- Document number
- Version number, if applicable
- Action taken
- Previous status
- New status
- Performed by
- Performed at timestamp
- Remarks/reason
- Assigned next approver, if any

---

## 9. Tender Management

### 9.1 Tender Creation

The assigned purchase department member will generate a tender from approved indent(s).

A tender can contain:

- One indent
- Multiple indents
- Material indent(s)
- Job/service indent(s)

However, the system must enforce tender classification rules.

### 9.2 Tender Classification Rules

Tender type should be determined based on the composition of material and job/service quantity.

| Condition | Tender Type | Allowed? |
|---|---|---|
| More than 85% quantity is material | Material Tender | Yes |
| 100% service/job | SO Tender | Yes |
| Mixed case not satisfying above rules | Invalid | No |

The system should not allow tender creation in invalid mixed cases.

### 9.3 Separate Tender Rule

Material and job tenders should be separate.

- PO is generated for material tender.
- SO is generated for service/job tender.

### 9.4 Tender Approval

Tender should go through the same dynamic approval logic as indent.

Approvers should have:

- Accept
- Reject with reason
- Seek clarification/change request

### 9.5 Tender Evaluation

Tender evaluation is required but will be covered in a separate PRD.

Current expected behavior:

- Tender evaluation module may be incomplete or bypassed.
- PO/SO generation may currently bypass vendor selection logic.
- The system should be designed so proper evaluation and vendor selection can be added later without breaking PO/SO flow.

---

## 10. PO / SO Management

### 10.1 PO/SO Creation

Once tender evaluation is complete, PO/SO should be generated based on the tender.

Current interim behavior:

- Vendor selection logic may be bypassed until tender evaluation is fully implemented.

### 10.2 PO/SO Rules

| Tender Type | Output Document |
|---|---|
| Material Tender | PO |
| Service/Job Tender | SO |

Rules:

- One PO/SO can have only one tender.
- One tender should generate either PO or SO based on tender type.
- PO is for material.
- SO is for service/job.

### 10.3 PO/SO Approval

PO/SO must go through the same dynamic approval workflow logic.

Approver actions:

- Accept
- Reject with reason
- Seek clarification/change request

---

## 11. Version Control & Version History

The following documents must be version-controlled:

- Indent
- Tender
- PO
- SO

### 11.1 Versioning Requirements

The system should maintain:

- Current active version
- Version number
- Version history
- Change reason
- Changed by
- Changed timestamp
- Previous values
- New values
- Approval state per version

### 11.2 Versioning Rule

Any material change after submission or approval should create a new version instead of overwriting historical records.

---

## 12. GPRN — Goods Provisional Receiving Note

### 12.1 GPRN Creation

Based on approved PO, store person will create GPRN.

Rules:

- One PO can have multiple GPRNs.
- Partial receiving is allowed.
- GPRN should populate required details from PO.
- Received quantity cannot exceed pending PO quantity.

### 12.2 GPRN Data

GPRN should include:

- PO reference
- Vendor reference
- Item details
- Ordered quantity
- Previously received quantity
- Pending quantity
- Current received quantity
- Receiving date
- Store person details
- Attachments, if required
- Remarks

---

## 13. GI — Goods Inspection

### 13.1 Inspection Trigger

Inspection is conducted based on GPRN.

### 13.2 Inspection Responsibility

| Tender/PO Scenario | Inspector |
|---|---|
| Single indent linked to tender | Indentor / indent creator |
| Multiple indents linked to tender | Store person |

### 13.3 Inspection Submission

Inspection should capture:

- GPRN reference
- Item-wise inspected quantity
- Accepted quantity
- Rejected quantity
- Remarks
- Attachments, if needed
- Inspector details

### 13.4 Inspection Approval

Once GI is submitted, it goes to **Store Purchase Officer**.

Store Purchase Officer has two options:

| Action | Behavior |
|---|---|
| Accept | Inspection approved; next steps can start |
| Reject | Reason is mandatory; user must redo GI |

If rejected:

- Rejection remarks should be sent/visible to user.
- GPRN should not be affected.
- User must perform GI again.

---

## 14. Goods Return

Goods Return is used to return items rejected during Goods Inspection.

### 14.1 Goods Return Requirements

System should support:

- Returning rejected GI quantity
- Reference to GPRN/GI
- Return quantity validation
- Return reason
- Vendor reference
- Transaction log
- Status tracking

Returned quantity should not be treated as accepted stock.

---

## 15. GRN — Goods Receipt Note

GRN is submitted after GI approval.

GRN behavior depends on whether the item is asset or non-asset.

---

## 16. Asset Item Handling

### 16.1 Asset Identification

If material has `assetFlag = true`, it is treated as an asset.

### 16.2 Asset Code Generation

At GRN:

- Asset code must be auto-generated.
- One quantity of asset material should generate one unique asset code.
- Asset code should be visible on GRN.

Example:

If 5 laptops are received, system should generate 5 unique asset codes.

### 16.3 Asset Assignment Rules

| Scenario | Assignment Rule |
|---|---|
| Single indent in tender | Auto-assign asset to indentor / indent creator |
| Multiple indents in tender | Assign asset to store person performing GRN |
| Optional enhancement | Auto-assign based on lowest indent number linked to tender, if feasible |

### 16.4 Asset Stock Rule

Assets should always be recorded in stock because they are non-consumable.

Even if auto-assigned, transaction history must record the movement for audit and reports.

---

## 17. Non-Asset / Consumable Item Handling

If `assetFlag = false`, item is treated as non-asset/consumable.

### 17.1 Store Stock Checkbox

At frontend, user should be prompted with a **store stock checkbox**.

#### Case 1: Store Stock Enabled

If checkbox is enabled:

- Item is stored in stock.
- Store person must issue it later through demand and issue.
- OHQ should be updated.

This is mandatory if tender has more than one indent.

#### Case 2: Store Stock Disabled

If checkbox is disabled:

- Item is directly issued to indentor/indent creator.
- OHQ should not be updated.
- This is allowed only when tender has a single indent.

### 17.2 Non-Asset Stock Rule

| Store Stock Checkbox | OHQ Update |
|---|---|
| Enabled | Yes |
| Disabled | No |

Even in direct issue or auto-assignment cases, transactions must be recorded for audit and reporting.

---

## 18. Stock Management

### 18.1 On-Hand Quantity

The system should maintain OHQ for stock-controlled items.

OHQ should be updated when:

- Asset item is received
- Non-asset item is received with store stock enabled
- Demand issue is performed
- Goods transfer affects asset ownership/location
- Asset disposal moves item to temporary storage
- Auction completion removes asset from system

### 18.2 Transaction Ledger

Every stock/asset movement must create a transaction record.

Transaction types may include:

- GPRN receipt
- GI accepted
- GI rejected
- Goods return
- GRN posting
- Asset code generation
- Auto assignment
- Direct issue
- Store stock receipt
- Demand issue
- Goods transfer request
- Goods transfer approval
- Asset disposal request
- Temporary storage movement
- Auction completion

---

## 19. Goods Transfer

Goods Transfer is used to transfer assets from one user to another.

### 19.1 Transfer Request

A user can raise an asset transfer request.

### 19.2 Approval Flow

Approval flow:

1. Receiver approval
2. Store Purchase Officer final approval

Both receiver and Store Purchase Officer can:

- Approve
- Reject with reason

### 19.3 Usage

Goods transfer can also be used by store person to assign assets that were not auto-assigned from GRN.

### 19.4 Audit

Every transfer should record:

- Asset code
- From user
- To user
- Requestor
- Receiver decision
- Store Purchase Officer decision
- Status
- Remarks
- Timestamp

---

## 20. Demand and Issue

Demand and Issue is used to issue available non-asset/consumable stock.

### 20.1 Demand Creation

Indent Creator role can raise demand for non-asset items available in stock.

### 20.2 Demand Approval

Demand must be approved by Store Purchase Officer.

### 20.3 Issue

Once approved:

- Store Person issues the stock.
- Indent Creator cannot self-issue.
- Issue should be role-based.

### 20.4 Stock Update

On issue:

- OHQ should reduce.
- Transaction should be recorded.
- Issued-to user should be captured.

---

## 21. Asset Master

Asset Master is used to view and update asset details.

### 21.1 Asset Master Capabilities

System should support:

- View asset code
- View asset item details
- View current owner/user
- View current location/store
- View status
- View acquisition reference
- View GRN reference
- Update allowed asset details
- View movement history
- View disposal status

---

## 22. Asset Disposal

Asset Disposal is used to dispose assets with reason.

### 22.1 Disposal Request

User/store can raise disposal request with:

- Asset code
- Disposal reason
- Supporting remarks
- Attachments, if needed

### 22.2 Disposal Approval

Disposal request must be approved by Store Purchase Officer.

### 22.3 Post-Approval Behavior

Once approved:

- Asset should move to temporary store for auction.
- Asset should be removed from visible on-hand usable quantity.
- Asset should appear in temporary storage.

### 22.4 Auction Completion

Once auction is complete:

- Asset should move out of the system.
- Final disposal transaction should be recorded.
- Asset status should be updated as disposed/auctioned.

---

## 23. Contingency Purchase

Contingency Purchase is used for emergency purchasing not routed through PO.

### 23.1 Limit

Current limit: `50,000`

This limit must be dynamic and configurable from the frontend/admin panel.

No code change should be required to update the limit.

### 23.2 Requirements

System should support:

- Create contingency purchase request
- Validate amount against dynamic limit
- Attach bills/documents
- Capture vendor/supplier details
- Approval flow, if configured
- Link to payment voucher
- Audit trail

---

## 24. Payment Voucher

Payment Voucher is used to generate payment for:

- PO
- SO
- Contingency Purchase

### 24.1 Voucher Source Options

Payment voucher should support three source types:

1. PO
2. SO
3. Contingency Purchase

### 24.2 Data Fetching

For PO/SO payment voucher:

- Tax data should be fetched from PO/SO.
- Quantity data should be fetched from GRN.
- Amount payable should be calculated based on received quantity, unless advance payment is selected.

### 24.3 Advance Payment

If advance payment is selected:

- Payment is not bound by received quantity.
- System should allow payment based on advance terms.

### 24.4 Normal Payment

If not advance payment:

- Payment can only be made for received/accepted quantity.
- Payment should not exceed eligible amount.

### 24.5 Payment Audit

Voucher should record:

- Source document
- Payment type
- Vendor
- Amount
- Tax details
- Quantity basis
- Advance flag
- Created by
- Approval status, if applicable
- Payment status
- Tally sync status, if applicable

---

## 25. Tally Integration

Tally integration exists but is not yet tested.

### 25.1 Expected Behavior

The system should support integration readiness for:

- Payment vouchers
- PO/SO accounting entries, if applicable
- Vendor ledger mapping
- Tax ledger mapping
- Sync status tracking
- Retry/error logs

### 25.2 Current Status

Tally integration should be marked as:

- Existing but untested
- To be validated after core procurement/store/payment flow is ready

---

## 26. Reporting and Audit Requirements

### 26.1 Mandatory Audit Logs

The system must maintain audit logs for:

- Login/register actions
- Master creation/update
- Approval actions
- Rejections
- Clarifications
- Version changes
- Tender creation/update
- PO/SO creation/update
- GPRN
- GI
- GRN
- Goods return
- Asset generation
- Asset assignment
- Stock movement
- Goods transfer
- Demand issue
- Asset disposal
- Payment voucher
- Tally sync

### 26.2 Reports

System should support reports for:

- Pending approvals
- Indent status
- Tender status
- PO/SO status
- GPRN pending inspection
- GI accepted/rejected
- GRN completed
- Stock OHQ
- Asset register
- Asset movement history
- Goods return
- Demand and issue
- Asset disposal
- Payment voucher status
- Tally integration status

---

## 27. Admin Panel Requirements

Admin panel should allow configuration of:

- Users
- Roles
- Permissions
- Dynamic approval workflows
- Contingency purchase limit
- Material/job categories
- Asset code format, if applicable
- Tender rules, if configurable
- Payment voucher settings
- Tally integration settings
- Notification templates
- System parameters

No code-level modification should be required for normal business configuration.

---

## 28. Notifications

System should support notifications/email where applicable for:

- Approval pending
- Rejection with remarks
- Clarification requested
- Clarification responded
- Tender assigned
- PO shared with vendor
- GI rejected
- Demand approved
- Asset transfer request
- Asset disposal approval
- Payment voucher status

---

## 29. Validation Rules Summary

### 29.1 Material/Job

- Material/job cannot be used in indent unless approved.
- Asset flag determines asset/non-asset behavior.

### 29.2 Indent

- Indent must follow dynamic approval.
- Clarification to indentor visible only to creator.

### 29.3 Tender

- Tender can have one or more indents.
- Tender may include material or job/service.
- Material tender allowed if material quantity is more than 85%.
- SO tender allowed if 100% service.
- Other mixed cases are not allowed.
- Material and job tender should be separate.

### 29.4 PO/SO

- One PO/SO can have one tender only.
- Material tender generates PO.
- Service tender generates SO.

### 29.5 GPRN

- One PO can have multiple GPRNs.
- Partial receiving allowed.
- Received quantity cannot exceed PO pending quantity.

### 29.6 GI

- Single indent: indentor inspects.
- Multiple indent: store person inspects.
- Rejected GI requires reason.
- GPRN remains unaffected if GI is rejected.

### 29.7 GRN

- Asset item generates one asset code per quantity.
- Asset is always recorded in stock.
- Non-asset updates OHQ only if store stock is enabled.
- Direct issue allowed only for single-indent tender.
- Store stock is mandatory for multi-indent tender.

### 29.8 Goods Transfer

- Receiver approval required.
- Store Purchase Officer final approval required.

### 29.9 Demand Issue

- Indent Creator can raise demand.
- Store Purchase Officer approves.
- Store Person issues.
- Indent Creator cannot self-issue.

### 29.10 Asset Disposal

- Disposal requires Store Purchase Officer approval.
- Approved asset moves to temporary auction store.
- Auction completion removes asset from active system.

### 29.11 Contingency Purchase

- Limit should be dynamic.
- Current value: 50,000.
- Limit must be configurable from frontend/admin.

### 29.12 Payment Voucher

- Can be generated for PO, SO, or Contingency Purchase.
- Tax comes from PO/SO.
- Quantity comes from GRN.
- Advance payment is not quantity-bound.
- Normal payment is quantity/receipt-bound.

---

## 30. Non-Functional Requirements

### 30.1 Performance

The system should support internal enterprise usage with acceptable response time for:

- Master search
- Approval queues
- Tender listing
- PO/SO lookup
- Stock reports
- Asset history
- Payment voucher lookup

### 30.2 Reliability

The system should prevent inconsistent states, especially in:

- Approval workflow
- Versioning
- Stock updates
- Asset generation
- Payment calculation
- Partial receiving
- GI rejection/retry

### 30.3 Maintainability

Code should follow:

- Modular service design
- Clear domain models
- Reusable workflow engine
- Reusable transaction/audit mechanism
- Clear DTO/entity separation
- Validation at backend level
- Frontend form validation for usability

### 30.4 Security

Because the system is internal:

- Harsh public-facing security controls like strict CORS restrictions are not a priority.
- However, authentication, authorization, role-based access, and auditability are still required.
- Users must only access features allowed by their role.

### 30.5 Configurability

The system must avoid hardcoded business rules where frontend/admin configuration is expected.

Configurable examples:

- Approval workflows
- Contingency purchase limit
- Roles and permissions
- System parameters
- Notification templates
- Tally settings

---

## 31. Out of Scope for Current PRD

The following are noted but not fully defined in this PRD:

- Detailed tender evaluation workflow
- Vendor selection scoring
- Comparative statement generation
- Commercial/technical bid evaluation
- Tally integration testing
- Advanced reporting dashboards

Tender evaluation should be created as a separate PRD.

---

## 32. Audit Expectations for Code Review

The PRD should be used as the source of truth for a code audit. The auditor should check frontend, backend and database implementation against this PRD and produce a gap report.

The audit should identify:

- What is working as per PRD
- What is partially working
- What is missing
- What exists in code but is not mentioned in PRD
- What exists in backend but is not exposed in frontend
- What exists in frontend but lacks backend/database support
- What is hardcoded but should be configurable
- What creates approval, financial, stock, audit, or data consistency risk

---

## 33. Additional Specialist Audit Checks

These are audit checks only. They should not be treated as new business functionality unless already supported by the PRD or code.

1. Check whether document numbers are generated consistently for:
   - Indent
   - Tender
   - PO
   - SO
   - GPRN
   - GI
   - GRN
   - Goods Return
   - Demand
   - Issue
   - Asset Transfer
   - Asset Disposal
   - Contingency Purchase
   - Payment Voucher

2. Check whether status transitions are controlled.
   - Draft → Submitted → In Approval → Approved → Rejected
   - Approved documents should not be edited directly without creating a new version.

3. Check whether backend validations prevent frontend bypass.
   Even though the system is internal, users should not be able to bypass business rules by calling APIs directly.

4. Check whether every financial/stock-changing action is database-transaction safe.
   Examples:
   - GRN posting
   - Asset code generation
   - OHQ update
   - Demand issue
   - Payment voucher creation
   - Asset disposal

5. Check whether duplicate submissions are handled.
   - Double-click on submit should not create duplicate GRN, payment voucher, approval action, or asset code.

6. Check whether partial receiving is correctly calculated.
   - Ordered quantity
   - Previously received quantity
   - Current received quantity
   - Rejected quantity
   - Pending quantity

7. Check whether rejected GI quantity is blocked from GRN/stock posting.

8. Check whether goods return is linked to rejected inspection quantity only.

9. Check whether audit records are immutable or at least protected from normal user edits.

10. Check whether admin-configurable values are actually loaded from database/config screens instead of hardcoded constants.

11. Check whether frontend role-based hiding is backed by backend authorization.
   Hiding a button on frontend is not enough.

12. Check whether reports use transaction data rather than only current master state.
   Reports should be audit-capable.

13. Check whether asset codes remain unique under concurrent GRN submission.

14. Check whether payment voucher prevents duplicate payment for the same received quantity.

15. Check whether PO/SO generation enforces one tender to one PO/SO relationship.

16. Check whether multi-indent tender logic is correctly handled in:
   - Inspection responsibility
   - Store stock requirement
   - Asset assignment
   - Direct issue blocking

17. Check whether tender material/service percentage logic uses correct quantity basis and handles edge cases.

18. Check whether deleted/cancelled/rejected documents are excluded from active operational calculations but still visible in history.

19. Check whether database foreign keys or equivalent application-level constraints exist for critical references.

20. Check whether Tally integration has:
   - Sync status
   - Error message
   - Retry option
   - Last sync timestamp
   - Payload/reference logging

---

## 34. Recommended Post-Audit Implementation Planning

After the audit report is ready, the next step should be a phased implementation plan. Code should not be changed until the audit is reviewed.

Recommended phases:

### Phase 1: Critical Business Blockers

Fix gaps that block core business operation or create financial, stock, approval, or data consistency risk.

### Phase 2: Approval / Workflow Stabilization

Fix reusable workflow engine gaps, status transitions, approval history, rejection, clarification, and module-wise workflow reuse.

### Phase 3: Stock, Asset and Payment Correctness

Fix GPRN, GI, GRN, OHQ, asset code generation, asset assignment, demand issue, disposal, and payment voucher correctness.

### Phase 4: Frontend / Admin Configurability

Move hardcoded business rules to frontend/admin-managed configuration where required by the PRD.

Ensure users can operate and configure business flows without code changes.

### Phase 5: Reports, Tally and Production Hardening

Complete audit-capable reports, Tally sync status/error/retry handling, logging, duplicate submission handling, transaction safety, and production readiness cleanup.

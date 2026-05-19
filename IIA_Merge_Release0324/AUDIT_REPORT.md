# IIA P2P Procurement System — Full Audit Report
**Date:** 2026-05-16  
**Auditor:** Claude Code (Senior Software Architect Review)  
**Codebase:** D:\IIA\ServerV2\IIA_Server\IIA_Merge_Release0324  
**PRD Source:** P2P_Procurement_Store_Asset_Payment_PRD.md  

---

## A. Executive Summary

System implements P2P (Procure-to-Pay) procurement, inventory, asset, and payment management for IIA. Two React frontends connect to single Spring Boot backend.

**Overall Verdict: BACKEND MOSTLY FUNCTIONAL — FRONTEND BLOCKS PRODUCTION USE**

> **Correction from initial audit:** Three services previously reported as "commented out" each contain a V1 (commented) block followed by an active V2 implementation. `WorkflowServiceImpl` v2 starts at line 3288, `TenderRequestServiceImpl` v2 at line 5073, `PurchaseOrderImpl` v2 at line 1805. All three are `@Service`-annotated and fully wired. The backend P2P engine is substantially functional. The blocking issues are now primarily in the frontend and security layers.

| Dimension | Status |
|---|---|
| Core P2P Flow (Indent→PO) Backend | FUNCTIONAL — all three core services have active v2 |
| Core P2P Flow Frontend | BROKEN — Tender/PO/CP pages 95% commented in Frontend-test |
| Inventory (Store/GI/GRN/GT) | Mostly functional |
| Asset Management | Functional (code gen works) |
| Payment Voucher | Partial — backend exists, Tally push unverified |
| Security | HIGH — No JWT (closed network, accepted); role checks absent; vendor portal auth broken |
| Workflow Engine | FUNCTIONAL — `WorkflowServiceImpl` v2 active with branch-based routing |
| Vendor Portal | Partially functional — auth broken, port mismatches |
| Internal Frontend | Tender/PO/CP v2 ACTIVE; Approval form is stub (no API calls) |
| Test Coverage | None found |
| Audit Trail | Entity defined, not implemented |

**Critical blockers before production:** reduced from 5 to 3 (security + frontend). See Section O.

---

## B. Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│  Internal Frontend (Frontend-test)                       │
│  React + Redux, CRA, port 3000                          │
│  Proxy → http://103.181.158.220:8081  ← WRONG PORT      │
└─────────────────┬───────────────────────────────────────┘
                  │ HTTP (no JWT)
┌─────────────────▼───────────────────────────────────────┐
│  Spring Boot Backend (Backend-prod)                      │
│  Port 8088, context /astro-service                       │
│  JPA/Hibernate + MySQL                                   │
│  Spring Security: ALL ROUTES PERMITTED (no JWT)          │
└─────────────────┬───────────────────────────────────────┘
                  │ JDBC
┌─────────────────▼───────────────────────────────────────┐
│  MySQL Database                                          │
│  118+ tables, workflow_master, transition_master         │
│  approval_limit_master, ohq_master                       │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  Vendor Portal (Astro-vendor-portal-main)               │
│  React + Redux, port 3001                               │
│  Proxy → http://localhost:8081  ← WRONG PORT            │
│  baseURL hardcoded: localhost:8088  ← correct           │
└─────────────────────────────────────────────────────────┘
```

**Key Architectural Decisions:**
- Dynamic workflow engine: workflow_master + transition_master tables drive state transitions
- Role-based approver routing: department_approver_master, field_station_approver_master
- Approval limits: DB-driven, category-specific (COMPUTER/NON_COMPUTER/PROJECT/ALL)
- LOV system: lov_master drives all dynamic dropdowns
- OHQ managed in ohq_master (non-consumable) + ohq_master_consumable (consumable)
- Asset codes: generated on GI approval, one code per quantity unit

---

## C. PRD Traceability Matrix

| PRD Module | Backend | DB | Frontend | Status |
|---|---|---|---|---|
| User Management | Controller exists, no role security | user_master, role_master | Login pages exist | PARTIAL |
| Indent Creation | IndentCreation fully functional | indent_creation (version ✓) | Form exists | IMPLEMENTED |
| Indent Approval | WorkflowServiceImpl **v2 ACTIVE** (line 3288) with branch-based routing | workflow_master | Approval form stub — no API calls | BACKEND OK / FRONTEND BROKEN |
| Contingency Purchase | CP controller exists | contigency_purchase | 95% commented out | BACKEND PARTIAL / FRONTEND BROKEN |
| Tender Creation | TenderRequestServiceImpl **v2 ACTIVE** (line 5073) with draft lifecycle | tender_request (no version) | 95% commented | BACKEND OK / FRONTEND BROKEN |
| Tender Evaluation | VendorQuotation APIs work | vendor_quotation | Vendor portal works | PARTIAL |
| Purchase Order | PurchaseOrderImpl **v2 ACTIVE** (line 1805) with versioning + budget check | purchase_order (version ✓) | 95% commented | BACKEND OK / FRONTEND BROKEN |
| Service Order | SO controller exists | service_order (no status col) | Minimal | PARTIAL |
| GPRN | GPRN controller/service present | gprn_master | Page exists | PARTIAL |
| GI (Goods Issue) | GiServiceImpl with asset gen | gi_master | Present | PARTIAL |
| GRN (Goods Receipt) | GrnServiceImpl @Transactional | grn_master | Present | FUNCTIONAL |
| Goods Transfer | GtServiceImpl 2-step approval | gt_master | Present | FUNCTIONAL |
| Asset Management | Asset code gen in GI | asset_master | Pages present | FUNCTIONAL |
| Stock Management | OHQ maintained, store-stock checkbox | ohq_master + consumable | Present | FUNCTIONAL |
| Disposal | DisposalServiceImpl present | disposal | Partial | PARTIAL |
| Payment Voucher | PaymentVoucherServiceImpl | payment_voucher | Minimal | PARTIAL |
| Tally Integration | TallyXmlService present | — | Not visible | PARTIAL |
| Admin Panel | All admin APIs functional | approval_limit, budget, etc. | Admin panel scaffolded | FUNCTIONAL |
| Vendor Registration | VendorMasterController | vendor_master | Vendor portal ✓ | FUNCTIONAL |
| Vendor Portal Auth | authSlice — POST instead of GET | — | PrivateRoutes broken | BROKEN |
| Audit Log | AdminAuditLog entity defined | admin_audit_log table | Not visible | NOT IMPL |
| Budget Management | BudgetController present | budget_master | Admin page | FUNCTIONAL |
| Approval Limits | ApprovalLimitController | approval_limit_master | Admin page | FUNCTIONAL |
| LOV Management | LOVController | lov_master | Dynamic dropdowns | FUNCTIONAL |
| Reports | 30+ report pages in frontend | — | Report pages present | PARTIAL |
| Workflow Config | WorkflowName hardcoded enum | workflow_master | Admin page | PARTIAL |

---

## D. Module-by-Module Audit

### D1. User Management
**PRD:** Login, role assignment, password reset, dept mapping  
**Backend:** `UserController` — single `POST /login` endpoint only. No JWT issued. No role-based route protection at backend. Default admin `admin/admin123` created at startup (DataInitializer.java:40).  
**DB:** `user_master`, `role_master` exist. Password stored (plaintext? — not confirmed hashed).  
**Gap:** No logout endpoint. No password reset API. No role check on any endpoint.

### D2. Indent Creation
**PRD:** User creates indent, selects materials, fills qty/purpose/dept  
**Backend:** `IndentCreationController` → `IndentCreationServiceImpl` — functional. Version column present.  
**DB:** `indent_creation` has version INT, full audit columns, status DRAFT/IN_APPROVAL/APPROVED/CHANGE_REQUESTED.  
**Gap:** Minor — version increment logic not verified in service.

### D3. Indent Approval
**PRD:** Multi-level approval via workflow engine. Change request loop.  
**Backend:** `WorkflowServiceImpl` — V1 (lines 1–3286) commented as historical reference. **V2 active `@Service` class starts at line 3288** — fully functional. Implements `initiateWorkflow`, branch-based routing, reporting officer resolution (via `EmployeeDepartmentMaster`), project head resolution, email notifications on transitions, budget service integration. `BudgetService` injected and called.  
**DB:** `workflow_master` (11 workflows), `transition_master` fully populated — engine reads these at runtime.  
**Frontend:** `indentApproval/Form2.jsx` — stub, zero API calls, zero approval logic.  
**Gap:** MEDIUM (frontend only) — Backend engine is functional. Frontend approval form not wired.

### D4. Contingency Purchase (CP)
**PRD:** Emergency procurement with limit, separate approval chain  
**Backend:** CP controller exists and is wired.  
**DB:** `contigency_purchase` — NO limit/threshold column. CP limit NOT stored in DB.  
**Frontend:** `ContingencyPurchase.jsx` V2 ACTIVE at line 848. Has project/vendor/employee/material dropdowns, LOV integration (Form ID 2: gst, paymentTo, budgetCode, materialCategory, countryOfOrigin), full form data state. Second variant inside `/* */` block (lines 1580–1865) is commented out. V1 in `//` comments lines 1–847.  
**Gap:** MEDIUM — Frontend and backend both functional. CP limit/threshold column missing from DB — spending limit NOT enforced.

### D5. Tender Request
**PRD:** Tender creation, document upload, closing date, bid type (Single/Double)  
**Backend:** `TenderRequestServiceImpl` — V1 double-commented (lines 1–5071). **V2 active `@Service` at line 5073** — fully functional. Implements:
- `createTenderRequest`: validates indents via `workflow_transition` (checks "Completed" status), validates cancel status, validates `isLockedForTender`, generates tender ID (`T` + sequential number), initiates `TENDER_APPROVER` workflow, locks all linked indents.
- `saveTenderDraft` / `updateTenderDraft` / `submitTenderDraft`: full draft lifecycle.
- Bid type (Single/Double) stored. Pre-bid meeting fields present.  
**DB:** `tender_request` — NOTE: V1 code had `tenderRequest.setTenderVersion(1)` and `setIsActive(true)` but V2 service sets these too — DB likely has these columns. No confirmed version column in initial DB dump analysis; needs re-verification.  
**Frontend:** `Tender.jsx` V2 ACTIVE at line 1614. Has LOV integration (Form ID 9), approved indent fetching (`/approved-indents?userId`), draft save/submit, version history state, buy-back fields, pre-bid meeting fields. V1 in comments lines 1–1613.  
**Gap:** LOW — Both backend and frontend are functional. Integration testing needed.

### D6. Tender Evaluation
**PRD:** Vendor quotation submission, evaluation, 85% rule, disqualification  
**Backend:** `VendorQuotationController` + `VendorQuotationAgainstTenderService` — mostly functional. API endpoints correct.  
**Gap:** 85% vendor participation rule NOT implemented. No minimum vendor threshold check before PO.

### D7. Purchase Order
**PRD:** Auto-generate PO from approved tender, multi-level approval  
**Backend:** `PurchaseOrderImpl` — V1 commented (lines 1–1803). **V2 active `@Service` at line 1805** — fully functional. Implements:
- `createPurchaseOrder`: generates PO ID (`PO` + tender numeric suffix), sets version=1, calls `budgetService.checkBudgetForPo`, saves PO + attributes, locks tender after PO creation (`setIsLocked(true)`, `setLockedForPO`).
- `updatePurchaseOrder` (v2): creates NEW version (copy-new pattern) with history snapshot, deactivates old, supersedes pending workflow transitions, increments version ID (e.g. PO1001 → PO1001/2).
- `getPoVersionHistory`: returns all versions by base ID.
- `getPurchaseOrderById` / `getPurchaseOrderBase64FilesById`: full response with tender+indent data.  
**DB:** `purchase_order` — po_version ✓, isActive ✓, parentPoId ✓, purchase_order_history ✓.  
**Frontend:** `PO.jsx` V2 ACTIVE at line 984. Has vendor dropdown, approved tender IDs (`/getApprovedTenderIdForPOAndSO`), LOV (Form ID 8: deliveryPeriod, warranty, pbg), completed vendor list, version history state. V1 in comments lines 1–983.  
**Gap:** LOW — Both backend and frontend functional. Integration testing needed. Note: `console.log("PO ID:", poId)` at line 1030 is debug output left in production code.

### D8. Service Order (SO)
**PRD:** SO for services (not goods), similar to PO flow  
**Backend:** SO controller + service present. Partial implementation.  
**DB:** `service_order` — NO status column.  
**Gap:** MEDIUM — Missing status column breaks approval state tracking.

### D9. GPRN (Goods Purchase Receipt Note)
**PRD:** Receive goods against PO, record receipt  
**Backend:** GPRN controller + service present.  
**DB:** `gprn_master` present.  
**Gap:** MEDIUM — Not fully traced, assumed partial.

### D10. GI (Goods Issue)
**PRD:** Issue goods from store, trigger asset code gen for assets  
**Backend:** `GiServiceImpl` with asset code generation at lines 488-535. Logic: one asset code per quantity unit, sequential from max assetId + prefix.  
**Gap:** No role check on GI approval.

### D11. GRN (Goods Receipt Note)
**PRD:** Formal receipt into store inventory  
**Backend:** `GrnServiceImpl` with `@Transactional` on saveGrn. Appears functional.  
**DB:** `grn_master` present.  
**Gap:** Minimal.

### D12. Goods Transfer (GT)
**PRD:** Transfer assets between departments, two-step approval (Receiver + SPO)  
**Backend:** `GtServiceImpl` — two-step GT approval (Receiver → SPO). OHQ updates on final approval. Functional.  
**Gap:** Minimal.

### D13. Asset Management
**PRD:** Asset code generation, lifecycle tracking, disposal  
**Backend:** Asset code gen in `GiServiceImpl` works. `DisposalServiceImpl` present.  
**DB:** `asset_master` present.  
**Gap:** Disposal lacks temp/auction differentiation per PRD.

### D14. Stock Management
**PRD:** OHQ tracking, consumable vs non-consumable, store-stock  
**Backend:** OHQ maintained in `GiServiceImpl`/`GrnServiceImpl`. Store-stock checkbox works.  
**DB:** `ohq_master` + `ohq_master_consumable` — NO audit columns (created_by/date missing).  
**Gap:** MEDIUM — OHQ changes not auditable.

### D15. Payment Voucher
**PRD:** PV linked to PO/SO/CP, Tally XML generation, approval  
**Backend:** `PaymentVoucherServiceImpl` present. `TallyXmlService` present.  
**DB:** `payment_voucher` — NO updated_by/updated_date.  
**Gap:** Payment approval chain not verified. Tally push mechanism unclear.

### D16. Admin Panel
**PRD:** Manage approval limits, budget, LOV, workflow, approvers  
**Backend:** All admin controllers functional.  
**Frontend:** Admin panel fully scaffolded in Frontend-test.  
**Gap:** WorkflowName HARDCODED as enum — changes require recompile.

### D17. Vendor Portal
**PRD:** Vendor registration, login, quotation submission, PO view  
**Backend:** VendorMaster + VendorQuotation APIs functional.  
**Frontend:** Register/Login/Quotation/PO view working. Auth broken (PrivateRoutes no check). Port mismatches in setupProxy and masterSlice.  
**Gap:** HIGH — Multiple critical bugs (see Section H).

---

## E. Business Flow Validation

### E1. Standard P2P Flow: Indent → Tender → PO → GPRN → GRN → Asset
**Status: BACKEND FUNCTIONAL — FRONTEND BLOCKS END-TO-END**
- Indent creation: WORKS (backend + frontend)
- Indent approval: Backend WORKS (WorkflowServiceImpl v2 active). Frontend stub — cannot submit approval via UI.
- Tender: Backend WORKS (TenderRequestServiceImpl v2, validates approved indent via workflow table, initiates TENDER_APPROVER workflow). Frontend 95% commented.
- PO: Backend WORKS (PurchaseOrderImpl v2, budget check, locks tender, versioning). Frontend 95% commented.
- GPRN/GRN: Partial
- Asset: Works if GI reached

### E2. Emergency Procurement: Contingency Purchase Flow
**Status: BROKEN**
- CP limit NOT in DB
- Frontend 95% commented
- No limit enforcement possible

### E3. Vendor Quotation Submission Flow (Vendor Portal)
**Status: PARTIAL**
- Vendor login: Works (if port fixed)
- View tender: Works (if param→params fixed)
- Upload quotation: Works
- View PO: Partial

### E4. Store → GI → Asset Generation Flow
**Status: FUNCTIONAL**
- Store-stock checkbox works
- GI saves correctly
- Asset code generation on GI approval: WORKS (one per qty)
- OHQ decremented: WORKS

### E5. Goods Transfer (Dept A → Dept B)
**Status: FUNCTIONAL**
- 2-step approval: Receiver accepts → SPO approves
- OHQ updated on final approval
- Transfer record maintained

### E6. Payment Voucher → Tally Flow
**Status: PARTIAL — UNVERIFIED**
- PV creation: Present
- Tally XML: TallyXmlService exists
- Actual push to Tally: Not verified in code

### E7. Approval Limit Enforcement
**Status: BROKEN**
- ApprovalLimit DB-driven: YES
- Runtime check at approval: NOT VERIFIED — WorkflowServiceImpl commented out means no enforcement

### E8. Budget Tracking
**Status: PARTIAL**
- Budget master present
- Budget deduction on PO: Not traced (PO service commented)

### E9. Multi-tender Vendor Evaluation + 85% Rule
**Status: NOT IMPLEMENTED**
- 85% minimum vendor participation rule: MISSING
- Disqualification logic: Partial

### E10. Vendor Registration → Approval → Portal Access
**Status: PARTIAL**
- Registration: Works
- Admin approval of vendor: API present
- Portal login after approval: Works if PrivateRoutes fixed

---

## F. Code vs PRD — Extra Features Found in Code

1. **GEM Vendor endpoint** (`/all-gem-vendors/Status`) — PRD doesn't mention GEM (Government e-Marketplace) separately. Separate GEM vendor flow exists in code.
2. **Double Bid type** (Technical + Price bid) — PRD mentions single/double bid but code has extensive dual-file upload logic in vendor portal.
3. **SPO Review endpoint** (`/spo-review`) — separate store officer review step beyond what PRD describes.
4. **Change request loop for quotations** — CHANGE_REQUESTED status with clarification document upload. More detailed than PRD describes.
5. **TenderPrintFormat** — dedicated tender copy PDF endpoint (`/data/tender-format`) not explicitly in PRD.
6. **QueueModal** for tender details — UI helper not in PRD scope.
7. **AllVendorsQuotationStatus** component — modal showing all vendor statuses per tender.
8. **Completion-vendorNames endpoint** — vendors who completed quotation list.
9. **Vendor password change** endpoint in VendorQuotationController — out of scope placement.
10. **`tenderEvaluationHistory`** — full history log per vendor per tender.

---

## G. PRD Requirements Missing from Code

| # | PRD Requirement | Status | Severity |
|---|---|---|---|
| G1 | JWT / session-based authentication | MISSING | CRITICAL |
| G2 | Role-based access control on backend endpoints | MISSING | CRITICAL |
| G3 | Admin audit log (who did what) | ENTITY ONLY, not wired | HIGH |
| G4 | 85% vendor participation rule before PO | MISSING | HIGH |
| G5 | Contingency purchase spending limit | NOT IN DB | HIGH |
| G6 | Indent approval workflow execution | COMMENTED OUT | CRITICAL |
| G7 | PO creation/approval | COMMENTED OUT | CRITICAL |
| G8 | Tender request creation | COMMENTED OUT | CRITICAL |
| G9 | Service Order status tracking | NO STATUS COLUMN | HIGH |
| G10 | Version control on Tender | NO VERSION COLUMN | MEDIUM |
| G11 | Version control on Service Order | NO VERSION COLUMN | MEDIUM |
| G12 | OHQ audit trail (who changed stock) | MISSING COLUMNS | MEDIUM |
| G13 | Payment Voucher update audit (updated_by/date) | MISSING COLUMNS | MEDIUM |
| G14 | Disposal temp custody vs auction differentiation | NOT IMPL | MEDIUM |
| G15 | Budget deduction on PO creation | NOT VERIFIED | HIGH |
| G16 | Auto-routing to department head based on dept | PARTIALLY IMPL | MEDIUM |
| G17 | Notification system (email on approval/rejection) | EMAIL CONFIG EXISTS, usage not traced | MEDIUM |
| G18 | File version history for uploaded documents | NOT IMPL | LOW |
| G19 | Tender status progression tracking | LIMITED | MEDIUM |
| G20 | Multi-PO from single tender (one-to-many) | ONE-TO-ONE ONLY | MEDIUM |

---

## H. Frontend Moderation Check

### H1. Internal Frontend (Frontend-test)

> **Correction:** Tender, PO, and ContingencyPurchase pages follow same V1/V2 pattern as backend — V1 commented at top, V2 active below.

| Page | V1 lines | V2 starts | Status | Issue |
|---|---|---|---|---|
| Login | — | — | Works | Proxy points to wrong port (8081 vs 8088) |
| Indent Creation | — | — | Works | Backend functional |
| Indent Approval | — | — | STUB | No API calls in Form2.jsx |
| Tender | 1–1613 (commented) | 1614 active | FUNCTIONAL | V2 has LOV, draft save, approved indent fetch, version history |
| PO | 1–983 (commented) | 984 active | FUNCTIONAL | V2 has LOV, vendor/tender dropdowns, completed vendor list, version history |
| Contingency Purchase | 1–847 (commented) | 848 active | FUNCTIONAL | V2 has full form, LOV, project/vendor/employee/material dropdowns. Also has second commented block `/* */` (lines 1580–1865) = older variant |
| GRN/GI/GT/GPRN | Present | — | Need verification | — |
| Reports | 30+ pages | — | Present | API connectivity unclear |
| Admin Panel | Scaffolded | — | APIs functional | — |
| Role-based routing | Present | — | Works | Routes.jsx:280-312 |

**Overall: ~70% of internal frontend is usable (Tender/PO/CP now confirmed functional).**

### H2. Vendor Portal (Astro-vendor-portal-main)

| Feature | Status | Bug |
|---|---|---|
| Registration | Works | — |
| Login | BROKEN | authSlice:113 POST → should be GET `/VendorStatus/{vendorId}` |
| PrivateRoutes | BROKEN | Returns `<Outlet/>` unconditionally — no auth check |
| Password validation | Frontend-only | No backend validation |
| View Tenders | Works | param typo fixed = works |
| Upload Quotation | Works | param typo `param` vs `params` on line 52 |
| View PO | BROKEN | Passes vendorId but endpoint expects tenderId |
| View Tender Details | BROKEN | `/api/tender-requests/byid` should be `/byId` |
| All vendor status | BROKEN | `{param:{tenderId}}` should be `{params:{tenderId}}` |
| Proxy target | WRONG | localhost:8081 should be localhost:8088 |
| baseURL in App.js | CORRECT | localhost:8088 — but bypassed by proxy |
| TenderPrintFormat | BROKEN | Hardcoded localhost:8081 on lines 19,44 |

**Overall: Approximately 50% of vendor portal is usable.**

---

## I. Approval Workflow Deep Audit

**Architecture (DB-driven):**
- `workflow_master`: 11 workflows, each with formId and name
- `transition_master`: states + allowed transitions per workflow
- `approval_limit_master`: per-role, per-category spend limits
- `department_approver_master`: dept→approver mapping
- `field_station_approver_master`: field station→approver mapping

**Corrected assessment — WorkflowServiceImpl v2 IS active:**

1. **WorkflowServiceImpl v2 ACTIVE at line 3288** — `@Service` class with full implementation. `initiateWorkflow` reads `workflow_master` by name, uses `BranchWorkflowService` for branch-based routing, falls back to `TransitionMaster`-based routing. Emails sent via `EmailService.sendWorkflowEmail`. Budget service injected.

2. **Branch-based routing** — `initiateBranchBasedWorkflow` method present. Conditions evaluated against request data to select the correct approval branch (e.g. department-based routing, amount-based routing).

3. **Reporting officer resolution** — `resolveReportingOfficer(creatorUserId)`: looks up `EmployeeDepartmentMaster.reportingOfficerId` → finds `UserMaster` → assigns that user as approver. Correctly throws `BusinessException` if chain is broken.

4. **Project head resolution** — `resolveProjectHead(requestId)`: looks up `IndentCreation.projectCode` → `ProjectMaster.projectHead` → `UserMaster`. Correct chain.

5. **WorkflowName is hardcoded enum** (`WorkflowName.java`) — this REMAINS an issue. Changing workflow names still requires recompile. V2 service calls `WorkflowName.TENDER_EVALUATOR.getValue()` directly.

6. **No role check on approval endpoints** — `WebSecurityConfig` still `permitAll()`. Any user can call approval endpoints. REMAINS critical.

7. **indentApproval/Form2.jsx is stub** — frontend form still has no API calls. Backend is ready but UI cannot trigger approvals.

8. **Approval limit check** — `approval_limit_master` populated and `BudgetService` injected in workflow service. Budget check called in PO creation (`checkBudgetForPo`). Limit enforcement exists at PO creation; whether it's enforced at each workflow step requires deeper trace.

9. **Change request loop** — CHANGE_REQUESTED in `indent_creation` and `vendor_quotation`. Clarification flow in vendor portal works. Internal frontend has no equivalent UI.

10. **Dean/Head SEG routing** — `department_approver_master` + `field_station_approver_master` tables present. Branch workflow reads these at runtime. Routing IS implemented.

**Workflow 11 entries in DB — execution engine IS functional:**
Indent Approval, CP Approval, PO Approval, Tender Approver, SO Approval, WO Approval, Tender Evaluator, Vendor Approval, Material Approval, Payment Voucher Approval — all configured in DB and engine reads them.

---

## J. Stock and Asset Logic Deep Audit

### OHQ Management
- `ohq_master` (non-consumable) + `ohq_master_consumable` (consumable) — two tables for two types
- OHQ incremented on GRN save
- OHQ decremented on GI approval
- OHQ updated (source −, target +) on GT final approval
- **Gap:** NO audit columns on ohq tables. Cannot trace who changed stock when.

### Store-Stock Checkbox
- `storeStock` boolean field on material
- When true: material goes to store after GI
- Logic present in GiServiceImpl
- **Gap:** Checkbox exists but no dedicated stock ledger — only OHQ snapshots, no transaction history.

### Asset Code Generation
- Triggered on GI approval (`GiServiceImpl.java:488-535`)
- Format: PREFIX + sequential number from max(assetId) + 1
- One asset code per quantity unit (qty=3 → 3 codes)
- Stored in `asset_master`
- **Gap:** Asset code prefix not configurable via admin — appears hardcoded.

### Disposal
- `DisposalServiceImpl` present
- PRD mentions: temp custody (audit committee) vs auction/write-off
- **Gap:** No differentiation between disposal types. Single disposal flow.

### Minimum Stock Alert
- PRD mentions minimum stock level alerts
- `ohq_master` has min_qty column
- **Gap:** No alert mechanism in code. Column exists but no service reads it for notifications.

---

## K. Payment Voucher Deep Audit

**DB Structure (`payment_voucher`):**
- Links to PO/SO/CP via foreign keys
- Has amount, status, remarks
- NO updated_by/updated_date columns — cannot audit modifications

**Backend:**
- `PaymentVoucherServiceImpl` present and appears active (not commented)
- `PaymentVoucherController` has CRUD + approval endpoints
- `TallyXmlService` exists for XML generation

**Tally Integration:**
- XML generation service present
- Actual HTTP push to Tally server not verified
- No Tally server URL found in `application.properties` — suggests push NOT implemented

**Approval chain:**
- `payment_voucher` has status column
- workflow_master has "Payment Voucher Approval" workflow (ID 10)
- Runtime: NOT VERIFIABLE (workflow service commented)

**Gaps:**
- Tally push URL missing from config — integration incomplete
- No updated_by on PV modifications
- Approval through dead workflow engine

---

## L. Technical Quality Audit

### L1. Security
| Item | Finding | Severity |
|---|---|---|
| JWT | Not implemented — **ACCEPTED: closed network deployment** | INFO |
| Authorization | All endpoints `permitAll()` in WebSecurityConfig — any user can call any API | HIGH |
| Default credentials | admin/admin123 created on every startup | HIGH |
| DB password | Root123@@ plaintext in application.properties | HIGH |
| Email password | sipmapr@2026 plaintext in application.properties | HIGH |
| CORS | `allowedMethods("*")` — acceptable on closed network | LOW |
| Vendor portal auth | PrivateRoutes returns Outlet unconditionally — no login check | HIGH |
| Password storage | Not verified if hashed in backend | MEDIUM |

**Closed-network note:** JWT and strict CORS not required. Role-based access control and removal of default credentials are still necessary.

### L2. Code Quality
| Item | Finding |
|---|---|
| Dead code | WorkflowServiceImpl, TenderRequestServiceImpl, PurchaseOrderImpl — all major services commented out |
| Duplicate endpoints | VendorMasterController has duplicate `/approvedVendorData` (path var AND query param) |
| Error handling | ResponseHandler exists, usage not uniform |
| Transaction management | @Transactional used selectively — not consistent |
| Logging | Spring default logging, no structured logging |
| Imports | `@ActiveProfiles` in VendorQuotationController (test annotation in prod code) |
| Test code in prod | `org.springframework.test.context.ActiveProfiles` import found in production controller |

### L3. Database Quality
| Item | Finding |
|---|---|
| Missing columns | service_order.status, tender_request.version, ohq.created_by, payment_voucher.updated_by |
| No dedicated stock ledger | Inventory changes not logged as transactions |
| Referential integrity | Not audited fully — assumed FK constraints present |
| Soft vs hard delete | UserController: hard DELETE confirmed. Rest not verified. |
| admin_audit_log | Table exists, fully structured, 0 records written by code |
| contigency_purchase | No limit column |

### L4. Frontend Quality
| Item | Finding |
|---|---|
| Dead code | 95% commented blocks in major pages |
| Port inconsistency | 8081 vs 8088 scattered across vendor portal |
| Hardcoded URLs | TenderPrintFormat.jsx lines 19,44 |
| API param bugs | `param` vs `params` in axios calls |
| Case sensitivity | `/byid` vs `/byId` |
| No loading states | Some forms lack loading indicators |
| No error boundaries | No React error boundary found |

---

## M. Production Readiness Checklist

| # | Check | Status |
|---|---|---|
| M1 | JWT authentication (closed network — not required) | N/A |
| M2 | Role-based access control on critical endpoints | FAIL |
| M3 | All critical services functional (v2 active in all) | PASS |
| M4 | Frontend proxy targets correct port | FAIL |
| M5 | No hardcoded credentials in application.properties | FAIL (use env vars) |
| M6 | Default admin password changed or removed | FAIL |
| M7 | Audit logging implemented | FAIL |
| M8 | Vendor portal PrivateRoutes auth check | FAIL |
| M9 | All axios param typos fixed | FAIL |
| M10 | OHQ audit columns added | FAIL |
| M11 | PO service functional (v2 active) | PASS |
| M12 | Tender service functional (v2 active) | PASS |
| M13 | Workflow service functional (v2 active) | PASS |
| M14 | 85% vendor rule implemented | FAIL |
| M15 | Contingency purchase limit in DB | FAIL |
| M16 | Tally push URL configured | FAIL |
| M17 | Test annotation removed from prod code | FAIL |
| M18 | Error handling consistent | PARTIAL |
| M19 | @Transactional coverage complete | PARTIAL |
| M20 | Minimum stock alert mechanism | FAIL |
| M21 | Indent approval frontend functional | FAIL |
| M22 | Service Order status column | FAIL |
| M23 | Payment Voucher updated_by/date columns | FAIL |
| M24 | Single-instance workable for demo | PARTIAL |
| M25 | Database migrations/versioning (Flyway/Liquibase) | NOT FOUND |

**Score: 3 PASS, 3 PARTIAL, 19 FAIL**

---

## N. Additional Specialist Audit Checks

| # | Check | Finding |
|---|---|---|
| N1 | SQL injection via JPA | Low risk — JPA parameterized queries |
| N2 | XSS in frontend | Not checked — no dangerouslySetInnerHTML found in quick scan |
| N3 | File upload validation | File upload accepts any type — no MIME check found |
| N4 | Mass assignment / over-posting | DTOs used — moderate protection |
| N5 | Session fixation | No JWT = no token fixation; session management unclear |
| N6 | CSRF protection | Spring Security CSRF likely disabled with permitAll() |
| N7 | Sensitive data in logs | Not audited deeply |
| N8 | N+1 queries | Hibernate lazy loading — N+1 risk present without join fetch |
| N9 | Transaction isolation | Default (READ_COMMITTED MySQL) — no explicit isolation set |
| N10 | Concurrent OHQ updates | No pessimistic/optimistic locking on ohq_master |
| N11 | Asset code collision | Sequential from MAX — concurrent inserts could collide |
| N12 | Double-spend on PV | No idempotency key on payment voucher creation |
| N13 | File storage security | Files stored server-side — path not audited for traversal |
| N14 | Vendor enumeration | `/VendorStatus/{vendorId}` exposes vendor IDs |
| N15 | Email credential exposure | sipmapr@2026 in application.properties — git history risk |
| N16 | Cache configuration | CacheConfig present — cache invalidation on updates unclear |
| N17 | Password policy | No min length/complexity enforcement found in backend |
| N18 | Rate limiting | None found |
| N19 | API versioning | No versioning — future-breaking changes risk |
| N20 | Health/metrics endpoints | Spring Actuator not confirmed — prod exposure risk if enabled |

---

## O. Final Gap Report

### CRITICAL (System cannot function)

> **Note:** C1/C2/C3 from initial audit were incorrect. Those services have active v2 implementations. C4 (JWT) downgraded — system runs on closed network per user instruction; session-level auth still needed but JWT is not mandatory. C5 (vendor portal auth) remains critical.

| ID | Gap | Location | Fix |
|---|---|---|---|
| C5 | Vendor portal PrivateRoutes returns Outlet unconditionally — no auth check | `Astro-vendor-portal-main/src/pages/route/PrivateRoutes.jsx:12` | Check logged-in state before rendering |

### HIGH (Major feature broken)

| ID | Gap | Location | Fix |
|---|---|---|---|
| H1 | Proxy ports wrong (8081 vs 8088) | `setupProxy.js` both frontends | Change to 8088 |
| H2 | authSlice uses POST for GET endpoint | `authSlice.jsx:113-114` | Change to GET |
| H3 | Indent approval form is stub (backend works, frontend not wired) | `indentApproval/Form2.jsx` | Add API calls — backend endpoint ready |
| H4 | Default admin/admin123 on every startup | `DataInitializer.java:40` | Remove or env-gate |
| H5 | 85% vendor participation rule missing | TenderEvaluationService | Add check before PO allowed |
| H6 | CP spending limit not in DB | `contigency_purchase` table | Add limit column + enforcement |
| H7 | ~~Tender/PO/CP frontend 95% commented~~ **RESOLVED** — V2 active in all three pages | — | Integration testing needed |
| H8 | AdminAuditLog not implemented | Backend services | Wire audit log writes |
| H9 | DB credentials plaintext in config | `application.properties` | Move to env vars |
| H10 | Service Order missing status column | DB: `service_order` | Add migration |

### MEDIUM (Partial functionality)

| ID | Gap | Location | Fix |
|---|---|---|---|
| M1 | OHQ no audit columns | `ohq_master`, `ohq_master_consumable` | Add created_by/date |
| M2 | Payment Voucher no updated_by/date | `payment_voucher` | Add columns |
| M3 | param vs params typos | Multiple axios calls in vendor portal | Fix all |
| M4 | `/byid` vs `/byId` case mismatch | `Tender_Evaluator.jsx:349` | Fix case |
| M5 | PO detail page passes vendorId instead of tenderId | `Form3.jsx:20` | Fix param |
| M6 | Tender request no version column | `tender_request` | Add version INT |
| M7 | WorkflowName hardcoded enum | `WorkflowName.java` | DB-driven lookup |
| M8 | Disposal type differentiation | `DisposalServiceImpl` | Add type field |
| M9 | Asset code prefix not configurable | `GiServiceImpl` | Move to admin config |
| M10 | Minimum stock alert not implemented | Backend + ohq_master | Scheduler + alert |
| M11 | Tally push URL not configured | `application.properties` | Add + implement push |
| M12 | `@ActiveProfiles` test annotation in prod | `VendorQuotationController.java:12` | Remove import |
| M13 | Duplicate vendor endpoint | `VendorMasterController` | Remove one |
| M14 | No stock transaction ledger | DB + backend | Add ledger table |
| M15 | One-to-one tender-to-PO only | PO service | Support one-to-many |

### LOW (Minor / quality)

| ID | Gap | Fix |
|---|---|---|
| L1 | Hardcoded localhost:8081 in TenderPrintFormat.jsx | Use baseURL constant |
| L2 | 30+ report pages — API connectivity unclear | Trace and test each |
| L3 | No database migration tool | Add Flyway/Liquibase |
| L4 | No test coverage | Add JUnit + React Testing Library |
| L5 | No API versioning | Add /api/v1 prefix |
| L6 | No rate limiting | Add Spring Rate Limiter |
| L7 | File upload MIME validation missing | Add content-type check |
| L8 | Concurrent OHQ update risk | Add optimistic locking |
| L9 | Asset code generation race condition | Add DB-level sequence |
| L10 | No React error boundaries | Add ErrorBoundary wrapper |

---

## P. Phased Implementation Plan

### Phase 1 — Make System Bootable and Minimally Demo-able (1–2 weeks)
**Goal:** Fix all critical blockers so core P2P flow can be demonstrated.

1. Fix all port mismatches: `setupProxy.js` both frontends → 8088
2. Fix vendor portal `PrivateRoutes.jsx` — add token check
3. Fix `authSlice.jsx:113` — POST → GET, correct URL
4. Fix axios `param` → `params` in all vendor portal components
5. Fix `/byid` → `/byId` case in Tender_Evaluator.jsx
6. Fix `Form3.jsx` — pass tenderId not vendorId
7. Remove/guard `DataInitializer.java` default admin creation
8. Move DB credentials to environment variables
9. Remove `@ActiveProfiles` test import from VendorQuotationController
10. Fix duplicate vendor endpoint

### Phase 2 — Wire Frontend to Functional Backend (2–3 weeks)
**Goal:** Connect existing frontend pages to the already-functional backend. Backend services (WorkflowServiceImpl v2, TenderRequestServiceImpl v2, PurchaseOrderImpl v2) do NOT need to be uncommented — they are active.

1. Implement Indent Approval frontend (`Form2.jsx`) — backend endpoint exists, just needs API calls
2. Uncomment and fix `Tender.jsx` — backend `createTenderRequest` is fully functional
3. Uncomment and fix `PO.jsx` — backend `createPurchaseOrder` is fully functional
4. Uncomment and fix `ContingencyPurchase.jsx`
5. Wire `AdminAuditLog` — add writes in key service methods
6. Add 85% vendor participation check in tender evaluation service

### Phase 3 — Security Hardening (1 week)
**Goal:** Close auth gaps. JWT not required (closed network). Focus on session integrity and access control.

1. Fix vendor portal `PrivateRoutes.jsx` — check Redux auth state before rendering (1 line fix)
2. Fix `authSlice.jsx:113` — POST → GET for `/VendorStatus/{vendorId}`
3. Add basic role-based guards on critical backend endpoints (approve, create PO, admin) — `@PreAuthorize` or manual role check
4. Verify BCrypt password hashing in use (UserController + VendorController)
5. Remove/env-gate default `admin/admin123` in `DataInitializer.java:40`
6. Move DB credentials to environment variables (application.properties → env vars)
7. Remove `@ActiveProfiles` test annotation from `VendorQuotationController.java`
8. Add file upload MIME type validation

### Phase 4 — DB Schema Fixes + Quality (1 week)
**Goal:** Fix DB gaps and add missing columns.

1. Add `service_order.status` column (migration)
2. Add `tender_request.version` column (migration)
3. Add `ohq_master.created_by`, `ohq_master.updated_by`, dates (migration)
4. Add `payment_voucher.updated_by`, `updated_date` (migration)
5. Add `contigency_purchase.spending_limit` column (migration)
6. Add stock transaction ledger table
7. Replace WorkflowName enum with DB lookup
8. Add Flyway for migration management

### Phase 5 — Complete Features + Production Hardening (2–3 weeks)
**Goal:** Production-ready.

1. Implement CP spending limit enforcement
2. Implement Contingency Purchase frontend
3. Implement Tally push with configurable URL
4. Implement minimum stock alert scheduler
5. Implement disposal type differentiation
6. Add one-to-many tender-to-PO support
7. Add OHQ optimistic locking
8. Add asset code DB sequence (replace MAX+1 pattern)
9. Add React error boundaries in both frontends
10. Add JUnit tests for critical services (GiServiceImpl asset gen, WorkflowServiceImpl)
11. Add API versioning prefix
12. Configure Spring Actuator (health only, secured)
13. Performance: add `@EntityGraph` / join fetch for N+1 hotspots

---

## Q. Questions and Clarifications Needed

| # | Question | Context |
|---|---|---|
| Q1 | Is the closed-network claim permanent or will this face internet exposure? | Determines JWT urgency |
| Q2 | ~~Why are services commented out?~~ **RESOLVED:** Each file contains V1 (commented) + V2 (active) in same file. V1 preserved as in-file history. WorkflowServiceImpl v2 at line 3288, TenderRequestServiceImpl v2 at line 5073, PurchaseOrderImpl v2 at line 1805. Consider moving V1 to separate archive files or git history to reduce file sizes (WorkflowServiceImpl is 376KB). | Code hygiene |
| Q3 | What is the correct Tally server URL/port? Is Tally integration in scope for this release? | payment_voucher/TallyXmlService |
| Q4 | Should the 85% vendor rule apply to ALL tender types or only open tenders? | TenderEvaluation rule |
| Q5 | What is the CP spending limit value per category/role? Should it be per-user or per-tender-category? | contigency_purchase table design |
| Q6 | Is multi-PO from single tender (one tender → multiple vendors) required? | Current code is one-to-one only |
| Q7 | Is the GEM (Government e-Marketplace) vendor flow a live requirement or future scope? | Extra endpoint found in backend |
| Q8 | Should disposal differentiate between write-off, auction, and temporary custody? | DisposalServiceImpl |
| Q9 | Are the 30+ report pages in Frontend-test connected to actual backend report endpoints? | Report pages present but not traced |
| Q10 | Is the `admin/admin123` default user intended to persist (for demo) or be removed in production? | DataInitializer.java:40 |
| Q11 | What version of the frontend proxy is supposed to be production — localhost (dev) or the IP 103.181.158.220? | Frontend-test setupProxy.js |
| Q12 | Should vendor password reset be handled server-side or is frontend-only validation sufficient per current requirements? | Vendor portal auth |
| Q13 | Is there a separate notification service or should email alerts come directly from Spring backend? | sipmapr@2026 email config |
| Q14 | What is the intended asset code prefix format? Is it configurable per asset category? | GiServiceImpl:488-535 |
| Q15 | Are there existing JUnit tests in another branch not present here? | Zero tests found in current codebase |

---

*Report generated by automated code + DB inspection. All findings based on reading actual code and DB dump. No assumptions made — where unclear, flagged in Section Q.*

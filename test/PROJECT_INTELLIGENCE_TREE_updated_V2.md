# Project Intelligence Tree

## 1. Project Intelligence Tree

<root>
├── backend/ (`Backend-prod`)
│   ├── entry/
│   │   ├── `src/main/java/com/astro/BackendServiceApplication.java`
│   │   │   - Purpose: Spring Boot entrypoint.
│   │   │   - Key methods: `main`.
│   │   │   - Dependencies: Spring Boot auto-configuration.
│   │   ├── `src/main/resources/application.properties`
│   │   │   - Purpose: runtime config for MySQL, multipart, mail, cache, context path `/astro-service`.
│   │   │   - Key fields: `server.port=8088`, `spring.jpa.hibernate.ddl-auto=update`, `filePath=C://astro//document//`.
│   │   │   - Dependencies: MySQL, JavaMail, local file storage.
│   │   └── `src/main/java/com/astro/config/*`
│   │       - `WebSecurityConfig`: CORS-only MVC config; no real auth/security chain.
│   │       - `AppConfig`/`PasswordConfig`: bean wiring incl. password encoder.
│   │       - `SwaggerConfigurations`: Swagger 2 setup.
│   │       - `CacheConfig`: simple cache for LOV/admin reads.
│   │       - `ResponseHandler`: centralized response wrapping.
│   │       - `DataInitializer`: startup seed/bootstrap logic.
│   ├── controller/
│   │   ├── auth + user
│   │   │   - `UserController`: `/login` -> `UserService.login`; returns multi-role login payload.
│   │   │   - `UserMasterController`: CRUD/search/toggle/change-password for internal users; calls `UserService`.
│   │   │   - `EmployeeDepartmentMasterController`: employee CRUD, draft workflow, lookup/search; calls `EmployeeDepartmentMasterService`.
│   │   ├── masters
│   │   │   - `MaterialMasterController`, `JobMasterController`, `WorkMasterController`, `ProjectMasterController`, `LocationMasterController`, `LocatorMasterController`, `UomMasterController`, `RoleMasterController`.
│   │   │   - Purpose: master CRUD/search + queue-driven actions.
│   │   ├── procurement
│   │   │   - `IndentCreationController` (`/api/indents`)
│   │   │     - Purpose: create/update/search/assign/cancel indents, history, cancellation requests, version history.
│   │   │     - Key methods: `createIndent`, `updateIndent`, `getIndentById`, `getIndentDataForTenderById`, `getIndentDataById`, `searchIndents`, `assignEmployee`, `requestIndentCancellation`, `approveCancellationRequest`, `getIndentVersionHistory`.
│   │   │     - Dependencies: `IndentCreationService`, `WorkflowService`.
│   │   │     - VERSIONING NOTE: All endpoints that accept `indentId` use `@RequestParam` (not `@PathVariable`) because versioned IDs like `IND1111/2` contain a `/` that breaks path variable parsing.
│   │   │       - `GET /api/indents/byId?indentId=` (was `GET /{indentId}`)
│   │   │       - `GET /api/indents/indentData?indentId=` (was `GET /indentData/{indentId}`)
│   │   │       - `GET /api/indents/IndentDataForTender?id=` (was `GET /IndentDataForTender/{indentId}`)
│   │   │       - `PUT /api/indents?indentId=` (was `PUT /{indentId}`)
│   │   │       - `GET /api/indents/version-history/{indentId}` — new endpoint returning all versions of an indent family.
│   │   │   - `TenderRequestController` (`/api/tender-requests`)
│   │   │     - Purpose: tender CRUD/search/approved tender picks/cancel.
│   │   │     - Key methods: `createTenderRequest`, `updateTenderRequest`, `getTenderRequestById`, `getApprovedTenderIdsForTenderEvaluation`.
│   │   │     - Dependencies: `TenderRequestService`, `WorkflowService`, `UtilProcurementService`.
│   │   │   - `TenderEvaluationController` (`/api/tender-evaluation`)
│   │   │     - Purpose: save tender evaluation result.
│   │   │     - Dependencies: `TenderEvaluationService`.
│   │   │   - `VendorQuotationController` (`/api/vendor-quotation`)
│   │   │     - Purpose: vendor quotation upload/versioning, accept/reject/change-request, vendor login status/password.
│   │   │     - Key methods: `createVendorQuotation`, `getVendorQuotationByTenderId`, `acceptVendorQuotation`, `storeOfficerReviewQuotation`, `changePassword`.
│   │   │     - Dependencies: `VendorQuotationAgainstTenderService`.
│   │   │   - `PurchaseOrderController` (`/api/purchase-orders`)
│   │   │     - Purpose: PO CRUD/search/material history/PO format.
│   │   │     - Key methods: `createPurchaseOrder`, `updatePurchaseOrder`, `getPurchaseOrderById`, `searchPoIds`.
│   │   │     - Dependencies: `PurchaseOrderService`, `WorkflowService`.
│   │   │   - `ServiceOrderController` (`/api/service-orders`)
│   │   │     - Purpose: SO CRUD.
│   │   │     - Dependencies: `ServiceOrderService`, `WorkflowService`.
│   │   │   - `ContigencyPurchaseController` (`/api/contigency-purchase`)
│   │   │     - Purpose: contingency purchase CRUD/report/search.
│   │   │     - Dependencies: `ContigencyPurchaseService`, `WorkflowService`.
│   │   ├── inventory
│   │   │   - `ProcessController` (`/api/process-controller`)
│   │   │     - Purpose: umbrella orchestration endpoint for GPRN/GI/GRN/GRV/ISN/IGP/OGP/GT/DI/asset disposal/payment voucher.
│   │   │     - Key methods: `saveGprn`, `saveGi`, `saveGrn`, `saveIgp`, `saveOgp`, `createGt`, `approveGt`, `approveGi`, `approveGrn`, `getPending*`, `paymentVoucher*`.
│   │   │     - Dependencies: `ProcessService`, `GiService`, `IgpService`, `GrnService`, `AssetMasterService`, `GtService`, `OgpService`, `DiService`, `PaymentVoucherService`, `WorkflowService`.
│   │   │   - `AssetMasterController` / `AssetController`
│   │   │     - Purpose: asset master save/update/search/disposal/serial management.
│   │   │     - Dependencies: `AssetMasterService`, `AssetService`.
│   │   │   - Other inventory CRUD controllers: `GRIController`, `GoodsInspectionController`, `GoodsTransferController`, `GoodsReturnController`, `GatepassController`.
│   │   ├── admin panel
│   │   │   - `ApproverController` (`/api/admin/approvers`)
│   │   │     - Purpose: dynamic workflow approvers, branches, next-level lookup, full workflow config.
│   │   │     - Key methods: branch CRUD, approver CRUD, `with-shift`, `full-config`.
│   │   │   - `ApprovalLimitController`, `DepartmentApproverController`, `FieldStationApproverController`, `LOVController`, `BudgetController`.
│   │   │   - Purpose: approval rule engine configuration + dropdown metadata + budgets.
│   │   ├── reports
│   │   │   - `Reports`, `InventoryReports`, `TallyIntegrationController`.
│   │   │   - Purpose: report endpoints and Tally export/integration.
│   │   └── file/util
│   │       - `FileProcessingController` (`/file`)
│   │       - Purpose: file upload/download handling for form attachments.
│   ├── service/
│   │   ├── workflow core
│   │   │   - `WorkflowServiceImpl`
│   │   │     - Purpose: central dynamic workflow engine for request routing, queue, transitions, history.
│   │   │     - Key methods: `initiateWorkflow`, queue/history/action helpers, workflow branch evaluation.
│   │   │     - Dependencies: workflow master/transition/state/action/admin branch repositories.
│   │   │     - Note: very large hotspot (~5732 lines).
│   │   ├── procurement
│   │   │   - `IndentCreationServiceImpl`
│   │   │     - Purpose: build indent aggregate, validate mode/vendor count, save files, compute value, cancellation flow, indent versioning.
│   │   │     - Key methods: `createIndent`, `updateIndent`, `getIndentVersionHistory`, `searchIndentIds`, `assignEmployeeToIndent`, `requestIndentCancellation`, `approveCancellationRequest`, `extractBaseIndentId`.
│   │   │     - Dependencies: indent/material/job repos, workflow transition repo, vendor/job mapping repo, user/employee repos, PO repos, email.
│   │   │     - Transaction boundaries: `createIndent`, `updateIndent`, `requestIndentCancellation`, `approveCancellationRequest`.
│   │   │     - VERSIONING LOGIC (added):
│   │   │       - `createIndent`: sets `version=1`, `isActive=true`, `parentIndentId=null`.
│   │   │       - `updateIndent`: copy-new pattern — old row gets `isActive=false`, old pending workflow transitions get `status=SUPERSEDED` + `nextAction=null`; new row inserted with `indentId = baseId + "/" + newVersion` (e.g. `IND1111/2`), fresh workflow initiated for new ID.
│   │   │       - `extractBaseIndentId(String)`: helper — strips `/N` suffix to get base ID (`IND1111/2` → `IND1111`).
│   │   │       - `getIndentVersionHistory(String)`: returns all versions of an indent family ordered by version desc.
│   │   │       - Only the original `createdBy` user can call `updateIndent`; others are rejected with 400.
│   │   │   - `TenderRequestServiceImpl`
│   │   │     - Purpose: create tender from approved indents, lock indents, file handling, search, approved-tender lookup.
│   │   │     - Key methods: `createTenderRequest`, `updateTenderRequest`, `updateTender`, `searchTenderIds`, `getApprovedTenderIdsForTenderEvaluation`.
│   │   │     - Dependencies: tender repo, indent repos, vendor quotation repo/service, workflow repo/service.
│   │   │   - `VendorQuotationAgainstTenderServiceImpl`
│   │   │     - Purpose: quotation versioning and state machine across vendor -> indentor -> store purchase officer.
│   │   │     - Key methods: `saveQuotation`, `getQuotationsByTenderId`, `markQuotationForChangeRequest`, `acceptVendorQuotation`, `storeOfficerReviewQuotation`, `changePassword`.
│   │   │     - Dependencies: vendor/vendor-login repos, tender repos, workflow transition repo, GeM tracker.
│   │   │     - Transaction boundaries: `changePassword`; versioning writes use multiple saves.
│   │   │   - `PurchaseOrderImpl`
│   │   │     - Purpose: create/update PO aggregate, version history, lock tender after PO, reporting/format generation.
│   │   │     - Key methods: `createPurchaseOrder`, `updatePurchaseOrder`, `getPurchaseOrderById`, `getPoFormatDetails`, report methods.
│   │   │     - Dependencies: PO repos, tender/indent repos/services, vendor/project/material repos, signature/freight/address repos.
│   │   │   - `ServiceOrderServiceImpl`
│   │   │     - Purpose: create/update SO aggregate from tender/job material selection, report generation.
│   │   │     - Key methods: `createServiceOrder`, `updateServiceOrder`, `getServiceOrderById`, approved/pending SO reports.
│   │   │   - `ContigencyPurchaseServiceImpl`
│   │   │     - Purpose: CP aggregate save/update/report/search with nested `CpMaterials`.
│   │   ├── inventory
│   │   │   - `ProcessServiceImpl`
│   │   │     - Purpose: thin orchestrator delegating process-stage actions to GPRN/GI/GRN/GRV/ISN/IGP/OGP/GT services.
│   │   │     - Key methods: `save*`, `getSubProcessDtls`, OHQ reports.
│   │   │   - `GrnServiceImpl`
│   │   │     - Purpose: GRN save/update/approval/history/payment-voucher bridge.
│   │   │     - Key methods: `saveGrn`, `getGrnDtls`, `approveGrn`, `rejectGrn`, `changeReqGrn`, `saveMaterialGrn`, `getPendingGrns`, `resolveConsigneeName`.
│   │   │     - Consignee rule (added): `resolveConsigneeName` auto-populates consignee on GRN save. Single-indent tender → indentor name from `IndentCreation.indentorName`. Multi-indent tender → store person name sent from frontend. Reuses `GprnMasterRepository` access-check queries shared with GI.
│   │   │   - `GiServiceImpl`
│   │   │     - Purpose: GI save/approval/rejection/change-request/history/pending dropdowns.
│   │   │     - Key methods: `saveGi`, `getGiDtls`, `approveGi`, `rejectGi`, `changeReqGi`, `getGiByStatuses`, `getPendingGprnsForGI`, `isGprnAccessibleToUser`.
│   │   │     - Access rule: `isGprnAccessibleToUser` gates dropdown + save + update. Store person = master. Indentor = single-indent tender only where they are the creator.
│   │   │   - `GtServiceImpl`
│   │   │     - Purpose: goods transfer creation + sender/receiver approval chain + reports.
│   │   │     - Key methods: `createGt`, `approveGt`, `receiverApproveGt`, `rejectGt`, `getPendingGt`, `getGtReport`.
│   │   │   - `IgpServiceImpl`
│   │   │     - Purpose: inward gate pass / material IGP save/approval/report.
│   │   │   - `OgpServiceImpl`
│   │   │     - Purpose: outward gate pass, rejected GI outward, GT-outward subflow.
│   │   │   - `AssetMasterServiceImpl`
│   │   │     - Purpose: asset master CRUD, disposal approvals/auction, serial search/update, GT asset dataset.
│   │   │   - Supporting: `GprnServiceImpl`, `DiServiceImpl`, `IsnServiceImpl`, `GoodsInspectionServiceImpl`, `GoodsReceiptInspectionServiceImpl`, `GoodsTransferServiceImpl`, `GatepassServiceImpl`.
│   │   ├── admin
│   │   │   - `ApprovalLimitServiceImpl`: rule matching and escalation checks.
│   │   │   - `DepartmentApproverServiceImpl`: dean/head-SEG mapping and amount-based lookup.
│   │   │   - `FieldStationApproverServiceImpl`: field-station in-charge lookup/config.
│   │   │   - `LOVServiceImpl`: form/designator/lov CRUD + dropdown aggregation; `@Transactional`.
│   │   └── utilities
│   │       - `EmailService`, `TenderEmailService`, `PdfGeneratorService`, `UtilProcurementService`, `ResponseBuilder`, `CommonUtils`.
│   ├── repository/
│   │   ├── pattern
│   │   │   - Mostly `JpaRepository` + custom JPQL/native queries for search/report/queue.
│   │   ├── workflow/admin
│   │   │   - `WorkflowTransitionRepository`, `WorkflowMasterRepository`, `StateMasterRepository`, `TransitionMasterRepository`, `SubWorkflowTransitionRepository`, `ActionMasterRepository`.
│   │   │   - `AdminPanel/*Repository`: approvers, workflow branches, LOVs, budgets, approval limits.
│   │   │   - `WorkflowTransitionRepository` (added): `findPendingTransitionsByRequestId(requestId)` — returns transitions where `nextAction = 'Pending'` for a given requestId; used by versioning to supersede old workflow rows.
│   │   ├── procurement
│   │   │   - `IndentCreationRepository`, `MaterialDetailsRepository`, `JobDetailsRepository`, `IndentIdRepository`, `TenderRequestRepository`, `TenderEvaluationRepository`, `PurchaseOrderRepository`, `PurchaseOrderAttributesRepository`, `ServiceOrderRepository`, `ServiceOrderMaterialRepository`, `ContigencyPurchaseRepository`, `CpMaterialRepository`.
│   │   │   - `IndentCreationRepository` (added queries):
│   │   │     - `findAllVersionsByBaseId(baseId)` — all versions matching `indentId = baseId OR indentId LIKE baseId/%`, ordered by version desc.
│   │   │     - `findActiveVersionByBaseId(baseId)` — single active version (`isActive = true`).
│   │   │     - All search queries (`findByIndentIdContaining*`, `findByIndentorName*`, `findByCreatedDateBetween*`) updated to filter `isActive = true` so only current versions appear in search results.
│   │   ├── inventory
│   │   │   - grouped repos per process: `grn`, `gprn`, `igp`, `ogp`, `GiRepository`, `GoodsTransfer`, assets/OHQ/disposal.
│   │   └── master/user/vendor
│   │       - `UserMasterRepository`, `UserRoleMasterRepository`, `EmployeeDepartmentMasterRepository`, `VendorMasterRepository`, `VendorMasterUtilRepository`, `VendorLoginDetailsRepository`, `MaterialMasterRepository`, `ProjectMasterRepository`, etc.
│   ├── entity/
│   │   ├── procurement aggregates
│   │   │   - `IndentCreation`
│   │   │     - Purpose: root aggregate for material/job indent with workflow/lock/escalation metadata.
│   │   │     - Important fields: `indentId`, `indentType`, `materialCategoryType`, `modeOfProcurement`, `totalIntentValue`, `isLockedForTender`, `currentStatus`, `projectCode`, `roProjectDetermination`.
│   │   │     - Versioning fields (added): `version` (Integer, default 1), `isActive` (Boolean, default true), `parentIndentId` (String, null for v1).
│   │   │     - ID scheme: v1 = `IND1111`, v2 = `IND1111/2`, v3 = `IND1111/3`. Base ID derived by stripping `/N` suffix.
│   │   │     - Relations: `OneToMany materialDetails`, `OneToMany jobDetails`, `cascade=ALL`, `orphanRemoval=true`.
│   │   │   - `MaterialDetails`
│   │   │     - Purpose: line items for material indents.
│   │   │     - Important fields: `materialCode`, `quantity`, `unitPrice`, `totalPrice`, `budgetCode`, `materialCategory`, `modeOfProcurement`, `conversionRate`.
│   │   │     - Relations: `ManyToOne indentCreation`.
│   │   │   - `JobDetails`
│   │   │     - Purpose: line items for job/service indents.
│   │   │     - Important fields: `jobCode`, `estimatedPrice`, `totalPrice`, `vendorNames`, `modeOfProcurement`.
│   │   │     - Relations: `ManyToOne indentCreation`.
│   │   │   - `TenderRequest`
│   │   │     - Purpose: tender aggregate over one-or-more indents with file/lock/version metadata.
│   │   │     - Important fields: `tenderId`, `tenderNumber`, `modeOfProcurement`, `vendorId`, `totalTenderValue`, `tenderVersion`, `isLocked`, `lockedForPO`.
│   │   │     - Relations: `OneToMany indentIds`, `cascade=ALL`, `orphanRemoval=true`, `fetch=LAZY`.
│   │   │   - `IndentId`
│   │   │     - Purpose: join entity linking tender to multiple indent IDs.
│   │   │     - Relations: `ManyToOne tenderRequest`.
│   │   │   - `PurchaseOrder`
│   │   │     - Purpose: PO header with version/lock status and nested material pricing rows.
│   │   │     - Important fields: `poId`, `tenderId`, `vendorId`, `totalValueOfPo`, `poVersion`, `isLocked`, `security*`.
│   │   │     - Relations: `OneToMany purchaseOrderAttributes`, `cascade=ALL`, `orphanRemoval=true`.
│   │   │   - `PurchaseOrderAttributes`
│   │   │     - Purpose: PO material lines.
│   │   │     - Important fields: `materialCode`, `quantity`, `rate`, `currency`, `exchangeRate`, `gst`, `duties`, `freightCharge`, `totalPoMaterialPriceInInr`.
│   │   │     - Relations: `ManyToOne purchaseOrder`.
│   │   │   - `ServiceOrder`
│   │   │     - Purpose: SO header for job/service procurement.
│   │   │     - Important fields: `soId`, `tenderId`, `vendorId`, `jobCompletionPeriod`, `startDateAmc`, `endDateAmc`.
│   │   │     - Relations: `OneToMany materials`, `cascade=ALL`, `orphanRemoval=true`.
│   │   │   - `ServiceOrderMaterial`
│   │   │     - Purpose: SO line items.
│   │   │     - Relations: `ManyToOne serviceOrder`.
│   │   │   - `ContigencyPurchase`
│   │   │     - Purpose: CP header for fast/emergency purchases.
│   │   │     - Important fields: `contigencyId`, `vendorsName`, `paymentTo*`, `totalCpValue`, `purpose`.
│   │   │     - Relations: `OneToMany cpMaterials`, `cascade=ALL`, `orphanRemoval=true`.
│   │   │   - `CpMaterials`
│   │   │     - Purpose: CP line items.
│   │   │     - Relations: `ManyToOne contigencyPurchase`.
│   │   ├── inventory
│   │   │   - Process entities are mostly flat process-header/detail tables linked by process/subprocess IDs rather than rich JPA relations.
│   │   │   - Key headers: `GrnMasterEntity`, `GiMasterEntity`, `GtMasterEntity`, `IgpMasterEntity`, `OgpMasterEntity`, `DemandAndIssueMasterEntity`, `AssetDisposalMasterEntity`.
│   │   │   - Key details: `GrnMaterialDtlEntity`, `GiMaterialDtlEntity`, `GtDtlEntity`, `IgpDetailEntity`, `OgpDetailEntity`, `AssetSerialEntity`, `AssetDisposalDetailEntity`.
│   │   │   - `AssetMasterEntity`/`Asset`: asset master + transactional asset instances used by OHQ, disposal, GT.
│   │   ├── workflow/admin
│   │   │   - `WorkflowMaster`, `WorkflowTransition`, `SubWorkflowTransition`, `StateMaster`, `TransitionMaster`, `TransitionConditionMaster`, `ActionMaster`.
│   │   │   - `ApprovalLimitMaster`: role/category/department/location amount rule + escalation role.
│   │   │   - `DepartmentApproverMapping`: department -> dean/head SEG + approval limit.
│   │   │   - `FieldStationApproverMaster`: field station -> engineer/professor in-charge.
│   │   └── user/vendor/master
│   │       - `UserMaster`: internal login account (`isFirstLogin`, `isActive`, encrypted password).
│   │       - `EmployeeDepartmentMaster`: employee profile used by workflow routing.
│   │       - `VendorMaster`, `VendorMasterUtil`, `VendorLoginDetails`, `GemVendorIdTracker`: approved/pending vendor states + vendor portal login.
│   └── templates/resources/
│       ├── `templates/*.html`: email + print/PDF templates.
│       ├── `DDL*.sql`, `InventoryScript.sql`: schema/setup snapshots.
│       └── `static/images/*`, `fonts/*`: branding/PDF assets.
├── frontend-main/ (`Frontend-test`)
│   ├── entry/
│   │   ├── `src/index.js`
│   │   │   - Purpose: React root + Redux provider + persisted store.
│   │   ├── `src/App.js`
│   │   │   - Purpose: sets axios base URL `http://localhost:8088/astro-service`, preloads masters.
│   │   │   - Dependencies: `Routes`, `fetchMasters`.
│   │   └── `src/pages/route/Routes.jsx`
│   │       - Purpose: role-based route table for Admin / Indent Creator / Store Purchase / Store Person / Purchase personnel / PO Creator / SO Creator / Tender Creator.
│   ├── store/
│   │   ├── `authSlice.jsx`
│   │   │   - Purpose: internal login, role switching, first-login flag.
│   │   │   - Key methods: `login`, `logout`, `changeRole`, `clearFirstLogin`.
│   │   │   - Dependencies: `/login`.
│   │   ├── `masterSlice.js`
│   │   │   - Purpose: preload shared master datasets.
│   │   │   - Key methods: `fetchMasters`.
│   │   │   - Dependencies: `/api/uom-master`, `/api/location-master`, `/api/vendor-master`, `/api/material-master`, `/api/locator-master`, `/api/project-master`, `/api/userMaster`.
│   │   └── `store/index.js`
│   │       - Purpose: Redux Toolkit + redux-persist (`auth`, `masters`).
│   ├── services/api/
│   │   ├── `services/approvalWorkflowService.js`
│   │   │   - Purpose: only centralized API layer present; wraps admin approval-config endpoints.
│   │   │   - Key methods: `ApprovalLimitsService.*`, `DepartmentApproversService.*`, `FieldStationApproversService.*`, `WorkflowConfigService.*`.
│   │   │   - Dependencies: axios -> `/api/admin/*`.
│   │   └── Most feature pages call axios directly instead of using shared service modules.
│   ├── hooks/context/
│   │   ├── `hooks/useLOVValues.js`
│   │   │   - Purpose: fetch dynamic dropdowns from `/api/lov`.
│   │   └── No app-wide context; Redux is primary shared state.
│   ├── components/
│   │   ├── `DKG_*`
│   │   │   - Purpose: reusable layout/form widgets (header, sidenav, custom form/table/input/upload).
│   │   ├── feature widgets
│   │   │   - `PurchaseHistoryModal`, `IndentCancellationModal`, `WorkflowProgressTracker`, `ProjectBudgetDisplay`, `AdvancedEmployeeSearch`, `AssetSearch`, `GrnSearchDropDown`, etc.
│   │   └── queue helpers
│   │       - `QueueModal`, `QueueList`, `QueueHistory`, `SubworkflowTransition`, `TenderEvaluationHistory`.
│   ├── pages/
│   │   ├── auth
│   │   │   - `Login.jsx`: internal employee login -> Redux auth.
│   │   │   - `ChangePassword.jsx`: first-login password reset -> `/api/userMaster/change-password`.
│   │   ├── procurement
│   │   │   - `dashboard/indentCreation/Indent1.jsx`
│   │   │     - Purpose: giant indent create/edit/search/print/cancel page.
│   │   │     - Key functions: search indent, load job/uom/budget/employee context, submit indent.
│   │   │     - Dependencies: shared masters, `PurchaseHistoryModal`, `IndentCancellationModal`, `/api/indents/*`, `/api/employee-department-master/*`, `/api/admin/budget*`.
│   │   │     - VERSIONING (added):
│   │   │       - After successful PUT, captures new `indentId` from response and updates local state (e.g. `IND1111` → `IND1111/2`).
│   │   │       - PUT call uses `axios.put('/api/indents', payload, { params: { indentId } })` — query param, not path variable.
│   │   │       - GET calls for indent data use query params: `axios.get('/api/indents/indentData', { params: { indentId } })`.
│   │   │       - If loaded indent has `isActive = false`, form is set read-only with a "Viewing Old Version" warning banner.
│   │   │       - "Version History" button calls `GET /api/indents/version-history/{indentId}` and renders a modal table of all versions with active/superseded status badges; clicking a version ID loads that version into the form.
│   │   │   - `dashboard/tenderRequest/Tender.jsx`
│   │   │     - Purpose: tender create/update/search/print with LOV-driven fields.
│   │   │     - Dependencies: `/api/tender-requests*`, `/api/indents/IndentDataForTender?id=*`, `/api/location-master`, `/getApprovedTenderIdForPOAndSO`.
│   │   │     - NOTE: `IndentDataForTender` call changed to query param (`?id=`) to support versioned indent IDs.
│   │   │   - `dashboard/tenderRequest/TenderEvaluator.jsx`
│   │   │     - Purpose: compare quotations, reject/change-request/accept, submit tender evaluation.
│   │   │     - Dependencies: `/api/vendor-quotation/*`, `/api/tender-evaluation`.
│   │   │   - `dashboard/purchaseOrder/PO.jsx`
│   │   │     - Purpose: PO create/update/search/print.
│   │   │     - Dependencies: `/api/purchase-orders*`, `/api/tender-requests/{id}`, `/api/vendor-quotation/completed-vendorNames/{id}`.
│   │   │   - `dashboard/serviceOrder/SO.jsx`
│   │   │     - Purpose: SO create/update/print for job-based orders.
│   │   │     - Dependencies: `/api/service-orders*`, tender lookups.
│   │   │   - `dashboard/contingencyPurchase/ContingencyPurchase.jsx`
│   │   │     - Purpose: CP create/search/print.
│   │   │     - Dependencies: `/api/contigency-purchase*`, LOV APIs.
│   │   ├── inventory
│   │   │   - `dashboard/grn/Grn.jsx`: GRN/material GRN create-update with locator/IGP lookup -> `/api/process-controller/*`.
│   │   │   - `dashboard/asset/Asset.jsx`: asset save/update/search/serials/disposal -> `/api/asset/*`.
│   │   │   - `dashboard/igp/Igp.jsx`, `dashboard/ogp/Ogp.jsx`, `dashboard/isn/Isn.jsx`, `dashboard/grv/Grv.jsx`, `dashboard/goodsInspection/*`, `dashboard/goodsProvisionalRecieptNote/GPRN.jsx`.
│   │   │   - Pattern: each page is a form shell around `ProcessController` or asset endpoints.
│   │   ├── queue
│   │   │   - `dashboard/queue/QueueTable.jsx`
│   │   │     - Purpose: tabbed shell over queue subpages.
│   │   │   - `dashboard/queue/QueueRequest.jsx`
│   │   │     - Purpose: mega workflow queue/action page for masters + transition actions + bulk actions.
│   │   │     - Dependencies: workflow queue endpoints, master util action endpoints.
│   │   │     - Note: major hotspot (~6066 lines).
│   │   │   - `GiApprovalPage`, `GrnApproval`, `GatePass`, `GoodsTransferQueue`, `DemandAndIssueQueue`, `PendingGi`, `AssetDisposalQueue`.
│   │   ├── admin
│   │   │   - `AdminDashboard.jsx`: admin landing.
│   │   │   - `ApprovalWorkflow.jsx`: dynamic approver/branch management UI for `/api/admin/approvers`; hotspot (~1878 lines).
│   │   │   - `ApprovalLimitsConfig.jsx`, `DepartmentApproverMapping.jsx`, `FieldStationApproverConfig.jsx`, `FullWorkflowConfig.jsx`, `BudgetManagement.jsx`, `ProjectManagement.jsx`, `EmployeeRegistration.jsx`, `UserCreation.jsx`, `ListOfValues.jsx`.
│   │   ├── masters/reports/accounting
│   │   │   - `masters/*`: employee/material/vendor/work/job master UIs.
│   │   │   - `reports/*`: procurement + inventory reporting UIs.
│   │   │   - `accounting/*`: vendor ledger / trial balance / payment register / tally integration views.
│   │   └── dashboard/newDashboard/MainDashboard.jsx
│   │       - Purpose: non-admin landing/dashboard.
│   └── utils/
│       ├── `CommonFunctions.js`: generic form rendering + axios helper.
│       ├── `*Format.jsx`: print/PDF layout helpers for tender/PO/GPRN/indent.
│       └── `Constants.jsx`: UI constants.
├── frontend-vendor/ (`Astro-vendor-portal-main`)
│   ├── entry/
│   │   ├── `src/App.js`: same axios base URL + master preload.
│   │   └── `src/pages/route/Routes.jsx`: public login/change-password + vendor tender route + PO details.
│   ├── store/
│   │   ├── `authSlice.jsx`
│   │   │   - Purpose: vendor login state (`vendorId`, first login/temp password flags).
│   │   └── `store/index.js`
│   │       - Purpose: persisted auth/masters store.
│   ├── pages/
│   │   ├── `auth/Login.jsx`
│   │   │   - Purpose: vendor status lookup via `/api/vendor-quotation/VendorStatus/{vendorId}`.
│   │   ├── `auth/Changepassword.jsx`
│   │   │   - Purpose: vendor password change via `/api/vendor-quotation/change-password`.
│   │   ├── `dashboard/approvedVendor/Form2.jsx`
│   │   │   - Purpose: vendor tender workspace; load tender details, all-vendor status, evaluator widget.
│   │   │   - Dependencies: `Tender_Evaluator`, PO details, quotation history.
│   │   ├── `dashboard/vendorRegistration/Form1.jsx`
│   │   │   - Purpose: vendor self-registration + email/PAN validation.
│   │   │   - Dependencies: `/api/vendor-master-util/*`.
│   │   └── `dashboard/poDetails/Form3.jsx`
│   │       - Purpose: vendor PO detail view.
│   └── components/
│       ├── `Tender_Evaluator.jsx`
│       │   - Purpose: vendor quotation upload and clarification resubmission.
│       │   - Dependencies: `/file/upload?fileType=Tender`, `/api/vendor-quotation`, `/api/tender-requests/{id}`.
│       └── `Purchaseorder_details.jsx`, `QuotationHistoryModal.jsx`, `QueueModal.jsx`.
└── ignored/non-runtime/
    ├── `node_modules`, `target`, dumps, logs, zip files.
    └── Not part of runtime intelligence tree.

## 2. Backend Flow Map

- Indent:
  - API: `POST /api/indents` -> `IndentCreationController.createIndent`.
  - Service: `IndentCreationServiceImpl.createIndent` validates indent type, vendor count by procurement mode, department computer price cap, stores files, computes total, saves `IndentCreation + MaterialDetails/JobDetails`, saves vendor-name mappings.
  - Versioning fields set on create: `version=1`, `isActive=true`, `parentIndentId=null`.
  - Workflow: controller immediately calls `WorkflowService.initiateWorkflow(requestId=indentId, workflowName="Indent Workflow", userId=createdBy)`.
  - Repos: `IndentCreationRepository`, `MaterialDetailsRepository`, `JobDetailsRepository`, `VendorNamesForJobWorkMaterialRepository`, workflow/user/employee repos.
  - Entities: `IndentCreation` -> `MaterialDetails[]` / `JobDetails[]`.
  - Relationships: `IndentCreation@OneToMany(materialDetails/jobDetails, cascade=ALL, orphanRemoval=true)`.
  - Transactions: `createIndent`, cancellation request/approval methods.

- Indent versioning (update flow):
  - API: `PUT /api/indents?indentId=IND1111` -> `IndentCreationController.updateIndent`.
  - Guard checks: locked for tender, not editable, createdBy mismatch → 400.
  - Old version: `isActive` set to `false`; all pending `WorkflowTransition` rows for old ID get `status=SUPERSEDED`, `nextAction=null`.
  - New version: new row inserted with `indentId = baseId + "/" + newVersion` (e.g. `IND1111/2`), same `indentNumber`, `createdBy` preserved from old row, fresh material/job details saved.
  - Workflow: controller calls `initiateWorkflow(newIndentId, "Indent Workflow", createdBy)` after service returns.
  - Version history: `GET /api/indents/version-history/{indentId}` returns all rows in the family ordered by version desc.
  - Search: all search queries filter `isActive = true` so superseded versions are invisible to search.

- Tender:
  - API: `POST /api/tender-requests` -> `TenderRequestController.createTenderRequest`.
  - Service: `TenderRequestServiceImpl.createTenderRequest` ensures selected indents exist, are workflow-complete, not cancelled, not locked; creates tender, attaches `IndentId[]`, computes total tender value from indent totals, saves files.
  - Workflow: service initiates tender workflow; controller also calls workflow initiation again using enum key/value variants.
  - Side effect: each indent is marked `isLockedForTender=true`, `currentStatus=TENDER_CREATED`.
  - Repos: `TenderRequestRepository`, `IndentIdRepository`, `IndentCreationRepository`, `WorkflowTransitionRepository`.
  - Entities: `TenderRequest` -> `IndentId[]`.
  - Relationships: `TenderRequest@OneToMany(indentIds, cascade=ALL, orphanRemoval=true)`.
  - Risk: duplicate workflow initiation path in controller + service.

- Vendor quotation / tender evaluation:
  - API: `/api/vendor-quotation/*`, `/api/tender-evaluation`.
  - Service: `VendorQuotationAgainstTenderServiceImpl` versions each quotation row; old latest row is marked non-latest, new row captures state transition.
  - State path: `SUBMITTED` -> indentor actions -> `PENDING_SPO` -> SPO `Completed` / `REJECTED` / `CHANGE_REQUESTED`.
  - Repos: quotation repo, vendor repos, tender repo, workflow transition repo.
  - Entities: `VendorQuotationAgainstTender`, `TenderEvaluation`.
  - Transactions: vendor password change; quotation version writes are multi-step but mostly not wrapped.

- Purchase Order:
  - API: `POST /api/purchase-orders` -> `PurchaseOrderController.createPurchaseOrder`.
  - Service: `PurchaseOrderImpl.createPurchaseOrder` generates `PO<tenderNo>`, maps line attributes, calculates total INR value, saves PO aggregate, then locks related tender (`TenderRequest.isLocked=true`, `lockedForPO=poId`).
  - Workflow: controller initiates `"PO Workflow"`.
  - Repos: PO header/attribute/history repos, tender repo, indent/tender support repos.
  - Entities: `PurchaseOrder` -> `PurchaseOrderAttributes[]`.
  - Relationships: `PurchaseOrder@OneToMany(purchaseOrderAttributes, cascade=ALL, orphanRemoval=true)`.
  - Versioning: update writes `PurchaseOrderHistory` snapshot JSON, increments `poVersion`.

- Service Order:
  - API: `POST /api/service-orders`.
  - Service: `ServiceOrderServiceImpl.createServiceOrder` generates `SO<tenderNo>`, maps job/material rows, saves aggregate; reads tender+indent data to calculate totals for responses/reports.
  - Workflow: controller initiates `"SO Workflow"`.
  - Entities: `ServiceOrder` -> `ServiceOrderMaterial[]`.
  - Relationships: `ServiceOrder@OneToMany(materials, cascade=ALL, orphanRemoval=true)`.

- Contingency Purchase:
  - API: `POST /api/contigency-purchase`.
  - Service: `ContigencyPurchaseServiceImpl.createContigencyPurchase` saves header + `CpMaterials[]`, computes `totalCpValue`, stores invoice/supporting files.
  - Workflow: controller starts contingency workflow.
  - Entities: `ContigencyPurchase` -> `CpMaterials[]`.
  - Relationships: `OneToMany cpMaterials`, `cascade=ALL`, `orphanRemoval=true`.

- Inventory orchestration:
  - Entry: `ProcessController` under `/api/process-controller`.
  - Service: `ProcessServiceImpl` routes by process stage to dedicated services (`GprnService`, `GiService`, `GrnService`, `IgpService`, `OgpService`, `GtService`, `DiService`, `IsnService`).
  - Pattern: page submits DTO -> process service -> process-specific repo tables keyed by `processId/subProcessId`.
  - Entity model: mostly flat headers/details; relationships are implicit via process IDs, not JPA associations.

- GRN:
  - API: `POST /api/process-controller/saveMaterialGrn` or `/saveGrn`.
  - Service: `GrnServiceImpl.saveMaterialGrn/saveGrn`; later `approveGrn`, `rejectGrn`, `changeReqGrn`.
  - Repos: `GrnMasterRepository`, `GrnMaterialDtlRepository`, `GrnWorkflowStatusRepository`, `GprnMasterRepository` (shared access-check queries), PO/GI support repos.
  - Entities: `GrnMasterEntity` (added `consigneeName` field), `GrnMaterialDtlEntity`, `GrnWorkflowStatus`.
  - Downstream: payment voucher data sourced from approved GRN/SO.
  - Consignee auto-population (added): on `saveGrn`, `resolveConsigneeName` traces `GPRN.poId` → `PurchaseOrder.tenderId` → indent count.
    - Single-indent tender → consignee = `IndentCreation.indentorName` of that indent.
    - Multi-indent tender → consignee = store person name passed from frontend via `GrnDto.consigneeName`.
  - `GrnDto` carries `String consigneeName` for the fallback store-person name.

- GI:
  - API: `POST /api/process-controller/saveGi`, queue approvals via `/approveGi`, `/rejectGi`, `/changeReqGi`.
  - `GET /api/process-controller/getPendingGprnsForGI?userId={userId}&role={role}` — role-filtered GPRN dropdown.
  - Service: `GiServiceImpl`.
  - Repos: `GiMasterRepository`, `GiMaterialDtlRepository`, `GiWorkflowStatusRepository`, `GprnMasterRepository` (3 new access-check queries).
  - Entities: `GiMasterEntity`, `GiMaterialDtlEntity`, `GiWorkflowStatus`.
  - Access control (added): GPRN visibility and save/update are gated by indent count on the tender.
    - Store person → master access, sees all GPRNs, can transact on any.
    - Indentor → sees/transacts only GPRNs where tender has exactly 1 indent AND `IndentCreation.createdBy == userId`.
    - Chain: `GprnMasterEntity.poId` → `PurchaseOrder.tenderId` → `IndentId` (join table) → `IndentCreation.createdBy`.
    - Enforced on: dropdown filter (`getPendingGprnsForGI`), `saveGi`, `updateGi`.
  - `SaveGiDto` carries `Integer createdBy` + `String role` for backend enforcement.

- GT:
  - API: `/createGt`, `/approveGt`, `/receiverApproveGt`, `/rejectGt`.
  - Service: `GtServiceImpl`.
  - Repos: `GtMasterRepository`, `GtDtlRepository`.
  - Entities: `GtMasterEntity`, `GtDtlEntity`.
  - Flow: sender creates -> approver approves -> receiver approves/rejects.

- Asset:
  - APIs: `/api/asset/*`, plus disposal endpoints under `/api/process-controller`.
  - Service: `AssetMasterServiceImpl`.
  - Repos: `AssetMasterRepository`, `AssetRepository`, serial/disposal/auction repos, OHQ repos.
  - Entities: `AssetMasterEntity`, `Asset`, `AssetSerialEntity`, `AssetDisposal*`, `AssetDisposalAuction*`.
  - Flow: asset create/update -> OHQ placement -> disposal request -> approval -> auction/Ogp.

- Admin approval configuration:
  - APIs: `/api/admin/approvers`, `/api/admin/approval-limits`, `/api/admin/department-approvers`, `/api/admin/field-station-approvers`.
  - Services: `ApprovalLimitServiceImpl`, `DepartmentApproverServiceImpl`, `FieldStationApproverServiceImpl`, plus branch/approver logic in workflow/admin layer.
  - Usage: feeds `WorkflowServiceImpl` branch selection and escalation decisions.

## 3. Frontend Flow Map

- Internal login:
  - `pages/auth/Login.jsx` -> Redux `authSlice.login` -> `POST /login` -> backend `UserController`/`UserServiceImpl.login`.
  - State: Redux persisted `auth`.
  - First-login redirect: `ChangePassword.jsx` -> `POST /api/userMaster/change-password`.

- Shared bootstrap:
  - `App.js` -> `fetchMasters()` -> preload UOM/location/vendor/material/locator/project/user masters.
  - State: Redux `masters`.

- Indent flow:
  - `Indent1.jsx` -> uses `DKG_*` form components, `PurchaseHistoryModal`, `IndentCancellationModal`, LOV hook.
  - APIs: search/get/create/update/cancel under `/api/indents/*`; employee/budget lookups.
  - State: heavy local `useState`; shared master data from Redux.
  - Validation: UI + backend vendor-count / price-limit checks.
  - Versioning (added): on update, captures new `indentId` from response; shows old-version banner if `isActive=false`; version history modal via `GET /api/indents/version-history/{indentId}`.
  - API param pattern (added): all indent fetch/update calls use `@RequestParam` (`?indentId=`) not path variables to avoid slash-in-ID routing issues.

- Tender flow:
  - `Tender.jsx` -> `CustomForm` + print modal -> `/api/tender-requests*` + `/api/indents/IndentDataForTender?id=*`.
  - `TenderEvaluator.jsx` -> table/checkbox driven evaluation -> `/api/vendor-quotation/*` + `/api/tender-evaluation`.
  - Component link: page imports `TenderEvaluationHistory` modal from queue module.

- PO/SO flow:
  - `PO.jsx` -> loads tenders/vendors/completed vendor names -> create/update/search/print via `/api/purchase-orders*`.
  - `SO.jsx` -> similar pattern for `/api/service-orders*`.
  - Both use `useReactToPrint`, `CustomModal`, direct axios.

- Inventory flow:
  - `Grn.jsx`, `Igp.jsx`, `Ogp.jsx`, `Isn.jsx`, `Asset.jsx`, `AssetDisposal.jsx`, queue approval pages.
  - Pattern: page form -> `/api/process-controller/*` or `/api/asset/*`.
  - Queue pages (`GrnApproval`, `GiApprovalPage`, `GoodsTransferQueue`, `GatePass`) are approval UIs over process-controller endpoints.

- Admin approval-config flow:
  - `ApprovalLimitsConfig.jsx` -> `ApprovalLimitsService`.
  - `DepartmentApproverMapping.jsx` -> `DepartmentApproversService`.
  - `FieldStationApproverConfig.jsx` -> `FieldStationApproversService`.
  - `FullWorkflowConfig.jsx` -> `WorkflowConfigService.getFullConfig()` -> `/api/admin/approvers/full-config`.
  - `ApprovalWorkflow.jsx` -> direct axios to `/api/admin/approvers*` for branch/approver management.

- Vendor portal flow:
  - `auth/Login.jsx` -> checks `/api/vendor-quotation/VendorStatus/{vendorId}`; state stored in vendor `authSlice`.
  - `approvedVendor/Form2.jsx` -> tender selection -> `Tender_Evaluator.jsx` for vendor quotation upload/resubmit.
  - `Tender_Evaluator.jsx` -> `/file/upload?fileType=Tender` then `/api/vendor-quotation`.
  - `auth/Changepassword.jsx` -> `/api/vendor-quotation/change-password`.

## 4. API Contract Map

- `POST /login`
  - Request: `UserDto`.
  - Response: `UserRoleDto` with `roles[]`, `isFirstLogin`.
  - Called from: `Frontend-test/src/store/slice/authSlice.jsx`.

- `GET /api/userMaster`, `POST /api/userMaster`, `PUT /api/userMaster/{id}`, `PUT /api/userMaster/{id}/toggle-status`, `POST /api/userMaster/change-password`
  - Request: `userRequestDto` / `ChangePasswordRequest`.
  - Response: wrapped `UserDto` / success message.
  - Called from: `Frontend-test/src/pages/dashboard/admin/UserCreation.jsx`, `pages/auth/ChangePassword.jsx`.

- `GET/POST/PUT /api/employee-department-master*`
  - Request: `EmployeeDepartmentMasterRequestDto`.
  - Response: employee DTO/search DTO/list.
  - Called from: `Frontend-test/src/pages/dashboard/admin/EmployeeRegistration.jsx`, `pages/masters/EmployeeMaster.jsx`, `pages/dashboard/admin/UserCreation.jsx`, `pages/dashboard/indentCreation/Indent1.jsx`.

- `GET/POST/PUT /api/indents*`
  - Request: `IndentCreationRequestDTO`, cancellation DTOs, assign DTO.
  - Response: `IndentCreationResponseDTO` (includes `version`, `isActive`, `parentIndentId`), search/history/cancellation DTOs.
  - Versioning endpoints (added):
    - `POST /api/indents` — create (v1).
    - `PUT /api/indents?indentId={id}` — update; creates new version row, supersedes old.
    - `GET /api/indents/byId?indentId={id}` — fetch single indent by exact ID.
    - `GET /api/indents/indentData?indentId={id}` — fetch full indent data for form load.
    - `GET /api/indents/IndentDataForTender?id={id}` — fetch indent data for tender creation.
    - `GET /api/indents/version-history/{indentId}` — fetch all versions of an indent family.
    - `GET /api/indents/search` — returns only `isActive=true` versions.
  - Called from: `Frontend-test/src/pages/dashboard/indentCreation/Indent1.jsx`, `components/PurchaseHistoryModal.jsx`, `components/IndentCancellationModal.jsx`, `pages/dashboard/tenderRequest/Tender.jsx`.

- `GET/POST/PUT /api/tender-requests*`
  - Request: `TenderRequestDto`, `tenderUpdateDto`, cancel DTO.
  - Response: `TenderResponseDto`, `TenderWithIndentResponseDTO`, approved tender ID DTOs.
  - Called from: `Frontend-test/src/pages/dashboard/tenderRequest/Tender.jsx`, `pages/dashboard/tenderRequest/TenderEvaluator.jsx`, `pages/dashboard/purchaseOrder/PO.jsx`, vendor portal `components/Tender_Evaluator.jsx`.

- `GET/POST/PUT /api/vendor-quotation*`
  - Request: `VendorQuotationAgainstTenderDto`, `VendorQuotationUpdateRequestDto`, `VendorQuotationChangeRequestDto`, `spoDto`, vendor password DTO.
  - Response: quotation DTO lists, vendor status/history DTOs, change-password response.
  - Called from: `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluator.jsx`, `pages/dashboard/purchaseOrder/PO.jsx`, `pages/dashboard/tenderRequest/Quotations.jsx`; vendor portal `pages/auth/Login.jsx`, `auth/Changepassword.jsx`, `components/Tender_Evaluator.jsx`, `pages/dashboard/approvedVendor/Form2.jsx`.

- `POST/PUT/GET /api/tender-evaluation`
  - Request: `TenderEvaluationRequestDto`.
  - Response: evaluation DTO.
  - Called from: `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluator.jsx`.

- `GET/POST/PUT /api/purchase-orders*`
  - Request: `PurchaseOrderRequestDTO`.
  - Response: `PurchaseOrderResponseDTO`, `poWithTenderAndIndentResponseDTO`, search/report DTOs.
  - Called from: `Frontend-test/src/pages/dashboard/purchaseOrder/PO.jsx`.

- `GET/POST/PUT /api/service-orders*`
  - Request: `ServiceOrderRequestDTO`.
  - Response: `ServiceOrderResponseDTO`, `soWithTenderAndIndentResponseDTO`.
  - Called from: `Frontend-test/src/pages/dashboard/serviceOrder/SO.jsx`.

- `GET/POST/PUT /api/contigency-purchase*`
  - Request: `ContigencyPurchaseRequestDto`.
  - Response: `ContigencyPurchaseResponseDto`, report/search DTOs.
  - Called from: `Frontend-test/src/pages/dashboard/contingencyPurchase/ContingencyPurchase.jsx`.

- `GET /api/lov/*`
  - Request: path/query params.
  - Response: LOV dropdown arrays/maps.
  - Called from: `Frontend-test/src/hooks/useLOVValues.js`, `pages/dashboard/admin/EmployeeRegistration.jsx`.

- `GET/POST/PUT /api/admin/approval-limits*`
  - Request: `ApprovalLimitDTO`.
  - Response: approval limit DTO/list/escalation check.
  - Called from: `Frontend-test/src/services/approvalWorkflowService.js` -> `pages/dashboard/admin/ApprovalLimitsConfig.jsx`.

- `GET/POST/PUT /api/admin/department-approvers*`
  - Request: `DepartmentApproverMappingDTO`.
  - Response: mapping DTO/list.
  - Called from: `Frontend-test/src/services/approvalWorkflowService.js` -> `pages/dashboard/admin/DepartmentApproverMapping.jsx`.

- `GET/POST/PUT /api/admin/field-station-approvers*`
  - Request: `FieldStationApproverDTO`.
  - Response: field-station approver DTO/list.
  - Called from: `Frontend-test/src/services/approvalWorkflowService.js` -> `pages/dashboard/admin/FieldStationApproverConfig.jsx`.

- `GET /api/admin/approvers/full-config`
  - Request: none.
  - Response: aggregated workflow config (`approvalLimits`, `departmentApproverMappings`, `fieldStationApprovers`, etc.).
  - Called from: `Frontend-test/src/pages/dashboard/admin/FullWorkflowConfig.jsx`.

- `/api/process-controller/*`
  - Request: many process DTOs (`SaveGiDto`, `GrnDto`, `IgpDto`, `OgpDto`, `GtMasterDto`, `paymentVoucherRequestDto`, etc.).
  - Response: process numbers, detail DTOs, pending queues, approval results.
  - Called from: `Frontend-test/src/pages/dashboard/grn/Grn.jsx`, `igp/Igp.jsx`, `assetDisposal/AssetDisposal.jsx`, queue pages (`GrnApproval`, `GiApprovalPage`, `GatePass`, `GoodsTransferQueue`, `DemandAndIssueQueue`, `PendingGi`).

- `/api/asset/*`
  - Request: `AssetMasterDto`, `AssetDisposalDto`, `DisposeAssetRequest`, serial DTOs.
  - Response: asset detail/search/disposal DTOs.
  - Called from: `Frontend-test/src/pages/dashboard/asset/Asset.jsx`, `assetDisposal/AssetDisposal.jsx`.

## 5. Architecture Summary

- Backend pattern: Spring Boot layered architecture with controller -> service -> repository -> entity, plus a central dynamic workflow engine and many DTOs.
- Frontend structure:
  - `Frontend-test`: CRA + Redux + Ant Design, role-based routing, page-heavy forms, direct axios usage.
  - `Astro-vendor-portal-main`: separate CRA vendor portal reusing backend.
- Entry points:
  - Backend: `Backend-prod/src/main/java/com/astro/BackendServiceApplication.java`.
  - Main frontend: `Frontend-test/src/index.js`, `Frontend-test/src/App.js`.
  - Vendor frontend: `Astro-vendor-portal-main/src/index.js`, `Astro-vendor-portal-main/src/App.js`.
- Key modules/features:
  - Internal auth/user/employee admin.
  - Master data management.
  - Procurement lifecycle: Indent -> Tender -> Vendor Quotation/Evaluation -> PO/SO/CP.
  - Inventory lifecycle: GPRN/GI/GRN/GRV/ISN/IGP/OGP/GT/DI/Asset.
  - Dynamic workflow and approval-rule administration.
  - Reports/accounting/Tally integration.
  - Indent versioning: copy-new row pattern with `IND1111/N` ID scheme, `isActive` flag, workflow supersession.

## 6. Risks / Observations

- `WorkflowServiceImpl`, `QueueRequest.jsx`, `ApprovalWorkflow.jsx`, `IndentCreationServiceImpl`, `GiServiceImpl`, `PurchaseOrderImpl`, `ProcessController` are oversized hotspots; high regression risk and expensive to reason about.
- Tender workflow initiation appears duplicated: service and controller both call `initiateWorkflow`; likely duplicate transition risk.
- `WebSecurityConfig` only enables CORS; there is no real backend authorization layer. Most protection is frontend role gating.
- Sensitive secrets are committed in `application.properties` (DB root password, mail password).
- Many frontend pages call axios directly; API contract changes require broad manual updates.
- Inventory entities rely on process IDs/subprocess IDs more than JPA relations, which reduces ORM cascade issues but increases manual consistency burden.
- Aggregate entities with `cascade=ALL` + `orphanRemoval=true`:
  - `IndentCreation.materialDetails/jobDetails`
  - `TenderRequest.indentIds`
  - `PurchaseOrder.purchaseOrderAttributes`
  - `ServiceOrder.materials`
  - `ContigencyPurchase.cpMaterials`
  - Risk: replace/clear collections carefully during updates to avoid accidental deletes.
- Likely N+1 / repeated-read zones:
  - `TenderRequestServiceImpl` repeatedly calls `indentCreationService.getIndentById` per indent.
  - `GiServiceImpl.getPendingGprnsForGI` and `GrnServiceImpl.resolveConsigneeName` each make up to 3 repo calls per GPRN (`findTenderIdByPoId`, `countIndentsByTenderId`, `findSingle*`). Acceptable for current list sizes; replace with a single joined query if pending lists grow large.
  - `PurchaseOrderImpl` and `ServiceOrderServiceImpl` compute totals by loading each indent/tender separately.
  - Vendor status screens loop over vendor IDs and query repos repeatedly.
- Circular/tight coupling signals:
  - `TenderRequestServiceImpl` <-> `WorkflowService` (`@Lazy` used).
  - Services frequently call other services instead of repositories only.
  - Frontend queue/tender pages import queue/history modals across feature boundaries.
- Vendor portal passwords appear plaintext in `VendorLoginDetails` flow, unlike internal users which use `PasswordEncoder`.
- Hard-coded/local URLs still exist in frontend code (`localhost`/legacy server refs), increasing environment drift risk.
- Reporting and print generation mix business logic with formatting/file concerns inside services, making unit testing harder.
- Indent versioning — known constraints (added):
  - Versioned IDs (e.g. `IND1111/2`) contain `/` which breaks Spring `@PathVariable` parsing. All indent endpoints that accept an ID must use `@RequestParam`. Any new endpoint added for indent must follow this pattern.
  - `getAllIndents()` returns all rows including inactive versions; callers that need only current versions must filter `isActive=true` or use search endpoints.
  - Tender locks the active indent ID at time of tender creation. If the indent is later versioned (new ID), the tender still references the old (now inactive) ID — this is intentional; a locked indent cannot be versioned anyway.

## Incremental Update Rule

- Update only changed nodes under the existing headings.
- Prefer appending/modifying file bullets instead of rebuilding sections.
- For future debugging, start from this file before reopening source files.

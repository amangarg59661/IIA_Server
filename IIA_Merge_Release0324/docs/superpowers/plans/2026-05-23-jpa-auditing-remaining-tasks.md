# JPA Auditing Remaining Tasks Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete JPA Auditing migration by renaming modifiedBy fields, adding frontend X-User-Id interceptor, removing redundant manual set calls, and verifying with a full build.

**Architecture:** 4 entities rename `modifiedBy` -> `updatedBy` (Java only, DB column stays via @Column mapping). Frontend apps add axios interceptor sending userId header. Redundant manual setCreatedBy/setUpdatedBy calls removed from services where JPA Auditing handles them. PurchaseOrderHistory EXCLUDED from rename (already has both fields).

**Tech Stack:** Spring Boot 2.7.3, Java 17, javax.persistence, Lombok @Data, React + Redux + axios

**Execution order:** Task 1 (rename) -> Task 2 (frontend interceptor) -> Task 3 (remove redundant sets) -> Task 4 (build + test). Order matters: interceptor must exist before removing manual sets.

---

## Task 1: Rename modifiedBy -> updatedBy in 3 Entities

**Why 3, not 4:** PurchaseOrderHistory already has BOTH `modifiedBy` (business: who changed PO) AND `updatedBy` (JPA audit). Skip it.

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/entity/WorkflowTransition.java:46-48`
- Modify: `Backend-prod/src/main/java/com/astro/entity/SubWorkflowTransition.java:40-41`
- Modify: `Backend-prod/src/main/java/com/astro/entity/VendorQuotationAgainstTender.java:69-70`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/WorkflowTransitionDto.java:18`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/CompletedIndentsQueueResponse.java:17,37,49`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/SubWorkflowTransitionDto.java:14`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/SubWorkflowQueueDto.java:25`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/QueueResponse.java:29`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/TenderEvaluationHistory.java:32`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/ProcurementDtos/IndentWorkflowStatusDto.java:14`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/ProcurementDtos/purchaseOrder/PoFormateApprovalHistory.java:16`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/VendorQuotationAgainstTenderDto.java` (modifiedBy field)
- Modify: `Backend-prod/src/main/java/com/astro/repository/WorkflowTransitionRepository.java:33`
- Modify: `Backend-prod/src/main/java/com/astro/repository/SubWorkflowTransitionRepository.java:13`
- Modify: `Backend-prod/src/main/java/com/astro/service/WorkflowService.java:37,39,60`
- Modify: `Backend-prod/src/main/java/com/astro/controller/WorkflowController.java:103-114`
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/WorkflowServiceImpl.java` (many lines)
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderRequestServiceImpl.java:1135-1140`
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/PurchaseOrderImpl.java:350,919,954`
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/IndentCreationServiceImpl.java:2400`
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/VendorQuotationAgainstTenderServiceImpl.java` (multiple lines)
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java:1262,1319`
- Modify: `Backend-prod/src/main/java/com/astro/util/UtilProcurementService.java:144`
- Modify: `Backend-prod/src/main/java/com/astro/util/EmailService.java:522`
- Modify: `Backend-prod/src/main/java/com/astro/config/AuditTrailListener.java:33`

**IMPORTANT:** Do NOT add `@LastModifiedBy` to the renamed `updatedBy` field in WorkflowTransition, SubWorkflowTransition, or VendorQuotationAgainstTender. These fields are SET MANUALLY with `actionByUserId` (the workflow actor), which is intentionally different from the logged-in user. Adding `@LastModifiedBy` would auto-overwrite with the wrong user.

- [ ] **Step 1: Rename field in WorkflowTransition entity**

In `Backend-prod/src/main/java/com/astro/entity/WorkflowTransition.java`, change:
```java
     @Column(name = "MODIFIEDBY")
    //@Column(name = "modifiedBy")
    private String modifiedBy;
```
to:
```java
     @Column(name = "MODIFIEDBY")
    private String updatedBy;
```

- [ ] **Step 2: Rename field in SubWorkflowTransition entity**

In `Backend-prod/src/main/java/com/astro/entity/SubWorkflowTransition.java`, change:
```java
    @Column(name = "MODIFIEDBY")
    private String modifiedBy;
```
to:
```java
    @Column(name = "MODIFIEDBY")
    private String updatedBy;
```

- [ ] **Step 3: Rename field in VendorQuotationAgainstTender entity**

In `Backend-prod/src/main/java/com/astro/entity/VendorQuotationAgainstTender.java`, change:
```java
   @Column(name = "modified_by")
   private String modifiedBy; // who performed this action/version
```
to:
```java
   @Column(name = "modified_by")
   private String updatedBy; // who performed this action/version
```

- [ ] **Step 4: Rename in all DTOs**

For each DTO, rename the field `modifiedBy` -> `updatedBy`. Since these use Lombok @Data, getters/setters auto-update. Files:
- `WorkflowTransitionDto.java`: `private String modifiedBy;` -> `private String updatedBy;`
- `CompletedIndentsQueueResponse.java`: field, constructor param, and assignment
- `SubWorkflowTransitionDto.java`
- `SubWorkflowQueueDto.java`
- `QueueResponse.java`
- `TenderEvaluationHistory.java`
- `IndentWorkflowStatusDto.java`
- `PoFormateApprovalHistory.java`
- `VendorQuotationAgainstTenderDto.java`

- [ ] **Step 5: Update repository methods**

`WorkflowTransitionRepository.java` line 33:
```java
// Change:
List<WorkflowTransition> findByModifiedBy(String modifiedBy);
// To:
List<WorkflowTransition> findByUpdatedBy(String updatedBy);
```

`WorkflowTransitionRepository.java` line 190 (JPQL query):
```java
// Change:
wt.transitionId, wt.requestId, wt.createdBy, wt.modifiedBy,
// To:
wt.transitionId, wt.requestId, wt.createdBy, wt.updatedBy,
```

`SubWorkflowTransitionRepository.java` line 13 - NOTE: method is `findByActionOn(String modifiedBy)`. This queries by `actionOn` field, NOT `modifiedBy`. The parameter name `modifiedBy` is misleading but the method queries a different column. Only rename the parameter:
```java
List<SubWorkflowTransition> findByActionOn(String updatedBy);
```

- [ ] **Step 6: Update all setModifiedBy/getModifiedBy calls**

Use bulk find-and-replace across all service files. Since entities use Lombok @Data, the getter/setter names change automatically when field renames. All callers must update:
- `setModifiedBy(` -> `setUpdatedBy(`
- `getModifiedBy()` -> `getUpdatedBy()`
- `wt.getModifiedBy()` -> `wt.getUpdatedBy()`
- Variable names `modifiedBy` -> `updatedBy` in method signatures and locals

Key files with many occurrences:
- `WorkflowServiceImpl.java` (~30 occurrences)
- `VendorQuotationAgainstTenderServiceImpl.java` (~8 occurrences)
- `TenderRequestServiceImpl.java` (~5 occurrences)
- `PurchaseOrderImpl.java` (~4 occurrences)
- `IndentCreationServiceImpl.java` (~1 occurrence)
- `TenderEvaluationApprovalServiceImpl.java` (~2 occurrences)
- `UtilProcurementService.java` (1 occurrence)
- `EmailService.java` (1 occurrence - template variable name)

**EXCEPTION - PurchaseOrderImpl.java line 350 and 919:**
```java
history.setModifiedBy(dto.getUpdatedBy());   // line 350 - PurchaseOrderHistory entity
history.setModifiedBy(dto.getModifiedBy());  // line 919
```
PurchaseOrderHistory keeps `modifiedBy` as a separate field. These calls stay as `setModifiedBy()`. Do NOT rename. But `dto.getModifiedBy()` on line 919 should become `dto.getUpdatedBy()` if the DTO was renamed.

- [ ] **Step 7: Update WorkflowService interface method signatures**

In `Backend-prod/src/main/java/com/astro/service/WorkflowService.java`:
```java
// Change parameter names (not method names - those are API contracts):
public List<WorkflowTransitionDto> approvedWorkflowTransition(String updatedBy);
public List<SubWorkflowTransitionDto> getSubWorkflowTransition(String updatedBy);
public List<SubWorkflowQueueDto> getSubWorkflowQueue(String updatedBy);
```

- [ ] **Step 8: Update WorkflowController parameter names**

In `Backend-prod/src/main/java/com/astro/controller/WorkflowController.java`:
```java
// Change @RequestParam names:
public ResponseEntity<Object> approvedWorkflowTransition(@RequestParam String updatedBy) {
    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(workflowService.approvedWorkflowTransition(updatedBy)), HttpStatus.OK);
}
// Same for getSubWorkflowTransition and getSubWorkflowTransitionQueue
```

**WARNING:** This changes the query parameter name from `modifiedBy` to `updatedBy`. Frontend calls using `?modifiedBy=` must update to `?updatedBy=`. Check frontend for these API calls.

- [ ] **Step 9: Update AuditTrailListener**

In `Backend-prod/src/main/java/com/astro/config/AuditTrailListener.java` line 33:
```java
// Change:
"createdBy", "updatedBy", "modifiedBy",
// To (remove modifiedBy since it's now updatedBy):
"createdBy", "updatedBy",
```

- [ ] **Step 10: Compile and verify**

Run: `mvn compile -q`
Expected: BUILD SUCCESS with 0 errors

If compilation fails, fix any missed references by grepping for remaining `modifiedBy` usage in the 3 renamed entities' callers.

---

## Task 2: Frontend Axios Interceptor for X-User-Id Header

**Files:**
- Modify: `Frontend-test/src/App.js`
- Modify: `Astro-vendor-portal-main/src/App.js`

- [ ] **Step 1: Add interceptor to Frontend-test App.js**

In `Frontend-test/src/App.js`, add after `axios.defaults.baseURL = baseURL;`:

```javascript
import store from './store';

// Send X-User-Id header with every request for JPA Auditing
axios.interceptors.request.use((config) => {
  const userId = store.getState().auth?.userId;
  if (userId) {
    config.headers['X-User-Id'] = userId;
  }
  return config;
});
```

Full file becomes:
```javascript
import React, { useEffect } from 'react';
import Routes from './pages/route/Routes';
import axios from 'axios';
import { useDispatch } from 'react-redux';
import { fetchMasters } from './store/slice/masterSlice';
import store from './store';

// axios.defaults.baseURL="http://103.181.158.220:8081/astro-service";
//axios.defaults.baseURL="http://localhost:8081/astro-service";
export const baseURL = "http://localhost:8081/astro-service";
axios.defaults.baseURL = baseURL;

// Send X-User-Id header with every request for JPA Auditing
axios.interceptors.request.use((config) => {
  const userId = store.getState().auth?.userId;
  if (userId) {
    config.headers['X-User-Id'] = userId;
  }
  return config;
});

//export const baseURL = "/astro-service";
//axios.defaults.baseURL = baseURL;
function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(fetchMasters());
  }, [dispatch])

  return (
    <Routes />
  );
}

export default App;
```

- [ ] **Step 2: Add interceptor to Astro-vendor-portal App.js**

In `Astro-vendor-portal-main/src/App.js`, add after `axios.defaults.baseURL = baseURL;`:

```javascript
import store from './store';

// Send X-User-Id header with every request for JPA Auditing
axios.interceptors.request.use((config) => {
  const userId = store.getState().auth?.userId;
  if (userId) {
    config.headers['X-User-Id'] = userId;
  }
  return config;
});
```

- [ ] **Step 3: Verify store exports**

Confirm `Frontend-test/src/store/index.js` has `export default store;` (it does - line 28).
Confirm `Astro-vendor-portal-main/src/store/index.js` has same export.

---

## Task 3: Remove Redundant Manual setCreatedBy/setUpdatedBy Calls

**Scope:** Remove ONLY `entity.setCreatedBy(dto.getCreatedBy())` and `entity.setUpdatedBy(dto.getUpdatedBy())` patterns in create/update methods. JPA Auditing now handles these via `@CreatedBy`/`@LastModifiedBy`.

**KEEP these patterns (not redundant):**
- `dto.setCreatedBy(entity.getCreatedBy())` — entity-to-DTO response mapping
- `entity.setCreatedBy(old.getCreatedBy())` — preserving original creator on copy/revision
- `entity.setCreatedBy(String.valueOf(actionBy))` — explicit different user
- `entity.setUpdatedBy(updatedBy)` where updatedBy is a method parameter with explicit value
- Anything in scheduler/batch context
- `materialStatus.setCreatedBy(...)` — child entities created in batch

**Files to modify (remove redundant DTO-to-entity sets on create/update):**

- [ ] **Step 1: Remove from simple CRUD services**

These services follow the pattern: create method copies dto.createdBy/updatedBy to entity, update method copies dto.updatedBy to entity. Remove ONLY the entity.set lines, keep dto.set lines.

Services with this pattern:
1. `AssetServiceImpl.java` — lines 47-48 (create), 78-79 (update). Remove 4 lines.
2. `ContigencyPurchaseServiceImpl.java` — line 78 (create), 131-132 (update). Remove 3 lines.
3. `GoodsResturnServiceImpl.java` — lines 40-41 (create), 63-64 (update). Remove 4 lines.
4. `GatepassServiceImpl.java` — lines 38-39 (create), 100-101 (update). Remove 4 lines.
5. `GoodsReceiptInspectionServiceImpl.java` — lines 51-52 (create), 81,83 (update). Remove 4 lines.
6. `JobMasterServiceImpl.java` — lines 61-62 (create), 108-109 (update). Remove 4 lines.
7. `GoodsInspectionServiceImpl.java` — lines 58-59 (create), 116-117 (update). Remove 4 lines.
8. `GoodsTransferServiceImpl.java` — lines 42-43 (create), 69-70 (update). Remove 4 lines.
9. `MaterialCreationServiceImpl.java` — lines 53-54 (create), 92-93 (update). Remove 4 lines.
10. `LocationMasterServiceImpl.java` — lines 39-40 (create), 63-64 (update). Remove 4 lines.
11. `MaterialMasterServiceImpl.java` — lines 81-82 (create), 129 (update). Remove 3 lines.
12. `MaterialDisposalServiceImpl.java` — lines 56-57 (create), 110-111 (update). Remove 4 lines.
13. `WorkMasterServiceImpl.java` — lines 36-37 (create), 74-75 (update). Remove 4 lines.
14. `WorkOrderImpl.java` — lines 82-83 (create), 129-130 (update). Remove 4 lines.
15. `PaymentVoucherServiceImpl.java` — line 55 (create). Remove 1 line.
16. `DepartmentComputerPriceLimitServiceImpl.java` — lines 50-51 (create), 88 (update). Remove 3 lines.

- [ ] **Step 2: Remove from services with DTO-to-entity and explicit userId patterns**

These services mix DTO copies with explicit userId sets. Remove ONLY the dto-copy lines:

17. `ApprovalLimitServiceImpl.java` — line 198 (create), 44 (update), 207 (explicit updatedBy param — KEEP). Remove 2 lines.
18. `DepartmentApproverServiceImpl.java` — line 208 (create), 39 (update). Remove 2 lines.
19. `FieldStationApproverServiceImpl.java` — line 182 (create), 36 (update). Remove 2 lines.
20. `EmployeeDepartmentMasterServiceImpl.java` — lines 127-128, 259-260, 583-584 (creates), 361, 648 (updates). Remove 8 lines. KEEP lines 451, 472 (explicit updatedBy param).

- [ ] **Step 3: Remove from procurement services**

21. `IndentCreationServiceImpl.java` — lines 241-242, 458-459 (creates), 630 (update). Remove 5 lines. KEEP line 886-887 (preserving old.getCreatedBy on revision copy).
22. `PurchaseOrderImpl.java` — lines 218-219 (create). Remove 2 lines. KEEP line 385-386 (copy preserving original creator).
23. `ServiceOrderServiceImpl.java` — remove dto-to-entity create/update copies (verify pattern first).

- [ ] **Step 4: Remove from inventory services**

24. `GrnServiceImpl.java` — lines 125, 662, 760, 910 (creates). Remove 4 setCreatedBy lines.
25. `DiServiceImpl.java` — line 80 (create). Remove 1 line.
26. `GtServiceImpl.java` — line 78 (create). Remove 1 line.
27. `IsnServiceImpl.java` — line 50 (create). Remove 1 line.
28. `IgpServiceImpl.java` — lines 112, 170 (creates). Remove 2 lines.
29. `OgpServiceImpl.java` — line 146 (create), 381, 550, 667 (creates). Remove 4 lines.
30. `GrvServiceImpl.java` — line 43 (create). Remove 1 line.
31. `GprnServiceImpl.java` — line 81 (create). Remove 1 line.

- [ ] **Step 5: Compile and verify**

Run: `mvn compile -q`
Expected: BUILD SUCCESS with 0 errors

---

## Task 4: Full Build and Test

- [ ] **Step 1: Full Maven build**

Run: `mvn clean install -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 2: Run with tests**

Run: `mvn clean install`
Expected: BUILD SUCCESS (if tests exist and pass)

- [ ] **Step 3: Start the application**

Run: `mvn spring-boot:run` or `java -jar target/*.jar`
Expected: Application starts without errors. Check for:
- No Hibernate schema errors
- JPA Auditing initialization log
- UserContextFilter registered

- [ ] **Step 4: Verify createdBy auto-population**

Using the running app:
1. Login as a user (get userId)
2. Create any entity (e.g., indent, material master)
3. Check DB: `createdBy` should contain the userId string
4. Check `audit_trail` table for a new INSERT record

- [ ] **Step 5: Cleanup temporary Python scripts**

Delete fix scripts from Backend-prod root:
- `fix_compilation.py`
- `fix_conversions.py`
- `fix_dto_types.py`
- `fix_final.py`
- `fix_line_specific.py`
- `fix_remaining.py`
- `fix_service_types.py`
- `audit_fix_listeners.py`
- `audit_migration.py`

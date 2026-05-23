# JPA Auditing Migration — Remaining Tasks Design

**Date:** 2026-05-23
**Status:** Approved
**Scope:** Tasks #17, #19, #20, #21

## Context

The core JPA Auditing migration is complete and compiling:
- 125 entities migrated (Integer -> String for createdBy/updatedBy/modifiedBy)
- 80 DTOs updated
- 5 repositories updated
- ~30 service/controller files updated
- Infrastructure built (UserContextHolder, UserContextFilter, AuditorAwareImpl, JpaAuditingConfig, AuditTrailListener, AuditTrail entity)
- BUILD SUCCESS with 0 errors

Four tasks remain before the migration is feature-complete.

---

## Task #17: Rename modifiedBy -> updatedBy (4 Entities)

### What
Rename the Java field `modifiedBy` to `updatedBy` in 4 entities that use `modifiedBy` instead of the project standard `updatedBy`.

### Entities
1. `WorkflowTransition`
2. `SubWorkflowTransition`
3. `VendorQuotationAgainstTender`
4. `PurchaseOrderHistory`

### Approach
- Rename Java field: `modifiedBy` -> `updatedBy`
- Add `@Column(name = "modified_by")` to map to existing DB column (no schema change)
- Update getter/setter: `getModifiedBy()` -> `getUpdatedBy()`, `setModifiedBy()` -> `setUpdatedBy()`
- Update all references in:
  - DTOs (WorkflowTransitionDto, SubWorkflowTransitionDto, VendorQuotationAgainstTenderDto, etc.)
  - Repositories (findByModifiedBy -> findByUpdatedBy)
  - Services (all setModifiedBy/getModifiedBy calls)
  - Controllers
- Annotation `@LastModifiedBy` already present on field — stays

### Risk
- Low. Purely a rename with column mapping. No DB migration needed.
- Grep for all `modifiedBy` references to ensure none missed.

---

## Task #19: Remove Redundant Manual Set Calls

### What
Remove manual `setCreatedBy()` and `setUpdatedBy()` calls that are redundant because JPA Auditing now auto-populates these fields.

### Rules
**Remove** calls where:
- Value comes from request context (same user JPA Auditing would set)
- Pattern: `entity.setCreatedBy(String.valueOf(userId))` where userId is from request/DTO

**Keep** calls where:
- Explicit different userId is used (workflow `actionByUserId` scenarios)
- Batch/scheduler context where no HTTP request exists
- Value comes from a different source entity (e.g., copying createdBy from parent to child)

### Approach
1. Grep all `setCreatedBy` and `setUpdatedBy` calls across services
2. Classify each as redundant vs. intentional
3. Remove redundant ones
4. For scheduler jobs (PendingApprovalReminderSchedule), keep manual sets since no HTTP context

### Risk
- Medium. Must correctly identify which calls are redundant vs. business-critical.
- If a service sets createdBy from actionByUserId (different from logged-in user), removing it would lose that information.
- Mitigation: Conservative approach — only remove obvious cases.

---

## Task #20: Frontend Axios Interceptor

### What
Add `X-User-Id` header to all API requests from both frontend apps so the backend `UserContextFilter` can populate `UserContextHolder`.

### Frontend Apps
1. **Frontend-test** (React, Redux)
2. **Astro-vendor-portal** (React)

### Approach
For each app:
1. Find existing axios configuration or create axios instance
2. Add request interceptor that reads userId from Redux store (`state.auth.userId`)
3. Attach `X-User-Id` header to every outgoing request

```javascript
// Example interceptor
axios.interceptors.request.use((config) => {
  const userId = store.getState().auth?.userId;
  if (userId) {
    config.headers['X-User-Id'] = userId;
  }
  return config;
});
```

### Risk
- Low. Additive change — just adds a header.
- If userId not in Redux state at login time, header won't be sent and JPA Auditing falls back to "SYSTEM".

---

## Task #21: Build and Test

### What
Full build verification and runtime smoke test.

### Steps
1. `mvn clean install` — full build with test execution
2. Start application
3. Verify:
   - Application starts without errors
   - Login endpoint works
   - Creating an entity (e.g., indent) auto-populates `createdBy` with logged-in userId
   - Updating an entity auto-populates `updatedBy`
   - `audit_trail` table receives records on insert/update
4. Check Hibernate DDL output for any unexpected schema changes

### Success Criteria
- Build succeeds with 0 errors
- Application starts
- createdBy/updatedBy auto-populated from X-User-Id header
- audit_trail table populated with change records

---

## Execution Order

1. Task #17 (rename) — foundational, other tasks may reference updatedBy
2. Task #19 (remove manual sets) — cleanup after rename
3. Task #20 (frontend interceptor) — enables runtime testing
4. Task #21 (build + test) — validates everything

## Branch Strategy

All changes remain uncommitted on main. User will create feature branch and commit when ready.

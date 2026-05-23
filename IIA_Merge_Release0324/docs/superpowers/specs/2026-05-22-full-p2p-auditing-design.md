# Full P2P Auditing System Design

## Context

Astro ERP is a Procure-to-Pay system (Indent -> Tender -> PO/SO -> GRN/GI -> Payment).
Every database write must record who performed it. The system currently has no Spring Security,
no JWT, no SecurityContext. User identity flows from the frontend via API calls. The system is
still under development on a closed network.

### Current State

- **Spring Boot 2.7.3**, Java 17, MySQL, `javax.persistence`
- **125 JPA entities** total
- ~70 entities already have `createdBy`/`updatedBy` fields
- ~55 child/detail tables have no audit fields
- Mixed types: ~30 entities use `Integer createdBy`, ~35 use `String createdBy`
- 4 entities use `modifiedBy` instead of `updatedBy`
- Date field names vary: `createDate`, `createdDate`, `updateDate`, `updatedDate`
- Some date fields have inline `= LocalDateTime.now()` initialization
- `spring.jpa.hibernate.ddl-auto=update` (Hibernate manages schema)
- No `@EnableJpaAuditing`, no `AuditorAware`, no `ThreadLocal`, no `@CreatedBy` anywhere

### Decision Log

| Decision | Choice | Reason |
|----------|--------|--------|
| Approach | JPA Auditing + Hibernate Event Listeners | Automatic, low maintenance, full coverage |
| Type standardization | All `createdBy`/`updatedBy` to `String` | `AuditorAware<String>` serves all entities; forward-compatible |
| userId transport | `X-User-Id` HTTP header via axios interceptor | One global change covers all API calls |
| `modifiedBy` fields | Rename to `updatedBy` everywhere | Consistency across all 125 entities |
| Child tables | Add audit fields to all 55 | Full coverage — every table independently auditable |
| Audit trail storage | Single `audit_trail` table with JSON diff column | Compact, no shadow tables, queryable |
| Audit trail mechanism | Hibernate Event Listeners (not JPA `@EntityListeners`) | Provides old/new state arrays; is a Spring bean |
| Existing Option 1 work | Revert all | JPA Auditing makes manual threading redundant |

---

## Architecture

```
Frontend (React + Redux)
    |
    |  axios interceptor adds: X-User-Id: "42"
    v
UserContextFilter (Servlet Filter, @Order(1))
    |
    |  reads X-User-Id header -> UserContextHolder.set("42")
    |  finally: UserContextHolder.clear()
    v
Controllers -> Services -> Repositories
    |
    |  repository.save(entity)
    v
Two separate listener systems fire:
    |
    +---> AuditingEntityListener (@CreatedBy / @LastModifiedBy)
    |       reads AuditorAwareImpl.getCurrentAuditor()
    |       reads UserContextHolder.get() -> "42"
    |       sets entity.createdBy = "42" (INSERT only)
    |       sets entity.updatedBy = "42" (INSERT + UPDATE)
    |
    +---> AuditTrailHibernateListener (PostInsert/PostUpdate/PostDelete)
    |       compares event.getOldState() vs event.getState()
    |       writes diff as JSON to audit_trail table
    |       changedBy = UserContextHolder.get()
    v
Database
    |
    +-- entity tables: createdBy/updatedBy auto-populated
    +-- audit_trail: full field-level change history
```

---

## New Files (7 total)

### 1. `com.astro.config.UserContextHolder`

Static ThreadLocal wrapper. Stores the current user ID for the duration of one HTTP request.

```java
public class UserContextHolder {
    private static final ThreadLocal<String> currentUserId = new ThreadLocal<>();

    public static void set(String userId)  { currentUserId.set(userId); }
    public static String get()             { return currentUserId.get(); }
    public static void clear()             { currentUserId.remove(); }
}
```

### 2. `com.astro.config.UserContextFilter`

Servlet filter registered at high priority. Reads `X-User-Id` from request header, stores in ThreadLocal, clears after request completes.

```java
@Component
@Order(1)
public class UserContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String userId = request.getHeader("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                UserContextHolder.set(userId.trim());
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }
}
```

### 3. `com.astro.config.AuditorAwareImpl`

Implements `AuditorAware<String>`. JPA Auditing calls this to get the current user.

```java
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(UserContextHolder.get());
    }
}
```

### 4. `com.astro.config.JpaAuditingConfig`

Enables JPA Auditing and points to the AuditorAware bean.

```java
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class JpaAuditingConfig {
}
```

### 5. `com.astro.entity.AuditTrail`

Entity for the audit trail table.

```java
@Entity
@Table(name = "audit_trail", indexes = {
    @Index(name = "idx_audit_entity", columnList = "entityName, entityId"),
    @Index(name = "idx_audit_changed_by", columnList = "changedBy"),
    @Index(name = "idx_audit_changed_at", columnList = "changedAt")
})
public class AuditTrail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String entityName;

    @Column(nullable = false, length = 100)
    private String entityId;

    @Column(nullable = false, length = 10)
    private String action;   // CREATE, UPDATE, DELETE

    @Column(length = 50)
    private String changedBy;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @Column(columnDefinition = "TEXT")
    private String changesJson;

    // Getters, setters, or @Data
}
```

### 6. `com.astro.repository.AuditTrailRepository`

```java
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {
    List<AuditTrail> findByEntityNameAndEntityIdOrderByChangedAtDesc(
        String entityName, String entityId);
}
```

### 7. `com.astro.config.AuditTrailListener`

Hibernate event listener. Registered as a Spring bean. Captures field-level diffs on every
INSERT, UPDATE, and DELETE using Hibernate's built-in old/new state arrays.

```java
@Component
public class AuditTrailListener implements
        PostInsertEventListener,
        PostUpdateEventListener,
        PostDeleteEventListener,
        HibernatePropertiesCustomizer {

    @Autowired
    private DataSource dataSource;  // Direct JDBC — not JPA repo (avoids session recursion during flush)

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        // Self-register with Hibernate's event system
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        // action = "CREATE"
        // event.getState() = new values
        // event.getPersister().getPropertyNames() = field names
        // Build JSON: {field: {old: null, new: value}} for all non-null fields
        // Save to audit_trail
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        // action = "UPDATE"
        // event.getOldState() = previous values
        // event.getState() = new values
        // Compare old vs new, build JSON only for changed fields
        // Save to audit_trail
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        // action = "DELETE"
        // event.getDeletedState() = values at time of deletion
        // Build JSON: {field: {old: value, new: null}}
        // Save to audit_trail
    }

    // Skip AuditTrail entity itself to avoid infinite recursion
}
```

Key implementation details:
- Skip `AuditTrail` entity itself (prevent infinite loop)
- Skip fields: `createdBy`, `updatedBy`, `createDate`, `updateDate` (these are audit metadata, not business data)
- Use `event.getPersister().getEntityName()` for entity name
- Extract primary key from `event.getId()`
- Serialize field values to String for JSON (handle null, dates, BigDecimal, etc.)
- Use direct JDBC insert (via `DataSource`) to save audit records, NOT the JPA repository — Hibernate event listeners fire during flush, and saving via JPA inside a flush causes session recursion. The `AuditTrailRepository` is for reading audit records only.
- JDBC insert runs in the same transaction as the entity change, so audit and data are atomically consistent

---

## Entity Changes

### Category 1: ~70 entities with existing `createdBy`/`updatedBy` fields

Changes per entity:
1. Add `@EntityListeners(AuditingEntityListener.class)` to class
2. Change `Integer createdBy` -> `String createdBy` (where currently Integer)
3. Change `Integer updatedBy` -> `String updatedBy` (where currently Integer)
4. Add `@CreatedBy` annotation on `createdBy`
5. Add `@LastModifiedBy` annotation on `updatedBy`
6. Add `@CreatedDate` on existing date field (whatever its name)
7. Add `@LastModifiedDate` on existing update date field
8. Remove any `= LocalDateTime.now()` inline initialization on date fields

### Category 2: ~55 child/detail tables missing audit fields

Changes per entity:
1. Add `@EntityListeners(AuditingEntityListener.class)` to class
2. Add four new fields:
   ```java
   @CreatedBy
   @Column(name = "created_by", length = 50)
   private String createdBy;

   @LastModifiedBy
   @Column(name = "updated_by", length = 50)
   private String updatedBy;

   @CreatedDate
   @Column(name = "create_date")
   private LocalDateTime createDate;

   @LastModifiedDate
   @Column(name = "update_date")
   private LocalDateTime updateDate;
   ```

### Category 3: 4 entities with `modifiedBy` -> `updatedBy` rename

Entities: `WorkflowTransition`, `SubWorkflowTransition`, `VendorQuotationAgainstTender`, `PurchaseOrderHistory`

Changes per entity:
1. Rename field `modifiedBy` -> `updatedBy`
2. Update `@Column(name = "updated_by")` (or let Hibernate rename via ddl-auto)
3. Update all DTOs that expose `modifiedBy`
4. Update all service/controller code that calls `getModifiedBy()` / `setModifiedBy()`
5. Add `@LastModifiedBy` annotation

---

## Service Layer Changes

### Remove all manual `setCreatedBy()` / `setUpdatedBy()` calls

JPA Auditing handles this automatically. Every `entity.setCreatedBy(...)` and
`entity.setUpdatedBy(...)` call in service classes becomes dead code and should be removed.

This includes reverting all Option 1 changes made in the prior session:
- BudgetService interface: remove `String updatedBy` params from 10 methods
- BudgetServiceImpl: remove `saveBudgetWithAudit` helper, revert all signature changes
- LOVServiceImpl: remove `setUpdatedBy` in updateLOV
- LOVController: remove `setCreatedBy`/`setUpdatedBy` mappings in `convertToEntity`
- LocationMasterServiceImpl, LocatorServiceImpl, UomMasterServiceImpl, DepartmentComputerPriceLimitServiceImpl: revert "SYSTEM" replacement changes
- VendorMasterServiceImpl: revert `setCreatedBy` addition
- VendorQuotationAgainstTenderServiceImpl: revert `setModifiedBy(dto.getCreatedBy())` changes
- ogpAssetServiceImpl: revert `setCreatedBy(request.getCreatedBy())` change
- IndentCreationServiceImpl: revert 2 budget caller changes

### Keep DTO `createdBy`/`updatedBy` fields

DTOs that already have `createdBy`/`updatedBy` fields can keep them:
- **Response DTOs:** Still populated from entity fields — UI shows who created/updated
- **Request DTOs:** Fields become ignored by backend for auditing purposes (header carries userId now). No need to remove them — they're harmless and some frontend code still sends them.

---

## Frontend Changes

### Main app (Frontend-test)

**One global change** in axios setup (e.g., `src/utils/axios.js` or `src/App.js`):

```js
import store from '../store'; // or wherever Redux store is exported

axios.interceptors.request.use(config => {
  const userId = store.getState().auth?.userId;
  if (userId) {
    config.headers['X-User-Id'] = String(userId);
  }
  return config;
});
```

This automatically adds the header to every API call, making per-page `createdBy: userId`
in payloads redundant.

**Admin pages** that currently hardcode `createdBy: 'admin'` are fixed automatically since
the header carries the real userId. No per-page fix needed.

### Vendor portal (Astro-vendor-portal-main)

Same interceptor, but reads `vendorId`:

```js
axios.interceptors.request.use(config => {
  const vendorId = store.getState().auth?.vendorId;
  if (vendorId) {
    config.headers['X-User-Id'] = String(vendorId);
  }
  return config;
});
```

---

## Database Changes

### Automatic via `ddl-auto=update`

Hibernate will automatically:
- Alter Integer columns to VARCHAR when entity field type changes to String
- Add new `created_by`, `updated_by`, `create_date`, `update_date` columns to child tables
- Create `audit_trail` table from the new entity

### Manual (if needed)

For the 4 `modifiedBy` -> `updatedBy` renames, Hibernate's `ddl-auto=update` may add a new
`updated_by` column instead of renaming `modified_by`. To avoid orphan columns:

```sql
-- Run after entity changes, before app restart (optional cleanup)
ALTER TABLE workflow_transition CHANGE COLUMN modified_by updated_by VARCHAR(50);
ALTER TABLE sub_workflow_transition CHANGE COLUMN modified_by updated_by VARCHAR(50);
ALTER TABLE vendor_quotation_against_tender CHANGE COLUMN modified_by updated_by VARCHAR(50);
ALTER TABLE purchase_order_history CHANGE COLUMN modified_by updated_by VARCHAR(50);
```

Or let Hibernate create new columns and migrate data manually, then drop old columns.

---

## Edge Cases

| Scenario | Solution |
|----------|----------|
| DataInitializer (seed data) | Wrap with `UserContextHolder.set("SYSTEM")` / `.clear()` |
| Scheduled tasks / @Async | Same — explicit set/clear around the work |
| No X-User-Id header sent | `createdBy`/`updatedBy` = null. Audit trail still logs the change with `changedBy = null`. Consider adding a warning log in the filter. |
| Entity manually sets createdBy before persist | `@CreatedBy` overwrites it. If "on behalf of" is needed, use ThreadLocal override instead. |
| Audit trail entity itself | Listener skips `AuditTrail` entity to prevent infinite recursion |
| Large batch operations | Audit trail may generate many rows. Consider async write or batch insert for performance. |
| Existing data with Integer createdBy values | MySQL auto-converts Integer to VARCHAR string (e.g., `42` -> `"42"`). No data loss. |

---

## Implementation Order

1. Build 4 infrastructure classes (UserContextHolder, Filter, AuditorAware, Config)
2. Standardize entity types (Integer -> String) for ~30 entities
3. Rename `modifiedBy` -> `updatedBy` for 4 entities + DTOs + services
4. Add audit annotations to ~70 existing entities
5. Add audit fields + annotations to ~55 child entities
6. Remove all manual `setCreatedBy`/`setUpdatedBy` calls from services (includes reverting Option 1 changes)
7. Build AuditTrail entity + repository
9. Build Hibernate AuditTrailListener
10. Add frontend axios interceptor (main app + vendor portal)
11. Test: verify createdBy auto-populated, audit_trail populated, no regressions

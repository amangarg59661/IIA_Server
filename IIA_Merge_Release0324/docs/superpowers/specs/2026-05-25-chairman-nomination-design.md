# Chairman Member Nomination — Design Spec

## Summary

Allow the STEC Chairman to nominate any system user as a committee member for a specific tender during the evaluation flow. The system auto-assigns the "Committee Member" role if the user doesn't already have it, creates a vote row for that tender, and auto-removes the role when PO is generated (if the user has no other active tender assignments).

## Context

- Roles are managed via `USER_ROLE_MASTER` (userId + roleId) and `ROLE_MASTER` (roleId + roleName).
- Committee membership for tenders is tracked in `TenderCommitteeDecision` (one row per member per tender).
- Permanent STEC members are in `techno_financial_committee` table.
- Tender lock happens when PO is created: `TenderRequest.lockedForPO` is set to PO ID.
- Frontend role name: `"Committee Member"`. Backend role constant used in routing: `"COMMITTEE_MEMBER"`.

## API Endpoint

### `POST /api/admin/techno-financial-committee/nominate`

**Request body:**
```json
{
  "tenderId": "T-2026-001",
  "userId": 42,
  "nominatedBy": 10
}
```

**Flow:**

1. **Validate tender exists** — fetch `TenderRequest` by `tenderId`, throw 404 if missing.
2. **Validate caller is chairman** — fetch `TenderEvaluation` for tender, determine STEC type from `amountCategory`. Look up active CHAIRMAN for that STEC type. Verify `nominatedBy` matches chairman's userId. Throw 403 if mismatch.
3. **Check user not already assigned** — query `TenderCommitteeDecision` for this `tenderId` + `userId`. If exists, throw 400 "User already assigned to this tender".
4. **Auto-assign Committee Member role:**
   - Query `RoleMaster.findByRoleName("Committee Member")` to get `roleId`.
   - Query `UserRoleMasterRepository.findByRoleIdAndUserId(roleId, userId)`.
   - If row exists and `isActive=true` → no-op (already has active role).
   - If row exists and `isActive=false` → set `isActive=true` (reactivate).
   - If no row exists → create new `UserRoleMaster` row with `userId`, `roleId`, `readPermission=true`, `writePermission=true`, `isActive=true`.
5. **Create TenderCommitteeDecision row** — same structure as existing members get during evaluation initiation (see `TenderEvaluationApprovalServiceImpl` line 222-228):
   ```
   tenderId = tenderId
   committeeUserId = userId
   committeeMemberName = user's name (from UserMaster)
   createdDate = now
   updatedDate = now
   ```
6. **Return** nominated member details.

**Response:**
```json
{
  "responseData": {
    "userId": 42,
    "userName": "Dr. Ramesh Kumar",
    "tenderId": "T-2026-001",
    "roleAssigned": true,
    "message": "Member nominated and Committee Member role assigned."
  }
}
```

## Role Deactivation on PO Generation

Roles are **soft-deactivated**, not deleted — so they can be reactivated if needed.

### Prerequisite: Add `isActive` to UserRoleMaster

`UserRoleMaster` currently has no `isActive` field. Add:
```java
@Column(name = "IS_ACTIVE", columnDefinition = "BOOLEAN DEFAULT TRUE")
private Boolean isActive = true;
```
Hibernate `ddl-auto=update` will add the column. Existing rows default to `true`.

**Impact:** All existing queries using `UserRoleMaster` (workflow routing, role checks) must be verified to filter by `isActive=true`. Key consumers: `WorkflowServiceImpl`, `UserServiceImpl`, `JobMasterServiceImpl`, `MaterialMasterUtilServiceImpl`, `VendorMasterUtilServiceImpl`.

### Trigger Points

Both in `PurchaseOrderImpl.java`:
- `createPurchaseOrder()` — after line 291 (after `trRepo.save(existing)`)
- `submitDraftPo()` — after line 2096 (after `trRepo.save(tender)`)

### Deactivation Logic

After `lockedForPO` is set for a tender:

1. Fetch all `TenderCommitteeDecision` rows for this `tenderId`.
2. For each unique `committeeUserId`:
   a. **Is user a permanent STEC member?** Query `techno_financial_committee` table for `userId` where `isActive=true`. If yes → skip (permanent members keep their role active).
   b. **Does user have other active tender assignments?** Query `TenderCommitteeDecision` for this `userId` where the associated `TenderRequest.lockedForPO IS NULL` (tender not yet PO'd) AND `tenderId != current tenderId`. If yes → skip.
   c. **Neither** → set `isActive=false` on the `UserRoleMaster` row for this `userId` + "Committee Member" `roleId`. Do NOT delete the row.

### Reactivation

When a user is nominated again (step 4 of nomination flow), check for existing **inactive** `UserRoleMaster` row:
- If inactive row exists → set `isActive=true` (reactivate).
- If no row exists → create new row with `isActive=true`.
- If active row exists → no-op.

### Extract to Shared Method

Create `deactivateNominatedMemberRoles(String tenderId)` in `TechnoFinancialCommitteeServiceImpl` (or a new helper service). Call from both PO creation points.

## Frontend Changes — TenderEvaluator.jsx

### Where

In the chairman's action section of TenderEvaluator (around line 2121+), add a "Nominate Member" button/section visible only when `isChairman === true` and tender amount is above 10L.

### UI

1. **Button:** "Nominate Member" — opens a modal or inline dropdown.
2. **User search dropdown:** Fetch from `GET /api/userMaster` (existing endpoint), filtered client-side to exclude users already in `committeeDecisions` array for this tender.
3. **On select + confirm:** Call `POST /api/admin/techno-financial-committee/nominate` with `{ tenderId, userId, nominatedBy: currentUserId }`.
4. **On success:** Refresh committee decisions list. Show success message.

### User Pool Filter

Show all system users EXCEPT:
- Users already in `committeeDecisions` for this tender.
- The chairman themselves.

## Files to Modify

| Layer | File | Change |
|---|---|---|
| Entity | `UserRoleMaster.java` | Add `isActive` column (Boolean, default true) |
| Controller | `TechnoFinancialCommitteeController.java` | Add `POST /nominate` endpoint |
| Service Interface | `TechnoFinancialCommitteeService.java` | Add `nominateMember()` and `deactivateNominatedMemberRoles()` |
| Service Impl | `TechnoFinancialCommitteeServiceImpl.java` | Implement nomination + role activation/deactivation |
| Service Impl | `PurchaseOrderImpl.java` | Call `deactivateNominatedMemberRoles(tenderId)` at both PO creation points |
| Repository | `UserRoleMasterRepository.java` | Add `findByUserIdAndRoleId()` (for reactivation check) |
| Repository | `TenderCommitteeDecisionRepository` | Add query for active tender check |
| Verify | `WorkflowServiceImpl`, `UserServiceImpl`, etc. | Ensure role queries filter by `isActive=true` |
| Frontend | `TenderEvaluator.jsx` | Add nomination UI for chairman |

## Repository Queries Needed

### TenderCommitteeDecisionRepository
```java
List<TenderCommitteeDecision> findByTenderId(String tenderId);

@Query("SELECT DISTINCT d.committeeUserId FROM TenderCommitteeDecision d " +
       "WHERE d.committeeUserId = :userId " +
       "AND d.tenderId != :excludeTenderId " +
       "AND d.tenderId IN (SELECT t.tenderId FROM TenderRequest t WHERE t.lockedForPO IS NULL)")
List<Integer> findActiveAssignmentsExcludingTender(
    @Param("userId") Integer userId,
    @Param("excludeTenderId") String excludeTenderId);
```

### UserRoleMasterRepository
```java
Optional<UserRoleMaster> findByUserIdAndRoleId(Integer userId, Integer roleId);
```

## Edge Cases

1. **Chairman nominates themselves** — block. Chairman already has Chairman role and shouldn't be a voting member.
2. **User already has Committee Member role from another tender** — don't create duplicate `UserRoleMaster` row, just add `TenderCommitteeDecision`.
3. **Tender already has PO** — block nomination if `lockedForPO` is not null.
4. **Role doesn't exist in ROLE_MASTER** — throw config error. Admin must create "Committee Member" role first.
5. **Cleanup race condition** — if two POs are created simultaneously for different tenders sharing a nominated member, both cleanup calls will check independently. Safe because cleanup only removes role when NO active tenders remain.

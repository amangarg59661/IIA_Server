# Clarification History Response Mapping Fix — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix `respondToClarification()` so vendor responses target the correct clarification history row when multiple open rows exist, and gate eval status restoration on ALL open rows being resolved.

**Architecture:** Add `clarificationHistoryId` to response DTO for PK-based targeting. Add a GET endpoint to expose open clarification questions per vendor. Replace per-branch restore logic with a unified gate checking both quotation statuses and history row counts. Fallback `findFirst()` gets target-type filtering for backward compat.

**Tech Stack:** Java 11, Spring Boot, Spring Data JPA (derived queries), Lombok

**Spec:** `docs/superpowers/specs/2026-05-27-clarification-history-response-mapping-design.md`

---

### Task 1: Add `clarificationHistoryId` to RespondClarificationDto

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/RespondClarificationDto.java`

- [ ] **Step 1: Add the field**

In `RespondClarificationDto.java`, add this field after the existing `vendorId` field (line 30):

```java
    /** PK of TenderClarificationHistory row — targets exact clarification question.
        Null = fallback to filtered lookup (backward compat). */
    private Long clarificationHistoryId;
```

The full file after edit:

```java
package com.astro.dto.workflow;

import lombok.Data;

/**
 * DTO used when a vendor, indentor, purchase personnel, or committee member
 * responds to a clarification request.
 */
@Data
public class RespondClarificationDto {

    private String tenderId;

    /**
     * Role of the person responding.
     * Allowed: VENDOR, INDENTOR, PURCHASE_PERSONNEL, COMMITTEE_MEMBER
     */
    private String respondedByRole;

    /** UserId (or vendorId for vendors) of the responder */
    private String respondedById;

    /** The clarification response text */
    private String responseText;

    /** Optional supporting file name (uploaded separately) */
    private String responseFileName;

    /** When PP responds on behalf of a vendor (GEM/OPEN/GLOBAL mode) */
    private String vendorId;

    /** PK of TenderClarificationHistory row — targets exact clarification question.
        Null = fallback to filtered lookup (backward compat). */
    private Long clarificationHistoryId;
}
```

- [ ] **Step 2: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/dto/workflow/RespondClarificationDto.java
git commit -m "feat: add clarificationHistoryId to RespondClarificationDto"
```

---

### Task 2: Add Repository Query Methods

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/repository/TenderClarificationHistoryRepository.java`

- [ ] **Step 1: Add the two derived query methods**

Add these two methods inside the `TenderClarificationHistoryRepository` interface, after the existing `findMaxRoundByTenderId` method (after line 17):

```java
    List<TenderClarificationHistory> findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(
            String tenderId, String targetVendorId);

    long countByTenderIdAndRespondedAtIsNull(String tenderId);
```

The full file after edit:

```java
package com.astro.repository;

import com.astro.entity.TenderClarificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenderClarificationHistoryRepository extends JpaRepository<TenderClarificationHistory, Long> {

    List<TenderClarificationHistory> findByTenderIdOrderByRequestedAtDesc(String tenderId);

    @Query("SELECT COALESCE(MAX(h.roundNumber), 0) FROM TenderClarificationHistory h WHERE h.tenderId = :tenderId")
    int findMaxRoundByTenderId(@Param("tenderId") String tenderId);

    List<TenderClarificationHistory> findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(
            String tenderId, String targetVendorId);

    long countByTenderIdAndRespondedAtIsNull(String tenderId);
}
```

- [ ] **Step 2: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/repository/TenderClarificationHistoryRepository.java
git commit -m "feat: add open-clarification query methods to repository"
```

---

### Task 3: Add Service Interface Method + Controller Endpoint

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/TenderEvaluationApprovalService.java`
- Modify: `Backend-prod/src/main/java/com/astro/controller/ProcurementModuleController/TenderEvaluationController.java`

- [ ] **Step 1: Add method signature to service interface**

In `TenderEvaluationApprovalService.java`, add this method after the `getClarificationHistory` method (after line 149):

```java
    /**
     * Returns open (unanswered) clarification questions for a specific vendor on a tender.
     * Excludes ALL_VENDORS rows (targetVendorId is null for those).
     */
    List<TenderClarificationHistory> getOpenClarifications(String tenderId, String vendorId);
```

- [ ] **Step 2: Add GET endpoint to controller**

In `TenderEvaluationController.java`, add this endpoint in the QUERY ENDPOINTS section, after the `getClarificationHistory` endpoint (after line 291):

```java
    @GetMapping("/open-clarifications")
    public ResponseEntity<Object> getOpenClarifications(
            @RequestParam String tenderId,
            @RequestParam String vendorId) {
        log.info("Get open clarifications tenderId={} vendorId={}", tenderId, vendorId);
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(approvalService.getOpenClarifications(tenderId, vendorId)),
                HttpStatus.OK);
    }
```

No new imports needed — `TenderClarificationHistory` is already returned by `getClarificationHistory` and the controller delegates to the service. The entity is serialized by Jackson directly (same pattern as the existing `clarification-history` endpoint).

- [ ] **Step 3: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/service/TenderEvaluationApprovalService.java
git add Backend-prod/src/main/java/com/astro/controller/ProcurementModuleController/TenderEvaluationController.java
git commit -m "feat: add GET /open-clarifications endpoint for vendor-specific open questions"
```

---

### Task 4: Implement `getOpenClarifications()` in ServiceImpl

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java`

- [ ] **Step 1: Add the implementation method**

Add this method in `TenderEvaluationApprovalServiceImpl.java` directly after the existing `getClarificationHistory` method. Find the `getClarificationHistory` method (it returns `clarificationHistoryRepository.findByTenderIdOrderByRequestedAtDesc(tenderId)`) and add immediately after it:

```java
    @Override
    public List<TenderClarificationHistory> getOpenClarifications(String tenderId, String vendorId) {
        return clarificationHistoryRepository
                .findByTenderIdAndTargetVendorIdAndRespondedAtIsNull(tenderId, vendorId);
    }
```

No new imports needed — `TenderClarificationHistory`, `List`, and the repository are already imported.

- [ ] **Step 2: Compile check**

Run the project build to verify compilation:

```bash
cd Backend-prod && mvn compile -q
```

Expected: BUILD SUCCESS (no errors)

- [ ] **Step 3: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java
git commit -m "feat: implement getOpenClarifications in service impl"
```

---

### Task 5: Rewrite `respondToClarification()` — VENDOR Branch History Update

This is the core fix. We modify the VENDOR branch (lines 1241-1258 of `TenderEvaluationApprovalServiceImpl.java`) to use PK-based lookup when `clarificationHistoryId` is provided, with target-type-filtered fallback.

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java`

- [ ] **Step 1: Replace VENDOR branch history update block**

In `respondToClarification()`, find the VENDOR branch history update (the `try` block starting at line 1241). Replace only the `try/catch` block — lines 1241 through 1261:

**Old code (lines 1241-1261):**
```java
            // Update clarification history with this vendor's response (non-fatal if table missing)
            try {
                List<TenderClarificationHistory> openRounds = clarificationHistoryRepository
                        .findByTenderIdOrderByRequestedAtDesc(tenderId);
                openRounds.stream()
                        .filter(h -> h.getRespondedAt() == null
                                && ("VENDOR".equals(h.getClarificationTarget())
                                    || "ALL_VENDORS".equals(h.getClarificationTarget()))
                                && (vendorId.equals(h.getTargetVendorId()) || h.getTargetVendorId() == null
                                    || "ALL_VENDORS".equals(h.getClarificationTarget())))
                        .findFirst()
                        .ifPresent(h -> {
                            h.setResponseText(dto.getResponseText());
                            h.setResponseFileName(dto.getResponseFileName());
                            h.setRespondedByRole(dto.getRespondedByRole());
                            h.setRespondedById(dto.getRespondedById());
                            h.setRespondedAt(LocalDateTime.now());
                            clarificationHistoryRepository.save(h);
                        });
            } catch (Exception e) {
                log.warn("Clarification history update failed: {}", e.getMessage());
            }
```

**New code:**
```java
            // Update clarification history — PK-based when available, filtered fallback otherwise
            try {
                Optional<TenderClarificationHistory> historyRow;
                if (dto.getClarificationHistoryId() != null) {
                    historyRow = clarificationHistoryRepository.findById(dto.getClarificationHistoryId())
                            .filter(h -> tenderId.equals(h.getTenderId()) && h.getRespondedAt() == null);
                } else {
                    historyRow = clarificationHistoryRepository
                            .findByTenderIdOrderByRequestedAtDesc(tenderId).stream()
                            .filter(h -> h.getRespondedAt() == null
                                    && ("VENDOR".equals(h.getClarificationTarget())
                                        || "ALL_VENDORS".equals(h.getClarificationTarget()))
                                    && (vendorId.equals(h.getTargetVendorId())
                                        || h.getTargetVendorId() == null))
                            .findFirst();
                }
                historyRow.ifPresent(h -> {
                    h.setResponseText(dto.getResponseText());
                    h.setResponseFileName(dto.getResponseFileName());
                    h.setRespondedByRole(dto.getRespondedByRole());
                    h.setRespondedById(dto.getRespondedById());
                    h.setRespondedAt(LocalDateTime.now());
                    clarificationHistoryRepository.save(h);
                });
            } catch (Exception e) {
                log.warn("Clarification history update failed: {}", e.getMessage());
            }
```

Note: `Optional` is already available via `java.util.*` import at line 46.

- [ ] **Step 2: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java
git commit -m "fix: VENDOR branch — use PK-based history lookup with filtered fallback"
```

---

### Task 6: Rewrite `respondToClarification()` — PP Per-Vendor Branch History Update

Same pattern as Task 5, applied to the PP per-vendor branch (lines 1291-1309).

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java`

- [ ] **Step 1: Replace PP per-vendor branch history update block**

Find the PP per-vendor branch history update `try/catch` block (starts with `// Update clarification history` around line 1290). Replace the `try/catch`:

**Old code (lines 1290-1309):**
```java
            // Update clarification history
            try {
                List<TenderClarificationHistory> openRounds = clarificationHistoryRepository
                        .findByTenderIdOrderByRequestedAtDesc(tenderId);
                openRounds.stream()
                        .filter(h -> h.getRespondedAt() == null
                                && "PURCHASE_PERSONNEL".equals(h.getClarificationTarget())
                                && ppVendorId.equals(h.getTargetVendorId()))
                        .findFirst()
                        .ifPresent(h -> {
                            h.setResponseText(dto.getResponseText());
                            h.setResponseFileName(dto.getResponseFileName());
                            h.setRespondedByRole("PURCHASE_PERSONNEL");
                            h.setRespondedById(dto.getRespondedById());
                            h.setRespondedAt(LocalDateTime.now());
                            clarificationHistoryRepository.save(h);
                        });
            } catch (Exception e) {
                log.warn("Clarification history update failed: {}", e.getMessage());
            }
```

**New code:**
```java
            // Update clarification history — PK-based when available, filtered fallback otherwise
            try {
                Optional<TenderClarificationHistory> historyRow;
                if (dto.getClarificationHistoryId() != null) {
                    historyRow = clarificationHistoryRepository.findById(dto.getClarificationHistoryId())
                            .filter(h -> tenderId.equals(h.getTenderId()) && h.getRespondedAt() == null);
                } else {
                    historyRow = clarificationHistoryRepository
                            .findByTenderIdOrderByRequestedAtDesc(tenderId).stream()
                            .filter(h -> h.getRespondedAt() == null
                                    && "PURCHASE_PERSONNEL".equals(h.getClarificationTarget())
                                    && ppVendorId.equals(h.getTargetVendorId()))
                            .findFirst();
                }
                historyRow.ifPresent(h -> {
                    h.setResponseText(dto.getResponseText());
                    h.setResponseFileName(dto.getResponseFileName());
                    h.setRespondedByRole("PURCHASE_PERSONNEL");
                    h.setRespondedById(dto.getRespondedById());
                    h.setRespondedAt(LocalDateTime.now());
                    clarificationHistoryRepository.save(h);
                });
            } catch (Exception e) {
                log.warn("Clarification history update failed: {}", e.getMessage());
            }
```

- [ ] **Step 2: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java
git commit -m "fix: PP per-vendor branch — use PK-based history lookup with filtered fallback"
```

---

### Task 7: Rewrite `respondToClarification()` — Else Branch History Update

Same pattern, applied to the else branch (indentor/PP-global/member — lines 1323-1342).

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java`

- [ ] **Step 1: Replace else branch history update block**

Find the else branch `try/catch` block (starts with `// Indentor/PP (global)/member response` around line 1323). Replace the `try/catch`:

**Old code (lines 1323-1342):**
```java
        } else {
            // Indentor/PP (global)/member response: update latest open history record (non-fatal if table missing)
            try {
                List<TenderClarificationHistory> openRounds = clarificationHistoryRepository
                        .findByTenderIdOrderByRequestedAtDesc(tenderId);
                openRounds.stream()
                        .filter(h -> h.getRespondedAt() == null)
                        .findFirst()
                        .ifPresent(h -> {
                            if (h.getResponseText() == null || h.getResponseText().isBlank()) {
                                h.setResponseText(dto.getResponseText());
                                h.setResponseFileName(dto.getResponseFileName());
                                h.setRespondedByRole(dto.getRespondedByRole());
                                h.setRespondedById(dto.getRespondedById());
                            }
                            h.setRespondedAt(LocalDateTime.now());
                            clarificationHistoryRepository.save(h);
                        });
            } catch (Exception e) {
                log.warn("Clarification history update failed: {}", e.getMessage());
            }
        }
```

**New code:**
```java
        } else {
            // Indentor/PP (global)/member response — PK-based when available, target-type filtered fallback
            try {
                Optional<TenderClarificationHistory> historyRow;
                if (dto.getClarificationHistoryId() != null) {
                    historyRow = clarificationHistoryRepository.findById(dto.getClarificationHistoryId())
                            .filter(h -> tenderId.equals(h.getTenderId()) && h.getRespondedAt() == null);
                } else {
                    Set<String> allowedTargets = Set.of("INDENTOR", "CHAIRMAN",
                            "PURCHASE_PERSONNEL", "SPECIFIC_MEMBER", "ALL_MEMBERS");
                    historyRow = clarificationHistoryRepository
                            .findByTenderIdOrderByRequestedAtDesc(tenderId).stream()
                            .filter(h -> h.getRespondedAt() == null
                                    && allowedTargets.contains(h.getClarificationTarget()))
                            .findFirst();
                }
                historyRow.ifPresent(h -> {
                    h.setResponseText(dto.getResponseText());
                    h.setResponseFileName(dto.getResponseFileName());
                    h.setRespondedByRole(dto.getRespondedByRole());
                    h.setRespondedById(dto.getRespondedById());
                    h.setRespondedAt(LocalDateTime.now());
                    clarificationHistoryRepository.save(h);
                });
            } catch (Exception e) {
                log.warn("Clarification history update failed: {}", e.getMessage());
            }
        }
```

Note: `Set` is already available via `java.util.*` import at line 46.

- [ ] **Step 2: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java
git commit -m "fix: else branch — use PK-based history lookup with target-type filtered fallback"
```

---

### Task 8: Unified Restore Gate

Replace the per-branch early-return logic and the bottom restore block with a single unified gate that checks both quotation statuses AND open history rows.

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java`

- [ ] **Step 1: Remove per-branch early-return checks from VENDOR branch**

Find and **delete** the VENDOR branch early-return block (the section after the history try/catch and before the `} else if` for PP). This is the block:

```java
            // Only restore eval status when NO vendor quotations remain CHANGE_REQUESTED
            long stillPending = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                    .stream()
                    .filter(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()))
                    .count();
            if (stillPending > 0) {
                // Other vendors haven't responded yet — keep PENDING_VENDOR_CLARIFICATION
                eval.setUpdatedDate(LocalDateTime.now());
                tenderEvaluationRepository.save(eval);
                return buildStatusDto(eval, tender, tenderId);
            }
            // All vendors responded — fall through to restore status below
```

Delete these lines entirely.

- [ ] **Step 2: Remove per-branch early-return checks from PP per-vendor branch**

Find and **delete** the PP per-vendor branch early-return block (same pattern, after its history try/catch):

```java
            // Check if all vendors responded — if not, keep current status
            long stillPending = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                    .stream()
                    .filter(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()))
                    .count();
            if (stillPending > 0) {
                eval.setUpdatedDate(LocalDateTime.now());
                tenderEvaluationRepository.save(eval);
                return buildStatusDto(eval, tender, tenderId);
            }
            // All vendors responded — fall through to restore status below
```

Delete these lines entirely.

- [ ] **Step 3: Replace the bottom restore block with the unified gate**

Find the bottom restore block (after the `} else { ... }` closing brace, starting around `// Restore the previous evaluation status`):

**Old code (lines 1345-1363):**
```java
        // Restore the previous evaluation status
        String restoreStatus = eval.getPreviousEvaluationStatus();
        if (restoreStatus == null || restoreStatus.isBlank()) {
            restoreStatus = "PENDING_APPROVAL";
        }
        eval.setEvaluationStatus(restoreStatus);

        // Clear clarification fields
        eval.setPreviousEvaluationStatus(null);
        eval.setClarificationPendingFrom(null);
        eval.setClarificationPendingFromId(null);
        eval.setClarificationPendingFromName(null);
        eval.setClarificationRequestedByRole(null);
        eval.setClarificationRemarks(null);
        eval.setClarificationTargetVendorId(null);
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
```

**New code:**
```java
        // ── Unified restore gate ────────────────────────────────────────
        // Only restore eval status when ALL clarifications are resolved:
        //   1. No quotations still CHANGE_REQUESTED (covers ALL_VENDORS flow)
        //   2. No history rows with respondedAt=null  (covers specific-row flows)
        boolean quotationsResolved = quotationRepository.findByTenderIdAndIsLatestTrue(tenderId)
                .stream()
                .noneMatch(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()));
        long openHistoryRows = clarificationHistoryRepository.countByTenderIdAndRespondedAtIsNull(tenderId);

        if (!quotationsResolved || openHistoryRows > 0) {
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);
            return buildStatusDto(eval, tender, tenderId);
        }

        // All resolved — restore the previous evaluation status
        String restoreStatus = eval.getPreviousEvaluationStatus();
        if (restoreStatus == null || restoreStatus.isBlank()) {
            restoreStatus = "PENDING_APPROVAL";
        }
        eval.setEvaluationStatus(restoreStatus);

        // Clear clarification fields
        eval.setPreviousEvaluationStatus(null);
        eval.setClarificationPendingFrom(null);
        eval.setClarificationPendingFromId(null);
        eval.setClarificationPendingFromName(null);
        eval.setClarificationRequestedByRole(null);
        eval.setClarificationRemarks(null);
        eval.setClarificationTargetVendorId(null);
        eval.setUpdatedDate(LocalDateTime.now());
        tenderEvaluationRepository.save(eval);

        return buildStatusDto(eval, tender, tenderId);
```

- [ ] **Step 4: Compile check**

```bash
cd Backend-prod && mvn compile -q
```

Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java
git commit -m "fix: unified restore gate — check all open history rows + quotation statuses"
```

---

### Task 9: Smoke Test

Manual verification that the API compiles and starts.

- [ ] **Step 1: Full build**

```bash
cd Backend-prod && mvn clean compile -q
```

Expected: BUILD SUCCESS

- [ ] **Step 2: Verify endpoint reachable (if server running)**

Test the new endpoint with curl or Postman:

```
GET /api/tender-evaluation/open-clarifications?tenderId=TEST-001&vendorId=V001
```

Expected: `200 OK` with empty array `[]` (or populated if test data exists).

Test respond-clarification with new field:

```json
POST /api/tender-evaluation/respond-clarification?tenderId=TEST-001
{
  "respondedByRole": "VENDOR",
  "respondedById": "V001",
  "responseText": "Delivery in 30 days",
  "clarificationHistoryId": 42
}
```

Expected: `200 OK` with evaluation status DTO.

- [ ] **Step 3: Final commit (if any compile fixes needed)**

```bash
git add -A
git commit -m "fix: compile fixes for clarification history response mapping"
```

# Above-10L Per-Vendor Committee Voting Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enable STEC committee members to Accept/Reject individual vendors in above-10L double bid tenders, with chairman resolving conflicts per vendor, and director approving between tech/financial phases.

**Architecture:** New `tender_committee_vendor_decision` table stores per-member per-vendor votes. Three new endpoints handle committee vendor decisions, vote grid retrieval, and chairman resolution. Initiation pre-creates vote rows. Director financial-phase unlock resets and re-creates rows. Frontend extends dual tables to above-10L with committee/chairman action columns.

**Tech Stack:** Spring Boot 2.x, JPA/Hibernate, React (Ant Design), MySQL

**Spec:** `docs/superpowers/specs/2026-05-27-above10l-dual-table-committee-voting-design.md`

**Pre-completed:** Frontend expert→nominate migration already done (TenderEvaluator.jsx:668-683 calls `/api/admin/techno-financial-committee/nominate` with `expert: true`). Backend expert endpoint already commented out.

---

### Task 1: DDL Migration + JPA Entity + Repository

**Files:**
- Create: `Backend-prod/src/main/resources/DDL-committee-vendor-decision.sql`
- Create: `Backend-prod/src/main/java/com/astro/entity/TenderCommitteeVendorDecision.java`
- Create: `Backend-prod/src/main/java/com/astro/repository/ProcurementModule/TenderCommitteeVendorDecisionRepository.java`

- [ ] **Step 1: Create DDL migration file**

```sql
-- DDL-committee-vendor-decision.sql
CREATE TABLE IF NOT EXISTS tender_committee_vendor_decision (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    tender_id         VARCHAR(50)  NOT NULL,
    vendor_id         VARCHAR(50)  NOT NULL,
    committee_user_id INT          NOT NULL,
    member_name       VARCHAR(255),
    decision          VARCHAR(20),
    remarks           VARCHAR(1000),
    phase             VARCHAR(20)  NOT NULL,
    decision_date     DATETIME,
    created_date      DATETIME     NOT NULL,
    updated_date      DATETIME,
    created_by        VARCHAR(50),
    updated_by        VARCHAR(50),
    UNIQUE KEY uk_tender_vendor_member_phase (tender_id, vendor_id, committee_user_id, phase)
);
```

- [ ] **Step 2: Create JPA entity**

```java
package com.astro.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Table(name = "tender_committee_vendor_decision",
       uniqueConstraints = @UniqueConstraint(
           name = "uk_tender_vendor_member_phase",
           columnNames = {"tender_id", "vendor_id", "committee_user_id", "phase"}))
@EntityListeners(AuditingEntityListener.class)
public class TenderCommitteeVendorDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tender_id", nullable = false, length = 50)
    private String tenderId;

    @Column(name = "vendor_id", nullable = false, length = 50)
    private String vendorId;

    @Column(name = "committee_user_id", nullable = false)
    private Integer committeeUserId;

    @Column(name = "member_name", length = 255)
    private String memberName;

    @Column(name = "decision", length = 20)
    private String decision;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "phase", nullable = false, length = 20)
    private String phase;

    @Column(name = "decision_date")
    private LocalDateTime decisionDate;

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @CreatedBy
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
}
```

- [ ] **Step 3: Create repository**

```java
package com.astro.repository.ProcurementModule;

import com.astro.entity.TenderCommitteeVendorDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenderCommitteeVendorDecisionRepository
        extends JpaRepository<TenderCommitteeVendorDecision, Long> {

    List<TenderCommitteeVendorDecision> findByTenderIdAndVendorIdAndPhase(
            String tenderId, String vendorId, String phase);

    List<TenderCommitteeVendorDecision> findByTenderIdAndCommitteeUserIdAndPhase(
            String tenderId, Integer committeeUserId, String phase);

    List<TenderCommitteeVendorDecision> findByTenderIdAndPhase(
            String tenderId, String phase);

    Optional<TenderCommitteeVendorDecision> findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(
            String tenderId, String vendorId, Integer committeeUserId, String phase);

    void deleteByTenderIdAndPhase(String tenderId, String phase);
}
```

- [ ] **Step 4: Run DDL on local database**

Run: `mysql -u root -p astrodatabase < Backend-prod/src/main/resources/DDL-committee-vendor-decision.sql`
Expected: Table created successfully.

- [ ] **Step 5: Commit**

```
git add Backend-prod/src/main/resources/DDL-committee-vendor-decision.sql \
  Backend-prod/src/main/java/com/astro/entity/TenderCommitteeVendorDecision.java \
  Backend-prod/src/main/java/com/astro/repository/ProcurementModule/TenderCommitteeVendorDecisionRepository.java
git commit -m "feat: add TenderCommitteeVendorDecision entity and repository for per-vendor committee voting"
```

---

### Task 2: New DTO + StatusDto Extension

**Files:**
- Create: `Backend-prod/src/main/java/com/astro/dto/workflow/CommitteeVendorVoteDto.java`
- Modify: `Backend-prod/src/main/java/com/astro/dto/workflow/TenderEvaluationStatusDto.java`

- [ ] **Step 1: Create CommitteeVendorVoteDto**

```java
package com.astro.dto.workflow;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommitteeVendorVoteDto {
    private Integer committeeUserId;
    private String memberName;
    private String decision;
    private String remarks;
    private LocalDateTime decisionDate;
}
```

- [ ] **Step 2: Add committeeVendorVotes to TenderEvaluationStatusDto**

In `TenderEvaluationStatusDto.java`, add after line 54 (`financialBidPhase`):

```java
private Map<String, List<CommitteeVendorVoteDto>> committeeVendorVotes;
```

Add import at top:
```java
import java.util.Map;
```

- [ ] **Step 3: Commit**

```
git add Backend-prod/src/main/java/com/astro/dto/workflow/CommitteeVendorVoteDto.java \
  Backend-prod/src/main/java/com/astro/dto/workflow/TenderEvaluationStatusDto.java
git commit -m "feat: add CommitteeVendorVoteDto and extend TenderEvaluationStatusDto"
```

---

### Task 3: Service Methods — Committee Vendor Decision + Vote Grid + Chairman Resolve

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/TenderEvaluationApprovalService.java`
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java`

- [ ] **Step 1: Add 3 method signatures to service interface**

In `TenderEvaluationApprovalService.java`, add after the commented-out `assignExpert` block (~line 72):

```java
    /**
     * Above 10L double bid: committee member Accept/Reject per vendor.
     */
    TenderEvaluationStatusDto committeeVendorDecision(String tenderId, String vendorId,
                                                       String decision, String remarks,
                                                       Integer committeeUserId);

    /**
     * Above 10L double bid: get all committee member votes per vendor.
     */
    Map<String, List<CommitteeVendorVoteDto>> getVendorVoteGrid(String tenderId, String phase);

    /**
     * Above 10L double bid: chairman resolves per-vendor decision.
     * Writes to indentorStatus/financialIndentorStatus. Auto-transitions when all resolved.
     */
    TenderEvaluationStatusDto chairmanVendorResolve(String tenderId, String vendorId,
                                                     String decision, String remarks,
                                                     Integer chairmanUserId);
```

Add imports: `import java.util.Map;` and `import com.astro.dto.workflow.CommitteeVendorVoteDto;`

- [ ] **Step 2: Inject TenderCommitteeVendorDecisionRepository in ServiceImpl**

In `TenderEvaluationApprovalServiceImpl.java`, add to the autowired fields (near the other repository injections):

```java
@Autowired
private TenderCommitteeVendorDecisionRepository committeeVendorDecisionRepository;
```

Add import: `import com.astro.repository.ProcurementModule.TenderCommitteeVendorDecisionRepository;`
Add import: `import com.astro.entity.TenderCommitteeVendorDecision;`
Add import: `import com.astro.dto.workflow.CommitteeVendorVoteDto;`

- [ ] **Step 3: Implement committeeVendorDecision**

Add after the commented-out `assignExpert` block (~line 513):

```java
    // ─────────────────────────────────────────────────────────────────
    // 8a. COMMITTEE VENDOR DECISION (Above 10L Double Bid)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto committeeVendorDecision(String tenderId, String vendorId,
                                                              String decision, String remarks,
                                                              Integer committeeUserId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        Set<String> lockedStatuses = Set.of("PENDING_SPO_APPROVAL", "APPROVED", "REJECTED",
                "PENDING_DIRECTOR_APPROVAL", "PENDING_COMMITTEE_FORMATION");
        if (lockedStatuses.contains(eval.getEvaluationStatus())) {
            throw new BusinessException(new ErrorDetails(400, 1, "LOCKED",
                    "Committee vendor decisions are locked. Current status: "
                    + eval.getEvaluationStatus()));
        }

        // Validate caller is a committee member for this tender
        committeeDecisionRepository.findByTenderIdAndCommitteeUserId(tenderId, committeeUserId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "User " + committeeUserId + " is not a committee member for tender " + tenderId)));

        // Validate vendor exists for this tender
        quotationRepository.findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "No quotation found for vendor " + vendorId + " in tender " + tenderId)));

        String normalizedDecision = decision.toUpperCase();
        if (!"ACCEPTED".equals(normalizedDecision) && !"REJECTED".equals(normalizedDecision)) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Decision must be ACCEPTED or REJECTED"));
        }

        String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";

        // Upsert: member can change vote until chairman resolves
        TenderCommitteeVendorDecision voteRow = committeeVendorDecisionRepository
                .findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(tenderId, vendorId, committeeUserId, phase)
                .orElseGet(() -> {
                    TenderCommitteeVendorDecision r = new TenderCommitteeVendorDecision();
                    r.setTenderId(tenderId);
                    r.setVendorId(vendorId);
                    r.setCommitteeUserId(committeeUserId);
                    r.setPhase(phase);
                    r.setCreatedDate(LocalDateTime.now());
                    return r;
                });

        UserMaster user = userMasterRepository.findByUserId(committeeUserId);
        voteRow.setMemberName(user != null ? user.getUserName() : String.valueOf(committeeUserId));
        voteRow.setDecision(normalizedDecision);
        voteRow.setRemarks(remarks);
        voteRow.setDecisionDate(LocalDateTime.now());
        voteRow.setUpdatedDate(LocalDateTime.now());
        committeeVendorDecisionRepository.save(voteRow);

        return buildStatusDto(eval, tender, tenderId);
    }
```

- [ ] **Step 4: Implement getVendorVoteGrid**

Add immediately after:

```java
    // ─────────────────────────────────────────────────────────────────
    // 8b. GET VENDOR VOTE GRID (Above 10L Double Bid)
    // ─────────────────────────────────────────────────────────────────
    @Override
    public Map<String, List<CommitteeVendorVoteDto>> getVendorVoteGrid(String tenderId, String phase) {
        List<TenderCommitteeVendorDecision> rows =
                committeeVendorDecisionRepository.findByTenderIdAndPhase(tenderId, phase);
        return rows.stream()
                .map(r -> {
                    CommitteeVendorVoteDto dto = new CommitteeVendorVoteDto();
                    dto.setCommitteeUserId(r.getCommitteeUserId());
                    dto.setMemberName(r.getMemberName());
                    dto.setDecision(r.getDecision());
                    dto.setRemarks(r.getRemarks());
                    dto.setDecisionDate(r.getDecisionDate());
                    return Map.entry(r.getVendorId(), dto);
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }
```

- [ ] **Step 5: Implement chairmanVendorResolve**

Add immediately after:

```java
    // ─────────────────────────────────────────────────────────────────
    // 8c. CHAIRMAN VENDOR RESOLVE (Above 10L Double Bid)
    // ─────────────────────────────────────────────────────────────────
    @Transactional
    @Override
    public TenderEvaluationStatusDto chairmanVendorResolve(String tenderId, String vendorId,
                                                            String decision, String remarks,
                                                            Integer chairmanUserId) {
        TenderEvaluation eval = requireEval(tenderId);
        TenderRequest tender = requireTender(tenderId);

        // Validate chairman identity (same as chairmanDecide)
        String amtCat = eval.getAmountCategory();
        if ("ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat)
                || "ABOVE_50_LAKH_UPTO_1_CRORE".equals(amtCat)) {
            String expectedType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) ? "STEC_I" : "STEC_II";
            TechnoFinancialCommittee chairman = committeeRepository
                    .findByRoleAndCommitteeTypeAndIsActiveTrue("CHAIRMAN", expectedType)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(400, 1,
                            "CONFIGURATION_ERROR", "No active Chairman for " + expectedType)));
            if (!chairman.getUserId().equals(chairmanUserId)) {
                throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "Only the " + expectedType + " Chairman can resolve vendor decisions."));
            }
        }

        String normalizedDecision = decision.toUpperCase();
        if (!"ACCEPTED".equals(normalizedDecision) && !"REJECTED".equals(normalizedDecision)) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Decision must be ACCEPTED or REJECTED"));
        }

        String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";

        // Write to VendorQuotationAgainstTender (same fields as saveVendorIndentorDecision)
        VendorQuotationAgainstTender quotation = quotationRepository
                .findByTenderIdAndVendorIdAndIsLatestTrue(tenderId, vendorId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1, "NOT_FOUND",
                        "No quotation found for vendor " + vendorId)));

        if ("FINANCIAL".equals(phase)) {
            quotation.setFinancialIndentorStatus(normalizedDecision);
            quotation.setFinancialIndentorRemarks(remarks);
        } else {
            quotation.setIndentorStatus(normalizedDecision);
            quotation.setIndentorRemarks(remarks);
        }
        quotation.setUpdatedBy(String.valueOf(chairmanUserId));
        quotation.setUpdatedDate(LocalDateTime.now());
        quotationRepository.save(quotation);

        // Audit: record chairman resolution in vendor decision table
        TenderCommitteeVendorDecision chairVoteRow = committeeVendorDecisionRepository
                .findByTenderIdAndVendorIdAndCommitteeUserIdAndPhase(tenderId, vendorId, chairmanUserId, phase)
                .orElseGet(() -> {
                    TenderCommitteeVendorDecision r = new TenderCommitteeVendorDecision();
                    r.setTenderId(tenderId);
                    r.setVendorId(vendorId);
                    r.setCommitteeUserId(chairmanUserId);
                    r.setPhase(phase);
                    r.setCreatedDate(LocalDateTime.now());
                    return r;
                });
        chairVoteRow.setMemberName("Chairman (Resolved)");
        chairVoteRow.setDecision(normalizedDecision);
        chairVoteRow.setRemarks(remarks);
        chairVoteRow.setDecisionDate(LocalDateTime.now());
        chairVoteRow.setUpdatedDate(LocalDateTime.now());
        committeeVendorDecisionRepository.save(chairVoteRow);

        // Check if ALL vendors resolved in current phase → auto-transition
        List<VendorQuotationAgainstTender> allQuotations =
                quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);
        boolean allResolved;
        if ("FINANCIAL".equals(phase)) {
            allResolved = allQuotations.stream()
                    .filter(q -> "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus()))
                    .allMatch(q -> q.getFinancialIndentorStatus() != null
                            && !"PENDING".equalsIgnoreCase(q.getFinancialIndentorStatus()));
        } else {
            allResolved = allQuotations.stream()
                    .allMatch(q -> q.getIndentorStatus() != null
                            && !"PENDING".equalsIgnoreCase(q.getIndentorStatus()));
        }

        if (allResolved) {
            eval.setEvaluationStatus("PENDING_DIRECTOR_APPROVAL");
            eval.setUpdatedDate(LocalDateTime.now());
            tenderEvaluationRepository.save(eval);
        }

        return buildStatusDto(eval, tender, tenderId);
    }
```

- [ ] **Step 6: Commit**

```
git add Backend-prod/src/main/java/com/astro/service/TenderEvaluationApprovalService.java \
  Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java
git commit -m "feat: add committeeVendorDecision, getVendorVoteGrid, chairmanVendorResolve methods"
```

---

### Task 4: Controller Endpoints

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/controller/ProcurementModuleController/TenderEvaluationController.java`

- [ ] **Step 1: Add 3 new endpoints**

Add after the commented-out expert endpoint (~line 169), before `chairmanDecide`:

```java
    @PostMapping("/committee/vendor-decision")
    public ResponseEntity<Object> committeeVendorDecision(
            @RequestParam String tenderId,
            @RequestBody Map<String, Object> body) {
        String vendorId = (String) body.get("vendorId");
        String decision = (String) body.get("decision");
        String remarks = (String) body.get("remarks");
        Integer committeeUserId = (Integer) body.get("committeeUserId");
        log.info("Committee vendor decision tenderId={} vendorId={} decision={} by={}",
                tenderId, vendorId, decision, committeeUserId);
        TenderEvaluationStatusDto status = approvalService.committeeVendorDecision(
                tenderId, vendorId, decision, remarks, committeeUserId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }

    @GetMapping("/committee/vendor-votes")
    public ResponseEntity<Object> getVendorVoteGrid(
            @RequestParam String tenderId,
            @RequestParam String phase) {
        log.info("Get vendor vote grid tenderId={} phase={}", tenderId, phase);
        Map<String, List<CommitteeVendorVoteDto>> grid =
                approvalService.getVendorVoteGrid(tenderId, phase);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(grid), HttpStatus.OK);
    }

    @PostMapping("/committee/chairman-vendor-resolve")
    public ResponseEntity<Object> chairmanVendorResolve(
            @RequestParam String tenderId,
            @RequestBody Map<String, Object> body) {
        String vendorId = (String) body.get("vendorId");
        String decision = (String) body.get("decision");
        String remarks = (String) body.get("remarks");
        Integer chairmanUserId = (Integer) body.get("chairmanUserId");
        log.info("Chairman vendor resolve tenderId={} vendorId={} decision={} by={}",
                tenderId, vendorId, decision, chairmanUserId);
        TenderEvaluationStatusDto status = approvalService.chairmanVendorResolve(
                tenderId, vendorId, decision, remarks, chairmanUserId);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(status), HttpStatus.OK);
    }
```

Add import: `import com.astro.dto.workflow.CommitteeVendorVoteDto;`
Add import: `import java.util.Map;` and `import java.util.List;`

- [ ] **Step 2: Commit**

```
git add Backend-prod/src/main/java/com/astro/controller/ProcurementModuleController/TenderEvaluationController.java
git commit -m "feat: add committee vendor-decision, vendor-votes, chairman-vendor-resolve endpoints"
```

---

### Task 5: Modify Initiation — Pre-create Vendor Decision Rows

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java` (lines 212-229)

- [ ] **Step 1: Add vendor decision row pre-creation after STEC vote row creation**

After the existing block at line 229 (the closing brace of the STEC member vote row creation), add:

```java
        // Pre-create per-vendor committee decision rows for above-10L DOUBLE_BID
        if (("ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) || "ABOVE_50_LAKH_UPTO_1_CRORE".equals(amtCat))
                && "DOUBLE_BID".equalsIgnoreCase(bidType)) {
            String stecType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) ? "STEC_I" : "STEC_II";
            List<TechnoFinancialCommittee> members = committeeRepository.findByCommitteeTypeAndIsActiveTrue(stecType)
                    .stream().filter(m -> !"CHAIRMAN".equalsIgnoreCase(m.getRole()))
                    .collect(Collectors.toList());
            List<VendorQuotationAgainstTender> vendors =
                    quotationRepository.findByTenderIdAndIsLatestTrue(tenderId);

            for (TechnoFinancialCommittee member : members) {
                for (VendorQuotationAgainstTender vendor : vendors) {
                    TenderCommitteeVendorDecision row = new TenderCommitteeVendorDecision();
                    row.setTenderId(tenderId);
                    row.setVendorId(vendor.getVendorId());
                    row.setCommitteeUserId(member.getUserId());
                    row.setMemberName(member.getMemberName());
                    row.setPhase("TECHNICAL");
                    row.setCreatedDate(LocalDateTime.now());
                    committeeVendorDecisionRepository.save(row);
                }
            }
        }
```

- [ ] **Step 2: Commit**

```
git add Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java
git commit -m "feat: pre-create committee vendor decision rows on above-10L double bid initiation"
```

---

### Task 6: Modify Director Approve — Financial Phase Vendor Decision Reset

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java` (directorApprove method, ~lines 613-642)

- [ ] **Step 1: Add vendor decision reset + financial phase row creation**

In the `directorApprove` method, inside the `APPROVED + double-bid + not financial phase` block (~line 613-642), after the existing committee vote reset (which clears vote/voteRemarks/votedDate on `TenderCommitteeDecision` rows), add:

```java
                // Reset per-vendor committee decisions for financial phase
                committeeVendorDecisionRepository.deleteByTenderIdAndPhase(tenderId, "FINANCIAL");

                // Pre-create FINANCIAL phase rows for technically-approved vendors
                List<TenderCommitteeDecision> committeeMembers =
                        committeeDecisionRepository.findByTenderId(tenderId).stream()
                        .filter(d -> d.getCommitteeMemberName() != null
                                && d.getCommitteeUserId() != null)
                        .collect(Collectors.toList());

                List<VendorQuotationAgainstTender> approvedVendors = quotations.stream()
                        .filter(q -> "APPROVED".equalsIgnoreCase(q.getTechnicalStatus())
                                && "ACCEPTED".equalsIgnoreCase(q.getIndentorStatus()))
                        .collect(Collectors.toList());

                for (TenderCommitteeDecision member : committeeMembers) {
                    for (VendorQuotationAgainstTender vendor : approvedVendors) {
                        TenderCommitteeVendorDecision row = new TenderCommitteeVendorDecision();
                        row.setTenderId(tenderId);
                        row.setVendorId(vendor.getVendorId());
                        row.setCommitteeUserId(member.getCommitteeUserId());
                        row.setMemberName(member.getCommitteeMemberName());
                        row.setPhase("FINANCIAL");
                        row.setCreatedDate(LocalDateTime.now());
                        committeeVendorDecisionRepository.save(row);
                    }
                }
```

- [ ] **Step 2: Commit**

```
git add Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java
git commit -m "feat: reset and pre-create committee vendor decision rows on director financial phase unlock"
```

---

### Task 7: Extend buildStatusDto — Populate committeeVendorVotes

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java` (~line 1630, inside buildStatusDto)

- [ ] **Step 1: Add committeeVendorVotes population**

After the existing committee data section (after expertName extraction, ~line 1633), add:

```java
            // Per-vendor committee votes (above-10L double bid)
            if ("DOUBLE_BID".equalsIgnoreCase(eval.getBidType())) {
                String phase = Boolean.TRUE.equals(eval.getFinancialBidPhase()) ? "FINANCIAL" : "TECHNICAL";
                List<TenderCommitteeVendorDecision> vendorVotes =
                        committeeVendorDecisionRepository.findByTenderIdAndPhase(tenderId, phase);
                Map<String, List<CommitteeVendorVoteDto>> voteMap = vendorVotes.stream()
                        .map(v -> {
                            CommitteeVendorVoteDto vDto = new CommitteeVendorVoteDto();
                            vDto.setCommitteeUserId(v.getCommitteeUserId());
                            vDto.setMemberName(v.getMemberName());
                            vDto.setDecision(v.getDecision());
                            vDto.setRemarks(v.getRemarks());
                            vDto.setDecisionDate(v.getDecisionDate());
                            return Map.entry(v.getVendorId(), vDto);
                        })
                        .collect(Collectors.groupingBy(
                                Map.Entry::getKey,
                                Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
                dto.setCommitteeVendorVotes(voteMap);
            }
```

- [ ] **Step 2: Commit**

```
git add Backend-prod/src/main/java/com/astro/service/impl/TenderEvaluationApprovalServiceImpl.java
git commit -m "feat: populate committeeVendorVotes in buildStatusDto for above-10L double bid"
```

---

### Task 8: Frontend — Dual Table Control Flags + Committee Column Definitions

**Files:**
- Modify: `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluator.jsx`

- [ ] **Step 1: Add above-10L committee control flags**

After `showSpoFinActions` (~line 179), add:

```js
// ── Above 10L double bid: committee per-vendor actions ──
const showCommitteeTechActions = isDoubleBidEval && !isFinancialPhase &&
  isAbove10L && isCommitteeMember && isVotingMember &&
  evalStatus?.evaluationStatus === 'PENDING_TECHNICAL';

const showCommitteeFinActions = isDoubleBidEval && isFinancialPhase &&
  isAbove10L && isCommitteeMember && isVotingMember &&
  evalStatus?.evaluationStatus === 'PENDING_FINANCIAL';

const showChairmanTechResolve = isDoubleBidEval && !isFinancialPhase &&
  isAbove10L && isChairman &&
  evalStatus?.evaluationStatus === 'PENDING_TECHNICAL';

const showChairmanFinResolve = isDoubleBidEval && isFinancialPhase &&
  isAbove10L && isChairman &&
  evalStatus?.evaluationStatus === 'PENDING_FINANCIAL';

const showCommitteeVendorActions = showCommitteeTechActions || showCommitteeFinActions;
const showChairmanVendorResolve = showChairmanTechResolve || showChairmanFinResolve;
```

- [ ] **Step 2: Add committee vendor decision handler**

After the existing `handleAssignExpertSubmit` handler (~line 683), add:

```js
// ── Committee per-vendor decision (above 10L double bid) ──
const handleCommitteeVendorDecision = async (vendorId, decision, remarkText) => {
  try {
    await axios.post('/api/tender-evaluation/committee/vendor-decision', {
      vendorId,
      decision,
      remarks: remarkText || '',
      committeeUserId: userId,
    }, { params: { tenderId } });
    message.success(`Vendor ${vendorId} ${decision.toLowerCase()}.`);
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error(e.response?.data?.message || 'Failed to save vendor decision.');
  }
};

// ── Chairman per-vendor resolve (above 10L double bid) ──
const handleChairmanVendorResolve = async (vendorId, decision, remarkText) => {
  try {
    await axios.post('/api/tender-evaluation/committee/chairman-vendor-resolve', {
      vendorId,
      decision,
      remarks: remarkText || '',
      chairmanUserId: userId,
    }, { params: { tenderId } });
    message.success(`Vendor ${vendorId} resolved as ${decision.toLowerCase()}.`);
    await fetchEvalStatus(tenderId);
  } catch (e) {
    message.error(e.response?.data?.message || 'Failed to resolve vendor.');
  }
};
```

- [ ] **Step 3: Add committee tech/financial column definitions**

After the existing `doubleBidFinColumns` definition (~line 1770), add:

```js
// ── Above 10L: Committee member per-vendor columns (tech) ──
const committeeTechColumns = [
  ...vendorInfoColumns,
  {
    title: 'Technical Document',
    dataIndex: 'quotationFileName',
    render: (text) => text
      ? <a href={`/file/download?fileName=${text}&fileType=Tender`} target="_blank" rel="noreferrer">{text}</a>
      : '-',
  },
  {
    title: 'My Vote',
    key: 'myVote',
    render: (_, record) => {
      const votes = evalStatus?.committeeVendorVotes?.[record.vendorId] || [];
      const myVote = votes.find(v => v.committeeUserId === userId);
      if (!myVote?.decision) return <Tag>Pending</Tag>;
      return <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>;
    },
  },
  ...(showCommitteeTechActions ? [{
    title: 'Action',
    key: 'action',
    render: (_, record) => (
      <Space>
        <Popconfirm title="Accept this vendor's technical bid?"
          onConfirm={() => handleCommitteeVendorDecision(record.vendorId, 'ACCEPTED', '')}>
          <Button type="primary" size="small">Accept</Button>
        </Popconfirm>
        <Popconfirm title="Reject this vendor's technical bid?"
          onConfirm={() => handleCommitteeVendorDecision(record.vendorId, 'REJECTED', '')}>
          <Button danger size="small">Reject</Button>
        </Popconfirm>
      </Space>
    ),
  }] : []),
];

// ── Above 10L: Committee member per-vendor columns (financial) ──
const committeeFinColumns = [
  ...vendorInfoColumns,
  {
    title: 'Financial Document',
    dataIndex: 'priceBidFileName',
    render: (text) => text
      ? <a href={`/file/download?fileName=${text}&fileType=Tender`} target="_blank" rel="noreferrer">{text}</a>
      : '-',
  },
  {
    title: 'My Vote',
    key: 'myVote',
    render: (_, record) => {
      const votes = evalStatus?.committeeVendorVotes?.[record.vendorId] || [];
      const myVote = votes.find(v => v.committeeUserId === userId);
      if (!myVote?.decision) return <Tag>Pending</Tag>;
      return <Tag color={myVote.decision === 'ACCEPTED' ? 'green' : 'red'}>{myVote.decision}</Tag>;
    },
  },
  ...(showCommitteeFinActions ? [{
    title: 'Action',
    key: 'action',
    render: (_, record) => (
      <Space>
        <Popconfirm title="Accept this vendor's financial bid?"
          onConfirm={() => handleCommitteeVendorDecision(record.vendorId, 'ACCEPTED', '')}>
          <Button type="primary" size="small">Accept</Button>
        </Popconfirm>
        <Popconfirm title="Reject this vendor's financial bid?"
          onConfirm={() => handleCommitteeVendorDecision(record.vendorId, 'REJECTED', '')}>
          <Button danger size="small">Reject</Button>
        </Popconfirm>
      </Space>
    ),
  }] : []),
];

// ── Above 10L: Chairman per-vendor resolve columns (tech) ──
const chairmanTechColumns = [
  ...vendorInfoColumns,
  {
    title: 'Technical Document',
    dataIndex: 'quotationFileName',
    render: (text) => text
      ? <a href={`/file/download?fileName=${text}&fileType=Tender`} target="_blank" rel="noreferrer">{text}</a>
      : '-',
  },
  {
    title: 'Committee Votes',
    key: 'votes',
    render: (_, record) => {
      const votes = evalStatus?.committeeVendorVotes?.[record.vendorId] || [];
      const accepted = votes.filter(v => v.decision === 'ACCEPTED').length;
      const rejected = votes.filter(v => v.decision === 'REJECTED').length;
      const pending = votes.filter(v => !v.decision).length;
      return (
        <Tooltip title={votes.map(v => `${v.memberName}: ${v.decision || 'Pending'}`).join('\n')}>
          <Space size={4}>
            {accepted > 0 && <Tag color="green">{accepted} Accept</Tag>}
            {rejected > 0 && <Tag color="red">{rejected} Reject</Tag>}
            {pending > 0 && <Tag>{pending} Pending</Tag>}
          </Space>
        </Tooltip>
      );
    },
  },
  {
    title: 'Resolved',
    dataIndex: 'indentorStatus',
    render: (val) => val && val !== 'PENDING'
      ? <Tag color={val === 'ACCEPTED' ? 'green' : 'red'}>{val}</Tag>
      : <Tag>Pending</Tag>,
  },
  ...(showChairmanTechResolve ? [{
    title: 'Resolve',
    key: 'resolve',
    render: (_, record) => record.indentorStatus && record.indentorStatus !== 'PENDING'
      ? <Tag color="blue">Resolved</Tag>
      : (
        <Space>
          <Popconfirm title="Accept this vendor (tech)?"
            onConfirm={() => handleChairmanVendorResolve(record.vendorId, 'ACCEPTED', '')}>
            <Button type="primary" size="small">Accept</Button>
          </Popconfirm>
          <Popconfirm title="Reject this vendor (tech)?"
            onConfirm={() => handleChairmanVendorResolve(record.vendorId, 'REJECTED', '')}>
            <Button danger size="small">Reject</Button>
          </Popconfirm>
        </Space>
      ),
  }] : []),
];

// ── Above 10L: Chairman per-vendor resolve columns (financial) ──
const chairmanFinColumns = [
  ...vendorInfoColumns,
  {
    title: 'Financial Document',
    dataIndex: 'priceBidFileName',
    render: (text) => text
      ? <a href={`/file/download?fileName=${text}&fileType=Tender`} target="_blank" rel="noreferrer">{text}</a>
      : '-',
  },
  {
    title: 'Committee Votes',
    key: 'votes',
    render: (_, record) => {
      const votes = evalStatus?.committeeVendorVotes?.[record.vendorId] || [];
      const accepted = votes.filter(v => v.decision === 'ACCEPTED').length;
      const rejected = votes.filter(v => v.decision === 'REJECTED').length;
      const pending = votes.filter(v => !v.decision).length;
      return (
        <Tooltip title={votes.map(v => `${v.memberName}: ${v.decision || 'Pending'}`).join('\n')}>
          <Space size={4}>
            {accepted > 0 && <Tag color="green">{accepted} Accept</Tag>}
            {rejected > 0 && <Tag color="red">{rejected} Reject</Tag>}
            {pending > 0 && <Tag>{pending} Pending</Tag>}
          </Space>
        </Tooltip>
      );
    },
  },
  {
    title: 'Resolved',
    dataIndex: 'financialIndentorStatus',
    render: (val) => val && val !== 'PENDING'
      ? <Tag color={val === 'ACCEPTED' ? 'green' : 'red'}>{val}</Tag>
      : <Tag>Pending</Tag>,
  },
  ...(showChairmanFinResolve ? [{
    title: 'Resolve',
    key: 'resolve',
    render: (_, record) => record.financialIndentorStatus && record.financialIndentorStatus !== 'PENDING'
      ? <Tag color="blue">Resolved</Tag>
      : (
        <Space>
          <Popconfirm title="Accept this vendor (financial)?"
            onConfirm={() => handleChairmanVendorResolve(record.vendorId, 'ACCEPTED', '')}>
            <Button type="primary" size="small">Accept</Button>
          </Popconfirm>
          <Popconfirm title="Reject this vendor (financial)?"
            onConfirm={() => handleChairmanVendorResolve(record.vendorId, 'REJECTED', '')}>
            <Button danger size="small">Reject</Button>
          </Popconfirm>
        </Space>
      ),
  }] : []),
];
```

- [ ] **Step 4: Commit**

```
git add Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluator.jsx
git commit -m "feat: add committee/chairman per-vendor column definitions and handlers for above-10L double bid"
```

---

### Task 9: Frontend — Dual Table Render for Above-10L

**Files:**
- Modify: `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluator.jsx`

- [ ] **Step 1: Update dual table render block to use committee/chairman columns for above-10L**

Find the existing dual table render block (~line 2385):
```jsx
{isDoubleBidEval && (
```

Replace the table column selection inside the Technical Bid Table Card. Current (line 2399):
```jsx
columns={isSpoRole ? spoTechColumns : doubleBidTechColumns}
```

Replace with:
```jsx
columns={
  isAbove10L && isChairman ? chairmanTechColumns
  : isAbove10L && isCommitteeMember ? committeeTechColumns
  : isSpoRole ? spoTechColumns
  : doubleBidTechColumns
}
```

Similarly for the Financial Bid Table (line 2414):
```jsx
columns={isSpoRole ? spoFinColumns : doubleBidFinColumns}
```

Replace with:
```jsx
columns={
  isAbove10L && isChairman ? chairmanFinColumns
  : isAbove10L && isCommitteeMember ? committeeFinColumns
  : isSpoRole ? spoFinColumns
  : doubleBidFinColumns
}
```

- [ ] **Step 2: Show dual tables for above-10L (remove isBelow10L gate if present)**

The dual tables are already gated on `{isDoubleBidEval && (...)}` which is amount-agnostic. Verify no `isBelow10L` check wraps the dual table block. If the entire vendor table section has an `isBelow10L` or `showActionButtons` gate that hides it for above-10L, add an `|| isAbove10L` condition.

The financial table visibility currently checks `isFinancialPhase`. For above-10L, also show it when `evalStatus?.evaluationStatus === 'PENDING_FINANCIAL'`. This should already be true since `isFinancialPhase` derives from `financialBidPhase`.

- [ ] **Step 3: Show Director panel the dual tables as read-only**

For `isDirector` role at `PENDING_DIRECTOR_APPROVAL`, the dual tables should render with no action columns. The existing column selection logic handles this — director is neither chairman, committee member, SPO, nor indentor, so it falls through to `doubleBidTechColumns`/`doubleBidFinColumns` which have read-only behavior when their `showTechActionButtons`/`showFinActionButtons` flags are false.

Verify this works — no code change needed if fallthrough is correct.

- [ ] **Step 4: Commit**

```
git add Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluator.jsx
git commit -m "feat: render dual tables with committee/chairman columns for above-10L double bid"
```

---

### Task 10: Verify + Manual Testing

- [ ] **Step 1: Build backend**

Run: `cd Backend-prod && mvn clean compile -q`
Expected: BUILD SUCCESS, no compilation errors.

- [ ] **Step 2: Start backend + frontend and test**

Test matrix:
1. Under-10L single bid: verify no behavior change
2. Under-10L double bid: verify dual tables still work
3. Above-10L single bid: verify evaluation-level committee vote still works
4. Above-10L double bid: 
   - Initiate → verify PENDING_TECHNICAL and committee vendor decision rows created
   - Committee member: see tech table with Accept/Reject, cast votes
   - Chairman: see vote grid per vendor, resolve each vendor
   - All resolved → verify status auto-transitions to PENDING_DIRECTOR_APPROVAL
   - Director approves → verify PENDING_FINANCIAL, financial rows created, tech committee votes reset
   - Financial phase: committee votes, chairman resolves → PENDING_DIRECTOR_APPROVAL
   - Director approves → APPROVED

- [ ] **Step 3: Final commit if any fixes needed**

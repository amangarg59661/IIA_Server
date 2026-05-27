# GEM Tender Evaluation — Bulk Save All Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a table-level "Send All Quotations for Evaluation" button that saves all NEW vendor rows at once via a new bulk API endpoint.

**Architecture:** Frontend validates all NEW rows, uploads files in parallel to existing `/file/upload`, then POSTs all quotation data to a new `/api/vendor-quotation/bulk` endpoint. Backend processes the list in a single `@Transactional` method, reusing existing `saveQuotation` logic per item.

**Tech Stack:** React (Ant Design), Spring Boot, JPA/Hibernate, Lombok

---

## File Map

| Action | File | Responsibility |
|--------|------|----------------|
| Create | `Backend-prod/src/main/java/com/astro/dto/workflow/BulkVendorQuotationRequest.java` | DTO wrapping tenderId + list of quotation DTOs |
| Modify | `Backend-prod/src/main/java/com/astro/service/VendorQuotationAgainstTenderService.java` | Add `saveBulkQuotations` method signature |
| Modify | `Backend-prod/src/main/java/com/astro/service/impl/VendorQuotationAgainstTenderServiceImpl.java` | Implement `saveBulkQuotations` with `@Transactional` |
| Modify | `Backend-prod/src/main/java/com/astro/controller/VendorQuotationController.java` | Add `POST /bulk` endpoint |
| Modify | `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluatorGem.jsx` | Add `handleSubmitAll` + "Send All" button |

---

### Task 1: Create BulkVendorQuotationRequest DTO

**Files:**
- Create: `Backend-prod/src/main/java/com/astro/dto/workflow/BulkVendorQuotationRequest.java`

- [ ] **Step 1: Create the DTO class**

```java
package com.astro.dto.workflow;

import lombok.Data;
import java.util.List;

@Data
public class BulkVendorQuotationRequest {
    private String tenderId;
    private List<VendorQuotationAgainstTenderDto> quotations;
}
```

- [ ] **Step 2: Verify compilation**

Run: `mvn compile -pl Backend-prod -q` from project root.
Expected: BUILD SUCCESS, no errors.

- [ ] **Step 3: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/dto/workflow/BulkVendorQuotationRequest.java
git commit -m "feat: add BulkVendorQuotationRequest DTO for bulk quotation save"
```

---

### Task 2: Add bulk save method to service interface

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/VendorQuotationAgainstTenderService.java:15`

- [ ] **Step 1: Add method signature**

Add this line after the existing `saveQuotation` method (line 15):

```java
public List<VendorQuotationAgainstTenderDto> saveBulkQuotations(BulkVendorQuotationRequest request);
```

Also add the import at the top of the file (after existing imports):

```java
import com.astro.dto.workflow.BulkVendorQuotationRequest;
```

- [ ] **Step 2: Verify compilation**

Run: `mvn compile -pl Backend-prod -q`
Expected: FAIL — service impl doesn't implement the new method yet. This is expected.

- [ ] **Step 3: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/service/VendorQuotationAgainstTenderService.java
git commit -m "feat: add saveBulkQuotations to VendorQuotationAgainstTenderService interface"
```

---

### Task 3: Implement bulk save in service impl

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/service/impl/VendorQuotationAgainstTenderServiceImpl.java`

- [ ] **Step 1: Add the import**

Add at top of file with other imports:

```java
import com.astro.dto.workflow.BulkVendorQuotationRequest;
```

- [ ] **Step 2: Add the bulk save method**

Add this method after the existing `saveQuotation` method (after line 163):

```java
@Override
@Transactional
public List<VendorQuotationAgainstTenderDto> saveBulkQuotations(BulkVendorQuotationRequest request) {
    if (request.getQuotations() == null || request.getQuotations().isEmpty()) {
        throw new BusinessException(new ErrorDetails("BULK_EMPTY", "No quotations provided in bulk request"));
    }

    List<VendorQuotationAgainstTenderDto> results = new ArrayList<>();
    for (VendorQuotationAgainstTenderDto dto : request.getQuotations()) {
        dto.setTenderId(request.getTenderId());
        VendorQuotationAgainstTenderDto saved = saveQuotation(dto);
        results.add(saved);
    }
    return results;
}
```

This reuses `saveQuotation` for each item. The `@Transactional` annotation ensures all saves succeed or all roll back.

- [ ] **Step 3: Verify compilation**

Run: `mvn compile -pl Backend-prod -q`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/service/impl/VendorQuotationAgainstTenderServiceImpl.java
git commit -m "feat: implement @Transactional saveBulkQuotations in service impl"
```

---

### Task 4: Add bulk endpoint to controller

**Files:**
- Modify: `Backend-prod/src/main/java/com/astro/controller/VendorQuotationController.java`

- [ ] **Step 1: Add the import**

Add at top with other imports:

```java
import com.astro.dto.workflow.BulkVendorQuotationRequest;
```

- [ ] **Step 2: Add the bulk POST endpoint**

Add this method after the existing `createVendorQuotation` method (after line 30):

```java
@PostMapping("/bulk")
public ResponseEntity<Object> createBulkVendorQuotations(@RequestBody BulkVendorQuotationRequest request) {
    List<VendorQuotationAgainstTenderDto> results = vqService.saveBulkQuotations(request);
    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(results), HttpStatus.OK);
}
```

- [ ] **Step 3: Verify compilation**

Run: `mvn compile -pl Backend-prod -q`
Expected: BUILD SUCCESS.

- [ ] **Step 4: Commit**

```bash
git add Backend-prod/src/main/java/com/astro/controller/VendorQuotationController.java
git commit -m "feat: add POST /api/vendor-quotation/bulk endpoint"
```

---

### Task 5: Add handleSubmitAll and "Send All" button to frontend

**Files:**
- Modify: `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluatorGem.jsx`

- [ ] **Step 1: Add `savingAll` state variable**

Add after the existing `loadingRows` state (line 27):

```javascript
const [savingAll, setSavingAll] = useState(false);
```

- [ ] **Step 2: Add `handleSubmitAll` function**

Add after the `handleSubmit` function (after line 466):

```javascript
const handleSubmitAll = async () => {
  const newRows = vendorList.filter((v) => v.status === "NEW");

  if (newRows.length === 0) {
    message.warning("No new vendors to submit");
    return;
  }

  const incomplete = newRows.filter(
    (v) => !v.technicalBidFile || !v.priceBidFile
  );
  if (incomplete.length > 0) {
    message.error(
      `Please upload both files for: ${incomplete
        .map((v) => v.vendorName)
        .join(", ")}`
    );
    return;
  }

  setSavingAll(true);
  try {
    const upload = async (fileObj) => {
      const fd = new FormData();
      fd.append("file", fileObj.file);
      const resp = await axios.post("/file/upload?fileType=Tender", fd, {
        headers: {
          "Content-Type": "multipart/form-data",
          Accept: "application/json",
        },
      });
      return resp.data.responseData.fileName;
    };

    const uploadResults = await Promise.all(
      newRows.map(async (row) => {
        const techFileName = await upload(row.technicalBidFile);
        const priceFileName = await upload(row.priceBidFile);
        return {
          vendorName: row.vendorName,
          quotationFileName: techFileName,
          priceBidFileName: priceFileName,
          fileType: "Tender",
          type: "GEM",
        };
      })
    );

    const response = await axios.post("/api/vendor-quotation/bulk", {
      tenderId: formData.tenderId,
      quotations: uploadResults,
    });

    if (response.data.responseStatus.statusCode === 0) {
      message.success(
        `All ${uploadResults.length} quotations submitted successfully`
      );
      await fetchVendors(formData.tenderId);
    } else {
      throw new Error("Bulk save failed");
    }
  } catch (error) {
    console.error("Bulk submission error:", error);
    message.error("An error occurred while submitting quotations");
  } finally {
    setSavingAll(false);
  }
};
```

- [ ] **Step 3: Add "Send All Quotations for Evaluation" button below the table**

In the JSX, find the `<Table>` component (line 635-640). Replace this block:

```jsx
          <Table
            dataSource={vendorList}
            columns={columns}
            rowKey="vendorName"
            pagination={false}
          />
```

With:

```jsx
          <Table
            dataSource={vendorList}
            columns={columns}
            rowKey="vendorName"
            pagination={false}
          />

          {vendorList.some((v) => v.status === "NEW") && (
            <div style={{ display: "flex", justifyContent: "flex-end", marginTop: 16 }}>
              <Button
                type="primary"
                onClick={handleSubmitAll}
                loading={savingAll}
                disabled={isUploading}
              >
                Send All Quotations for Evaluation
              </Button>
            </div>
          )}
```

- [ ] **Step 4: Verify frontend compiles**

Run: `npm start` (or `npm run build`) from `Frontend-test/` directory.
Expected: No compilation errors.

- [ ] **Step 5: Commit**

```bash
git add Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluatorGem.jsx
git commit -m "feat: add Send All Quotations for Evaluation button with bulk save"
```

---

### Task 6: Integration verification

- [ ] **Step 1: Start backend and frontend**

Start Spring Boot backend and React frontend.

- [ ] **Step 2: Navigate to `/procurement/tender/gem`**

Select a tender ID from dropdown.

- [ ] **Step 3: Test line-level save still works**

Add one vendor, upload both files, click line-level "Send Quotation for Evaluation". Verify it saves and table refreshes.

- [ ] **Step 4: Test bulk save — happy path**

Add 2-3 vendors, upload both files for each, click "Send All Quotations for Evaluation". Verify:
- All vendors saved
- Success message shows count
- Table refreshes and NEW rows become SUBMITTED
- Button disappears (no more NEW rows)

- [ ] **Step 5: Test bulk save — validation block**

Add 2 vendors, upload files for only one. Click "Send All". Verify:
- Error message names the incomplete vendor
- No files uploaded, no API call made

- [ ] **Step 6: Test button visibility**

Verify "Send All" button only shows when NEW rows exist. After all are submitted, button should disappear.

- [ ] **Step 7: Final commit if any fixes needed**

```bash
git add -A
git commit -m "fix: address integration test findings for bulk save"
```

# GEM Tender Evaluation — Bulk "Save All" Feature

## Summary

Add a table-level "Send All Quotations for Evaluation" button to GEM Tender Evaluation page (`TenderEvaluatorGem.jsx`) that saves all NEW vendor rows at once. Existing line-level save button remains.

## Scope

- **Frontend**: `TenderEvaluatorGem.jsx` — new button, new `handleSubmitAll` function
- **Backend**: New bulk endpoint `POST /api/vendor-quotation/bulk`, new DTO, new service method

## Frontend

### Button
- Text: "Send All Quotations for Evaluation"
- Placement: Below the vendor table, right-aligned
- Visibility: Only when at least one NEW row exists
- Disabled while `isUploading` is true

### Flow
1. Validate every NEW row has `technicalBidFile` AND `priceBidFile`
   - If any missing: `message.error` listing which vendor(s) are incomplete. Stop.
2. Set loading state
3. Upload all files in parallel via `Promise.all` to existing `/file/upload?fileType=Tender`
4. Build quotation array with returned fileNames
5. POST to `/api/vendor-quotation/bulk` with `{ tenderId, quotations: [...] }`
6. On success: `message.success("All quotations submitted successfully")`, call `fetchVendors(tenderId)`
7. On failure: `message.error` with backend error message
8. Clear loading state

### Existing line-level button
No changes. Both buttons available simultaneously.

## Backend

### New DTO: `BulkVendorQuotationRequest`
```java
public class BulkVendorQuotationRequest {
    private String tenderId;
    private List<VendorQuotationItem> quotations;
}

public class VendorQuotationItem {
    private String vendorName;
    private String quotationFileName;
    private String priceBidFileName;
    private String fileType;  // "Tender"
    private String type;      // "GEM"
}
```

### Endpoint
```
POST /api/vendor-quotation/bulk

Request:
{
  "tenderId": "T123",
  "quotations": [
    {
      "vendorName": "Vendor A",
      "quotationFileName": "tech_abc.pdf",
      "priceBidFileName": "price_abc.pdf",
      "fileType": "Tender",
      "type": "GEM"
    }
  ]
}

Response: { responseStatus: { statusCode: 0, statusMessage: "..." } }
```

### Service
- Method: `saveBulkVendorQuotations(BulkVendorQuotationRequest request)`
- Annotated `@Transactional` — all rows saved or none
- Loops through quotations list, creates and saves each entity
- Reuses existing save logic from single quotation flow

## Error Handling

| Scenario | Behavior |
|---|---|
| Row missing file (frontend) | Block before any upload, show which vendor |
| File upload fails | Stop, show error, no bulk POST |
| Bulk POST fails | Backend rolls back all saves, show error |
| Orphaned files on POST failure | Acceptable (same as current single-save behavior) |

## Files to Modify

### Frontend
- `Frontend-test/src/pages/dashboard/tenderRequest/TenderEvaluatorGem.jsx`

### Backend (to be located)
- VendorQuotation controller — add bulk endpoint
- VendorQuotation service — add bulk save method
- New DTO class for bulk request

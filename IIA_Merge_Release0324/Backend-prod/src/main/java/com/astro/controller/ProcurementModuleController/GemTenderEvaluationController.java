package com.astro.controller.ProcurementModuleController;

import com.astro.service.GemTenderEvaluationService;
import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * GeM / Open / Global Tender Evaluation.
 * Purchase Personnel manually adds vendor entries and uploads documents before
 * sending them into the main Tender Evaluation flow.
 */
@RestController
@RequestMapping("/api/gem-tender-evaluation")
public class GemTenderEvaluationController {

    @Autowired
    private GemTenderEvaluationService gemService;

    /**
     * GET /api/gem-tender-evaluation/{tenderId}
     * Returns all GeM vendor entries for a tender.
     */
    @GetMapping("/{tenderId}")
    public ResponseEntity<Object> getByTender(@PathVariable String tenderId) {
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(gemService.getByTenderId(tenderId)),
                HttpStatus.OK);
    }

    /**
     * POST /api/gem-tender-evaluation/{tenderId}/add-vendor
     * Body: { "vendorName":"ABC Ltd", "technicalDocFileName":"tech.pdf",
     *         "financialDocFileName":"fin.pdf", "addedByUserId":5 }
     */
    @PostMapping("/{tenderId}/add-vendor")
    public ResponseEntity<Object> addVendor(
            @PathVariable String tenderId,
            @RequestBody Map<String, Object> body) {
        String vendorName          = (String)  body.get("vendorName");
        String technicalDocFile    = (String)  body.get("technicalDocFileName");
        String financialDocFile    = (String)  body.get("financialDocFileName");
        Integer addedByUserId      = (Integer) body.get("addedByUserId");
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(
                        gemService.addVendor(tenderId, vendorName, technicalDocFile, financialDocFile, addedByUserId)),
                HttpStatus.OK);
    }

    /**
     * PUT /api/gem-tender-evaluation/upload-technical/{id}
     * Body: { "technicalDocFileName":"new_tech.pdf", "userId":5 }
     */
    @PutMapping("/upload-technical/{id}")
    public ResponseEntity<Object> uploadTechnical(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String fileName = (String)  body.get("technicalDocFileName");
        Integer userId  = (Integer) body.get("userId");
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(gemService.uploadTechnicalDoc(id, fileName, userId)),
                HttpStatus.OK);
    }

    /**
     * PUT /api/gem-tender-evaluation/upload-financial/{id}
     * Body: { "financialDocFileName":"new_fin.pdf", "userId":5 }
     */
    @PutMapping("/upload-financial/{id}")
    public ResponseEntity<Object> uploadFinancial(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String fileName = (String)  body.get("financialDocFileName");
        Integer userId  = (Integer) body.get("userId");
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(gemService.uploadFinancialDoc(id, fileName, userId)),
                HttpStatus.OK);
    }

    /**
     * POST /api/gem-tender-evaluation/{tenderId}/send-for-evaluation
     * Promotes all pending GeM entries into VendorQuotationAgainstTender.
     * Body: { "actionByUserId": 5 }
     */
    @PostMapping("/{tenderId}/send-for-evaluation")
    public ResponseEntity<Object> sendForEvaluation(
            @PathVariable String tenderId,
            @RequestBody Map<String, Object> body) {
        Integer userId = (Integer) body.get("actionByUserId");
        return new ResponseEntity<>(
                ResponseBuilder.getSuccessResponse(gemService.sendForEvaluation(tenderId, userId)),
                HttpStatus.OK);
    }
}

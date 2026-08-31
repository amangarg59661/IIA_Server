package com.astro.controller.InventoryModule;

import com.astro.dto.workflow.InventoryModule.grn.GrnReportRowDto;
import com.astro.service.InventoryModule.GrnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.astro.util.ResponseBuilder;
import java.time.LocalDateTime;
import java.util.List;

// NOTE: adjust the package above to wherever your other GRN controllers live —
// this file wasn't in what you uploaded, so I couldn't match it exactly.
@RestController
@RequestMapping("/api/grn/report")
public class GrnReportController {

    @Autowired
    private GrnService grnService;

    // GET /api/grn/report
    // GET /api/grn/report?fromDate=2026-08-01T00:00:00&toDate=2026-08-31T23:59:59
    // GET /api/grn/report?category=Consumables&status=APPROVED
    @GetMapping
    public ResponseEntity<Object> getGrnReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {

        List<GrnReportRowDto> report = grnService.getGrnReport(fromDate, toDate, category, status);
        return new ResponseEntity<Object>(ResponseBuilder.getSuccessResponse(report), HttpStatus.OK);
    }
}

package com.astro.controller.InventoryModule;

import com.astro.dto.workflow.InventoryModule.serviceInspection.SaveServiceInspectionDto;
import com.astro.dto.workflow.InventoryModule.serviceInspection.ServiceInspectionDto;
import com.astro.service.InventoryModule.ServiceInspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Save + read only. Approve / reject / change-request all go through the existing
 * generic performTransitionAction() endpoint on the workflow controller — Service
 * Inspection doesn't need (and deliberately doesn't get) its own approval endpoints.
 */
@RestController
@RequestMapping("/api/service-inspection")
public class ServiceInspectionController {

    @Autowired
    private ServiceInspectionService serviceInspectionService;

    @PostMapping("/save")
    public ResponseEntity<Object> saveServiceInspection(@RequestBody SaveServiceInspectionDto req) {
        return ResponseEntity.ok(serviceInspectionService.saveServiceInspection(req));
    }

    @GetMapping("/details")
    public ResponseEntity<Object> getServiceInspectionDtls(@RequestParam String inspectionProcessId) {
        return ResponseEntity.ok(serviceInspectionService.getServiceInspectionDtls(inspectionProcessId));
    }

    @GetMapping("/pendingSoIds")
    public ResponseEntity<Object> getPendingServiceInspectionSoIds() {
        return ResponseEntity.ok(serviceInspectionService.getPendingServiceInspectionSoIds());
    }

    @GetMapping("/bySoId")
    public ResponseEntity<Object> getServiceInspectionsBySoId(@RequestParam String soId) {
        return ResponseEntity.ok(serviceInspectionService.getServiceInspectionsBySoId(soId));
    }

    @GetMapping("/approvedIds")
    public ResponseEntity<Object> getApprovedServiceInspectionIds() {
        return ResponseEntity.ok(serviceInspectionService.getApprovedServiceInspectionIds());
    }

    @GetMapping("/paymentVoucherData")
    public ResponseEntity<Object> getPaymentVoucherDtoByInspectionId(@RequestParam String inspectionProcessId) {
        return ResponseEntity.ok(serviceInspectionService.getPaymentVoucherDtoByInspectionId(inspectionProcessId));
    }
}

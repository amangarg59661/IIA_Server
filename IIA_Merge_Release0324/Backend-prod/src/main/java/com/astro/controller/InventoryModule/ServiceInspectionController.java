package com.astro.controller.InventoryModule;

import com.astro.dto.workflow.InventoryModule.serviceInspection.SaveServiceInspectionDto;
import com.astro.dto.workflow.InventoryModule.serviceInspection.ServiceInspectionDto;
import com.astro.dto.workflow.InventoryModule.serviceInspection.SaveServiceInspectionResponseDto;
import com.astro.service.InventoryModule.ServiceInspectionService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

import com.astro.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    // AFTER line 27, insert:

    @PostMapping("/draft")
    public ResponseEntity<Object> saveInspectionDraft(@RequestBody SaveServiceInspectionDto req) {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.saveInspectionDraft(req)), HttpStatus.OK);
    }

    @PutMapping("/draft")
    public ResponseEntity<Object> updateInspectionDraft(@RequestParam String inspectionProcessId,
                                                          @RequestBody SaveServiceInspectionDto req) {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.updateInspectionDraft(inspectionProcessId, req)), HttpStatus.OK);
    }

    @PostMapping("/draft/submit")
    public ResponseEntity<Object> submitInspectionDraft(@RequestParam String inspectionProcessId,
                                                          @RequestBody SaveServiceInspectionDto req) {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.submitInspectionDraft(inspectionProcessId, req)), HttpStatus.OK);
    }

    @GetMapping("/drafts")
    public ResponseEntity<Object> getUserInspectionDrafts(@RequestParam Integer userId) {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.getUserInspectionDrafts(userId)), HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateServiceInspection(@RequestParam String inspectionProcessId,
                                                            @RequestBody SaveServiceInspectionDto req) {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.updateServiceInspection(inspectionProcessId, req)), HttpStatus.OK);
    }

    @GetMapping("/eligibleSoIds")
    public ResponseEntity<Object> getEligibleSoIdsForInspection() {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.getEligibleSoIdsForInspection()), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Object> saveServiceInspection(@RequestBody SaveServiceInspectionDto req) {
        SaveServiceInspectionResponseDto request = serviceInspectionService.saveServiceInspection(req);
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                request), HttpStatus.OK);
    }

    @GetMapping("/details")
    public ResponseEntity<Object> getServiceInspectionDtls(@RequestParam String inspectionProcessId) {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.getServiceInspectionDtls(inspectionProcessId)), HttpStatus.OK);
    }

    @GetMapping("/pendingSoIds")
    public ResponseEntity<Object> getPendingServiceInspectionSoIds() {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.getPendingServiceInspectionSoIds()), HttpStatus.OK);
    }

    @GetMapping("/bySoId")
    public ResponseEntity<Object> getServiceInspectionsBySoId(@RequestParam String soId) {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.getServiceInspectionsBySoId(soId)), HttpStatus.OK);
    }

    @GetMapping("/approvedIds")
    public ResponseEntity<Object> getApprovedServiceInspectionIds() {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.getApprovedServiceInspectionIds()), HttpStatus.OK);
    }

     @GetMapping("/approvedSoIds")
public ResponseEntity<Object> getApprovedSoIdsForInspection() {
    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
            serviceInspectionService.getApprovedSoIdsForInspection()), HttpStatus.OK);
}

    // @GetMapping("/byApprovedSoId")
    // public ResponseEntity<Object> getApprovedInspectionIdsBySoId(@RequestParam String soId) {
    //     return ResponseEntity.ok(serviceInspectionService.getApprovedInspectionIdsBySoId(soId));
    // }
@GetMapping("/byApprovedSoId")
public ResponseEntity<Object> getApprovedInspectionIdsBySoId(@RequestParam String soId) {
    return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
            serviceInspectionService.getApprovedInspectionIdsBySoId(soId)), HttpStatus.OK);
}
    @GetMapping("/paymentVoucherData")
    public ResponseEntity<Object> getPaymentVoucherDtoByInspectionId(@RequestParam String inspectionProcessId) {
        return new ResponseEntity<>(ResponseBuilder.getSuccessResponse(
                serviceInspectionService.getPaymentVoucherDtoByInspectionId(inspectionProcessId)), HttpStatus.OK);
    }
}

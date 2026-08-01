package com.astro.service.InventoryModule;

import com.astro.dto.workflow.InventoryModule.serviceInspection.SaveServiceInspectionDto;
import com.astro.dto.workflow.InventoryModule.serviceInspection.ServiceInspectionDto;

import java.util.List;

public interface ServiceInspectionService {

    // Persists master + material lines, then kicks off the generic
    // WorkflowTransition-based approval chain. Returns the new inspectionProcessId.
    String saveServiceInspection(SaveServiceInspectionDto req);

    ServiceInspectionDto getServiceInspectionDtls(String inspectionProcessId);

    // Approved SOs that don't currently have an inspection cycle in flight —
    // i.e. eligible to start a new cycle (first inspection, next AMC cycle, or reinspection after rejection).
    List<String> getPendingServiceInspectionSoIds();

    List<ServiceInspectionDto> getServiceInspectionsBySoId(String soId);

    // Terminal-approved inspection ids — mirrors WorkflowTransitionRepository.findApprovedSoIds(),
    // just for 'Service Inspection Workflow' instead of 'SO Workflow'.
    List<String> getApprovedServiceInspectionIds();

    // Payment voucher built from THIS inspection's accepted qty/value — not the SO's ordered qty.
    com.astro.dto.workflow.InventoryModule.paymentVoucherDto getPaymentVoucherDtoByInspectionId(String inspectionProcessId);
}

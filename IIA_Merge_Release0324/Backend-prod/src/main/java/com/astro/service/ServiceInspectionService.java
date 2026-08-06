package com.astro.service.InventoryModule;

import com.astro.dto.workflow.InventoryModule.serviceInspection.SaveServiceInspectionDto;
import com.astro.dto.workflow.InventoryModule.serviceInspection.ServiceInspectionDto;
import com.astro.dto.workflow.InventoryModule.paymentVoucherDto;
import com.astro.dto.workflow.InventoryModule.EligibleSoDto;
import com.astro.dto.workflow.InventoryModule.SoInspectionInfoDto;

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
     paymentVoucherDto getPaymentVoucherDtoByInspectionId(String inspectionProcessId);

    String saveInspectionDraft(SaveServiceInspectionDto req);
String updateInspectionDraft(String inspectionProcessId, SaveServiceInspectionDto req);
String submitInspectionDraft(String inspectionProcessId, SaveServiceInspectionDto req);
List<ServiceInspectionDto> getUserInspectionDrafts(Integer userId);
String updateServiceInspection(String inspectionProcessId, SaveServiceInspectionDto req);
List<EligibleSoDto> getEligibleSoIdsForInspection();
List<SoInspectionInfoDto> getApprovedSoIdsForInspection();
List<String> getApprovedInspectionIdsBySoId(String soId);
}

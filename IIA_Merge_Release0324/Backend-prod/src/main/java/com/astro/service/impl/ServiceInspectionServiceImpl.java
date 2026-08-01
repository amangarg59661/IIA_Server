package com.astro.service.impl.InventoryModule;

import com.astro.dto.workflow.InventoryModule.paymentVoucherDto;
import com.astro.dto.workflow.InventoryModule.paymentVoucherMaterials;
import com.astro.dto.workflow.InventoryModule.serviceInspection.SaveServiceInspectionDto;
import com.astro.dto.workflow.InventoryModule.serviceInspection.ServiceInspectionDto;
import com.astro.dto.workflow.InventoryModule.serviceInspection.ServiceInspectionMaterialLineDto;
import com.astro.entity.InventoryModule.ServiceInspectionMaster;
import com.astro.entity.InventoryModule.ServiceInspectionMaterialDtl;
import com.astro.entity.PaymentVoucher;
import com.astro.entity.ProcurementModule.ServiceOrder;
import com.astro.repository.InventoryModule.PaymentVoucherReposiotry;
import com.astro.repository.InventoryModule.ServiceInspectionMaterialDtlRepository;
import com.astro.repository.InventoryModule.ServiceInspectionRepository;
import com.astro.repository.ProcurementModule.ServiceOrderRepository.ServiceOrderRepository;
import com.astro.repository.WorkflowTransitionRepository;
import com.astro.service.InventoryModule.ServiceInspectionService;
import com.astro.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceInspectionServiceImpl implements ServiceInspectionService {

    @Autowired
    private ServiceInspectionRepository serviceInspectionRepository;

    @Autowired
    private ServiceInspectionMaterialDtlRepository serviceInspectionMaterialDtlRepository;

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private PaymentVoucherReposiotry paymentVoucherReposiotry;

    // Workflow name registered in WORKFLOW_MASTER (create via admin frontend) —
    // must match exactly what buildConditionsForWorkflow()/updateRequestEntityOnRejection() key off.
    private static final String WORKFLOW_NAME = "Service Inspection Workflow";

    @Override
    public String saveServiceInspection(SaveServiceInspectionDto req) {
        if (req.getSoId() == null || req.getSoId().isEmpty()) {
            throw new RuntimeException("soId is required to create a Service Inspection.");
        }

        ServiceOrder so = serviceOrderRepository.findById(req.getSoId())
                .orElseThrow(() -> new RuntimeException("Service Order not found: " + req.getSoId()));

        // Determine next cycle for this SO. First inspection => new root id + cycle 1.
        // Every subsequent call (recurring AMC cycle, or a fresh cycle after a rejection) => same root, cycle+1.
        List<ServiceInspectionMaster> existing = serviceInspectionRepository.findBySoIdOrderBySubProcessIdDesc(req.getSoId());

        String rootId;
        int nextSubProcessId;
        if (existing.isEmpty()) {
            // TODO: swap for whatever sequence/ID-generator utility GprnServiceImpl uses for
            // its own processNo — this count-based placeholder is NOT safe under concurrent saves.
            long nextSeq = serviceInspectionRepository.count() + 1;
            rootId = "SI" + String.format("%04d", nextSeq);
            nextSubProcessId = 1;
        } else {
            String previousFullId = existing.get(0).getInspectionProcessId();
            rootId = previousFullId.substring(0, previousFullId.indexOf("/"));
            nextSubProcessId = existing.get(0).getSubProcessId() + 1;
        }
        String inspectionProcessId = rootId + "/" + nextSubProcessId;

        ServiceInspectionMaster master = new ServiceInspectionMaster();
        master.setInspectionProcessId(inspectionProcessId);
        master.setSoId(req.getSoId());
        master.setSubProcessId(nextSubProcessId);
        master.setVendorName(req.getVendorName() != null ? req.getVendorName() : so.getVendorName());
        master.setProjectName(req.getProjectName());
        master.setCurrentStatus("AWAITING APPROVAL");
        master.setInspectedBy(req.getInspectedBy());
        master.setInspectionDate(new Date());
        master.setRemarks(req.getRemarks());
        master.setSupportingDocBase64(req.getSupportingDocBase64());
        master.setCreatedDate(new Date());
        serviceInspectionRepository.save(master);

        List<ServiceInspectionMaterialDtl> lines = req.getMaterials().stream().map(line -> {
            ServiceInspectionMaterialDtl d = new ServiceInspectionMaterialDtl();
            d.setInspectionProcessId(inspectionProcessId);
            d.setMaterialCode(line.getMaterialCode());
            d.setMaterialDescription(line.getMaterialDescription());
            d.setOrderedQty(line.getOrderedQty());
            d.setAcceptedQty(line.getAcceptedQty());
            d.setRejectedQty(line.getRejectedQty());
            d.setRate(line.getRate());
            d.setGst(line.getGst());
            d.setDuties(line.getDuties());
            d.setRemarks(line.getRemarks());
            return d;
        }).collect(Collectors.toList());
        serviceInspectionMaterialDtlRepository.saveAll(lines);

        // Hand off to the generic, frontend-configurable approval engine.
        // No bespoke approve/reject/changeReq methods here — that's performTransitionAction()'s job.
        workflowService.initiateWorkflow(inspectionProcessId, WORKFLOW_NAME, req.getCreatedBy());

        return inspectionProcessId;
    }

    @Override
    public ServiceInspectionDto getServiceInspectionDtls(String inspectionProcessId) {
        ServiceInspectionMaster master = serviceInspectionRepository.findById(inspectionProcessId)
                .orElseThrow(() -> new RuntimeException("Service Inspection not found: " + inspectionProcessId));

        List<ServiceInspectionMaterialDtl> lines =
                serviceInspectionMaterialDtlRepository.findByInspectionProcessId(inspectionProcessId);

        return toDto(master, lines);
    }

    @Override
    public List<String> getPendingServiceInspectionSoIds() {
        List<String> approvedSoIds = workflowTransitionRepository.findApprovedSoIds();

        return approvedSoIds.stream()
                .filter(soId -> {
                    List<ServiceInspectionMaster> cycles = serviceInspectionRepository.findBySoId(soId);
                    boolean hasOpenCycle = cycles.stream()
                            .anyMatch(c -> "AWAITING APPROVAL".equalsIgnoreCase(c.getCurrentStatus()));
                    return !hasOpenCycle;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceInspectionDto> getServiceInspectionsBySoId(String soId) {
        return serviceInspectionRepository.findBySoId(soId).stream()
                .map(master -> toDto(master,
                        serviceInspectionMaterialDtlRepository.findByInspectionProcessId(master.getInspectionProcessId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getApprovedServiceInspectionIds() {
        return workflowTransitionRepository.findApprovedServiceInspectionIds();
    }

    @Override
    public paymentVoucherDto getPaymentVoucherDtoByInspectionId(String inspectionProcessId) {
        ServiceInspectionMaster si = serviceInspectionRepository.findById(inspectionProcessId)
                .orElseThrow(() -> new RuntimeException("Service Inspection not found: " + inspectionProcessId));

        ServiceOrder so = serviceOrderRepository.findById(si.getSoId())
                .orElseThrow(() -> new RuntimeException("Service Order not found: " + si.getSoId()));

        List<ServiceInspectionMaterialDtl> lines =
                serviceInspectionMaterialDtlRepository.findByInspectionProcessId(inspectionProcessId);

        paymentVoucherDto dto = new paymentVoucherDto();
        dto.setProcessId(inspectionProcessId);
        dto.setVendorName(so.getVendorName());

        List<paymentVoucherMaterials> materials = lines.stream().map(mat -> {
            paymentVoucherMaterials m = new paymentVoucherMaterials();
            m.setMaterialCode(mat.getMaterialCode());
            m.setMaterialDescription(mat.getMaterialDescription());
            m.setQuantity(mat.getAcceptedQty());   // accepted qty only — never the ordered qty
            m.setUnitPrice(mat.getRate());
            m.setGst(mat.getGst());
            BigDecimal qty = mat.getAcceptedQty() != null ? mat.getAcceptedQty() : BigDecimal.ZERO;
            BigDecimal price = mat.getRate() != null ? mat.getRate() : BigDecimal.ZERO;
            m.setAmount(qty.multiply(price));
            return m;
        }).collect(Collectors.toList());

        dto.setMaterialsList(materials);

        BigDecimal totalAmount = materials.stream()
                .map(m -> {
                    BigDecimal amount = m.getAmount() != null ? m.getAmount() : BigDecimal.ZERO;
                    BigDecimal gst = m.getGst() != null ? m.getGst() : BigDecimal.ZERO;
                    BigDecimal gstAmount = amount.multiply(gst).divide(BigDecimal.valueOf(100));
                    return amount.add(gstAmount);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(totalAmount);

        // NOTE: carry-forward is keyed by soId, same as the old GRN-based method was. For a
        // recurring/AMC SO with multiple inspection cycles this may need to be scoped to
        // inspectionProcessId instead, so cycle 2 doesn't inherit cycle 1's "already paid"
        // figure. Flagging, not silently resolving — see earlier note on this same question.
        Optional<PaymentVoucher> existingVoucherOpt =
                paymentVoucherReposiotry.findTopByServiceOrderDetailsOrderByIdDesc(si.getSoId());

        if (existingVoucherOpt.isPresent()) {
            PaymentVoucher existingVoucher = existingVoucherOpt.get();
            String type = existingVoucher.getPaymentVoucherType();

            if ("Partial".equalsIgnoreCase(type)) {
                BigDecimal partialPaid = existingVoucher.getPaidAmount() != null ? existingVoucher.getPaidAmount() : BigDecimal.ZERO;
                dto.setPaymentVoucherType("Partial");
                dto.setPartialAmountAlreadypaid(partialPaid);
                dto.setPartialBalanceAmount(totalAmount.subtract(partialPaid));
            } else if ("Advance".equalsIgnoreCase(type)) {
                BigDecimal advancePaid = existingVoucher.getPaidAmount() != null ? existingVoucher.getPaidAmount() : BigDecimal.ZERO;
                dto.setPaymentVoucherType("Advance");
                dto.setAdvanceAmountAlreadyPaid(advancePaid);
                dto.setAdvanceBalanceAmount(totalAmount.subtract(advancePaid));
            }
        }
        return dto;
    }

    private ServiceInspectionDto toDto(ServiceInspectionMaster master, List<ServiceInspectionMaterialDtl> lines) {
        ServiceInspectionDto dto = new ServiceInspectionDto();
        dto.setInspectionProcessId(master.getInspectionProcessId());
        dto.setSoId(master.getSoId());
        dto.setSubProcessId(master.getSubProcessId());
        dto.setVendorName(master.getVendorName());
        dto.setProjectName(master.getProjectName());
        dto.setCurrentStatus(master.getCurrentStatus());
        dto.setInspectedBy(master.getInspectedBy());
        dto.setInspectionDate(master.getInspectionDate());
        dto.setRemarks(master.getRemarks());
        dto.setSupportingDocBase64(master.getSupportingDocBase64());

        dto.setMaterials(lines.stream().map(l -> {
            ServiceInspectionMaterialLineDto m = new ServiceInspectionMaterialLineDto();
            m.setMaterialCode(l.getMaterialCode());
            m.setMaterialDescription(l.getMaterialDescription());
            m.setOrderedQty(l.getOrderedQty());
            m.setAcceptedQty(l.getAcceptedQty());
            m.setRejectedQty(l.getRejectedQty());
            m.setRate(l.getRate());
            m.setGst(l.getGst());
            m.setDuties(l.getDuties());
            m.setRemarks(l.getRemarks());
            return m;
        }).collect(Collectors.toList()));

        return dto;
    }
}

package com.astro.service.impl;




import com.astro.constant.AppConstant;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCreationResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.MaterialDetailsRequestDTO;
import com.astro.dto.workflow.ProcurementDtos.TenderWithIndentResponseDTO;
import com.astro.dto.workflow.ProcurementDtos.WorkOrderDto.*;
import com.astro.entity.ProcurementModule.*;
import com.astro.entity.ProjectMaster;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.ProcurementModule.IndentIdRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.repository.ProcurementModule.WorkOrder.WorkOrderMaterialRepository;
import com.astro.repository.ProcurementModule.WorkOrder.WorkOrderRepository;
import com.astro.repository.ProjectMasterRepository;
import com.astro.service.IndentCreationService;
import com.astro.service.TenderRequestService;
import com.astro.service.WorkOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkOrderImpl implements WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;
    @Autowired
    private WorkOrderMaterialRepository workOrderMaterialRepository;
    @Autowired
    private IndentCreationService indentCreationService;
    @Autowired
    private TenderRequestService tenderRequestService;
    @Autowired
    private IndentIdRepository indentIdRepository;
    @Autowired
    private TenderRequestRepository tenderRequestRepository;
    @Autowired
    private ProjectMasterRepository projectMasterRepository;
    public WorkOrderResponseDTO createWorkOrder(WorkOrderRequestDTO workOrderRequestDTO) {

        // Check if the indentorId already exists
        if (workOrderRepository.existsById(workOrderRequestDTO.getWoId())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate work order ID", "work order ID " + workOrderRequestDTO.getWoId() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }

     /*   // Iterate over materialDetails and check if materialCode already exists
        for (WorkOrderMaterialRequestDTO materialRequest : workOrderRequestDTO.getMaterials()) {
            if (workOrderMaterialRepository.existsById(materialRequest.getWorkCode())) {
                ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Material Code",
                        "Material Code " + materialRequest.getWorkCode() + " already exists.");
                throw new InvalidInputException(errorDetails);
            }
        }

      */
        WorkOrder workOrder = new WorkOrder();
        workOrder.setWoId(workOrderRequestDTO.getWoId());
        workOrder.setTenderId(workOrderRequestDTO.getTenderId());
        workOrder.setConsignesAddress(workOrderRequestDTO.getConsignesAddress());
        workOrder.setBillingAddress(workOrderRequestDTO.getBillingAddress());
        workOrder.setJobCompletionPeriod(workOrderRequestDTO.getJobCompletionPeriod());
        workOrder.setIfLdClauseApplicable(workOrderRequestDTO.getIfLdClauseApplicable());
        workOrder.setIncoTerms(workOrderRequestDTO.getIncoTerms());
        workOrder.setPaymentTerms(workOrderRequestDTO.getPaymentTerms());
        workOrder.setVendorName(workOrderRequestDTO.getVendorName());
        workOrder.setVendorAddress(workOrderRequestDTO.getVendorAddress());
        workOrder.setApplicablePBGToBeSubmitted(workOrderRequestDTO.getApplicablePBGToBeSubmitted());
        workOrder.setVendorsAccountNo(workOrderRequestDTO.getVendorsAccountNo());
        workOrder.setVendorsZRSCCode(workOrderRequestDTO.getVendorsZRSCCode());
        workOrder.setVendorsAccountName(workOrderRequestDTO.getVendorsAccountName());
        List<WorkOrderMaterial> workOrderMaterials = workOrderRequestDTO.getMaterials().stream()
                .map(dto -> {

                   WorkOrderMaterial material = new WorkOrderMaterial();
                    material.setWorkCode(dto.getWorkCode());
                    material.setWorkDescription(dto.getWorkDescription());
                    material.setQuantity(dto.getQuantity());
                    material.setRate(dto.getRate());
                    material.setExchangeRate(dto.getExchangeRate());
                    material.setCurrency(dto.getCurrency());
                    material.setGst(dto.getGst());
                    material.setDuties(dto.getDuties());
                    material.setBudgetCode(dto.getBudgetCode());
                    material.setWorkOrder(workOrder);
                    return material;
                })
                .collect(Collectors.toList());
        workOrder.setMaterials(workOrderMaterials);
        workOrderRepository.save(workOrder);

        return mapToResponseDTO(workOrder);
    }
    public WorkOrderResponseDTO updateWorkOrder(String woId, WorkOrderRequestDTO workOrderRequestDTO) {
        WorkOrder existingWorkOrder = workOrderRepository.findById(woId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Work order not found for the provided asset ID.")
                ));
        existingWorkOrder.setTenderId(workOrderRequestDTO.getTenderId());
        existingWorkOrder.setConsignesAddress(workOrderRequestDTO.getConsignesAddress());
        existingWorkOrder.setBillingAddress(workOrderRequestDTO.getBillingAddress());
        existingWorkOrder.setJobCompletionPeriod(workOrderRequestDTO.getJobCompletionPeriod());
        existingWorkOrder.setIfLdClauseApplicable(workOrderRequestDTO.getIfLdClauseApplicable());
        existingWorkOrder.setIncoTerms(workOrderRequestDTO.getIncoTerms());
        existingWorkOrder.setPaymentTerms(workOrderRequestDTO.getPaymentTerms());
        existingWorkOrder.setVendorName(workOrderRequestDTO.getVendorName());
        existingWorkOrder.setVendorAddress(workOrderRequestDTO.getVendorAddress());
        existingWorkOrder.setApplicablePBGToBeSubmitted(workOrderRequestDTO.getApplicablePBGToBeSubmitted());
        existingWorkOrder.setVendorsAccountNo(workOrderRequestDTO.getVendorsAccountNo());
        existingWorkOrder.setVendorsZRSCCode(workOrderRequestDTO.getVendorsZRSCCode());
        existingWorkOrder.setVendorsAccountName(workOrderRequestDTO.getVendorsAccountName());

        List<WorkOrderMaterial> existingAttributes = existingWorkOrder.getMaterials();

        // Remove orphaned attributes manually
        existingAttributes.clear();
        List<WorkOrderMaterial> updatedWorkMaterials = workOrderRequestDTO.getMaterials().stream()
                .map(dto -> {

                    WorkOrderMaterial material = new WorkOrderMaterial();
                    material.setWorkCode(dto.getWorkCode());
                    material.setWorkDescription(dto.getWorkDescription());
                    material.setQuantity(dto.getQuantity());
                    material.setRate(dto.getRate());
                    material.setExchangeRate(dto.getExchangeRate());
                    material.setCurrency(dto.getCurrency());
                    material.setGst(dto.getGst());
                    material.setDuties(dto.getDuties());
                    material.setBudgetCode(dto.getBudgetCode());
                    material.setWorkOrder(existingWorkOrder);
                    return material;
                })
                .collect(Collectors.toList());

        existingAttributes.addAll(updatedWorkMaterials);
        //Save updated work order
        workOrderRepository.save(existingWorkOrder);
        return mapToResponseDTO(existingWorkOrder);
    }

    public List<WorkOrderResponseDTO> getAllWorkOrders() {
        List<WorkOrder> workOrders = workOrderRepository.findAll();
        return workOrders.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public woWithTenderAndIndentResponseDTO getWorkOrderById(String woId) {
        WorkOrder workOrder = workOrderRepository.findById(woId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "work order not found for the provided asset ID.")
                ));

        // Fetch related Tender & Indent
        TenderWithIndentResponseDTO tenderWithIndent = tenderRequestService.getTenderRequestById(workOrder.getTenderId());
      woWithTenderAndIndentResponseDTO response = new woWithTenderAndIndentResponseDTO();
        response.setWoId(workOrder.getWoId());
        response.setTenderId(workOrder.getTenderId());
        response.setConsignesAddress(workOrder.getConsignesAddress());
        response.setBillingAddress(workOrder.getBillingAddress());
        response.setJobCompletionPeriod(workOrder.getJobCompletionPeriod());
        response.setIfLdClauseApplicable(workOrder.getIfLdClauseApplicable());
        response.setIncoTerms(workOrder.getIncoTerms());
        response.setPaymentTerms(workOrder.getPaymentTerms());
        response.setVendorName(workOrder.getVendorName());
        response.setVendorAddress(workOrder.getVendorAddress());
        response.setApplicablePBGToBeSubmitted(workOrder.getApplicablePBGToBeSubmitted());
        response.setVendorsAccountNo(workOrder.getVendorsAccountNo());
        response.setVendorsZRSCCode(workOrder.getVendorsZRSCCode());
        response.setVendorsAccountName(workOrder.getVendorsAccountName());
        response.setTotalValueOfWo(tenderWithIndent.getTotalTenderValue());
        response.setCreatedBy(workOrder.getCreatedBy());
        response.setUpdatedBy(workOrder.getUpdatedBy());
        response.setCreatedDate(workOrder.getCreatedDate());
        response.setUpdatedDate(workOrder.getUpdatedDate());
        response.setMaterials(workOrder.getMaterials().stream()
                .map(dto -> {
                    WorkOrderMaterialResponseDTO material = new WorkOrderMaterialResponseDTO();

                    material.setWorkCode(dto.getWorkCode());
                    material.setWorkDescription(dto.getWorkDescription());
                    material.setQuantity(dto.getQuantity());
                    material.setRate(dto.getRate());
                    material.setExchangeRate(dto.getExchangeRate());
                    material.setCurrency(dto.getCurrency());
                    material.setGst(dto.getGst());
                    material.setDuties(dto.getDuties());
                    material.setBudgetCode(dto.getBudgetCode()); // Associate with PurchaseOrder
                    return material;
                })
                .collect(Collectors.toList()));
        response.setTenderDetails(tenderWithIndent);
        return response;
    }

    public void deleteWorkOrder(String woId) {

      WorkOrder workOrder=workOrderRepository.findById(woId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Work order not found for the provided ID."
                        )
                ));
        try {
            workOrderRepository.delete(workOrder);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the  wo."
                    ),
                    ex
            );
        }
    }


    private WorkOrderResponseDTO mapToResponseDTO(WorkOrder workOrder) {
        WorkOrderResponseDTO response = new WorkOrderResponseDTO();
       response.setWoId(workOrder.getWoId());
        response.setTenderId(workOrder.getTenderId());
        response.setConsignesAddress(workOrder.getConsignesAddress());
        response.setBillingAddress(workOrder.getBillingAddress());
        response.setJobCompletionPeriod(workOrder.getJobCompletionPeriod());
        response.setIfLdClauseApplicable(workOrder.getIfLdClauseApplicable());
        response.setIncoTerms(workOrder.getIncoTerms());
        response.setPaymentTerms(workOrder.getPaymentTerms());
        response.setVendorName(workOrder.getVendorName());
        response.setVendorAddress(workOrder.getVendorAddress());
        response.setApplicablePBGToBeSubmitted(workOrder.getApplicablePBGToBeSubmitted());
        response.setVendorsAccountNo(workOrder.getVendorsAccountNo());
        response.setVendorsZRSCCode(workOrder.getVendorsZRSCCode());
        response.setVendorsAccountName(workOrder.getVendorsAccountName());
        response.setCreatedBy(workOrder.getCreatedBy());
        response.setUpdatedBy(workOrder.getUpdatedBy());
        response.setCreatedDate(workOrder.getCreatedDate());
        response.setUpdatedDate(workOrder.getUpdatedDate());
        response.setMaterials(workOrder.getMaterials().stream()
                .map(dto -> {
                    WorkOrderMaterialResponseDTO material = new WorkOrderMaterialResponseDTO();

                    material.setWorkCode(dto.getWorkCode());
                    material.setWorkDescription(dto.getWorkDescription());
                    material.setQuantity(dto.getQuantity());
                    material.setRate(dto.getRate());
                    material.setExchangeRate(dto.getExchangeRate());
                    material.setCurrency(dto.getCurrency());
                    material.setGst(dto.getGst());
                    material.setDuties(dto.getDuties());
                    material.setBudgetCode(dto.getBudgetCode()); // Associate with PurchaseOrder
                    return material;
                })
                .collect(Collectors.toList()));
        List<String> indentIds = indentIdRepository.findTenderWithIndent(workOrder.getTenderId());

        // Calculate total tender value by summing totalPriceOfAllMaterials of all indents
      /*  BigDecimal totalTenderValue = indentIds.stream()
                .map(indentCreationService::getIndentById) // Fetch Indent data
                .map(IndentCreationResponseDTO::getTotalPriceOfAllMaterials) // Extract total price
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sum up values
        response.setTotalValue(totalTenderValue);*/
        //System.out.println("tottalTenderValue"+ totalTenderValue);
        Optional<TenderRequest> tenderRequest = tenderRequestRepository.findByTenderId(workOrder.getTenderId());

        String projectName =tenderRequest.map(TenderRequest::getProjectName).orElse(null);
        response.setProjectName(projectName);
        System.out.println("projectName:"+projectName);
        BigDecimal allocatedAmount = projectMasterRepository
                .findByProjectNameDescription(projectName)
                .map(ProjectMaster::getAllocatedAmount)
                .orElse(BigDecimal.ZERO);
        response.setProjectLimit(allocatedAmount);
        System.out.println("allocatedAmount: " + allocatedAmount);
        return response;
    }


}

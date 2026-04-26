package com.astro.service;

import com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto.*;
import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.ApprovedPoListReportDto;
import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.pendingPoReportDto;

import java.util.List;

public interface ServiceOrderService {

    public ServiceOrderResponseDTO createServiceOrder(ServiceOrderRequestDTO serviceOrderRequestDTO);


    public ServiceOrderResponseDTO updateServiceOrder(String soId, ServiceOrderRequestDTO serviceOrderRequestDTO);
    public List<ServiceOrderResponseDTO> getAllServiceOrders();
    public soWithTenderAndIndentResponseDTO getServiceOrderById(String soId);
    public void deleteServiceOrder(String soId);
    List<ServiceOrderResponseDTO> getSoVersionHistory(String soId);
    public List<ApprovedSoListReportDto> getApprovedSoListReport(String startDate, String endDate,Integer userId, String roleName);

    public List<PendingSoReportDto> getPendingSoReport(String startDate, String endDate , Integer userId, String roleName);

}

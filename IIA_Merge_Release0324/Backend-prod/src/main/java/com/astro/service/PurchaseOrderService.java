package com.astro.service;



import com.astro.dto.workflow.ProcurementDtos.IndentDto.materialHistoryDto;
import com.astro.dto.workflow.ProcurementDtos.PoFormateDto;
import com.astro.dto.workflow.ProcurementDtos.ProcurementActivityReportResponse;
import com.astro.dto.workflow.ProcurementDtos.performanceWarrsntySecurityReportDto;
import com.astro.dto.workflow.ProcurementDtos.purchaseOrder.*;
import com.astro.dto.workflow.VendorContractReportDTO;
import com.astro.dto.workflow.poMaterialHistoryDto;

import java.io.IOException;
import java.util.List;

public interface PurchaseOrderService {

    public PurchaseOrderResponseDTO createPurchaseOrder(PurchaseOrderRequestDTO purchaseOrderRequestDTO);
    public PurchaseOrderResponseDTO updatePurchaseOrder(String poId, PurchaseOrderRequestDTO purchaseOrderRequestDTO);


    List<PurchaseOrderResponseDTO> getPoVersionHistory(String poId);
    
   public List<PurchaseOrderResponseDTO > getAllPurchaseOrders();

    public poWithTenderAndIndentResponseDTO getPurchaseOrderById(String poId) ;
    public PoWithTenderAndIndentBase64FilesDto getPurchaseOrderBase64FilesById(String poId) throws IOException;
    public void deletePurchaseOrder(String poId);

    List<VendorContractReportDTO> getVendorContractDetails(String startDate, String endDate);

    List<ProcurementActivityReportResponse> getProcurementActivityReport(String startDate, String endDate);

    public List<ApprovedPoListReportDto> getApprovedPoReport(String startDate, String endDate, Integer userId, String roleName);
    public List<pendingPoReportDto> getPendingPoReport(String startDate, String endDate, Integer userId, String roleName);

    public  List<QuarterlyVigilanceReportDto> getQuarterlyVigilanceReport();
    public List<ShortClosedCancelledOrderReportDto> getShortClosedCancelledOrders(String startDate, String endDate);

    public List<MonthlyProcurementReportDto> getMonthlyProcurementReport(String startDate, String endDate);

    public List<SearchPOIdDto> searchPOIds(String type, String value);

    public List<poMaterialHistoryDto> getLatestPurchaseOrders(String materialCode);

    public List<performanceWarrsntySecurityReportDto> getPerformanceSecurityReport(String startDate, String endDate);
    public PoFormateDto getPoFormatDetails(String poId) throws IOException;

}

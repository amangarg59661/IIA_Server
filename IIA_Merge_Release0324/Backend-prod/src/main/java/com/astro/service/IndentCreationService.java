package com.astro.service;

import com.astro.dto.workflow.AssignEmployeeToIndentDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.*;
import com.astro.dto.workflow.ProcurementDtos.IndentWorkflowStatusDto;
import com.astro.dto.workflow.ProcurementDtos.SreviceOrderDto.ApprovedSoListReportDto;
import com.astro.dto.workflow.ProcurementDtos.TechnoMomReportDTO;
import com.astro.entity.ProcurementModule.IndentCreation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IndentCreationService {

    public IndentCreationResponseDTO createIndent(IndentCreationRequestDTO indentRequestDTO);
            //,String uploadingPriorApprovalsFileName,String uploadTenderDocumentsFileName,String uploadGOIOrRFPFileName,String uploadPACOrBrandPACFileName );

   public IndentCreationResponseDTO updateIndent(String indentId, IndentCreationRequestDTO indentRequestDTO);
         //  ,String uploadingPriorApprovalsFileName, String uploadTenderDocumentsFileName,String uploadGOIOrRFPFileName,String uploadPACOrBrandPACFileName);

    public IndentCreationResponseDTO getIndentById(String indentId);

    public IndentDataResponseDto getIndentDataById(String indentId) throws IOException;

    public List<IndentCreationResponseDTO> getAllIndents();

    void deleteIndent(String indentId);
    public List<IndentCreationResponseDTO> getIndentVersionHistory(String indentId) ;

    List<IndentReportDetailsDTO> getIndentReport(String startDate, String endDate);

    public List<TechnoMomReportDTO> getTechnoMomReport(String startDate, String endDate);

    public List<materialHistoryDto> getIndentIdAndUserId(String materialCode);

    public List<materialHistoryDto> getJobIndentHistory(String jobCode);

    public List<IndentListReportDto> getAllIndentsReport(String startDate, String endDate, Integer userId, String roleName);

    public List<IndentWorkflowStatusDto> getIndentWorkflowStatus(String indentId, Integer userId, String roleName);

   public List<SearchIndentIdDto> searchIndentIds(String type, String value, String indentType, String materialCategoryType);

    public String assignEmployeeToIndent(AssignEmployeeToIndentDto dto);

    public String cancelIndent(CancelIndentRequestDto request);

    public String requestIndentCancellation(com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCancellationRequestDto request);

    public List<com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCancellationResponseDto> getPendingCancellationRequests();

    public String approveCancellationRequest(com.astro.dto.workflow.ProcurementDtos.IndentDto.IndentCancellationApprovalDto approval);

    public IndentCreationResponseDTO getIndentDataForTenderById(String indentId) throws IOException;

    public List<com.astro.dto.workflow.MaterialPurchaseHistoryDTO> getMaterialPurchaseHistory(String materialCode);

    public List<com.astro.dto.workflow.MaterialPurchaseHistoryDTO> getJobPurchaseHistory(String jobCode);

    }

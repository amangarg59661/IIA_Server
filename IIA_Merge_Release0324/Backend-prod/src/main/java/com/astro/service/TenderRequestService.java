package com.astro.service;


import com.astro.dto.workflow.ProcurementDtos.*;
import com.astro.entity.ProcurementModule.TenderRequest;

import java.io.IOException;
import java.util.List;

public interface TenderRequestService {


    public TenderResponseDto createTenderRequest(TenderRequestDto tenderRequestDto);
           // ,String uploadTenderDocumentsFileName,String uploadGeneralTermsAndConditionsFileName, String uploadSpecificTermsAndConditionsFileName);
    public TenderResponseDto updateTenderRequest(String tenderId, TenderRequestDto tenderRequestDto);
           // ,String uploadTenderDocumentsFileName,String uploadGeneralTermsAndConditionsFileName , String uploadSpecificTermsAndConditionsFileName);
   public TenderWithIndentResponseDTO getTenderRequestById(String tenderId);
   public TenderResponseDto getTenderData(String tenderId);
    public List<TenderResponseDto> getAllTenderRequests();
    public void deleteTenderRequest(String tenderId);
    public TenderResponseDto updateTender(String tenderId, tenderUpdateDto dto);
       // public List<TenderResponseDto> getTenderVersionHistory(String tenderId);
    public VendorQualificationResponseDto  vendorCheck(String tenderId, String vendorId);
    public TenderResponseBase64FilesDto getTenderDataWithBase64Files(String tenderId) throws IOException;
    public List<SearchTenderIdDto> searchTenderIds(String type, String value);

    public List<ApprovedTenderIdDtos> getApprovedTenderIdsForTenderEvaluation();
    public List<ApprovedTenderIdDtos> getApprovedTenderIdsForGemTenderEvaluation();
    public List<TenderResponseDto> getTenderVersionHistory(String tenderId);

TenderResponseDto saveTenderDraft(TenderRequestDto dto);
TenderResponseDto updateTenderDraft(String tenderId, TenderRequestDto dto);
TenderResponseDto submitTenderDraft(String tenderId, TenderRequestDto dto);
List<TenderResponseDto> getUserTenderDrafts(Integer userId);

    //TenderWithIndentResponseDTO getTendersRequestById(String tenderId);
}

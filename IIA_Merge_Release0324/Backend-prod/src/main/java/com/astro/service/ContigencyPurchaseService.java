package com.astro.service;

import com.astro.dto.workflow.ProcurementDtos.ContigencyPurchaseReportDto;
import com.astro.dto.workflow.ProcurementDtos.ContigencyPurchaseRequestDto;
import com.astro.dto.workflow.ProcurementDtos.ContigencyPurchaseResponseDto;
import com.astro.dto.workflow.ProcurementDtos.SearchCpIdDto;
import com.astro.entity.ProcurementModule.ContigencyPurchase;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContigencyPurchaseService {


    public ContigencyPurchaseResponseDto createContigencyPurchase(ContigencyPurchaseRequestDto contigencyPurchaseDto);
            //,String uploadCopyOfInvoiceFileName);
   // public ContigencyPurchaseResponseDto updateContigencyPurchase(String contigencyId, ContigencyPurchaseRequestDto contigencyPurchaseDto);
            //,String uploadCopyOfInvoiceFileName);
    public ContigencyPurchaseResponseDto getContigencyPurchaseById(String contigencyId);
    public List<ContigencyPurchaseResponseDto> getAllContigencyPurchase();
    public void deleteContigencyPurchase(String contigencyId);

    public List<ContigencyPurchaseReportDto> getContigencyPurchaseReport(String startDate, String endDate);


    public List<SearchCpIdDto> searchContigencyIds(String type, String value);

    ContigencyPurchaseResponseDto saveCpDraft(ContigencyPurchaseRequestDto dto);
    ContigencyPurchaseResponseDto updateCpDraft(String cpId, ContigencyPurchaseRequestDto dto);
    ContigencyPurchaseResponseDto submitCpDraft(String cpId, ContigencyPurchaseRequestDto dto);
    List<ContigencyPurchaseResponseDto> getUserCpDrafts(Integer userId);

}

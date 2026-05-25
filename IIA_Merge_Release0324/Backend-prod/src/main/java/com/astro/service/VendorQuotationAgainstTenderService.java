package com.astro.service;

import com.astro.dto.workflow.*;
import com.astro.dto.workflow.ProcurementDtos.AllVendorStatus;
import com.astro.dto.workflow.ProcurementDtos.QuotationViewHistoryDto;
import com.astro.dto.workflow.ProcurementDtos.VendorQuotationChangeRequestDto;
import com.astro.dto.workflow.VendorLoginRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VendorQuotationAgainstTenderService {

    public VendorQuotationAgainstTenderDto saveQuotation(VendorQuotationAgainstTenderDto dto);

    public List<VendorQuotationAgainstTenderDto> saveBulkQuotations(BulkVendorQuotationRequest request);

    public List<VendorQuotationAgainstTenderDto> getQuotationsByTenderId(String tenderId, String loggedInRole);

    public VendorQuotationAcceptedAndRejectedDataDto getAllVendorQuotationsByTenderId(String tenderId);

    public VendorStatusDto getVendorStatus(String vendorId);
    
    public List<String> getVendorsWhoDidNotSubmitQuotation(String tenderId);
    
    public boolean updateStatusAndRemarks(VendorQuotationUpdateRequestDto request);

    boolean markQuotationForChangeRequest(VendorQuotationChangeRequestDto request);

    List<QuotationViewHistoryDto> getVendorHistory(String tenderId, String vendorId);
    
    public boolean acceptVendorQuotation(String tenderId, String vendorId, Integer userId);
    
    public boolean storeOfficerReviewQuotation(String tenderId, String vendorId, String action, String remarks, Integer userId);
    
    public boolean rejectVendorQuotation(String tenderId, String vendorId, String remarks, Integer userId);
    
    public List<TenderEvaluationHistory> getFullQuotationHistory(String tenderId, String vendorId);
    
    List<String> getVendorsWithCompletedQuotation(String tenderId);
    
    public List<CompletedVendorsDto> getVendorsNamesWithCompletedQuotation(String tenderId);
    
    public List<AllVendorStatus> getAllVendorStatusOnTenderid(String tenderId);
    
    public List<AllVendorStatus> getAllVendorStatusOnTenderidsForGem(String tenderId);

    // New method for changing password
    public ChangePasswordResponseDto changePassword(ChangePasswordRequestDto request);

    public VendorStatusDto vendorLogin(VendorLoginRequestDto request);
}
package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.*;
import com.astro.dto.workflow.ProcurementDtos.AllVendorStatus;
import com.astro.dto.workflow.ProcurementDtos.QuotationViewHistoryDto;
import com.astro.dto.workflow.ProcurementDtos.VendorQuotationChangeRequestDto;
import com.astro.entity.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.astro.entity.ProcurementModule.TenderEvaluation;
import com.astro.entity.ProcurementModule.TenderRequest;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.*;
import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
import com.astro.repository.ProcurementModule.IndentIdRepository;
import com.astro.repository.ProcurementModule.TenderEvaluationRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.service.VendorQuotationAgainstTenderService;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.astro.config.JwtUtil;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VendorQuotationAgainstTenderServiceImpl implements VendorQuotationAgainstTenderService {

    @Autowired
    private VendorQuotationAgainstTenderRepository vendorQuotationAgainstTenderRepository;
    @Autowired
    private VendorMasterRepository vendorMasterRepository;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VendorMasterUtilRepository vendorMasterUtilRepository;
    @Autowired
    private VendorLoginDetailsRepository vendorLoginDetailsRepository;
    @Autowired
    private IndentIdRepository indentRepo;
    @Autowired
    private VendorNamesForJobWorkMaterialRepository vRepo;
    @Autowired
    private TenderRequestRepository tenderRepo;
    @Autowired
    private TenderEvaluationRepository tenderEvaluationRepository;
    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;
    @Autowired
    private GemVendorIdTrackerRepository gemVendorIdTrackerRepository;
    @Autowired
    private TenderClarificationHistoryRepository clarificationHistoryRepository;
   
   @Override
   public VendorQuotationAgainstTenderDto saveQuotation(VendorQuotationAgainstTenderDto dto) {

       TenderEvaluation tenderEval = tenderEvaluationRepository.findByTenderId(dto.getTenderId());
       if (tenderEval != null && tenderEval.getInitiated() != null && tenderEval.getInitiated() == 1) {
           String pendingFrom = tenderEval.getClarificationPendingFrom();
           boolean vendorClarificationPending =
                   "VENDOR".equalsIgnoreCase(pendingFrom) || "ALL_VENDORS".equalsIgnoreCase(pendingFrom);

           if (!vendorClarificationPending) {
               throw new BusinessException(new ErrorDetails(400, 1, "TENDER_INITIATED",
                       "Tender already under evaluation. Cannot submit new quotations."));
           }
           // For single-vendor clarification, only the targeted vendor may respond
           if ("VENDOR".equalsIgnoreCase(pendingFrom)) {
               String targetVid = tenderEval.getClarificationTargetVendorId();
               if (targetVid != null && !targetVid.equalsIgnoreCase(dto.getVendorId())) {
                   throw new BusinessException(new ErrorDetails(400, 1, "TENDER_INITIATED",
                           "Tender already under evaluation. Cannot submit new quotations."));
               }
           }
       }

       if (dto.getVendorId() == null && "GEM".equalsIgnoreCase(dto.getType())) {
           // get last vendorId
           Optional<GemVendorIdTracker> latest = gemVendorIdTrackerRepository.findTopByOrderByVendorIdDesc();
           Long newVendorId = latest.map(v -> v.getVendorId() + 1).orElse(1000L); // start from 1000
           String vendorId = "GEM" + newVendorId;
           // save new vendor entry
           GemVendorIdTracker tracker = new GemVendorIdTracker();
           tracker.setVendorId(newVendorId);
           tracker.setVendorName(dto.getVendorName());
           tracker.setGemVendorId(vendorId);
           gemVendorIdTrackerRepository.save(tracker);
           // assign back to DTO
           dto.setVendorId(vendorId);
       }

       List<VendorQuotationAgainstTender> existingQuotations =
               vendorQuotationAgainstTenderRepository.findAllByTenderIdAndVendorId(dto.getTenderId(), dto.getVendorId());

       int maxVersion = 0;
       for (VendorQuotationAgainstTender q : existingQuotations) {
           q.setIsLatest(false);
           vendorQuotationAgainstTenderRepository.save(q);
           if (q.getVersion() != null && q.getVersion() > maxVersion) {
               maxVersion = q.getVersion();
           }
       }
       VendorQuotationAgainstTender quotation = new VendorQuotationAgainstTender();
       if ("Change Requested".equalsIgnoreCase(dto.getStatus())) {
           Optional<VendorQuotationAgainstTender> vt = vendorQuotationAgainstTenderRepository.findLatestByTenderIdAndVendorId(dto.getTenderId(), dto.getVendorId());
           VendorQuotationAgainstTender v = null;
           if (vt.isPresent()) {
               v = vt.get();
           }

           quotation.setTenderId(dto.getTenderId());
           quotation.setVendorId(dto.getVendorId());
           quotation.setQuotationFileName(v.getQuotationFileName());
           quotation.setPriceBidFileName(v.getPriceBidFileName());
           quotation.setFileType(v.getFileType());
           quotation.setClarificationFileName(dto.getClarificationFileName());
           quotation.setVendorResponse(dto.getVendorResponse());
           quotation.setCreatedBy(dto.getVendorId());
           quotation.setVersion(maxVersion + 1);
           quotation.setIsLatest(true);

           quotation.setStatus("SUBMITTED");
           quotation.setUpdatedBy("1");
           quotation.setCurrentRole(VendorQuotationAgainstTender.WorkflowActorRole.VENDOR);
           quotation.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
           quotation.setCreatedDate(LocalDateTime.now());
           quotation.setUpdatedDate(LocalDateTime.now());
       }else{
          // VendorQuotationAgainstTender quotation = new VendorQuotationAgainstTender();
           quotation.setTenderId(dto.getTenderId());
           quotation.setVendorId(dto.getVendorId());
           quotation.setQuotationFileName(dto.getQuotationFileName());
           quotation.setPriceBidFileName(dto.getPriceBidFileName());
           quotation.setFileType(dto.getFileType());
           quotation.setCreatedBy(dto.getCreatedBy());
           quotation.setVersion(maxVersion + 1);
           quotation.setIsLatest(true);
           quotation.setStatus("SUBMITTED");
           quotation.setUpdatedBy("1");
           quotation.setCurrentRole(VendorQuotationAgainstTender.WorkflowActorRole.VENDOR);
           quotation.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
           quotation.setCreatedDate(LocalDateTime.now());
           quotation.setUpdatedDate(LocalDateTime.now());

       }

       VendorQuotationAgainstTender saved = vendorQuotationAgainstTenderRepository.save(quotation);

       // ── Update clarification history with actual vendor response ──
       if ("Change Requested".equalsIgnoreCase(dto.getStatus()) && dto.getVendorResponse() != null) {
           try {
               String tenderId = dto.getTenderId();
               String vendorId = dto.getVendorId();
               List<TenderClarificationHistory> history = clarificationHistoryRepository
                       .findByTenderIdOrderByRequestedAtDesc(tenderId);
               history.stream()
                       .filter(h -> h.getRespondedAt() == null
                               && ("VENDOR".equals(h.getClarificationTarget())
                                   || "ALL_VENDORS".equals(h.getClarificationTarget()))
                               && (vendorId.equals(h.getTargetVendorId())
                                   || h.getTargetVendorId() == null
                                   || "ALL_VENDORS".equals(h.getClarificationTarget())))
                       .findFirst()
                       .ifPresent(h -> {
                           h.setResponseText(dto.getVendorResponse());
                           h.setResponseFileName(dto.getClarificationFileName());
                           h.setRespondedByRole("VENDOR");
                           h.setRespondedById(dto.getVendorId());
                           h.setRespondedAt(LocalDateTime.now());
                           clarificationHistoryRepository.save(h);
                       });
           } catch (Exception e) {
            //    log.warn("Clarification history update in saveQuotation failed: {}", e.getMessage());
           }

           // ── Auto-restore eval status when all vendors have responded ──
           try {
               String tenderId = dto.getTenderId();
               TenderEvaluation eval = tenderEvaluationRepository.findByTenderId(tenderId);
               if (eval != null && "PENDING_VENDOR_CLARIFICATION".equals(eval.getEvaluationStatus())) {
                   long stillPending = vendorQuotationAgainstTenderRepository
                           .findByTenderIdAndIsLatestTrue(tenderId)
                           .stream()
                           .filter(q -> "CHANGE_REQUESTED".equalsIgnoreCase(q.getStatus()))
                           .count();
                   if (stillPending == 0) {
                       String restoreStatus = eval.getPreviousEvaluationStatus();
                       if (restoreStatus == null || restoreStatus.isBlank()) {
                           restoreStatus = "PENDING_APPROVAL";
                       }
                       eval.setEvaluationStatus(restoreStatus);
                       eval.setPreviousEvaluationStatus(null);
                       eval.setClarificationPendingFrom(null);
                       eval.setClarificationPendingFromId(null);
                       eval.setClarificationPendingFromName(null);
                       eval.setClarificationRequestedByRole(null);
                       eval.setClarificationRemarks(null);
                       eval.setClarificationTargetVendorId(null);
                       eval.setUpdatedDate(LocalDateTime.now());
                       tenderEvaluationRepository.save(eval);
                   }
               }
           } catch (Exception e) {
               // non-fatal: eval status restore can be done manually if needed
           }
       }

       return mapToResponse(saved);
   }

    @Override
    @Transactional
    public List<VendorQuotationAgainstTenderDto> saveBulkQuotations(BulkVendorQuotationRequest request) {
        if (request.getQuotations() == null || request.getQuotations().isEmpty()) {
            throw new BusinessException(new ErrorDetails(400, 1, "BULK_EMPTY", "No quotations provided in bulk request"));
        }

        List<VendorQuotationAgainstTenderDto> results = new ArrayList<>();
        for (VendorQuotationAgainstTenderDto dto : request.getQuotations()) {
            dto.setTenderId(request.getTenderId());
            VendorQuotationAgainstTenderDto saved = saveQuotation(dto);
            results.add(saved);
        }
        return results;
    }

    @Override
   public List<VendorQuotationAgainstTenderDto> getQuotationsByTenderId(String tenderId, String loggedInRole) {

       List<VendorQuotationAgainstTender> allLatest = vendorQuotationAgainstTenderRepository.findLatestVersionsForTender(tenderId);

       return allLatest.stream()
               .map(vq -> {
                   VendorQuotationAgainstTenderDto dto = new VendorQuotationAgainstTenderDto();
                   dto.setTenderId(vq.getTenderId());
                /*  Optional<VendorMaster> vm = vendorMasterRepository.findByVendorId(vq.getVendorId());
                  if(vm.isPresent()){
                      VendorMaster vendor = vm.get();
                      dto.setVendorName(vendor.getVendorName());
                  }*/
                   String vendorId = vq.getVendorId();

                   if (vendorId.startsWith("GEM")) {
                       Optional<GemVendorIdTracker> gemVendor =
                               gemVendorIdTrackerRepository.findByGemVendorId(vendorId);
                       if (gemVendor.isPresent()) {
                           GemVendorIdTracker gem = gemVendor.get();
                          dto.setVendorName(gem.getVendorName());
                       }
                   }
                   else  {
                       Optional<VendorMaster> vm = vendorMasterRepository.findByVendorId(vendorId);
                       if(vm.isPresent()){
                           VendorMaster vendor = vm.get();
                           dto.setVendorName(vendor.getVendorName());
                       }
                   } 
                   dto.setVendorId(vq.getVendorId());
                   dto.setQuotationFileName(vq.getQuotationFileName());
                   dto.setFileType(vq.getFileType());
                   dto.setCreatedBy(vq.getCreatedBy());
                   dto.setVersion(vq.getVersion());
                   dto.setRemarks(vq.getRemarks());
                   dto.setStatus(vq.getStatus()); //  SUBMITTED, CHANGE_REQUESTED, Rejected
                   dto.setIndentorStatus(vq.getIndentorStatus());
                   dto.setIndentorRemarks(vq.getIndentorRemarks());
                   dto.setSopStatus(vq.getSpoStatus());
                   dto.setPriceBidFileName(vq.getPriceBidFileName());
                   dto.setClarificationFileName(vq.getClarificationFileName());
                   dto.setVendorResponse(vq.getVendorResponse());
                 //  dto.setAcceptanceStatus(vq.getAcceptanceStatus()); // ACCEPTED etc.

                   dto.setFinancialIndentorStatus(vq.getFinancialIndentorStatus());
                   dto.setFinancialSpoStatus(vq.getFinancialSpoStatus());
                   dto.setFinancialIndentorRemarks(vq.getFinancialIndentorRemarks());
                   dto.setFinancialSpoRemarks(vq.getFinancialSpoRemarks());
                   dto.setRegisteredVendorId(vq.getRegisteredVendorId());
                   dto.setRegisteredVendorName(vq.getRegisteredVendorName());

                   dto.setCanIndentorAct(canIndentorAct(vq, loggedInRole));
                   dto.setCanSpoAct(canSpoAct(vq, loggedInRole));

                   return dto;
               })
               .collect(Collectors.toList());
   }

private boolean canIndentorAct(VendorQuotationAgainstTender vq, String role) {
    // Only Indentor or Purchase Personal can act
    if (!(role.equalsIgnoreCase("INDENT CREATOR") || role.equalsIgnoreCase("Purchase personnel"))) {
        return false;
    }

    String status = vq.getStatus();
    String nextRole = String.valueOf(vq.getNextRole());

    // Indentor can act only if they are the next role and status is actionable
    if ("INDENTOR".equalsIgnoreCase(nextRole) &&
            !"ACCEPTED".equalsIgnoreCase(status) &&
            !"REJECTED".equalsIgnoreCase(status)) {
        return true;
    }
    if ("CHANGE_REQUESTED".equalsIgnoreCase(status)) {
        return true;
    }

    return false;
}
    private String normalizeRole(String role) {
        if (role == null) return "";
        return role.trim().replaceAll("\\s+", "_").toUpperCase();
    }

    private boolean canSpoAct(VendorQuotationAgainstTender vq, String role) {
    String roleName= normalizeRole(role);
        if (!"STORE_PURCHASE_OFFICER".equalsIgnoreCase(roleName)) {
            return false;
        }

        // If SPO already sent a change request back to indentor and waiting, block further SPO action
        if (Boolean.TRUE.equals(vq.getChangeRequestToIndentor())) {
            return false;
        }

        // SPO can act if it's their turn (nextRole == STORE_PURCHASE_OFFICER)
        return vq.getNextRole() == VendorQuotationAgainstTender.WorkflowActorRole.STORE_PURCHASE_OFFICER;
    }





    public VendorQuotationAcceptedAndRejectedDataDto getAllVendorQuotationsByTenderId(String tenderId) {
        VendorQuotationAcceptedAndRejectedDataDto responseDto = new VendorQuotationAcceptedAndRejectedDataDto();


        List<VendorQuotationAgainstTender> vqList = vendorQuotationAgainstTenderRepository
                .findRejectedOrAcceptedQuotations(tenderId);


        List<VendorQuotationAgainstTenderDto> dtoList = vqList.stream()
                .map(vq -> {
                    VendorQuotationAgainstTenderDto dto = new VendorQuotationAgainstTenderDto();
                    dto.setTenderId(vq.getTenderId());
                    dto.setVendorId(vq.getVendorId());
                    dto.setQuotationFileName(vq.getQuotationFileName());
                    dto.setFileType(vq.getFileType());
                    dto.setCreatedBy(vq.getCreatedBy());
                    dto.setVersion(vq.getVersion());
                    dto.setRemarks(vq.getRemarks());
                    dto.setStatus(vq.getStatus());

                    return dto;
                })
                .collect(Collectors.toList());


        responseDto.setVendor(dtoList);


        TenderEvaluation tr = tenderEvaluationRepository.findByTenderId(tenderId);

        if (tr != null) {
            responseDto.setUploadQualifiedVendorsFileName(tr.getUploadQualifiedVendorsFileName());
        }

        return responseDto;
    }





    @Override
    public List<String> getVendorsWhoDidNotSubmitQuotation(String tenderId) {

        List<String> indentIds = indentRepo.findTenderWithIndent(tenderId);
        List<String> allVendorIds = new ArrayList<>();
        for (String indentId : indentIds) {
            List<String> vendorIds = vRepo.findVendorNamesByIndentId(indentId);
            allVendorIds.addAll(vendorIds);
        }
        List<String> submittedVendorIds = vendorQuotationAgainstTenderRepository.findVendorIdsByTenderId(tenderId);
        allVendorIds.removeAll(submittedVendorIds);
        List<String> result = new ArrayList<>();
        if (!allVendorIds.isEmpty()) {
            List<Object[]> vendorDetails = vendorMasterRepository.findVendorIdAndNameByIds(allVendorIds);
            for (Object[] obj : vendorDetails) {
                String vendorId = (String) obj[0];
                String vendorName = (String) obj[1];
                result.add(vendorId + " - " + vendorName);
            }
        }

        return result;
    }

    @Override
public VendorStatusDto getVendorStatus(String vendorId) {

    VendorStatusDto dto = new VendorStatusDto();
    dto.setVendorId(vendorId);

    // approved vendors
    Optional<VendorMaster> approved = vendorMasterRepository.findByVendorId(vendorId);
    Optional<VendorLoginDetails> vendorLogin = vendorLoginDetailsRepository.findByVendorId(vendorId);
    
    if (vendorLogin.isPresent()) {
        VendorLoginDetails vl = vendorLogin.get();
        dto.setEmailStatus(vl.getEmailSent());
        dto.setIsFirstLogin(vl.getIsFirstLogin());
        dto.setIsTempPassword(vl.getIsTempPassword());
    }

    if (approved.isPresent()) {
        VendorMaster vm = approved.get();
        dto.setStatus(vm.getStatus());
        dto.setComments("Vendor is approved.");
        return dto;
    }

    // rejected/awaiting vendors
    Optional<VendorMasterUtil> pendingOrRej = vendorMasterUtilRepository.findByVendorId(vendorId);
    if (pendingOrRej.isPresent()) {
        VendorMasterUtil vendor = pendingOrRej.get();
        dto.setStatus(vendor.getApprovalStatus().name()); // rejected or awaiting for approval
        dto.setComments(vendor.getComments());
        return dto;
    }

    // Not found anywhere
    dto.setStatus("NOT_FOUND");
    dto.setComments("Vendor ID is not available in the system.");
    dto.setIsFirstLogin(null);
    dto.setIsTempPassword(null);
    return dto;
}


    private VendorQuotationAgainstTenderDto mapToResponse(VendorQuotationAgainstTender dto) {
        VendorQuotationAgainstTenderDto quotation = new VendorQuotationAgainstTenderDto();
        quotation.setTenderId(dto.getTenderId());
        quotation.setVendorId(dto.getVendorId());
       // quotation.setVendorName(dto.getVendorName());
        quotation.setQuotationFileName(dto.getQuotationFileName());
        quotation.setFileType(dto.getFileType());
        quotation.setCreatedBy(dto.getCreatedBy());
        return quotation;
    }


   public boolean updateStatusAndRemarks(VendorQuotationUpdateRequestDto request) {
       List<VendorQuotationAgainstTender> quotations = vendorQuotationAgainstTenderRepository.findByTenderIdAndVendorId(
               request.getTenderId(), request.getVendorId());

       if (!quotations.isEmpty()) {
           // You can choose to update all or only the latest one
           for (VendorQuotationAgainstTender record : quotations) {
               record.setStatus(request.getStatus());
               record.setRemarks(request.getRemarks());
               record.setUpdatedDate(LocalDateTime.now());
             //  quotation.setIndentorStatus("ACCEPTED");
            //   quotation.setIndentorRemarks("Accepted by indentor");
               vendorQuotationAgainstTenderRepository.save(record);
           }
           return true;
       } else {
           return false;
       }
   }


public boolean markQuotationForChangeRequest(VendorQuotationChangeRequestDto request) {
    Optional<VendorQuotationAgainstTender> optional = vendorQuotationAgainstTenderRepository
            .findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc(
                    request.getTenderId(), request.getVendorId());

    if (optional.isEmpty()) return false;

    VendorQuotationAgainstTender oldQuotation = optional.get();

    // Mark old version as not latest
    oldQuotation.setIsLatest(false);
    vendorQuotationAgainstTenderRepository.save(oldQuotation);

    // Create new version
    VendorQuotationAgainstTender newQuotation = new VendorQuotationAgainstTender();
    newQuotation.setTenderId(oldQuotation.getTenderId());
    newQuotation.setVendorId(oldQuotation.getVendorId());
    newQuotation.setQuotationFileName(oldQuotation.getQuotationFileName());
    newQuotation.setPriceBidFileName(oldQuotation.getPriceBidFileName());
    newQuotation.setClarificationFileName(oldQuotation.getClarificationFileName());
    newQuotation.setVendorResponse(oldQuotation.getVendorResponse());
    newQuotation.setFileType(oldQuotation.getFileType());
    newQuotation.setCreatedBy(oldQuotation.getCreatedBy());
    newQuotation.setVersion(oldQuotation.getVersion() + 1);
    newQuotation.setIsLatest(true);
    newQuotation.setCreatedDate(LocalDateTime.now());
    newQuotation.setUpdatedDate(LocalDateTime.now());

    // Copy other statuses if required
    newQuotation.setAcceptanceStatus(oldQuotation.getAcceptanceStatus());
    newQuotation.setSpoStatus(oldQuotation.getSpoStatus());
    newQuotation.setSpoRemarks(oldQuotation.getSpoRemarks());

    // Set Indentor change request values
    newQuotation.setStatus("CHANGE_REQUESTED");
    newQuotation.setIndentorStatus("CHANGE_REQUESTED");
    newQuotation.setRemarks(request.getRemarks());
    newQuotation.setIndentorRemarks(request.getRemarks());
    newQuotation.setStatus("CHANGE_REQUESTED");
   // newQuotation.setRemarks(request.getRemarks());
    newQuotation.setUpdatedBy(String.valueOf(request.getUserId()));
    newQuotation.setCurrentRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
    newQuotation.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.VENDOR);


    vendorQuotationAgainstTenderRepository.save(newQuotation);
    return true;
}

@Override
@Transactional
public ChangePasswordResponseDto changePassword(ChangePasswordRequestDto request) {
    
    // Validate new password and confirm password match
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
        return new ChangePasswordResponseDto(false, "New password and confirm password do not match", request.getVendorId());
    }

    // Validate new password is different from current password
    if (request.getCurrentPassword().equals(request.getNewPassword())) {
        return new ChangePasswordResponseDto(false, "New password must be different from current password", request.getVendorId());
    }

    // Find vendor login details
    Optional<VendorLoginDetails> vendorLoginOpt = vendorLoginDetailsRepository.findByVendorId(request.getVendorId());
    
    if (vendorLoginOpt.isEmpty()) {
        return new ChangePasswordResponseDto(false, "Vendor not found", request.getVendorId());
    }

    VendorLoginDetails vendorLogin = vendorLoginOpt.get();

    // Verify current password
    // ✅ Verify current password using BCrypt
    if (!passwordEncoder.matches(request.getCurrentPassword(), vendorLogin.getPassword())) {
        return new ChangePasswordResponseDto(false, "Current password is incorrect", request.getVendorId());
    }

    // ✅ Prevent reuse of the same password
    if (passwordEncoder.matches(request.getNewPassword(), vendorLogin.getPassword())) {
        return new ChangePasswordResponseDto(false, "New password must be different from current password", request.getVendorId());
    }

    // ✅ Encode new password before saving
    vendorLogin.setPassword(passwordEncoder.encode(request.getNewPassword()));
    // if (!vendorLogin.getPassword().equals(request.getCurrentPassword())) {
    //     return new ChangePasswordResponseDto(false, "Current password is incorrect", request.getVendorId());
    // }

    // // Update password
    // vendorLogin.setPassword(request.getNewPassword());
    vendorLogin.setIsFirstLogin(false);
    vendorLogin.setIsTempPassword(false);
    vendorLogin.setPasswordChangedAt(LocalDateTime.now());

    vendorLoginDetailsRepository.save(vendorLogin);

    return new ChangePasswordResponseDto(true, "Password changed successfully. Please login with your new password.", request.getVendorId());
}









    @Override
    public List<QuotationViewHistoryDto> getVendorHistory(String tenderId, String vendorId) {
        List<VendorQuotationAgainstTender> list =
                vendorQuotationAgainstTenderRepository.findAllByTenderIdAndVendorIdOrderByCreatedDateDesc(tenderId, vendorId);


        if (list.isEmpty()) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "No quotation history found for this vendor and tender"
            ));
        }

          return list.stream().map(quotation -> {
            QuotationViewHistoryDto dto = new QuotationViewHistoryDto();
            dto.setStatus(quotation.getStatus());
            dto.setRemarks(quotation.getRemarks());
            dto.setDate(quotation.getCreatedDate());
            return dto;
        }).collect(Collectors.toList());
    }

public boolean acceptVendorQuotation(String tenderId, String vendorId,Integer userId) {
    Optional<VendorQuotationAgainstTender> optional =
            vendorQuotationAgainstTenderRepository
                    .findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc(tenderId, vendorId);

    if (optional.isEmpty()) return false;

    VendorQuotationAgainstTender oldQuotation = optional.get();
    oldQuotation.setIsLatest(false);
    vendorQuotationAgainstTenderRepository.save(oldQuotation);

    VendorQuotationAgainstTender newQuotation = new VendorQuotationAgainstTender();
    newQuotation.setTenderId(tenderId);
    newQuotation.setVendorId(vendorId);
    newQuotation.setQuotationFileName(oldQuotation.getQuotationFileName());
    newQuotation.setPriceBidFileName(oldQuotation.getPriceBidFileName());
    newQuotation.setVendorResponse(oldQuotation.getVendorResponse());
    newQuotation.setClarificationFileName(oldQuotation.getClarificationFileName());
    newQuotation.setFileType(oldQuotation.getFileType());
    newQuotation.setCreatedBy(oldQuotation.getCreatedBy());
    newQuotation.setVersion(oldQuotation.getVersion() + 1);
    newQuotation.setIsLatest(true);
    newQuotation.setCreatedDate(LocalDateTime.now());
    newQuotation.setUpdatedDate(LocalDateTime.now());

    // Indentor accepted, hand over to SPO
    newQuotation.setStatus("PENDING_SPO"); // intermediate
  //  newQuotation.setRemarks("Accepted by indentor");
    newQuotation.setIndentorStatus("ACCEPTED");
   // newQuotation.setIndentorRemarks("Accepted by indentor");
    newQuotation.setUpdatedBy(String.valueOf(userId));
    newQuotation.setCurrentRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
    newQuotation.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.STORE_PURCHASE_OFFICER);

    vendorQuotationAgainstTenderRepository.save(newQuotation);
    return true;
}



    /**
     * Store/Purchase Officer reviews after indentor decision.
     * action: "ACCEPT", "REJECT", "CHANGE_REQUEST_TO_INTENTOR"
     */
    public boolean storeOfficerReviewQuotation(String tenderId, String vendorId, String action, String remarks, Integer userId) {
        Optional<VendorQuotationAgainstTender> optional =
                vendorQuotationAgainstTenderRepository.findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc(tenderId, vendorId);

        if (optional.isEmpty()) return false;

        VendorQuotationAgainstTender oldQuotation = optional.get();

        // Mark old as not latest
        oldQuotation.setIsLatest(false);
        vendorQuotationAgainstTenderRepository.save(oldQuotation);

        // Create new version
        VendorQuotationAgainstTender newQuotation = new VendorQuotationAgainstTender();
        newQuotation.setTenderId(oldQuotation.getTenderId());
        newQuotation.setVendorId(oldQuotation.getVendorId());
        newQuotation.setQuotationFileName(oldQuotation.getQuotationFileName());
        newQuotation.setPriceBidFileName(oldQuotation.getPriceBidFileName());
        newQuotation.setVendorResponse(oldQuotation.getVendorResponse());
        newQuotation.setClarificationFileName(oldQuotation.getClarificationFileName());
        newQuotation.setFileType(oldQuotation.getFileType());
        newQuotation.setCreatedBy(oldQuotation.getCreatedBy());
        newQuotation.setVersion(oldQuotation.getVersion() + 1);
        newQuotation.setIsLatest(true);
        newQuotation.setCreatedDate(LocalDateTime.now());
        newQuotation.setUpdatedDate(LocalDateTime.now());

        newQuotation.setIndentorStatus(oldQuotation.getIndentorStatus());
        newQuotation.setIndentorRemarks(oldQuotation.getIndentorRemarks());
        newQuotation.setRemarks(oldQuotation.getIndentorRemarks());
        newQuotation.setUpdatedBy(String.valueOf(userId));
        newQuotation.setCurrentRole(VendorQuotationAgainstTender.WorkflowActorRole.STORE_PURCHASE_OFFICER);

        switch (action) {
            case "ACCEPT":
                newQuotation.setSpoStatus("ACCEPTED");
                newQuotation.setSpoRemarks(remarks);
                newQuotation.setAcceptanceStatus("ACCEPTED");
                newQuotation.setChangeRequestToIndentor(false);
                newQuotation.setStatus("Completed");
                newQuotation.setRemarks(remarks);
                newQuotation.setNextRole(null);
                break;
            case "REJECT":
                newQuotation.setSpoStatus("REJECTED");
                newQuotation.setSpoRemarks(remarks);
                newQuotation.setAcceptanceStatus("REJECTED");
                newQuotation.setChangeRequestToIndentor(false);
                newQuotation.setStatus("REJECTED");
                newQuotation.setRemarks(remarks);
                newQuotation.setNextRole(null);
                break;
            case "CHANGE_REQUEST_TO_INTENTOR":
                newQuotation.setSpoStatus("CHANGE_REQUESTED_TO_INTENTOR");
                newQuotation.setSpoRemarks(remarks);
                newQuotation.setChangeRequestToIndentor(true);

                newQuotation.setStatus("CHANGE_REQUESTED");
                newQuotation.setRemarks(remarks);
                newQuotation.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
                break;
            default:
                return false;
        }

        vendorQuotationAgainstTenderRepository.save(newQuotation);
        return true;
    }

    @Transactional
    public boolean rejectVendorQuotation(String tenderId, String vendorId, String remarks, Integer userId) {
        Logger log = LoggerFactory.getLogger(getClass());

        Optional<VendorQuotationAgainstTender> optional =
                vendorQuotationAgainstTenderRepository
                        .findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc(tenderId, vendorId);

        if (optional.isEmpty()) {
            log.warn("No latest quotation found to reject for tenderId={} vendorId={}", tenderId, vendorId);
            return false;
        }

        VendorQuotationAgainstTender oldQuotation = optional.get();

        // mark previous as not latest
        oldQuotation.setIsLatest(false);
        vendorQuotationAgainstTenderRepository.save(oldQuotation);

        // build new version
        VendorQuotationAgainstTender newQuotation = new VendorQuotationAgainstTender();
        newQuotation.setTenderId(tenderId);
        newQuotation.setVendorId(vendorId);
        newQuotation.setQuotationFileName(oldQuotation.getQuotationFileName());
        newQuotation.setPriceBidFileName(oldQuotation.getPriceBidFileName());
        newQuotation.setClarificationFileName(oldQuotation.getClarificationFileName());
        newQuotation.setVendorResponse(oldQuotation.getVendorResponse());
        newQuotation.setFileType(oldQuotation.getFileType());
        newQuotation.setCreatedBy(oldQuotation.getCreatedBy());
        newQuotation.setVersion((oldQuotation.getVersion() != null ? oldQuotation.getVersion() : 0) + 1);
        newQuotation.setIsLatest(true);
        newQuotation.setCreatedDate(LocalDateTime.now());
        newQuotation.setUpdatedDate(LocalDateTime.now());

        // Indentor rejected → forward to SPO
        newQuotation.setStatus("PENDING_SPO"); // intermediate
        newQuotation.setRemarks(remarks);
        newQuotation.setIndentorStatus("REJECTED");
        newQuotation.setIndentorRemarks(remarks);
        newQuotation.setUpdatedBy(String.valueOf(userId)); // indentor
        newQuotation.setCurrentRole(VendorQuotationAgainstTender.WorkflowActorRole.INDENTOR);
        newQuotation.setNextRole(VendorQuotationAgainstTender.WorkflowActorRole.STORE_PURCHASE_OFFICER);

        // (Optional) clear acceptance/spo fields if not applicable
        newQuotation.setAcceptanceStatus(null);
        newQuotation.setSpoStatus(null);
        newQuotation.setSpoRemarks(null);
        newQuotation.setChangeRequestToIndentor(false);

        VendorQuotationAgainstTender saved = vendorQuotationAgainstTenderRepository.save(newQuotation);
        log.info("Created new rejected quotation version: tenderId={} vendorId={} version={}",
                tenderId, vendorId, saved.getVersion());

        return true;
    }

    @Override
    public List<TenderEvaluationHistory> getFullQuotationHistory(String tenderId, String vendorId) {
        List<VendorQuotationAgainstTender> list =
                vendorQuotationAgainstTenderRepository.findAllByTenderIdAndVendorIdOrderByCreatedDateDesc(tenderId, vendorId);

        if (list.isEmpty()) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "No quotation history found for this vendor and tender"
            ));
        }

        return list.stream().map(vq -> {
            TenderEvaluationHistory dto = new TenderEvaluationHistory();
            dto.setId(vq.getId());
            dto.setTenderId(vq.getTenderId());
            dto.setVendorId(vq.getVendorId());
            dto.setQuotationFileName(vq.getQuotationFileName());
            dto.setFileType(vq.getFileType());
            dto.setStatus(vq.getStatus());
            dto.setRemarks(vq.getRemarks());
            dto.setVersion(vq.getVersion());
            dto.setIsLatest(vq.getIsLatest());
            dto.setCreatedBy(vq.getCreatedBy());
            dto.setAcceptanceStatus(vq.getAcceptanceStatus());
            dto.setCreatedDate(vq.getCreatedDate());
            dto.setUpdatedDate(vq.getUpdatedDate());
            dto.setIndentorStatus(vq.getIndentorStatus());
            dto.setIndentorRemarks(vq.getIndentorRemarks());
            dto.setSpoStatus(vq.getSpoStatus());
            dto.setSpoRemarks(vq.getSpoRemarks());
            dto.setChangeRequestToIndentor(vq.getChangeRequestToIndentor());
            dto.setUpdatedBy(vq.getUpdatedBy());
            dto.setCurrentRole(vq.getCurrentRole());
            dto.setNextRole(vq.getNextRole());
            return dto;
        }).collect(Collectors.toList());

    }

    @Override
    public List<String> getVendorsWithCompletedQuotation(String tenderId) {
        return vendorQuotationAgainstTenderRepository.findVendorIdsWithCompletedStatus(tenderId);
    }
  @Override
  public List<CompletedVendorsDto> getVendorsNamesWithCompletedQuotation(String tenderId) {
     // return vendorQuotationAgainstTenderRepository.findVendorsNameWithCompletedStatus(tenderId);
      List<CompletedVendorsDto> allVendors = new ArrayList<>();
      allVendors.addAll(vendorQuotationAgainstTenderRepository.findVendorMasterCompleted(tenderId));
      allVendors.addAll(vendorQuotationAgainstTenderRepository.findGemVendorCompleted(tenderId));
   return allVendors;
  }

    @Override
    public List<AllVendorStatus> getAllVendorStatusOnTenderid(String tenderId) {
        TenderRequest tenderRequest = tenderRepo.findById(tenderId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Tender not found for the provided asset ID.")
                ));
        List<AllVendorStatus> resultList = new ArrayList<>();

        List<String> indentIds = indentRepo.findTenderWithIndent(tenderId);

        List<String> allVendorIds = new ArrayList<>();
        for (String indentId : indentIds) {
            List<String> vendorIds = vRepo.findVendorNamesByIndentId(indentId);
            allVendorIds.addAll(vendorIds);
        }

        Set<String> uniqueVendorIds = new HashSet<>(allVendorIds);

        for (String vendorId : uniqueVendorIds) {
            Optional<VendorQuotationAgainstTender> latestOpt =
                    vendorQuotationAgainstTenderRepository
                            .findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc(tenderId, vendorId);
             Optional<VendorMaster> vm =   vendorMasterRepository.findByVendorId(vendorId);

            AllVendorStatus dto = new AllVendorStatus();
            dto.setTenderId(tenderId);
            dto.setVendorId(vendorId);
            if(vm.isPresent()){
                VendorMaster vendorm = vm.get();
                dto.setVendorName(vendorm.getVendorName());
            }
            if (latestOpt.isPresent()) {
                VendorQuotationAgainstTender latest = latestOpt.get();

                if(latest.getStatus().equalsIgnoreCase("Completed")){
                    dto.setStatus("Qualified");
                    if (tenderRequest.getVendorId() != null
                            && tenderRequest.getVendorId().equalsIgnoreCase(vendorId)){
                        String poRequestId = tenderId.replace("T", "PO");
                        WorkflowTransition wt = workflowTransitionRepository
                                .findTopByRequestIdOrderByWorkflowSequenceDesc(poRequestId);

                        if (wt.getStatus().equalsIgnoreCase("Completed")) {
                            dto.setPo("Generated");
                        } else {
                            dto.setPo("Proposed");
                        }
                    }
                }else if(latest.getStatus().equalsIgnoreCase("Rejected")){
                    dto.setStatus("Disqualified");
                }else {
                    dto.setStatus("In-progress");
                }

            } else {
            }


            resultList.add(dto);
        }

        return resultList;
    }
    @Override
    public VendorStatusDto vendorLogin(VendorLoginRequestDto request) {
        VendorStatusDto dto = new VendorStatusDto();
        dto.setVendorId(request.getVendorId());

        Optional<VendorLoginDetails> vendorLoginOpt = vendorLoginDetailsRepository.findByVendorId(request.getVendorId());
        if (vendorLoginOpt.isEmpty()) {
            dto.setStatus("NOT_FOUND");
            dto.setComments("Vendor ID not found.");
            return dto;
        }

        VendorLoginDetails vl = vendorLoginOpt.get();
         if (!passwordEncoder.matches(request.getPassword(), vl.getPassword())) {
        dto.setStatus("INVALID_CREDENTIALS");
        dto.setComments("Invalid password.");
        return dto;
    }
        // if (!vl.getPassword().equals(request.getPassword())) {
        //     dto.setStatus("INVALID_CREDENTIALS");
        //     dto.setComments("Invalid password.");
        //     return dto;
        // }

         // Generate JWT token
         List<String> roleNames = null;
               String token = jwtUtil.generateToken(
                String.valueOf(request.getVendorId()),
                roleNames,
                "VENDOR"
        );
        dto.setToken(token);

        dto.setEmailStatus(vl.getEmailSent());
        dto.setIsFirstLogin(vl.getIsFirstLogin());
        dto.setIsTempPassword(vl.getIsTempPassword());
        dto.setStatus("SUCCESS");
        dto.setComments("Login successful.");
        return dto;
    }

    @Override
    public List<AllVendorStatus> getAllVendorStatusOnTenderidsForGem(String tenderId) {
        TenderRequest tenderRequest = tenderRepo.findById(tenderId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Tender not found for the provided asset ID.")
                ));
        List<AllVendorStatus> resultList = new ArrayList<>();

       // List<String> indentIds = indentRepo.findTenderWithIndent(tenderId);

       /* List<String> allVendorIds = new ArrayList<>();
        for (String indentId : indentIds) {
            List<String> vendorIds = vRepo.findVendorNamesByIndentId(indentId);
            allVendorIds.addAll(vendorIds);
        }*/
        List<String> allVendorIds = vendorQuotationAgainstTenderRepository.findLatestVendorIdsByTenderId(tenderId);

        Set<String> uniqueVendorIds = new HashSet<>(allVendorIds);

        for (String vendorId : uniqueVendorIds) {
            Optional<VendorQuotationAgainstTender> latestOpt =
                    vendorQuotationAgainstTenderRepository
                            .findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc(tenderId, vendorId);
           // Optional<VendorMaster> vm =   vendorMasterRepository.findByVendorId(vendorId);
             Optional<GemVendorIdTracker> gem = gemVendorIdTrackerRepository.findByGemVendorId(vendorId);

            AllVendorStatus dto = new AllVendorStatus();
            dto.setTenderId(tenderId);
            dto.setVendorId(vendorId);
            if(gem.isPresent()){
                GemVendorIdTracker vendorm = gem.get();
                dto.setVendorName(vendorm.getVendorName());
            }
            if (latestOpt.isPresent()) {
                VendorQuotationAgainstTender latest = latestOpt.get();

                if(latest.getStatus().equalsIgnoreCase("Completed")){
                    dto.setStatus("Qualified");
                    if (tenderRequest.getVendorId() != null
                            && tenderRequest.getVendorId().equalsIgnoreCase(vendorId)){
                        String poRequestId = tenderId.replace("T", "PO");
                        WorkflowTransition wt = workflowTransitionRepository
                                .findTopByRequestIdOrderByWorkflowSequenceDesc(poRequestId);

                        if (wt.getStatus().equalsIgnoreCase("Completed")) {
                            dto.setPo("Generated");
                        } else {
                            dto.setPo("Proposed");
                        }
                    }
                }else if(latest.getStatus().equalsIgnoreCase("Rejected")){
                    dto.setStatus("Disqualified");
                }else {
                    dto.setStatus("In-progress");
                }

            } else {
            }


            resultList.add(dto);
        }

        return resultList;
    }




}

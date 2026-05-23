package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.ApprovalAndRejectionRequestDTO;
import com.astro.dto.workflow.VendorRegiEmailResponseDTO;
import com.astro.dto.workflow.VendorRegistrationRequestDTO;
import com.astro.dto.workflow.VendorRegistrationResponseDTO;
import com.astro.entity.*;
import com.astro.exception.BusinessException;
import com.astro.exception.EmailNotSentException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.*;
import com.astro.service.VendorMasterUtilService;
import com.astro.util.EmailService;
import com.astro.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VendorMasterUtilServiceImpl implements VendorMasterUtilService {

    @Autowired
    private VendorMasterUtilRepository vendorMasterUtilRepository;
    @Autowired
    private UserRoleMasterRepository userRoleMasterRepository;
    @Autowired
    private VendorMasterRepository vendorMasterRepository;
    @Autowired
    private VendorIdSequenceRepository vendorIdSequenceRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VendorLoginDetailsRepository vendorLoginDetailsRepository;
    @Autowired
    private VendorIdTrackerRepository vendorIdTrackerRepository;
    
    @Override
    @Transactional
    public VendorRegiEmailResponseDTO registerVendor(VendorRegistrationRequestDTO dto) {
        VendorMasterUtil vendor = new VendorMasterUtil();

        // Generate vendor ID based on primary business
        String vendorId = generateVendorId(dto.getPrimaryBusiness());
        
        vendor.setVendorId(vendorId);
        vendor.setVendorNumber(extractSequenceNumber(vendorId));
        vendor.setVendorName(dto.getVendorName());
        vendor.setVendorType(dto.getVendorType());
        vendor.setContactNumber(dto.getContactNumber());
        vendor.setEmailAddress(dto.getEmailAddress());
        vendor.setRegisteredPlatform(dto.getRegisteredPlatform());
        vendor.setPfmsVendorCode(dto.getPfmsVendorCode());
        vendor.setPrimaryBusiness(dto.getPrimaryBusiness());
        vendor.setAddress(dto.getAddress());
        vendor.setFaxNumber(dto.getFaxNumber());
        vendor.setPanNumber(dto.getPanNumber());
        vendor.setGstNumber(dto.getGstNumber());
        vendor.setBankName(dto.getBankName());
        vendor.setAccountNumber(dto.getAccountNumber());
        vendor.setIfscCode(dto.getIfscCode());
        vendor.setApprovalStatus(VendorMasterUtil.ApprovalStatus.AWAITING_APPROVAL);
        vendor.setComments(null);
        vendor.setCreatedBy(dto.getCreatedBy());
        vendor.setUpdatedBy(dto.getUpdatedBy());
        vendor.setSwiftCode(dto.getSwiftCode());
        vendor.setAlternateEmailOrPhoneNumber(dto.getAlternateEmailOrPhoneNumber());
        vendor.setBicCode(dto.getBicCode());
        vendor.setIbanAbaNumber(dto.getIbanAbaNumber());
        vendor.setSortCode(dto.getSortCode());
        vendor.setBankRoutingNumber(dto.getBankRoutingNumber());
        vendor.setBankAddress(dto.getBankAddress());
        vendor.setCountry(dto.getCountry());
        vendor.setState(dto.getState());
        vendor.setPlace(dto.getPlace());
        
        VendorMasterUtil vm = vendorMasterUtilRepository.save(vendor);

        String password = PasswordGenerator.generateRandomPassword();

        // Save login details FIRST within the transaction - before sending email
        VendorLoginDetails vendorLoginDetails = new VendorLoginDetails();
        vendorLoginDetails.setVendorId(vm.getVendorId());
        vendorLoginDetails.setEmailAddress(vm.getEmailAddress());
        vendorLoginDetails.setPassword(password);
        vendorLoginDetails.setEmailSent(true); // optimistic: email will be sent after commit
        vendorLoginDetails.setIsFirstLogin(true);
        vendorLoginDetails.setIsTempPassword(true);
        vendorLoginDetails.setPasswordChangedAt(null);
        vendorLoginDetailsRepository.save(vendorLoginDetails);

        // Send email ONLY AFTER the transaction commits - prevents sending email if DB save fails
        final String finalPassword = password;
        final VendorMasterUtil finalVm = vm;
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    emailService.sendEmail(finalVm.getEmailAddress(), finalVm.getVendorId(), finalPassword, finalVm);
                } catch (MessagingException e) {
                    // Email failed - mark as not sent in a new transaction
                    System.err.println("Failed to send registration email for vendor " + finalVm.getVendorId() + ": " + e.getMessage());
                    vendorLoginDetailsRepository.findByVendorId(finalVm.getVendorId()).ifPresent(vl -> {
                        vl.setEmailSent(false);
                        vendorLoginDetailsRepository.save(vl);
                    });
                }
            }
        });

        return mapToRigisterResponse(vendor);
    }

    private String generateVendorId(String primaryBusiness) {
    // Get prefix based on primary business
    String prefix = getCategoryPrefix(primaryBusiness);
    
    // Find or create tracker for this category
    Optional<VendorIdTracker> trackerOpt = vendorIdTrackerRepository.findByPrimaryBusiness(primaryBusiness);
    
    VendorIdTracker tracker;
    int categorySequence;
    
    if (trackerOpt.isPresent()) {
        tracker = trackerOpt.get();
        categorySequence = tracker.getLastSequence() + 1;
        tracker.setLastSequence(categorySequence);
    } else {
        tracker = new VendorIdTracker();
        tracker.setPrimaryBusiness(primaryBusiness);
        tracker.setPrefix(prefix);
        tracker.setLastSequence(1);
        categorySequence = 1;
    }
    
    vendorIdTrackerRepository.save(tracker);
    
    // Format: PREFIX + 3-digit number (e.g., CHEM001, COMP012)
    String vendorId = prefix + String.format("%03d", categorySequence);
    
    // Save COMPLETE vendor ID to vendor_id_sequence table
    VendorIdSequence sequence = new VendorIdSequence();
    sequence.setVendorId(vendorId); // Store "COMP001", "CHEM001", etc.
    vendorIdSequenceRepository.save(sequence);
    
    return vendorId;
}

// private Integer extractSequenceNumber(String vendorId) {
//     // Extract numeric part from vendor ID (e.g., "COMP001" -> 1)
//     String numericPart = vendorId.replaceAll("[^0-9]", "");
//     return numericPart.isEmpty() ? 0 : Integer.parseInt(numericPart);
// }

    private String getCategoryPrefix(String primaryBusiness) {
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("Chemicals", "CHEM");
        prefixMap.put("Computers & Peripherals", "COMP");
        prefixMap.put("Electricals", "ELEC");
        prefixMap.put("Electronics", "ELTC");
        prefixMap.put("Optics", "OPTI");
        prefixMap.put("Fabrication", "FABR");
        prefixMap.put("Furniture", "FURN");
        prefixMap.put("Hardware", "HARD");
        prefixMap.put("Instrument/ Equipment & Machinery", "INST");
        prefixMap.put("Software", "SOFT");
        prefixMap.put("Vehicles", "VEHI");
        prefixMap.put("Stationary", "STAT");
        prefixMap.put("Miscellaneous", "MISC");
        prefixMap.put("Services", "SERV");
        
        return prefixMap.getOrDefault(primaryBusiness, "VNDR");
    }

    private Integer extractSequenceNumber(String vendorId) {
        // Extract numeric part from vendor ID (e.g., "CHEM001" -> 1)
        String numericPart = vendorId.replaceAll("[^0-9]", "");
        return numericPart.isEmpty() ? 0 : Integer.parseInt(numericPart);
    }

    private VendorRegiEmailResponseDTO mapToRigisterResponse(VendorMasterUtil vendor) {
        VendorRegiEmailResponseDTO vendorResponse = new VendorRegiEmailResponseDTO();

        vendorResponse.setVendorId(vendor.getVendorId());
        vendorResponse.setVendorName(vendor.getVendorName());
        vendorResponse.setVendorType(vendor.getVendorType());
        vendorResponse.setContactNumber(vendor.getContactNumber());
        vendorResponse.setEmailAddress(vendor.getEmailAddress());
        vendorResponse.setRegisteredPlatform(vendor.getRegisteredPlatform());
        vendorResponse.setPfmsVendorCode(vendor.getPfmsVendorCode());
        vendorResponse.setPrimaryBusiness(vendor.getPrimaryBusiness());
        vendorResponse.setAddress(vendor.getAddress());
        vendorResponse.setFaxNumber(vendor.getFaxNumber());
        vendorResponse.setPanNumber(vendor.getPanNumber());
        vendorResponse.setGstNumber(vendor.getGstNumber());
        vendorResponse.setBankName(vendor.getBankName());
        vendorResponse.setAccountNumber(vendor.getAccountNumber());
        vendorResponse.setIfscCode(vendor.getIfscCode());
        vendorResponse.setApprovalStatus(vendor.getApprovalStatus().name());
        vendorResponse.setCreatedBy(vendor.getCreatedBy());
        vendorResponse.setUpdatedBy(vendor.getUpdatedBy());
        vendorResponse.setCreatedDate(vendor.getCreatedDate());
        vendorResponse.setUpdatedDate(vendor.getUpdatedDate());
        vendorResponse.setComments(vendor.getComments());
        vendorResponse.setSwiftCode(vendor.getSwiftCode());
        vendorResponse.setAlternateEmailOrPhoneNumber(vendor.getAlternateEmailOrPhoneNumber());
        vendorResponse.setBicCode(vendor.getBicCode());
        vendorResponse.setIbanAbaNumber(vendor.getIbanAbaNumber());
        vendorResponse.setSortCode(vendor.getSortCode());
        vendorResponse.setBankRoutingNumber(vendor.getBankRoutingNumber());
        vendorResponse.setBankAddress(vendor.getBankAddress());
        vendorResponse.setCountry(vendor.getCountry());
        vendorResponse.setState(vendor.getState());
        vendorResponse.setPlace(vendor.getPlace());
        
        Optional<VendorLoginDetails> vendorLogin = vendorLoginDetailsRepository.findByVendorId(vendor.getVendorId());
        if (vendorLogin.isPresent()) {
            VendorLoginDetails vl = vendorLogin.get();
            vendorResponse.setEmailStatus(vl.getEmailSent());
        }

        return vendorResponse;
    }

    @Override
    public List<VendorRegistrationResponseDTO> getAllAwaitingApprovalVendors() {
        List<VendorMasterUtil> vendors = vendorMasterUtilRepository.findByApprovalStatus(VendorMasterUtil.ApprovalStatus.AWAITING_APPROVAL);
        return vendors.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private VendorRegistrationResponseDTO mapToResponse(VendorMasterUtil vendor) {
        VendorRegistrationResponseDTO vendorResponse = new VendorRegistrationResponseDTO();

        vendorResponse.setVendorId(vendor.getVendorId());
        vendorResponse.setVendorName(vendor.getVendorName());
        vendorResponse.setVendorType(vendor.getVendorType());
        vendorResponse.setContactNumber(vendor.getContactNumber());
        vendorResponse.setEmailAddress(vendor.getEmailAddress());
        vendorResponse.setRegisteredPlatform(vendor.getRegisteredPlatform());
        vendorResponse.setPfmsVendorCode(vendor.getPfmsVendorCode());
        vendorResponse.setPrimaryBusiness(vendor.getPrimaryBusiness());
        vendorResponse.setAddress(vendor.getAddress());
        vendorResponse.setFaxNumber(vendor.getFaxNumber());
        vendorResponse.setPanNumber(vendor.getPanNumber());
        vendorResponse.setGstNumber(vendor.getGstNumber());
        vendorResponse.setBankName(vendor.getBankName());
        vendorResponse.setAccountNumber(vendor.getAccountNumber());
        vendorResponse.setIfscCode(vendor.getIfscCode());
        vendorResponse.setApprovalStatus(vendor.getApprovalStatus().name());
        vendorResponse.setCreatedBy(vendor.getCreatedBy());
        vendorResponse.setUpdatedBy(vendor.getUpdatedBy());
        vendorResponse.setCreatedDate(vendor.getCreatedDate());
        vendorResponse.setUpdatedDate(vendor.getUpdatedDate());
        vendorResponse.setComments(vendor.getComments());
        vendorResponse.setSwiftCode(vendor.getSwiftCode());
        vendorResponse.setAlternateEmailOrPhoneNumber(vendor.getAlternateEmailOrPhoneNumber());
        vendorResponse.setBicCode(vendor.getBicCode());
        vendorResponse.setIbanAbaNumber(vendor.getIbanAbaNumber());
        vendorResponse.setSortCode(vendor.getSortCode());
        vendorResponse.setBankRoutingNumber(vendor.getBankRoutingNumber());
        vendorResponse.setBankAddress(vendor.getBankAddress());
        vendorResponse.setCountry(vendor.getCountry());
        vendorResponse.setState(vendor.getState());
        vendorResponse.setPlace(vendor.getPlace());
        return vendorResponse;
    }

    @Override
    public String performAction(ApprovalAndRejectionRequestDTO request) {
        UserRoleMaster userRoleMaster = userRoleMasterRepository.findByRoleIdAndUserId(11, request.getActionBy());

        if (Objects.isNull(userRoleMaster)) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.UNAUTHORIZED_ACTION,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Unauthorized user"
            ));
        }

        VendorMasterUtil vendor = vendorMasterUtilRepository
                .findById(request.getRequestId())
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Vendor ID not found!"
                )));

        if ("APPROVED".equalsIgnoreCase(request.getAction())) {
            int actionBy = request.getActionBy();
            String remarks = request.getRemarks();
            return approveVendor(vendor, actionBy, remarks);
        } else if ("REJECTED".equalsIgnoreCase(request.getAction())) {
            return rejectVendor(vendor, request.getRemarks());
        } else if ("CHANGE REQUEST".equalsIgnoreCase(request.getAction())) {
            return changeRequestVendor(vendor, request.getRemarks());
        } else {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.INVALID_ACTION,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Invalid action. Use 'APPROVED' or 'REJECTED'."
            ));
        }
    }

    @Override
    public String performAllAction(List<ApprovalAndRejectionRequestDTO> request) {
        String response = null;
        for (ApprovalAndRejectionRequestDTO dto : request) {
            response = performAction(dto);
        }
        return response;
    }

    @Override
    public VendorRegistrationResponseDTO getVendorMasterUtilById(String vendorId) {
        VendorMasterUtil vendorMasterUtil = vendorMasterUtilRepository.findById(vendorId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Vendor master Util not found for the provided vendor id.")
                ));
        return mapToResponse(vendorMasterUtil);
    }

    private String changeRequestVendor(VendorMasterUtil vendor, String remarks) {
        vendor.setApprovalStatus(VendorMasterUtil.ApprovalStatus.CHANGE_REQUEST);
        vendor.setComments(remarks);
        vendorMasterUtilRepository.save(vendor);
        return "Vendor " + vendor.getVendorId() + " Change Requested.";
    }

    private String approveVendor(VendorMasterUtil vendor, int actionBy, String remarks) {
        VendorMaster newVendor = new VendorMaster();
        newVendor.setVendorId(vendor.getVendorId());
        newVendor.setVendorType(vendor.getVendorType());
        newVendor.setVendorName(vendor.getVendorName());
        newVendor.setContactNo(vendor.getContactNumber());
        newVendor.setEmailAddress(vendor.getEmailAddress());
        newVendor.setRegisteredPlatform(vendor.getRegisteredPlatform());
        newVendor.setPfmsVendorCode(vendor.getPfmsVendorCode());
        newVendor.setPrimaryBusiness(vendor.getPrimaryBusiness());
        newVendor.setAddress(vendor.getAddress());
        newVendor.setFax(vendor.getFaxNumber());
        newVendor.setPanNo(vendor.getPanNumber());
        newVendor.setGstNo(vendor.getGstNumber());
        newVendor.setBankName(vendor.getBankName());
        newVendor.setAccountNo(vendor.getAccountNumber());
        newVendor.setIfscCode(vendor.getIfscCode());
        newVendor.setPurchaseHistory(null);
        newVendor.setSwiftCode(vendor.getSwiftCode());
        newVendor.setAlternateEmailOrPhoneNumber(vendor.getAlternateEmailOrPhoneNumber());
        newVendor.setBicCode(vendor.getBicCode());
        newVendor.setIbanAbaNumber(vendor.getIbanAbaNumber());
        newVendor.setSortCode(vendor.getSortCode());
        newVendor.setBankRoutingNumber(vendor.getBankRoutingNumber());
        newVendor.setBankAddress(vendor.getBankAddress());
        newVendor.setCountry(vendor.getCountry());
        newVendor.setState(vendor.getState());
        newVendor.setPlace(vendor.getPlace());
        newVendor.setStatus("APPROVED");
        newVendor.setCreatedBy(String.valueOf(actionBy));
        newVendor.setRemarks(remarks);

        vendorMasterRepository.save(newVendor);
        vendorMasterUtilRepository.deleteById(vendor.getVendorId());
        return "Vendor " + vendor.getVendorId() + " has been APPROVED and added the vendor data to vendor master, deleted from vendor master util.";
    }

    private String rejectVendor(VendorMasterUtil vendor, String remarks) {
        vendor.setApprovalStatus(VendorMasterUtil.ApprovalStatus.REJECTED);
        vendor.setComments(remarks);
        vendorMasterUtilRepository.save(vendor);
        return "Vendor " + vendor.getVendorId() + " has been REJECTED.";
    }
}
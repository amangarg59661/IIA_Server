package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.*;
import com.astro.dto.workflow.ProcurementDtos.AllVendorStatus;
import com.astro.dto.workflow.ProcurementDtos.approvedTenderIdWithTitle;
import com.astro.entity.ProcurementModule.IndentId;
import com.astro.entity.ProcurementModule.PurchaseOrder;
import com.astro.entity.ProcurementModule.TenderRequest;
import com.astro.entity.VendorMaster;
import com.astro.entity.VendorNamesForJobWorkMaterial;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.entity.VendorQuotationAgainstTender;
import com.astro.repository.ProcurementModule.IndentIdRepository;
import com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.repository.VendorMasterRepository;
import com.astro.repository.VendorNamesForJobWorkMaterialRepository;
import com.astro.repository.VendorQuotationAgainstTenderRepository;
import com.astro.repository.WorkflowTransitionRepository;
import com.astro.service.VendorMasterService;
import com.astro.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VendorMasterServiceImpl implements VendorMasterService {

    @Autowired
    private VendorMasterRepository vendorMasterRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private TenderRequestRepository tenderRequestRepository;

    @Autowired
    private IndentIdRepository indentIdRepository;
    @Autowired
    private VendorNamesForJobWorkMaterialRepository vendorNamesForJobWorkMaterialRepository;

    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;

    @Autowired
    private VendorQuotationAgainstTenderRepository vendorQuotationAgainstTenderRepository;

    @Override
    public VendorMasterResponseDto createVendorMaster(VendorMasterRequestDto vendorMasterRequestDto) {

        // Check if the indentorId already exists
        if (vendorMasterRepository.existsById(vendorMasterRequestDto.getVendorId())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate vendor id", "vendor id" + vendorMasterRequestDto.getVendorId() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }

        VendorMaster vendorMaster = new VendorMaster();
        vendorMaster.setVendorId(vendorMasterRequestDto.getVendorId());
        vendorMaster.setVendorType(vendorMasterRequestDto.getVendorType());
        vendorMaster.setVendorName(vendorMasterRequestDto.getVendorName());
        vendorMaster.setContactNo(vendorMasterRequestDto.getContactNo());
        vendorMaster.setEmailAddress(vendorMasterRequestDto.getEmailAddress());
        vendorMaster.setRegisteredPlatform(false);
        vendorMaster.setPfmsVendorCode(vendorMasterRequestDto.getPfmsVendorCode());
        vendorMaster.setPrimaryBusiness(vendorMasterRequestDto.getPrimaryBusiness());
        vendorMaster.setAddress(vendorMasterRequestDto.getAddress());
      //  vendorMaster.setLandline(vendorMasterRequestDto.getLandline());
      //  vendorMaster.setMobileNo(vendorMasterRequestDto.getMobileNo());
        vendorMaster.setFax(vendorMasterRequestDto.getFax());
        vendorMaster.setPanNo(vendorMasterRequestDto.getPanNo());
        vendorMaster.setGstNo(vendorMasterRequestDto.getGstNo());
        vendorMaster.setBankName(vendorMasterRequestDto.getBankName());
        vendorMaster.setAccountNo(vendorMasterRequestDto.getAccountNo());
        vendorMaster.setIfscCode(vendorMasterRequestDto.getIfscCode());
        vendorMaster.setPurchaseHistory(vendorMasterRequestDto.getPurchaseHistory());
        vendorMaster.setStatus(AppConstant.PENDING_TYPE);
        vendorMaster.setCreatedBy(vendorMasterRequestDto.getCreatedBy());
        vendorMaster.setUpdatedBy(vendorMasterRequestDto.getUpdatedBy());
        vendorMaster.setSwiftCode(vendorMasterRequestDto.getSwiftCode());
        vendorMaster.setAlternateEmailOrPhoneNumber(vendorMasterRequestDto.getAlternateEmailOrPhoneNumber());
        vendorMaster.setBicCode(vendorMasterRequestDto.getBicCode());
        vendorMaster.setIbanAbaNumber(vendorMasterRequestDto.getIbanAbaNumber());
        vendorMaster.setSortCode(vendorMasterRequestDto.getSortCode());
        vendorMaster.setBankRoutingNumber(vendorMasterRequestDto.getBankRoutingNumber());
        vendorMaster.setBankAddress(vendorMasterRequestDto.getBankAddress());
        vendorMaster.setCountry(vendorMasterRequestDto.getCountry());
        vendorMaster.setState(vendorMasterRequestDto.getState());
        vendorMaster.setPlace(vendorMaster.getPlace());

        vendorMasterRepository.save(vendorMaster);

        return mapToResponseDTO(vendorMaster);

    }



    @Override
    public VendorMasterResponseDto updateVendorMaster(String vendorId, VendorMasterRequestDto vendorMasterRequestDto) {

        VendorMaster vendorMaster = vendorMasterRepository.findById(vendorId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "vendor master not found for the provided vendor id.")
                ));

        vendorMaster.setVendorId(vendorMasterRequestDto.getVendorId());
        vendorMaster.setVendorType(vendorMasterRequestDto.getVendorType());
        vendorMaster.setVendorName(vendorMasterRequestDto.getVendorName());
        vendorMaster.setContactNo(vendorMasterRequestDto.getContactNo());
        vendorMaster.setEmailAddress(vendorMasterRequestDto.getEmailAddress());
        vendorMaster.setRegisteredPlatform(vendorMasterRequestDto.getRegisteredPlatform());
        vendorMaster.setPfmsVendorCode(vendorMasterRequestDto.getPfmsVendorCode());
        vendorMaster.setPrimaryBusiness(vendorMasterRequestDto.getPrimaryBusiness());
        vendorMaster.setAddress(vendorMasterRequestDto.getAddress());
       // vendorMaster.setLandline(vendorMasterRequestDto.getLandline());
      //  vendorMaster.setMobileNo(vendorMasterRequestDto.getMobileNo());
        vendorMaster.setFax(vendorMasterRequestDto.getFax());
        vendorMaster.setPanNo(vendorMasterRequestDto.getPanNo());
        vendorMaster.setGstNo(vendorMasterRequestDto.getGstNo());
        vendorMaster.setBankName(vendorMasterRequestDto.getBankName());
        vendorMaster.setAccountNo(vendorMasterRequestDto.getAccountNo());
        vendorMaster.setIfscCode(vendorMasterRequestDto.getIfscCode());
        vendorMaster.setPurchaseHistory(vendorMasterRequestDto.getPurchaseHistory());
        vendorMaster.setStatus(vendorMasterRequestDto.getStatus());
        vendorMaster.setCreatedBy(vendorMasterRequestDto.getCreatedBy());
        vendorMaster.setUpdatedBy(vendorMasterRequestDto.getUpdatedBy());
        vendorMaster.setSwiftCode(vendorMasterRequestDto.getSwiftCode());
        vendorMaster.setAlternateEmailOrPhoneNumber(vendorMasterRequestDto.getAlternateEmailOrPhoneNumber());
        vendorMaster.setBicCode(vendorMasterRequestDto.getBicCode());
        vendorMaster.setIbanAbaNumber(vendorMasterRequestDto.getIbanAbaNumber());
        vendorMaster.setSortCode(vendorMasterRequestDto.getSortCode());
        vendorMaster.setBankRoutingNumber(vendorMasterRequestDto.getBankRoutingNumber());
        vendorMaster.setBankAddress(vendorMasterRequestDto.getBankAddress());
        vendorMaster.setCountry(vendorMasterRequestDto.getCountry());
        vendorMaster.setState(vendorMasterRequestDto.getState());
        vendorMaster.setPlace(vendorMasterRequestDto.getPlace());
        vendorMasterRepository.save(vendorMaster);

        return mapToResponseDTO(vendorMaster);
    }

    @Override
    public List<VendorMasterResponseDto> getAllVendorMasters() {
       // List<VendorMaster> vendorMasters= vendorMasterRepository.findAll();
        List<VendorMaster> vendorMasters = vendorMasterRepository.findAllActiveVendors();
        return vendorMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());

    }

    @Override
    public VendorMasterResponseDto getVendorMasterById(String vendorId) {
       VendorMaster vendorMaster= vendorMasterRepository.findById(vendorId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Vendor master not found for the provided vendor id.")
                ));
        return mapToResponseDTO(vendorMaster);
    }

    @Override
    public void deleteVendorMaster(String vendorId) {


        VendorMaster vendorMaster=vendorMasterRepository.findById(vendorId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Vendor master not found for the provided Vendor ID."
                        )
                ));
        try {
            vendorMasterRepository.delete(vendorMaster);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the Vendor master."
                    ),
                    ex
            );
        }

    }

    @Override
    public List<VendorMasterResponseDto> getAllNotApprovedVendors() {
        return getAllVendorMasters().stream()
                .filter(vendor -> "Pending".equalsIgnoreCase(vendor.getStatus()))
                .collect(Collectors.toList());
    }



    @Override
    public RegisteredVendorsDataDto getVendorPurchaseOrders(String tenderId) {


      //  List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findByVendorId(vendorId);
        PurchaseOrder  po =  purchaseOrderRepository.findByTenderId(tenderId);

            Optional<TenderRequest> tender= tenderRequestRepository.findByTenderId(tenderId);
            TenderRequest tr= tender.get();
            RegisteredVendorsDataDto dto = new RegisteredVendorsDataDto();
            dto.setPurchaseOrder(po.getPoId());
            dto.setTenderNumber(po.getTenderId());
            dto.setDeliveryAndAcceptanceStatus("null");
            dto.setPaymentStatus("null");
            dto.setPaymentUTRNumber("null");
            dto.setTenderRequestCopy("null");
            dto.setPoCopy("null");
          //  dto.setUploadTenderDocumentsFileName(tr.getUploadTenderDocumentsFileName());
           // dto.setUploadSpecificTermsAndConditionsFileName(tr.getUploadSpecificTermsAndConditionsFileName());
          //  dto.setUploadGeneralTermsAndConditionsFileName(dto.getUploadGeneralTermsAndConditionsFileName());

          //  result.add(dto);
      //  }

        return dto;
    }

    @Override
    public List<approvedTenderIdWithTitle> getTenderIds(String vendorId) {

        List<VendorNamesForJobWorkMaterial> vendorMaterials =
                vendorNamesForJobWorkMaterialRepository.findByVendorName(vendorId);
        List<String> indentIds = vendorMaterials.stream()
                .map(VendorNamesForJobWorkMaterial::getIndentId)
                .distinct()
                .collect(Collectors.toList());
        List<IndentId> indentEntities = indentIdRepository.findByIndentIdIn(indentIds);
        List<String> approvedTenderIds = workflowTransitionRepository.findApprovedTenderIds();
        List<String> tenderIds = indentEntities.stream()
                .map(indent -> indent.getTenderRequest().getTenderId())
                .filter(approvedTenderIds::contains)
                .distinct()
                .collect(Collectors.toList());
        List<TenderRequest> tenderRequests = tenderRequestRepository.findByTenderIdIn(tenderIds);

        // Convert to Map for quick lookup
        Map<String, TenderRequest> tenderRequestMap = tenderRequests.stream()
                .collect(Collectors.toMap(TenderRequest::getTenderId, tr -> tr));

        List<approvedTenderIdWithTitle> resultList = new ArrayList<>();
        for (String tenderId : tenderIds) {
            TenderRequest tenderRequest = tenderRequestMap.get(tenderId);
            if (tenderRequest != null) {
                approvedTenderIdWithTitle approved = new approvedTenderIdWithTitle();
                approved.setTenderId(tenderId);
                approved.setTitle(tenderRequest.getTitleOfTender());

                // Populate vendor's current submission status for this tender
                Optional<VendorQuotationAgainstTender> latestOpt =
                        vendorQuotationAgainstTenderRepository
                                .findTopByTenderIdAndVendorIdAndIsLatestTrueOrderByVersionDesc(tenderId, vendorId);
                if (latestOpt.isPresent()) {
                    approved.setActionStatus(latestOpt.get().getStatus());
                } else {
                    approved.setActionStatus(null); // PENDING — no submission yet
                }

                resultList.add(approved);
            }
        }

        return resultList;
    }


    private VendorMasterResponseDto mapToResponseDTO(VendorMaster vendorMaster) {

        VendorMasterResponseDto responseDto = new VendorMasterResponseDto();

        responseDto.setVendorId(vendorMaster.getVendorId());
        responseDto.setVendorType(vendorMaster.getVendorType());
        responseDto.setVendorName(vendorMaster.getVendorName());
        responseDto.setContactNo(vendorMaster.getContactNo());
        responseDto.setEmailAddress(vendorMaster.getEmailAddress());
        responseDto.setRegisteredPlatform(vendorMaster.getRegisteredPlatform());
        responseDto.setPfmsVendorCode(vendorMaster.getPfmsVendorCode());
        responseDto.setPrimaryBusiness(vendorMaster.getPrimaryBusiness());
        responseDto.setAddress(vendorMaster.getAddress());
       // responseDto.setLandline(vendorMaster.getLandline());
     //   responseDto.setMobileNo(vendorMaster.getMobileNo());
        responseDto.setFax(vendorMaster.getFax());
        responseDto.setPanNo(vendorMaster.getPanNo());
        responseDto.setGstNo(vendorMaster.getGstNo());
        responseDto.setBankName(vendorMaster.getBankName());
        responseDto.setAccountNo(vendorMaster.getAccountNo());
        responseDto.setIfscCode(vendorMaster.getIfscCode());
        responseDto.setPurchaseHistory(vendorMaster.getPurchaseHistory());
        responseDto.setStatus(vendorMaster.getStatus());
        responseDto.setRemarks(vendorMaster.getRemarks());
        responseDto.setCreatedBy(vendorMaster.getCreatedBy());
        responseDto.setUpdatedBy(vendorMaster.getUpdatedBy());
        responseDto.setCreatedDate(vendorMaster.getCreatedDate());
        responseDto.setUpdatedDate(vendorMaster.getUpdatedDate());
        vendorMaster.setSwiftCode(vendorMaster.getSwiftCode());
        vendorMaster.setAlternateEmailOrPhoneNumber(vendorMaster.getAlternateEmailOrPhoneNumber());
        vendorMaster.setBicCode(vendorMaster.getBicCode());
        vendorMaster.setIbanAbaNumber(vendorMaster.getIbanAbaNumber());
        vendorMaster.setSortCode(vendorMaster.getSortCode());
        vendorMaster.setBankRoutingNumber(vendorMaster.getBankRoutingNumber());
        vendorMaster.setBankAddress(vendorMaster.getBankAddress());
        vendorMaster.setCountry(vendorMaster.getCountry());
        vendorMaster.setState(vendorMaster.getState());
        vendorMaster.setPlace(vendorMaster.getPlace());


        return responseDto;

    }
    public boolean checkPanExists(String panNumber) {
        return vendorMasterRepository.existsByPanNoIgnoreCase(panNumber);
    }

    public boolean checkEmailExistsForInternational(String email) {
        return vendorMasterRepository.existsByEmailAddressIgnoreCaseAndVendorType(email, "International");
    }

    @Override
    public List<VendorIdNameDTO> getAllVendorIdAndName() {

        return vendorMasterRepository.findAllVendorIdAndName();
    }
    public VendorMaster getVendorByVendorId(String vendorId) {
        return vendorMasterRepository.findById(vendorId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Vendor master not found for the provided vendor id."
                        )
                ));
    }

    @Override
    public VendorMaster updateVendor(String vendorId, VendorMasterUpdateDto dto){
        VendorMaster vendor = vendorMasterRepository.findById(vendorId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Vendor master not found for the provided vendor id."
                        )
                ));
        // Map DTO fields to entity
        vendor.setVendorName(dto.getVendorName());
        vendor.setVendorType(dto.getVendorType());
        vendor.setContactNo(dto.getContactNo());
        vendor.setEmailAddress(dto.getEmailAddress());
        vendor.setRegisteredPlatform(dto.getRegisteredPlatform());
        vendor.setPfmsVendorCode(dto.getPfmsVendorCode());
        vendor.setPrimaryBusiness(dto.getPrimaryBusiness());
        vendor.setAddress(dto.getAddress());
        vendor.setAlternateEmailOrPhoneNumber(dto.getAlternateEmailOrPhoneNumber());
        vendor.setPanNo(dto.getPanNo());
        vendor.setGstNo(dto.getGstNo());
        vendor.setBankName(dto.getBankName());
        vendor.setAccountNo(dto.getAccountNo());
        vendor.setIfscCode(dto.getIfscCode());
        vendor.setPurchaseHistory(dto.getPurchaseHistory());
        vendor.setSwiftCode(dto.getSwiftCode());
        vendor.setBicCode(dto.getBicCode());
        vendor.setIbanAbaNumber(dto.getIbanAbaNumber());
        vendor.setSortCode(dto.getSortCode());
        vendor.setBankRoutingNumber(dto.getBankRoutingNumber());
        vendor.setBankAddress(dto.getBankAddress());
        vendor.setCountry(dto.getCountry());
        vendor.setState(dto.getState());
        vendor.setPlace(dto.getPlace());
        vendor.setStatusOfVendorActiveOrDebar(dto.getStatus());
        vendor.setUpdatedBy(dto.getUpdatedBy());
        vendor.setUpdatedDate(java.time.LocalDateTime.now());

        vendor.setReasonForDebar(dto.getReasonForDebar());


        return vendorMasterRepository.save(vendor);
    }


}

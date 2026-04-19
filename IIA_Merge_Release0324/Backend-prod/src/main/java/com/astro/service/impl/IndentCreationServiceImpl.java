package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.AssignEmployeeToIndentDto;
import com.astro.dto.workflow.ProcurementDtos.IndentDto.*;
import com.astro.dto.workflow.ProcurementDtos.IndentWorkflowStatusDto;
import com.astro.dto.workflow.ProcurementDtos.TechnoMomReportDTO;

import com.astro.entity.*;
import com.astro.entity.ProcurementModule.IndentCreation;
import com.astro.entity.ProcurementModule.JobDetails;
import com.astro.entity.ProcurementModule.MaterialDetails;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;

import com.astro.repository.*;
import com.astro.repository.ProcurementModule.IndentCreation.IndentCreationRepository;
import com.astro.repository.ProcurementModule.IndentCreation.IndentMaterialMappingRepository;
import com.astro.repository.ProcurementModule.IndentCreation.JobDetailsRepository;
import com.astro.repository.ProcurementModule.IndentCreation.MaterialDetailsRepository;
import com.astro.service.IndentCreationService;
import com.astro.util.CommonUtils;
import com.astro.util.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class IndentCreationServiceImpl implements IndentCreationService {

    @Autowired
    private IndentCreationRepository indentCreationRepository;

    @Autowired
    private MaterialDetailsRepository materialDetailsRepository;

    @Autowired
    private JobDetailsRepository jobDetailsRepository;

    @Autowired
    private IndentMaterialMappingRepository indentMaterialMappingRepository;

    @Autowired
    private ProjectMasterRepository projectMasterRepository;

    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;

    @Autowired
    private VendorNamesForJobWorkMaterialRepository vendorNameRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmployeeDepartmentMasterRepository employeeDepartmentMasterRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Autowired
    private VendorMasterRepository vendorMasterRepository;

    @Autowired
    private DepartmentComputerPriceLimitRepository departmentComputerPriceLimitRepository;

    @Autowired
    private com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderAttributesRepository purchaseOrderAttributesRepository;

    @Autowired
    private com.astro.repository.ProcurementModule.IndentCancellationRequestRepository indentCancellationRequestRepository;

    @Autowired
    private com.astro.repository.ProcurementModule.IndentIdRepository indentIdRepository;

    @Autowired
    private com.astro.repository.ProcurementModule.PurchaseOrder.PurchaseOrderRepository purchaseOrderRepository;

    // added by abhinav
    @Autowired
    private IndentAssignmentRepository indentAssignmentRepository;

    @Value("${filePath}")
    private String bp;
    private final String basePath;

    public IndentCreationServiceImpl(@Value("${filePath}") String bp) {
        this.basePath = bp + "/Indent";
    }

    @Transactional
    public IndentCreationResponseDTO createIndent(IndentCreationRequestDTO indentRequestDTO) {

        // Determine indent type (default to material if not specified)
        String indentType = indentRequestDTO.getIndentType();
        if (indentType == null || indentType.isEmpty()) {
            indentType = "material";
        }

        // Validation based on indent type
        String materialCategory = null;
        if ("material".equalsIgnoreCase(indentType) && indentRequestDTO.getMaterialDetails() != null) {
            for (MaterialDetailsRequestDTO materialRequest : indentRequestDTO.getMaterialDetails()) {
                if (materialCategory == null) {
                    materialCategory = materialRequest.getMaterialCategory();
                } else if (!materialCategory.equals(materialRequest.getMaterialCategory())) {
                    throw new InvalidInputException(new ErrorDetails(400, 2, "Inconsistent Material Category",
                            "All materials must have the same material category."));
                }
            }
        }

        Integer maxNumber = indentCreationRepository.findMaxIndentNumber();
        int nextNumber = (maxNumber == null) ? 1001 : maxNumber + 1;

        String indentId = "IND" + nextNumber;
        System.out.println("IndentId" + indentId);

        IndentCreation indentCreation = new IndentCreation();
        indentCreation.setIndentorName(indentRequestDTO.getIndentorName());
        indentCreation.setIndentId(indentId);
        indentCreation.setIndentNumber(nextNumber);
        indentCreation.setIndentorMobileNo(indentRequestDTO.getIndentorMobileNo());
        indentCreation.setIndentorEmailAddress(indentRequestDTO.getIndentorEmailAddress());
        indentCreation.setConsignesLocation(indentRequestDTO.getConsignesLocation());
        indentCreation.setProjectName(indentRequestDTO.getProjectName());
        indentCreation.setIsPreBitMeetingRequired(indentRequestDTO.getIsPreBidMeetingRequired());

        String Date = indentRequestDTO.getPreBidMeetingDate();
        if (Date != null) {
            indentCreation.setPreBidMeetingDate(CommonUtils.convertStringToDateObject(Date));
        } else {
            indentCreation.setPreBidMeetingDate(null);
        }

        indentCreation.setPreBidMeetingVenue(indentRequestDTO.getPreBidMeetingVenue());
        indentCreation.setIsItARateContractIndent(indentRequestDTO.getIsItARateContractIndent());
        indentCreation.setEstimatedRate(indentRequestDTO.getEstimatedRate());
        indentCreation.setPeriodOfContract(indentRequestDTO.getPeriodOfContract());

        // NEW: Handle multiple job codes for rate contract
        // Convert List<String> to comma-separated string for storage
        if (indentRequestDTO.getRateContractJobCodes() != null && !indentRequestDTO.getRateContractJobCodes().isEmpty()) {
            String jobCodesStr = String.join(",", indentRequestDTO.getRateContractJobCodes());
            indentCreation.setRateContractJobCodes(jobCodesStr);
        } else {
            indentCreation.setRateContractJobCodes(null);
        }

        indentCreation.setFileType(indentRequestDTO.getFileType());
        indentCreation.setEmployeeDepartment(indentRequestDTO.getEmployeeDepartment());
        indentCreation.setBuyBackAmount(indentRequestDTO.getBuyBackAmount());
        indentCreation.setProprietaryAndLimitedDeclaration(indentRequestDTO.getProprietaryAndLimitedDeclaration());

        // Handle file uploads
        if (indentRequestDTO.getUploadingPriorApprovalsFileName() == null || indentRequestDTO.getUploadingPriorApprovalsFileName().isEmpty()) {
            indentCreation.setUploadingPriorApprovalsFileName(null);
        } else {
            String prior = saveBase64Files(indentRequestDTO.getUploadingPriorApprovalsFileName(), basePath);
            indentCreation.setUploadingPriorApprovalsFileName(prior);
        }

        if (indentRequestDTO.getTechnicalSpecificationsFileName() == null || indentRequestDTO.getTechnicalSpecificationsFileName().isEmpty()) {
            indentCreation.setTechnicalSpecificationsFileName(null);
        } else {
            String technical = saveBase64Files(indentRequestDTO.getTechnicalSpecificationsFileName(), basePath);
            indentCreation.setTechnicalSpecificationsFileName(technical);
        }

        if (indentRequestDTO.getDraftEOIOrRFPFileName() == null || indentRequestDTO.getDraftEOIOrRFPFileName().isEmpty()) {
            indentCreation.setDraftEOIOrRFPFileName(null);
        } else {
            String draft = saveBase64Files(indentRequestDTO.getDraftEOIOrRFPFileName(), basePath);
            indentCreation.setDraftEOIOrRFPFileName(draft);
        }

        if (indentRequestDTO.getUploadPACOrBrandPACFileName() == null || indentRequestDTO.getUploadPACOrBrandPACFileName().isEmpty()) {
            indentCreation.setUploadPACOrBrandPACFileName(null);
        } else {
            String pac = saveBase64Files(indentRequestDTO.getUploadPACOrBrandPACFileName(), basePath);
            indentCreation.setUploadPACOrBrandPACFileName(pac);
        }

        indentCreation.setBrandPac(indentRequestDTO.getBrandPac());
        indentCreation.setJustification(indentRequestDTO.getJustification());
        indentCreation.setBrandAndModel(indentRequestDTO.getBrandAndModel());
        indentCreation.setPurpose(indentRequestDTO.getPurpose());
        indentCreation.setQuarter(indentRequestDTO.getQuarter());
        indentCreation.setProprietaryJustification(indentRequestDTO.getProprietaryJustification());
        indentCreation.setBuyBack(indentRequestDTO.getBuyBack());

        if (indentRequestDTO.getUploadBuyBackFileNames() == null || indentRequestDTO.getUploadBuyBackFileNames().isEmpty()) {
            indentCreation.setUploadBuyBackFileNames(null);
        } else {
            String buy = saveBase64Files(indentRequestDTO.getUploadBuyBackFileNames(), basePath);
            indentCreation.setUploadBuyBackFileNames(buy);
        }

        indentCreation.setSerialNumber(indentRequestDTO.getSerialNumber());
        indentCreation.setModelNumber(indentRequestDTO.getModelNumber());

        String dateOfPurchase = indentRequestDTO.getDateOfPurchase();
        if (dateOfPurchase != null) {
            indentCreation.setDateOfPurchase(CommonUtils.convertStringToDateObject(dateOfPurchase));
        } else {
            indentCreation.setDateOfPurchase(null);
        }

        indentCreation.setReason(indentRequestDTO.getReason());
        indentCreation.setCreatedBy(indentRequestDTO.getCreatedBy());
        indentCreation.setUpdatedBy(indentRequestDTO.getUpdatedBy());

        // Resolve indentor's department from the employee master linked to the creating user.
        // This is used by the workflow engine for department-based approver routing (Dean/Head SEG).
        if (indentRequestDTO.getCreatedBy() != null) {
            try {
                UserMaster creatingUser = userMasterRepository.findByUserId(indentRequestDTO.getCreatedBy());
                if (creatingUser != null && creatingUser.getEmployeeId() != null) {
                    employeeDepartmentMasterRepository.findByEmployeeId(creatingUser.getEmployeeId())
                            .ifPresent(emp -> {
                                indentCreation.setIndentorDepartment(emp.getDepartmentName());
                                System.out.println("✅ indentorDepartment set from employee: "
                                        + emp.getDepartmentName() + " (empId=" + emp.getEmployeeId() + ")");
                            });
                }
            } catch (Exception e) {
                System.err.println("⚠️ Could not resolve indentorDepartment: " + e.getMessage());
            }
        }

        // Set indent type and material category type on entity
        indentCreation.setIndentType(indentType);
        indentCreation.setMaterialCategoryType(indentRequestDTO.getMaterialCategoryType());

        // Set project-related fields for workflow branch matching
        indentCreation.setIsUnderProject(indentRequestDTO.getIsUnderProject() != null ? indentRequestDTO.getIsUnderProject() : false);
        indentCreation.setProjectCode(indentRequestDTO.getProjectCode());
        indentCreation.setModeOfProcurement(indentRequestDTO.getModeOfProcurement());

        // Bug Fix: Initialize new fields for indent tracking
        indentCreation.setIsEditable(true);
        indentCreation.setIsLockedForTender(false);
        indentCreation.setVersion(1);
        indentCreation.setCurrentStatus("DRAFT");
        indentCreation.setCurrentStage("INDENT_CREATION");
        indentCreation.setApprovalLevel(0);

        BigDecimal totalIndentPrice = BigDecimal.ZERO;

        // Process based on indent type
        if ("material".equalsIgnoreCase(indentType)) {

            // Validate vendor count based on mode of procurement
            validateVendorCountByModeOfProcurement(indentRequestDTO.getModeOfProcurement(), indentRequestDTO.getMaterialDetails());

            // Validate computer item prices for department-specific limits
            if (indentRequestDTO.getEmployeeDepartment() != null && !indentRequestDTO.getEmployeeDepartment().isEmpty()) {
                validateComputerItemPrices(indentRequestDTO.getMaterialDetails(), indentRequestDTO.getEmployeeDepartment());
            }

            // Save MaterialDetails entities and link them to the indentCreation
            List<MaterialDetails> materialDetailsList = indentRequestDTO.getMaterialDetails().stream().map(materialRequest -> {
                MaterialDetails material = new MaterialDetails();
                material.setMaterialCode(materialRequest.getMaterialCode());
                material.setMaterialDescription(materialRequest.getMaterialDescription());
                material.setQuantity(materialRequest.getQuantity());
                material.setUnitPrice(materialRequest.getUnitPrice());
                material.setUom(materialRequest.getUom());
                material.setModeOfProcurement(materialRequest.getModeOfProcurement());
                material.setCurrency(materialRequest.getCurrency());
                material.setConversionRate(materialRequest.getConversionRate()); // added by abhinav

                // Calculate total price (apply conversion rate for non-INR currencies)
                BigDecimal totalPrice = materialRequest.getQuantity().multiply(materialRequest.getUnitPrice());
                // added by abhinav
                if (materialRequest.getConversionRate() != null
                        && materialRequest.getCurrency() != null
                        && !"INR".equalsIgnoreCase(materialRequest.getCurrency())) {
                    totalPrice = totalPrice.multiply(materialRequest.getConversionRate());
                }
                material.setTotalPrice(totalPrice);
                material.setBudgetCode(materialRequest.getBudgetCode());
                material.setMaterialCategory(materialRequest.getMaterialCategory());
                material.setMaterialSubCategory(materialRequest.getMaterialSubCategory());
                material.setIndentCreation(indentCreation);
                return material;
            }).collect(Collectors.toList());

            indentCreation.setMaterialDetails(materialDetailsList);

            // Calculate sum of all material total prices
            totalIndentPrice = materialDetailsList.stream()
                    .map(MaterialDetails::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        } else if ("job".equalsIgnoreCase(indentType)) {

            // Process Job Details
            List<JobDetails> jobDetailsList = indentRequestDTO.getJobDetails().stream().map(jobRequest -> {
                JobDetails job = new JobDetails();
                job.setJobCode(jobRequest.getJobCode());
                job.setJobDescription(jobRequest.getJobDescription());
                job.setCategory(jobRequest.getCategory());
                job.setSubCategory(jobRequest.getSubCategory());
                job.setUom(jobRequest.getUom());
                job.setQuantity(jobRequest.getQuantity());
                job.setEstimatedPrice(jobRequest.getEstimatedPrice());
                job.setCurrency(jobRequest.getCurrency());
                job.setBriefDescription(jobRequest.getBriefDescription());
                job.setOrigin(jobRequest.getOrigin());
                job.setModeOfProcurement(jobRequest.getModeOfProcurement());
                job.setBudgetCode(jobRequest.getBudgetCode());
                job.setVendorNames(jobRequest.getVendorNames());

                BigDecimal qty = jobRequest.getQuantity() != null ? jobRequest.getQuantity() : BigDecimal.ZERO;
                BigDecimal price = jobRequest.getEstimatedPrice() != null ? jobRequest.getEstimatedPrice() : BigDecimal.ZERO;
                BigDecimal jobTotal = qty.multiply(price);
                job.setTotalPrice(jobTotal);
                job.setIndentCreation(indentCreation);
                return job;
            }).collect(Collectors.toList());

            indentCreation.setJobDetails(jobDetailsList);

            // Calculate total price for jobs
            totalIndentPrice = jobDetailsList.stream()
                    .map(jd -> jd.getTotalPrice() != null ? jd.getTotalPrice() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        indentCreation.setTotalIntentValue(totalIndentPrice);

        // Use saveAndFlush to immediately commit to database
        // This is needed so that workflow initiation can find the indent
        indentCreationRepository.saveAndFlush(indentCreation);

        // Save VendorNames for each Material (only for material indents)
        if ("material".equalsIgnoreCase(indentType)) {
            List<MaterialDetails> savedMaterials = materialDetailsRepository.findByIndentCreation_IndentId(indentId);

            List<VendorNamesForJobWorkMaterial> vendorList = new ArrayList<>();
            for (int i = 0; i < savedMaterials.size(); i++) {
                MaterialDetails savedMaterial = savedMaterials.get(i);
                MaterialDetailsRequestDTO materialRequest = indentRequestDTO.getMaterialDetails().get(i);
                System.out.println("Material ID: " + savedMaterial.getId() + ", Material Code: " + savedMaterial.getMaterialCode());

                if (materialRequest.getVendorNames() != null && !materialRequest.getVendorNames().isEmpty()) {
                    for (String vendorName : materialRequest.getVendorNames()) {
                        VendorNamesForJobWorkMaterial vendor = new VendorNamesForJobWorkMaterial();
                        vendor.setVendorName(vendorName);
                        vendor.setMaterialId(savedMaterial.getId());
                        vendor.setIndentId(indentId);
                        vendor.setMaterialCode(savedMaterial.getMaterialCode());
                        vendorList.add(vendor);
                    }
                }
            }

            // Save all vendor records
            if (!vendorList.isEmpty()) {
                vendorNameRepository.saveAll(vendorList);
            }
        }

        IndentCreation indentCreations = indentCreationRepository.findByIndentId(indentId);
        return mapToResponseDTO(indentCreations);
    }

    public String saveBase64Files(List<String> base64Files, String basePath) {
        try {
            List<String> fileNames = new ArrayList<>();
            for (String base64File : base64Files) {
                String fileName = CommonUtils.saveBase64Image(base64File, basePath);
                fileNames.add(fileName);
            }
            return String.join(",", fileNames);
        } catch (Exception e) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.FILE_UPLOAD_ERROR,
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CORRUPTED,
                    "Error while uploading files."));
        }
    }

    public IndentCreationResponseDTO updateIndent(String indentId, IndentCreationRequestDTO indentRequestDTO) {
        IndentCreation indentCreation = indentCreationRepository.findById(indentId)
                .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "indent not found for the provided indent ID.")
        ));

        // Bug Fix 2: Check if indent is locked due to tender creation
        if (Boolean.TRUE.equals(indentCreation.getIsLockedForTender())) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Indent is locked for editing as tender has been created. Reason: " + indentCreation.getLockedReason())
            );
        }

        // Bug Fix 1: Check if indent is editable (workflow-based edit restriction)
        if (Boolean.FALSE.equals(indentCreation.getIsEditable())) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Indent is not editable. It can only be edited when sent back by an approver for revision.")
            );
        }

        // Bug Fix 3: Increment version on update
        Integer currentVersion = indentCreation.getVersion();
        if (currentVersion == null) {
            currentVersion = 1;
        }
        indentCreation.setVersion(currentVersion + 1);

        // Determine indent type (use request if given, otherwise existing, default material)
        String indentType = indentRequestDTO.getIndentType();
        if (indentType == null || indentType.isEmpty()) {
            indentType = (indentCreation.getIndentType() == null || indentCreation.getIndentType().isEmpty())
                    ? "material"
                    : indentCreation.getIndentType();
        }
        indentCreation.setIndentType(indentType);
        indentCreation.setMaterialCategoryType(indentRequestDTO.getMaterialCategoryType());

        // Update project-related fields for workflow branch matching
        indentCreation.setIsUnderProject(indentRequestDTO.getIsUnderProject() != null ? indentRequestDTO.getIsUnderProject() : false);
        indentCreation.setProjectCode(indentRequestDTO.getProjectCode());
        indentCreation.setModeOfProcurement(indentRequestDTO.getModeOfProcurement());

        // Update indent fields
        indentCreation.setIndentorName(indentRequestDTO.getIndentorName());
        indentCreation.setIndentorMobileNo(indentRequestDTO.getIndentorMobileNo());
        indentCreation.setIndentorEmailAddress(indentRequestDTO.getIndentorEmailAddress());
        indentCreation.setConsignesLocation(indentRequestDTO.getConsignesLocation());
        indentCreation.setProjectName(indentRequestDTO.getProjectName());
        indentCreation.setIsPreBitMeetingRequired(indentRequestDTO.getIsPreBidMeetingRequired());
        indentCreation.setProprietaryAndLimitedDeclaration(indentRequestDTO.getProprietaryAndLimitedDeclaration());

        String preBidDate = indentRequestDTO.getPreBidMeetingDate();
        indentCreation.setPreBidMeetingDate(preBidDate != null ? CommonUtils.convertStringToDateObject(preBidDate) : null);

        indentCreation.setPreBidMeetingVenue(indentRequestDTO.getPreBidMeetingVenue());
        indentCreation.setIsItARateContractIndent(indentRequestDTO.getIsItARateContractIndent());
        indentCreation.setEstimatedRate(indentRequestDTO.getEstimatedRate());
        indentCreation.setPeriodOfContract(indentRequestDTO.getPeriodOfContract());

        // NEW: Handle multiple job codes for rate contract on update
        if (indentRequestDTO.getRateContractJobCodes() != null && !indentRequestDTO.getRateContractJobCodes().isEmpty()) {
            String jobCodesStr = String.join(",", indentRequestDTO.getRateContractJobCodes());
            indentCreation.setRateContractJobCodes(jobCodesStr);
        } else {
            indentCreation.setRateContractJobCodes(null);
        }

        if (indentRequestDTO.getUploadBuyBackFileNames() == null || indentRequestDTO.getUploadBuyBackFileNames().isEmpty()) {
            indentCreation.setUploadBuyBackFileNames(null);
        } else {
            String buy = saveBase64Files(indentRequestDTO.getUploadBuyBackFileNames(), basePath);
            indentCreation.setUploadBuyBackFileNames(buy);
        }
        if (indentRequestDTO.getUploadingPriorApprovalsFileName() == null || indentRequestDTO.getUploadingPriorApprovalsFileName().isEmpty()) {
            indentCreation.setUploadingPriorApprovalsFileName(null);
        } else {
            String prior = saveBase64Files(indentRequestDTO.getUploadingPriorApprovalsFileName(), basePath);
            indentCreation.setUploadingPriorApprovalsFileName(prior);
        }
        if (indentRequestDTO.getTechnicalSpecificationsFileName() == null || indentRequestDTO.getTechnicalSpecificationsFileName().isEmpty()) {
            indentCreation.setTechnicalSpecificationsFileName(null);
        } else {
            String technical = saveBase64Files(indentRequestDTO.getTechnicalSpecificationsFileName(), basePath);
            indentCreation.setTechnicalSpecificationsFileName(technical);
        }
        if (indentRequestDTO.getDraftEOIOrRFPFileName() == null || indentRequestDTO.getDraftEOIOrRFPFileName().isEmpty()) {
            indentCreation.setDraftEOIOrRFPFileName(null);
        } else {
            String draft = saveBase64Files(indentRequestDTO.getDraftEOIOrRFPFileName(), basePath);
            indentCreation.setDraftEOIOrRFPFileName(draft);
        }
        if (indentRequestDTO.getUploadPACOrBrandPACFileName() == null || indentRequestDTO.getUploadPACOrBrandPACFileName().isEmpty()) {
            indentCreation.setUploadPACOrBrandPACFileName(null);
        } else {
            String pac = saveBase64Files(indentRequestDTO.getUploadPACOrBrandPACFileName(), basePath);
            indentCreation.setUploadPACOrBrandPACFileName(pac);
        }

        indentCreation.setBrandPac(indentRequestDTO.getBrandPac());
        indentCreation.setJustification(indentRequestDTO.getJustification());
        indentCreation.setBrandAndModel(indentRequestDTO.getBrandAndModel());
        indentCreation.setQuarter(indentRequestDTO.getQuarter());
        indentCreation.setPurpose(indentRequestDTO.getPurpose());
        indentCreation.setProprietaryJustification(indentRequestDTO.getProprietaryJustification());
        indentCreation.setReason(indentRequestDTO.getReason());
        indentCreation.setBuyBack(indentRequestDTO.getBuyBack());
        indentCreation.setSerialNumber(indentRequestDTO.getSerialNumber());
        indentCreation.setModelNumber(indentRequestDTO.getModelNumber());

        String purchaseDate = indentRequestDTO.getDateOfPurchase();
        indentCreation.setDateOfPurchase(purchaseDate != null ? CommonUtils.convertStringToDateObject(purchaseDate) : null);

        indentCreation.setFileType(indentRequestDTO.getFileType());
        indentCreation.setUpdatedBy(indentRequestDTO.getUpdatedBy());
        indentCreation.setCreatedBy(indentRequestDTO.getCreatedBy());
        indentCreation.setEmployeeDepartment(indentRequestDTO.getEmployeeDepartment());

        // Update details based on indentType
        if ("material".equalsIgnoreCase(indentType)) {

            // Validate vendor count based on mode of procurement
            validateVendorCountByModeOfProcurement(indentRequestDTO.getModeOfProcurement(), indentRequestDTO.getMaterialDetails());

            // Validate computer item prices for department-specific limits
            if (indentRequestDTO.getEmployeeDepartment() != null && !indentRequestDTO.getEmployeeDepartment().isEmpty()) {
                validateComputerItemPrices(indentRequestDTO.getMaterialDetails(), indentRequestDTO.getEmployeeDepartment());
            }

            List<MaterialDetails> existingMaterials = indentCreation.getMaterialDetails();
            Map<String, MaterialDetails> existingMap = existingMaterials.stream()
                    .filter(m -> m.getMaterialCode() != null)
                    .collect(Collectors.toMap(MaterialDetails::getMaterialCode, m -> m));

            for (MaterialDetailsRequestDTO materialRequest : indentRequestDTO.getMaterialDetails()) {
                MaterialDetails material = existingMap.getOrDefault(
                        materialRequest.getMaterialCode(), new MaterialDetails());

                material.setMaterialCode(materialRequest.getMaterialCode());
                material.setMaterialDescription(materialRequest.getMaterialDescription());
                material.setQuantity(materialRequest.getQuantity());
                material.setUnitPrice(materialRequest.getUnitPrice());
                material.setUom(materialRequest.getUom());
                material.setModeOfProcurement(materialRequest.getModeOfProcurement());
                material.setCurrency(materialRequest.getCurrency());
                material.setConversionRate(materialRequest.getConversionRate());
                BigDecimal updatedTotalPrice = materialRequest.getQuantity().multiply(materialRequest.getUnitPrice());
                if (materialRequest.getConversionRate() != null
                        && materialRequest.getCurrency() != null
                        && !"INR".equalsIgnoreCase(materialRequest.getCurrency())) {
                    updatedTotalPrice = updatedTotalPrice.multiply(materialRequest.getConversionRate());
                }
                material.setTotalPrice(updatedTotalPrice);
                material.setBudgetCode(materialRequest.getBudgetCode());
                material.setMaterialCategory(materialRequest.getMaterialCategory());
                material.setMaterialSubCategory(materialRequest.getMaterialSubCategory());
                material.setIndentCreation(indentCreation);

                MaterialDetails savedMaterial = materialDetailsRepository.save(material);

                if (savedMaterial.getId() != null) {
                    List<VendorNamesForJobWorkMaterial> existingVendors = vendorNameRepository
                            .findByIndentIdAndMaterialIdAndMaterialCode(indentCreation.getIndentId(), savedMaterial.getId(), savedMaterial.getMaterialCode());

                    List<String> updatedVendorNames = materialRequest.getVendorNames() != null
                            ? materialRequest.getVendorNames()
                            : new ArrayList<>();

                    for (VendorNamesForJobWorkMaterial existingVendor : existingVendors) {
                        if (!updatedVendorNames.contains(existingVendor.getVendorName())) {
                            vendorNameRepository.delete(existingVendor);
                        }
                    }

                    for (String newVendor : updatedVendorNames) {
                        boolean alreadyExists = existingVendors.stream()
                                .anyMatch(ev -> ev.getVendorName().equalsIgnoreCase(newVendor));

                        if (!alreadyExists) {
                            VendorNamesForJobWorkMaterial vendor = new VendorNamesForJobWorkMaterial();
                            vendor.setVendorName(newVendor);
                            vendor.setIndentId(indentCreation.getIndentId());
                            vendor.setMaterialId(savedMaterial.getId());
                            vendor.setMaterialCode(savedMaterial.getMaterialCode());
                            vendorNameRepository.save(vendor);
                        }
                    }
                    if (!existingMaterials.contains(savedMaterial)) {
                        existingMaterials.add(savedMaterial);
                    }
                }
            }

        } else if ("job".equalsIgnoreCase(indentType)) {
            // Basic job update logic: replace existing jobs with the new list
            if (indentCreation.getJobDetails() != null && !indentCreation.getJobDetails().isEmpty()) {
                jobDetailsRepository.deleteAll(indentCreation.getJobDetails());
                indentCreation.getJobDetails().clear();
            }

            if (indentRequestDTO.getJobDetails() != null) {
                List<JobDetails> jobDetailsList = indentRequestDTO.getJobDetails().stream().map(jobRequest -> {
                    JobDetails job = new JobDetails();
                    job.setJobCode(jobRequest.getJobCode());
                    job.setJobDescription(jobRequest.getJobDescription());
                    job.setCategory(jobRequest.getCategory());
                    job.setSubCategory(jobRequest.getSubCategory());
                    job.setUom(jobRequest.getUom());
                    job.setQuantity(jobRequest.getQuantity());
                    job.setEstimatedPrice(jobRequest.getEstimatedPrice());
                    job.setCurrency(jobRequest.getCurrency());
                    job.setBriefDescription(jobRequest.getBriefDescription());
                    job.setOrigin(jobRequest.getOrigin());
                    job.setModeOfProcurement(jobRequest.getModeOfProcurement());
                    job.setBudgetCode(jobRequest.getBudgetCode());
                    job.setVendorNames(jobRequest.getVendorNames());

                    BigDecimal qty = jobRequest.getQuantity() != null ? jobRequest.getQuantity() : BigDecimal.ZERO;
                    BigDecimal price = jobRequest.getEstimatedPrice() != null ? jobRequest.getEstimatedPrice() : BigDecimal.ZERO;
                    BigDecimal jobTotal = qty.multiply(price);
                    job.setTotalPrice(jobTotal);
                    job.setIndentCreation(indentCreation);
                    return job;
                }).collect(Collectors.toList());

                indentCreation.setJobDetails(jobDetailsList);
            }
        }

        // Calculate totalIndentPrice based on type
        BigDecimal totalIndentPrice;
        if ("job".equalsIgnoreCase(indentType)) {
            totalIndentPrice = indentCreation.getJobDetails() != null
                    ? indentCreation.getJobDetails().stream()
                            .map(JobDetails::getTotalPrice)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                    : BigDecimal.ZERO;
        } else {
            totalIndentPrice = indentCreation.getMaterialDetails().stream()
                    .map(MaterialDetails::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        indentCreation.setTotalIntentValue(totalIndentPrice);

        indentCreationRepository.save(indentCreation);
        return mapToResponseDTO(indentCreation);
    }

    public IndentCreationResponseDTO getIndentById(String indentId) {
        IndentCreation indentCreation = indentCreationRepository.findById(indentId)
                .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Indent not found for the provided Indent ID.")
        ));
        return mapToResponseDTO(indentCreation);
    }

    @Override
    public IndentDataResponseDto getIndentDataById(String indentId) throws IOException {
        IndentCreation indentCreation = indentCreationRepository.findById(indentId)
                .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Indent not found for the provided Indent ID.")
        ));

        IndentDataResponseDto response = new IndentDataResponseDto();
        response.setIndentorName(indentCreation.getIndentorName());
        response.setIndentId(indentCreation.getIndentId());
        response.setIndentorMobileNo(indentCreation.getIndentorMobileNo());
        response.setIndentorEmailAddress(indentCreation.getIndentorEmailAddress());
        response.setPriorApprovalsFileName(indentCreation.getUploadingPriorApprovalsFileName());
        response.setProjectName(indentCreation.getProjectName());
        response.setProprietaryAndLimitedDeclaration(indentCreation.getProprietaryAndLimitedDeclaration());
        response.setIsPreBidMeetingRequired(indentCreation.getIsPreBitMeetingRequired());
        LocalDate Date = indentCreation.getPreBidMeetingDate();
        if (Date != null) {
            response.setPreBidMeetingDate(CommonUtils.convertDateToString(Date));
        } else {
            indentCreation.setPreBidMeetingDate(null);
        }
        response.setPreBidMeetingVenue(indentCreation.getPreBidMeetingVenue());
        response.setIsItARateContractIndent(indentCreation.getIsItARateContractIndent());
        response.setEstimatedRate(indentCreation.getEstimatedRate());
        response.setPeriodOfContract(indentCreation.getPeriodOfContract());
        response.setSingleAndMultipleJob(indentCreation.getRateContractJobCodes());
        response.setTechnicalSpecificationsFile(indentCreation.getTechnicalSpecificationsFileName());
        response.setDraftFileName(indentCreation.getDraftEOIOrRFPFileName());
        response.setPacAndBrandFileName(indentCreation.getUploadPACOrBrandPACFileName());
        if (indentCreation.getUploadingPriorApprovalsFileName() == null || indentCreation.getUploadingPriorApprovalsFileName().isEmpty()) {
            response.setUploadingPriorApprovalsFileName(null);
        } else {
            response.setUploadingPriorApprovalsFileName(
                    convertFilesToBase64(indentCreation.getUploadingPriorApprovalsFileName(), basePath));
        }
        if (indentCreation.getTechnicalSpecificationsFileName() == null || indentCreation.getTechnicalSpecificationsFileName().isEmpty()) {
            response.setTechnicalSpecificationsFileName(null);
        } else {
            response.setTechnicalSpecificationsFileName(
                    convertFilesToBase64(indentCreation.getTechnicalSpecificationsFileName(), basePath));
        }
        if (indentCreation.getDraftEOIOrRFPFileName() == null || indentCreation.getDraftEOIOrRFPFileName().isEmpty()) {
            response.setDraftEOIOrRFPFileName(null);
        } else {
            response.setDraftEOIOrRFPFileName(
                    convertFilesToBase64(indentCreation.getDraftEOIOrRFPFileName(), basePath));
        }

        if (indentCreation.getUploadPACOrBrandPACFileName() == null || indentCreation.getUploadPACOrBrandPACFileName().isEmpty()) {
            response.setUploadPACOrBrandPACFileName(null);
        } else {
            response.setUploadPACOrBrandPACFileName(
                    convertFilesToBase64(indentCreation.getUploadPACOrBrandPACFileName(), basePath));
        }
        response.setBrandPac(indentCreation.getBrandPac());
        response.setJustification(indentCreation.getJustification());
        response.setBrandAndModel(indentCreation.getBrandAndModel());
        response.setPurpose(indentCreation.getPurpose());
        response.setQuarter(indentCreation.getQuarter());
        response.setProprietaryJustification(indentCreation.getProprietaryJustification());
        response.setReason(indentCreation.getReason());
        response.setFileType(indentCreation.getFileType());
        response.setBuyBack(indentCreation.getBuyBack());
        if (indentCreation.getUploadBuyBackFileNames() == null || indentCreation.getUploadBuyBackFileNames().isEmpty()) {
            response.setUploadBuyBackFileNames(null);
        } else {
            response.setUploadBuyBackFileNames(convertFilesToBase64(indentCreation.getUploadBuyBackFileNames(), basePath));
        }
        response.setBuyBackFileName(indentCreation.getUploadBuyBackFileNames());
        response.setSerialNumber(indentCreation.getSerialNumber());
        response.setModelNumber(indentCreation.getModelNumber());
        LocalDate dateOfPurchase = indentCreation.getDateOfPurchase();
        if (dateOfPurchase != null) {
            response.setDateOfPurchase(CommonUtils.convertDateToString(dateOfPurchase));
        } else {
            indentCreation.setDateOfPurchase(null);
        }
        response.setCreatedBy(indentCreation.getCreatedBy());
        response.setUpdatedBy(indentCreation.getUpdatedBy());

        Optional<WorkflowTransition> lastRecord = workflowTransitionRepository.findTopByRequestIdOrderByWorkflowTransitionIdDesc(indentId);

        if (lastRecord.isPresent()) {
            WorkflowTransition transition = lastRecord.get();

            response.setApprovedBy(transition.getCurrentRole());
            String d = CommonUtils.convertDateTooString(transition.getCreatedDate());
            response.setDate(d);
            response.setRemarks(transition.getRemarks());
        }

        // For now, this still assumes material-based indent for this DTO
        String materialSubCategory = indentCreation.getMaterialDetails().stream()
                .map(MaterialDetails::getMaterialSubCategory)
                .findFirst()
                .orElse(null);

        if ("Computer & Peripherals".equalsIgnoreCase(materialSubCategory)) {
            materialSubCategory = "Computer";
        } else {
            materialSubCategory = "Normal";
        }

        response.setMaterialCategory(materialSubCategory);
        response.setConsignesLocation(indentCreation.getConsignesLocation());

        // Map project-related fields
        response.setIsUnderProject(indentCreation.getIsUnderProject());
        response.setProjectCode(indentCreation.getProjectCode());
        response.setModeOfProcurement(indentCreation.getModeOfProcurement());

        // Map material details
        List<MaterialDetailsResponseDTO> materialDetailsResponse = indentCreation.getMaterialDetails().stream().map(material -> {
            MaterialDetailsResponseDTO materialResponse = new MaterialDetailsResponseDTO();
            materialResponse.setMaterialCode(material.getMaterialCode());
            materialResponse.setMaterialDescription(material.getMaterialDescription());
            materialResponse.setQuantity(material.getQuantity());
            materialResponse.setUnitPrice(material.getUnitPrice());
            materialResponse.setUom(material.getUom());
            materialResponse.setTotalPrice(material.getTotalPrice());
            materialResponse.setBudgetCode(material.getBudgetCode());
            materialResponse.setModeOfProcurement(material.getModeOfProcurement());
            materialResponse.setMaterialCategory(material.getMaterialCategory());
            materialResponse.setMaterialSubCategory(material.getMaterialSubCategory());
            materialResponse.setCurrency(material.getCurrency());
            materialResponse.setConversionRate(material.getConversionRate());

            List<String> vendorNames = vendorNameRepository.findByMaterialId(material.getId())
                    .stream()
                    .map(VendorNamesForJobWorkMaterial::getVendorName)
                    .collect(Collectors.toList());
            System.out.println("material_id" + material.getId());
            materialResponse.setVendorNames(vendorNames);

            return materialResponse;
        }).collect(Collectors.toList());

        // Calculate total price of all materials
        BigDecimal totalPriceOfAllMaterials = materialDetailsResponse.stream()
                .map(MaterialDetailsResponseDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String projectName = indentCreation.getProjectName();// project name is project code
        BigDecimal allocatedAmount = projectMasterRepository
                .findByProjectCode(projectName)
                .map(ProjectMaster::getAllocatedAmount)
                .orElse(BigDecimal.ZERO);
        response.setProjectLimit(allocatedAmount);

        System.out.println("allocatedAmount: " + allocatedAmount);
        response.setTotalPriceOfAllMaterials(totalPriceOfAllMaterials);

        response.setMaterialDetails(materialDetailsResponse);

        // Set indent type and material category type
        response.setIndentType(indentCreation.getIndentType());
        response.setMaterialCategoryType(indentCreation.getMaterialCategoryType());

        // Map job details (for job-type indents)
        if ("job".equalsIgnoreCase(indentCreation.getIndentType()) && indentCreation.getJobDetails() != null) {
            List<com.astro.dto.workflow.ProcurementDtos.IndentDto.JobDetailsResponseDTO> jobDetailsResponse =
                    indentCreation.getJobDetails().stream().map(job -> {
                        com.astro.dto.workflow.ProcurementDtos.IndentDto.JobDetailsResponseDTO jobResponse =
                                new com.astro.dto.workflow.ProcurementDtos.IndentDto.JobDetailsResponseDTO();
                        jobResponse.setJobCode(job.getJobCode());
                        jobResponse.setJobDescription(job.getJobDescription());
                        jobResponse.setCategory(job.getCategory());
                        jobResponse.setSubCategory(job.getSubCategory());
                        jobResponse.setUom(job.getUom());
                        jobResponse.setQuantity(job.getQuantity());
                        jobResponse.setEstimatedPrice(job.getEstimatedPrice());
                        jobResponse.setTotalPrice(job.getTotalPrice());
                        jobResponse.setCurrency(job.getCurrency());
                        jobResponse.setBriefDescription(job.getBriefDescription());
                        jobResponse.setModeOfProcurement(job.getModeOfProcurement());
                        jobResponse.setBudgetCode(job.getBudgetCode());
                        jobResponse.setVendorNames(job.getVendorNames());
                        return jobResponse;
                    }).collect(Collectors.toList());
            response.setJobDetails(jobDetailsResponse);
        }

        // Calculate proper workflow status from transitions
        List<WorkflowTransition> workflowTransitions = workflowTransitionRepository.findByRequestId(indentId);

        if (workflowTransitions != null && !workflowTransitions.isEmpty()) {
            // Get the last transition by workflowSequence
            WorkflowTransition lastTransition = workflowTransitions.stream()
                    .max(Comparator.comparing(wt -> wt.getWorkflowSequence() != null ? wt.getWorkflowSequence() : 0))
                    .orElse(null);

            // FIX: Count ACTUAL approvals completed (where nextAction = "Completed")
            // When an approver approves, their transition's nextAction is set to "Completed"
            long approvalsDone = workflowTransitions.stream()
                    .filter(wt -> "Completed".equalsIgnoreCase(wt.getNextAction()))
                    .count();

            // FIX: Workflow is fully approved when the LAST transition has:
            // - status = "Completed" (final state)
            // - nextRole is null or empty (no more approvers)
            // Note: Old transitions keep "In-progress" status even after approval,
            // only their nextAction changes to "Completed"
            boolean isRejected = lastTransition != null
    && ("Rejected".equalsIgnoreCase(lastTransition.getAction())
        || "Rejected".equalsIgnoreCase(lastTransition.getNextAction()));


            boolean isFullyApproved = !isRejected &&
            lastTransition != null 
                    && "Completed".equalsIgnoreCase(lastTransition.getStatus())
                    && (lastTransition.getNextRole() == null || lastTransition.getNextRole().isEmpty());

            // FIX: Find ACTUALLY pending transition (one with nextAction = "Pending")
            // Not just any "In-progress" status, as old approved transitions keep that status
            WorkflowTransition pendingTransition = workflowTransitions.stream()
                    .filter(wt -> "Pending".equalsIgnoreCase(wt.getNextAction()))
                    .findFirst()
                    .orElse(null);

            // FIX: Calculate total approval levels correctly
            int totalLevels;
            if (isFullyApproved) {
                // When fully approved, total levels = approvals done
                totalLevels = (int) approvalsDone;
            } else {
                // Count transitions that have nextRole set (these are actual approval steps)
                totalLevels = (int) workflowTransitions.stream()
                        .filter(wt -> wt.getNextRole() != null && !wt.getNextRole().isEmpty())
                        .count();
            }

            response.setStatus(lastTransition != null ? lastTransition.getStatus() : "Pending");
            response.setProcessStage(lastTransition != null ? lastTransition.getNextRole() : null);
            response.setApprovalLevel((int) approvalsDone);
            response.setTotalApprovalLevels(totalLevels);
            response.setIsFullyApproved(isFullyApproved);

            if (pendingTransition != null && !isFullyApproved) {
                response.setCurrentApprovalLevel(pendingTransition.getApprovalLevel());
                response.setPendingWith(pendingTransition.getNextRole());
            } else {
                response.setCurrentApprovalLevel(null);
                response.setPendingWith(null);
            }

            // Set appropriate status and message
            if (isRejected) {
    response.setCurrentStatus("REJECTED");
    response.setStatusMessage("Indent has been rejected.");
    response.setIsEditable(false);}
           else if (isFullyApproved) {
                response.setCurrentStatus("APPROVED");
                response.setStatusMessage("Your indent is finally approved.");
                response.setIsEditable(true);
            } else if (pendingTransition != null) {
                // Check if an approver sent it back to the Indent Creator for changes
                boolean isChangeRequested = "Change requested".equalsIgnoreCase(pendingTransition.getAction())
                        && "Indent Creator".equalsIgnoreCase(pendingTransition.getNextRole());
                if (isChangeRequested) {
                    response.setCurrentStatus("CHANGE_REQUESTED");
                    response.setStatusMessage("Change requested by approver. Please review, edit and resubmit.");
                    response.setIsEditable(true);
                } else {
                    response.setCurrentStatus("IN_PROGRESS");
                    response.setStatusMessage("Indent is currently in approval workflow. Completed " + approvalsDone + " of " + totalLevels + " approvals.");
                    response.setIsEditable(false);
                }
            } else if (approvalsDone > 0) {
                response.setCurrentStatus("IN_PROGRESS");
                response.setStatusMessage("Indent is being processed. " + approvalsDone + " approval(s) completed.");
                response.setIsEditable(false);
            } else {
                response.setCurrentStatus("DRAFT");
                response.setStatusMessage("Indent is pending submission or approval.");
                response.setIsEditable(true);
            }
        } else {
            // No workflow transitions - indent is in draft
            response.setStatus("Pending");
            response.setProcessStage("Not Started");
            response.setCurrentStatus("DRAFT");
            response.setStatusMessage("Indent is in draft state.");
            response.setApprovalLevel(0);
            response.setTotalApprovalLevels(0);
            response.setIsFullyApproved(false);
            response.setIsEditable(true);
        }

        return response;
    }

    public static List<String> convertFilesToBase64(String fileNames, String basePath) throws IOException {
        List<String> base64List = new ArrayList<>();

        if (fileNames != null && !fileNames.isEmpty()) {
            String[] fileNameArray = fileNames.split(",");

            for (String fileName : fileNameArray) {
                String trimmedFileName = fileName.trim();
                if (!trimmedFileName.isEmpty()) {
                    String base64 = CommonUtils.convertImageToBase64(trimmedFileName, basePath);
                    base64List.add(base64);
                }
            }
        }

        return base64List;
    }

    // Get All Indents
    public List<IndentCreationResponseDTO> getAllIndents() {
        List<IndentCreation> indentList = indentCreationRepository.findAll();
        return indentList.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public IndentCreationResponseDTO getIndentDataForTenderById(String indentId) throws IOException {
        IndentCreation indentCreation = indentCreationRepository.findById(indentId)
                .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Indent not found for the provided Indent ID.")
        ));

        IndentCreationResponseDTO response = new IndentCreationResponseDTO();
        response.setIndentorName(indentCreation.getIndentorName());
        response.setIndentId(indentCreation.getIndentId());
        response.setIndentorMobileNo(indentCreation.getIndentorMobileNo());
        response.setIndentorEmailAddress(indentCreation.getIndentorEmailAddress());
        response.setUploadingPriorApprovalsFileName(indentCreation.getUploadingPriorApprovalsFileName());
        response.setProjectName(
                (indentCreation.getProjectName() != null && !indentCreation.getProjectName().isBlank())
                ? indentCreation.getProjectName()
                : null
        );
        response.setProprietaryAndLimitedDeclaration(indentCreation.getProprietaryAndLimitedDeclaration());
        response.setIsPreBidMeetingRequired(indentCreation.getIsPreBitMeetingRequired());
        LocalDate Date = indentCreation.getPreBidMeetingDate();
        if (Date != null) {
            response.setPreBidMeetingDate(CommonUtils.convertDateToString(Date));
        } else {
            indentCreation.setPreBidMeetingDate(null);
        }
        response.setPreBidMeetingVenue(indentCreation.getPreBidMeetingVenue());
        response.setIsItARateContractIndent(indentCreation.getIsItARateContractIndent());
        response.setEstimatedRate(indentCreation.getEstimatedRate());
        response.setPeriodOfContract(indentCreation.getPeriodOfContract());

        // NEW: Return job codes as List
        response.setRateContractJobCodes(convertCommaSeparatedToList(indentCreation.getRateContractJobCodes()));

        response.setTechnicalSpecificationsFileName(indentCreation.getTechnicalSpecificationsFileName());
        response.setDraftEOIOrRFPFileName(indentCreation.getDraftEOIOrRFPFileName());
        response.setUploadPACOrBrandPACFileName(indentCreation.getUploadPACOrBrandPACFileName());
        response.setBrandPac(indentCreation.getBrandPac());
        response.setJustification(indentCreation.getJustification());
        response.setBrandAndModel(indentCreation.getBrandAndModel());
        response.setPurpose(indentCreation.getPurpose());
        response.setQuarter(indentCreation.getQuarter());
        response.setProprietaryJustification(indentCreation.getProprietaryJustification());
        response.setReason(indentCreation.getReason());
        response.setFileType(indentCreation.getFileType());
        response.setBuyBack(indentCreation.getBuyBack());

        if (indentCreation.getUploadBuyBackFileNames() == null || indentCreation.getUploadBuyBackFileNames().isEmpty()) {
            response.setUploadBuyBackFile(null);
        } else {
            response.setUploadBuyBackFile(convertFilesToBase64(indentCreation.getUploadBuyBackFileNames(), basePath));
        }

        response.setUploadBuyBackFileNames(indentCreation.getUploadBuyBackFileNames());
        response.setSerialNumber(indentCreation.getSerialNumber());
        response.setModelNumber(indentCreation.getModelNumber());
        response.setBuyBackAmount(indentCreation.getBuyBackAmount());
        response.setCancelStatus(indentCreation.getCancelStatus());
        response.setCancelRemarks(indentCreation.getCancelRemarks());
        LocalDate dateOfPurchase = indentCreation.getDateOfPurchase();
        if (dateOfPurchase != null) {
            response.setDateOfPurchase(CommonUtils.convertDateToString(dateOfPurchase));
        } else {
            indentCreation.setDateOfPurchase(null);
        }
        response.setCreatedBy(indentCreation.getCreatedBy());
        response.setUpdatedBy(indentCreation.getUpdatedBy());

        // indent type and materialCategoryType in tender response
        String indentType = indentCreation.getIndentType();
        if (indentType == null || indentType.isEmpty()) {
            indentType = "material";
        }
        response.setIndentType(indentType);
        response.setMaterialCategoryType(indentCreation.getMaterialCategoryType());

        // Set consigne location directly from entity - DO NOT MODIFY
        response.setConsignesLocation(indentCreation.getConsignesLocation());

        BigDecimal totalPriceOfAllMaterials = BigDecimal.ZERO;

        if ("material".equalsIgnoreCase(indentType)) {
            String materialSubCategory = indentCreation.getMaterialDetails().stream()
                    .map(MaterialDetails::getMaterialSubCategory)
                    .findFirst()
                    .orElse(null);

            if ("Computer & Peripherals".equalsIgnoreCase(materialSubCategory)) {
                materialSubCategory = "Computer";
            } else {
                materialSubCategory = "Normal";
            }

            response.setMaterialCategory(materialSubCategory);

            // Map material details
            List<MaterialDetailsResponseDTO> materialDetailsResponse = indentCreation.getMaterialDetails().stream().map(material -> {
                MaterialDetailsResponseDTO materialResponse = new MaterialDetailsResponseDTO();
                materialResponse.setMaterialCode(material.getMaterialCode());
                materialResponse.setMaterialDescription(material.getMaterialDescription());
                materialResponse.setQuantity(material.getQuantity());
                materialResponse.setUnitPrice(material.getUnitPrice());
                materialResponse.setUom(material.getUom());
                materialResponse.setTotalPrice(material.getTotalPrice());
                materialResponse.setBudgetCode(material.getBudgetCode());
                materialResponse.setModeOfProcurement(material.getModeOfProcurement());
                materialResponse.setMaterialCategory(material.getMaterialCategory());
                materialResponse.setMaterialSubCategory(material.getMaterialSubCategory());
                materialResponse.setCurrency(material.getCurrency());
                materialResponse.setConversionRate(material.getConversionRate());

                List<String> vendorNames = vendorNameRepository.findByMaterialId(material.getId())
                        .stream()
                        .map(VendorNamesForJobWorkMaterial::getVendorName)
                        .collect(Collectors.toList());
                System.out.println("material_id" + material.getId());

                System.out.println("VendorNames:" + vendorNames);
                List<String> vendorIds = vendorNames;

                List<String> vendorIdNameList = vendorIds.stream()
                        .map(vendorId -> {
                            String name = vendorMasterRepository.findVendorNameByVendorId(vendorId);
                            return vendorId + "-" + name;
                        })
                        .collect(Collectors.toList());

                materialResponse.setVendorNames(vendorIdNameList);

                return materialResponse;
            }).collect(Collectors.toList());

            response.setMaterialDetails(materialDetailsResponse);

            // Calculate total price of all materials
            totalPriceOfAllMaterials = materialDetailsResponse.stream()
                    .map(MaterialDetailsResponseDTO::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        } else if ("job".equalsIgnoreCase(indentType)) {
            // Map job details for tender response as well
            List<JobDetailsResponseDTO> jobDetailsResponse = indentCreation.getJobDetails().stream().map(job -> {
                JobDetailsResponseDTO jobResponse = new JobDetailsResponseDTO();
                jobResponse.setJobCode(job.getJobCode());
                jobResponse.setJobDescription(job.getJobDescription());
                jobResponse.setCategory(job.getCategory());
                jobResponse.setSubCategory(job.getSubCategory());
                jobResponse.setUom(job.getUom());
                jobResponse.setQuantity(job.getQuantity());
                jobResponse.setEstimatedPrice(job.getEstimatedPrice());
                jobResponse.setTotalPrice(job.getTotalPrice());
                jobResponse.setCurrency(job.getCurrency());
                jobResponse.setBriefDescription(job.getBriefDescription());
                jobResponse.setOrigin(job.getOrigin());
                jobResponse.setModeOfProcurement(job.getModeOfProcurement());
                jobResponse.setBudgetCode(job.getBudgetCode());
                jobResponse.setVendorNames(job.getVendorNames());
                return jobResponse;
            }).collect(Collectors.toList());

            response.setJobDetails(jobDetailsResponse);

            totalPriceOfAllMaterials = jobDetailsResponse.stream()
                    .map(JobDetailsResponseDTO::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        String projectName = indentCreation.getProjectName();// project name is project code
        BigDecimal allocatedAmount = projectMasterRepository
                .findByProjectCode(projectName)
                .map(ProjectMaster::getAllocatedAmount)
                .orElse(BigDecimal.ZERO);
        response.setProjectLimit(allocatedAmount);
        if ("Engineering".equals(indentCreation.getEmployeeDepartment())) {
            response.setEmployeeDepartment(indentCreation.getEmployeeDepartment());
        } else {
            response.setEmployeeDepartment("OtherDept");
        }

        System.out.println("allocatedAmount: " + allocatedAmount);
        response.setTotalPriceOfAllMaterials(totalPriceOfAllMaterials);

        return response;
    }

    // NEW: Helper method to convert comma-separated string to List
    private List<String> convertCommaSeparatedToList(String commaSeparated) {
        if (commaSeparated == null || commaSeparated.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(commaSeparated.split(",")));
    }

    @Transactional
    private IndentCreationResponseDTO mapToResponseDTO(IndentCreation indentCreation) {
        IndentCreationResponseDTO response = new IndentCreationResponseDTO();
        response.setIndentorName(indentCreation.getIndentorName());
        response.setIndentId(indentCreation.getIndentId());
        response.setIndentorMobileNo(indentCreation.getIndentorMobileNo());
        response.setIndentorEmailAddress(indentCreation.getIndentorEmailAddress());
        response.setUploadingPriorApprovalsFileName(indentCreation.getUploadingPriorApprovalsFileName());
        response.setProjectName(
                (indentCreation.getProjectName() != null && !indentCreation.getProjectName().isBlank())
                ? indentCreation.getProjectName()
                : null
        );
        response.setProprietaryAndLimitedDeclaration(indentCreation.getProprietaryAndLimitedDeclaration());
        response.setIsPreBidMeetingRequired(indentCreation.getIsPreBitMeetingRequired());
        LocalDate Date = indentCreation.getPreBidMeetingDate();
        if (Date != null) {
            response.setPreBidMeetingDate(CommonUtils.convertDateToString(Date));
        } else {
            indentCreation.setPreBidMeetingDate(null);
        }
        response.setPreBidMeetingVenue(indentCreation.getPreBidMeetingVenue());
        response.setIsItARateContractIndent(indentCreation.getIsItARateContractIndent());
        response.setEstimatedRate(indentCreation.getEstimatedRate());
        response.setPeriodOfContract(indentCreation.getPeriodOfContract());

        // NEW: Return job codes as List instead of singleAndMultipleJob
        response.setRateContractJobCodes(convertCommaSeparatedToList(indentCreation.getRateContractJobCodes()));

        response.setTechnicalSpecificationsFileName(indentCreation.getTechnicalSpecificationsFileName());
        response.setDraftEOIOrRFPFileName(indentCreation.getDraftEOIOrRFPFileName());
        response.setUploadPACOrBrandPACFileName(indentCreation.getUploadPACOrBrandPACFileName());
        response.setBrandPac(indentCreation.getBrandPac());
        response.setJustification(indentCreation.getJustification());
        response.setBrandAndModel(indentCreation.getBrandAndModel());
        response.setPurpose(indentCreation.getPurpose());
        response.setQuarter(indentCreation.getQuarter());
        response.setProprietaryJustification(indentCreation.getProprietaryJustification());
        response.setReason(indentCreation.getReason());
        response.setFileType(indentCreation.getFileType());
        response.setBuyBack(indentCreation.getBuyBack());
        response.setUploadBuyBackFileNames(indentCreation.getUploadBuyBackFileNames());
        response.setSerialNumber(indentCreation.getSerialNumber());
        response.setModelNumber(indentCreation.getModelNumber());
        response.setBuyBackAmount(indentCreation.getBuyBackAmount());
        response.setCancelStatus(indentCreation.getCancelStatus());
        response.setCancelRemarks(indentCreation.getCancelRemarks());
        response.setEmployeeId(indentCreation.getEmployeeId());
        response.setEmployeeName(indentCreation.getEmployeeName());
        response.setEmployeeDept(indentCreation.getEmployeeDepartment());
        LocalDate dateOfPurchase = indentCreation.getDateOfPurchase();
        if (dateOfPurchase != null) {
            response.setDateOfPurchase(CommonUtils.convertDateToString(dateOfPurchase));
        } else {
            indentCreation.setDateOfPurchase(null);
        }
        response.setCreatedBy(indentCreation.getCreatedBy());
        response.setUpdatedBy(indentCreation.getUpdatedBy());

        // indent type + materialCategoryType in normal response
        String indentType = indentCreation.getIndentType();
        if (indentType == null || indentType.isEmpty()) {
            indentType = "material";
        }
        response.setIndentType(indentType);
        response.setMaterialCategoryType(indentCreation.getMaterialCategoryType());

        // Map project-related fields
        response.setIsUnderProject(indentCreation.getIsUnderProject());
        response.setProjectCode(indentCreation.getProjectCode());
        response.setModeOfProcurement(indentCreation.getModeOfProcurement());

        BigDecimal totalPriceOfAllMaterials = BigDecimal.ZERO;

        if ("material".equalsIgnoreCase(indentType)) {
            String materialSubCategory = indentCreation.getMaterialDetails().stream()
                    .map(MaterialDetails::getMaterialSubCategory)
                    .findFirst()
                    .orElse(null);

            if ("Computer & Peripherals".equalsIgnoreCase(materialSubCategory)) {
                materialSubCategory = "Computer";
            } else {
                materialSubCategory = "Normal";
            }

            response.setMaterialCategory(materialSubCategory);

            // Map material details
            List<MaterialDetailsResponseDTO> materialDetailsResponse = indentCreation.getMaterialDetails().stream().map(material -> {
                MaterialDetailsResponseDTO materialResponse = new MaterialDetailsResponseDTO();
                materialResponse.setMaterialCode(material.getMaterialCode());
                materialResponse.setMaterialDescription(material.getMaterialDescription());
                materialResponse.setQuantity(material.getQuantity());
                materialResponse.setUnitPrice(material.getUnitPrice());
                materialResponse.setUom(material.getUom());
                materialResponse.setTotalPrice(material.getTotalPrice());
                materialResponse.setBudgetCode(material.getBudgetCode());
                materialResponse.setModeOfProcurement(material.getModeOfProcurement());
                materialResponse.setMaterialCategory(material.getMaterialCategory());
                materialResponse.setMaterialSubCategory(material.getMaterialSubCategory());
                materialResponse.setCurrency(material.getCurrency());
                materialResponse.setConversionRate(material.getConversionRate());

                List<String> vendorNames = vendorNameRepository.findByMaterialId(material.getId())
                        .stream()
                        .map(VendorNamesForJobWorkMaterial::getVendorName)
                        .collect(Collectors.toList());
                System.out.println("material_id" + material.getId());

                System.out.println("VendorNames:" + vendorNames);
                List<String> vendorIds = vendorNames;

                List<String> vendorIdNameList = vendorIds.stream()
                        .map(vendorId -> {
                            String name = vendorMasterRepository.findVendorNameByVendorId(vendorId);
                            return vendorId + "-" + name;
                        })
                        .collect(Collectors.toList());

                materialResponse.setVendorNames(vendorIdNameList);

                return materialResponse;
            }).collect(Collectors.toList());

            response.setMaterialDetails(materialDetailsResponse);

            // Calculate total price of all materials
            totalPriceOfAllMaterials = materialDetailsResponse.stream()
                    .map(MaterialDetailsResponseDTO::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

        } else if ("job".equalsIgnoreCase(indentType)) {
            // Map job details
            List<JobDetailsResponseDTO> jobDetailsResponse = indentCreation.getJobDetails().stream().map(job -> {
                JobDetailsResponseDTO jobResponse = new JobDetailsResponseDTO();
                jobResponse.setJobCode(job.getJobCode());
                jobResponse.setJobDescription(job.getJobDescription());
                jobResponse.setCategory(job.getCategory());
                jobResponse.setSubCategory(job.getSubCategory());
                jobResponse.setUom(job.getUom());
                jobResponse.setQuantity(job.getQuantity());
                jobResponse.setEstimatedPrice(job.getEstimatedPrice());
                jobResponse.setTotalPrice(job.getTotalPrice());
                jobResponse.setCurrency(job.getCurrency());
                jobResponse.setBriefDescription(job.getBriefDescription());
                jobResponse.setOrigin(job.getOrigin());
                jobResponse.setModeOfProcurement(job.getModeOfProcurement());
                jobResponse.setBudgetCode(job.getBudgetCode());
                jobResponse.setVendorNames(job.getVendorNames());
                return jobResponse;
            }).collect(Collectors.toList());

            response.setJobDetails(jobDetailsResponse);

            // Calculate total price of all jobs
            totalPriceOfAllMaterials = jobDetailsResponse.stream()
                    .map(JobDetailsResponseDTO::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Set consigne location directly from entity - DO NOT MODIFY
        response.setConsignesLocation(indentCreation.getConsignesLocation());

        // Handle employee department
        if ("Engineering".equals(indentCreation.getEmployeeDepartment())) {
            response.setEmployeeDepartment(indentCreation.getEmployeeDepartment());
        } else {
            response.setEmployeeDepartment("OtherDept");
        }

        // Set project limit
        String projectName = indentCreation.getProjectName();
        BigDecimal allocatedAmount = projectMasterRepository
                .findByProjectCode(projectName)
                .map(ProjectMaster::getAllocatedAmount)
                .orElse(BigDecimal.ZERO);
        response.setProjectLimit(allocatedAmount);

        System.out.println("allocatedAmount: " + allocatedAmount);
        response.setTotalPriceOfAllMaterials(totalPriceOfAllMaterials);

        // Bug Fix: Map new fields to response
        response.setIsEditable(indentCreation.getIsEditable());
        response.setIsLockedForTender(indentCreation.getIsLockedForTender());
        response.setLockedReason(indentCreation.getLockedReason());
        response.setVersion(indentCreation.getVersion());
        response.setParentIndentId(indentCreation.getParentIndentId());
        response.setCurrentStage(indentCreation.getCurrentStage());

        // FIX: Calculate actual approval level and status from workflow transitions
        String indentId = indentCreation.getIndentId();
        List<WorkflowTransition> workflowTransitions = workflowTransitionRepository.findByRequestId(indentId);

        if (workflowTransitions != null && !workflowTransitions.isEmpty()) {
            // Get the last transition by workflowSequence
            WorkflowTransition lastTransition = workflowTransitions.stream()
                    .max(Comparator.comparing(wt -> wt.getWorkflowSequence() != null ? wt.getWorkflowSequence() : 0))
                    .orElse(null);

            // FIX: Count ACTUAL approvals completed (where nextAction = "Completed")
            // When an approver approves, their transition's nextAction is set to "Completed"
            long approvalsDone = workflowTransitions.stream()
                    .filter(wt -> "Completed".equalsIgnoreCase(wt.getNextAction()))
                    .count();

            // FIX: Workflow is fully approved when the LAST transition has:
            // - status = "Completed" (final state)
            // - nextRole is null or empty (no more approvers)
boolean isRejected = lastTransition != null
    && ("Rejected".equalsIgnoreCase(lastTransition.getAction())
        || "Rejected".equalsIgnoreCase(lastTransition.getNextAction()));

            boolean isFullyApproved = lastTransition != null
                    && "Completed".equalsIgnoreCase(lastTransition.getStatus())
                    && (lastTransition.getNextRole() == null || lastTransition.getNextRole().isEmpty());

            // FIX: Find ACTUALLY pending transition (one with nextAction = "Pending")
            WorkflowTransition pendingTransition = workflowTransitions.stream()
                    .filter(wt -> "Pending".equalsIgnoreCase(wt.getNextAction()))
                    .findFirst()
                    .orElse(null);

            // FIX: Calculate total approval levels correctly
            int totalLevels;
            if (isFullyApproved) {
                totalLevels = (int) approvalsDone;
            } else {
                totalLevels = (int) workflowTransitions.stream()
                        .filter(wt -> wt.getNextRole() != null && !wt.getNextRole().isEmpty())
                        .count();
            }

            // Set actual approval level
            response.setApprovalLevel((int) approvalsDone);
            response.setTotalApprovalLevels(totalLevels);
            response.setIsFullyApproved(isFullyApproved);

            // Set appropriate status and message
            if (isRejected) {
    response.setCurrentStatus("REJECTED");
    response.setStatusMessage("Indent has been rejected.");
    response.setIsEditable(false);}
            else if (isFullyApproved) {
                response.setCurrentStatus("APPROVED");
                response.setStatusMessage("Your indent is finally approved.");
            } else if (pendingTransition != null) {
                response.setCurrentStatus("IN_PROGRESS");
                response.setStatusMessage("Indent is currently in approval workflow. Completed " + approvalsDone + " of " + totalLevels + " approvals.");
            } else if (approvalsDone > 0) {
                response.setCurrentStatus("IN_PROGRESS");
                response.setStatusMessage("Indent is being processed. " + approvalsDone + " approval(s) completed.");
            } else {
                response.setCurrentStatus(indentCreation.getCurrentStatus() != null ? indentCreation.getCurrentStatus() : "DRAFT");
                response.setStatusMessage("Indent is pending submission or approval.");
            }
        } else {
            // No workflow transitions - indent is in draft
            response.setApprovalLevel(indentCreation.getApprovalLevel() != null ? indentCreation.getApprovalLevel() : 0);
            response.setCurrentStatus(indentCreation.getCurrentStatus() != null ? indentCreation.getCurrentStatus() : "DRAFT");
            response.setStatusMessage("Indent is in draft state.");
            response.setTotalApprovalLevels(0);
            response.setIsFullyApproved(false);
        }

        return response;
    }

    @Override
    public void deleteIndent(String indentId) {

        IndentCreation indent = indentCreationRepository.findById(indentId)
                .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Indent not found for the provided ID."
                )
        ));
        try {
            indentCreationRepository.delete(indent);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the  Indent."
                    ),
                    ex
            );
        }
    }

    @Override
    public List<IndentReportDetailsDTO> getIndentReport(String startDate, String endDate) {
        // Convert String dates to LocalDate
        LocalDate startLocalDate = CommonUtils.convertStringToDateObject(startDate);
        LocalDate endLocalDate = CommonUtils.convertStringToDateObject(endDate);

        // Fetch results from the repository
        List<Object[]> results = indentCreationRepository.fetchIndentReportDetails(startLocalDate, endLocalDate);

        System.out.println(results);
        // Map results to DTO
        return results.stream().map(result -> {

            return new IndentReportDetailsDTO(
                    (String) result[0], // indentId
                    result[1] != null ? (Date) result[1] : null, // approvedDate
                    (String) result[2], // assignedTo
                    (String) result[3], // tenderRequest
                    (String) result[4], // modeOfTendering
                    (String) result[5], // correspondingPoSo
                    (String) result[6], // statusOfPoSo
                    result[7] != null ? (Date) result[7] : null, // submittedDate
                    (String) result[8], // pendingApprovalWith
                    result[9] != null ? (Date) result[9] : null, // poSoApprovedDate
                    (String) result[10], // material
                    (String) result[11], // materialCategory
                    (String) result[12], // materialSubCategory
                    (String) result[13], // vendorName
                    (String) result[14], // indentorName
                    result[15] != null ? ((BigDecimal) result[15]).doubleValue() : null, // valueOfIndent
                    result[16] != null ? ((BigDecimal) result[16]).doubleValue() : null, // valueOfPo
                    (String) result[17],
                    (String) result[18], // project
                    (String) result[19], // invoiceNo
                    (String) result[20], // gissNo
                    result[21] != null ? ((BigDecimal) result[21]).doubleValue() : null, // valuePendingToBePaid
                    (String) result[22], // currentStageOfIndent
                    (String) result[23], // shortClosedAndCancelled
                    (String) result[24] // reasonForShortClosure
            );
        }).collect(Collectors.toList());
    }

    public List<TechnoMomReportDTO> getTechnoMomReport(String startDate, String endDate) {
        List<Object[]> results = indentCreationRepository.getTechnoMomReport(CommonUtils.convertStringToDateObject(startDate), CommonUtils.convertStringToDateObject(endDate));

        return results.stream().map(obj -> new TechnoMomReportDTO(
                (Date) obj[0],
                (String) obj[1],
                (String) obj[2],
                obj[3] != null ? new BigDecimal(obj[3].toString()) : BigDecimal.ZERO,
                (String) obj[4]
        )).collect(Collectors.toList());

    }

    @Override
    public List<materialHistoryDto> getIndentIdAndUserId(String materialCode) {
        List<String> indentIds = materialDetailsRepository.findIndentIdsByMaterialCode(materialCode);

        return indentIds.stream()
                .map(id -> {
                    materialHistoryDto dto = new materialHistoryDto();
                    dto.setIndentId(id);
                    IndentCreation indent = indentCreationRepository.findById(id).orElse(null);
                    if (indent != null) {
                        dto.setUserId(indent.getCreatedBy());
                    } else {
                        dto.setUserId(null);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<materialHistoryDto> getJobIndentHistory(String jobCode) {
        List<String> indentIds = jobDetailsRepository.findIndentIdsByJobCode(jobCode);

        return indentIds.stream()
                .map(id -> {
                    materialHistoryDto dto = new materialHistoryDto();
                    dto.setIndentId(id);
                    IndentCreation indent = indentCreationRepository.findById(id).orElse(null);
                    if (indent != null) {
                        dto.setUserId(indent.getCreatedBy());
                    } else {
                        dto.setUserId(null);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void handleFileUpload(IndentCreation indentCreation, MultipartFile file, Consumer<byte[]> fileSetter) {
        if (file != null) {
            try (InputStream inputStream = file.getInputStream()) {
                byte[] fileBytes = inputStream.readAllBytes();
                fileSetter.accept(fileBytes);
            } catch (IOException e) {
                throw new InvalidInputException(new ErrorDetails(500, 3, "File Processing Error",
                        "Error while processing the uploaded file. Please try again."));
            }
        } else {
            fileSetter.accept(null);
        }
    }

    @Override
    public List<IndentListReportDto> getAllIndentsReport(String startDate, String endDate, Integer userId, String roleName) {

        List<LocalDateTime> range = CommonUtils.getDateRenge(startDate, endDate);
        LocalDateTime from = range.get(0);
        LocalDateTime to = range.get(1);

        List<Object[]> rows;
        if ("Indent Creator".equalsIgnoreCase(roleName)) {
            rows = indentCreationRepository.getAllIndentListUserIdsReport(from, to, userId);
            System.out.println(roleName);
        } else {
            rows = indentCreationRepository.getAllIndentListReport(from, to);
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return rows.stream().map(row -> {
            IndentListReportDto dto = new IndentListReportDto();
            dto.setIndentId((String) row[0]);
            dto.setIndentorName((String) row[1]);
            dto.setIndentorMobileNo((String) row[2]);
            dto.setIndentorEmailAddress((String) row[3]);
            dto.setConsignesLocation((String) row[4]);
            dto.setProjectName((String) row[5]);

            if (row[6] != null) {
                LocalDate submitted = ((Timestamp) row[6]).toLocalDateTime().toLocalDate();
                dto.setSubmittedDate(CommonUtils.convertDateToString(submitted));
            }
            dto.setPendingWith((String) row[7]);
            if (row[8] != null) {
                LocalDate pending = ((Timestamp) row[8]).toLocalDateTime().toLocalDate();
                dto.setPendingFrom(CommonUtils.convertDateToString(pending));
            }
            dto.setStatus((String) row[9]);
            dto.setAsOnDate(LocalDate.now());
            dto.setCreatedBy((Integer) row[10]);
            dto.setIndentValue((BigDecimal) row[11]);

            String json = (String) row[12];
            try {
                List<IndentMaterialListReportDto> materials = mapper.readValue(
                        json,
                        mapper.getTypeFactory().constructCollectionType(
                                List.class,
                                IndentMaterialListReportDto.class
                        )
                );
                dto.setMaterialDetails(materials);
            } catch (Exception ex) {
                dto.setMaterialDetails(new ArrayList<>());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<IndentWorkflowStatusDto> getIndentWorkflowStatus(String indentId, Integer userId, String roleName) {

        List<WorkflowTransition> wts = workflowTransitionRepository.findByRequestId(indentId);

        if ("Indent Creator".equalsIgnoreCase(roleName)) {
            wts = wts.stream()
                    .filter(wt -> wt.getCreatedBy() != null && wt.getCreatedBy().equals(userId))
                    .collect(Collectors.toList());
        }

        return wts.stream().map(wt -> {

            IndentWorkflowStatusDto indent = new IndentWorkflowStatusDto();
            indent.setRequestId(wt.getRequestId());
            indent.setCreatedBy(wt.getCreatedBy());
            indent.setModifiedBy(wt.getModifiedBy());
            indent.setStatus(wt.getStatus());
            indent.setNextAction(wt.getNextAction());
            indent.setAction(wt.getAction());
            indent.setCurrentRole(wt.getCurrentRole());
            indent.setNextRole(wt.getNextRole());
            indent.setRemarks(wt.getRemarks());
            indent.setCreatedDate(wt.getCreatedDate());
            indent.setModificationDate(wt.getModificationDate());
            return indent;
        }).collect(Collectors.toList());

    }

    @Override
    public List<SearchIndentIdDto> searchIndentIds(String type, String value, String indentType, String materialCategoryType) {
        List<SearchIndentIdDto> result;
        boolean filterByType = indentType != null && !indentType.isBlank();
        boolean filterByCategory = materialCategoryType != null && !materialCategoryType.isBlank();

        switch (type.toLowerCase()) {
            case "processid":
                if (filterByType && filterByCategory) {
                    result = indentCreationRepository.findByIndentIdContainingIgnoreCaseAndIndentTypeAndMaterialCategoryType(value, indentType, materialCategoryType);
                } else if (filterByType) {
                    result = indentCreationRepository.findByIndentIdContainingIgnoreCaseAndIndentType(value, indentType);
                } else {
                    result = indentCreationRepository.findByIndentIdContainingIgnoreCase(value);
                }
                break;
            case "submitteddate":
                try {
                    LocalDate date = LocalDate.parse(value);
                    LocalDateTime startOfDay = date.atStartOfDay();
                    LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

                    if (filterByType && filterByCategory) {
                        result = indentCreationRepository.findByCreatedDateBetweenAndIndentTypeAndMaterialCategoryType(startOfDay, endOfDay, indentType, materialCategoryType);
                    } else if (filterByType) {
                        result = indentCreationRepository.findByCreatedDateBetweenAndIndentType(startOfDay, endOfDay, indentType);
                    } else {
                        result = indentCreationRepository.findByCreatedDateBetween(startOfDay, endOfDay);
                    }
                } catch (Exception e) {
                    throw new BusinessException(
                            new ErrorDetails(
                                    AppConstant.ERROR_CODE_RESOURCE,
                                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                    AppConstant.ERROR_TYPE_RESOURCE,
                                    "Invalid submitted date format. Expected yyyy-MM-dd"
                            )
                    );
                }
                break;

            case "indentorname":
                if (filterByType && filterByCategory) {
                    result = indentCreationRepository.findByIndentorNameAndIndentTypeAndMaterialCategoryType(value, indentType, materialCategoryType);
                } else if (filterByType) {
                    result = indentCreationRepository.findByIndentorNameAndIndentType(value, indentType);
                } else {
                    result = indentCreationRepository.findByIndentorName(value);
                }
                break;

            case "materialdescription":
                result = indentCreationRepository.findByMaterialDescription(value);
                break;

            case "vendorname":
                result = vendorNameRepository.findIndentIdsByVendorName(value);
                break;

            default:
                throw new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Invalid search type: " + type
                        )
                );
        }

        if (result == null || result.isEmpty()) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "No matching indents found for the given search criteria."
                    )
            );
        }

        return result;
    }

    // updated assigning part bellow
    @Override
    @Transactional
    public String assignEmployeeToIndent(AssignEmployeeToIndentDto dto) {

        IndentCreation indent = indentCreationRepository.findById(dto.getIndentId())
                .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Indent not found for the provided ID."
                )
        ));
        // added by abhinav starts
        //  STEP 1: Deactivate existing assignment (if any)
        Optional<IndentAssignment> existingAssignment
                = indentAssignmentRepository.findByIndentIdAndStatus(dto.getIndentId(), "ACTIVE");

        if (existingAssignment.isPresent()) {
            IndentAssignment old = existingAssignment.get();
            old.setStatus("INACTIVE");
            indentAssignmentRepository.save(old);
        }

        //  STEP 2: Create new assignment record
        IndentAssignment newAssignment = new IndentAssignment();
        newAssignment.setIndentId(dto.getIndentId());
        newAssignment.setAssignedToEmployeeId(dto.getEmployeeId());

        newAssignment.setAssignedByEmployeeId("SYSTEM");

        newAssignment.setAssignedDate(LocalDateTime.now());
        newAssignment.setStatus("ACTIVE");

        indentAssignmentRepository.save(newAssignment);

        //  STEP 3: (Optional but safe) Update old column for backward compatibility
        indent.setEmployeeId(dto.getEmployeeId());
        indent.setEmployeeName(dto.getEmployeeName());
        indentCreationRepository.save(indent);

        //  Email Notification
        Optional<EmployeeDepartmentMaster> em
                = employeeDepartmentMasterRepository.findByEmployeeId(dto.getEmployeeId());

        UserMaster um = userMasterRepository.findByUserId(indent.getCreatedBy());

        if (em.isPresent()) {
            try {
                emailService.notifyEmployeeAssigned(indent, em.get().getEmailAddress(), um.getEmail());
            } catch (Exception e) {
                // log only
            }
        }

        return "Employee " + dto.getEmployeeName()
                + " assigned to indent " + dto.getIndentId() + " successfully";
    } // updated till here by abhinav

    @Override
    public String cancelIndent(CancelIndentRequestDto request) {
        IndentCreation indent = indentCreationRepository.findById(request.getIndentId())
                .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Indent not found for the provided ID."
                )
        ));

        indent.setCancelStatus(request.getCancelStatus());
        indent.setCancelRemarks(request.getCancelRemarks());

        indentCreationRepository.save(indent);
        return "indent saved";
    }

    /**
     * Validates computer item prices against department-specific price limits
     * Validates vendor name count based on mode of procurement. - Limited
     * Tender: minimum 4 vendor names required - Proprietary Purchase: maximum 1
     * vendor name allowed - Open Tender, Global Tender, BRAND PAC, GEM: no
     * vendor names allowed
     */
    private void validateVendorCountByModeOfProcurement(String modeOfProcurement, List<MaterialDetailsRequestDTO> materialDetailsList) {
        if (modeOfProcurement == null || modeOfProcurement.trim().isEmpty() || materialDetailsList == null) {
            return;
        }

        String mop = modeOfProcurement.trim().toUpperCase().replace(" ", "_").replace("-", "_");

        for (MaterialDetailsRequestDTO material : materialDetailsList) {
            int vendorCount = (material.getVendorNames() != null) ? material.getVendorNames().size() : 0;
            // Also check material-level MOP if set, otherwise use indent-level
            String materialMop = material.getModeOfProcurement();
            String effectiveMop = (materialMop != null && !materialMop.trim().isEmpty())
                    ? materialMop.trim().toUpperCase().replace(" ", "_").replace("-", "_")
                    : mop;

            if (effectiveMop.contains("LIMITED") && effectiveMop.contains("TENDER")) {
                // Limited Tender: minimum 4 vendors
                if (vendorCount < 4) {
                    throw new InvalidInputException(new ErrorDetails(
                            AppConstant.USER_INVALID_INPUT,
                            AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Limited Tender requires a minimum of 4 vendor names. "
                            + "Material '" + material.getMaterialCode() + "' has only " + vendorCount + " vendor(s)."));
                }
            } else if (effectiveMop.contains("PROPRIETARY")) {
                // Proprietary Purchase: maximum 1 vendor
                if (vendorCount > 1) {
                    throw new InvalidInputException(new ErrorDetails(
                            AppConstant.USER_INVALID_INPUT,
                            AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Proprietary Purchase allows a maximum of 1 vendor name. "
                            + "Material '" + material.getMaterialCode() + "' has " + vendorCount + " vendor(s)."));
                }
            } else if (effectiveMop.contains("OPEN") || effectiveMop.contains("GLOBAL")
                    || effectiveMop.contains("BRAND") || effectiveMop.contains("GEM")) {
                // Open Tender, Global Tender, BRAND PAC, GEM: no vendor selection
                if (vendorCount > 0) {
                    throw new InvalidInputException(new ErrorDetails(
                            AppConstant.USER_INVALID_INPUT,
                            AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Vendor names are not allowed for '" + modeOfProcurement + "'. "
                            + "Material '" + material.getMaterialCode() + "' has " + vendorCount + " vendor(s)."));
                }
            }
        }
    }

    /**
     * @param materialDetailsList List of materials to validate
     * @param departmentName Department name for which to check price limits
     * @throws InvalidInputException if any computer item exceeds the
     * department's price limit
     */
    private void validateComputerItemPrices(List<MaterialDetailsRequestDTO> materialDetailsList, String departmentName) {
        if (materialDetailsList == null || materialDetailsList.isEmpty()) {
            return;
        }

        // Get the price limit for this department (if configured)
        var priceLimitOptional = departmentComputerPriceLimitRepository
                .findByDepartmentNameIgnoreCaseAndIsActiveTrue(departmentName);

        if (priceLimitOptional.isEmpty()) {
            // No price limit configured for this department, allow all purchases
            return;
        }

        BigDecimal departmentPriceLimit = priceLimitOptional.get().getPriceLimit();
        // Added by aman
if (departmentPriceLimit== null ){
    return;
}

        // Check each material for Computer & Peripherals category
        for (MaterialDetailsRequestDTO material : materialDetailsList) {
            if ("Computer & Peripherals".equalsIgnoreCase(material.getMaterialSubCategory())) {
                if (material.getUnitPrice().compareTo(departmentPriceLimit) > 0) {
                    throw new InvalidInputException(new ErrorDetails(
                            AppConstant.ERROR_CODE_INVALID,
                            AppConstant.ERROR_TYPE_CODE_VALIDATION,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            String.format(
                                    "Computer item '%s' with unit price Rs. %s exceeds the price limit of Rs. %s for department '%s'",
                                    material.getMaterialDescription(),
                                    material.getUnitPrice(),
                                    departmentPriceLimit,
                                    departmentName
                            )
                    ));
                }
            }
        }
    }

    @Override
    public List<com.astro.dto.workflow.MaterialPurchaseHistoryDTO> getMaterialPurchaseHistory(String materialCode) {
        if (materialCode == null || materialCode.trim().isEmpty()) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_INVALID,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Material code is required to fetch purchase history"
            ));
        }

        List<com.astro.dto.workflow.MaterialPurchaseHistoryDTO> purchaseHistory
                = purchaseOrderAttributesRepository.findPurchaseHistoryByMaterialCode(materialCode);

        if (purchaseHistory == null || purchaseHistory.isEmpty()) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "No purchase history found for material code: " + materialCode
                    )
            );
        }

        return purchaseHistory;
    }

    @Override
    public List<com.astro.dto.workflow.MaterialPurchaseHistoryDTO> getJobPurchaseHistory(String jobCode) {
        if (jobCode == null || jobCode.trim().isEmpty()) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_INVALID,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Job code is required to fetch purchase history"
            ));
        }

        List<Object[]> rows = purchaseOrderAttributesRepository.findPurchaseHistoryByJobCodeRaw(jobCode);

        if (rows == null || rows.isEmpty()) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "No purchase history found for job code: " + jobCode
                    )
            );
        }

        return rows.stream().map(row -> {
            com.astro.dto.workflow.MaterialPurchaseHistoryDTO dto = new com.astro.dto.workflow.MaterialPurchaseHistoryDTO();
            dto.setPoId(row[0] != null ? row[0].toString() : null);
            dto.setIndentId(row[1] != null ? row[1].toString() : null);
            dto.setVendorName(row[2] != null ? row[2].toString() : null);
            dto.setVendorId(row[3] != null ? row[3].toString() : null);
            dto.setQuantity(row[4] != null ? new java.math.BigDecimal(row[4].toString()) : null);
            dto.setRate(row[5] != null ? new java.math.BigDecimal(row[5].toString()) : null);
            dto.setCurrency(row[6] != null ? row[6].toString() : null);
            dto.setExchangeRate(row[7] != null ? new java.math.BigDecimal(row[7].toString()) : null);
            dto.setCreatedDate(row[8] != null ? ((java.sql.Timestamp) row[8]).toLocalDateTime() : null);
            dto.setMaterialCode(row[9] != null ? row[9].toString() : null);
            dto.setMaterialDescription(row[10] != null ? row[10].toString() : null);
            dto.setGst(row[11] != null ? new java.math.BigDecimal(row[11].toString()) : null);
            dto.setTotalPoMaterialPriceInInr(row[12] != null ? new java.math.BigDecimal(row[12].toString()) : null);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String requestIndentCancellation(IndentCancellationRequestDto request) {
        // Validate indent exists
        IndentCreation indent = indentCreationRepository.findById(request.getIndentId())
                .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Indent not found for the provided ID: " + request.getIndentId()
                )
        ));

        // Check if there's already a pending cancellation request
        Optional<com.astro.entity.ProcurementModule.IndentCancellationRequest> existingRequest
                = indentCancellationRequestRepository.findByIndentIdAndRequestStatus(request.getIndentId(), "PENDING");

        if (existingRequest.isPresent()) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_INVALID,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "A cancellation request is already pending for this indent."
            ));
        }

        // Validate that there's no active Tender or Purchase Order
        validateNoActiveTenderOrPO(request.getIndentId());

        // Create cancellation request
        com.astro.entity.ProcurementModule.IndentCancellationRequest cancellationRequest
                = new com.astro.entity.ProcurementModule.IndentCancellationRequest();
        cancellationRequest.setIndentId(request.getIndentId());
        cancellationRequest.setRequestedBy(request.getRequestedBy());
        cancellationRequest.setRequestedByName(request.getRequestedByName());
        cancellationRequest.setCancellationReason(request.getCancellationReason());
        cancellationRequest.setRequestStatus("PENDING");
        cancellationRequest.setCreatedDate(LocalDateTime.now());
        cancellationRequest.setUpdatedDate(LocalDateTime.now());

        indentCancellationRequestRepository.save(cancellationRequest);

        return "Cancellation request submitted successfully. Awaiting approval from Purchase Head/Personnel.";
    }

    @Override
    public List<IndentCancellationResponseDto> getPendingCancellationRequests() {
        List<com.astro.entity.ProcurementModule.IndentCancellationRequest> pendingRequests
                = indentCancellationRequestRepository.findPendingCancellationRequests();

        return pendingRequests.stream()
                .map(this::mapToCancellationResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String approveCancellationRequest(IndentCancellationApprovalDto approval) {
        // Fetch the cancellation request
        com.astro.entity.ProcurementModule.IndentCancellationRequest cancellationRequest
                = indentCancellationRequestRepository.findById(approval.getRequestId())
                        .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Cancellation request not found."
                        )
                ));

        // Validate request is still pending
        if (!"PENDING".equals(cancellationRequest.getRequestStatus())) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_INVALID,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "This cancellation request has already been processed."
            ));
        }

        // If approved, validate again that no tender/PO exists and cancel the indent
        if ("APPROVED".equals(approval.getApprovalStatus())) {
            validateNoActiveTenderOrPO(cancellationRequest.getIndentId());

            // Cancel the indent
            IndentCreation indent = indentCreationRepository.findById(cancellationRequest.getIndentId())
                    .orElseThrow(() -> new BusinessException(
                    new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "Indent not found."
                    )
            ));

            indent.setCancelStatus(true);
            indent.setCancelRemarks(cancellationRequest.getCancellationReason());
            indentCreationRepository.save(indent);
        }

        // Update cancellation request status
        cancellationRequest.setRequestStatus(approval.getApprovalStatus());
        cancellationRequest.setApprovedBy(approval.getApprovedBy());
        cancellationRequest.setApprovedByName(approval.getApprovedByName());
        cancellationRequest.setApprovalRemarks(approval.getApprovalRemarks());
        cancellationRequest.setApprovalDate(LocalDateTime.now());
        cancellationRequest.setUpdatedDate(LocalDateTime.now());

        indentCancellationRequestRepository.save(cancellationRequest);

        if ("APPROVED".equals(approval.getApprovalStatus())) {
            return "Cancellation request approved. Indent " + cancellationRequest.getIndentId() + " has been cancelled.";
        } else {
            return "Cancellation request rejected.";
        }
    }

    /**
     * Validates that there's no active Tender or Purchase Order for the given
     * indent
     *
     * @param indentId The indent ID to validate
     * @throws InvalidInputException if there's an active Tender or PO
     */
    private void validateNoActiveTenderOrPO(String indentId) {
        // Check if indent is linked to any tender
        Optional<com.astro.entity.ProcurementModule.IndentId> indentIdEntity
                = indentIdRepository.findByIndentId(indentId);

        if (indentIdEntity.isPresent() && indentIdEntity.get().getTenderRequest() != null) {
            String tenderId = indentIdEntity.get().getTenderRequest().getTenderId();

            // Check if there's a purchase order for this tender
            com.astro.entity.ProcurementModule.PurchaseOrder purchaseOrder
                    = purchaseOrderRepository.findByTenderId(tenderId);

            if (purchaseOrder != null) {
                throw new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_INVALID,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Cannot cancel indent. An active Purchase Order (PO ID: " + purchaseOrder.getPoId()
                        + ") exists for this indent. Please cancel the Purchase Order first."
                ));
            }

            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_INVALID,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Cannot cancel indent. An active Tender (Tender ID: " + tenderId
                    + ") exists for this indent. Please cancel the Tender first."
            ));
        }
    }

    /**
     * Maps IndentCancellationRequest entity to IndentCancellationResponseDto
     */
    private IndentCancellationResponseDto mapToCancellationResponseDto(
            com.astro.entity.ProcurementModule.IndentCancellationRequest request) {
        IndentCancellationResponseDto dto = new IndentCancellationResponseDto();
        dto.setId(request.getId());
        dto.setIndentId(request.getIndentId());
        dto.setRequestedBy(request.getRequestedBy());
        dto.setRequestedByName(request.getRequestedByName());
        dto.setCancellationReason(request.getCancellationReason());
        dto.setRequestStatus(request.getRequestStatus());
        dto.setApprovedBy(request.getApprovedBy());
        dto.setApprovedByName(request.getApprovedByName());
        dto.setApprovalRemarks(request.getApprovalRemarks());
        dto.setApprovalDate(request.getApprovalDate());
        dto.setCreatedDate(request.getCreatedDate());
        return dto;
    }
}

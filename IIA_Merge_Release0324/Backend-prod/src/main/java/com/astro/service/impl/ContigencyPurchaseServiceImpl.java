package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.ProcurementDtos.*;

import com.astro.entity.ProcurementModule.ContigencyPurchase;
import com.astro.entity.ProcurementModule.CpJobDetails;
import com.astro.entity.ProcurementModule.CpMaterials;
import com.astro.entity.WorkflowTransition;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.ProcurementModule.ContigencyPurchaseRepository;
import com.astro.repository.WorkflowTransitionRepository;
import com.astro.service.ContigencyPurchaseService;
import com.astro.util.CommonUtils;

import com.azure.core.util.serializer.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;

@Service
public class ContigencyPurchaseServiceImpl implements ContigencyPurchaseService {
    @Autowired
    private ContigencyPurchaseRepository CPrepo;
    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;
    @Autowired
    private ObjectMapper objectMapper;

     @Value("${filePath}")
    private String bp;
    private final String basePath;

    public ContigencyPurchaseServiceImpl(@Value("${filePath}") String bp) {
        this.basePath = bp + "/ContingencyPurchase";
    }

    @Override
    public ContigencyPurchaseResponseDto createContigencyPurchase(ContigencyPurchaseRequestDto contigencyPurchaseDto){
            //,String uploadCopyOfInvoiceFileName) {

        // Check if the indentorId already exists
     /*   if (CPrepo.existsById(contigencyPurchaseDto.getContigencyId())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Contigency Purchase ID", "CP ID " + contigencyPurchaseDto.getContigencyId() + " already exists.");
            throw new InvalidInputException(errorDetails);
        }

      */
        Integer maxNumber = CPrepo.findMaxCpNumber();
        int nextNumber = (maxNumber == null) ? 1001 : maxNumber + 1;

        String cpId = "CP" + nextNumber;

       // String cpId = "CP" + System.currentTimeMillis();
        ModelMapper mapper = new ModelMapper();
        ContigencyPurchase cp = mapper.map(contigencyPurchaseDto, ContigencyPurchase.class);
        cp.setContigencyId(cpId);
        cp.setCpNumber(nextNumber);
        cp.setCpVersion(1);
cp.setIsActive(true);
        cp.setVendorsName(contigencyPurchaseDto.getVendorName());
        cp.setVendorsInvoiceNo(contigencyPurchaseDto.getVendorInvoiceNo());
        cp.setPredifinedPurchaseStatement(contigencyPurchaseDto.getPredifinedPurchaseStatement());
        cp.setRemarksForPurchase(contigencyPurchaseDto.getRemarksForPurchase());

        // if (contigencyPurchaseDto.getUploadCopyOfInvoice() == null || contigencyPurchaseDto.getUploadCopyOfInvoice().isEmpty()) {
        //     cp.setUploadCopyOfInvoiceFileName(null);
        // } else {
        //     cp.setUploadCopyOfInvoiceFileName(CommonUtils.saveBase64Image(contigencyPurchaseDto.getUploadCopyOfInvoice(), basePath));
        // }
        cp.setUploadCopyOfInvoiceFileName(saveBase64File(contigencyPurchaseDto.getUploadCopyOfInvoice(), basePath));
        String Date = contigencyPurchaseDto.getDate();
        if (Date != null) {
            cp.setDate(CommonUtils.convertStringToDateObject(contigencyPurchaseDto.getDate()));
        }else{
            cp.setDate(null);
        }

        cp.setCpType(contigencyPurchaseDto.getCpType());

        if ("JOB".equalsIgnoreCase(contigencyPurchaseDto.getCpType())) {
            List<CpJobDetails> jobs = contigencyPurchaseDto.getCpJobDetails().stream().map(jobDto -> {
                CpJobDetails job = mapper.map(jobDto, CpJobDetails.class);
                job.setContigencyPurchase(cp);
                job.setGst(jobDto.getGst());
                return job;
            }).collect(Collectors.toList());

            cp.setCpJobDetails(jobs);
            BigDecimal totalJobPrice = jobs.stream()
                    .map(CpJobDetails::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cp.setTotalCpValue(totalJobPrice);
        } else {
            List<CpMaterials> materials = contigencyPurchaseDto.getCpMaterials().stream().map(materialDto -> {
                CpMaterials material = mapper.map(materialDto, CpMaterials.class);
                // material.setContigencyId(cpId);
                material.setContigencyPurchase(cp);
                material.setGst(materialDto.getGst());
                return material;
            }).collect(Collectors.toList());

            cp.setCpMaterials(materials);
            BigDecimal totalMaterialPrice = materials.stream()
                    .map(CpMaterials::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cp.setTotalCpValue(totalMaterialPrice);
        }
        CPrepo.save(cp);

        // List<CpMaterials> materials = contigencyPurchaseDto.getCpMaterials().stream().map(materialDto -> {
        //     CpMaterials material = mapper.map(materialDto, CpMaterials.class);
        //     // material.setContigencyId(cpId);
        //     material.setContigencyPurchase(cp);
        //     material.setGst(materialDto.getGst());
        //     return material;
        // }).collect(Collectors.toList());

        // cp.setCpMaterials(materials);
        // BigDecimal totalMaterialPrice = materials.stream()
        //         .map(CpMaterials::getTotalPrice)
        //         .filter(Objects::nonNull)
        //         .reduce(BigDecimal.ZERO, BigDecimal::add);
        // cp.setTotalCpValue(totalMaterialPrice);
        // CPrepo.save(cp);



        return mapToResponseDTO(cp);
    }

private String extractBaseCpId(String contigencyId) {
    if (contigencyId == null) return null;
    int slashIdx = contigencyId.indexOf('/');
    return slashIdx >= 0 ? contigencyId.substring(0, slashIdx) : contigencyId;
}

@Override
public ContigencyPurchaseResponseDto updateContigencyPurchase(String contigencyId, ContigencyPurchaseRequestDto dto) {

    // 1. Load existing active CP
    ContigencyPurchase old = CPrepo.findById(contigencyId)
            .orElseThrow(() -> new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE, AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION, "Contigency Purchase not found for the provided ID.")));

    // Guard: cannot version-up an unsubmitted draft — submit it first
    if ("DRAFT".equals(old.getCurrentStatus())) {
        throw new BusinessException(new ErrorDetails(
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "This Contigency Purchase is a saved draft. Please submit it before making revisions."));
    }

    // Guard: only the original creator can edit
    if (!old.getCreatedBy().equals(dto.getCreatedBy())) {
        throw new BusinessException(new ErrorDetails(
                AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Only the original creator can revise this Contigency Purchase."));
    }

    int currentVersion = old.getCpVersion() != null ? old.getCpVersion() : 1;

    // 2. Deactivate old version
    old.setIsActive(false);
    CPrepo.save(old);

    // 3. Supersede old version's pending workflow transitions
    List<WorkflowTransition> pendingTransitions =
            workflowTransitionRepository.findPendingTransitionsByRequestId(old.getContigencyId());
    for (WorkflowTransition wt : pendingTransitions) {
        wt.setStatus("SUPERSEDED");
        wt.setNextAction(null);
        wt.setRemarks("Superseded by new version: " + extractBaseCpId(old.getContigencyId())
                + "/" + (currentVersion + 1));
        workflowTransitionRepository.save(wt);
    }

    // 4. Compute new CP ID e.g. CP1001 -> CP1001/2, CP1001/2 -> CP1001/3
    String baseId = extractBaseCpId(old.getContigencyId());
    int newVersion = currentVersion + 1;
    String newContigencyId = baseId + "/" + newVersion;

    // 5. Build new ContigencyPurchase (copy-new pattern)
    ContigencyPurchase newCp = new ContigencyPurchase();
    newCp.setContigencyId(newContigencyId);
    newCp.setCpNumber(old.getCpNumber());        // same number, new suffix
    newCp.setCpVersion(newVersion);
    newCp.setIsActive(true);
    newCp.setParentContigencyId(old.getContigencyId());
    newCp.setCreatedBy(old.getCreatedBy());      // original creator always
    newCp.setUpdatedBy(dto.getUpdatedBy());
    newCp.setCreatedDate(old.getCreatedDate());  // preserve original submission date
    newCp.setUpdatedDate(LocalDateTime.now());
    newCp.setCurrentStatus(null);                // re-enters workflow, same as submitCpDraft

    // 6. Copy business fields from the request
    newCp.setVendorsName(dto.getVendorName());
    newCp.setVendorsInvoiceNo(dto.getVendorInvoiceNo());
    newCp.setPredifinedPurchaseStatement(dto.getPredifinedPurchaseStatement());
    newCp.setRemarksForPurchase(dto.getRemarksForPurchase());
    newCp.setProjectDetail(dto.getProjectDetail());
    newCp.setProjectName(dto.getProjectName());
    newCp.setPaymentTo(dto.getPaymentTo());
    newCp.setPaymentToVendor(dto.getPaymentToVendor());
    newCp.setPaymentToEmployee(dto.getPaymentToEmployee());
    newCp.setPurpose(dto.getPurpose());
    newCp.setDeclarationOne(dto.getDeclarationOne());
    newCp.setDeclarationTwo(dto.getDeclarationTwo());
    newCp.setFileType(dto.getFileType());
    newCp.setCpType(dto.getCpType());

    String date = dto.getDate();
    newCp.setDate(date != null ? CommonUtils.convertStringToDateObject(date) : null);

    // 7. File handling — only re-upload if a new file was provided, else carry the old one forward
    if (dto.getUploadCopyOfInvoice() != null && !dto.getUploadCopyOfInvoice().isEmpty()) {
        newCp.setUploadCopyOfInvoiceFileName(saveBase64File(dto.getUploadCopyOfInvoice(), basePath));
    } else {
        newCp.setUploadCopyOfInvoiceFileName(old.getUploadCopyOfInvoiceFileName());
    }

    // 8. Materials / jobs — same processing as create
    ModelMapper mapper = new ModelMapper();
    if ("JOB".equalsIgnoreCase(dto.getCpType())) {
        List<CpJobDetails> jobs = dto.getCpJobDetails() == null
                ? Collections.emptyList()
                : dto.getCpJobDetails().stream().map(jobDto -> {
                    CpJobDetails job = mapper.map(jobDto, CpJobDetails.class);
                    job.setContigencyPurchase(newCp);
                    job.setGst(jobDto.getGst());
                    return job;
                }).collect(Collectors.toList());

        newCp.setCpJobDetails(jobs);
        BigDecimal totalJobPrice = jobs.stream()
                .map(CpJobDetails::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        newCp.setTotalCpValue(totalJobPrice);
    } else {
        List<CpMaterials> materials = dto.getCpMaterials() == null
                ? Collections.emptyList()
                : dto.getCpMaterials().stream().map(materialDto -> {
                    CpMaterials material = mapper.map(materialDto, CpMaterials.class);
                    material.setContigencyPurchase(newCp);
                    material.setGst(materialDto.getGst());
                    return material;
                }).collect(Collectors.toList());

        newCp.setCpMaterials(materials);
        BigDecimal totalMaterialPrice = materials.stream()
                .map(CpMaterials::getTotalPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        newCp.setTotalCpValue(totalMaterialPrice);
    }

    CPrepo.save(newCp);
    return mapToResponseDTO(newCp);
}

@Override
public List<ContigencyPurchaseResponseDto> getContigencyPurchaseVersionHistory(String contigencyId) {
    String baseId = extractBaseCpId(contigencyId);
    return CPrepo.findAllVersionsByBaseId(baseId)
            .stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
}

 /*   @Override
    public ContigencyPurchaseResponseDto updateContigencyPurchase(String contigencyId, ContigencyPurchaseRequestDto contigencyPurchaseDto){
            //,String uploadCopyOfInvoiceFileName) {
        ContigencyPurchase existingCP = CPrepo.findById(contigencyId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                " ContigencyPurchase not found for the provided contigency purchase ID.")
                ));
        existingCP.setVendorsName(contigencyPurchaseDto.getVendorsName());
        existingCP.setVendorsInvoiceNo(contigencyPurchaseDto.getVendorsInvoiceNo());
        String date = contigencyPurchaseDto.getDate();
        existingCP.setDate(CommonUtils.convertStringToDateObject(date));
        existingCP.setMaterialCode(contigencyPurchaseDto.getMaterialCode());
        existingCP.setMaterialDescription(contigencyPurchaseDto.getMaterialDescription());
        existingCP.setQuantity(contigencyPurchaseDto.getQuantity());
        existingCP.setUnitPrice(contigencyPurchaseDto.getUnitPrice());
        existingCP.setRemarksForPurchase(contigencyPurchaseDto.getRemarksForPurchase());
        existingCP.setAmountToBePaid(contigencyPurchaseDto.getAmountToBePaid());
        existingCP.setUploadCopyOfInvoiceFileName(contigencyPurchaseDto.getUploadCopyOfInvoice());
        existingCP.setFileType(contigencyPurchaseDto.getFileType());
        existingCP.setProjectName(contigencyPurchaseDto.getProjectName());
       // handleFileUpload(existingCP, contigencyPurchaseDto.getUploadCopyOfInvoice(),
              //  existingCP::setUploadCopyOfInvoice);
        existingCP.setPredifinedPurchaseStatement(contigencyPurchaseDto.getPredifinedPurchaseStatement());
        existingCP.setProjectDetail(contigencyPurchaseDto.getProjectDetail());
        existingCP.setUpdatedBy(contigencyPurchaseDto.getUpdatedBy());
        existingCP.setCreatedBy(contigencyPurchaseDto.getCreatedBy());
   CPrepo.save(existingCP);

        return mapToResponseDTO(existingCP);
    }*/

    @Override
    public ContigencyPurchaseResponseDto getContigencyPurchaseById(String contigencyId) {
        ContigencyPurchase contigencyPurchase = CPrepo.findById(contigencyId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Contigency Purchase not found for the provided contigency purchase ID.")
                ));
        return mapToResponseDTO(contigencyPurchase);
    }

    @Override
    public List<ContigencyPurchaseResponseDto> getAllContigencyPurchase() {
        List<ContigencyPurchase> contigencyPurchases = CPrepo.findAll();
        return contigencyPurchases.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteContigencyPurchase(String contigencyId) {

      ContigencyPurchase contigencyPurchase=CPrepo.findById(contigencyId)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "ContigencyPurchase not found for the provided ID."
                        )
                ));
        try {
            CPrepo.delete(contigencyPurchase);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the  Contigency purchase."
                    ),
                    ex
            );
        }
    }

    @Override
    public List<ContigencyPurchaseReportDto> getContigencyPurchaseReport(String startDate, String endDate) {
        List<Object[]> rawResults = CPrepo.getContigencyPurchaseReport(
                CommonUtils.convertStringToDateObject(startDate),
                CommonUtils.convertStringToDateObject(endDate)
        );

        List<ContigencyPurchaseReportDto> reportList = new ArrayList<>();

        for (Object[] row : rawResults) {
            ContigencyPurchaseReportDto dto = new ContigencyPurchaseReportDto();
            dto.setContigencyId((String) row[0]);
            dto.setVendorName((String) row[1]);
            dto.setProjectName((String) row[2]);
            dto.setPaymentToVendor((String) row[3]);
            dto.setPaymentToEmployee((String) row[4]);
            dto.setPurpose((String) row[5]);
            dto.setCreatedBy(row[6] != null ? String.valueOf(((Number) row[6]).intValue()) : null);
            dto.setPendingWith((String) row[7]);

            if (row[8] != null && row[8] instanceof Timestamp) {
                LocalDate pendingFrom = ((Timestamp) row[8]).toLocalDateTime().toLocalDate();
                dto.setPendingFrom(CommonUtils.convertDateToString(pendingFrom));
            }

            dto.setStatus((String) row[9]);
            dto.setAction((String) row[10]);

            try {
                String materialJson = (String) row[11];
                List<CpMaterialRequestDto> materials = objectMapper.readValue(
                        materialJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, CpMaterialRequestDto.class)
                );
                dto.setCpMaterials(materials);
            } catch (Exception e) {
                dto.setCpMaterials(new ArrayList<>());
            }

            reportList.add(dto);
        }

        return reportList;
    }


    private ContigencyPurchaseResponseDto mapToResponseDTO(ContigencyPurchase contigencyPurchase) {
        ContigencyPurchaseResponseDto dto = new ContigencyPurchaseResponseDto();

        dto.setContigencyId(contigencyPurchase.getContigencyId());
        //  dto.setCpNumber(contigencyPurchase.getCpNumber());
        dto.setVendorName(contigencyPurchase.getVendorsName());
        dto.setVendorInvoiceNo(contigencyPurchase.getVendorsInvoiceNo());

        LocalDate date = contigencyPurchase.getDate();
        dto.setDate(CommonUtils.convertDateToString(date));

        dto.setRemarksForPurchase(contigencyPurchase.getRemarksForPurchase());
        //   dto.setAmountToBePaid(contigencyPurchase.getAmountToBePaid());
        dto.setUploadCopyOfInvoice(contigencyPurchase.getUploadCopyOfInvoiceFileName());
        dto.setFileType(contigencyPurchase.getFileType());
        dto.setPredifinedPurchaseStatement(contigencyPurchase.getPredifinedPurchaseStatement());
        dto.setProjectDetail(contigencyPurchase.getProjectDetail());
        dto.setProjectName(contigencyPurchase.getProjectName());
        dto.setUpdatedBy(contigencyPurchase.getUpdatedBy());
        dto.setCreatedBy(contigencyPurchase.getCreatedBy());
        dto.setUpdatedDate(contigencyPurchase.getUpdatedDate());
        dto.setCreatedDate(contigencyPurchase.getCreatedDate());
        dto.setPurpose(contigencyPurchase.getPurpose());
      //  dto.setCountryOfOrigin(contigencyPurchase.getCountryOfOrigin());
        dto.setDeclarationOne(contigencyPurchase.getDeclarationOne());
        dto.setDeclarationTwo(contigencyPurchase.getDeclarationTwo());
        dto.setTotalCpValue(contigencyPurchase.getTotalCpValue());
        dto.setPaymentTo(contigencyPurchase.getPaymentTo());
        dto.setPaymentToVendor(contigencyPurchase.getPaymentToVendor());
        dto.setPaymentToEmployee(contigencyPurchase.getPaymentToEmployee());
        dto.setCurrentStatus(contigencyPurchase.getCurrentStatus());
          dto.setCpType(contigencyPurchase.getCpType());
       // WorkflowTransition wt = workflowTransitionRepository.findTopByRequestIdOrderByWorkflowSequenceDesc(contigencyPurchase.getContigencyId());
      //  dto.setStatus(wt.getStatus());
    //    dto.setProcessStage(wt.getNextRole());

        // // Map list of CpMaterials to CpMaterialsResponseDto
        // List<CpMaterialResponseDto> materialsDtoList = contigencyPurchase.getCpMaterials().stream()
        //         .map(material -> {
        //             CpMaterialResponseDto mDto = new CpMaterialResponseDto();
        //             mDto.setMaterialCode(material.getMaterialCode());
        //             mDto.setMaterialDescription(material.getMaterialDescription());
        //             mDto.setQuantity(material.getQuantity());
        //             mDto.setUnitPrice(material.getUnitPrice());
        //             mDto.setUom(material.getUom());
        //             mDto.setTotalPrice(material.getTotalPrice());
        //             mDto.setBudgetCode(material.getBudgetCode());
        //             mDto.setMaterialCategory(material.getMaterialCategory());
        //             mDto.setMaterialSubCategory(material.getMaterialSubCategory());
        //             mDto.setCurrency(material.getCurrency());
        //             mDto.setGst(material.getGst());
        //             mDto.setCountryOfOrigin(material.getCountryOfOrigin());
        //             return mDto;
        //         }).collect(Collectors.toList());

        // dto.setCpMaterials(materialsDtoList);

        // Map list of CpMaterials to CpMaterialsResponseDto
        List<CpMaterialResponseDto> materialsDtoList = (contigencyPurchase.getCpMaterials() == null
                ? new ArrayList<CpMaterials>() : contigencyPurchase.getCpMaterials()).stream()
                .map(material -> {
                    CpMaterialResponseDto mDto = new CpMaterialResponseDto();
                    mDto.setMaterialCode(material.getMaterialCode());
                    mDto.setMaterialDescription(material.getMaterialDescription());
                    mDto.setQuantity(material.getQuantity());
                    mDto.setUnitPrice(material.getUnitPrice());
                    mDto.setUom(material.getUom());
                    mDto.setTotalPrice(material.getTotalPrice());
                    mDto.setBudgetCode(material.getBudgetCode());
                    mDto.setMaterialCategory(material.getMaterialCategory());
                    mDto.setMaterialSubCategory(material.getMaterialSubCategory());
                    mDto.setCurrency(material.getCurrency());
                    mDto.setGst(material.getGst());
                    mDto.setCountryOfOrigin(material.getCountryOfOrigin());
                    return mDto;
                }).collect(Collectors.toList());

        dto.setCpMaterials(materialsDtoList);

        // Map list of CpJobDetails to CpJobResponseDto
        List<CpJobResponseDto> jobDtoList = (contigencyPurchase.getCpJobDetails() == null
                ? new ArrayList<CpJobDetails>() : contigencyPurchase.getCpJobDetails()).stream()
                .map(job -> {
                    CpJobResponseDto jDto = new CpJobResponseDto();
                    jDto.setJobCode(job.getJobCode());
                    jDto.setJobDescription(job.getJobDescription());
                    jDto.setQuantity(job.getQuantity());
                    jDto.setEstimatedPrice(job.getEstimatedPrice());
                    jDto.setUom(job.getUom());
                    jDto.setTotalPrice(job.getTotalPrice());
                    jDto.setBudgetCode(job.getBudgetCode());
                    jDto.setJobCategory(job.getJobCategory());
                    jDto.setJobSubCategory(job.getJobSubCategory());
                    jDto.setCurrency(job.getCurrency());
                    jDto.setGst(job.getGst());
                    jDto.setCountryOfOrigin(job.getCountryOfOrigin());
                    return jDto;
                }).collect(Collectors.toList());

        dto.setCpJobDetails(jobDtoList);

        return dto;

    }

    public void handleFileUpload(ContigencyPurchase contigencyPurchase, MultipartFile file, Consumer<byte[]> fileSetter) {
        if (file != null) {
            try (InputStream inputStream = file.getInputStream()) {
                byte[] fileBytes = inputStream.readAllBytes();
                fileSetter.accept(fileBytes);
            } catch (IOException e) {
                throw new InvalidInputException(new ErrorDetails(500, 3, "File Processing Error",
                        "Error while processing the uploaded file. Please try again."));
            }
        } else {
            fileSetter.accept(null);  // Handle gracefully if no file is uploaded
        }
    }

    // ── DRAFT LIFECYCLE ──────────────────────────────────────────────

    @Override
    public ContigencyPurchaseResponseDto saveCpDraft(ContigencyPurchaseRequestDto dto) {
        Integer maxNumber = CPrepo.findMaxCpNumber();
        int nextNumber = (maxNumber == null) ? 1001 : maxNumber + 1;
        String cpId = "CP" + nextNumber;

        ModelMapper mapper = new ModelMapper();
        ContigencyPurchase cp = mapper.map(dto, ContigencyPurchase.class);
        cp.setContigencyId(cpId);
        cp.setCpNumber(nextNumber);
        cp.setCpVersion(1);
cp.setIsActive(true);
        cp.setCurrentStatus("DRAFT");
        cp.setVendorsName(dto.getVendorName());
        cp.setVendorsInvoiceNo(dto.getVendorInvoiceNo());
        cp.setPredifinedPurchaseStatement(dto.getPredifinedPurchaseStatement());
        cp.setRemarksForPurchase(dto.getRemarksForPurchase());
        //  if (dto.getUploadCopyOfInvoice() == null || dto.getUploadCopyOfInvoice().isEmpty()) {
        //     cp.setUploadCopyOfInvoiceFileName(null);
        // } else {
        //     cp.setUploadCopyOfInvoiceFileName(CommonUtils.saveBase64Image(dto.getUploadCopyOfInvoice(), basePath));
        // }
        cp.setUploadCopyOfInvoiceFileName(saveBase64File(dto.getUploadCopyOfInvoice(), basePath));

        String date = dto.getDate();
        cp.setDate(date != null ? CommonUtils.convertStringToDateObject(date) : null);

        // List<CpMaterials> materials = dto.getCpMaterials() == null
        //         ? Collections.emptyList()
        //         : dto.getCpMaterials().stream().map(materialDto -> {
        //             CpMaterials material = mapper.map(materialDto, CpMaterials.class);
        //             material.setContigencyPurchase(cp);
        //             material.setGst(materialDto.getGst());
        //             return material;
        //         }).collect(Collectors.toList());

        // cp.setCpMaterials(materials);
        // BigDecimal totalMaterialPrice = materials.stream()
        //         .map(CpMaterials::getTotalPrice)
        //         .filter(Objects::nonNull)
        //         .reduce(BigDecimal.ZERO, BigDecimal::add);
        // cp.setTotalCpValue(totalMaterialPrice);
cp.setCpType(dto.getCpType());

        if ("JOB".equalsIgnoreCase(dto.getCpType())) {
            List<CpJobDetails> jobs = dto.getCpJobDetails() == null
                    ? Collections.emptyList()
                    : dto.getCpJobDetails().stream().map(jobDto -> {
                        CpJobDetails job = mapper.map(jobDto, CpJobDetails.class);
                        job.setContigencyPurchase(cp);
                        job.setGst(jobDto.getGst());
                        return job;
                    }).collect(Collectors.toList());

            cp.setCpJobDetails(jobs);
            BigDecimal totalJobPrice = jobs.stream()
                    .map(CpJobDetails::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cp.setTotalCpValue(totalJobPrice);
        } else {
            List<CpMaterials> materials = dto.getCpMaterials() == null
                    ? Collections.emptyList()
                    : dto.getCpMaterials().stream().map(materialDto -> {
                        CpMaterials material = mapper.map(materialDto, CpMaterials.class);
                        material.setContigencyPurchase(cp);
                        material.setGst(materialDto.getGst());
                        return material;
                    }).collect(Collectors.toList());

            cp.setCpMaterials(materials);
            BigDecimal totalMaterialPrice = materials.stream()
                    .map(CpMaterials::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            cp.setTotalCpValue(totalMaterialPrice);
        }
        CPrepo.save(cp);
        return mapToResponseDTO(cp);
    }

    @Override
    public ContigencyPurchaseResponseDto updateCpDraft(String cpId, ContigencyPurchaseRequestDto dto) {
        ContigencyPurchase existing = CPrepo.findById(cpId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE, AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_VALIDATION, "Draft Contingency Purchase not found: " + cpId)));

        if (!"DRAFT".equals(existing.getCurrentStatus()))
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Only DRAFT CPs can be updated via this endpoint. Current status: " + existing.getCurrentStatus()));

        if (!existing.getCreatedBy().equals(dto.getCreatedBy()))
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, "Only the original creator can update this draft."));

        existing.setVendorsName(dto.getVendorName());
        existing.setVendorsInvoiceNo(dto.getVendorInvoiceNo());
        existing.setPredifinedPurchaseStatement(dto.getPredifinedPurchaseStatement());
        existing.setRemarksForPurchase(dto.getRemarksForPurchase());
        existing.setProjectDetail(dto.getProjectDetail());
        existing.setProjectName(dto.getProjectName());
        existing.setPaymentTo(dto.getPaymentTo());
        existing.setPaymentToVendor(dto.getPaymentToVendor());
        existing.setPaymentToEmployee(dto.getPaymentToEmployee());
        existing.setPurpose(dto.getPurpose());
        existing.setDeclarationOne(dto.getDeclarationOne());
        existing.setDeclarationTwo(dto.getDeclarationTwo());
        // existing.setUploadCopyOfInvoiceFileName(dto.getUploadCopyOfInvoice());
        // if (dto.getUploadCopyOfInvoice() != null && !dto.getUploadCopyOfInvoice().isEmpty()) {
        //     existing.setUploadCopyOfInvoiceFileName(CommonUtils.saveBase64Image(dto.getUploadCopyOfInvoice(), basePath));
        // }
        existing.setUploadCopyOfInvoiceFileName(saveBase64File(dto.getUploadCopyOfInvoice(), basePath));
        existing.setFileType(dto.getFileType());
        existing.setUpdatedBy(dto.getUpdatedBy());

        String date = dto.getDate();
        existing.setDate(date != null ? CommonUtils.convertStringToDateObject(date) : null);
        existing.setCpType(dto.getCpType());
        ModelMapper mapper = new ModelMapper();

        if (existing.getCpMaterials() == null) existing.setCpMaterials(new ArrayList<>());
        if (existing.getCpJobDetails() == null) existing.setCpJobDetails(new ArrayList<>());
        existing.getCpMaterials().clear();
        existing.getCpJobDetails().clear();

        if ("JOB".equalsIgnoreCase(dto.getCpType())) {
            List<CpJobDetails> newJobs = dto.getCpJobDetails() == null
                    ? Collections.emptyList()
                    : dto.getCpJobDetails().stream().map(jobDto -> {
                        CpJobDetails job = mapper.map(jobDto, CpJobDetails.class);
                        job.setContigencyPurchase(existing);
                        job.setGst(jobDto.getGst());
                        return job;
                    }).collect(Collectors.toList());

            existing.getCpJobDetails().addAll(newJobs);
            BigDecimal totalJobPrice = newJobs.stream()
                    .map(CpJobDetails::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            existing.setTotalCpValue(totalJobPrice);
        } else {
            List<CpMaterials> newMaterials = dto.getCpMaterials() == null
                    ? Collections.emptyList()
                    : dto.getCpMaterials().stream().map(materialDto -> {
                        CpMaterials material = mapper.map(materialDto, CpMaterials.class);
                        material.setContigencyPurchase(existing);
                        material.setGst(materialDto.getGst());
                        return material;
                    }).collect(Collectors.toList());

            existing.getCpMaterials().addAll(newMaterials);
            BigDecimal totalMaterialPrice = newMaterials.stream()
                    .map(CpMaterials::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            existing.setTotalCpValue(totalMaterialPrice);
        }

        // existing.getCpMaterials().clear();
        // ModelMapper mapper = new ModelMapper();
        // List<CpMaterials> newMaterials = dto.getCpMaterials() == null
        //         ? Collections.emptyList()
        //         : dto.getCpMaterials().stream().map(materialDto -> {
        //             CpMaterials material = mapper.map(materialDto, CpMaterials.class);
        //             material.setContigencyPurchase(existing);
        //             material.setGst(materialDto.getGst());
        //             return material;
        //         }).collect(Collectors.toList());

        // existing.getCpMaterials().addAll(newMaterials);
        // BigDecimal totalMaterialPrice = newMaterials.stream()
        //         .map(CpMaterials::getTotalPrice)
        //         .filter(Objects::nonNull)
        //         .reduce(BigDecimal.ZERO, BigDecimal::add);
        // existing.setTotalCpValue(totalMaterialPrice);

        CPrepo.save(existing);
        return mapToResponseDTO(existing);
    }

    @Override
    public ContigencyPurchaseResponseDto submitCpDraft(String cpId, ContigencyPurchaseRequestDto dto) {
        ContigencyPurchase existing = CPrepo.findById(cpId)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE, AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_VALIDATION, "Draft Contingency Purchase not found: " + cpId)));

        if (!"DRAFT".equals(existing.getCurrentStatus()))
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_TYPE_CODE_VALIDATION, AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Only DRAFT CPs can be submitted via this endpoint. Current status: " + existing.getCurrentStatus()));

        existing.setVendorsName(dto.getVendorName());
        existing.setVendorsInvoiceNo(dto.getVendorInvoiceNo());
        existing.setPredifinedPurchaseStatement(dto.getPredifinedPurchaseStatement());
        existing.setRemarksForPurchase(dto.getRemarksForPurchase());
        existing.setProjectDetail(dto.getProjectDetail());
        existing.setProjectName(dto.getProjectName());
        existing.setPaymentTo(dto.getPaymentTo());
        existing.setPaymentToVendor(dto.getPaymentToVendor());
        existing.setPaymentToEmployee(dto.getPaymentToEmployee());
        existing.setPurpose(dto.getPurpose());
        existing.setDeclarationOne(dto.getDeclarationOne());
        existing.setDeclarationTwo(dto.getDeclarationTwo());
        existing.setUploadCopyOfInvoice(null);
        // if (dto.getUploadCopyOfInvoice() != null && !dto.getUploadCopyOfInvoice().isEmpty()) {
        //     existing.setUploadCopyOfInvoiceFileName(CommonUtils.saveBase64Image(dto.getUploadCopyOfInvoice(), basePath));
        // }
        existing.setUploadCopyOfInvoiceFileName(saveBase64File(dto.getUploadCopyOfInvoice(), basePath));
        // existing.setUploadCopyOfInvoiceFileName(dto.getUploadCopyOfInvoice());
        existing.setFileType(dto.getFileType());
        existing.setUpdatedBy(dto.getUpdatedBy());

        String date = dto.getDate();
        existing.setDate(date != null ? CommonUtils.convertStringToDateObject(date) : null);

        existing.setCpType(dto.getCpType());
        ModelMapper mapper = new ModelMapper();

        if (existing.getCpMaterials() == null) existing.setCpMaterials(new ArrayList<>());
        if (existing.getCpJobDetails() == null) existing.setCpJobDetails(new ArrayList<>());
        existing.getCpMaterials().clear();
        existing.getCpJobDetails().clear();

        if ("JOB".equalsIgnoreCase(dto.getCpType())) {
            List<CpJobDetails> finalJobs = dto.getCpJobDetails() == null
                    ? Collections.emptyList()
                    : dto.getCpJobDetails().stream().map(jobDto -> {
                        CpJobDetails job = mapper.map(jobDto, CpJobDetails.class);
                        job.setContigencyPurchase(existing);
                        job.setGst(jobDto.getGst());
                        return job;
                    }).collect(Collectors.toList());

            existing.getCpJobDetails().addAll(finalJobs);
            BigDecimal totalJobPrice = finalJobs.stream()
                    .map(CpJobDetails::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            existing.setTotalCpValue(totalJobPrice);
        } else {
            List<CpMaterials> finalMaterials = dto.getCpMaterials() == null
                    ? Collections.emptyList()
                    : dto.getCpMaterials().stream().map(materialDto -> {
                        CpMaterials material = mapper.map(materialDto, CpMaterials.class);
                        material.setContigencyPurchase(existing);
                        material.setGst(materialDto.getGst());
                        return material;
                    }).collect(Collectors.toList());

            existing.getCpMaterials().addAll(finalMaterials);
            BigDecimal totalMaterialPrice = finalMaterials.stream()
                    .map(CpMaterials::getTotalPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            existing.setTotalCpValue(totalMaterialPrice);
        }

        // existing.getCpMaterials().clear();
        // ModelMapper mapper = new ModelMapper();
        // List<CpMaterials> finalMaterials = dto.getCpMaterials() == null
        //         ? Collections.emptyList()
        //         : dto.getCpMaterials().stream().map(materialDto -> {
        //             CpMaterials material = mapper.map(materialDto, CpMaterials.class);
        //             material.setContigencyPurchase(existing);
        //             material.setGst(materialDto.getGst());
        //             return material;
        //         }).collect(Collectors.toList());

        // existing.getCpMaterials().addAll(finalMaterials);
        // BigDecimal totalMaterialPrice = finalMaterials.stream()
        //         .map(CpMaterials::getTotalPrice)
        //         .filter(Objects::nonNull)
        //         .reduce(BigDecimal.ZERO, BigDecimal::add);
        // existing.setTotalCpValue(totalMaterialPrice);

        existing.setCurrentStatus(null);
        CPrepo.save(existing);

        return mapToResponseDTO(existing);
    }
private String saveBase64File(String base64File, String basePath) {
    try {
        return CommonUtils.saveBase64Image(base64File, basePath);
    } catch (Exception e) {
        throw new InvalidInputException(new ErrorDetails(
                AppConstant.FILE_UPLOAD_ERROR,
                AppConstant.USER_INVALID_INPUT,
                AppConstant.ERROR_TYPE_CORRUPTED,
                "Error while uploading files."));
    }
}
    @Override
    public List<ContigencyPurchaseResponseDto> getUserCpDrafts(Integer userId) {
        return CPrepo.findByCreatedByAndCurrentStatus(String.valueOf(userId), "DRAFT")
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────

    public List<SearchCpIdDto> searchContigencyIds(String type, String value) {
        List<String> result;

        switch (type.toLowerCase()) {
            case "processid":
                result = CPrepo.findCpIdByContigencyIdContainingIgnoreCase(value);
                break;

            case "materialdescription":
                result = CPrepo.findCpIdByMaterialDescriptionContainingIgnoreCase(value);
                break;

            case "submitteddate":
                try {
                    LocalDate date = LocalDate.parse(value);
                    LocalDateTime start = date.atStartOfDay();
                    LocalDateTime end = date.plusDays(1).atStartOfDay();
                    result = CPrepo.findCpIdByCreatedDateBetween(start, end);
                } catch (Exception e) {
                    throw new BusinessException(new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_RESOURCE,
                            "Invalid submitted date format. Expected yyyy-MM-dd"
                    ));
                }
                break;

            case "vendorname":
                result = CPrepo.findCpIdByPaymentToVendorContainingIgnoreCase(value);
                break;


            default:
                throw new BusinessException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_RESOURCE,
                        "Invalid search type: " + type
                ));
        }

        if (result == null || result.isEmpty()) {
            throw new BusinessException(new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "No matching CP IDs found for the given search criteria."
            ));
        }

        return result.stream()
                .map(SearchCpIdDto::new)
                .collect(Collectors.toList());

    }

}

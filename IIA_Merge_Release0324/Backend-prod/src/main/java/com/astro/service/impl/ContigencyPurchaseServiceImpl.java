package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.ProcurementDtos.*;

import com.astro.entity.ProcurementModule.ContigencyPurchase;

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

@Service
public class ContigencyPurchaseServiceImpl implements ContigencyPurchaseService {
    @Autowired
    private ContigencyPurchaseRepository CPrepo;
    @Autowired
    private WorkflowTransitionRepository workflowTransitionRepository;
    @Autowired
    private ObjectMapper objectMapper;

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
        cp.setVendorsName(contigencyPurchaseDto.getVendorName());
        cp.setVendorsInvoiceNo(contigencyPurchaseDto.getVendorInvoiceNo());
        cp.setPredifinedPurchaseStatement(contigencyPurchaseDto.getPredifinedPurchaseStatement());
        cp.setRemarksForPurchase(contigencyPurchaseDto.getRemarksForPurchase());
        String Date = contigencyPurchaseDto.getDate();
        if (Date != null) {
            cp.setDate(CommonUtils.convertStringToDateObject(contigencyPurchaseDto.getDate()));
        }else{
            cp.setDate(null);
        }

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
        CPrepo.save(cp);



        return mapToResponseDTO(cp);
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
       // WorkflowTransition wt = workflowTransitionRepository.findTopByRequestIdOrderByWorkflowSequenceDesc(contigencyPurchase.getContigencyId());
      //  dto.setStatus(wt.getStatus());
    //    dto.setProcessStage(wt.getNextRole());

        // Map list of CpMaterials to CpMaterialsResponseDto
        List<CpMaterialResponseDto> materialsDtoList = contigencyPurchase.getCpMaterials().stream()
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
        cp.setCurrentStatus("DRAFT");
        cp.setVendorsName(dto.getVendorName());
        cp.setVendorsInvoiceNo(dto.getVendorInvoiceNo());
        cp.setPredifinedPurchaseStatement(dto.getPredifinedPurchaseStatement());
        cp.setRemarksForPurchase(dto.getRemarksForPurchase());

        String date = dto.getDate();
        cp.setDate(date != null ? CommonUtils.convertStringToDateObject(date) : null);

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
        existing.setUploadCopyOfInvoiceFileName(dto.getUploadCopyOfInvoice());
        existing.setFileType(dto.getFileType());
        existing.setUpdatedBy(dto.getUpdatedBy());

        String date = dto.getDate();
        existing.setDate(date != null ? CommonUtils.convertStringToDateObject(date) : null);

        existing.getCpMaterials().clear();
        ModelMapper mapper = new ModelMapper();
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
        existing.setUploadCopyOfInvoiceFileName(dto.getUploadCopyOfInvoice());
        existing.setFileType(dto.getFileType());
        existing.setUpdatedBy(dto.getUpdatedBy());

        String date = dto.getDate();
        existing.setDate(date != null ? CommonUtils.convertStringToDateObject(date) : null);

        existing.getCpMaterials().clear();
        ModelMapper mapper = new ModelMapper();
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

        existing.setCurrentStatus(null);
        CPrepo.save(existing);

        return mapToResponseDTO(existing);
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

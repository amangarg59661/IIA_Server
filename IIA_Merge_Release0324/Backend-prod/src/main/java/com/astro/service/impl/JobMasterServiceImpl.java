package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.ApprovalAndRejectionRequestDTO;
import com.astro.dto.workflow.JobMasterRequestDto;
import com.astro.dto.workflow.JobMasterResponseDto;
import com.astro.entity.JobMaster;
import com.astro.entity.UserRoleMaster;
import com.astro.repository.UserRoleMasterRepository;

import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.JobMasterRepository;
import com.astro.repository.VendorNamesForJobWorkMaterialRepository;
import com.astro.service.JobMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobMasterServiceImpl implements JobMasterService {

    @Autowired
    private JobMasterRepository jobMasterRepository;
    @Autowired
    private VendorNamesForJobWorkMaterialRepository vendorNameRepository;
    @Autowired
    private UserRoleMasterRepository userRoleMasterRepository;
    @Override
    public JobMasterResponseDto createJobMaster(JobMasterRequestDto jobMasterRequestDto) {

        // Check for duplicate entry: same description + category + sub-category
        if (jobMasterRepository.existsByCategoryAndSubCategoryAndJobDescriptionIgnoreCase(
                jobMasterRequestDto.getCategory(),
                jobMasterRequestDto.getSubCategory(),
                jobMasterRequestDto.getJobDescription())) {
            ErrorDetails errorDetails = new ErrorDetails(400, 1, "Duplicate Job Entry",
                    "A job with the same description, category, and sub-category already exists.");
            throw new InvalidInputException(errorDetails);
        }

        // Generate sequential job code in format J000001
        Long maxSeq = jobMasterRepository.findMaxJobSequence();
        long nextSeq = (maxSeq == null) ? 1L : maxSeq + 1L;
        String jobCode = String.format("J%06d", nextSeq);
        JobMaster jobMaster = new JobMaster();
        jobMaster.setJobCode(jobCode);
        jobMaster.setCategory(jobMasterRequestDto.getCategory());
        jobMaster.setJobDescription(jobMasterRequestDto.getJobDescription());
        jobMaster.setAssetId(jobMasterRequestDto.getAssetId());
        jobMaster.setUom(jobMasterRequestDto.getUom());
        jobMaster.setValue(jobMasterRequestDto.getValue());
     //   jobMaster.setModeOfProcurement(jobMasterRequestDto.getModeOfProcurement());
        jobMaster.setCurrency(jobMasterRequestDto.getCurrency());
        jobMaster.setEstimatedPriceWithCcy(jobMasterRequestDto.getEstimatedPriceWithCcy());
        jobMaster.setBriefDescription(jobMasterRequestDto.getBriefDescription());
        jobMaster.setSubCategory(jobMasterRequestDto.getSubCategory());
        jobMaster.setCreatedBy(jobMasterRequestDto.getCreatedBy());
        jobMaster.setUpdatedBy(jobMasterRequestDto.getUpdatedBy());
        jobMaster.setOrigin(jobMasterRequestDto.getOrigin());
        jobMasterRepository.save(jobMaster);
     /*   // Saveing Vendornames in different table
        if (jobMasterRequestDto.getVendorNames() != null && !jobMasterRequestDto.getVendorNames().isEmpty()) {
            List<VendorNamesForJobWorkMaterial> vendors = jobMasterRequestDto.getVendorNames().stream().map(vendorName -> {
                VendorNamesForJobWorkMaterial vendor = new VendorNamesForJobWorkMaterial();
                vendor.setVendorName(vendorName);
                vendor.setJobCode(jobCode);
                return vendor;
            }).collect(Collectors.toList());

            vendorNameRepository.saveAll(vendors);


        }

      */


        return mapToResponseDTO(jobMaster);
    }



    @Override
    public JobMasterResponseDto updateJobMaster(String jobCode, JobMasterRequestDto jobMasterRequestDto) {

        JobMaster jobMaster= jobMasterRepository.findById(jobCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_VALIDATION,
                                "Job Code not found for the provided JOb code.")
                ));
        //jobMaster.setJobCode(jobMasterRequestDto.getJobCode());
        jobMaster.setCategory(jobMasterRequestDto.getCategory());
        jobMaster.setSubCategory(jobMasterRequestDto.getSubCategory());
        jobMaster.setJobDescription(jobMasterRequestDto.getJobDescription());
        jobMaster.setBriefDescription(jobMasterRequestDto.getBriefDescription());
        jobMaster.setAssetId(jobMasterRequestDto.getAssetId());
        jobMaster.setUom(jobMasterRequestDto.getUom());
        jobMaster.setValue(jobMasterRequestDto.getValue());
        jobMaster.setCurrency(jobMasterRequestDto.getCurrency());
        jobMaster.setEstimatedPriceWithCcy(jobMasterRequestDto.getEstimatedPriceWithCcy());
        jobMaster.setCreatedBy(jobMasterRequestDto.getCreatedBy());
        jobMaster.setUpdatedBy(jobMasterRequestDto.getUpdatedBy());
        jobMaster.setOrigin(jobMasterRequestDto.getOrigin());
        // After Indent Creator edits a CHANGE_REQUEST job, reset to AWAITING_APPROVAL
        // so it reappears in the SPO queue for re-approval
        if ("CHANGE_REQUEST".equals(jobMaster.getApprovalStatus())) {
            jobMaster.setApprovalStatus("AWAITING_APPROVAL");
        }
        jobMasterRepository.save(jobMaster);
      /*  // Saveing Vendornames in different table
        if (jobMasterRequestDto.getVendorNames() != null && !jobMasterRequestDto.getVendorNames().isEmpty()) {
            List<VendorNamesForJobWorkMaterial> vendors = jobMasterRequestDto.getVendorNames().stream().map(vendorName -> {
                VendorNamesForJobWorkMaterial vendor = new VendorNamesForJobWorkMaterial();
                vendor.setVendorName(vendorName);
                vendor.setJobCode(jobCode);
                return vendor;
            }).collect(Collectors.toList());

            vendorNameRepository.saveAll(vendors);
        }

       */




        return mapToResponseDTO(jobMaster);
    }

    @Override
    public List<JobMasterResponseDto> getAllJobMasters() {
        // Return all jobs (for admin use)
        List<JobMaster> jobMasters= jobMasterRepository.findAll();
        return jobMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<JobMasterResponseDto> getAllApprovedJobMasters() {
        List<JobMaster> jobMasters = jobMasterRepository.findByApprovalStatus("APPROVED");
        return jobMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<JobMasterResponseDto> getAllAwaitingApprovalJobs() {
        List<JobMaster> jobMasters = jobMasterRepository.findByApprovalStatus("AWAITING_APPROVAL");
        return jobMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<JobMasterResponseDto> getAllChangeRequestJobs() {
        List<JobMaster> jobMasters = jobMasterRepository.findByApprovalStatus("CHANGE_REQUEST");
        return jobMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public String performActionForJob(ApprovalAndRejectionRequestDTO request) {
        List<Integer> roleIds = userRoleMasterRepository.findAllRoleIdsByUserId(request.getActionBy());
        if (roleIds == null || roleIds.isEmpty()) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Unauthorised User!"
            ));
        }

        JobMaster job = jobMasterRepository.findById(request.getRequestId())
                .orElseThrow(() -> new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Job Code not found!"
                )));

        boolean isStorePurchaseOfficer = roleIds.contains(11);
        boolean isIndentCreator = roleIds.contains(1);

        switch (request.getAction().toUpperCase()) {
            case "APPROVED":
                if (isStorePurchaseOfficer) {
                    job.setApprovalStatus("APPROVED");
                    job.setComments(request.getRemarks());
                    jobMasterRepository.save(job);
                    return "Job " + job.getJobCode() + " has been APPROVED.";
                } else if (isIndentCreator && "CHANGE_REQUEST".equals(job.getApprovalStatus())) {
                    // Indent Creator re-submits after making changes → back to SPO queue
                    job.setApprovalStatus("AWAITING_APPROVAL");
                    job.setComments(request.getRemarks());
                    jobMasterRepository.save(job);
                    return "Job " + job.getJobCode() + " has been re-submitted for approval.";
                }
                throw new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Only Store Purchase Officer can approve jobs."
                ));
            case "REJECTED":
                job.setApprovalStatus("REJECTED");
                job.setComments(request.getRemarks());
                jobMasterRepository.save(job);
                return "Job " + job.getJobCode() + " has been REJECTED.";
            case "CHANGE REQUEST":
                if (isStorePurchaseOfficer) {
                    job.setApprovalStatus("CHANGE_REQUEST");
                    job.setComments(request.getRemarks());
                    jobMasterRepository.save(job);
                    return "Job " + job.getJobCode() + " sent back for changes.";
                }
                throw new InvalidInputException(new ErrorDetails(
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Only Store Purchase Officer can request changes on jobs."
                ));
            default:
                throw new InvalidInputException(new ErrorDetails(
                        AppConstant.INVALID_ACTION,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Invalid action. Use 'APPROVED', 'REJECTED', or 'CHANGE REQUEST'."
                ));
        }
    }

    @Override
    public JobMasterResponseDto getJobMasterById(String jobCode) {
        JobMaster jobMaster= jobMasterRepository.findById(jobCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Job Master not found for the provided job code.")
                ));
        return mapToResponseDTO(jobMaster);
    }

    @Override
    public List<JobMasterResponseDto> searchJobs(String keyword) {
        List<JobMaster> jobMasters = jobMasterRepository.searchApprovedJobs(keyword == null ? "" : keyword);
        return jobMasters.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteJobMaster(String jobCode) {

        JobMaster jobMaster=jobMasterRepository.findById(jobCode)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Job Master not found for the provided Job code."
                        )
                ));
        try {
            jobMasterRepository.delete(jobMaster);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the Job master."
                    ),
                    ex
            );
        }

    }

    private JobMasterResponseDto mapToResponseDTO(JobMaster jobMaster) {

        JobMasterResponseDto responseDto = new JobMasterResponseDto();
        responseDto.setJobCode(jobMaster.getJobCode());
        responseDto.setCategory(jobMaster.getCategory());
        responseDto.setJobDescription(jobMaster.getJobDescription());
        responseDto.setAssetId(jobMaster.getAssetId());
        responseDto.setUom(jobMaster.getUom());
        responseDto.setValue(jobMaster.getValue());
        responseDto.setBriefDescription(jobMaster.getBriefDescription());
        responseDto.setEstimatedPriceWithCcy(jobMaster.getEstimatedPriceWithCcy());
        responseDto.setCurrency(jobMaster.getCurrency());
        responseDto.setSubCategory(jobMaster.getSubCategory());
      //  responseDto.setModeOfProcurement(jobMaster.getModeOfProcurement());
        responseDto.setUpdatedBy(jobMaster.getUpdatedBy());
        responseDto.setCreatedBy(jobMaster.getCreatedBy());
        responseDto.setCreatedDate(jobMaster.getCreatedDate());
        responseDto.setUpdatedDate(jobMaster.getUpdatedDate());
        responseDto.setApprovalStatus(jobMaster.getApprovalStatus());
        responseDto.setComments(jobMaster.getComments());
        responseDto.setOrigin(jobMaster.getOrigin());
      /*  List<String> vendorNames = vendorNameRepository.findByJobCode(jobMaster.getJobCode())
                .stream()
                .map(VendorNamesForJobWorkMaterial::getVendorName)
                .collect(Collectors.toList());

        responseDto.setVendorNames(vendorNames);

       */

        return responseDto;
    }
}

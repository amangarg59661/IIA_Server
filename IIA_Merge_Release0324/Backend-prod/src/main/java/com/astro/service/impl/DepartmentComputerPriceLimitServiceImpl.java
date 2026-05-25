package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.DepartmentComputerPriceLimitRequestDTO;
import com.astro.dto.workflow.DepartmentComputerPriceLimitResponseDTO;
import com.astro.entity.DepartmentComputerPriceLimit;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.DepartmentComputerPriceLimitRepository;
import com.astro.service.DepartmentComputerPriceLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// Added by Aman 
import com.astro.dto.AdminPanel.LOVRequestDto;
import com.astro.entity.AdminPanel.LOVMaster;
import java.util.Optional;
// End

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentComputerPriceLimitServiceImpl implements DepartmentComputerPriceLimitService {

    @Autowired
    private DepartmentComputerPriceLimitRepository priceLimitRepository;

    @Override
    @Transactional
    public DepartmentComputerPriceLimitResponseDTO createPriceLimit(DepartmentComputerPriceLimitRequestDTO requestDTO) {

        // Check if price limit already exists for this department
        if (priceLimitRepository.existsByDepartmentNameIgnoreCaseAndIsActiveTrue(requestDTO.getDepartmentName())) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_INVALID,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Price limit already exists for department: " + requestDTO.getDepartmentName()
            ));
        }

        DepartmentComputerPriceLimit priceLimit = new DepartmentComputerPriceLimit();
        priceLimit.setDepartmentName(requestDTO.getDepartmentName());
        priceLimit.setPriceLimit(requestDTO.getPriceLimit());
        priceLimit.setIsActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : true);
        priceLimit.setRemarks(requestDTO.getRemarks());
        priceLimit.setCreatedDate(LocalDateTime.now());
        priceLimit.setUpdatedDate(LocalDateTime.now());

        DepartmentComputerPriceLimit savedPriceLimit = priceLimitRepository.save(priceLimit);
        return mapToResponseDTO(savedPriceLimit);
    }

    @Override
    @Transactional
    public DepartmentComputerPriceLimitResponseDTO updatePriceLimit(Long id, DepartmentComputerPriceLimitRequestDTO requestDTO) {

        DepartmentComputerPriceLimit priceLimit = priceLimitRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Price limit not found for the provided ID."
                        )
                ));

        // Check if changing department name conflicts with existing record
        if (!priceLimit.getDepartmentName().equalsIgnoreCase(requestDTO.getDepartmentName()) &&
                priceLimitRepository.existsByDepartmentNameIgnoreCaseAndIsActiveTrue(requestDTO.getDepartmentName())) {
            throw new InvalidInputException(new ErrorDetails(
                    AppConstant.ERROR_CODE_INVALID,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Price limit already exists for department: " + requestDTO.getDepartmentName()
            ));
        }

        priceLimit.setDepartmentName(requestDTO.getDepartmentName());
        priceLimit.setPriceLimit(requestDTO.getPriceLimit());
        priceLimit.setIsActive(requestDTO.getIsActive() != null ? requestDTO.getIsActive() : priceLimit.getIsActive());
        priceLimit.setRemarks(requestDTO.getRemarks());
        priceLimit.setUpdatedDate(LocalDateTime.now());

        DepartmentComputerPriceLimit updatedPriceLimit = priceLimitRepository.save(priceLimit);
        return mapToResponseDTO(updatedPriceLimit);
    }

    @Override
    public DepartmentComputerPriceLimitResponseDTO getPriceLimitById(Long id) {
        DepartmentComputerPriceLimit priceLimit = priceLimitRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Price limit not found for the provided ID."
                        )
                ));
        return mapToResponseDTO(priceLimit);
    }

    @Override
    public DepartmentComputerPriceLimitResponseDTO getPriceLimitByDepartment(String departmentName) {
        return priceLimitRepository
                .findByDepartmentNameIgnoreCaseAndIsActiveTrue(departmentName)
                .map(this::mapToResponseDTO)
                .orElse(null);
    }

    @Override
    public List<DepartmentComputerPriceLimitResponseDTO> getAllActivePriceLimits() {
        List<DepartmentComputerPriceLimit> priceLimits = priceLimitRepository.findByIsActiveTrue();
        return priceLimits.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentComputerPriceLimitResponseDTO> getAllPriceLimits() {
        List<DepartmentComputerPriceLimit> priceLimits = priceLimitRepository.findAllByOrderByDepartmentNameAsc();
        return priceLimits.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePriceLimit(Long id) {
        DepartmentComputerPriceLimit priceLimit = priceLimitRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Price limit not found for the provided ID."
                        )
                ));
        try {
            priceLimitRepository.delete(priceLimit);
        } catch (Exception ex) {
            throw new BusinessException(
                    new ErrorDetails(
                            AppConstant.INTER_SERVER_ERROR,
                            AppConstant.ERROR_TYPE_CODE_INTERNAL,
                            AppConstant.ERROR_TYPE_ERROR,
                            "An error occurred while deleting the price limit."
                    ),
                    ex
            );
        }
    }

    @Override
    @Transactional
    public void deactivatePriceLimit(Long id) {
        DepartmentComputerPriceLimit priceLimit = priceLimitRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                                AppConstant.ERROR_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                                AppConstant.ERROR_TYPE_RESOURCE,
                                "Price limit not found for the provided ID."
                        )
                ));
        priceLimit.setIsActive(false);
        priceLimit.setUpdatedDate(LocalDateTime.now());
        priceLimitRepository.save(priceLimit);
    }

    @Override
    public BigDecimal validateComputerItemPriceForDepartment(String departmentName, BigDecimal unitPrice, String materialSubCategory) {

        // Only validate for Computer & Peripherals category
        if (!"Computer & Peripherals".equalsIgnoreCase(materialSubCategory)) {
            return null; // No validation needed for non-computer items
        }

        // Check if department has a price limit configured
        DepartmentComputerPriceLimit priceLimit = priceLimitRepository
                .findByDepartmentNameIgnoreCaseAndIsActiveTrue(departmentName)
                .orElse(null);

        if (priceLimit == null) {
            // No price limit configured for this department, allow the purchase
            return null;
        }

        // Return the configured price limit for validation
        return priceLimit.getPriceLimit();
    }

    // Added by Aman 
 // ========== LOV-TRIGGERED METHODS ==========

    /**
     * Called by LOVServiceImpl when a new LOV is saved for designatorId = 13 (Department).
     * Creates a corresponding DepartmentComputerPriceLimit record.
     */
    @Override
    public void createFromLOV(LOVMaster saved, LOVRequestDto request) {
        DepartmentComputerPriceLimit limit = new DepartmentComputerPriceLimit();
        limit.setDepartmentCode(saved.getLovValue());
        limit.setDepartmentName(saved.getLovDisplayValue());
        limit.setPriceLimit(request.getDepartmentLimit());
        limit.setIsActive(saved.getIsActive());
        if (saved.getCreatedBy() != null) {
            limit.setCreatedBy(saved.getCreatedBy());
            limit.setUpdatedBy(saved.getCreatedBy());
        }
        priceLimitRepository.save(limit);
    }

    /**
     * Called by LOVServiceImpl when an existing LOV is updated for designatorId = 13 (Department).
     * Updates the corresponding DepartmentComputerPriceLimit record.
     */
    @Override
    public void updateFromLOV(LOVMaster updated, LOVRequestDto request) {
        Optional<DepartmentComputerPriceLimit> existing =
                priceLimitRepository.findByDepartmentCode(updated.getLovValue());

        DepartmentComputerPriceLimit limit = existing.get();
        limit.setDepartmentName(updated.getLovDisplayValue());
        limit.setPriceLimit(request.getDepartmentLimit());
        limit.setIsActive(updated.getIsActive());
        String actor = updated.getUpdatedBy() != null ? updated.getUpdatedBy() : updated.getCreatedBy();
        if (actor != null) {
            limit.setUpdatedBy(actor);
        }
        priceLimitRepository.save(limit);
    }

//      end

    private DepartmentComputerPriceLimitResponseDTO mapToResponseDTO(DepartmentComputerPriceLimit priceLimit) {
        DepartmentComputerPriceLimitResponseDTO responseDTO = new DepartmentComputerPriceLimitResponseDTO();
        responseDTO.setId(priceLimit.getId());
        responseDTO.setDepartmentName(priceLimit.getDepartmentName());
        responseDTO.setPriceLimit(priceLimit.getPriceLimit());
        responseDTO.setIsActive(priceLimit.getIsActive());
        responseDTO.setCreatedBy(priceLimit.getCreatedBy());
        responseDTO.setUpdatedBy(priceLimit.getUpdatedBy());
        responseDTO.setCreatedDate(priceLimit.getCreatedDate());
        responseDTO.setUpdatedDate(priceLimit.getUpdatedDate());
        responseDTO.setRemarks(priceLimit.getRemarks());
        return responseDTO;
    }
}

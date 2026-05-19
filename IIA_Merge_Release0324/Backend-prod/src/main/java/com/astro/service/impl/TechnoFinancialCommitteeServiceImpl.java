
package com.astro.service.impl;

import com.astro.dto.workflow.TechnoFinancialCommitteeDto;
import com.astro.entity.TechnoFinancialCommittee;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.TechnoFinancialCommitteeRepository;
import com.astro.service.TechnoFinancialCommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TechnoFinancialCommitteeServiceImpl implements TechnoFinancialCommitteeService {

    @Autowired
    private TechnoFinancialCommitteeRepository committeeRepository;

    @Override
    public List<TechnoFinancialCommitteeDto> getAllActiveMembers() {
        return committeeRepository.findByIsActiveTrue()
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public TechnoFinancialCommitteeDto addMember(TechnoFinancialCommitteeDto dto) {
        String role = dto.getRole() != null ? dto.getRole().toUpperCase() : "MEMBER";
        String committeeType = dto.getCommitteeType() != null ? dto.getCommitteeType().toUpperCase() : null;

        // For CHAIRMAN or CO_CHAIRMAN: only one allowed per committee type
        if ("CHAIRMAN".equals(role) || "CO_CHAIRMAN".equals(role)) {
            if (committeeType == null) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "committeeType (STEC_I or STEC_II) is required for CHAIRMAN and CO_CHAIRMAN roles."));
            }
            committeeRepository.findByRoleAndCommitteeTypeAndIsActiveTrue(role, committeeType)
                    .ifPresent(existing -> {
                        throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                                "A " + role + " already exists for " + committeeType + ": "
                                + existing.getMemberName() + ". Deactivate the existing one first."));
                    });
            // Ensure STEC-I and STEC-II have different Chair/Co-Chair
            if ("CHAIRMAN".equals(role)) {
                String otherType = "STEC_I".equals(committeeType) ? "STEC_II" : "STEC_I";
                committeeRepository.findByRoleAndCommitteeTypeAndIsActiveTrue("CHAIRMAN", otherType)
                        .ifPresent(other -> {
                            if (other.getUserId().equals(dto.getUserId())) {
                                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                                        "The same person cannot be Chairman of both STEC-I and STEC-II."));
                            }
                        });
            }
            if ("CO_CHAIRMAN".equals(role)) {
                String otherType = "STEC_I".equals(committeeType) ? "STEC_II" : "STEC_I";
                committeeRepository.findByRoleAndCommitteeTypeAndIsActiveTrue("CO_CHAIRMAN", otherType)
                        .ifPresent(other -> {
                            if (other.getUserId().equals(dto.getUserId())) {
                                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                                        "The same person cannot be Co-Chairman of both STEC-I and STEC-II."));
                            }
                        });
            }
        }
        TechnoFinancialCommittee entity = toEntity(dto);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setUpdatedDate(LocalDateTime.now());
        return toDto(committeeRepository.save(entity));
    }

    @Override
    public TechnoFinancialCommitteeDto updateMember(Long id, TechnoFinancialCommitteeDto dto) {
        TechnoFinancialCommittee entity = committeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1,
                        "NOT_FOUND", "Committee member not found: " + id)));

        entity.setMemberName(dto.getMemberName());
        entity.setDesignation(dto.getDesignation());
        entity.setEmailAddress(dto.getEmailAddress());
        entity.setRole(dto.getRole() != null ? dto.getRole().toUpperCase() : entity.getRole());
        entity.setCommitteeType(dto.getCommitteeType() != null ? dto.getCommitteeType().toUpperCase() : entity.getCommitteeType());
        entity.setIsActive(dto.getIsActive());
        entity.setUpdatedDate(LocalDateTime.now());
        return toDto(committeeRepository.save(entity));
    }

    @Override
    public void deactivateMember(Long id) {
        TechnoFinancialCommittee entity = committeeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1,
                        "NOT_FOUND", "Committee member not found: " + id)));
        entity.setIsActive(false);
        entity.setUpdatedDate(LocalDateTime.now());
        committeeRepository.save(entity);
    }

    @Override
    public TechnoFinancialCommitteeDto getChairman() {
        // With STEC_I and STEC_II having different chairmen, there can be multiple active chairmen.
        // This method returns the first one found; prefer getChairmanByType() for committee-specific lookups.
        List<TechnoFinancialCommittee> chairmen = committeeRepository.findByRoleAndIsActiveTrue("CHAIRMAN");
        if (chairmen.isEmpty()) {
            throw new BusinessException(new ErrorDetails(404, 1,
                    "NOT_FOUND", "No active Chairman configured in the Techno-Financial Committee."));
        }
        return toDto(chairmen.get(0));
    }

    @Override
    public TechnoFinancialCommitteeDto getChairmanByType(String committeeType) {
        return committeeRepository.findByRoleAndCommitteeTypeAndIsActiveTrue("CHAIRMAN", committeeType.toUpperCase())
                .map(this::toDto)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1,
                        "NOT_FOUND", "No active Chairman configured for " + committeeType + ".")));
    }

    @Override
    public List<TechnoFinancialCommitteeDto> getMembersByType(String committeeType) {
        return committeeRepository.findByCommitteeTypeAndIsActiveTrue(committeeType.toUpperCase())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private TechnoFinancialCommitteeDto toDto(TechnoFinancialCommittee e) {
        TechnoFinancialCommitteeDto dto = new TechnoFinancialCommitteeDto();
        dto.setId(e.getId());
        dto.setUserId(e.getUserId());
        dto.setEmployeeId(e.getEmployeeId());
        dto.setMemberName(e.getMemberName());
        dto.setDesignation(e.getDesignation());
        dto.setEmailAddress(e.getEmailAddress());
        dto.setRole(e.getRole());
        dto.setCommitteeType(e.getCommitteeType());
        dto.setIsActive(e.getIsActive());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setCreatedDate(e.getCreatedDate());
        dto.setUpdatedDate(e.getUpdatedDate());
        return dto;
    }

    private TechnoFinancialCommittee toEntity(TechnoFinancialCommitteeDto dto) {
        TechnoFinancialCommittee e = new TechnoFinancialCommittee();
        e.setUserId(dto.getUserId());
        e.setEmployeeId(dto.getEmployeeId());
        e.setMemberName(dto.getMemberName());
        e.setDesignation(dto.getDesignation());
        e.setEmailAddress(dto.getEmailAddress());
        e.setRole(dto.getRole() != null ? dto.getRole().toUpperCase() : "MEMBER");
        e.setCommitteeType(dto.getCommitteeType() != null ? dto.getCommitteeType().toUpperCase() : null);
        e.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        e.setCreatedBy(dto.getCreatedBy());
        return e;
    }
}
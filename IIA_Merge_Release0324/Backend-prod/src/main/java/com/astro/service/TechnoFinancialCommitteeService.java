
package com.astro.service;

import com.astro.dto.workflow.TechnoFinancialCommitteeDto;

import java.util.List;

public interface TechnoFinancialCommitteeService {

    List<TechnoFinancialCommitteeDto> getAllActiveMembers();

    TechnoFinancialCommitteeDto addMember(TechnoFinancialCommitteeDto dto);

    TechnoFinancialCommitteeDto updateMember(Long id, TechnoFinancialCommitteeDto dto);

    void deactivateMember(Long id);

    TechnoFinancialCommitteeDto getChairman();

    /** Get CHAIRMAN of a specific STEC type (STEC_I or STEC_II). */
    TechnoFinancialCommitteeDto getChairmanByType(String committeeType);

    /** Get all active members of a specific STEC type. */
    List<TechnoFinancialCommitteeDto> getMembersByType(String committeeType);
}
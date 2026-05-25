
package com.astro.service;

import com.astro.dto.workflow.CommitteeNominationDto;
import com.astro.dto.workflow.TechnoFinancialCommitteeDto;

import java.util.List;
import java.util.Map;

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

    /** Chairman nominates a user as committee member for a specific tender. */
    Map<String, Object> nominateMember(CommitteeNominationDto dto);

    /** Deactivate Committee Member role for nominated members when PO is generated. */
    void deactivateNominatedMemberRoles(String tenderId);
}
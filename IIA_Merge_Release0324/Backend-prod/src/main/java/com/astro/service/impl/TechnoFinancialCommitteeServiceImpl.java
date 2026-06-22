
package com.astro.service.impl;

import com.astro.dto.workflow.CommitteeNominationDto;
import com.astro.dto.workflow.TechnoFinancialCommitteeDto;
import com.astro.entity.ProcurementModule.TenderEvaluation;
import com.astro.entity.ProcurementModule.TenderRequest;
import com.astro.entity.RoleMaster;
import com.astro.entity.TechnoFinancialCommittee;
import com.astro.entity.TenderCommitteeDecision;
import com.astro.entity.UserMaster;
import com.astro.entity.UserRoleMaster;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.ProcurementModule.TenderEvaluationRepository;
import com.astro.repository.ProcurementModule.TenderRequestRepository;
import com.astro.repository.RoleMasterRepository;
import com.astro.repository.TechnoFinancialCommitteeRepository;
import com.astro.repository.TenderCommitteeDecisionRepository;
import com.astro.repository.EmployeeDepartmentMasterRepository;
import com.astro.repository.UserMasterRepository;
import com.astro.repository.UserRoleMasterRepository;
import com.astro.service.TechnoFinancialCommitteeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TechnoFinancialCommitteeServiceImpl implements TechnoFinancialCommitteeService {

    private static final Logger log = LoggerFactory.getLogger(TechnoFinancialCommitteeServiceImpl.class);
    private static final String COMMITTEE_MEMBER_ROLE = "Committee Member";

    @Autowired
    private TechnoFinancialCommitteeRepository committeeRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Autowired
    private UserRoleMasterRepository userRoleMasterRepository;

    @Autowired
    private RoleMasterRepository roleMasterRepository;

    @Autowired
    private EmployeeDepartmentMasterRepository employeeDepartmentMasterRepository;

    @Autowired
    private TenderCommitteeDecisionRepository committeeDecisionRepository;

    @Autowired
    private TenderEvaluationRepository tenderEvaluationRepository;

    @Autowired
    private TenderRequestRepository tenderRequestRepository;

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

    @Override
    @Transactional
    public Map<String, Object> nominateMember(CommitteeNominationDto dto) {
        // 1. Validate tender exists
        TenderRequest tender = tenderRequestRepository.findById(dto.getTenderId())
                .orElseThrow(() -> new BusinessException(new ErrorDetails(404, 1,
                        "NOT_FOUND", "Tender not found: " + dto.getTenderId())));

        if (tender.getLockedForPO() != null) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Tender already has PO generated. Cannot nominate members."));
        }

        // 2. Validate caller is chairman for this tender's STEC type
        TenderEvaluation eval = tenderEvaluationRepository.findByTenderId(dto.getTenderId());
        if (eval == null) {
            throw new BusinessException(new ErrorDetails(404, 1,
                    "NOT_FOUND", "Tender evaluation not found for: " + dto.getTenderId()));
        }

        String amtCat = eval.getAmountCategory();

        if ("ABOVE_1_CRORE".equals(amtCat)) {
            // Above 1 Crore uses ad-hoc committee — validate against ad-hoc chairman
            if (eval.getAdHocChairmanUserId() == null) {
                throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                        "No ad-hoc Chairman assigned yet for this ABOVE_1_CRORE tender."));
            }
            if (!eval.getAdHocChairmanUserId().equals(dto.getNominatedBy())) {
                throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "Only the ad-hoc Chairman (" + eval.getAdHocChairmanName()
                        + ") can nominate members for ABOVE_1_CRORE tenders."));
            }
        } else {
            String committeeType = "ABOVE_10_LAKH_UPTO_50_LAKH".equals(amtCat) ? "STEC_I" : "STEC_II";

            TechnoFinancialCommittee chairman = committeeRepository
                    .findByRoleAndCommitteeTypeAndIsActiveTrue("CHAIRMAN", committeeType)
                    .orElseThrow(() -> new BusinessException(new ErrorDetails(400, 1,
                            "CONFIGURATION_ERROR", "No active Chairman configured for " + committeeType)));

            if (!chairman.getUserId().equals(dto.getNominatedBy())) {
                throw new BusinessException(new ErrorDetails(403, 1, "FORBIDDEN",
                        "Only the " + committeeType + " Chairman (" + chairman.getMemberName()
                        + ") can nominate members."));
            }
        }

        // 3. Block self-nomination
        if (dto.getUserId().equals(dto.getNominatedBy())) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "Chairman cannot nominate themselves as a committee member."));
        }

        // 4. Check user not already assigned to this tender
        if (committeeDecisionRepository.findByTenderIdAndCommitteeUserId(
                dto.getTenderId(), dto.getUserId()).isPresent()) {
            throw new BusinessException(new ErrorDetails(400, 1, "VALIDATION",
                    "User is already assigned to this tender's committee."));
        }

        // 5. Get user details
        UserMaster user = userMasterRepository.findByUserId(dto.getUserId());
        if (user == null) {
            throw new BusinessException(new ErrorDetails(404, 1,
                    "NOT_FOUND", "User not found: " + dto.getUserId()));
        }

        // 6. Auto-assign Committee Member role
        boolean roleAssigned = ensureCommitteeMemberRole(dto.getUserId());

        // 7. Create TenderCommitteeDecision row
        String resolvedExpertName = (dto.getExpertName() != null && !dto.getExpertName().isBlank())
                ? dto.getExpertName()
                : user.getUserName();
        String displayName = dto.isExpert()
                ? resolvedExpertName + " (Expert)"
                : user.getUserName();

        TenderCommitteeDecision decision = new TenderCommitteeDecision();
        decision.setTenderId(dto.getTenderId());
        decision.setCommitteeUserId(dto.getUserId());
        decision.setCommitteeMemberName(displayName);
        decision.setCreatedDate(LocalDateTime.now());
        decision.setUpdatedDate(LocalDateTime.now());
        committeeDecisionRepository.save(decision);

        // 8. Audit trail: record expert assignment on chairman's row
        if (dto.isExpert()) {
            TenderCommitteeDecision chairRow = committeeDecisionRepository
                    .findByTenderIdAndCommitteeUserId(dto.getTenderId(), dto.getNominatedBy())
                    .orElseGet(() -> {
                        TenderCommitteeDecision r = new TenderCommitteeDecision();
                        r.setTenderId(dto.getTenderId());
                        r.setCommitteeUserId(dto.getNominatedBy());
                        r.setCreatedDate(LocalDateTime.now());
                        return r;
                    });
            chairRow.setExpertUserId(dto.getUserId());
            chairRow.setExpertName(resolvedExpertName);
            chairRow.setExpertAssignedDate(LocalDateTime.now());
            chairRow.setUpdatedDate(LocalDateTime.now());
            committeeDecisionRepository.save(chairRow);
        }

        log.info("Chairman {} nominated user {} (expert={}) for tender {}. Role assigned: {}",
                dto.getNominatedBy(), dto.getUserId(), dto.isExpert(), dto.getTenderId(), roleAssigned);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("userId", dto.getUserId());
        result.put("userName", displayName);
        result.put("tenderId", dto.getTenderId());
        result.put("expert", dto.isExpert());
        result.put("roleAssigned", roleAssigned);
        result.put("message", roleAssigned
                ? "Member nominated and Committee Member role assigned."
                : "Member nominated. Committee Member role was already active.");
        return result;
    }

    private boolean ensureCommitteeMemberRole(Integer userId) {
        RoleMaster role = roleMasterRepository.findByRoleName(COMMITTEE_MEMBER_ROLE)
                .orElseThrow(() -> new BusinessException(new ErrorDetails(500, 1,
                        "CONFIGURATION_ERROR",
                        "Role '" + COMMITTEE_MEMBER_ROLE + "' not found in ROLE_MASTER. Admin must create it first.")));

        UserRoleMaster existing = userRoleMasterRepository.findByRoleIdAndUserId(role.getRoleId(), userId);

        if (existing != null) {
            if (Boolean.TRUE.equals(existing.getIsActive())) {
                return false;
            }
            existing.setIsActive(true);
            userRoleMasterRepository.save(existing);
            log.info("Reactivated Committee Member role for userId {}", userId);
            return true;
        }

        UserRoleMaster newRole = new UserRoleMaster();
        newRole.setUserId(userId);
        newRole.setRoleId(role.getRoleId());
        newRole.setReadPermission(true);
        newRole.setWritePermission(true);
        newRole.setIsActive(true);
        newRole.setCreatedDate(new java.util.Date());
        userRoleMasterRepository.save(newRole);
        log.info("Assigned new Committee Member role to userId {}", userId);
        return true;
    }

    @Override
    @Transactional
    public void deactivateNominatedMemberRoles(String tenderId) {
        List<TenderCommitteeDecision> decisions = committeeDecisionRepository.findByTenderId(tenderId);
        if (decisions.isEmpty()) return;

        RoleMaster role = roleMasterRepository.findByRoleName(COMMITTEE_MEMBER_ROLE).orElse(null);
        if (role == null) {
            log.warn("Role '{}' not found in ROLE_MASTER. Skipping role deactivation for tender {}.",
                    COMMITTEE_MEMBER_ROLE, tenderId);
            return;
        }

        Set<Integer> processedUserIds = new HashSet<>();
        for (TenderCommitteeDecision d : decisions) {
            Integer userId = d.getCommitteeUserId();
            if (userId == null || !processedUserIds.add(userId)) continue;

            // Skip permanent STEC members
            if (committeeRepository.findByUserIdAndIsActiveTrue(userId).isPresent()) {
                log.debug("User {} is permanent STEC member. Keeping role active.", userId);
                continue;
            }

            // Skip if user has other active tender assignments
            int activeCount = committeeDecisionRepository
                    .countActiveAssignmentsExcludingTender(userId, tenderId);
            if (activeCount > 0) {
                log.debug("User {} has {} other active tender assignments. Keeping role active.",
                        userId, activeCount);
                continue;
            }

            // Deactivate role
            UserRoleMaster urm = userRoleMasterRepository.findByRoleIdAndUserId(role.getRoleId(), userId);
            if (urm != null && Boolean.TRUE.equals(urm.getIsActive())) {
                urm.setIsActive(false);
                userRoleMasterRepository.save(urm);
                log.info("Deactivated Committee Member role for userId {} (tender {} PO generated).",
                        userId, tenderId);
            }
        }
    }

    @Override
    public List<Map<String, Object>> getEligibleExperts(String tenderId) {
        Set<Integer> committeeUserIds = committeeDecisionRepository.findByTenderId(tenderId)
                .stream()
                .map(TenderCommitteeDecision::getCommitteeUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Batch-load employee department data keyed by employeeId
        Map<String, com.astro.entity.EmployeeDepartmentMaster> empMap =
                employeeDepartmentMasterRepository.findAll().stream()
                        .collect(Collectors.toMap(
                                com.astro.entity.EmployeeDepartmentMaster::getEmployeeId,
                                e -> e,
                                (a, b) -> a));

        return userMasterRepository.findAll().stream()
                .filter(u -> u.getUserId() != null && !committeeUserIds.contains(u.getUserId()))
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("userId", u.getUserId());
                    m.put("userName", u.getUserName());
                    m.put("roleName", u.getRoleName());
                    m.put("employeeId", u.getEmployeeId());
                    com.astro.entity.EmployeeDepartmentMaster emp =
                            u.getEmployeeId() != null ? empMap.get(u.getEmployeeId()) : null;
                    m.put("department", emp != null ? emp.getDepartmentName() : null);
                    return m;
                })
                .collect(Collectors.toList());
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
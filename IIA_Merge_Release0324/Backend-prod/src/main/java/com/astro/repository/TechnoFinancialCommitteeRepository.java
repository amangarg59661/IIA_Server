
package com.astro.repository;

import com.astro.entity.TechnoFinancialCommittee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnoFinancialCommitteeRepository extends JpaRepository<TechnoFinancialCommittee, Long> {

    List<TechnoFinancialCommittee> findByIsActiveTrue();

    Optional<TechnoFinancialCommittee> findByUserIdAndIsActiveTrue(Integer userId);

    /** Returns all active members with the given role (may return multiple for CHAIRMAN across STEC types). */
    List<TechnoFinancialCommittee> findByRoleAndIsActiveTrue(String role);

    List<TechnoFinancialCommittee> findByRoleAndIsActiveTrue(String role, org.springframework.data.domain.Sort sort);

    boolean existsByUserId(Integer userId);

    // Queries by committeeType (STEC_I / STEC_II)
    List<TechnoFinancialCommittee> findByCommitteeTypeAndIsActiveTrue(String committeeType);

    Optional<TechnoFinancialCommittee> findByRoleAndCommitteeTypeAndIsActiveTrue(String role, String committeeType);

    List<TechnoFinancialCommittee> findByCommitteeTypeInAndIsActiveTrue(List<String> committeeTypes);
}
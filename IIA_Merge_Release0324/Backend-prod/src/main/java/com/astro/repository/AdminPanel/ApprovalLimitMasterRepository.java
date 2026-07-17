package com.astro.repository.AdminPanel;

import com.astro.entity.AdminPanel.ApprovalLimitMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalLimitMasterRepository extends JpaRepository<ApprovalLimitMaster, Long> {

    List<ApprovalLimitMaster> findByRoleIdAndIsActiveTrue(Integer roleId);

    List<ApprovalLimitMaster> findByRoleNameAndIsActiveTrue(String roleName);

    List<ApprovalLimitMaster> findByCategoryAndIsActiveTrue(String category);

    List<ApprovalLimitMaster> findByIsActiveTrueOrderByPriorityAsc();

    Optional<ApprovalLimitMaster> findByRoleNameAndCategoryAndIsActiveTrue(String roleName, String category);

    /**
     * Find applicable limits for a given context (role, category, department, location)
     * Uses priority ordering and matches either specific or 'ALL' values
     */
    @Query("SELECT a FROM ApprovalLimitMaster a WHERE " +
           "(a.roleName = :roleName OR a.roleId = :roleId) " +
           "AND (a.category = :category OR a.category = 'ALL' OR a.category IS NULL) " +
           "AND (a.departmentName = :dept OR a.departmentName IS NULL) " +
           "AND (a.location = :location OR a.location = 'ALL' OR a.location IS NULL) " +
           "AND a.isActive = true " +
           "ORDER BY a.priority ASC")
    List<ApprovalLimitMaster> findApplicableLimits(
            @Param("roleId") Integer roleId,
            @Param("roleName") String roleName,
            @Param("category") String category,
            @Param("dept") String departmentName,
            @Param("location") String location
    );

    /**
     * Find limit by role name with optional category filter
     */
    @Query("SELECT a FROM ApprovalLimitMaster a WHERE " +
           "a.roleName = :roleName " +
           "AND (a.category = :category OR a.category = 'ALL' OR a.category IS NULL) " +
           "AND a.isActive = true " +
           "ORDER BY a.priority ASC")
    List<ApprovalLimitMaster> findByRoleNameAndCategory(
            @Param("roleName") String roleName,
            @Param("category") String category
    );

    /**
     * Find all active limits ordered by role name and priority
     */
//     @Query("SELECT a FROM ApprovalLimitMaster a WHERE a.isActive = true " +
//            "ORDER BY a.roleName ASC, a.priority ASC")
//     List<ApprovalLimitMaster> findAllActiveOrderByRoleAndPriority();
@Query("SELECT a FROM ApprovalLimitMaster a WHERE a.isActive = true " +
       "AND (:workflowId IS NULL OR a.workflowId = :workflowId) " +
       "ORDER BY a.roleName ASC, a.priority ASC")
List<ApprovalLimitMaster> findAllActiveOrderByRoleAndPriority(@Param("workflowId") Integer workflowId);
    /**
     * Find all distinct role names with configured limits
     */
    @Query("SELECT DISTINCT a.roleName FROM ApprovalLimitMaster a WHERE a.isActive = true AND a.roleName IS NOT NULL ORDER BY a.roleName")
    List<String> findDistinctRoleNames();

    /**
     * Find all distinct categories
     */
    @Query("SELECT DISTINCT a.category FROM ApprovalLimitMaster a WHERE a.isActive = true AND a.category IS NOT NULL ORDER BY a.category")
    List<String> findDistinctCategories();

    List<ApprovalLimitMaster> findByWorkflowIdAndIsActiveTrue(Integer workflowId);
List<ApprovalLimitMaster> findByWorkflowIdAndIsActiveFalse(Integer workflowId);
}

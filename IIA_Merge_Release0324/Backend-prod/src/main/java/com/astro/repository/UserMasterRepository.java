package com.astro.repository;

import com.astro.entity.UserMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserMasterRepository extends JpaRepository<UserMaster, Integer> {
    UserMaster findByUserIdAndPassword(Integer userId, String password);

    Optional<UserMaster> findByCreatedBy(String createdBy);

    UserMaster findByUserId(Integer createdBy);

    @Query("SELECT u FROM UserMaster u WHERE u.userId IN :ids")
    List<UserMaster> findByUserIdIn(@Param("ids") Set<Integer> ids);
    
    @Query("SELECT u.userName FROM UserMaster u WHERE u.userId = :userId")
    String findUserNameByUserId(@Param("userId") Integer userId);
    
    // NEW METHOD
    Optional<UserMaster> findByEmployeeId(String employeeId);

    // Search users by keyword (username, email, mobile, employee ID)
    @Query(value = "SELECT u.user_id, u.user_name, u.email, u.mobile_number, u.employee_id, " +
                   "e.employee_name, " +
                   "GROUP_CONCAT(DISTINCT rm.ROLENAME SEPARATOR ', ') as role_names, " +
                   "u.created_by, u.created_date, u.is_active " +
                   "FROM user_master u " +
                   "LEFT JOIN employee_department_master e ON u.employee_id = e.employee_id " +
                   "LEFT JOIN user_role_master urm ON u.user_id = urm.USERID " +
                   "LEFT JOIN role_master rm ON urm.ROLEID = rm.ROLEID " +
                   "WHERE (LOWER(u.user_name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                   "   OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                   "   OR LOWER(u.mobile_number) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                   "   OR LOWER(u.employee_id) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                   "   OR LOWER(e.employee_name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                   "GROUP BY u.user_id, u.user_name, u.email, u.mobile_number, u.employee_id, " +
                   "e.employee_name, u.created_by, u.created_date, u.is_active " +
                   "ORDER BY u.user_id DESC " +
                   "LIMIT 50",
            nativeQuery = true)
    List<Object[]> searchUsersByKeyword(@Param("keyword") String keyword);

    // Get all users with roles for listing
    @Query(value = "SELECT u.user_id, u.user_name, u.email, u.mobile_number, u.employee_id, " +
                   "e.employee_name, " +
                   "GROUP_CONCAT(DISTINCT rm.ROLENAME SEPARATOR ', ') as role_names, " +
                   "u.created_by, u.created_date, u.is_active " +
                   "FROM user_master u " +
                   "LEFT JOIN employee_department_master e ON u.employee_id = e.employee_id " +
                   "LEFT JOIN user_role_master urm ON u.user_id = urm.USERID " +
                   "LEFT JOIN role_master rm ON urm.ROLEID = rm.ROLEID " +
                   "GROUP BY u.user_id, u.user_name, u.email, u.mobile_number, u.employee_id, " +
                   "e.employee_name, u.created_by, u.created_date, u.is_active " +
                   "ORDER BY u.user_id DESC",
            nativeQuery = true)
    List<Object[]> getAllUsersWithRoles();

    // added findEmailByRoleName
    @Query("SELECT u.email FROM UserMaster u JOIN UserRoleMaster ur ON u.userId = ur.userId JOIN RoleMaster r ON ur.roleId = r.roleId WHERE r.roleName = :roleName")
    String findEmailByRoleName(@Param("roleName") String roleName);

    // Check if username already exists
    Optional<UserMaster> findByUserName(String userName);

    // Check if email already exists
    Optional<UserMaster> findByEmail(String email);

    // Find user by role name and employee location (for location-based workflow routing).
    // Prefers exact location match; falls back to employees with location = 'ALL'.
    @Query(value = "SELECT u.* FROM user_master u " +
                   "JOIN user_role_master urm ON u.user_id = urm.USERID " +
                   "JOIN role_master rm ON urm.ROLEID = rm.ROLEID " +
                   "JOIN employee_department_master e ON u.employee_id = e.employee_id " +
                   "WHERE LOWER(TRIM(rm.ROLENAME)) = LOWER(TRIM(:roleName)) " +
                   "AND (LOWER(TRIM(e.location)) = LOWER(TRIM(:location)) OR UPPER(TRIM(e.location)) = 'ALL') " +
                   "AND e.status = 'Active' " +
                   "ORDER BY CASE WHEN UPPER(TRIM(e.location)) = 'ALL' THEN 1 ELSE 0 END ASC, u.user_id ASC LIMIT 1",
            nativeQuery = true)
    Optional<UserMaster> findByRoleNameAndEmployeeLocation(
            @Param("roleName") String roleName,
            @Param("location") String location);
}
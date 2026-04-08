package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.LoginRoleDto;
import com.astro.dto.workflow.UserDto;
import com.astro.dto.workflow.UserRoleDto;
import com.astro.dto.workflow.UserSearchResponseDto;
import com.astro.dto.workflow.userRequestDto;
import com.astro.entity.EmployeeDepartmentMaster;
import com.astro.entity.RoleMaster;
import com.astro.entity.UserMaster;
import com.astro.exception.BusinessException;
import com.astro.entity.UserRoleMaster;
import com.astro.exception.ErrorDetails;
import com.astro.exception.InvalidInputException;
import com.astro.repository.EmployeeDepartmentMasterRepository;
import com.astro.repository.RoleMasterRepository;
import com.astro.repository.UserMasterRepository;
import com.astro.repository.UserRoleMasterRepository;
import com.astro.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMasterRepository userMasterRepository;

    @Autowired
    UserRoleMasterRepository userRoleMasterRepository;

    @Autowired
    RoleMasterRepository roleMasterRepository;
    
    @Autowired
    EmployeeDepartmentMasterRepository employeeRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void validateUser(Integer userId) {
        UserMaster userMaster = userMasterRepository.findById(userId)
            .orElseThrow(() -> new InvalidInputException(
                new ErrorDetails(
                    AppConstant.USER_NOT_FOUND, 
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, 
                    "User not found."
                )
            ));
    }

    @Override
    public UserRoleDto login(UserDto userDto) {
        UserRoleDto userRoleDto = null;

        if(Objects.nonNull(userDto.getEmployeeId()) && Objects.nonNull(userDto.getPassword())){
            UserMaster userMaster = userMasterRepository.findByEmployeeId(userDto.getEmployeeId())
                .orElse(null);
                
            if(Objects.isNull(userMaster)){
                throw new InvalidInputException(
                    new ErrorDetails(
                        AppConstant.USER_NOT_FOUND,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "User not found."
                    )
                );
            }

            // Check if user is active
            if(Boolean.FALSE.equals(userMaster.getIsActive())){
                throw new InvalidInputException(
                    new ErrorDetails(
                        AppConstant.USER_NOT_FOUND,
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Your account has been deactivated. Please contact the administrator."
                    )
                );
            }

            // Verify encrypted password
            if(!passwordEncoder.matches(userDto.getPassword(), userMaster.getPassword())){
                throw new InvalidInputException(
                    new ErrorDetails(
                        AppConstant.USER_NOT_FOUND, 
                        AppConstant.ERROR_TYPE_CODE_VALIDATION,
                        AppConstant.ERROR_TYPE_VALIDATION, 
                        "Invalid credentials."
                    )
                );
            }

            List<UserRoleMaster> userRoles = userRoleMasterRepository.findAllByUserId(userMaster.getUserId());
            Optional<EmployeeDepartmentMaster> employee = employeeRepo.findByEmployeeId(userMaster.getEmployeeId());

            userRoleDto = new UserRoleDto();
            userRoleDto.setUserId(userMaster.getUserId());
            userRoleDto.setCreatedBy(userMaster.getCreatedBy());
            userRoleDto.setUserName(userMaster.getUserName());
            userRoleDto.setMobileNumber(userMaster.getMobileNumber());
            userRoleDto.setEmail(userMaster.getEmail());

            // TC_14 FIX: Set first login flag to prompt password change
            userRoleDto.setIsFirstLogin(userMaster.getIsFirstLogin() != null ? userMaster.getIsFirstLogin() : true);

            List<LoginRoleDto> roleDtos = userRoles.stream().map(role -> {
                LoginRoleDto dto = new LoginRoleDto();
                dto.setUserRoleId(role.getUserRoleId());
                dto.setRoleId(role.getRoleId());
                dto.setRoleName(roleNameById(role.getRoleId()));
                dto.setReadPermission(role.getReadPermission());
                dto.setWritePermission(role.getWritePermission());
                return dto;
            }).collect(Collectors.toList());
            
            userRoleDto.setRoles(roleDtos);
            
            if(employee.isPresent()){
                EmployeeDepartmentMaster emp = employee.get();
                userRoleDto.setEmployeeDepartment(emp.getDepartmentName());
            } else {
                userRoleDto.setEmployeeDepartment(null);
            }
        } else {
            throw new InvalidInputException(
                new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT, 
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION, 
                    "Invalid input."
                )
            );
        }

        return userRoleDto;
    }

    private String roleNameById(Integer roleId) {
        if(Objects.nonNull(roleId)) {
            return roleMasterRepository.findById(roleId)
                .orElse(new RoleMaster())
                .getRoleName();
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public UserDto createUser(userRequestDto userDto) {
        return createUserWithEncryption(userDto);
    }
    
    @Override
    @Transactional
    public UserDto createUserWithEncryption(userRequestDto userDto) {
        // Validate required fields
        if(userDto.getUserName() == null || userDto.getUserName().trim().isEmpty()) {
            throw new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Username is required"
                )
            );
        }

        if(userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Email is required"
                )
            );
        }

        if(userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            throw new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Password is required"
                )
            );
        }

        // TC_16 FIX: Validate employee ID exists in employee_department_master table
        if(userDto.getEmployeeId() != null && !userDto.getEmployeeId().trim().isEmpty()) {
            // First check if employee exists
            Optional<EmployeeDepartmentMaster> employee = employeeRepo.findByEmployeeId(userDto.getEmployeeId());
            if(!employee.isPresent()) {
                throw new BusinessException(
                    new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Employee ID does not exist in the system. Please register the employee first."
                    )
                );
            }

            // Then check if user already exists for this employee ID
            Optional<UserMaster> existingUser = userMasterRepository.findByEmployeeId(userDto.getEmployeeId());
            if(existingUser.isPresent()) {
                throw new BusinessException(
                    new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "User already exists for this employee ID"
                    )
                );
            }
        }

        UserMaster userMaster = new UserMaster();
        userMaster.setUserName(userDto.getUserName());

        // Set mobileNumber - use empty string if null to avoid database constraint issues
        userMaster.setMobileNumber(userDto.getMobileNumber() != null ? userDto.getMobileNumber() : "");

        // Encrypt password before saving
        userMaster.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userMaster.setEmail(userDto.getEmail());
        userMaster.setCreatedBy(userDto.getCreatedBy());

        // Set employeeId - use empty string if null
        userMaster.setEmployeeId(userDto.getEmployeeId() != null ? userDto.getEmployeeId() : "");

        // Determine role names - support both single roleName and list roleNames
        List<String> rolesToAssign = new ArrayList<>();
        if (userDto.getRoleNames() != null && !userDto.getRoleNames().isEmpty()) {
            rolesToAssign = userDto.getRoleNames();
        } else if (userDto.getRoleName() != null && !userDto.getRoleName().trim().isEmpty()) {
            rolesToAssign.add(userDto.getRoleName());
        }

        // Don't save role_name in user_master - roles are properly stored in user_role_master table
        userMaster.setRoleName(null);

        // Save user
        userMasterRepository.save(userMaster);

        // Save each role in user_role_master
        for (String roleName : rolesToAssign) {
            RoleMaster role = roleMasterRepository.findFirstByRoleName(roleName)
                .orElseThrow(() -> new BusinessException(
                    new ErrorDetails(
                        AppConstant.ERROR_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_CODE_RESOURCE,
                        AppConstant.ERROR_TYPE_VALIDATION,
                        "Role with name '" + roleName + "' not found."
                    )
                ));

            UserRoleMaster userRole = new UserRoleMaster();
            userRole.setUserId(userMaster.getUserId());
            userRole.setRoleId(role.getRoleId());
            userRole.setReadPermission(true);
            userRole.setWritePermission(true);
            userRole.setCreatedBy(userDto.getCreatedBy());
            userRole.setCreatedDate(new Date());
            userRoleMasterRepository.save(userRole);
        }

        return mapToResponseDTO(userMaster);
    }

    @Override
    @Transactional
    public UserDto updateUser(int userId, userRequestDto userDto) {
        UserMaster userMaster = userMasterRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "User not found for the provided user ID."
                )
            ));

        userMaster.setUserName(userDto.getUserName());

        // Only update password if a new one is provided
        if(userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            userMaster.setPassword(passwordEncoder.encode(userDto.getPassword()));
            // Added by aman 
            userMaster.setIsFirstLogin(true);
            // END

        }

        userMaster.setMobileNumber(userDto.getMobileNumber());
        userMaster.setEmail(userDto.getEmail());
        userMaster.setCreatedBy(userDto.getCreatedBy());

        // Update employee ID if provided
        if(userDto.getEmployeeId() != null) {
            userMaster.setEmployeeId(userDto.getEmployeeId());
        }

        userMasterRepository.save(userMaster);

        // Update roles if provided
        List<String> rolesToAssign = new ArrayList<>();
        if (userDto.getRoleNames() != null && !userDto.getRoleNames().isEmpty()) {
            rolesToAssign = userDto.getRoleNames();
        } else if (userDto.getRoleName() != null && !userDto.getRoleName().trim().isEmpty()) {
            // Handle comma-separated role names
            String[] roleArray = userDto.getRoleName().split(",");
            for (String role : roleArray) {
                if (role.trim().length() > 0) {
                    rolesToAssign.add(role.trim());
                }
            }
        }

        if (!rolesToAssign.isEmpty()) {
            // Delete existing roles for this user
            List<UserRoleMaster> existingRoles = userRoleMasterRepository.findAllByUserId(userId);
            if (existingRoles != null && !existingRoles.isEmpty()) {
                userRoleMasterRepository.deleteAll(existingRoles);
            }

            // Add new roles
            for (String roleName : rolesToAssign) {
                RoleMaster role = roleMasterRepository.findFirstByRoleName(roleName)
                    .orElseThrow(() -> new BusinessException(
                        new ErrorDetails(
                            AppConstant.ERROR_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_CODE_RESOURCE,
                            AppConstant.ERROR_TYPE_VALIDATION,
                            "Role with name '" + roleName + "' not found."
                        )
                    ));

                UserRoleMaster userRole = new UserRoleMaster();
                userRole.setUserId(userMaster.getUserId());
                userRole.setRoleId(role.getRoleId());
                userRole.setReadPermission(true);
                userRole.setWritePermission(true);
                userRole.setCreatedBy(userDto.getCreatedBy());
                userRole.setCreatedDate(new Date());
                userRoleMasterRepository.save(userRole);
            }
        }

        return mapToResponseDTO(userMaster);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserMaster> userMasters = userMasterRepository.findAll();
        return userMasters.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(int userId) {
        UserMaster userMaster = userMasterRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "User not found for the provided user ID."
                )
            ));
        return mapToResponseDTO(userMaster);
    }

    @Override
    public Optional<UserMaster> getUserMasterByCreatedBy(String createdBy) {
        return userMasterRepository.findByCreatedBy(createdBy);
    }

    @Override
    @Transactional
    public void deleteUser(int userId) {
        UserMaster userMaster = userMasterRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "User not found for the provided ID."
                )
            ));
        try {
            userMasterRepository.delete(userMaster);
        } catch (Exception ex) {
            throw new BusinessException(
                new ErrorDetails(
                    AppConstant.INTER_SERVER_ERROR,
                    AppConstant.ERROR_TYPE_CODE_INTERNAL,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "An error occurred while deleting the user."
                ),
                ex
            );
        }
    }
    
    @Override
    @Transactional
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        UserMaster userMaster = userMasterRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.USER_NOT_FOUND,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "User not found."
                )
            ));

        // Verify old password
        if(!passwordEncoder.matches(oldPassword, userMaster.getPassword())) {
            throw new BusinessException(
                new ErrorDetails(
                    AppConstant.USER_INVALID_INPUT,
                    AppConstant.ERROR_TYPE_CODE_VALIDATION,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Old password is incorrect."
                )
            );
        }

        // Set new encrypted password
        userMaster.setPassword(passwordEncoder.encode(newPassword));

        // TC_14 FIX: Mark as not first login after password change
        userMaster.setIsFirstLogin(false);
        userMaster.setLastPasswordChangeDate(java.time.LocalDateTime.now());

        userMasterRepository.save(userMaster);
    }
    
    @Override
    public boolean userExistsByEmployeeId(String employeeId) {
        return userMasterRepository.findByEmployeeId(employeeId).isPresent();
    }

    private UserDto mapToResponseDTO(UserMaster userMaster) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userMaster.getUserId());
        userDto.setUserName(userMaster.getUserName());
        // Don't return password in DTO
        userDto.setPassword(null);
        userDto.setEmail(userMaster.getEmail());
        userDto.setMobileNumber(userMaster.getMobileNumber());
        userDto.setEmployeeId(userMaster.getEmployeeId());
        userDto.setCreatedDate(userMaster.getCreatedDate());
        userDto.setCreatedBy(userMaster.getCreatedBy());

        // FIX: Fetch roles from user_role_master table for this user
        List<UserRoleMaster> userRoles = userRoleMasterRepository.findAllByUserId(userMaster.getUserId());
        if (userRoles != null && !userRoles.isEmpty()) {
            List<String> roleNames = userRoles.stream()
                    .map(ur -> {
                        Optional<RoleMaster> role = roleMasterRepository.findById(ur.getRoleId());
                        return role.map(RoleMaster::getRoleName).orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            userDto.setRoleNames(roleNames);
            // Also set the combined role names as the primary roleName for display
            if (!roleNames.isEmpty()) {
                userDto.setRoleName(String.join(", ", roleNames));
            }
        } else {
            // Fall back to roleName from user_master if no roles in junction table
            userDto.setRoleName(userMaster.getRoleName());
            if (userMaster.getRoleName() != null && !userMaster.getRoleName().isEmpty()) {
                userDto.setRoleNames(List.of(userMaster.getRoleName()));
            }
        }

        userDto.setIsActive(userMaster.getIsActive() != null ? userMaster.getIsActive() : true);

        return userDto;
    }

    @Override
    @Transactional
    public UserDto toggleUserStatus(int userId) {
        UserMaster userMaster = userMasterRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "User not found for the provided user ID."
                )
            ));

        Boolean currentStatus = userMaster.getIsActive() != null ? userMaster.getIsActive() : true;
        userMaster.setIsActive(!currentStatus);
        userMasterRepository.save(userMaster);

        return mapToResponseDTO(userMaster);
    }

    @Override
    @Transactional
    public UserDto activateUser(int userId) {
        UserMaster userMaster = userMasterRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "User not found for the provided user ID."
                )
            ));

        userMaster.setIsActive(true);
        userMasterRepository.save(userMaster);

        return mapToResponseDTO(userMaster);
    }

    @Override
    @Transactional
    public UserDto deactivateUser(int userId) {
        UserMaster userMaster = userMasterRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "User not found for the provided user ID."
                )
            ));

        userMaster.setIsActive(false);
        userMasterRepository.save(userMaster);

        return mapToResponseDTO(userMaster);
    }

    @Override
    public List<UserSearchResponseDto> searchUsers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllUsersWithRoles();
        }

        List<Object[]> results = userMasterRepository.searchUsersByKeyword(keyword.trim());
        return results.stream()
                .map(this::mapToUserSearchResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSearchResponseDto> getAllUsersWithRoles() {
        List<Object[]> results = userMasterRepository.getAllUsersWithRoles();
        return results.stream()
                .map(this::mapToUserSearchResponseDto)
                .collect(Collectors.toList());
    }

    private UserSearchResponseDto mapToUserSearchResponseDto(Object[] row) {
        UserSearchResponseDto dto = new UserSearchResponseDto();
        dto.setUserId(row[0] != null ? ((Number) row[0]).intValue() : null);
        dto.setUserName(row[1] != null ? row[1].toString() : null);
        dto.setEmail(row[2] != null ? row[2].toString() : null);
        dto.setMobileNumber(row[3] != null ? row[3].toString() : null);
        dto.setEmployeeId(row[4] != null ? row[4].toString() : null);
        dto.setEmployeeName(row[5] != null ? row[5].toString() : null);
        dto.setRoleNames(row[6] != null ? row[6].toString() : null);
        dto.setCreatedBy(row[7] != null ? row[7].toString() : null);
        dto.setCreatedDate(row[8] != null ? row[8].toString() : null);
        dto.setIsActive(row.length > 9 && row[9] != null ? Boolean.valueOf(row[9].toString()) : true);
        return dto;
    }
}
package com.astro.service.impl;

import com.astro.constant.AppConstant;
import com.astro.dto.workflow.*;
import com.astro.entity.DepartmentMaster;
import com.astro.entity.DesignationMaster;
import com.astro.entity.EmployeeDepartmentMaster;
import com.astro.entity.EmployeeIdSequence;
import com.astro.entity.UserMaster;
import com.astro.exception.BusinessException;
import com.astro.exception.ErrorDetails;
import com.astro.repository.DepartmentMasterRepository;
import com.astro.repository.DesignationMasterRepository;
import com.astro.repository.EmployeeDepartmentMasterRepository;
import com.astro.repository.EmployeeIdSequenceRepository;
import com.astro.repository.UserMasterRepository;
import com.astro.service.EmployeeDepartmentMasterService;
import com.astro.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeDepartmentMasterServiceImpl implements EmployeeDepartmentMasterService {

    @Autowired
    private EmployeeDepartmentMasterRepository employeeRepository;
    
    @Autowired
    private EmployeeIdSequenceRepository employeeIdSequenceRepository;
    
    @Autowired
    private DesignationMasterRepository designationMasterRepository;
    
    @Autowired
    private DepartmentMasterRepository departmentMasterRepository;

    @Autowired
    private UserMasterRepository userMasterRepository;

    @Autowired
private UserService userService;

@Override
@Transactional
public EmployeeDepartmentMasterResponseDto createEmployeeDepartmentWithUser(EmployeeDepartmentMasterRequestDto employeeRequestDto) {
    // Modified by Aman
    // Validate phone number
   /* if (!employeeRequestDto.getPhoneNumber().matches("^[0-9]{10}$")) {
        throw new BusinessException(
            new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Phone number must be exactly 10 digits"
            )
        );
    }*/
    // End

    // Create employee first
    Integer maxNumber = employeeIdSequenceRepository.findMaxEmployeeId();
    int nextNumber = (maxNumber == null) ? 1100 : maxNumber + 1;
    String employeeId = "E" + nextNumber;

    EmployeeIdSequence em = new EmployeeIdSequence();
    em.setEmployeeId(nextNumber);
    employeeIdSequenceRepository.save(em);
    
    EmployeeDepartmentMaster employee = new EmployeeDepartmentMaster();
    employee.setEmployeeId(employeeId);
    employee.setEmployeeName(employeeRequestDto.getEmployeeName());
    employee.setFirstName(employeeRequestDto.getFirstName());
    employee.setLastName(employeeRequestDto.getLastName());
    employee.setLocation(employeeRequestDto.getLocation());
    employee.setDepartmentName(employeeRequestDto.getDepartmentName());
    employee.setDesignation(employeeRequestDto.getDesignation());
    employee.setPhoneNumber(employeeRequestDto.getPhoneNumber());
    employee.setEmailAddress(employeeRequestDto.getEmailAddress());
    employee.setAddress(employeeRequestDto.getAddress());

    // New address fields
    employee.setStreetAddress(employeeRequestDto.getStreetAddress());
    employee.setCity(employeeRequestDto.getCity());
    employee.setState(employeeRequestDto.getState());
    employee.setPinCode(employeeRequestDto.getPinCode());

    // Reporting Officer (replaces manager)
    employee.setReportingOfficerId(employeeRequestDto.getReportingOfficerId());
    employee.setReportingOfficerName(employeeRequestDto.getReportingOfficerName());

    employee.setDateOfBirth(employeeRequestDto.getDateOfBirth());
    employee.setHireDate(employeeRequestDto.getHireDate());
    employee.setEndDate(employeeRequestDto.getEndDate());
    employee.setEmploymentType(employeeRequestDto.getEmploymentType());
    employee.setStatus(employeeRequestDto.getStatus() != null ? employeeRequestDto.getStatus() : "Active");
    employee.setCreatedBy(employeeRequestDto.getCreatedBy());
    employee.setUpdatedBy(employeeRequestDto.getUpdatedBy());
    employee.setCreatedDate(LocalDateTime.now());
    employee.setUpdatedDate(LocalDateTime.now());
    employee.setIsDraft(false);

    employeeRepository.save(employee);

    Integer createdUserId = null;  // Track created userId
    
    // Create user account if requested
    if(employeeRequestDto.getCreateUserAccount() != null && 
       employeeRequestDto.getCreateUserAccount() && 
       employeeRequestDto.getUserPassword() != null &&
       !employeeRequestDto.getUserPassword().isEmpty()) {
        
        userRequestDto userRequest = new userRequestDto();
        userRequest.setUserName(employeeRequestDto.getUserName() != null ? 
                                 employeeRequestDto.getUserName() : 
                                 employeeRequestDto.getEmployeeName());
        userRequest.setPassword(employeeRequestDto.getUserPassword());
        userRequest.setEmail(employeeRequestDto.getEmailAddress());
        userRequest.setMobileNumber(employeeRequestDto.getPhoneNumber());
        userRequest.setEmployeeId(employeeId);
        userRequest.setRoleNames(employeeRequestDto.getUserRoles());
        userRequest.setCreatedBy(employeeRequestDto.getCreatedBy());
        
        UserDto createdUser = userService.createUserWithEncryption(userRequest);  // ✅ CHANGED: Capture response
        createdUserId = createdUser.getUserId();  // ✅ ADD THIS: Store userId
    }
    
    EmployeeDepartmentMasterResponseDto response = mapToResponseDTO(employee);
    response.setUserId(createdUserId);  // ✅ ADD THIS: Include userId in response
    return response;
}


@Override
public String getDepartmentByEmployeeName(String employeeName) {
    if (employeeName == null || employeeName.trim().isEmpty()) {
        return null;
    }
    
    Optional<EmployeeDepartmentMaster> employee = employeeRepository
        .findByEmployeeNameIgnoreCaseAndStatusAndIsDraftFalse(employeeName.trim(), "Active");
    
    if (employee.isPresent()) {
        return employee.get().getDepartmentName();
    }
    
    return null;
}

    @Override
    @Transactional
    public EmployeeDepartmentMasterResponseDto createEmployeeDepartment(EmployeeDepartmentMasterRequestDto employeeRequestDto) {
        // Modified by Aman
        // // Validate phone number
        // if (!employeeRequestDto.getPhoneNumber().matches("^[0-9]{10}$")) {
        //     throw new BusinessException(
        //         new ErrorDetails(
        //             AppConstant.ERROR_CODE_RESOURCE,
        //             AppConstant.ERROR_TYPE_CODE_RESOURCE,
        //             AppConstant.ERROR_TYPE_VALIDATION,
        //             "Phone number must be exactly 10 digits"
        //         )
        //     );
        // }
      

        // Integer maxNumber = employeeIdSequenceRepository.findMaxEmployeeId();
        // int nextNumber = (maxNumber == null) ? 1100 : maxNumber + 1;

        // String employeeId = "E" + nextNumber;

        // EmployeeIdSequence em = new EmployeeIdSequence();
        // em.setEmployeeId(nextNumber);
        // employeeIdSequenceRepository.save(em);
        // ✅ Check if employeeId already exists in DB
    if (employeeRequestDto.getEmployeeId() != null && !employeeRequestDto.getEmployeeId().trim().isEmpty()) {
        boolean exists = employeeRepository.existsById(employeeRequestDto.getEmployeeId().trim());
        if (exists) {
            throw new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Employee with ID '" + employeeRequestDto.getEmployeeId() + "' already exists."
                )
            );
        }
    }
    // ENd
        
        EmployeeDepartmentMaster employee = new EmployeeDepartmentMaster();
        employee.setEmployeeId(employeeRequestDto.getEmployeeId());
        
        employee.setEmployeeName(employeeRequestDto.getEmployeeName());
        employee.setFirstName(employeeRequestDto.getFirstName());
        employee.setLastName(employeeRequestDto.getLastName());
        employee.setLocation(employeeRequestDto.getLocation());
        employee.setDepartmentName(employeeRequestDto.getDepartmentName());
        employee.setDesignation(employeeRequestDto.getDesignation());
        employee.setPhoneNumber(employeeRequestDto.getPhoneNumber());
        employee.setEmailAddress(employeeRequestDto.getEmailAddress());
        employee.setAddress(employeeRequestDto.getAddress());

        // New address fields
        employee.setStreetAddress(employeeRequestDto.getStreetAddress());
        employee.setCity(employeeRequestDto.getCity());
        employee.setState(employeeRequestDto.getState());
        employee.setPinCode(employeeRequestDto.getPinCode());

        // Reporting Officer (replaces manager)
        employee.setReportingOfficerId(employeeRequestDto.getReportingOfficerId());
        employee.setReportingOfficerName(employeeRequestDto.getReportingOfficerName());

        employee.setDateOfBirth(employeeRequestDto.getDateOfBirth());
        employee.setHireDate(employeeRequestDto.getHireDate());
        employee.setEndDate(employeeRequestDto.getEndDate());
        employee.setEmploymentType(employeeRequestDto.getEmploymentType());
        employee.setStatus(employeeRequestDto.getStatus() != null ? employeeRequestDto.getStatus() : "Active");
        employee.setCreatedBy(employeeRequestDto.getCreatedBy());
        employee.setUpdatedBy(employeeRequestDto.getUpdatedBy());
        employee.setCreatedDate(LocalDateTime.now());
        employee.setUpdatedDate(LocalDateTime.now());

        employeeRepository.save(employee);
        return mapToResponseDTO(employee);
    }

    private EmployeeDepartmentMasterResponseDto mapToResponseDTO(EmployeeDepartmentMaster employee) {
        EmployeeDepartmentMasterResponseDto responseDto = new EmployeeDepartmentMasterResponseDto();
        responseDto.setEmployeeId(employee.getEmployeeId());
        responseDto.setLocation(employee.getLocation());
        responseDto.setEmployeeName(employee.getEmployeeName());
        responseDto.setDepartmentName(employee.getDepartmentName());
        responseDto.setDesignation(employee.getDesignation());
        responseDto.setPhoneNumber(employee.getPhoneNumber());
        responseDto.setEmailAddress(employee.getEmailAddress());
        responseDto.setAddress(employee.getAddress());

        // New fields - split name
        responseDto.setFirstName(employee.getFirstName());
        responseDto.setLastName(employee.getLastName());

        // New fields - split address
        responseDto.setStreetAddress(employee.getStreetAddress());
        responseDto.setCity(employee.getCity());
        responseDto.setState(employee.getState());
        responseDto.setPinCode(employee.getPinCode());

        // New fields - Reporting Officer (replaces manager)
        responseDto.setReportingOfficerId(employee.getReportingOfficerId());
        responseDto.setReportingOfficerName(employee.getReportingOfficerName());

        responseDto.setStatus(employee.getStatus());
        responseDto.setIsDraft(employee.getIsDraft());
        responseDto.setCreatedBy(employee.getCreatedBy());
        responseDto.setUpdatedBy(employee.getUpdatedBy());
        responseDto.setCreatedDate(employee.getCreatedDate());
        responseDto.setUpdatedDate(employee.getUpdatedDate());
        return responseDto;
    }

    @Override
    @Transactional
    public EmployeeDepartmentMasterResponseDto updateEmployeeDepartmentMaster(String employeeId, EmployeeDepartmentMasterRequestDto employeeRequestDto) {
        
        // Validate phone number
        if (!employeeRequestDto.getPhoneNumber().matches("^[0-9]{10}$")) {
            throw new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Phone number must be exactly 10 digits"
                )
            );
        }

        EmployeeDepartmentMaster employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_VALIDATION,
                    "Employee department not found for the provided employee id."
                )
            ));

        employee.setEmployeeName(employeeRequestDto.getEmployeeName());
        employee.setFirstName(employeeRequestDto.getFirstName());
        employee.setLastName(employeeRequestDto.getLastName());
        employee.setDepartmentName(employeeRequestDto.getDepartmentName());
        employee.setLocation(employeeRequestDto.getLocation());
        employee.setDesignation(employeeRequestDto.getDesignation());
        employee.setPhoneNumber(employeeRequestDto.getPhoneNumber());
        employee.setEmailAddress(employeeRequestDto.getEmailAddress());
        employee.setAddress(employeeRequestDto.getAddress());

        // Update new address fields
        employee.setStreetAddress(employeeRequestDto.getStreetAddress());
        employee.setCity(employeeRequestDto.getCity());
        employee.setState(employeeRequestDto.getState());
        employee.setPinCode(employeeRequestDto.getPinCode());

        // Update Reporting Officer (replaces manager)
        employee.setReportingOfficerId(employeeRequestDto.getReportingOfficerId());
        employee.setReportingOfficerName(employeeRequestDto.getReportingOfficerName());

        if (employeeRequestDto.getDateOfBirth() != null) employee.setDateOfBirth(employeeRequestDto.getDateOfBirth());
        if (employeeRequestDto.getHireDate() != null) employee.setHireDate(employeeRequestDto.getHireDate());
        if (employeeRequestDto.getEndDate() != null) employee.setEndDate(employeeRequestDto.getEndDate());
        if (employeeRequestDto.getEmploymentType() != null) employee.setEmploymentType(employeeRequestDto.getEmploymentType());

        if (employeeRequestDto.getStatus() != null) {
            employee.setStatus(employeeRequestDto.getStatus());
        }
        employee.setUpdatedBy(employeeRequestDto.getUpdatedBy());
        employee.setUpdatedDate(LocalDateTime.now());

        employeeRepository.save(employee);
        return mapToResponseDTO(employee);
    }

    @Override
    public List<EmployeeDepartmentMasterResponseDto> getAllEmployeeDepartmentMasters() {
        List<EmployeeDepartmentMaster> employees = employeeRepository.findAll();
        return employees.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<employeedto> getAllEmployeeDepartmentMasterswithName() {
        List<EmployeeDepartmentMaster> employees = employeeRepository.findAll();
        return employees.stream()
            .map(emp -> new employeedto(emp.getEmployeeId(), emp.getEmployeeName()))
            .collect(Collectors.toList());
    }

    @Override
    public EmployeeDepartmentMasterResponseDto getEmployeeDepartmentMasterById(String employeeId) {
        EmployeeDepartmentMaster employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Employee not found for the provided employee id."
                )
            ));
        return mapToResponseDTO(employee);
    }

    @Override
    @Transactional
    public void deleteEmployeeDepartmentMasterr(String employeeId) {
        EmployeeDepartmentMaster employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Employee not found for the provided employee ID."
                )
            ));
        try {
            employeeRepository.delete(employee);
        } catch (Exception ex) {
            throw new BusinessException(
                new ErrorDetails(
                    AppConstant.INTER_SERVER_ERROR,
                    AppConstant.ERROR_TYPE_CODE_INTERNAL,
                    AppConstant.ERROR_TYPE_ERROR,
                    "An error occurred while deleting the employee."
                ),
                ex
            );
        }
    }

    @Override
    public List<EmployeeSearchResponseDto> searchEmployees(String keyword) {
        List<Object[]> results = employeeRepository.searchEmployeesForDropdown(keyword);
        return results.stream()
            .map(obj -> new EmployeeSearchResponseDto(
                (String) obj[0],  // employee_id
                (String) obj[1],  // employee_name
                (String) obj[2],  // department_name
                (String) obj[3],  // designation
                (String) obj[4]   // status
            ))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeDepartmentMasterResponseDto activateEmployee(String employeeId, String updatedBy) {
        EmployeeDepartmentMaster employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Employee not found for the provided employee id."
                )
            ));
        
        employee.setStatus("Active");
        employee.setUpdatedBy(updatedBy);
        employee.setUpdatedDate(LocalDateTime.now());
        employeeRepository.save(employee);
        
        return mapToResponseDTO(employee);
    }

    @Override
    @Transactional
    public EmployeeDepartmentMasterResponseDto deactivateEmployee(String employeeId, String updatedBy) {
        EmployeeDepartmentMaster employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Employee not found for the provided employee id."
                )
            ));
        
        employee.setStatus("Inactive");
        employee.setUpdatedBy(updatedBy);
        employee.setUpdatedDate(LocalDateTime.now());
        employeeRepository.save(employee);
        
        return mapToResponseDTO(employee);
    }

    @Override
    public List<DesignationDto> getAllDesignations() {
        List<DesignationMaster> designations = designationMasterRepository.findByIsActiveTrue();
        return designations.stream()
            .map(d -> new DesignationDto(d.getId(), d.getDesignationName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentDto> getAllDepartments() {
        List<DepartmentMaster> departments = departmentMasterRepository.findByIsActiveTrue();
        return departments.stream()
            .map(d -> new DepartmentDto(d.getId(), d.getDepartmentName()))
            .collect(Collectors.toList());
    }

    @Override
@Transactional
public EmployeeDepartmentMasterResponseDto saveAsDraft(EmployeeDepartmentMasterRequestDto requestDto) {
    
    EmployeeDepartmentMaster employee;
    
    // Check if updating existing draft or creating new
    if (requestDto.getEmployeeId() != null && !requestDto.getEmployeeId().isEmpty()) {
        // Update existing draft
        employee = employeeRepository.findById(requestDto.getEmployeeId())
            .orElseThrow(() -> new BusinessException(
                new ErrorDetails(
                    AppConstant.ERROR_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_CODE_RESOURCE,
                    AppConstant.ERROR_TYPE_RESOURCE,
                    "Draft not found for the provided employee id."
                )
            ));
    } else {
        // Create new draft with generated ID
        Integer maxNumber = employeeIdSequenceRepository.findMaxEmployeeId();
        int nextNumber = (maxNumber == null) ? 1100 : maxNumber + 1;
        String employeeId = "E" + nextNumber;

        EmployeeIdSequence em = new EmployeeIdSequence();
        em.setEmployeeId(nextNumber);
        employeeIdSequenceRepository.save(em);

        employee = new EmployeeDepartmentMaster();
        employee.setEmployeeId(employeeId);
        employee.setCreatedDate(LocalDateTime.now());
    }

    // Set fields - allowing partial/empty values for drafts
    employee.setEmployeeName(requestDto.getEmployeeName() != null ? requestDto.getEmployeeName() : "");
    employee.setFirstName(requestDto.getFirstName());
    employee.setLastName(requestDto.getLastName());
    employee.setLocation(requestDto.getLocation() != null ? requestDto.getLocation() : "");
    employee.setDepartmentName(requestDto.getDepartmentName() != null ? requestDto.getDepartmentName() : "");
    employee.setDesignation(requestDto.getDesignation() != null ? requestDto.getDesignation() : "");
    employee.setPhoneNumber(requestDto.getPhoneNumber() != null ? requestDto.getPhoneNumber() : "");
    employee.setEmailAddress(requestDto.getEmailAddress() != null ? requestDto.getEmailAddress() : "");
    employee.setAddress(requestDto.getAddress() != null ? requestDto.getAddress() : "");

    // New address fields
    employee.setStreetAddress(requestDto.getStreetAddress());
    employee.setCity(requestDto.getCity());
    employee.setState(requestDto.getState());
    employee.setPinCode(requestDto.getPinCode());

    // Reporting Officer (replaces manager)
    employee.setReportingOfficerId(requestDto.getReportingOfficerId());
    employee.setReportingOfficerName(requestDto.getReportingOfficerName());

    employee.setDateOfBirth(requestDto.getDateOfBirth());
    employee.setHireDate(requestDto.getHireDate());
    employee.setEndDate(requestDto.getEndDate());
    employee.setEmploymentType(requestDto.getEmploymentType());
    employee.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : "Active");
    employee.setIsDraft(true);
    employee.setCreatedBy(requestDto.getCreatedBy());
    employee.setUpdatedBy(requestDto.getUpdatedBy());
    employee.setUpdatedDate(LocalDateTime.now());

    employeeRepository.save(employee);
    return mapToResponseDTO(employee);
}

@Override
public List<EmployeeDepartmentMasterResponseDto> getDraftsByUser(String userId) {
    List<EmployeeDepartmentMaster> drafts = employeeRepository.findByCreatedByAndIsDraftTrue(userId);
    return drafts.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
}

@Override
public List<EmployeeDepartmentMasterResponseDto> getAllDrafts() {
    List<EmployeeDepartmentMaster> drafts = employeeRepository.findByIsDraftTrue();
    return drafts.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
}

@Override
@Transactional
public EmployeeDepartmentMasterResponseDto submitDraft(String employeeId, EmployeeDepartmentMasterRequestDto requestDto) {
    
    EmployeeDepartmentMaster employee = employeeRepository.findById(employeeId)
        .orElseThrow(() -> new BusinessException(
            new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "Draft not found for the provided employee id."
            )
        ));

    // Validate all required fields before submitting
    validateRequiredFields(requestDto);

    // Update all fields
    employee.setEmployeeName(requestDto.getEmployeeName());
    employee.setFirstName(requestDto.getFirstName());
    employee.setLastName(requestDto.getLastName());
    employee.setLocation(requestDto.getLocation());
    employee.setDepartmentName(requestDto.getDepartmentName());
    employee.setDesignation(requestDto.getDesignation());
    employee.setPhoneNumber(requestDto.getPhoneNumber());
    employee.setEmailAddress(requestDto.getEmailAddress());
    employee.setAddress(requestDto.getAddress());

    // New address fields
    employee.setStreetAddress(requestDto.getStreetAddress());
    employee.setCity(requestDto.getCity());
    employee.setState(requestDto.getState());
    employee.setPinCode(requestDto.getPinCode());

    // Reporting Officer (replaces manager)
    employee.setReportingOfficerId(requestDto.getReportingOfficerId());
    employee.setReportingOfficerName(requestDto.getReportingOfficerName());

    employee.setDateOfBirth(requestDto.getDateOfBirth());
    employee.setHireDate(requestDto.getHireDate());
    employee.setEndDate(requestDto.getEndDate());
    employee.setEmploymentType(requestDto.getEmploymentType());
    employee.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : "Active");
    employee.setIsDraft(false); // Mark as submitted
    employee.setUpdatedBy(requestDto.getUpdatedBy());
    employee.setUpdatedDate(LocalDateTime.now());

    employeeRepository.save(employee);
    return mapToResponseDTO(employee);
}

private void validateRequiredFields(EmployeeDepartmentMasterRequestDto requestDto) {
    StringBuilder errors = new StringBuilder();

    if (requestDto.getEmployeeName() == null || requestDto.getEmployeeName().trim().isEmpty()) {
        errors.append("Employee name is required. ");
    }
    if (requestDto.getDepartmentName() == null || requestDto.getDepartmentName().trim().isEmpty()) {
        errors.append("Department is required. ");
    }
    if (requestDto.getDesignation() == null || requestDto.getDesignation().trim().isEmpty()) {
        errors.append("Designation is required. ");
    }
    if (requestDto.getLocation() == null || requestDto.getLocation().trim().isEmpty()) {
        errors.append("Location is required. ");
    }
    if (requestDto.getPhoneNumber() == null || !requestDto.getPhoneNumber().matches("^[0-9]{10}$")) {
        errors.append("Valid 10-digit phone number is required. ");
    }
    if (requestDto.getEmailAddress() == null || requestDto.getEmailAddress().trim().isEmpty()) {
        errors.append("Email address is required. ");
    }
    if (requestDto.getAddress() == null || requestDto.getAddress().trim().isEmpty()) {
        errors.append("Address is required. ");
    }
    // New mandatory fields validation
    if (requestDto.getCity() == null || requestDto.getCity().trim().isEmpty()) {
        errors.append("City is required. ");
    }
    if (requestDto.getState() == null || requestDto.getState().trim().isEmpty()) {
        errors.append("State is required. ");
    }
    if (requestDto.getReportingOfficerId() == null || requestDto.getReportingOfficerId().trim().isEmpty()) {
        errors.append("Reporting Officer is required. ");
    }

    if (errors.length() > 0) {
        throw new BusinessException(
            new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_VALIDATION,
                errors.toString().trim()
            )
        );
    }
}

@Override
public List<employeedto> getEmployeesByDepartment(String departmentName) {
    List<EmployeeDepartmentMaster> employees = employeeRepository
        .findByDepartmentNameAndStatusAndIsDraftFalse(departmentName, "Active");
    return employees.stream()
        .map(emp -> new employeedto(emp.getEmployeeId(), emp.getEmployeeName()))
        .collect(Collectors.toList());
}

@Override
public EmployeeDepartmentMasterResponseDto getEmployeeDetailsByUserId(Integer userId) {
    // Step 1: Get user by userId to get employeeId
    UserMaster user = userMasterRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(
            new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "User not found for the provided user ID: " + userId
            )
        ));

    // Step 2: Get employee by employeeId
    String employeeId = user.getEmployeeId();

    if (employeeId == null || employeeId.trim().isEmpty()) {
        throw new BusinessException(
            new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "This user is not linked to any employee record."
            )
        );
    }

    EmployeeDepartmentMaster employee = employeeRepository.findByEmployeeId(employeeId)
        .orElseThrow(() -> new BusinessException(
            new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_RESOURCE,
                "Employee not found for employee ID: " + employeeId
            )
        ));

    // Step 3: Check if employee is active
    if (!"Active".equalsIgnoreCase(employee.getStatus())) {
        throw new BusinessException(
            new ErrorDetails(
                AppConstant.ERROR_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_CODE_RESOURCE,
                AppConstant.ERROR_TYPE_VALIDATION,
                "Employee account is inactive. Please contact administrator."
            )
        );
    }

    // Step 4: Return employee details
    return mapToResponseDTO(employee);
}

// TC_15 FIX: Advanced employee search with multiple filters
@Override
public List<EmployeeDepartmentMasterResponseDto> advancedSearch(String searchTerm, String department, String location) {
    List<EmployeeDepartmentMaster> employees;

    // If all filters are null/empty, return all active employees
    if ((searchTerm == null || searchTerm.trim().isEmpty()) &&
        (department == null || department.trim().isEmpty()) &&
        (location == null || location.trim().isEmpty())) {
        employees = employeeRepository.findByIsDraftFalse();
    }
    // If department is specified
    else if (department != null && !department.trim().isEmpty()) {
        employees = employeeRepository.findByDepartmentNameContainingIgnoreCaseAndIsDraftFalse(department);
    }
    // If location is specified
    else if (location != null && !location.trim().isEmpty()) {
        employees = employeeRepository.findByLocationContainingIgnoreCaseAndIsDraftFalse(location);
    }
    // Otherwise use general search term
    else {
        employees = employeeRepository.searchEmployees(searchTerm);
    }

    return employees.stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
}

// Get all reporting officers (active employees) for LOV dropdown
@Override
public List<ReportingOfficerDto> getAllReportingOfficers() {
    List<EmployeeDepartmentMaster> activeEmployees = employeeRepository.findByStatus("Active");
    return activeEmployees.stream()
        .filter(emp -> !Boolean.TRUE.equals(emp.getIsDraft()))
        .map(emp -> new ReportingOfficerDto(
            emp.getEmployeeId(),
            emp.getEmployeeName(),
            emp.getDesignation(),
            emp.getDepartmentName()
        ))
        .collect(Collectors.toList());
}

// Get all Indian states for LOV dropdown
@Override
public List<Map<String, String>> getAllStates() {
    List<Map<String, String>> states = new ArrayList<>();
    String[] indianStates = {
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
        "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
        "Uttar Pradesh", "Uttarakhand", "West Bengal",
        "Andaman and Nicobar Islands", "Chandigarh", "Dadra and Nagar Haveli and Daman and Diu",
        "Delhi", "Jammu and Kashmir", "Ladakh", "Lakshadweep", "Puducherry"
    };

    for (String state : indianStates) {
        Map<String, String> stateMap = new HashMap<>();
        stateMap.put("value", state);
        stateMap.put("displayValue", state);
        states.add(stateMap);
    }
    return states;
}

// Get cities by state for LOV dropdown
@Override
public List<Map<String, String>> getCitiesByState(String state) {
    List<Map<String, String>> cities = new ArrayList<>();

    // Major cities for each state (can be extended or moved to database)
    Map<String, String[]> stateCitiesMap = new HashMap<>();
    stateCitiesMap.put("Karnataka", new String[]{"Bangalore", "Mysore", "Hubli", "Mangalore", "Belgaum", "Gulbarga", "Davangere", "Bellary", "Bijapur", "Shimoga"});
    stateCitiesMap.put("Maharashtra", new String[]{"Mumbai", "Pune", "Nagpur", "Thane", "Nashik", "Aurangabad", "Solapur", "Kolhapur", "Navi Mumbai", "Amravati"});
    stateCitiesMap.put("Tamil Nadu", new String[]{"Chennai", "Coimbatore", "Madurai", "Tiruchirappalli", "Salem", "Tirunelveli", "Erode", "Vellore", "Thoothukudi", "Dindigul"});
    stateCitiesMap.put("Telangana", new String[]{"Hyderabad", "Warangal", "Nizamabad", "Karimnagar", "Khammam", "Ramagundam", "Mahbubnagar", "Nalgonda", "Adilabad", "Suryapet"});
    stateCitiesMap.put("Andhra Pradesh", new String[]{"Visakhapatnam", "Vijayawada", "Guntur", "Nellore", "Kurnool", "Tirupati", "Rajahmundry", "Kakinada", "Kadapa", "Anantapur"});
    stateCitiesMap.put("Kerala", new String[]{"Thiruvananthapuram", "Kochi", "Kozhikode", "Thrissur", "Kollam", "Kannur", "Alappuzha", "Palakkad", "Malappuram", "Kottayam"});
    stateCitiesMap.put("Gujarat", new String[]{"Ahmedabad", "Surat", "Vadodara", "Rajkot", "Bhavnagar", "Jamnagar", "Junagadh", "Gandhinagar", "Anand", "Nadiad"});
    stateCitiesMap.put("Rajasthan", new String[]{"Jaipur", "Jodhpur", "Udaipur", "Kota", "Ajmer", "Bikaner", "Alwar", "Bharatpur", "Sikar", "Bhilwara"});
    stateCitiesMap.put("Uttar Pradesh", new String[]{"Lucknow", "Kanpur", "Varanasi", "Agra", "Prayagraj", "Ghaziabad", "Noida", "Meerut", "Bareilly", "Aligarh"});
    stateCitiesMap.put("West Bengal", new String[]{"Kolkata", "Howrah", "Durgapur", "Asansol", "Siliguri", "Bardhaman", "Malda", "Baharampur", "Habra", "Kharagpur"});
    stateCitiesMap.put("Delhi", new String[]{"New Delhi", "Central Delhi", "North Delhi", "South Delhi", "East Delhi", "West Delhi", "North East Delhi", "North West Delhi", "South East Delhi", "South West Delhi"});
    stateCitiesMap.put("Punjab", new String[]{"Ludhiana", "Amritsar", "Jalandhar", "Patiala", "Bathinda", "Mohali", "Pathankot", "Hoshiarpur", "Batala", "Moga"});
    stateCitiesMap.put("Haryana", new String[]{"Faridabad", "Gurgaon", "Panipat", "Ambala", "Yamunanagar", "Rohtak", "Hisar", "Karnal", "Sonipat", "Panchkula"});
    stateCitiesMap.put("Madhya Pradesh", new String[]{"Indore", "Bhopal", "Jabalpur", "Gwalior", "Ujjain", "Sagar", "Dewas", "Satna", "Ratlam", "Rewa"});
    stateCitiesMap.put("Bihar", new String[]{"Patna", "Gaya", "Bhagalpur", "Muzaffarpur", "Purnia", "Darbhanga", "Bihar Sharif", "Arrah", "Begusarai", "Katihar"});
    stateCitiesMap.put("Odisha", new String[]{"Bhubaneswar", "Cuttack", "Rourkela", "Berhampur", "Sambalpur", "Puri", "Balasore", "Bhadrak", "Baripada", "Jharsuguda"});
    stateCitiesMap.put("Jharkhand", new String[]{"Ranchi", "Jamshedpur", "Dhanbad", "Bokaro", "Deoghar", "Hazaribagh", "Giridih", "Ramgarh", "Medininagar", "Chirkunda"});
    stateCitiesMap.put("Chhattisgarh", new String[]{"Raipur", "Bhilai", "Bilaspur", "Korba", "Durg", "Rajnandgaon", "Raigarh", "Jagdalpur", "Ambikapur", "Dhamtari"});
    stateCitiesMap.put("Assam", new String[]{"Guwahati", "Silchar", "Dibrugarh", "Jorhat", "Nagaon", "Tinsukia", "Tezpur", "Bongaigaon", "Dhubri", "North Lakhimpur"});
    stateCitiesMap.put("Uttarakhand", new String[]{"Dehradun", "Haridwar", "Roorkee", "Haldwani", "Rudrapur", "Kashipur", "Rishikesh", "Pithoragarh", "Ramnagar", "Nainital"});
    stateCitiesMap.put("Himachal Pradesh", new String[]{"Shimla", "Mandi", "Dharamshala", "Solan", "Nahan", "Bilaspur", "Chamba", "Hamirpur", "Kullu", "Una"});
    stateCitiesMap.put("Goa", new String[]{"Panaji", "Margao", "Vasco da Gama", "Mapusa", "Ponda", "Bicholim", "Curchorem", "Sanquelim", "Cuncolim", "Canacona"});
    stateCitiesMap.put("Jammu and Kashmir", new String[]{"Srinagar", "Jammu", "Anantnag", "Baramulla", "Sopore", "Kathua", "Udhampur", "Kupwara", "Pulwama", "Rajouri"});
    stateCitiesMap.put("Chandigarh", new String[]{"Chandigarh"});
    stateCitiesMap.put("Puducherry", new String[]{"Puducherry", "Karaikal", "Mahe", "Yanam"});

    String[] cityArray = stateCitiesMap.getOrDefault(state, new String[]{});

    for (String city : cityArray) {
        Map<String, String> cityMap = new HashMap<>();
        cityMap.put("value", city);
        cityMap.put("displayValue", city);
        cities.add(cityMap);
    }

    return cities;
}

}
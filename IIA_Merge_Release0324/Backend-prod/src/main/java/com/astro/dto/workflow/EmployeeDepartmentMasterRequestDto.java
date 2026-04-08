package com.astro.dto.workflow;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
public class EmployeeDepartmentMasterRequestDto {
    // Added by Aman
    @NotBlank(message = "Employee id is required")
    // End
    private String employeeId;

    @NotBlank(message = "Employee name is required")
    private String employeeName;

    // Split name fields
    private String firstName;
    private String lastName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Department name is required")
    private String departmentName;

    @NotBlank(message = "Designation is required")
    private String designation;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNumber;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    private String emailAddress;

    @NotBlank(message = "Address is required")
    private String address;

    // Split address fields
    private String streetAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    private String pinCode;

    // Reporting Officer (mandatory) - replaces manager
    @NotBlank(message = "Reporting Officer is required")
    private String reportingOfficerId;

    private String reportingOfficerName;

    private LocalDate dateOfBirth;

    private LocalDate hireDate;

    private LocalDate endDate;

    private String employmentType; // Full-time, Part-time, Contract

    private String status = "Active";
    
    private String createdBy;
    
    private String updatedBy;

    private Boolean isDraft = false;
    
    // NEW FIELDS for user creation
    private Boolean createUserAccount = false;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String userPassword;
    
    private String userName;
    
    private List<String> userRoles;
}
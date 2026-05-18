package com.astro.dto.workflow;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class userRequestDto {

    @NotBlank(message = "Username is required")
    private String userName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // Support for single role (from frontend)
    private String roleName;

    // Support for multiple roles (backward compatibility)
    private List<String> roleNames;
    
    @Email(message = "Please provide a valid email")
    @NotBlank(message = "Email is required")
    private String email;
    
     @Pattern(regexp = "^[0-9]{7,15}$", message = "Mobile/phone number must be 7 to 15 digits")
    private String mobileNumber; 
    // @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
    // @NotBlank(message = "Mobile number is required")
    // private String mobileNumber;

    // Employee ID is optional - user may or may not be linked to an employee
    private String employeeId;

    private String createdBy;
}
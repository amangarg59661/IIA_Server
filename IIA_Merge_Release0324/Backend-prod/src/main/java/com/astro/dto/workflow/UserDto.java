package com.astro.dto.workflow;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class UserDto {

    private Integer userId;
    private String password;
    private String userName;
    private String email;
    private String mobileNumber;
    private String employeeId;
    private String roleName;    private String departmentName;
    private String location;
    private String createdBy;
    private LocalDateTime createdDate;

    // New field: List of roles assigned to the user
    private List<String> roleNames;

    // User active/inactive status
    private Boolean isActive;
}

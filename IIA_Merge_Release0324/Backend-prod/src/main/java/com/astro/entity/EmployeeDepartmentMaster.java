package com.astro.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EntityListeners;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Table(name = "employee_department_master")
@EntityListeners(AuditingEntityListener.class)
public class EmployeeDepartmentMaster {

    @Id
    @Column(name = "employee_id")
    private String employeeId;
    
    @Column(name = "employee_name", nullable = false)
    private String employeeName;

    // Split name fields (NEW)
    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "department_name", nullable = false)
    private String departmentName;
    
    @Column(name = "location", nullable = false)
    private String location;
    
    @Column(name = "designation", nullable = false)
    private String designation;
    
    // Separate contact detail fields
    @Column(name = "phone_number", nullable = false, length = 20)
    // @Column(name = "phone_number", nullable = false, length = 10)
    private String phoneNumber;
    
    @Column(name = "email_address", nullable = false)
    private String emailAddress;
    
    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    // Split address fields (NEW)
    @Column(name = "street_address", length = 255)
    private String streetAddress;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pin_code", length = 20)
    private String pinCode;

    // Additional employment fields (NEW)
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Reporting Officer fields (renamed from manager)
    @Column(name = "reporting_officer_id", length = 50)
    private String reportingOfficerId;

    @Column(name = "reporting_officer_name", length = 150)
    private String reportingOfficerName;

    @Column(name = "employment_type", length = 50)
    private String employmentType; // Full-time, Part-time, Contract

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "end_date")
    private LocalDate endDate; // Resignation/Termination date (optional)

    // Status field - Active or Inactive
    @Column(name = "status", nullable = false)
    private String status = "Active";
    
    @Column(name = "created_by", nullable = false)
    @CreatedBy
    private String createdBy;
    
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    @LastModifiedDate
    private LocalDateTime updatedDate;

    @Column(name = "is_draft")
    private Boolean isDraft = false;
}
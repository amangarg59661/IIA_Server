package com.astro.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.EntityListeners;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "project_master")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ProjectMaster {

    @Id
    @Column(name = "project_code")
    private String projectCode;

    @Column(name = "project_name_description")
    private String projectNameDescription;

    @Column(name = "financial_year")
    private String financialYear;

    @Column(name = "allocated_amount")
    private BigDecimal allocatedAmount;

    @Column(name = "available_project_limit")
    private BigDecimal availableProjectLimit;

    @Column(name = "department_division")
    private String departmentDivision;

    @Column(name = "budget_type")
    private String budgetType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // New fields for Admin Panel
    @Column(name = "status", length = 50)
    private String status = "Active"; // Active, Completed, Closed

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "budget_code", length = 2000)
    private String budgetCode;  // comma-separated list of budget codes

    @Column(name = "remarks_notes")
    private String remarksNotes;

    @Column(name = "project_head", length = 50)
    private String projectHead; // Stores employee ID of the project head

    @Column(name = "project_head_name", length = 150)
    private String projectHeadName; // Stores name of the project head for display

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;
    @Column(name = "updated_by")
    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updatedDate;


}
